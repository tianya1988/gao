
package excel;


import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;


public class ReadExcelDemo {

    public static void main(String[] args) {
        readExcel("/home/jason/Desktop/car2/1.xls");
    }

    public static void readExcel(String path) {
        File file = new File(path);
        FileInputStream fileInputStream = null;
        Workbook workBook = null;

        if (file.exists()) {
            try {
                fileInputStream = new FileInputStream(file);
                workBook = WorkbookFactory.create(fileInputStream);

                int numberOfSheets = workBook.getNumberOfSheets();
                System.out.println("number of sheets is : " + numberOfSheets);

                // sheet0工作表
                for (int s = 0; s < 1; s++) {
                    Sheet sheetAt = workBook.getSheetAt(s);
                    //获取工作表名称
                    String sheetName = sheetAt.getSheetName();
                    System.out.println("工作表名称：" + sheetName);
                    // 获取当前Sheet的总行数
                    int rowsOfSheet = sheetAt.getPhysicalNumberOfRows();
                    System.out.println("当前表格的总行数:" + rowsOfSheet);

                    int beginRowNum = 0;

                    //跳过第一行(此为合并行);
                    for (int i = 1; i < rowsOfSheet; i++) {
                        Row row = sheetAt.getRow(i);
                        if ("编号".equals(row.getCell(0).getStringCellValue()) && "时间".equals(row.getCell(1).getStringCellValue())) {
                            beginRowNum = i;
                        }
                    }
                    System.out.println("beginRowNum is : " + beginRowNum);

                    // 计算 beginRow 一共有几列
                    Row beginRow = sheetAt.getRow(beginRowNum);
                    int physicalNumberOfCells = sheetAt.getRow(beginRowNum).getPhysicalNumberOfCells();
                    System.out.println("physicalNumberOfCells is : " + physicalNumberOfCells);

                    // 统计列名  :  编号  时间  温度°C
                    String[] title = new String[physicalNumberOfCells];
                    for (int i = 0; i < physicalNumberOfCells; i++) {
                        title[i] = beginRow.getCell(i).getStringCellValue();
                    }

                    for (int r = (beginRowNum + 1); r < rowsOfSheet; r++) {
                        Row row = sheetAt.getRow(r);
                        if (row == null) {
                            continue;
                        } else {
                            int rowNum = row.getRowNum() + 1;
                            System.out.println("当前行:" + rowNum);

                            for (int columnNum = 0; columnNum < physicalNumberOfCells; columnNum++) {
                                Cell cell = row.getCell(columnNum);
                                if ((cell.getCellTypeEnum() == CellType.STRING)) {
                                    String cellValue = cell.getStringCellValue();
                                    System.out.println(title[columnNum] + " : " + cellValue);
                                } else {
                                    System.out.println("第" + rowNum + "行，第" + (columnNum + 1) + "列[" + title[columnNum] + "]数据错误！");
                                }
                            }

                            /*
                            // 总列(格)
                            Cell cell0 = row.getCell(0);
                            Cell cell1 = row.getCell(1);
                            Cell cell2 = row.getCell(2);


                            if ((cell0.getCellTypeEnum() == CellType.STRING)) {
                                String numericCellValue = cell0.getStringCellValue();
                                System.out.println(numericCellValue);
                            } else {
                                System.out.println("第" + rowNum + "行，第一列[" + title[0] + "]数据错误！");
                            }
                            if (cell1.getCellTypeEnum() == CellType.STRING) {
                                String stringCellValue = cell1.getStringCellValue();
                                System.out.println(stringCellValue);
                            } else {
                                System.out.println("第" + rowNum + "行，第二列[" + title[1] + "]数据错误！");
                            }
                            if (cell2.getCellTypeEnum() == CellType.STRING) {
                                String stringCellValue = cell2.getStringCellValue();
                                System.out.println(stringCellValue);
                            } else {
                                System.out.println("第" + rowNum + "行，第三列[" + title[2] + "]数据错误！");
                            }
                            */

                            System.out.println("=============");
                        }
                    }
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("文件不存在!");
        }
    }
}

