/*
 * Copyright 2023 Sejin Im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.imsejin.dl.lezhin.process.impl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import me.tongfei.progressbar.ConsoleProgressBarConsumer;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

import io.github.imsejin.common.assertion.Asserts;
import io.github.imsejin.common.util.FileUtils;
import io.github.imsejin.dl.lezhin.annotation.ProcessSpecification;
import io.github.imsejin.dl.lezhin.api.auth.model.Authority;
import io.github.imsejin.dl.lezhin.api.auth.model.ServiceRequest;
import io.github.imsejin.dl.lezhin.api.auth.service.AuthorityService;
import io.github.imsejin.dl.lezhin.api.image.service.EpisodeImageCountService;
import io.github.imsejin.dl.lezhin.argument.impl.EpisodeRange;
import io.github.imsejin.dl.lezhin.argument.impl.Language;
import io.github.imsejin.dl.lezhin.attribute.impl.Content.Episode;
import io.github.imsejin.dl.lezhin.browser.WebBrowser;
import io.github.imsejin.dl.lezhin.common.Loggers;
import io.github.imsejin.dl.lezhin.common.ModelPropertyMapper;
import io.github.imsejin.dl.lezhin.exception.DirectoryCreationException;
import io.github.imsejin.dl.lezhin.exception.ImageCountNotFoundException;
import io.github.imsejin.dl.lezhin.http.url.URIs;
import io.github.imsejin.dl.lezhin.process.ProcessContext;
import io.github.imsejin.dl.lezhin.process.Processor;
import io.github.imsejin.dl.lezhin.util.FileNameUtils;
import io.github.imsejin.dl.lezhin.util.PathUtils;

import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;

/**
 * Processor for downloading images
 *
 * @since 3.0.0
 */
@ProcessSpecification(dependsOn = DirectoryCreationProcessor.class)
public class DownloadProcessor implements Processor {

    /**
     * EpisodeImageCountService is only available on korean platform.
     */
    private static final Map<Locale, ImageCountResolver> IMPLEMENTATION_MAP = Map.of(
            Locale.KOREA, new UsingService(),
            Locale.US, new VisitingPage(),
            Locale.JAPAN, new VisitingPage()
    );

    @Override
    public Void process(ProcessContext context) throws DirectoryCreationException {
        Locale locale = context.getLanguage().getValue();

        ImageCountResolver imageCountResolver = IMPLEMENTATION_MAP.get(locale);
        imageCountResolver.prepare(context);

        AuthorityService service = new AuthorityService(locale, context.getAccessToken().getValue());

        List<Episode> episodes = context.getContent().getEpisodes().stream()
                .sorted(comparingInt(Episode::getSeq)).collect(toUnmodifiableList());

        int[] range = calculateRange(context.getEpisodeRange(), episodes.size());
        for (int i : range) {
            Episode episode = episodes.get(i);

            // You can access episode you bought and free episode only.
            // If you didn't buy it when accessing an expired content, you can't access even free episode.
            boolean purchased = context.getPurchasedEpisodes().contains(episode.getId());
            if (!(purchased || (episode.isFree() && !context.getContent().getProperties().isExpired()))) {
                continue;
            }

            int imageCount = getImageCount(context, episode, imageCountResolver);

            // If episode has no image, skips this episode.
            if (imageCount < 1) {
                continue;
            }

            int sequence = i + 1;
            String directoryName = String.format("%04d - %s", sequence, episode.getDisplay().getTitle());
            directoryName = FileNameUtils.sanitize(directoryName);
            directoryName = FileNameUtils.replaceForbiddenCharacters(directoryName);

            // Creates a directory with the name of episode.
            Path episodeDirectoryPath = context.getDirectoryPath().getValue().resolve(directoryName);
            PathUtils.createDirectoryIfNotExists(episodeDirectoryPath);

            String taskName = String.format("%s ep.%d", context.getContent().getAlias(), sequence);
            try (ProgressBar progressBar = createProgressBar(taskName, imageCount)) {
                ServiceRequest serviceRequest = ModelPropertyMapper.INSTANCE.toServiceRequest(context.getContent(),
                        episode,
                        purchased);
                Authority authority = service.getAuthForViewEpisode(serviceRequest);

                // Downloads all images of the episode in sequential.
                if (context.getSingleThreading().getValue()) {
                    for (int n = 1; n <= imageCount; n++) {
                        String fileName = String.format("%03d.%s", n, context.getImageFormat().getValue());
                        Path dest = episodeDirectoryPath.resolve(fileName);

                        // Tries to download an image of the specific resolution.
                        // The resolution depends on whether you paid for this episode or not.
                        URL url = getImageUrl(context, episode, authority, n, purchased);
                        boolean success = downloadImage(url, dest);

                        // If failed to download, skips this image.
                        if (!success) {
                            continue;
                        }

                        progressBar.step();
                    }

                    continue;
                }

                // Downloads all images of the episode in parallel.
                IntStream.rangeClosed(1, imageCount).parallel().forEach(n -> {
                    String fileName = String.format("%03d.%s", n, context.getImageFormat().getValue());
                    Path dest = episodeDirectoryPath.resolve(fileName);

                    // Tries to download an image of the specific resolution.
                    // The resolution depends on whether you paid for this episode or not.
                    URL url = getImageUrl(context, episode, authority, n, purchased);
                    boolean success = downloadImage(url, dest);

                    // If failed to download, skips this image.
                    if (!success) {
                        return;
                    }

                    progressBar.step();
                });
            }
        }

        return null;
    }

    // -------------------------------------------------------------------------------------------------

    /**
     * @since 3.0.3
     */
    private interface ImageCountResolver {
        default void prepare(ProcessContext context) {
        }

        int getImageCountOfEpisode(ProcessContext context, Episode episode);
    }

    private static final class UsingService implements ImageCountResolver {
        private Map<String, Integer> imageCountMap;

        @Override
        public void prepare(ProcessContext context) {
            UUID token = context.getAccessToken().getValue();
            String contentAlias = context.getContent().getAlias();
            EpisodeImageCountService service = new EpisodeImageCountService(token);

            this.imageCountMap = service.getImageCountMap(contentAlias);
        }

        @Override
        public int getImageCountOfEpisode(ProcessContext context, Episode episode) {
            Integer imageCount = this.imageCountMap.get(episode.getName());

            // There is case that imageCountMap doesn't have all names of episode as key for a certain content.
            // I guess that lezhin API doesn't provide metadata of new episode. - ImSejin
            if (imageCount == null) {
                throw new ImageCountNotFoundException("Failed to get image count of episode[%s]: imageCountMap=%s",
                        episode, this.imageCountMap);
            }

            return imageCount;
        }
    }

    private static final class VisitingPage implements ImageCountResolver {
        @Override
        public int getImageCountOfEpisode(ProcessContext context, Episode episode) {
            Language language = context.getLanguage();

            String episodeUrl;
            if (context.getContent().getProperties().isExpired()) {
                // Visits to "My Library", if the content is expired.
                episodeUrl = URIs.LIBRARY_EPISODE.get(language.getValue().getLanguage(),
                        language.asLocaleString(), context.getContent().getAlias(), episode.getName());
            } else {
                episodeUrl = URIs.EPISODE.get(language.getValue().getLanguage(),
                        context.getContent().getAlias(), episode.getName());
            }

            Loggers.getLogger().debug("Request episode page: {}", episodeUrl);
            WebBrowser.request(episodeUrl);

            try {
                // Waits for DOM to complete the rendering.
                Loggers.getLogger()
                        .debug("Wait up to {} sec for images to be rendered", WebBrowser.DEFAULT_TIMEOUT_SECONDS);
                WebElement episodeList = WebBrowser.waitForVisibilityOfElement(By.id("scroll-list"));

                List<WebElement> images = episodeList.findElements(
                        By.xpath(
                                ".//div[@class='cut' and not(contains(@class, 'cutLicense')) and @data-cut-index and @data-cut-type='cut']"));

                // Successful
                return images.size();
            } catch (NoSuchElementException ex) {
                // Failed
                return 0;
            }
        }
    }

    // -------------------------------------------------------------------------------------------------

    private static int[] calculateRange(EpisodeRange episodeRange, int episodeCount) {
        switch (episodeRange.getRangeType()) {
            case ALL:
                return IntStream.range(0, episodeCount).toArray();
            case ONE:
                return new int[] {episodeRange.getStartNumber() - 1};
            case TO_END:
                return IntStream.range(episodeRange.getStartNumber() - 1, episodeCount).toArray();
            case FROM_BEGINNING:
                return IntStream.range(0, episodeRange.getEndNumber()).toArray();
            case SOME:
                return IntStream.range(episodeRange.getStartNumber() - 1, episodeRange.getEndNumber()).toArray();
            default:
                throw new AssertionError("Never happened");
        }
    }

    private static int getImageCount(ProcessContext context, Episode episode, ImageCountResolver resolver) {
        int imageCount;

        try {
            imageCount = resolver.getImageCountOfEpisode(context, episode);
        } catch (ImageCountNotFoundException e) {
            Asserts.that(resolver)
                    .isNotNull()
                    .isInstanceOf(UsingService.class);

            Loggers.getLogger().debug(e.getMessage());
            resolver = IMPLEMENTATION_MAP.get(Locale.US);

            Asserts.that(resolver)
                    .isNotNull()
                    .isInstanceOf(VisitingPage.class);

            // Fix: https://github.com/ImSejin/lezhin-comics-downloader/issues/153
            imageCount = resolver.getImageCountOfEpisode(context, episode);
        }

        return imageCount;
    }

    private URL getImageUrl(ProcessContext context, Episode episode, Authority authority, int num, boolean purchased) {
        String uriString = URIs.EPISODE_IMAGE.get(context.getContent().getId(), episode.getId(), num,
                context.getImageFormat().getValue(), purchased, authority.getPolicy(), authority.getSignature(),
                authority.getKeyPairId());
        try {
            if (authority.isExpired()) {
                throw new IllegalStateException("Authority for viewing episode is expired: " + authority);
            }

            URI uri = URI.create(context.getHttpHosts().getContentsCdn());
            return uri.resolve(uriString).normalize().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Creates an image file with the image URL. Returns {@code true} if success or {@code false}.
     */
    private static boolean downloadImage(URL url, Path dest) {
        try {
            FileUtils.download(url, dest);
            return true;
        } catch (Exception e) {
            Loggers.getLogger().debug("Failed to download an image to {} by {}", dest, url);
            return false;
        }
    }

    /**
     * Creates a progress bar.
     *
     * <pre>
     *     i_have_a_baby ep.1 100% [=======] 80/80 imgs (0:00:03 / 0:00:00) | 26.7 imgs/s
     *     i_have_a_baby ep.2 100% [=======] 70/70 imgs (0:00:02 / 0:00:00) | 35.0 imgs/s
     *     i_have_a_baby ep.3 100% [=======] 56/56 imgs (0:00:01 / 0:00:00) | 56.0 imgs/s
     * </pre>
     *
     * @param taskName   name of task
     * @param imageCount number of images of the episode
     * @return progress bar
     */
    private static ProgressBar createProgressBar(String taskName, int imageCount) {
        ProgressBarBuilder builder = new ProgressBarBuilder();
        builder.setTaskName(taskName);
        builder.setInitialMax(imageCount);
        builder.setUpdateIntervalMillis(250);
        builder.setConsumer(new ConsoleProgressBarConsumer(System.out));
        builder.setStyle(ProgressBarStyle.ASCII);
        builder.setUnit(" imgs", 1);
        builder.showSpeed(new DecimalFormat("| #.0"));
        builder.setSpeedUnit(ChronoUnit.SECONDS);
        builder.startsFrom(0L, Duration.ZERO);

        return builder.build();
    }

}
