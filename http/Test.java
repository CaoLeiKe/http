package com.zd.core.http;

import com.zd.core.http.pojo.juhe.JuheVO;
import com.zd.utils.CommonUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 爬虫，登录武夷山网站
 */
public class Test {

    // 登录页面
    private static String logPage = "http://sso.wyschina.com/login.jsp";

    // 获取验证码接口
    private static String captchaUrl = "http://sso.wyschina.com/createimage";

    // 登录页面，用来请求验证码
    private static String logUrl = "http://sso.wyschina.com/SSOAuth";

    // 登录携带的数据
    private static String name = "clk5858";             //账号
    private static String passowrd = "caoleike0916";    //密码


    /**
     * 1: 请求登录页面，获取验证码，cookie
     * 2: 打码--获取验证码的字符串
     * 3: 携带验证码及其cookie进行登录
     */
    public static void main(String[] args) throws IOException {
        // 请求登录页面
        HttpUtils.sendGet(logPage, "url=http%3A%2F%2Fw.wyschina.com%2Fcustom%2Flogin.action%3Faction%3Dhttp%253A%252F%252Fwww.wyschina.com%252F");
        // 再次请求验证码
        String base64String = HttpUtils.sendGetReBase64(captchaUrl, "Rgb=255|0|0&r=4267");

        // 请求打码工具
        Map<String, String> juheParamMap = new HashMap<>();
        juheParamMap.put("key", "c8a905de25c8df8ae8f5c3758fe00168");
        juheParamMap.put("codeType", "4004");
        juheParamMap.put("base64Str", base64String);
        JuheVO vo = HttpUtils.sendPost("http://op.juhe.cn/vercode/index", juheParamMap, false, JuheVO.class);

        // 进行登录
        Map<String, String> logParamMap = new HashMap<>();
        logParamMap.put("username", name);
        logParamMap.put("password", passowrd);
        logParamMap.put("random", vo.getResult());
        Pair<CloseableHttpResponse, HttpRequestBase> logResult = HttpUtils.sendPostReAll(logUrl, logParamMap, false);
        CloseableHttpResponse response = logResult.getKey();

        try {
            CommonUtils.httpIs200(response.getStatusLine());
            System.out.println("登录成功！");
        } catch (Exception e) {
            System.out.println("登录失败！");
        }
    }
}