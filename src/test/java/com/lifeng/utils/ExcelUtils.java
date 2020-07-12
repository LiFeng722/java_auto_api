package com.lifeng.utils;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.lifeng.constants.Constants;
import com.lifeng.pojo.CaseInfo;
import com.lifeng.pojo.WriteBackData;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ExcelUtils {

    public static List<WriteBackData> wdbList=new LinkedList<>();
    /**
     * 读取Excel并返回映射关系集合。
     * @param sheetIndex  sheet开始位置
     * @param sheetNum  sheet个数
     * @param classs   映射关系字节码
     * @return
     * @throws Exception
     */
    public static List read(int sheetIndex,int sheetNum,Class classs) throws Exception {

        FileInputStream fis=new FileInputStream(Constants.EXCEL_PATH);

        //以下是easypoi的使用
        //创建导入参数对象
        ImportParams params=new ImportParams();
        //从第几个sheet开始读取
        params.setStartSheetIndex(sheetIndex);
        //读取几个sheet
        params.setSheetNum(sheetNum);

        //Excel导入工具类，其中三个参数的含义是：1.文件流，2.映射关系字节码对象，3.导入参数
        List list = ExcelImportUtil.importExcel(fis, classs, params);
        return list;

    }
    /**
     * 将读取到的Excel进一步处理成一维数组，
     * 供testng作为数据驱动
     * @param sheetIndex
     * @param sheetNum
     * @param classs
     * @return
     */
    public static Object[] getDatas(int sheetIndex,int sheetNum,Class classs){
        try {
            List list = read(sheetIndex, sheetNum, classs);
            Object[] datas = list.toArray();
            return datas;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeBack(){

        FileInputStream fis= null;
        try {
            fis = new FileInputStream(Constants.EXCEL_PATH);
            try {
                Workbook excle = WorkbookFactory.create(fis);
                for (WriteBackData writeBackData : wdbList) {
                    //获取回写的列
                    int cellNum = writeBackData.getCellNum();
                    //获取回写的内容
                    String content = writeBackData.getContent();
                    //获取回写的行
                    int rowNum = writeBackData.getRowNum();
                    //获取回写的表
                    int sheetIndex = writeBackData.getSheetIndex();
                    Sheet sheet = excle.getSheetAt(sheetIndex);
                    Row row = sheet.getRow(rowNum);
                    Cell cell = row.getCell(cellNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(content);
                }
                FileOutputStream fos=new FileOutputStream(Constants.EXCEL_PATH);
                excle.write(fos);
                fis.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}
