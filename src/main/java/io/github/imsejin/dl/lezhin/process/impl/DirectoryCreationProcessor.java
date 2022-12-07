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

import io.github.imsejin.common.util.FilenameUtils;
import io.github.imsejin.dl.lezhin.annotation.ProcessSpecification;
import io.github.imsejin.dl.lezhin.attribute.impl.Content.Artist;
import io.github.imsejin.dl.lezhin.common.Loggers;
import io.github.imsejin.dl.lezhin.exception.DirectoryCreationException;
import io.github.imsejin.dl.lezhin.process.ProcessContext;
import io.github.imsejin.dl.lezhin.process.Processor;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.stream.Collectors.joining;

/**
 * Processor for creating a directory of the content
 *
 * @since 3.0.0
 */
@RequiredArgsConstructor
@ProcessSpecification(dependsOn = ContentInformationProcessor.class)
public class ContentDirectoryProcessor implements Processor {

    private final Path basePath;

    @Override
    public Void process(ProcessContext context) throws DirectoryCreationException {
        String contentTitle = context.getContent().getDisplay().getTitle();
        String artists = context.getContent().getArtists().stream().map(Artist::getName)
                .collect(joining(", "));
        String directoryName = FilenameUtils.replaceUnallowables(String.format("L_%s - %s", contentTitle, artists));

        Path targetPath = this.basePath.resolve(directoryName);

        if (!Files.isDirectory(targetPath)) {
            Loggers.getLogger().debug("Create directory: {}", targetPath);

            try {
                Files.createDirectory(targetPath);
            } catch (IOException e) {
                throw new DirectoryCreationException(e, "Failed to create directory: %s", targetPath);
            }
        }

        return null;
    }

}
