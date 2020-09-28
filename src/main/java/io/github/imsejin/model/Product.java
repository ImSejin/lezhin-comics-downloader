package io.github.imsejin.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class Product {

    private Display display;
    private List<Artist> artists;
    private String alias;
    private long id;
    private List<Episode> episodes;

}
