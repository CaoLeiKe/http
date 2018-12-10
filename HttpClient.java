package com.zoomdu.utils.http;

import com.zoomdu.utils.http.cookie.DefaultCookieStore;
import com.zoomdu.utils.http.exception.RemoteHttpException;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 封装httpClient
 * 所有的请求同用一个方法
 *
 * @author 曹磊科
 * @date 2018/05/10 12:32
 */
class HttpClient {
    private static final Logger log = LoggerFactory.getLogger(HttpClient.class);

    /** dns解析最大时间，超时SocketTimeoutException */
    private static final int CONNECT_TIMEOUT = 5 * 1000;

    /** http请求最大的连接数，超量则等待CONNECTION_REQUEST_TIMEOUT毫秒后ConnectionPoolTimeoutException */
    private static final int MAX_CONN_TOTAL = 100;

    /** 数据包发收时间大于SOCKET_TIME_OUT秒，则抛异常，单位：毫秒 */
    private static final int SOCKET_TIME_OUT = CONNECT_TIMEOUT * 2;
    /** 同一个地址流没有关闭前，最多能同时打开几个链接，默认为2，当打开第三个链接会等待，超时则等待CONNECTION_REQUEST_TIMEOUT毫秒后ConnectionPoolTimeoutException */
    private static final int MAX_CONN_PER_ROUTE = MAX_CONN_TOTAL / 2;
    /** 链接url的超时时间，超时则抛异常，毫秒 */
    private static final int CONNECTION_REQUEST_TIMEOUT = CONNECT_TIMEOUT * 2;


    /** Http客户端 */
    private static final CloseableHttpClient httpClient;

    static {
        httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(
                        RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIME_OUT).setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT).build()
                )
                .setMaxConnTotal(MAX_CONN_TOTAL)
                .setMaxConnPerRoute(MAX_CONN_PER_ROUTE)
                .build();
    }

    static Pair<CloseableHttpResponse, CookieStore> doPost(String url, Map<String, String> mapHeaders, HttpEntity entity, Cookie... cookies) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(entity);
        return requestAfter(url, mapHeaders, entity, httpPost, cookies);
    }

    static Pair<CloseableHttpResponse, CookieStore> doGet(String url, Map<String, String> mapHeaders, Map<String, String> queryMapParams, Cookie... cookies) {
        String urlParams = mapToUrlParams(queryMapParams);
        try {
            urlParams = urlParams != null ? ("?" + URLEncoder.encode(urlParams, CharEncoding.UTF_8)) : "";
        } catch (UnsupportedEncodingException e) {
            urlParams = "?" + URLEncoder.encode(urlParams);
        }
        HttpGet httpGet = new HttpGet(url + urlParams);
        return requestAfter(url, mapHeaders, urlParams, httpGet, cookies);
    }

    /**
     * map转化url参数，如果是特殊字符会进行转义
     */
    private static String mapToUrlParams(Map<String, String> map) {
        if (map == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value == null) {
                sb.append(key).append("=").append("&");
            } else {
                sb.append(key).append("=").append(value).append("&");
            }
        }
        StringBuilder result = sb.deleteCharAt(sb.length() - 1);
        return result.toString();
    }

    /**
     * 最终请求，返回response和cookie
     * cookie避免每次请求加锁，自定义每次请求都会生成新的cookie储存器
     */
    private static Pair<CloseableHttpResponse, CookieStore> requestAfter(String url, Map<String, String> mapHeaders, Object requestData, HttpRequestBase httpMethod, Cookie... cookies) {

        // 自定义头
        if (mapHeaders != null && mapHeaders.size() > 0) {
            for (Entry<String, String> entry : mapHeaders.entrySet()) {
                httpMethod.addHeader(entry.getKey(), entry.getValue());
            }
        }

        // cookie储存器
        DefaultCookieStore cookieStore = new DefaultCookieStore();
        cookieStore.addCookies(cookies);

        long start = 0;
        try {
            log.info("*****远程{} Start*****", httpMethod.getMethod());
            log.info("url:{}", url);
            if (log.isErrorEnabled()) {
                log.debug("mapHeaders:{}", mapHeaders);
                if (requestData instanceof StringEntity) {
                    try {
                        log.debug("requestData:{}", EntityUtils.toString((HttpEntity) requestData));
                    } catch (IOException ignored) {
                    }
                } else {
                    log.debug("requestData:{}", requestData);
                }
            }

            start = System.currentTimeMillis();

            // 使用当前的cookieStore
            CloseableHttpResponse response = httpClient.execute(httpMethod, new BasicHttpContext() {
                @Override
                public Object getAttribute(String id) {
                    if (HttpClientContext.COOKIE_STORE.equals(id)) {
                        return cookieStore;
                    }
                    return super.getAttribute(id);
                }
            });

            long end = System.currentTimeMillis();
            log.info("*****远程http end,耗时：{}*****", end - start);
            return Pair.of(response, cookieStore);
        } catch (IOException e) {
            try {
                httpMethod.releaseConnection();
                log.error("http异常");
                log.error("url:{}", url);
                log.error("mapHeaders:{}", mapHeaders);
                if (requestData instanceof StringEntity) {
                    log.error("requestData:{}", EntityUtils.toString((HttpEntity) requestData));
                } else {
                    log.error("requestData:{}", requestData);
                }
                log.error("异常时间:{}", System.currentTimeMillis() - start);
            } catch (IOException ignored) {
            }
            throw new RemoteHttpException(e);
        }
    }

}