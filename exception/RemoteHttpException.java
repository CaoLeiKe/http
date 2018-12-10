package com.zoomdu.utils.http.exception;

/**
 * 远程http调用总异常
 */
public class RemoteHttpException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RemoteHttpException(String message) {
        super(message);
    }

    public RemoteHttpException(Throwable cause) {
        super(cause);
    }

    public RemoteHttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
