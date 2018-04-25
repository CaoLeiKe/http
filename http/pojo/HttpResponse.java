package com.zd.core.http.pojo;

import java.io.Serializable;

/**
 * @Email caoleike@zoomdu.com
 * @User 曹磊科
 * @Time 2018/3/16 20:03
 */
public interface HttpResponse extends Serializable {

    /** 请求是否成功 */
    boolean isSuccess();

    /** 获取请求的数据 */
    Object getSuccessResult();

    /** 请求失败的原因 */
    String getErrorMsg();
}
