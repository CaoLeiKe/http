package com.zd.core.http;


import com.zd.configuration.Api;
import com.zd.consul.methodProxy.ConsulProxy;
import com.zd.core.http.pojo.zoomdu.ZoomDuResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.Md5Hash;

import java.util.HashMap;
import java.util.Map;

/**
 * @Email caoleike@zoomdu.com
 * @User 曹磊科
 * @Time 2018/3/7 10:50
 */
@Slf4j
public class ZoomDuHttp {

    private static final Api api = ConsulProxy.getProxyObj(Api.class);

    /**
     * 发送 文本数据流
     *
     * @param url
     * @param content
     * @return
     */
    public static ZoomDuResponse sendDefaultPost(String url, String content, boolean isJsonRequest) {
        String urlKrSign = getKrSing().insert(0, "?").insert(0, url).toString();
        return HttpUtils.sendPost(urlKrSign, content, isJsonRequest, ZoomDuResponse.class);
    }


    /**
     * post请求
     *
     * @param url
     * @param mapParams
     * @param isJsonRequest
     * @return
     */
    public static ZoomDuResponse sendDefaultPost(String url, Map<String, Object> mapParams, boolean isJsonRequest) {
        mapParams = setKrSing(mapParams);
        return HttpUtils.sendPost(url, mapParams, isJsonRequest, ZoomDuResponse.class);
    }

    /**
     * 设置krSing
     *
     * @param mapParams
     * @return
     */
    private static Map<String, Object> setKrSing(Map<String, Object> mapParams) {
        if (mapParams == null) {
            mapParams = new HashMap<>();
        }
        String k = api.getKey();
        mapParams.put("k", k);
        String r = String.valueOf(System.currentTimeMillis());
        mapParams.put("r", r);
        String sign = new Md5Hash(k + r + api.getKeySec()).toHex();
        mapParams.put("sign", sign);
        return mapParams;
    }

    /**
     * 获取url的krSing
     *
     * @return
     */
    private static StringBuilder getKrSing() {
        StringBuilder sb = new StringBuilder();
        String k = api.getKey();
        String r = String.valueOf(System.currentTimeMillis());
        String sign = new Md5Hash(k + r + api.getKeySec()).toHex();
        sb.append("k=").append(k).append("&r=").append(r).append("&sign=").append(sign);
        return sb;
    }

}
