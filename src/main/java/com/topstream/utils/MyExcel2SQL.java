package com.topstream.utils;

import java.util.Map;

public class MyExcel2SQL {

/*表样例：
    DROP TABLE IF EXISTS `t_table1`;
    CREATE TABLE `t_table1` (
       `id` int NOT NULL AUTO_INCREMENT COLLATE utf8_bin COMMENT '记录唯一ID；说明：记录唯一ID；样例：1234567',
       `name` varchar(10) NOT NULL COLLATE utf8_bin COMMENT '名字；样例：张三',
       `age` int(5) NOT NULL COLLATE utf8_bin COMMENT '年龄；说明：小于150；样例：30',
       `import_time` datetime  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COLLATE utf8_bin COMMENT '数据插入时间',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8
    COLLATE = utf8_bin
    COMMENT = '表1'
    ROW_FORMAT = COMPACT;
    */

    public static void printSQL(String filePath) {
        //根据文件path获取文件
        System.out.println("\nSET NAMES utf8mb4;\n" + "SET FOREIGN_KEY_CHECKS = 0;");
        int sheetNo = 0;
        Map<Integer, Map<Integer, String>> dataFromExcelMap = MyExcelReaderUtils.getSheetFromExcel(filePath, sheetNo);
        System.out.println("list中的数据打印出来==="+dataFromExcelMap);
        StringBuffer table = new StringBuffer();
        StringBuffer rows = new StringBuffer();
        StringBuffer insertC = new StringBuffer();
        StringBuffer insertV = new StringBuffer();
        int startRow = 2;
        int tableNameRow = 0;

        int size = dataFromExcelMap.size();
        for (int i = startRow; i < size; i++) {
            Map<Integer, String> rowMap = dataFromExcelMap.get(i);
            int rowSize = rowMap.size();
            int columStart = 0;
            int columEnd = 0;
            //在excel中做固定，不要在程序中选择
            //第一列：列名，第二列类型，第三列描述....
            //字段ID	数据类型	可空	默认值	字段名称	字段补充说明	数据样例
            String clnName = rowMap.get(columStart + 1).toString().trim();//字段ID
            String type = rowMap.get(columStart + 2).toString().trim();//数据类型
            String isNull = rowMap.get(columStart + 3).toString().trim();//可空
            String comment = rowMap.get(columStart + 5).toString().trim();//字段名（中文名称）
            String comment1 = rowMap.get(columStart + 6).toString().trim();//字段补充说明
            String comment2 = rowMap.get(columStart + 7).toString().trim();//数据样例
            comment = comment.replaceAll("'", "\"");

            rows.append("    `").append(clnName).append("` ")//cln name
                    .append(type)//类型
                    .append(" " + isNull)//是否非空
                    //`import_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '数据插入时间'
                    .append(clnName.equals("import_time") ? " DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" : "")
                    .append(clnName.equals("id") ? " AUTO_INCREMENT" : "")
                    .append(" COLLATE utf8_bin COMMENT ")
                    .append("'")//注释
                    .append(comment)//字段名（中文名称）
                    .append(comment1 == null || comment1.trim().equals("") ? "" : ";说明:" + comment1)//字段补充说明
                    .append(comment2 == null || comment2.trim().equals("") ? "" : ";样例:" + comment2)//数据样例
                    .append("',\n");

            //组将insert
            if (!clnName.equals("id") && !clnName.equals("import_time")) {
                insertC.append(clnName)
                        .append(",");

                insertV.append(type.contains("int") ? "" : "\"")
                        .append(type.contains("int") && !clnName.equals("id") ? 1 : "")
                        .append(type.contains("char") ? "testval" + i : "")
                        .append(type.contains("date") && !clnName.equals("import_time") ? "2019-01-16" : "")
                        .append(type.contains("int") ? "" : "\"")
                        .append(",");
            }
        }
        //表名、主键等
        String pk = dataFromExcelMap.get(startRow).get(1).toString().trim();
        String tableName = dataFromExcelMap.get(tableNameRow).get(0).toString().trim();
        String tableComment = dataFromExcelMap.get(tableNameRow).get(5).toString().trim();

        table.append("\nDROP TABLE IF EXISTS `" + tableName + "`;\nCREATE TABLE `" + tableName + "` (\n");
        table.append(rows.toString().substring(0, rows.length() - 1) + "\n");
        table.append("PRIMARY KEY (`" + pk + "`)\n) ");
        table.append("ENGINE = InnoDB\n" +
                "DEFAULT CHARACTER SET = utf8\n" +
                "COLLATE = utf8_bin\n");
        table.append("COMMENT = '" + tableComment + "'\n" +
                "ROW_FORMAT = COMPACT;");
        //打印建表语句
        System.out.println(table);
        //insert
        String replace = insertV.toString().replace("\"\"", "");
        String ins = "insert into " + tableName + " (" + insertC.substring(0, insertC.length() - 1) + ")values(" + replace.substring(0, replace.length() - 1) + ")";
        //打印插入语句
//        System.out.println(ins);
    }

}