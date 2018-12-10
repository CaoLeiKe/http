package com.zoomdu.utils.http;

import com.alibaba.fastjson.JSON;
import com.zoomdu.utils.http.exception.RemoteHttpException;
import com.zoomdu.utils.http.exception.RemoteHttpResultException;
import com.zoomdu.utils.http.exception.RemoteHttpStatusCodeException;
import com.zoomdu.utils.http.pojo.HttpResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * http工具类,目前支持Post和Get请求，后续可加
 *
 * @author 曹磊科
 * @date 2018/05/10 12:34
 */
public class HttpUtils {

    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * json转化成实体类
     */
    public static <T extends HttpResponse> T getHttpResponse(Class<T> tClass, String result) {

        T t;
        try {
            t = JSON.parseObject(result, tClass);
        } catch (Exception e) {
            log.error("请求结果json转实体异常，类型:{},请求数据:{}", tClass.getName(), result);
            throw new RemoteHttpResultException(e);
        }

        if (!t.isSucceed()) {
            log.error("远程http请求返回的结果不正确");
            log.error("请求的结果：{}", result);
            log.error("请求转化后的实体：{}", t);
            throw new RemoteHttpResultException(t.getErrorMsg(), t, result);
        }
        return t;
    }

    /**
     * 读取响应数据并转化成string
     */
    private static String getStringResult(Pair<CloseableHttpResponse, CookieStore> pair, String url, Map<String, ?> params) {
        try (CloseableHttpResponse key = pair.getKey()) {
            if (key.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.error("状态码不正确:{}", key);
                log.error("请求地址:{}", url);
                log.error("请求参数:{}", params);
                throw new RemoteHttpStatusCodeException(key.getStatusLine().getReasonPhrase());
            }
            return EntityUtils.toString(key.getEntity());
        } catch (IOException e) {
            log.error("读取响应出错", e);
            log.error("请求地址:{}", url);
            log.error("请求参数:{}", params);
            throw new RemoteHttpException(e.getMessage(), e);
        }
    }

    /**
     * 返回全部的数据，不建议使用，注意流的关闭
     */
    @Deprecated
    public static Pair<CloseableHttpResponse, CookieStore> senGetReAll(String url, Map<String, String> headMap, Map<String, String> queryMapParams, Cookie... cookies) {
        return HttpClient.doGet(url, headMap, queryMapParams, cookies);
    }

    /**
     * GET Map参数
     */
    public static String sendGet(String url, Map<String, String> queryMapParams) {
        return sendGet(url, null, queryMapParams);
    }

    /**
     * GET 自定义头，Map参数
     */
    public static String sendGet(String url, Map<String, String> headMap, Map<String, String> queryMapParams) {
        return getStringResult(senGetReAll(url, headMap, queryMapParams), url, queryMapParams);
    }

    /**
     * 返回全部的数据，不建议使用，注意流的关闭
     */
    @Deprecated
    public static Pair<CloseableHttpResponse, CookieStore> senPostReAll(String url, Map<String, String> headMap, HttpEntity httpEntity, Cookie... cookies) {
        return HttpClient.doPost(url, headMap, httpEntity, cookies);
    }

    /**
     * 发送Post请求，Map参数
     */
    public static String sendJSONPost(String url, Map<String, ?> mapParams) {
        return sendJSONPost(url, null, mapParams);
    }

    /**
     * 发送Post请求，自定义头，Map参数
     */
    public static String sendJSONPost(String url, Map<String, String> headMap, Map<String, ?> mapParams) {
        StringEntity stringEntity = new StringEntity(JSON.toJSONString(mapParams), ContentType.APPLICATION_JSON);
        return getStringResult(senPostReAll(url, headMap, stringEntity), url, mapParams);
    }
}
