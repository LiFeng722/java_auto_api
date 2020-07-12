package com.lifeng.constants;

import com.lifeng.utils.ExcelUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 常量类  final修饰的字段（主要是放不常改变的字段，被整个项目共享的）
 * psfs快捷键
 * 常量的变量名都是英文字母大写，用下划线分隔
 */
public class Constants {

//    public static final String EXCEL_PATH= Constants.class.getClassLoader().getResource("./cases_v3.xlsx").getPath();
    public static final String EXCEL_PATH="F:\\IdeaProjects\\java19_api\\java19_api_auto_v8\\src\\test\\resources\\cases_v3.xlsx";
    //存储项目需要的默认请求头（X-Lemonban-Media-Type和Content-Type）
    public static final Map<String,String> HEADERS=new HashMap<>();
    //默认写回的列（写回响应）
    public static final int RESPONSE_CELL_NUM=8;
    //默认写回断言的列
    public static final int ASSERT_CELL_NUM=10;

    //数据库连接URL  是固定写法
    public static final String JDBC_URL="jdbc:mysql://api.lemonban.com:3306/futureloan?useUnicode=true&characterEncoding=utf-8";
    //数据库连接用户名
    public static final String JDBC_USERNAME="future";
    //数据库连接密码
    public static final String JDBC_PASSWORD="123456";

}
