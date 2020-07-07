package excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by jason on 20-7-2.
 * 参考地址: https://blog.csdn.net/weixin_45455623/article/details/98475787
 */


public class CreateExcelDemo {
    /**
     * 2003 excel后缀
     */
    private static final String EXCEL_XLS = "xls";

    /**
     * 2007 及以上版本excel后缀
     */
    private static final String EXCEL_XLSX = "xlsx";


    public static void createExcel(String filePath) {
        FileOutputStream out = null;
        try {
            // 创建工作薄
            Workbook wb = null;

            if (StringUtils.substringAfterLast(filePath, ".").equals(EXCEL_XLS)) {
                wb = new HSSFWorkbook();
            }
            if (StringUtils.substringAfterLast(filePath, ".").equals(EXCEL_XLSX)) {
                wb = new XSSFWorkbook();
            }
            if (wb == null) {
                System.out.println("文件后缀不规范，无法操作excel文件，后缀示例：" + EXCEL_XLS + ", " + EXCEL_XLSX);
                return;
            }
            out = new FileOutputStream(filePath);
            // 创建工作表
            wb.createSheet();
            // 设置工作表名
            wb.setSheetName(0, "Sheet1");
            wb.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        createExcel("/home/jason/Desktop/car2/data2.xls");
    }

}

