package com.lifeng.cases;

import com.alibaba.fastjson.JSONPath;
import com.lifeng.constants.Constants;
import com.lifeng.pojo.CaseInfo;
import com.lifeng.pojo.WriteBackData;
import com.lifeng.utils.AuthenticationUtils;
import com.lifeng.utils.ExcelUtils;
import com.lifeng.utils.HttpUtils;
import com.lifeng.utils.SQLUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Recharge extends BaseCase {

    @Test(dataProvider = "datas")
    public void test(CaseInfo caseInfo){
        //  1、参数化替换
        paramsReplace(caseInfo);
        //	2、数据库前置查询结果(数据断言必须在接口执行前后都查询)
        Object beforeQueryResult = SQLUtils.getSingleResult(caseInfo.getSql());
        //	3、调用接口
        //从VARS获取token
        Object token = AuthenticationUtils.VARS.get("${token}");
        //新建一个map
        Map<String,String> header=new HashMap<>();
        //put token进去
        header.put("Authorization","Bearer "+token);
        //将默认的请求头一起put进去
        header.putAll(Constants.HEADERS);
        HttpResponse response = HttpUtils.call(caseInfo.getMethod(), caseInfo.getContentType(), caseInfo.getUrl(), caseInfo.getParams(), header);
        String body = HttpUtils.httpResponse(response);
        //	4、断言响应结果
        boolean assertResponse = assertResponse(caseInfo.getExpectedResult(), body);
        //	5、添加接口响应回写内容
        //将响应结果写入excel
        addWdbList(caseInfo.getId(),Constants.RESPONSE_CELL_NUM, body);
        //	6、数据库后置查询结果
        Object afterQueryResult = SQLUtils.getSingleResult(caseInfo.getSql());
        //	7、数据库断言
        boolean sqlAssert = sqlAssert(caseInfo, beforeQueryResult, afterQueryResult);
        //	8、添加断言回写内容（按照需求想加就加）
        String assertResult =assertResponse && sqlAssert ? "passed" : "failed";
        addWdbList(caseInfo.getId(), Constants.ASSERT_CELL_NUM,assertResult);
        //	9、添加日志
        //	10、报表断言
    }



    /**
     * 数据库断言
     * @param caseInfo
     * @param beforeQueryResult
     * @param afterQueryResult
     */
    public boolean sqlAssert(CaseInfo caseInfo, Object beforeQueryResult, Object afterQueryResult) {
        boolean flag=false;
        if(StringUtils.isNotBlank(caseInfo.getSql())) {
            if (beforeQueryResult == null || afterQueryResult == null) {
                System.out.println("sql为空");
            } else {
                //强转成BigDecimal
                BigDecimal b1 = (BigDecimal) beforeQueryResult;
                BigDecimal b2 = (BigDecimal) afterQueryResult;
                //充值后-充值前的结果
                BigDecimal result = b2.subtract(b1);
                //获取充值金额
                Object read = JSONPath.read(caseInfo.getParams(), "$.amount");
                //强转成BigDecimal
                BigDecimal b3=new BigDecimal(read.toString());
                //判断充值后-充值前的金额，是否和充值金额相等
                if (b3.compareTo(result) == 0) {
                    System.out.println("数据库断言成功");
                    flag=true;
                } else {
                    System.out.println("数据库断言失败");
                }
            }
        }else {
            System.out.println("sql为空，不需要数据库断言");
        }
        return flag;
    }

    @DataProvider
    public Object[] datas(){
        Object[] datas = ExcelUtils.getDatas(sheetIndex, 1, CaseInfo.class);
        return datas;
    }
}
