package com.scm.utils;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExcelUtils {
    private final static String excel2003L =".xls";    //2003- 版本的excel
    private final static String excel2007U =".xlsx";   //2007+ 版本的excel

    /**
     * 描述：获取IO流中的数据，组装成List<List<Object>>对象
     * @param in,fileName
     * @return
     * @throws
     */
    public  List<List<Object>> getBankListByExcel(InputStream in, String fileName) throws Exception{
        List<List<Object>> list = null;

        //创建Excel工作薄
        Workbook work = this.getWorkbook(in,fileName);
        if(null == work){
            throw new Exception("创建Excel工作薄为空！");
        }
        Sheet sheet = null;  //页数
        Row row = null;  //行数
        Cell cell = null;  //列数

        list = new ArrayList<List<Object>>();
        //遍历Excel中所有的sheet
        // 将最大的列数记录下来
        int lastCellNum = 0;
        for (int i = 0; i < work.getNumberOfSheets(); i++) {
            System.out.println("work.getNumberOfSheets()->"+work.getNumberOfSheets());
            sheet = work.getSheetAt(i);
            if(sheet==null){continue;}


            //遍历当前sheet中的所有行
            for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                row = sheet.getRow(j);

                //遍历所有的列
                List<Object> li = new ArrayList<Object>();
                // 比较当前行的列数跟表的最大的列数
                if (j == sheet.getFirstRowNum()) {
                    // 将第一行的列数设为最大
                    lastCellNum = row.getLastCellNum();
//                    System.out.println("lastCellNum--->"+lastCellNum);
                }else {
                    lastCellNum = lastCellNum > row.getLastCellNum() ? lastCellNum : row.getLastCellNum();
                }

            }
            //遍历当前sheet中的所有行
            for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                row = sheet.getRow(j);
                if(row==null||row.getFirstCellNum()==j){continue;}

                //遍历所有的列
                List<Object> li = new ArrayList<Object>();
                // 比较当前行的列数跟表的最大的列数

                for (int y = row.getFirstCellNum(); y < lastCellNum; y++) {
                    cell = row.getCell(y);
                    li.add(this.getValue(cell).trim());
                }
                list.add(li);
            }
        }

        return list;

    }

    /**
     * 描述：根据文件后缀，自适应上传文件的版本
     * @param inStr,fileName
     * @return
     * @throws Exception
     */
    public  Workbook getWorkbook(InputStream inStr,String fileName) throws Exception{
        Workbook wb = null;
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        if(excel2003L.equals(fileType)){
            wb = new HSSFWorkbook(inStr);  //2003-
        }else if(excel2007U.equals(fileType)){
            wb = new XSSFWorkbook(inStr);  //2007+
        }else{
            throw new Exception("解析的文件格式有误！");
        }
        return wb;
    }

    /**
     * 描述：对表格中数值进行格式化
     * @param cell
     * @return
     */
    //解决excel类型问题，获得数值
    public  String getValue(Cell cell) {
        String value = "";
        if(null==cell){
            return value;
        }
        switch (cell.getCellType()) {
            //数值型
            case NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    //如果是date类型则 ，获取该cell的date值
                    Date date = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
                    // 根据自己的实际情况，excel表中的时间格式是yyyy-MM-dd HH:mm:ss还是yyyy-MM-dd，或者其他类型
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    // 由于方法的返回值类型为String，这里将Date类型转为String，便于统一返回数据
                    value = format.format(date);;
                }else {// 纯数字
                    BigDecimal big=new BigDecimal(cell.getNumericCellValue());
                    value = big.toString();
                    //解决1234.0  去掉后面的.0
                    if(null!=value&&!"".equals(value.trim())){
                        String[] item = value.split("[.]");
                        if(1<item.length&&"0".equals(item[1])){
                            value=item[0];
                        }
                    }
                }
                break;
            //字符串类型
            case STRING:
                value = cell.getStringCellValue().toString();
                break;
            // 公式类型
            case FORMULA:
                //读公式计算值
                value = String.valueOf(cell.getNumericCellValue());
                if (value.equals("NaN")) {// 如果获取的数据值为非法值,则转换为获取字符串
                    value = cell.getStringCellValue().toString();
                }
                break;
            // 布尔类型
            case BOOLEAN:
                value = " "+ cell.getBooleanCellValue();
                break;
            default:
                value = cell.getStringCellValue().toString();
        }
        if("null".endsWith(value.trim())){
            value="";
        }
        return value;
    }
}
