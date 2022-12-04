package io.github.imsejin.dl.lezhin.attribute.impl;

import io.github.imsejin.dl.lezhin.attribute.Attribute;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * @since 3.0.0
 */
@Getter
@ToString
@EqualsAndHashCode
public final class AccessToken implements Attribute {

    private final UUID value;

    public AccessToken(String value) {
        this.value = UUID.fromString(value);
    }

}
