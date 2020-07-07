package excel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jason on 20-7-2.
 * 参考地址: https://blog.csdn.net/weixin_45455623/article/details/98475787
 */
public class WriteExcelDemo {


    public static void main(String args[]) {
        List<Object> rowList = new ArrayList<Object>();

        for (int i = 1; i <= 10; i++) {
            List<String> cellList = new ArrayList<String>();
            for (int j = 1; j <= 5; j++) {
                cellList.add(i + "行" + j + "列");
            }

            rowList.add(cellList);
        }

        String outFile = "/home/jason/Desktop/car2/data2.xls";

        // 创建输出excel表格
        CreateExcelDemo.createExcel(outFile);

        // 写入数据
        writeExcel(rowList, outFile);
        // 删除数据
//        deleteMessage(2, 5, "E:\\123.xls");
//        deleteMessage(6, 9, "E:\\123.xlsx");
    }

    /**
     * 功能描述:
     * <写入Excel>
     *
     * @param rowList  1
     * @param filePath 2
     * @return void
     */
    public static void writeExcel(List<Object> rowList, String filePath) {
        OutputStream out = null;
        try {
            File file = new File(filePath);
            Workbook workbook = getWorkbook(file);

            // 取第一个Sheet工作表
            Sheet sheet = workbook.getSheetAt(0);

            // 设置单元格属性
            CellStyle cellStyle = workbook.createCellStyle();

            // 水平居左
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            // 垂直居中
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);


            // 颜色样式
            CellStyle colorStyle = workbook.createCellStyle();
            // 文字属性
            Font font;
            font = workbook.createFont();
            font.setColor(IndexedColors.RED.getIndex());
            colorStyle.setFont(font);

            colorStyle.setAlignment(HorizontalAlignment.LEFT);
            colorStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // 加粗样式
            /*
            CellStyle boldStyle = workbook.createCellStyle();
            Font font2;
            font2 = workbook.createFont();
            font2.setBold(true);
            boldStyle.setFont(font2);
            boldStyle.setAlignment(HorizontalAlignment.CENTER);
            boldStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            */

            // 写入数据
            for (int rowNum = 0; rowNum < rowList.size(); rowNum++) {
                // 创建行
                Row row = sheet.createRow(rowNum);

                List<String> cellList = (ArrayList<String>) rowList.get(rowNum);

                for (int columnNum = 0; columnNum < cellList.size(); columnNum++) {
                    // 创建列
                    Cell cell = row.createCell(columnNum);
                    cell.setCellValue(cellList.get(columnNum));

                    if (columnNum == 0) {
                        cell.setCellStyle(cellStyle);
                    } else {
                        cell.setCellStyle(colorStyle);
                    }
                }
            }
            // 第一列列宽为10
            sheet.setColumnWidth(0, (int) ((20 + 0.72) * 256));
            out = new FileOutputStream(filePath);
            workbook.write(out);
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

    /**
     * 功能描述:
     * <删除数据>
     *
     * @param rowMin   1 需从第几行开始删除（非下标）
     * @param rowMax   2 需从第几行结束删除
     * @param filePath 3
     * @return void
     */
    public static void deleteMessage(int rowMin, int rowMax, String filePath) {
        OutputStream out = null;
        try {
            File file = new File(filePath);
            Workbook workbook = getWorkbook(file);
            // 取第一个Sheet工作表
            Sheet sheet = workbook.getSheetAt(0);
            // 原始数据总行数
            int rowNum = sheet.getLastRowNum() + 1;
            System.out.println("原始数据总行数:" + rowNum);
            if (rowNum < rowMax) {
                rowMax = rowNum;
            }
            if (rowNum < rowMin) {
                System.out.println("数据量不够");
                return;
            }
            // 删除数据
            for (int i = rowMin; i <= rowMax; i++) {
                Row row = sheet.getRow(i - 1);
                if (row != null) {
                    sheet.removeRow(row);
                }
            }
            out = new FileOutputStream(filePath);
            workbook.write(out);
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

    /**
     * 功能描述:
     * <根据Excel的版本获取Workbook>
     *
     * @param file 1
     * @return org.apache.poi.ss.usermodel.Workbook
     */
    public static Workbook getWorkbook(File file) throws IOException, InvalidFormatException {
        FileInputStream in = new FileInputStream(file);
        return WorkbookFactory.create(in);
    }


}
