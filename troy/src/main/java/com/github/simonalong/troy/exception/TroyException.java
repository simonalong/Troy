package com.github.simonalong.troy.exception;

/**
 * @author shizi
 * @since 2021-04-13 17:11:15
 */
public class TroyException extends RuntimeException {

    public TroyException(String code, String message) {
        super(code + "：" + message);
    }

    public TroyException(String message) {
        super(message);
    }
}
