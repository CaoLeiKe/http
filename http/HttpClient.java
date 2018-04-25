package com.zd.core.http;

import com.alibaba.fastjson.JSON;
import com.zd.exception.RemoteHttpCallException;
import com.zd.utils.URLEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * 统一返回响应正确的信息
 *
 * @User 曹磊科
 * @Time 2018/3/7 15:52
 */
@Slf4j
class HttpClient {

    /** cookie */
    public static CookieStore cookieStore = new BasicCookieStore();

    /** 10秒没有响应，直接抛异常，异常处理器会处理 */
    private static final int TIME_OUT = 10;

    /** http请求最大的连接数，超量则等待 */
    private static final int MAX_CONN_TOTAL = 100;

    /** 构造Http客户端 */
    private static final CloseableHttpClient httpClient = HttpClientBuilder.create()
            .setConnectionTimeToLive(TIME_OUT, TimeUnit.SECONDS)
            .setMaxConnTotal(MAX_CONN_TOTAL)
            .setDefaultCookieStore(cookieStore)
            .build();

    /**
     * POST请求
     * 默认时间 全局
     * 默认字符集 utf-8
     *
     * @return
     */
    static Pair<CloseableHttpResponse, HttpRequestBase> sendDefaultPost(String url, Map<String, ?> mapParams, boolean isJsonRequest) {
        String requestData;
        HashMap<String, String> headerMap = new HashMap<>();
        if (isJsonRequest) {
            requestData = JSON.toJSONString(mapParams);
            headerMap.put("Content-Type", "application/json");
        } else {
            headerMap.put("Content-Type", "application/x-www-form-urlencoded");
            requestData = mapToUrlParams(mapParams);
        }
        return doPost(url, headerMap, requestData);
    }

    /**
     * POST请求
     * 默认时间 全局
     * 默认字符集 utf-8
     *
     * @return
     */
    static Pair<CloseableHttpResponse, HttpRequestBase> sendDefaultPost(String url, String strParams, boolean isJsonRequest) {
        HashMap<String, String> headerMap = new HashMap<>();
        // 不设定则会默认使用 "application/x-www-form-urlencoded"，这样会从流中读取不到数据
        if (isJsonRequest) {
            headerMap.put("Content-Type", "application/json");
        } else {
            headerMap.put("Content-Type", "application/x-www-form-urlencoded");
        }
        return doPost(url, headerMap, strParams);
    }

    /**
     * POST请求、自定义头响应
     */
    static Pair<CloseableHttpResponse, HttpRequestBase> sendCustomPost(String url, Map<String, String> headerMap, String strParams) {
        if (strParams == null) {
            strParams = "";
        }
        return doPost(url, headerMap, strParams);
    }

    /**
     * GET请求
     * 默认时间 全局
     * 默认字符集 utf-8
     *
     * @return
     */
    static Pair<CloseableHttpResponse, HttpRequestBase> sendDefaultGet(String url, String strParams) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/x-www-form-urlencoded");
        return doGet(url, headerMap, strParams);
    }

    /**
     * GET请求
     * 默认时间 全局
     * 默认字符集 utf-8
     *
     * @return
     */
    static Pair<CloseableHttpResponse, HttpRequestBase> sendDefaultGet(String url, Map<String, String> mapParams) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/x-www-form-urlencoded");
        return doGet(url, headerMap, mapToUrlParams(mapParams));
    }

    /**
     * GET请求
     * 默认时间 全局
     * 默认字符集 utf-8
     *
     * @return
     */
    static Pair<CloseableHttpResponse, HttpRequestBase> sendDefaultGet(String url, Map<String, String> headerMap, Map<String, ?> mapParams) {
        if (headerMap != null) {
            headerMap.put("Content-Type", "application/x-www-form-urlencoded");
        }
        return doGet(url, headerMap, mapToUrlParams(mapParams));
    }


    private static Pair<CloseableHttpResponse, HttpRequestBase> doPost(String url, Map<String, String> mapHeaders, String strParams) {
        HttpPost httpPost = new HttpPost(url);
        StringEntity se = new StringEntity(strParams, CharEncoding.UTF_8);
        httpPost.setEntity(se);
        return requestAfter(url, mapHeaders, strParams, httpPost);
    }

    private static Pair<CloseableHttpResponse, HttpRequestBase> doGet(String url, Map<String, String> mapHeaders, String strParams) {
        HttpGet httpGet = new HttpGet(url + (strParams != null ? ("?" + URLEncoder.QUERY.encode(strParams, Charset.forName(CharEncoding.UTF_8))) : ""));
        return requestAfter(url, mapHeaders, strParams, httpGet);
    }

    /**
     * map转化url参数，如果是特殊字符会进行转义
     *
     * @param map
     * @return
     */
    private static String mapToUrlParams(Map map) {
        if (map == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object key : map.keySet()) {
            Object value = map.get(key);
            if (value == null) {
                sb.append(key).append("=").append("").append("&");
            }
            sb.append(key).append("=").append(value).append("&");
        }
        String result = sb.substring(0, sb.length() - 1);
        return URLEncoder.QUERY.encode(result, Charset.forName(CharEncoding.UTF_8));
    }

    private static Pair<CloseableHttpResponse, HttpRequestBase> requestAfter(String url, Map<String, String> mapHeaders, String strParams, HttpRequestBase httpMethod) {
        long start = System.currentTimeMillis();
        if (mapHeaders != null) {
            for (Entry<String, String> entry : mapHeaders.entrySet()) {
                httpMethod.addHeader(entry.getKey(), entry.getValue());
            }
        }
        try {
            CloseableHttpResponse response = httpClient.execute(httpMethod);
            long end = System.currentTimeMillis();
            log.info("*****远程{} Start*****", httpMethod.getMethod());
            log.info("url:{}", url);
            log.debug("mapHeaders:{}", mapHeaders);
            log.debug("requestData:{}", strParams);
            log.info("*****远程http end,耗时：{}*****", end - start);
            return Pair.of(response, httpMethod);
        } catch (IOException e) {
            throw new RemoteHttpCallException(e);
        }
    }

}  