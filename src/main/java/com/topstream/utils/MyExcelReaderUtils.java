/**
 * 
 */
package com.topstream.utils;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author majun
 *
 */
public class MyExcelReaderUtils {

	private static Logger logger = LoggerFactory.getLogger(MyExcelReaderUtils.class);

	public String getCellFromExcel(String path, String row, String col) throws Exception {
		return getCellFromExcel(path, 0, Integer.valueOf(row), Integer.valueOf(col));
	}

	public Map<Integer, String> getRowFromExcel(String path, String row) throws Exception {
		return getRowFromExcel(path, 0, Integer.valueOf(row));
	}

	/**
	 *
	 * @param path
	 * @param sheetNo
	 * @return
	 * @throws Exception
	 */
	public static Map<Integer, Map<Integer, String>> getSheetFromExcel(String path, int sheetNo) {
		File xlsxFile = new File(path);
		Workbook workbook = null;
		try {
			workbook = WorkbookFactory.create(xlsxFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Sheet sheet = workbook.getSheetAt(sheetNo);
		int firstRowNum = sheet.getFirstRowNum();
		int lastRowNum = sheet.getLastRowNum();
		Map<Integer, Map<Integer, String>> integerMapHashMap = new HashMap<>();
		for (int i = firstRowNum; i < lastRowNum+1; i++) {
			Row row = sheet.getRow(i);
			Map<Integer, String> integerStringHashMap = new HashMap<>();
			int cellNum = row.getLastCellNum();
			for (int j = 0; j < cellNum; j++) {
				String cellValueByCell = getCellValueByCell(row.getCell(j));
				integerStringHashMap.put(j, cellValueByCell);
			}
			logger.info("文件名：{}，sheetNo：{}，取值：{}", path, sheetNo, integerStringHashMap.toString());
			integerMapHashMap.put(i,integerStringHashMap);
		}
		return integerMapHashMap;
	}

	public Map<Integer, String> getRowFromExcel(String path, int sheetNo, int rowNo) throws Exception {
		File xlsxFile = new File(path);
		Workbook workbook = WorkbookFactory.create(xlsxFile);
		Sheet sheet = workbook.getSheetAt(sheetNo);
		Row row = sheet.getRow(rowNo);
		Map<Integer, String> integerStringHashMap = new HashMap<>();
		int rowNum = row.getLastCellNum();
		for (int i = 0; i < rowNum; i++) {
			String cellValueByCell = getCellValueByCell(row.getCell(i));
			integerStringHashMap.put(i,cellValueByCell);
		}
		logger.info("文件名：{}，sheetNo：{}，rowNo：{}，取值：{}", path, sheetNo, rowNo, integerStringHashMap.toString());
		return integerStringHashMap;
	}

	public String getCellFromExcel(String path, int sheet, int row, int col) throws Exception {
		File xlsx = new File(path);
		Workbook workbook = WorkbookFactory.create(xlsx);
		Sheet sheet1 = workbook.getSheetAt(sheet);
		Row row1 = sheet1.getRow(row);
		String cell = this.getCellValueByCell(row1.getCell(col));
		logger.info("文件名：{}，sheet：{}，row：{}，col：{}，取值：{}", path, sheet, row, col, cell);
		return cell;
	}

	// 获取单元格各类型值，返回字符串类型
	private static String getCellValueByCell(Cell cell) {
		// 判断是否为null或空串
		if (cell == null || cell.toString().trim().equals("")) {
			return "";
		}
		String cellValue = "";
		CellType cellType = cell.getCellType();
		// 以下是判断数据的类型
		switch (cellType) {
		case NUMERIC: // 数字
			// 判断当前的cell是否为Date
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				// 如果是Date类型则，取得该Cell的Date值
				Date date = cell.getDateCellValue();
				// 把Date转换成本地格式的字符串
				cellValue = cell.getDateCellValue().toLocaleString();
			} else { // 如果是纯数字
				// 取得当前Cell的数值,强制转换为int
				double numericCellValue = cell.getNumericCellValue();
				long longVal = Math.round(numericCellValue);
				if (Double.parseDouble(longVal + ".0") == numericCellValue) {
					cellValue = String.valueOf(longVal);
				} else {
					cellValue = String.valueOf(numericCellValue);
					// 格式化科学计数法，取一位整数
//					DecimalFormat df = new DecimalFormat("0");
//					cellValue = df.format(numericCellValue);
				}
			}
			break;
		case STRING: // 字符串
			cellValue = cell.getStringCellValue();
			break;
		case BOOLEAN: // Boolean
			cellValue = cell.getBooleanCellValue() + "";
			break;
		case FORMULA: // 公式
			cellValue = cell.getCellFormula() + "";
			break;
		case BLANK: // 空值
			cellValue = "";
			break;
		case ERROR: // 故障
			cellValue = "非法字符";
			break;
		default:
			cellValue = "未知类型";
			break;
		}
		return cellValue;
	}

	/**
	 *
	 * @Title: contentToTxt @Description: TODO(这里用一句话描述这个方法的作用) @param @param
	 *         filePath @param @param content 参数 @return void 返回类型 @throws
	 */
	public static void contentToTxt(String filePath, String content) {
		String oldString = new String(); // 原有txt内容
		String newString = new String();// 内容更新
		try {
			File file = new File(filePath);
			if (file.exists()) {
				System.out.print("文件存在");
			} else {
				System.out.print("文件不存在");
				file.createNewFile();// 不存在则创建
			}
			BufferedReader input = new BufferedReader(new FileReader(file));
			while ((oldString = input.readLine()) != null) {
				newString += oldString + "\n";
			}
			System.out.println(newString);
			input.close();
			newString += content;
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(newString);
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}
