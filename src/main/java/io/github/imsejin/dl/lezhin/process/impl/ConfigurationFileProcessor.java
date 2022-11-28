package io.github.imsejin.dl.lezhin.process.impl;

import io.github.imsejin.common.util.IniUtils;
import io.github.imsejin.common.util.StringUtils;
import io.github.imsejin.dl.lezhin.attribute.impl.Authentication;
import io.github.imsejin.dl.lezhin.exception.ConfigurationFileNotFoundException;
import io.github.imsejin.dl.lezhin.exception.InvalidConfigurationFileException;
import io.github.imsejin.dl.lezhin.exception.LezhinComicsDownloaderException;
import io.github.imsejin.dl.lezhin.process.ProcessContext;
import io.github.imsejin.dl.lezhin.process.Processor;
import org.ini4j.Ini;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ConfigurationFileProcessor implements Processor {

    private final Path filePath;

    public ConfigurationFileProcessor(Path basePath) {
        this.filePath = basePath.resolve("config.ini");
    }

    @Override
    public Authentication process(ProcessContext context) throws LezhinComicsDownloaderException {
        if (!Files.isRegularFile(this.filePath)) {
            throw new ConfigurationFileNotFoundException("There is no configuration file: %s", this.filePath);
        }

        Ini ini = IniUtils.read(this.filePath.toFile());

        Map<String, String> section = ini.get("account");
        if (section == null) {
            throw new InvalidConfigurationFileException("Configuration file has no section[account]: %s", this.filePath);
        }

        String username = section.get("username");
        if (StringUtils.isNullOrBlank(username)) {
            throw new InvalidConfigurationFileException("It is invalid value of name[username] in section[account]: %s", username);
        }

        String password = section.get("password");
        if (StringUtils.isNullOrBlank(password)) {
            throw new InvalidConfigurationFileException("It is invalid value of name[password] in section[account]: %s", password);
        }

        return new Authentication(username, password);
    }

}
