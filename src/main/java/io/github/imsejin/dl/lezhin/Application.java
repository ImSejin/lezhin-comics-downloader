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

package io.github.imsejin.dl.lezhin;

import io.github.imsejin.dl.lezhin.argument.Argument;
import io.github.imsejin.dl.lezhin.argument.ArgumentsParser;
import io.github.imsejin.dl.lezhin.argument.impl.ContentName;
import io.github.imsejin.dl.lezhin.argument.impl.DebugMode;
import io.github.imsejin.dl.lezhin.argument.impl.EpisodeRange;
import io.github.imsejin.dl.lezhin.argument.impl.Language;
import io.github.imsejin.dl.lezhin.argument.impl.SaveAsJpeg;
import io.github.imsejin.dl.lezhin.exception.LezhinComicsDownloaderException;
import io.github.imsejin.dl.lezhin.process.ProcessContext;
import io.github.imsejin.dl.lezhin.process.Processor;
import io.github.imsejin.dl.lezhin.process.impl.ConfigurationFileProcessor;
import io.github.imsejin.dl.lezhin.process.impl.EpisodeAuthorityProcessor;
import io.github.imsejin.dl.lezhin.process.impl.LoginProcessor;
import io.github.imsejin.dl.lezhin.util.PathUtils;

import java.nio.file.Path;
import java.util.List;

public final class Application {

    public static void main(String[] args) throws LezhinComicsDownloaderException {
        ArgumentsParser argumentsParser = new ArgumentsParser(
                new Language(), new ContentName(), new EpisodeRange(), new SaveAsJpeg(), new DebugMode());
//        argumentsParser.parse(args);
        List<Argument> arguments = argumentsParser.parse("-l=en", "-n=name", "-d");

        Path currentPath = PathUtils.getCurrentPath();

        List<Processor> processors = List.of(
                new ConfigurationFileProcessor(currentPath),
                new LoginProcessor(),
                new EpisodeAuthorityProcessor()
        );

        ProcessContext context = ProcessContext.create(arguments.toArray());
        for (Processor processor : processors) {
            Object result = processor.process(context);
            context = ProcessContext.of(context, result);
        }
    }

}
