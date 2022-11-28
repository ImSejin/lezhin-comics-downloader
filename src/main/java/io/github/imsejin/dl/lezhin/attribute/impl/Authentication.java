package io.github.imsejin.dl.lezhin.attribute.impl;

import io.github.imsejin.dl.lezhin.attribute.Attribute;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public final class Authentication implements Attribute {

    private final String username;

    private String password;

    public void erasePassword() {
        if (!"<erased>".equals(this.password)) {
            this.password = "<erased>";
        }
    }

}
