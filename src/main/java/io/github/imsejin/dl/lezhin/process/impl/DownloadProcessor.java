/*
 * Copyright 2022 Sejin Im
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
import io.github.imsejin.dl.lezhin.common.PropertyBinder;
import io.github.imsejin.dl.lezhin.exception.DirectoryCreationException;
import io.github.imsejin.dl.lezhin.http.url.URIs;
import io.github.imsejin.dl.lezhin.process.ProcessContext;
import io.github.imsejin.dl.lezhin.process.Processor;
import io.github.imsejin.dl.lezhin.util.PathUtils;
import me.tongfei.progressbar.ConsoleProgressBarConsumer;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

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

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toUnmodifiableList;

/**
 * Processor for downloading images
 *
 * @since 3.0.0
 */
@ProcessSpecification(dependsOn = DirectoryCreationProcessor.class)
public class DownloadProcessor implements Processor {

    private static final Map<Locale, DownloadProcessor> IMPLEMENTATION_MAP = Map.ofEntries(
            Map.entry(Locale.KOREA, new KoreanImpl()),
            Map.entry(Locale.US, new EnglishImpl()),
            Map.entry(Locale.JAPAN, new EnglishImpl())
    );

    @Override
    public Void process(ProcessContext context) throws DirectoryCreationException {
        Locale locale = context.getLanguage().getValue();

        DownloadProcessor impl = IMPLEMENTATION_MAP.get(locale);
        impl.prepare(context);

        AuthorityService service = new AuthorityService(locale, context.getAccessToken().getValue());

        List<Episode> episodes = context.getContent().getEpisodes().stream()
                .sorted(comparingInt(Episode::getSeq)).collect(toUnmodifiableList());

        int[] range = calculateRange(context.getEpisodeRange(), episodes.size());
        for (int i : range) {
            Episode episode = episodes.get(i);

            // Cannot download paid episode.
            if (!episode.isFree()) {
                continue;
            }

            int imageCount = impl.getImageCountOfEpisode(context, episode);

            // If episode has no image, skips this episode.
            if (imageCount < 1) {
                continue;
            }

            // Creates a directory with the name of episode.
            int sequence = i + 1;
            String directoryName = String.format("%04d - %s", sequence, episode.getDisplay().getTitle());
            Path episodeDirectoryPath = context.getDirectoryPath().getValue().resolve(directoryName);
            PathUtils.createDirectoryIfNotExists(episodeDirectoryPath);

            String taskName = String.format("%s ep.%d", context.getContent().getAlias(), sequence);
            try (ProgressBar progressBar = createProgressBar(taskName, imageCount)) {
                ServiceRequest serviceRequest = PropertyBinder.INSTANCE.toServiceRequest(context.getContent(), episode);
                Authority authority = service.getAuthForViewEpisode(serviceRequest);

                // Downloads all images of the episode.
                IntStream.rangeClosed(1, imageCount).parallel().forEach(n -> {
                    String fileName = String.format("%03d.%s", n, context.getImageFormat().getValue());
                    Path dest = episodeDirectoryPath.resolve(fileName);

                    // Tries to download high-resolution image for only paid users.
                    URL url = getImageUrl(context, episode, authority, n, true);
                    boolean success = downloadImage(url, dest);

                    // Try to download low-resolution image for all users.
                    if (!success) {
                        url = getImageUrl(context, episode, authority, n, false);
                        success = downloadImage(url, dest);

                        // If failed to download, skips this image.
                        if (!success) {
                            return;
                        }
                    }

                    progressBar.step();
                });
            }
        }

        return null;
    }

    // -------------------------------------------------------------------------------------------------

    void prepare(ProcessContext context) {
    }

    int getImageCountOfEpisode(ProcessContext context, Episode episode) {
        throw new UnsupportedOperationException("Not implemented");
    }

    // -------------------------------------------------------------------------------------------------

    private static final class KoreanImpl extends DownloadProcessor {
        private Map<String, Integer> imageCountMap;

        @Override
        void prepare(ProcessContext context) {
            UUID token = context.getAccessToken().getValue();
            String contentAlias = context.getContent().getAlias();
            EpisodeImageCountService service = new EpisodeImageCountService(token);

            this.imageCountMap = service.getImageCountMap(contentAlias);
        }

        @Override
        int getImageCountOfEpisode(ProcessContext context, Episode episode) {
            return this.imageCountMap.get(episode.getName());
        }
    }

    private static final class EnglishImpl extends DownloadProcessor {
        @Override
        int getImageCountOfEpisode(ProcessContext context, Episode episode) {
            // 서비스 종료된 웹툰이면 '내 서재'로 접근한다.
            Language language = context.getLanguage();
//            URI episodeUrl = args.isExpiredComic()
//                    ? URIs.LIBRARY_EPISODE.get(language.getValue(), language.getLocale(), args.getComicName(), episode.getName())
//                    : URIs.EPISODE.get(language.getValue(), args.getComicName(), episode.getName());

//            Loggers.getLogger().debug("Request episode page: {}", episodeUrl);
//            WebBrowser.request(episodeUrl);

            try {
                // Waits for DOM to complete the rendering.
                Loggers.getLogger().debug("Wait up to {} sec for images to be rendered", WebBrowser.DEFAULT_TIMEOUT_SECONDS);
                WebElement scrollList = WebBrowser.waitForVisibilityOfElement(By.id("scroll-list"));

                List<WebElement> images = scrollList.findElements(
                        By.xpath(".//div[@class='cut' and not(contains(@class, 'cutLicense')) and @data-cut-index and @data-cut-type='cut']"));

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
                return new int[]{episodeRange.getStartNumber() - 1};
            case TO_END:
                return IntStream.range(episodeRange.getStartNumber() - 1, episodeCount).toArray();
            case FROM_BEGINNING:
                return IntStream.range(1, episodeRange.getEndNumber()).toArray();
            case SOME:
                return IntStream.range(episodeRange.getStartNumber() - 1, episodeRange.getEndNumber()).toArray();
            default:
                throw new AssertionError("Never happened");
        }
    }

    private URL getImageUrl(ProcessContext context, Episode episode, Authority authority, int num, boolean purchased) {
        String uriString = URIs.EPISODE_IMAGE.get(context.getContent().getId(), episode.getId(), num, purchased,
                authority.getPolicy(), authority.getSignature(), authority.getKeyPairId());
        try {
            if (authority.isExpired()) {
                throw new IllegalStateException("Authority for viewing episode is expired: " + authority);
            }

            return URI.create(uriString).toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Creates a image file with the image URL. Returns {@code true} if success or {@code false}.
     */
    private static boolean downloadImage(URL url, Path dest) {
        try {
            FileUtils.download(url, dest);
            return true;
        } catch (Exception e) {
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
//        String taskName = String.format("%s ep.%d", episodeName, episodeNo);

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
