package io.github.imsejin.dl.lezhin.process.impl;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.ini4j.Config;
import org.ini4j.Ini;
import org.jetbrains.annotations.VisibleForTesting;

import io.github.imsejin.common.util.StringUtils;
import io.github.imsejin.dl.lezhin.annotation.ProcessSpecification;
import io.github.imsejin.dl.lezhin.attribute.impl.Authentication;
import io.github.imsejin.dl.lezhin.exception.ConfigurationFileNotFoundException;
import io.github.imsejin.dl.lezhin.exception.InvalidConfigurationFileException;
import io.github.imsejin.dl.lezhin.process.ProcessContext;
import io.github.imsejin.dl.lezhin.process.Processor;

/**
 * Processor for reading the configuration file
 *
 * @since 3.0.0
 */
@ProcessSpecification
public class ConfigurationFileProcessor implements Processor {

    @VisibleForTesting
    static final String CONFIGURATION_FILE_NAME = "config.ini";

    private final Path filePath;

    public ConfigurationFileProcessor(Path basePath) {
        this.filePath = basePath.resolve(CONFIGURATION_FILE_NAME);
    }

    /**
     * Performs a process of authentication from configuration file.
     *
     * @param context process context
     * @return authentication
     * @throws ConfigurationFileNotFoundException if configuration file is not found
     * @throws InvalidConfigurationFileException  if configuration file is not specified properly
     */
    @Override
    public Authentication process(ProcessContext context)
            throws ConfigurationFileNotFoundException, InvalidConfigurationFileException {
        if (!Files.isRegularFile(this.filePath)) {
            throw new ConfigurationFileNotFoundException("There is no configuration file: %s", this.filePath);
        }

        Ini ini = readIni(this.filePath);

        Map<String, String> section = ini.get("account");
        if (section == null) {
            throw new InvalidConfigurationFileException("Configuration file has no section[account]: %s",
                    this.filePath);
        }

        String username = section.get("username");
        if (StringUtils.isNullOrBlank(username)) {
            throw new InvalidConfigurationFileException("It is invalid value of name[username] in section[account]: %s",
                    username);
        }

        String password = section.get("password");
        if (StringUtils.isNullOrBlank(password)) {
            throw new InvalidConfigurationFileException("It is invalid value of name[password] in section[account]: %s",
                    password);
        }

        return new Authentication(username, password);
    }

    // -------------------------------------------------------------------------------------------------

    private static Ini readIni(Path filePath) {
        Config conf = new Config();
        conf.setMultiSection(true);
        conf.setFileEncoding(StandardCharsets.UTF_8);

        Ini ini = new Ini();
        ini.setConfig(conf);

        try (Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            ini.load(reader);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return ini;
    }

}
