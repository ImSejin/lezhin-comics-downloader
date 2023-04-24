package io.github.imsejin.dl.lezhin.browser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @since 2.6.2
 */
@Getter
@RequiredArgsConstructor
public enum ChromeOption {

    /**
     * Opens browser on private mode.
     */
    INCOGNITO("--incognito"),

    /**
     * Runs browser using CLI.
     */
    HEADLESS("--headless=new"),

    /**
     * Bypasses OS security model.
     *
     * @since 2.8.2
     */
    NO_SANDBOX("--no-sandbox"),

    /**
     * Disables GPU computation (applicable to Windows OS only).
     *
     * @since 2.8.2
     */
    DISABLE_GPU("--disable-gpu"),

    /**
     * Ignores certificate errors.
     */
    IGNORE_CERTIFICATE_ERRORS("--ignore-certificate-errors"),

    /**
     * Disables to check if Google Chrome is default browser on your device.
     *
     * @since 2.8.2
     */
    NO_DEFAULT_BROWSER_CHECK("--no-default-browser-check"),

    /**
     * Disables popup blocking.
     */
    DISABLE_POPUP_BLOCKING("--disable-popup-blocking"),

    /**
     * Disables installed extensions(plugins) of Google Chrome.
     *
     * @since 2.8.2
     */
    DISABLE_EXTENSIONS("--disable-extensions"),

    /**
     * Disables default web apps on Google Chrome’s new tab page
     * <p>
     * Chrome Web Store, Google Drive, Gmail, YouTube, Google Search, etc.
     */
    DISABLE_DEFAULT_APPS("--disable-default-apps"),

    /**
     * Disables Google translate feature.
     *
     * @since 2.8.2
     */
    DISABLE_TRANSLATE("--disable-translate"),

    /**
     * Disables detection for client side phishing.
     *
     * @since 2.8.2
     */
    DISABLE_CLIENT_SIDE_PHISHING_DETECTION("--disable-client-side-phishing-detection"),

    /**
     * Overcomes limited resource problems.
     *
     * @since 2.8.2
     */
    DISABLE_DEV_SHM_USAGE("--disable-dev-shm-usage"),

    /**
     * Allows all remote origins.
     *
     * @since 3.0.4
     */
    REMOTE_ALLOW_ALL_ORIGINS("--remote-allow-origins=*"),

    /**
     * Opens browser in maximized mode.
     */
    START_MAXIMIZED("--start-maximized");

    private final String argument;

    public static List<String> getArguments() {
        return Arrays.stream(values()).map(it -> it.argument).collect(toList());
    }

}
