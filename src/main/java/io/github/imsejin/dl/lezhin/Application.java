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

import io.github.imsejin.common.tool.ClassFinder;
import io.github.imsejin.common.tool.ClassFinder.SearchPolicy;
import io.github.imsejin.common.util.ClassUtils;
import io.github.imsejin.dl.lezhin.argument.Argument;
import io.github.imsejin.dl.lezhin.argument.ArgumentsParser;
import io.github.imsejin.dl.lezhin.argument.impl.ContentName;
import io.github.imsejin.dl.lezhin.argument.impl.DebugMode;
import io.github.imsejin.dl.lezhin.argument.impl.EpisodeRange;
import io.github.imsejin.dl.lezhin.argument.impl.Language;
import io.github.imsejin.dl.lezhin.argument.impl.SaveAsJpeg;
import io.github.imsejin.dl.lezhin.browser.WebBrowser;
import io.github.imsejin.dl.lezhin.common.Loggers;
import io.github.imsejin.dl.lezhin.process.ProcessContext;
import io.github.imsejin.dl.lezhin.process.Processor;
import io.github.imsejin.dl.lezhin.process.framework.ProcessorCreator;
import io.github.imsejin.dl.lezhin.process.framework.ProcessorOrderResolver;
import io.github.imsejin.dl.lezhin.util.PathUtils;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toUnmodifiableSet;

public final class Application {

    public static void main(String[] args) {
        ArgumentsParser argumentsParser = new ArgumentsParser(
                new Language(), new ContentName(), new EpisodeRange(), new SaveAsJpeg(), new DebugMode());
//        List<Argument> arguments = argumentsParser.parse(args);
        List<Argument> arguments = argumentsParser.parse("-l=en", "-n=appetite", "-d");

        ProcessContext context = ProcessContext.create(arguments.toArray());
        if (context.getDebugMode().getValue()) {
            Loggers.debugging();
            WebBrowser.debugging();
        }

        // Finds all types of implementation of the processor.
        Set<Class<? extends Processor>> processorTypes = ClassFinder.getAllSubtypes(Processor.class, SearchPolicy.CLASS)
                .stream().filter(it -> it.getEnclosingClass() == null && !ClassUtils.isAbstractClass(it))
                .collect(toUnmodifiableSet());

        // Sorts order of the types.
        List<Class<? extends Processor>> orderedTypes = ProcessorOrderResolver.resolve(processorTypes);

        // Prepares objects needed to instantiate the processors.
        List<Object> beans = List.of(PathUtils.getCurrentPath(), context.getLanguage().getValue());

        // Creates the processors with beans.
        ProcessorCreator processorCreator = new ProcessorCreator(beans.toArray());
        List<Processor> processors = processorCreator.create(orderedTypes);

        try {
            for (Processor processor : processors) {
                Object result = processor.process(context);
                context = ProcessContext.of(context, result);
            }
        } catch (Exception e) {
            WebBrowser.quitIfInitialized();
            throw e;
        }
    }

}
