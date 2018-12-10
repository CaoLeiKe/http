package com.zoomdu.utils.http.exception;

/**
 * 远程调用http响应码异常
 * 不是200报异常
 */
public class RemoteHttpStatusCodeException extends RemoteHttpException {

    private static final long serialVersionUID = -1L;

    public RemoteHttpStatusCodeException(String message) {
        super(message);
    }

    public RemoteHttpStatusCodeException(Throwable cause) {
        super(cause);
    }

    public RemoteHttpStatusCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
