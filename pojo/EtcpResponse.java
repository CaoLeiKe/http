package com.zoomdu.utils.http.pojo;

import java.util.Objects;

public class EtcpResponse implements HttpResponse<Object> {

    private static final long serialVersionUID = 1L;

    private Integer code;
    private String message;
    private Object data;

    @Override
    public boolean isSucceed() {
        return Objects.equals(code, 0);
    }

    @Override
    public Object getSuccessResult() {
        return data;
    }

    @Override
    public String getErrorMsg() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "EtcpResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
