package io.github.imsejin.lzcodl.common.exception;

import javax.annotation.Nullable;

public class LoginFailureException extends RuntimeException {

    public LoginFailureException() {
        super(getErrorMessage(null));
    }

    public LoginFailureException(String errorCode) {
        super(getErrorMessage(errorCode));
    }

    public LoginFailureException(String errorCode, Throwable cause) {
        super(getErrorMessage(errorCode), cause);
    }

    private static String getErrorMessage(String errorCode) {
        return "Failed to login: " + convertErrorCode(errorCode);
    }

    private static String convertErrorCode(@Nullable String errorCode) {
        if (errorCode == null) return "unrecognized error";

        switch (errorCode) {
            case "1101":
                return "non-existent username";
            case "1104":
                return "invalid password";
            default:
                return "unrecognized error";
        }
    }

}
