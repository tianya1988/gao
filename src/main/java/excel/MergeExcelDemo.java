package excel;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by jason on 20-7-3.
 */
public class MergeExcelDemo {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {

//        args[0] = "C:\\Users\\zhang\\Desktop\\temp";
//        args[1] = "2021-11-29_11:41:29";
//        args[2] = "2021-11-29_16:10:29";
        if (args.length != 3) {
            System.out.println("参数个数错误");
            System.out.println("参数格式 : 目录 开始时间  结束时间");
            System.out.println("注意时间格式为 : yyyy-MM-dd_HH:mm:ss, 比如2020-07-06_01:01:00");
            return;
        }


        if (args[1].length() != 19 || !args[1].contains("_")) {
            System.out.println("开始时间格式错误");
            return;
        }

        if (args[2].length() != 19 || !args[2].contains("_")) {
            System.out.println("结束时间格式错误");
            return;
        }

        String dir = args[0];
        String startTimeStr = args[1];
        String endTimeStr = args[2];

/*        String dir = "/home/jason/Desktop/shuju2";
        String startTimeStr = "2020-06-11_11:50:04";
        String endTimeStr = "2020-06-12_11:50:04";*/

        HashMap<String, HashMap<Long, Float>> statisticMap = new HashMap<String, HashMap<Long, Float>>();
        File fileDir = new File(dir);

        String[] fileList = fileDir.list();

        int[] fileListOrder = new int[fileList.length];
        ;
        if (fileDir.isDirectory()) {
            fileListOrder = new int[fileList.length];

            for (int i = 0; i < fileList.length; i++) {
//                System.out.println(fileList[i]);
                String[] filePartArray = fileList[i].split("\\.");
                try {
                    // 多次运行的时候,会有data.xls,处理此文件时报错,数组对应位置出错时,初始化为0
                    fileListOrder[i] = (Integer.parseInt(filePartArray[0]));
                } catch (NumberFormatException e) {
                    System.out.println("********");
                    System.out.println(e.getMessage());
                    System.out.println("********");
                }

            }

            Arrays.sort(fileListOrder);

            // 按序加载数据
            for (int fileNamePart : fileListOrder) {
                File file = new File(dir + "/" + fileNamePart + ".xls");
                System.out.println("======" + file.getAbsoluteFile() + "=======");
                readExcel(file.getAbsolutePath(), statisticMap);

            }
            System.out.println();
            System.out.println("******************");
            System.out.println("共处理" + statisticMap.size() + "个表格");
            System.out.println("******************");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

        startTimeStr = startTimeStr.substring(0, startTimeStr.length() - 2);
        startTimeStr = startTimeStr + "00";

        endTimeStr = endTimeStr.substring(0, endTimeStr.length() - 2);
        endTimeStr = endTimeStr + "00";


        Date startTime = null;
        Date endTime = null;
        try {
            startTime = sdf.parse(startTimeStr);
            endTime = sdf.parse(endTimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long startTimeLong = startTime.getTime();
        long endTimeLong = endTime.getTime();



        List<Object> rowList = new ArrayList<Object>();
        for (long start = startTimeLong; start <= endTimeLong; ) {

            List<String> cellList = new ArrayList<String>();
            cellList.add(simpleDateFormat.format(start));

            for (int i : fileListOrder) {
                // win系统和linux系统 map中国的key对目录分隔符"/" "\"是严格区分的
//                String filePath = dir + "/" + i + ".xls";

                //win系统目录分隔符"\"
                String filePath = dir + "\\" + i + ".xls";
                HashMap<Long, Float> fileHashMap = statisticMap.get(filePath);

                if (fileHashMap.containsKey(start)) {
                    Float temperatureValue = fileHashMap.get(start);
                    cellList.add(String.valueOf(temperatureValue));
                    continue;

                } else {
                    cellList.add("0");
                    continue;
                }

            }

            rowList.add(cellList);

            // 步长1分钟
            start = start + (1000 * 60);

        }

        String outPath = dir + "/data.xls";
        CreateExcelDemo.createExcel(outPath);

        WriteExcelDemo.writeExcel(rowList, outPath);

    }


    private static void readExcel(String path, HashMap<String, HashMap<Long, Float>> statisticMap) {

        HashMap<Long, Float> fileMap = new HashMap<Long, Float>(1000);

        File file = new File(path);
        FileInputStream fileInputStream = null;
        Workbook workBook = null;

        if (file.exists()) {
            try {
                fileInputStream = new FileInputStream(file);
                workBook = WorkbookFactory.create(fileInputStream);

                int numberOfSheets = workBook.getNumberOfSheets();
//                System.out.println("number of sheets is : " + numberOfSheets);

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
                        if (row == null) {
                            continue;
                        }
                        // 有效行（有温度数字的行） 列数=3
                        if (row.getPhysicalNumberOfCells() <= 2) {
                            continue;
                        }
                        if ((row.getCell(0).getCellType() == CellType.STRING) && (row.getCell(1).getCellType() == CellType.STRING) && "序号".equals(row.getCell(0).getStringCellValue()) && "时间".equals(row.getCell(1).getStringCellValue())) {
                            beginRowNum = i;
                        }
                    }
                    System.out.println("beginRowNum is : " + beginRowNum);

                    // 计算 beginRow 一共有几列
                    Row beginRow = sheetAt.getRow(beginRowNum);
                    int physicalNumberOfCells = sheetAt.getRow(beginRowNum).getPhysicalNumberOfCells();
//                    System.out.println("physicalNumberOfCells is : " + physicalNumberOfCells);

                    // 统计列名  :  序号  时间  温度°C
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
//                            System.out.println("当前行:" + rowNum);

                            // 第一列是序号,跳过
                            /*
                            for (int columnNum = 1; columnNum < physicalNumberOfCells; columnNum++) {
                                Cell cell = row.getCell(columnNum);
                                if ((cell.getCellTypeEnum() == CellType.STRING)) {
                                    String cellValue = cell.getStringCellValue();
                                } else {
                                    System.out.println("第" + rowNum + "行，第" + (columnNum + 1) + "列[" + title[columnNum] + "]数据错误！");
                                }
                            }*/


                            String timeValue = "";
                            String temperatureValue = "";
                            Cell cell1 = row.getCell(1);
                            if ((cell1.getCellType() == CellType.STRING)) {
                                timeValue = cell1.getStringCellValue();
                                timeValue = timeValue.substring(0, timeValue.length() - 2);
                                timeValue = timeValue + "00";
                            } else if (cell1.getCellType() == CellType.NUMERIC) {
                                // 表格中时间字段格式有时候是数字类型的,需要转化一次
//                                System.out.println("cell type is " + cell1.getCellType());

//                                System.out.println(numericCellValue);

                                Date date = simpleDateFormat.parse(excelTime(cell1));
//                                System.out.println(sdf.format(date));
                                timeValue= simpleDateFormat.format(date);
                                timeValue = timeValue.substring(0, timeValue.length() - 2);
                                timeValue = timeValue + "00";

                            } else {
                                System.out.println(cell1.getCellType());
                                System.out.println("第" + rowNum + "行，第" + (2) + "列[" + title[1] + "]数据错误！");
                            }


                            Cell cell2 = row.getCell(2);
                            if ((cell2.getCellTypeEnum() == CellType.STRING)) {
                                temperatureValue = cell2.getStringCellValue();
                            } else if (cell2.getCellTypeEnum() == CellType.NUMERIC) {
                                temperatureValue = cell2.getNumericCellValue() + "";
                            } else {
                                System.out.println("第" + rowNum + "行，第" + (3) + "列[" + title[2] + "]数据错误！");
                            }

                            Date timeValueDate = simpleDateFormat.parse(timeValue);
                            long timeValueLong = timeValueDate.getTime();

                            fileMap.put(timeValueLong, Float.parseFloat(temperatureValue));
                        }
                    }
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else

        {
            System.out.println("文件不存在!");
        }

        statisticMap.put(path, fileMap);
    }


    public static String excelTime(Cell cell) {
        String guarantee_time = null;
        if (DateUtil.isCellDateFormatted(cell)) {
            //用于转化为日期格式
            Date d = cell.getDateCellValue();
//	             System.err.println(d.toString());
//	             DateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            guarantee_time = simpleDateFormat.format(d);
        }
        return guarantee_time;

    }
}
