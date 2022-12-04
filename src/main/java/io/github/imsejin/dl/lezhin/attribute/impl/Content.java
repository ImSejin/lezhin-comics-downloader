package io.github.imsejin.dl.lezhin.attribute.impl;

import io.github.imsejin.dl.lezhin.attribute.Attribute;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @since 3.0.0
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public final class Content implements Attribute {

    private Long id;

    /**
     * Content name in URI.
     */
    private String alias;

    private Display display;

    private Properties properties;

    private String locale;

    private String state;

    private List<Artist> artists;

    private List<Episode> episodes;

    // -------------------------------------------------------------------------------------------------

    /**
     * @since 1.0.0
     */
    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    public static final class Artist {
        private String id;
        private String name;
        private String role;
        private String email;
    }

    // -------------------------------------------------------------------------------------------------

    /**
     * @since 1.0.0
     */
    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    public static final class Episode {
        private Long id;

        /**
         * Episode name in URI.
         */
        private String name;

        private Integer seq;

        private Display display;

        private Properties properties;

        private Integer coin;

        /**
         * When episode was uploaded to the Lezhin Comics server.
         */
        private Long updatedAt;

        /**
         * When episode actually appears on a web page to users.
         */
        private Long publishedAt;

        /**
         * When to change to a free episode.
         *
         * <p> If this is {@code null}, it means the episode doesn't turn free even if you wait.
         */
        @Nullable
        private Long freedAt;

        /**
         * @since 2.6.0
         */
        public boolean isFree() {
            return this.coin == 0 || (this.freedAt != null && this.freedAt <= System.currentTimeMillis());
        }
    }

    // -------------------------------------------------------------------------------------------------

    /**
     * @since 1.0.0
     */
    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    public static final class Display {
        private String title;
        private String synopsis;
    }

    // -------------------------------------------------------------------------------------------------

    /**
     * @since 1.0.0
     */
    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    public static final class Properties {
        private boolean expired;
        private boolean notForSale;
    }

}
