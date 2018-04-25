package com.zd.core.http.pojo.juhe;

import com.zd.core.http.pojo.HttpResponse;
import lombok.Data;

import java.io.Serializable;

/**
 * @Email caoleike@zoomdu.com
 * @User 曹磊科
 * @Time 2018/4/24 18:36
 */
@Data
public class JuheVO implements Serializable, HttpResponse {
    private static final long serialVersionUID = -4506170797842183304L;

    private Integer error_code;
    private String reason;
    private String result;

    @Override
    public boolean isSuccess() {
        return error_code != null && error_code.equals(0);
    }

    @Override
    public Object getSuccessResult() {
        return result;
    }

    @Override
    public String getErrorMsg() {
        return reason;
    }
}
