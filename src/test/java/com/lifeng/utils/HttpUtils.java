package com.lifeng.utils;

import com.alibaba.fastjson.JSONObject;
import com.lifeng.cases.BaseCase;
import com.lifeng.pojo.CaseInfo;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class HttpUtils {

    private static Logger logger=Logger.getLogger(HttpUtils.class);

    /**
     * 发送http请求
     * @param method 请求方法
     * @param contentType 请求参数类型
     * @param url 请求地址
     * @param params 请求参数
     * @param header 请求头
     * @return
     */
    //注意点：在一个方法里面 if分支有return的话，所有都要return,有一个一劳永逸的方法，就是在最后return一个null
    public static HttpResponse call(String method,String contentType,String url,String params,Map<String,String> header) {
        //如果请求方法是post
        try {
            if ("post".equalsIgnoreCase(method)) {
                //如果参数类型是json
                if ("json".equalsIgnoreCase(contentType)) {
                    return HttpUtils.jsonPost(url, params,header);
                 //如果参数类型是form
                } else if ("form".equalsIgnoreCase(contentType)) {
                    String formResult = json2KeyValue(params);
                    return HttpUtils.formPost(url, formResult);
                } else {
                    logger.info("method = " + method + ", contentType = " + contentType + ", url = " + url + ", params = " + params);
                }
            //如果请求方式是get
            } else if ("get".equalsIgnoreCase(method)) {
                //此处要注意处理url,有两种形式
                return HttpUtils.get(url,header);
            //如果请求方式是patch,patch请求方法只有json一种参数类型
            } else if ("patch".equalsIgnoreCase(method)) {
                return HttpUtils.patch(url, params,header);
            } else {
                logger.info("method = " + method + ", contentType = " + contentType + ", url = " + url + ", params = " + params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * json参数转换成form类型的参数：key=value&key=value....
     * @param jsonParams  json类型的参数
     * @return
     */
    public static String json2KeyValue(String jsonParams){
//        将json转成Map
        Map<String,String> map = JSONObject.parseObject(jsonParams, Map.class);
//        获取所有的键，为了遍历
        Set<String> keySet = map.keySet();
        //定义一个空字符串，用来接收最后的结果
        String result="";
        for (String key : keySet) {
            //获取key对应的value
            String value=map.get(key);
            //字符串的拼接
            result+=key+"="+value+"&";
        }
        //字符串的截取，去掉最后一个&,其实不去掉也不会报错，只是为了美观
        result = result.substring(0, result.length() - 1);
        //返回转换后的结果
        return result;
    }

    /**
     * 发送get请求
     *
     * @param url 必须带参数
     * @throws Exception
     */
    public static HttpResponse get(String url,Map<String,String> header) throws Exception {
        //创建一个get请求
        HttpGet get = new HttpGet(url);
        //添加请求头
//        get.addHeader("X-Lemonban-Media-Type", "lemonban.v2");
        set2Header(header, get);
        //创建一个客户端  HttpClients-是工具类，还有xxxutils也是工具类
        HttpClient client = HttpClients.createDefault();
        //客户端发送请求，返回响应头，响应体，状态码
        HttpResponse response = client.execute(get);
        //获取响应头
        httpResponse(response);
        return response;
    }



    /**
     * 发起post请求,参数类型是json
     *
     * @param url  接口地址
     * @param json 请求体
     * @param header 请求头
     * @throws Exception
     */
    public static HttpResponse jsonPost(String url, String json,Map<String,String> header) throws Exception {
        //创建一个post请求
        HttpPost post = new HttpPost(url);
        //添加请求头
//        post.addHeader("X-Lemonban-Media-Type", "lemonban.v2");
//        post.addHeader("Content-Type", "application/json");
        set2Header(header,post);
        //添加参数
        StringEntity stringEntity = new StringEntity(json, "UTF-8");
        post.setEntity(stringEntity);
        //创建一个客户端
        HttpClient client = HttpClients.createDefault();
        //用客户端请求 并返回响应头，响应体，状态码
        HttpResponse response = client.execute(post);
//        httpResponse(response);

        return response;

    }

    /**
     * 发起post请求，参数类型是form
     *
     * @param url
     * @param params   key=value形式的
     * @throws Exception
     */
    public static HttpResponse formPost(String url, String params) throws Exception {
        //创建一个post请求
        HttpPost post = new HttpPost(url);
        //添加请求头
        post.addHeader("X-Lemonban-Media-Type", "lemonban.v2");
        post.addHeader("Content-Type", "application/x-www-form-urlencoded");
        //添加参数
        StringEntity stringEntity = new StringEntity(params, "UTF-8");
        post.setEntity(stringEntity);
        //创建一个客户端
        HttpClient client = HttpClients.createDefault();
        //用客户端请求 并返回响应头，响应体，状态码
        HttpResponse response = client.execute(post);
        httpResponse(response);

        return response;

    }

    /**
     * 发起patch请求
     *
     * @param url
     * @param json
     * @throws Exception
     */
    public static HttpResponse patch(String url, String json,Map<String,String> header) throws Exception {
        //创建一个post请求
        HttpPatch patch = new HttpPatch(url);
        //添加请求头
//        patch.addHeader("X-Lemonban-Media-Type", "lemonban.v2");
//        patch.addHeader("Content-Type", "application/x-www-form-urlencoded");
        set2Header(header,patch);
        //添加参数
        StringEntity stringEntity = new StringEntity(json, "UTF-8");
        patch.setEntity(stringEntity);
        //创建一个客户端
        HttpClient client = HttpClients.createDefault();
        //用客户端请求 并返回响应头，响应体，状态码
        HttpResponse response = client.execute(patch);
//        httpResponse(response);

        return response;

    }

    /**
     * 获取响应头，响应体，状态码
     *
     * @param response
     * @throws IOException
     */
    public static String httpResponse(HttpResponse response)  {
        try {
            //获取响应头
            Header[] allHeaders = response.getAllHeaders();
            logger.info(Arrays.toString(allHeaders));
            //获取响应体
            HttpEntity entity = response.getEntity();
            String body = null;
            body = EntityUtils.toString(entity);
            logger.info(body);

            //获取响应状态码
            logger.info(response.getStatusLine().getStatusCode());
            return body;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     *
     * @param header
     * @param request
     */
    public static void set2Header(Map<String, String> header, HttpRequest request) {
        Set<String> keySet = header.keySet();
        for (String name : keySet) {
            String value=header.get(name);
            request.addHeader(name,value);
        }
    }
}
