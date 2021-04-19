package com.github.simonalong.autologger.exception;

/**
 * @author shizi
 * @since 2021-04-13 17:11:15
 */
public class AutoLoggerException extends RuntimeException {

    public AutoLoggerException(String code, String message) {
        super(code + "：" + message);
    }

    public AutoLoggerException(String message) {
        super(message);
    }
}
