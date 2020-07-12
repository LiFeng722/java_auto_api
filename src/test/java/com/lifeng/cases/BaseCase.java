package com.lifeng.cases;

import cn.binarywang.tools.generator.*;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.lifeng.constants.Constants;
import com.lifeng.pojo.CaseInfo;
import com.lifeng.pojo.WriteBackData;
import com.lifeng.utils.AuthenticationUtils;
import com.lifeng.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 用例父类
 */
public class BaseCase {

    public static void main(String[] args) {
        String mobileNumber = ChineseMobileNumberGenerator.getInstance().generate();
        String iDCardNumber = ChineseIDCardNumberGenerator.getInstance().generate();
        System.out.println("mobileNumber = " + mobileNumber);
        System.out.println("iDCardNumber = " + iDCardNumber);

    }
    //读取testng.xml sheetIndex参数，sheetIndex是sheet的索引
    public int sheetIndex;

    private static Logger logger = Logger.getLogger(BaseCase.class);

    //在所有测试套件开始之前，设定好默认的请求头
    @BeforeSuite
    public void beforeSuite() throws Exception {
        logger.info("=========init==========");
        Constants.HEADERS.put("X-Lemonban-Media-Type","lemonban.v2");
        Constants.HEADERS.put("Content-Type","application/json");
        //创建Properties对象
        Properties prop =new Properties();
        //获取params.properties文件的路径
        String path=BaseCase.class.getClassLoader().getResource("./params.properties").getPath();
        //创建输入流对象
        FileInputStream fis=new FileInputStream(path);
        //从输入流对象中读取键值对
        prop.load(fis);
        //将读取后的内容存储到VARS中（putAll只能存Map对象的内容，所以要强转一下）
        AuthenticationUtils.VARS.putAll((Map)prop);
        //关流
        fis.close();
    }

    //这此类运行之前，从testng.xml中获取到对应的sheetIndex
    @BeforeClass
    @Parameters({"sheetIndex"})
    public void beforeClass(int sheetIndex){
        this.sheetIndex=sheetIndex;
    }

    /**
     * 将需要写回的内容，以WriteBackData对象add到wdbList中
     * @param rowNum 需要写回的行号，这个就和用例编号对应
     * @param body 需要写回的内容，即响应体
     */
    public void addWdbList(int rowNum, int cellNum,String body) {
        //创建WriteBackData对象，存储sheet，row，cell，以及回写内容
        WriteBackData wbd=new WriteBackData(sheetIndex,rowNum, cellNum,body);
        //将WriteBackData对象add到List集合中，以便循环写入excel
        ExcelUtils.wdbList.add(wbd);
    }

    /**
     * 接口响应断言
     * @param expecteResult excel中响应期望值
     * @param body  接口响应体
     * @return  断言结果，true or false
     */
    public boolean assertResponse(String expecteResult,String body) {
        //将获取到的期望结果转成map
        Map<String ,Object> map = JSONObject.parseObject(expecteResult, Map.class);
        //获取所有key的集合
        Set<String> keySet = map.keySet();
        //定义一个断言响应结果的布尔变量
        boolean assertResponseFlag=true;
        //循环获得期望值，并与实际结果对比
        for (String key : keySet) {
            //获得期望结果
            Object expectValue=map.get(key);
            //获得实际结果
            Object actualValue = JSONPath.read(body, key);
            //判断期望值与实际结果是否相等，否则终止循环对比
            if (expectValue.equals(actualValue)){
                continue;
            }else {
                assertResponseFlag=false;
                break;
            }
        }
        System.out.println("响应断言结果为："+assertResponseFlag);
        return assertResponseFlag;
    }

    /**
     * 参数化替换
     * @param caseInfo  CaseInfo对象
     */
    public void paramsReplace(CaseInfo caseInfo) {
        //获取VARS里面所有的key（${.....}）
        Set<String> keySet = AuthenticationUtils.VARS.keySet();
        //循环key，
        for (String key : keySet) {
            //获得value(实际值)
            String value = AuthenticationUtils.VARS.get(key).toString();
            //判断不为空时，才替换
            if (StringUtils.isNotBlank(caseInfo.getSql())) {
                //将sql里面的占位符替换成实际值
                String sql = caseInfo.getSql().replace(key, value);
                //将上面得到的新的sql重新设置回caseInfo中
                caseInfo.setSql(sql);
            }
            //判断不为空时，才替换
//            if (StringUtils.isNotBlank(caseInfo.getExpectedResult())){
//                //将期望值expectedResult里面的占位符替换成实际值
//                String expectedResult = caseInfo.getExpectedResult().replace(key, value);
//                caseInfo.setExpectedResult(expectedResult);
//            }
            //判断不为空时，才替换
            if (StringUtils.isNotBlank(caseInfo.getParams())){
                ////将参数params里面的占位符替换成实际值
                String params = caseInfo.getParams().replace(key, value);
                //将上面得到的新的params重新设置回caseInfo中
                caseInfo.setParams(params);
            }
        }
    }
}
