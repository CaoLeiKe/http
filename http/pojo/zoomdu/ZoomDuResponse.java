package com.zd.core.http.pojo.zoomdu;

import com.zd.core.http.pojo.HttpResponse;
import lombok.Data;

/**
 * 中渡系统响应
 *
 * @Email caoleike@zoomdu.com
 * @User 曹磊科
 * @Time 2018/3/6 17:09
 */
@Data
public class ZoomDuResponse implements HttpResponse {

    private static final long serialVersionUID = 3528648800112629009L;

    //    {"code":201,"serverDate":"2018-03-06 16:52:55","msg":"签名错误"}
    // 响应编码
    private Integer code;
    // 响应信息
    private String msg;
    // 响应实体
    private Object rs;

    @Override
    public boolean isSuccess() {
        return code != null && (1 == code || 200 == code);
    }

    @Override
    public Object getSuccessResult() {
        return rs;
    }

    @Override
    public String getErrorMsg() {
        return msg;
    }
}
