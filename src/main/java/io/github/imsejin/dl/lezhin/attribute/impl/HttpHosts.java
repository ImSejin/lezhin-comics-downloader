package io.github.imsejin.dl.lezhin.attribute.impl;

import io.github.imsejin.common.assertion.Asserts;
import io.github.imsejin.dl.lezhin.attribute.Attribute;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Attribute for configuration of HTTP host
 *
 * <pre>
 *     __LZ_CONFIG__ = _.merge(window.__LZ_CONFIG__, {
 *       apiUrl: 'api.lezhin.com',
 *       cdnUrl: 'https://ccdn.lezhin.com',
 *       contentsCdnUrl: 'https://rcdn.lezhin.com',
 *       recoUrl: 'dondog.lezhin.com',
 *       payUrl: 'https://pay.lezhin.com',
 *       pantherUrl: 'https://panther.lezhin.com',
 *       ...
 *     });
 * </pre>
 *
 * @since 3.0.0
 */
@Getter
@ToString
@EqualsAndHashCode
public final class HttpHosts implements Attribute {

    private final String api;

    private final String cdn;

    private final String contentsCdn;

    private final String reco;

    private final String pay;

    private final String panther;

    public HttpHosts(String api, String cdn, String contentsCdn, String reco, String pay, String panther) {
        this.api = prependProtocolIfMissing(api);
        this.cdn = prependProtocolIfMissing(cdn);
        this.contentsCdn = prependProtocolIfMissing(contentsCdn);
        this.reco = prependProtocolIfMissing(reco);
        this.pay = prependProtocolIfMissing(pay);
        this.panther = prependProtocolIfMissing(panther);
    }

    private String prependProtocolIfMissing(String url) {
        Asserts.that(url)
                .isNotNull()
                .isNotEmpty()
                .hasText();

        // Base URL for Retrofit must end with forward slash(/).
        if (!url.endsWith("/")) {
            url += '/';
        }

        if (url.matches("^https?://[\\w.]+/$")) {
            return url;
        }

        return "https://" + url;
    }

}
