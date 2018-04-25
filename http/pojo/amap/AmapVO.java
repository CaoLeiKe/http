package com.zd.core.http.pojo.amap;

import com.alibaba.fastjson.JSONArray;
import com.zd.core.http.pojo.HttpResponse;
import lombok.Data;

/**
 * 钉钉响应
 */
@Data
public class AmapVO implements HttpResponse {

    private static final long serialVersionUID = 279553374574893398L;

    // 1：成功；0：失败
    private String status;
    // status为0时，info返回错误原；否则返回“OK”
    private String info;
    // 返回结果总数目
    private String count;

    // 城市建议列表 当用户输入的词语为泛搜索词的时候，将显示城市列表
    private Province suggestion;
    // 搜索POI信息列表
    private JSONArray pois;
    // 建议提示列表
    private Object tips;


    @Override
    public boolean isSuccess() {
        return status != null && status.equals("1");
    }

    @Override
    public Object getSuccessResult() {
        if (tips != null)
            return tips;
        if (suggestion != null)
            return suggestion;
        if (pois != null)
            return pois;
        return "";
    }

    @Override
    public String getErrorMsg() {
        return info;
    }


    @Data
    public class Province {
        private Object keywords;
        private JSONArray cities;
    }
}
