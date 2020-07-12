package com.lifeng.cases;

import com.lifeng.constants.Constants;
import com.lifeng.pojo.CaseInfo;
import com.lifeng.pojo.WriteBackData;
import com.lifeng.utils.ExcelUtils;
import com.lifeng.utils.HttpUtils;
import com.lifeng.utils.SQLUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.List;

public class Register extends BaseCase {

    @Test(dataProvider = "datas")
    public void test(CaseInfo caseInfo)  {
        //  1、参数化替换
        paramsReplace(caseInfo);
        //	2、数据库前置查询结果(数据断言必须在接口执行前后都查询)
        Object beforeQueryResult = SQLUtils.getSingleResult(caseInfo.getSql());
        //	3、调用接口
        HttpResponse httpResponse = HttpUtils.call(caseInfo.getMethod(),caseInfo.getContentType(),caseInfo.getUrl(),caseInfo.getParams(), Constants.HEADERS);
        String body = HttpUtils.httpResponse(httpResponse);
        //	4、断言响应结果
        boolean assertResponse = assertResponse(caseInfo.getExpectedResult(), body);
        //	5、添加接口响应回写内容
        //将响应结果写入excel
        addWdbList(caseInfo.getId(), Constants.RESPONSE_CELL_NUM,body);
        //获取期望结果{$.code: ,$.msg:" "}
        //	6、数据库后置查询结果
        Object afterQueryResult = SQLUtils.getSingleResult(caseInfo.getSql());
        //	7、数据库断言
        boolean sqlAssert = sqlAssert(caseInfo,beforeQueryResult, afterQueryResult);
        //	8、添加断言回写内容
        String assertResult =assertResponse && sqlAssert ? "passed" : "failed";
        addWdbList(caseInfo.getId(), Constants.ASSERT_CELL_NUM,assertResult);
        //	9、添加日志
        //	10、报表断言
    }

    /**
     * 数据库断言
     * @param beforeQueryResult  sql前置查询结果
     * @param afterQueryResult   sql后置查询结果
     * @return
     */
    public boolean sqlAssert(CaseInfo caseInfo,Object beforeQueryResult, Object afterQueryResult) {
        boolean flag=false;
        if (StringUtils.isNotBlank(caseInfo.getSql())) {
            if (beforeQueryResult == null || afterQueryResult == null) {
                System.out.println("数据库断言失败");
            } else {
                long l1 = (long) beforeQueryResult;//可以用.getclass()方法查看返回的Object结果集到底是什么类型数据
                long l2 = (long) afterQueryResult;
                if (l1 == 0 && l2 == 1) {
                    System.out.println("数据库断言成功");
                    flag = true;
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
    public Object[] datas() {
        Object[] datas = ExcelUtils.getDatas(sheetIndex, 1, CaseInfo.class);
        return datas;

    }
}
