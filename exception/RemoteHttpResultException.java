package com.zoomdu.utils.http.exception;


import com.zoomdu.utils.http.pojo.HttpResponse;

/**
 * 远程调用http返回结果异常
 */
public class RemoteHttpResultException extends RemoteHttpException {

    private static final long serialVersionUID = -5721724888986867128L;

    private HttpResponse resultObj;

    private String resultStr;

    /**
     * 请求结果转换异常
     */
    public <T extends HttpResponse> RemoteHttpResultException(String message, T resultObj, String resultStr) {
        super(message);
        this.resultObj = resultObj;
        this.resultStr = resultStr;
    }

    public RemoteHttpResultException(Throwable cause) {
        super(cause);
    }

    public RemoteHttpResultException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpResponse getResultObj() {
        return resultObj;
    }

    public String getResultStr() {
        return resultStr;
    }
}
