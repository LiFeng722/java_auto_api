package com.lifeng.cases;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.lifeng.constants.Constants;
import com.lifeng.pojo.CaseInfo;
import com.lifeng.pojo.WriteBackData;
import com.lifeng.utils.AuthenticationUtils;
import com.lifeng.utils.ExcelUtils;
import com.lifeng.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.testng.annotations.*;

import java.util.Map;
import java.util.Set;

public class LoginCase extends BaseCase {

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
        //调用封装好的call方法，传入CaseInfo对象封装好的方法，请求参数类型，请求url,请求参数，请求头，返回响应
        HttpResponse response = HttpUtils.call(caseInfo.getMethod(), caseInfo.getContentType(), caseInfo.getUrl(), caseInfo.getParams(),Constants.HEADERS);
        //调用封装好的httpResponse,打印响应头，响应状态码，返回响应体
        String body = HttpUtils.httpResponse(response);
        //  3.1、从响应体里面获取token
        //将登录后，响应体body里面取到的token存储到VARS中
        AuthenticationUtils.json2Map(body,"$.data.token_info.token","${token}");
        //  3.2、从响应体里面获取member_id
        //将登录后取到的member_id存储到VARS中
        AuthenticationUtils.json2Map(body,"$.data.id","${member_id}");
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
