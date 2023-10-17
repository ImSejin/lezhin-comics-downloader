package io.github.imsejin.dl.lezhin.attribute.impl;

import io.github.imsejin.common.assertion.Asserts;
import io.github.imsejin.dl.lezhin.attribute.Attribute;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.nio.file.Path;

/**
 * @since 3.0.0
 */
@Getter
@ToString
@EqualsAndHashCode
public final class DirectoryPath implements Attribute {

    private final Path value;

    public DirectoryPath(Path path) {
        Asserts.that(path)
                .isNotNull()
                .isDirectory();

        this.value = path;
    }

}
