package io.github.imsejin.lzcodl.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Episode {

    private String name;
    private Display display;
    private Properties properties;
    private int coin;
    private int point;
    private long updatedAt;
    private long freedAt;
    private int seq;
    private long publishedAt;
    private long id;

}
