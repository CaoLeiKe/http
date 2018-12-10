package com.zoomdu.utils.http.pojo;

import java.io.Serializable;

/**
 * 请求是否成功的接口
 *
 * @author 曹磊科
 * @date 2018/12/10 12:35
 */
public interface HttpResponse<T> extends Serializable {

    /** 请求是否成功 */
    boolean isSucceed();

    /** 获取请求的数据 */
    T getSuccessResult();

    /** 请求失败的原因 */
    String getErrorMsg();
}
