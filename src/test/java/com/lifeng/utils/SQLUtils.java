package com.lifeng.utils;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.Map;

public class SQLUtils {
    public static void main(String[] args) {
//  DBUtils
        getSingleResult("SELECT COUNT(*) FROM member a WHERE a.mobile_phone='15580913094'");
    }

    /**
     * 查询数据库单行单列结果集
     * @param sql   sql语句
     * @return      查询结果
     */
    public static Object getSingleResult(String sql) {
        if(StringUtils.isBlank(sql)){
            System.out.println("sql为空");
            return null;
        }
        Object query=null;
        //创建QueryRunner对象(QueryRunner中提供对sql语句操作的API.)
        QueryRunner runner=new QueryRunner();
        //获取数据库连接
        Connection connection= JDBCUtils.getConnection();

        try {
            //创建结果集对象
            ScalarHandler scalarHandler=new ScalarHandler();
            //执行查询语句
            query = runner.query(connection, sql, scalarHandler);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            JDBCUtils.close(connection);
        }
        return query;
    }

    public static void mapHandler() {
        QueryRunner runner=new QueryRunner();
        Connection connection = JDBCUtils.getConnection();
        try {
            String sql="SELECT * FROM member a WHERE a.mobile_phone='15580913094'";
            MapHandler handler=new MapHandler();
            Map<String, Object> query = runner.query(connection, sql, handler);
            System.out.println(query);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            JDBCUtils.close(connection);
        }
    }
}
