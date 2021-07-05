package com.github.simonalong.troy.exception;

import lombok.Getter;

/**
 * @author shizi
 * @since 2021-04-13 17:06:40
 */
@Getter
public class HttpException extends RuntimeException{

    private Integer code;
    private String message;

    public HttpException(Integer code, Throwable e) {
        super(e);
        this.code = code;
    }

    public HttpException(Integer code, String message, Throwable e) {
        super(e);
        this.code = code;
        this.message = message;
    }

    public HttpException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
