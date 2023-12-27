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

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.openqa.selenium.chrome.ChromeDriverService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import io.github.imsejin.common.io.Resource;
import io.github.imsejin.common.io.finder.ResourceFinder;
import io.github.imsejin.common.io.finder.ZipResourceFinder;
import io.github.imsejin.common.util.CollectionUtils;
import io.github.imsejin.common.util.FileUtils;
import io.github.imsejin.common.util.FilenameUtils;
import io.github.imsejin.dl.lezhin.annotation.ProcessSpecification;
import io.github.imsejin.dl.lezhin.api.chromedriver.model.ChromeDriverDownload;
import io.github.imsejin.dl.lezhin.api.chromedriver.model.Platform;
import io.github.imsejin.dl.lezhin.api.chromedriver.service.ChromeDriverDownloadService;
import io.github.imsejin.dl.lezhin.attribute.impl.ChromeInfo;
import io.github.imsejin.dl.lezhin.browser.ChromeVersion;
import io.github.imsejin.dl.lezhin.common.Loggers;
import io.github.imsejin.dl.lezhin.exception.LezhinComicsDownloaderException;
import io.github.imsejin.dl.lezhin.process.ProcessContext;
import io.github.imsejin.dl.lezhin.process.Processor;

/**
 * Processor for downloading chromedriver
 *
 * @since 4.0.0
 */
@ProcessSpecification(dependsOn = ChromeInfoResolutionProcessor.class)
public class ChromeDriverDownloadProcessor implements Processor {

    private final ChromeDriverDownloadService service;

    private final List<ChromeDriverDownloader> downloaders;

    public ChromeDriverDownloadProcessor(ChromeDriverDownloadService service) {
        this.service = service;
        this.downloaders = List.of(
                new NoChromeInfoDownloader(),
                new KnownDriverVersionDownloader(),
                new KnownBrowserVersionDownloader()
        );
    }

    @Override
    public Void process(ProcessContext context) throws LezhinComicsDownloaderException {
        ChromeInfo chromeInfo = context.getChromeInfo();
        ChromeVersion browserVersion = chromeInfo.getBrowserVersion();
        ChromeVersion driverVersion = chromeInfo.getDriverVersion();
        ChromeInfo.Status status = chromeInfo.getStatus();

        // Does nothing, if the resolved version is compatible with browser.
        if (status == ChromeInfo.Status.ENTIRE
                && Objects.requireNonNull(browserVersion).isCompatibleWith(driverVersion)) {
            Loggers.getLogger()
                    .debug("Both version of chrome browser and chromedriver are compatible; ignore this step");
            return null;
        }

        ChromeDriverDownloader downloader = this.downloaders.stream()
                .filter(it -> it.support(status))
                .findFirst().orElseThrow();

        downloader.download(context);

        return null;
    }

    // -------------------------------------------------------------------------------------------------

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static abstract class ChromeDriverDownloader {
        public abstract boolean support(ChromeInfo.Status status);

        public abstract void download(ProcessContext context);

        protected final boolean downloadDriver(ChromeDriverDownload.Version version, Path dest, Path driverPath) {
            Optional<Platform> maybeCurrent = Platform.getCurrentPlatform();

            // Not supported platform.
            if (maybeCurrent.isEmpty()) {
                return false;
            }

            Platform current = maybeCurrent.get();
            List<ChromeDriverDownload.Program> drivers = version.getDownloads().getChromedrivers();

            // There is no download link of chromedriver on the version.
            if (CollectionUtils.isNullOrEmpty(drivers)) {
                return false;
            }

            ChromeDriverDownload.Program chromedriver = null;
            for (ChromeDriverDownload.Program driver : drivers) {
                if (driver.getPlatform() == current) {
                    chromedriver = driver;
                    break;
                }
            }

            // There is no download link compatible with the current platform.
            if (chromedriver == null) {
                return false;
            }

            URL url = chromedriver.getUrl();
            String fileName = FilenameUtils.getName(url.toString());
            Path filePath = dest.resolve(fileName);

            Loggers.getLogger().debug("Download a archive file of chromedriver: {}", url);
            FileUtils.download(url, filePath);

            // chromedriver-win64.zip
            // └ chromedriver-win64/
            //   └ chromedriver.exe
            //   └ LICENSE.chromedriver
            ResourceFinder resourceFinder = new ZipResourceFinder(false,
                    entry -> !entry.isDirectory() && FilenameUtils.getBaseName(FilenameUtils.getName(entry.getName()))
                            .equals(ChromeDriverService.CHROME_DRIVER_NAME));
            List<Resource> resources = resourceFinder.getResources(filePath);

            // There is no chromedriver in the archive file.
            if (CollectionUtils.isNullOrEmpty(resources)) {
                Loggers.getLogger()
                        .debug("There is no chromedriver in the archive file; ignore this step: {}", fileName);
                return false;
            }

            if (resources.size() > 1) {
                Loggers.getLogger()
                        .debug("There are two or more chromedrivers in the archive file; ignore this step: {}",
                                resources);
                return false;
            }

            try {
                // Removes the existing chromedriver.
                if (Files.isRegularFile(driverPath)) {
                    Files.delete(driverPath);
                }

                // Moves chromedriver from the archive file.
                Resource resource = resources.get(0);
                FileUtils.download(resource.getInputStream(), driverPath);
                Files.setPosixFilePermissions(driverPath, EnumSet.of(
                        PosixFilePermission.OWNER_READ,
                        PosixFilePermission.OWNER_WRITE,
                        PosixFilePermission.OWNER_EXECUTE,
                        PosixFilePermission.GROUP_READ,
                        PosixFilePermission.GROUP_EXECUTE,
                        PosixFilePermission.OTHERS_READ,
                        PosixFilePermission.OTHERS_EXECUTE
                ));
            } catch (IOException ignored) {
                return false;
            } finally {
                // Removes the archive file.
                FileUtils.deleteRecursively(filePath);
            }

            return true;
        }
    }

    // -------------------------------------------------------------------------------------------------

    private class NoChromeInfoDownloader extends ChromeDriverDownloader {
        @Override
        public boolean support(ChromeInfo.Status status) {
            return status == ChromeInfo.Status.NONE;
        }

        @Override
        public void download(ProcessContext context) {
            ChromeDriverDownload download = service.findChromeDriverDownload();
            Optional<ChromeDriverDownload.Version> maybeLatestVersion = download.findLatestVersion();

            if (maybeLatestVersion.isEmpty()) {
                Loggers.getLogger().debug("Failed to find the latest version of chromedriver; ignore this step");
                return;
            }

            Path dest = context.getDirectoryPath().getValue();
            Path driverPath = context.getChromeInfo().getDriverPath();
            ChromeDriverDownload.Version latestVersion = maybeLatestVersion.get();

            // Downloads the latest version.
            boolean downloaded = downloadDriver(latestVersion, dest, driverPath);
            if (!downloaded) {
                return;
            }

            Loggers.getLogger()
                    .info("Download the latest version({}) of chromedriver: {}", latestVersion.getValue(), driverPath);
        }
    }

    // -------------------------------------------------------------------------------------------------

    private class KnownDriverVersionDownloader extends ChromeDriverDownloader {
        @Override
        public boolean support(ChromeInfo.Status status) {
            return status == ChromeInfo.Status.DRIVER_ONLY;
        }

        @Override
        public void download(ProcessContext context) {
            ChromeDriverDownload download = service.findChromeDriverDownload();
            Optional<ChromeDriverDownload.Version> maybeLatestVersion = download.findLatestVersion();

            if (maybeLatestVersion.isEmpty()) {
                Loggers.getLogger().debug("Failed to find the latest version of chromedriver; ignore this step");
                return;
            }

            Path dest = context.getDirectoryPath().getValue();
            Path driverPath = context.getChromeInfo().getDriverPath();
            ChromeVersion driverVersion = context.getChromeInfo().getDriverVersion();
            ChromeDriverDownload.Version latestVersion = maybeLatestVersion.get();

            // If the resolved version is compatible with the latest one, ignore this step.
            if (latestVersion.getValue().isCompatibleWith(driverVersion)) {
                Loggers.getLogger().debug("Resolved chromedriver({}) is the latest; ignore this step", driverVersion);
                return;
            }

            // Downloads the latest version, if the resolved version is not compatible with that.
            boolean downloaded = downloadDriver(latestVersion, dest, driverPath);
            if (!downloaded) {
                return;
            }

            Loggers.getLogger()
                    .info("Download the latest version({}) of chromedriver: {}", latestVersion.getValue(), driverPath);
        }
    }

    // -------------------------------------------------------------------------------------------------

    private class KnownBrowserVersionDownloader extends ChromeDriverDownloader {
        @Override
        public boolean support(ChromeInfo.Status status) {
            return status == ChromeInfo.Status.BROWSER_ONLY || status == ChromeInfo.Status.ENTIRE;
        }

        @Override
        public void download(ProcessContext context) {
            ChromeVersion browserVersion = context.getChromeInfo().getBrowserVersion();

            ChromeDriverDownload download = service.findChromeDriverDownload();
            Optional<ChromeDriverDownload.Version> maybeVersion = download.getVersions()
                    .stream()
                    .filter(it -> it.getValue().isCompatibleWith(browserVersion))
                    .findFirst();

            if (maybeVersion.isEmpty()) {
                Loggers.getLogger().debug("Failed to find the version of chromedriver; ignore this step");
                return;
            }

            ChromeDriverDownload.Version version = maybeVersion.get();
            Path dest = context.getDirectoryPath().getValue();
            Path driverPath = context.getChromeInfo().getDriverPath();

            // Downloads the version fit for browser.
            boolean downloaded = downloadDriver(version, dest, driverPath);
            if (!downloaded) {
                return;
            }

            Loggers.getLogger()
                    .info("Download the version({}) of chromedriver fit for browser: {}",
                            version.getValue().getValue(),
                            driverPath);
        }
    }

}
