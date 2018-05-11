package com.zd.core.http;

import com.alibaba.fastjson.JSON;
import com.zd.core.http.pojo.HttpResponse;
import com.zd.exception.RemoteHttpCallException;
import com.zd.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @Email caoleike@zoomdu.com
 * @User 曹磊科
 * @Time 2018/3/16 19:44
 */
@Slf4j
public class HttpUtils {

    /**
     * json转化成实体类
     */
    private static <T extends HttpResponse> T getT(Class<T> tClass, String result) {

        T t;
        try {
            t = JSON.parseObject(result, tClass);
        } catch (Exception e) {
            log.error("请求结果json转实体异常，类型:{},请求数据:{}", tClass.getName(), result);
            throw new RuntimeException(e);
        }

        if (!t.isSuccess()) {
            log.error("远程http请求返回的结果不正确");
            log.error("请求的结果：{}", result);
            log.error("请求转化后的实体：{}", t);
            throw new RemoteHttpCallException(t.getErrorMsg());
        }
        return t;
    }

    /**
     * 读取响应数据并转化成string
     */
    private static String getString(Pair<CloseableHttpResponse, HttpRequestBase> pair) {
        CloseableHttpResponse key = pair.getKey();
        CommonUtils.httpIs200(key.getStatusLine());

        HttpRequestBase value = pair.getValue();
        try {

            return EntityUtils.toString(key.getEntity());
        } catch (IOException e) {
            log.error("读取响应出错", e);
            throw new RemoteHttpCallException(e.getMessage(), e);
        } finally {
            value.releaseConnection();
            try {
                key.close();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * GET string参数
     */
    public static String sendGet(String url, String strParams) {
        Pair<CloseableHttpResponse, HttpRequestBase> pair = HttpClient.sendDefaultGet(url, strParams);
        return getString(pair);
    }


    /**
     * 发送Post请求，String参数
     * 命令
     */
    public static <T extends HttpResponse> T sendPost(String url, String strParams, boolean isJsonRequest, Class<T> tClass) {
        Pair<CloseableHttpResponse, HttpRequestBase> pair = HttpClient.sendDefaultPost(url, strParams, isJsonRequest);
        return getT(tClass, getString(pair));
    }

    /**
     * 发送Post请求，String参数
     */
    public static String sendPost(String url, String strParams, boolean isJsonRequest) {
        Pair<CloseableHttpResponse, HttpRequestBase> pair = HttpClient.sendDefaultPost(url, strParams, isJsonRequest);
        return getString(pair);
    }

    /**
     * 发送Post请求，Map参数
     */
    public static String sendPost(String url, Map<String, ?> mapParams, boolean isJsonRequest) {
        Pair<CloseableHttpResponse, HttpRequestBase> pair = HttpClient.sendDefaultPost(url, mapParams, isJsonRequest);
        return getString(pair);
    }

    /**
     * 返回全部的数据，不建议使用，注意流的关闭
     */
    @Deprecated
    public static Pair<CloseableHttpResponse, HttpRequestBase> sendPostReAll(String url, Map<String, ?> mapParams, boolean isJsonRequest) {
        return HttpClient.sendDefaultPost(url, mapParams, isJsonRequest);
    }

    /**
     * 返回全部的数据，不建议使用，注意流的关闭
     */
    @Deprecated
    public static Pair<CloseableHttpResponse, HttpRequestBase> sendPostReAll(String url, String strParams, boolean isJsonRequest) {
        return HttpClient.sendDefaultPost(url, strParams, isJsonRequest);
    }

    /**
     * 发送Post请求，Map参数
     */
    public static <T extends HttpResponse> T sendPost(String url, Map<String, ?> mapParams, boolean isJsonRequest, Class<T> tClass) {
        Pair<CloseableHttpResponse, HttpRequestBase> pair = HttpClient.sendDefaultPost(url, mapParams, isJsonRequest);
        return getT(tClass, getString(pair));
    }
}
