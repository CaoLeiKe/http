package com.zd.core.http.pojo.dingding;

import com.zd.core.http.pojo.HttpResponse;
import lombok.Data;

/**
 * 钉钉响应
 */
@Data
public class DingDingVO implements HttpResponse {

    private static final long serialVersionUID = 279553374574893398L;

    private Integer errCode;
    private String errMsg;

    public boolean isSuccess() {
        return errCode != null && errCode.equals(0);
    }

    @Override
    public Object getSuccessResult() {
        return "";
    }

    @Override
    public String getErrorMsg() {
        return errMsg;
    }
}
