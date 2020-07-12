package com.lifeng.cases;

import com.lifeng.constants.Constants;
import com.lifeng.pojo.CaseInfo;
import com.lifeng.utils.AuthenticationUtils;
import com.lifeng.utils.ExcelUtils;
import com.lifeng.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class AuditCase extends BaseCase {
    @AfterSuite
    public void afterSuite(){
        ExcelUtils.writeBack();
    }

    @Test(dataProvider = "datas")
    public void test(CaseInfo caseInfo){
        //  1、参数化替换
        paramsReplace(caseInfo);
        //	2、数据库前置查询结果(数据断言必须在接口执行前后都查询)
        //	3、调用接口
        //从VARS获取token
        Object token = AuthenticationUtils.VARS.get("${token}");
        //新建一个map
        Map<String,String> header=new HashMap<>();
        //put token进去
        header.put("Authorization","Bearer "+token);
        //将默认的请求头一起put进去
        header.putAll(Constants.HEADERS);
        //调用封装好的call方法，传入CaseInfo对象封装好的方法，请求参数类型，请求url,请求参数，请求头，返回响应
        HttpResponse response = HttpUtils.call(caseInfo.getMethod(), caseInfo.getContentType(), caseInfo.getUrl(), caseInfo.getParams(), header);
        //调用封装好的httpResponse,打印响应头，响应状态码，返回响应体
        String body = HttpUtils.httpResponse(response);
        //	4、断言响应结果
        boolean assertResponse = assertResponse(caseInfo.getExpectedResult(), body);
        //	5、添加接口响应回写内容
        //将响应结果写入excel
        addWdbList(caseInfo.getId(), Constants.RESPONSE_CELL_NUM,body);
        //获取期望结果{$.code: ,$.msg:" "}
        //	6、数据库后置查询结果
        //	7、据库断言
        //	8、添加断言回写内容
        String assertResult =assertResponse ? "passed" : "failed";
        addWdbList(caseInfo.getId(), Constants.ASSERT_CELL_NUM,assertResult);
        //	9、添加日志
        //	10、报表断言
    }

    @DataProvider
    public Object[] datas(){
        //调用封装好的getDatas获得对应sheet的excel数据，返回一个一维数组，数组里面存的是CaseInfo对象，数据供test使用
        Object[] datas = ExcelUtils.getDatas(sheetIndex, 1, CaseInfo.class);
        return datas;
    }
}
