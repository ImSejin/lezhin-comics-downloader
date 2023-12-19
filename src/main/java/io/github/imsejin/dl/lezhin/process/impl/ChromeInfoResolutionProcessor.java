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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.openqa.selenium.chrome.ChromeDriverService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import io.github.imsejin.common.constant.OS;
import io.github.imsejin.dl.lezhin.annotation.ProcessSpecification;
import io.github.imsejin.dl.lezhin.attribute.impl.ChromeInfo;
import io.github.imsejin.dl.lezhin.attribute.impl.DirectoryPath;
import io.github.imsejin.dl.lezhin.browser.ChromeVersion;
import io.github.imsejin.dl.lezhin.common.Loggers;
import io.github.imsejin.dl.lezhin.exception.LezhinComicsDownloaderException;
import io.github.imsejin.dl.lezhin.process.ProcessContext;
import io.github.imsejin.dl.lezhin.process.Processor;
import io.github.imsejin.dl.lezhin.util.CommandUtils;

/**
 * Processor for resolution of chrome product information
 *
 * @since 4.0.0
 */
@ProcessSpecification(dependsOn = ConfigurationFileProcessor.class)
public class ChromeInfoResolutionProcessor implements Processor {

    private final List<ChromeInfoResolver> resolvers;

    public ChromeInfoResolutionProcessor() {
        this.resolvers = List.of(
                new WindowsChromeInfoResolver(),
                new LinuxChromeInfoResolver(),
                new MacosChromeInfoResolver()
        );
    }

    @Override
    public ChromeInfo process(ProcessContext context) throws LezhinComicsDownloaderException {
        ChromeInfoResolver resolver = this.resolvers.stream()
                .filter(it -> it.support(OS.getCurrentOS()))
                .findFirst().orElseThrow();

        Loggers.getLogger().debug("Resolve version of chrome browser");
        Optional<ChromeVersion> maybeBrowserVersion = resolver.resolveChromeBrowserVersion();

        // Regards chrome browser as installed.
        if (maybeBrowserVersion.isEmpty()) {
            Loggers.getLogger().debug("Failed to resolve version of chrome browser");
        }

        Loggers.getLogger().debug("Resolve path of chromedriver");
        Path driverPath = resolver.resolveChromeDriverPath(context.getDirectoryPath());

        if (!Files.isRegularFile(driverPath)) {
            Loggers.getLogger().debug("Failed to resolve path of chromedriver");
            return ChromeInfo.ofDriverPath(maybeBrowserVersion.orElse(null), driverPath);
        }

        Loggers.getLogger().debug("Resolve version of chromedriver");
        Optional<ChromeVersion> maybeDriverVersion = resolver.resolveChromeDriverVersion(driverPath);

        if (maybeDriverVersion.isEmpty()) {
            Loggers.getLogger().debug("Failed to resolve version of chromedriver");
            return ChromeInfo.ofDriverPath(maybeBrowserVersion.orElse(null), driverPath);
        }

        return ChromeInfo.ofDriver(maybeBrowserVersion.orElse(null), driverPath, maybeDriverVersion.orElse(null));
    }

    // -------------------------------------------------------------------------------------------------

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private abstract static class ChromeInfoResolver {
        private final List<List<String>> chromeBrowserVersionCommands;
        private final Function<Path, List<String>> chromeDriverVersionCommandFunction;

        public abstract boolean support(OS os);

        public Path resolveChromeDriverPath(DirectoryPath directoryPath) {
            return directoryPath.getValue().resolve(ChromeDriverService.CHROME_DRIVER_NAME);
        }

        public final Optional<ChromeVersion> resolveChromeBrowserVersion() {
            return resolveChromeVersion(this.chromeBrowserVersionCommands);
        }

        public final Optional<ChromeVersion> resolveChromeDriverVersion(Path driverPath) {
            List<List<String>> commands = List.of(this.chromeDriverVersionCommandFunction.apply(driverPath));
            return resolveChromeVersion(commands);
        }

        private static Optional<ChromeVersion> resolveChromeVersion(List<List<String>> commands) {
            for (List<String> command : commands) {
                try {
                    String versionString = CommandUtils.runCommand(command.toArray(String[]::new));
                    ChromeVersion version = ChromeVersion.from(versionString);
                    return Optional.of(version);
                } catch (Exception e) {
                    Loggers.getLogger()
                            .debug("Failed to run command; {}({}): {}",
                                    e.getClass().getName(),
                                    e.getMessage(),
                                    command);
                }
            }

            return Optional.empty();
        }
    }

    // -------------------------------------------------------------------------------------------------

    private static class WindowsChromeInfoResolver extends ChromeInfoResolver {
        private WindowsChromeInfoResolver() {
            super(
                    // Uses Windows Powershell to find browser version by unknown path.
                    List.of(
                            List.of("powershell",
                                    "-Command",
                                    "(Get-Item (Get-ItemProperty 'HKLM:\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\App Paths\\chrome.exe').'(Default)').VersionInfo.ProductVersion"),
                            List.of("powershell",
                                    "-Command",
                                    "(Get-Package -Name 'Google Chrome').Version")
                    ),
                    // Uses Windows cmd to find driver version by known path.
                    driverPath -> List.of(driverPath.toString(), "-v")
            );
        }

        @Override
        public boolean support(OS os) {
            return os == OS.WINDOWS;
        }

        @Override
        public Path resolveChromeDriverPath(DirectoryPath directoryPath) {
            String fileName = ChromeDriverService.CHROME_DRIVER_NAME + ".exe";
            return directoryPath.getValue().resolve(fileName);
        }
    }

    // -------------------------------------------------------------------------------------------------

    private static class LinuxChromeInfoResolver extends ChromeInfoResolver {
        private LinuxChromeInfoResolver() {
            super(
                    List.of(List.of("google-chrome", "-v")),
                    driverPath -> List.of(driverPath.toString(), "-v")
            );
        }

        @Override
        public boolean support(OS os) {
            return os == OS.LINUX || os == OS.AIX || os == OS.SOLARIS || os == OS.OTHER;
        }
    }

    // -------------------------------------------------------------------------------------------------

    private static class MacosChromeInfoResolver extends ChromeInfoResolver {
        private MacosChromeInfoResolver() {
            super(
                    List.of(List.of("/Applications/Google Chrome.app/Contents/MacOS/Google Chrome", "--version")),
                    driverPath -> List.of(driverPath.toString(), "-v")
            );
        }

        @Override
        public boolean support(OS os) {
            return os == OS.MAC;
        }
    }

}
