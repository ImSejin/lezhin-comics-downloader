package io.github.imsejin.lzcodl.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Episode {

    /**
     * Episode name in URI.
     */
    private String name;

    private Display display;
    private Properties properties;
    private int coin;
    private int point;

    /**
     * When episode was uploaded to the Lezhin Comics server.
     */
    private long updatedAt;

    /**
     * When to change to a free episode.
     *
     * <p> If this is {@code 0}, it means the episode doesn't turn free even if you wait.
     */
    private long freedAt;

    private int seq;

    /**
     * When episode actually appears on a web page to users.
     */
    private long publishedAt;
    private long id;

    public boolean isFree() {
        return this.freedAt <= System.currentTimeMillis();
    }

}
