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

import org.openqa.selenium.chrome.ChromeDriverService;

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

        ChromeVersion browserVersion = resolver.resolveChromeBrowserVersion();

        // Regards chrome browser as installed.
        if (browserVersion == null) {
            Loggers.getLogger().debug("Failed to resolve version of chrome browser: ignore this step");
        }

        Path driverPath = resolver.resolveChromeDriverPath(context.getDirectoryPath());
        if (!Files.isRegularFile(driverPath)) {
            return new ChromeInfo(browserVersion, null, null);
        }

        ChromeVersion driverVersion = resolver.resolveChromeDriverVersion(driverPath);

        return new ChromeInfo(browserVersion, driverVersion, driverPath);
    }

    // -------------------------------------------------------------------------------------------------

    private interface ChromeInfoResolver {
        boolean support(OS os);

        ChromeVersion resolveChromeBrowserVersion();

        ChromeVersion resolveChromeDriverVersion(Path driverPath);

        default Path resolveChromeDriverPath(DirectoryPath directoryPath) {
            return directoryPath.getValue().resolve(ChromeDriverService.CHROME_DRIVER_NAME);
        }
    }

    // -------------------------------------------------------------------------------------------------

    private static class WindowsChromeInfoResolver implements ChromeInfoResolver {
        @Override
        public boolean support(OS os) {
            return os == OS.WINDOWS;
        }

        @Override
        public ChromeVersion resolveChromeBrowserVersion() {
            List<List<String>> commands = List.of(
                    List.of("powershell",
                            "-command",
                            "(Get-Item (Get-ItemProperty 'HKLM:\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\App Paths\\chrome.exe').'(Default)').VersionInfo.ProductVersion"),
                    List.of("powershell",
                            "-command",
                            "(Get-Package -Name 'Google Chrome').Version")
            );

            for (List<String> command : commands) {
                try {
                    String versionString = CommandUtils.runCommand(command.toArray(new String[0]));
                    return ChromeVersion.from(versionString);
                } catch (Exception ignored) {
                }
            }

            return null;
        }

        @Override
        public ChromeVersion resolveChromeDriverVersion(Path driverPath) {
            try {
                String result = CommandUtils.runCommand("cmd", "/c", "\"" + driverPath.toRealPath() + "\"", "-v");
                return ChromeVersion.from(result);
            } catch (Exception ignored) {
            }

            return null;
        }

        @Override
        public Path resolveChromeDriverPath(DirectoryPath directoryPath) {
            String baseName = ChromeDriverService.CHROME_DRIVER_NAME + ".exe";
            return directoryPath.getValue().resolve(baseName);
        }
    }

    // -------------------------------------------------------------------------------------------------

    private static class LinuxChromeInfoResolver implements ChromeInfoResolver {
        @Override
        public boolean support(OS os) {
            return os == OS.LINUX || os == OS.AIX || os == OS.SOLARIS || os == OS.OTHER;
        }

        @Override
        public ChromeVersion resolveChromeBrowserVersion() {
            try {
                String versionString = CommandUtils.runCommand("google-chrome", "-v");
                return ChromeVersion.from(versionString);
            } catch (Exception ignored) {
                return null;
            }
        }

        @Override
        public ChromeVersion resolveChromeDriverVersion(Path driverPath) {
            return null;
        }
    }

    // -------------------------------------------------------------------------------------------------

    private static class MacosChromeInfoResolver implements ChromeInfoResolver {
        @Override
        public boolean support(OS os) {
            return os == OS.MAC;
        }

        @Override
        public ChromeVersion resolveChromeBrowserVersion() {
            try {
                String versionString = CommandUtils.runCommand(
                        "/Applications/Google\\ Chrome.app/Contents/MacOS/Google\\ Chrome",
                        "--version");
                return ChromeVersion.from(versionString);
            } catch (Exception ignored) {
                return null;
            }
        }

        @Override
        public ChromeVersion resolveChromeDriverVersion(Path driverPath) {
            return null;
        }
    }

}
