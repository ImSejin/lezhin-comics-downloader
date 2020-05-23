package io.github.imsejin.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product {

    private Display display;
    private List<Artist> artists;
    private String alias;
    private long id;
    private List<Episode> episodes;

}
