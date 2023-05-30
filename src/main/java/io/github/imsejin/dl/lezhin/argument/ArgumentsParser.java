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

package io.github.imsejin.dl.lezhin.argument;

import io.github.imsejin.common.util.ArrayUtils;
import io.github.imsejin.common.util.ReflectionUtils;
import io.github.imsejin.dl.lezhin.exception.DuplicatedArgumentException;
import io.github.imsejin.dl.lezhin.exception.ParsingArgumentException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @since 3.0.0
 */
@ThreadSafe
public class ArgumentsParser {

    private final Options options;

    private final List<Argument> arguments;

    public ArgumentsParser(Argument... arguments) throws DuplicatedArgumentException {
        Options options = new Options();

        for (Argument argument : arguments) {
            Option option = argument.getOption();

            if (options.getOption(option.getOpt()) != null) {
                throw new DuplicatedArgumentException("ArgumentsParser received an argument registered already: %s(option=[%s,%s])",
                        argument.getClass().getSimpleName(), option.getOpt(), option.getLongOpt());
            }

            options.addOption(option);
        }

        this.options = options;
        this.arguments = List.of(arguments);
    }

    /**
     * Parses program arguments.
     *
     * @param args program arguments
     * @return arguments
     * @throws ParsingArgumentException if failed to parse argument
     */
    public List<Argument> parse(String... args) throws ParsingArgumentException {
        CommandLine cmd;

        try {
            cmd = new DefaultParser().parse(this.options, args);
        } catch (ParseException e) {
            // Without required options or arguments, the program will exit.
            new HelpFormatter().printHelp(" ", null, this.options, "", true);
            throw new ParsingArgumentException(e, "Failed to parse argument: %s", ArrayUtils.toString(args));
        }

        List<Argument> clones = new ArrayList<>();

        for (Argument argument : this.arguments) {
            String optionValue = getOptionValue(cmd, argument.getOption());

            // Checks validity of the option value for each argument.
            argument.validate(optionValue);

            // Defensive copy.
            // This needs high cost so that will be performed after checking the argument has no problem.
            Argument clone = ReflectionUtils.instantiate(argument.getClass());

            clone.setValue(optionValue);
            clones.add(clone);
        }

        return Collections.unmodifiableList(clones);
    }

    // -------------------------------------------------------------------------------------------------

    private static String getOptionValue(CommandLine cmd, Option option) {
        String optionValue = cmd.getOptionValue(option.getOpt());

        // When option with required argument.
        if (option.hasArg() && !option.hasOptionalArg()) {
            return Objects.requireNonNullElse(optionValue, "");
        }

        // When option has optional argument.
        if (optionValue != null) {
            return optionValue;
        }

        // When option without argument.
        boolean hasOption = cmd.hasOption(option.getOpt());
        return String.valueOf(hasOption);
    }

}
