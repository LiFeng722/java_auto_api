package com.lifeng.utils;

import com.alibaba.fastjson.JSONPath;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 接口鉴权类
 */
public class AuthenticationUtils {
    //类似于Jmeter的用户变量，用于存储在登陆时获取的token，以及member_id
    public static final Map<String,Object> VARS=new HashMap<>();

    /**
     * 使用jsonpath获取内容，存到VARS，给其他接口使用
     * @param json  json字符串
     * @param jsonPath jsonpath表达式
     * @param key  存储到VARS中的key
     */
    public static void json2Map(String json, String jsonPath,String key){
        //如果json不是空，则继续操作(这里传进来的json是响应体)
        if (StringUtils.isNotBlank(json)){
            //使用jsonpath获取内容 （在响应体获取对应json表达式的值）
            Object read = JSONPath.read(json, jsonPath);
            //如果获取到的内容不为空
            if (read!=null){
                System.out.println(read);
                //存储到VARS中
                AuthenticationUtils.VARS.put(key,read);
            }

        }
    }
}
