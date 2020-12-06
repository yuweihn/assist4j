package com.yuweix.assist4j.core.office;


import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;


/**
 * @author yuwei
 */
public abstract class ExcelUtil {
	private static final Logger log = LoggerFactory.getLogger(ExcelUtil.class);
	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	
	
	/**
	 * 读入excel工作簿文件，只读取第一个sheet
	 */
	public static List<Map<String, Object>> read(byte[] fileData) {
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(fileData);
			Workbook wb = WorkbookFactory.create(is);
			Sheet sheet = wb.getSheetAt(0);
			/**
			 * 读取头部，第一行
			 **/
			List<String> headList = getInputHeadList(sheet.getRow(0));
			/**
			 * 读取数据部分，从第二行开始
			 **/
			return getInputDataList(sheet, headList);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error("", e);
				}
			}
		}
	}

	public static<T> List<T> read(byte[] fileData, Class<T> clz) {
		List<T> list = new ArrayList<T>();

		Field[] fields = clz.getDeclaredFields();
		if (fields == null || fields.length <= 0) {
			return list;
		}

		List<Map<String, Object>> mapList = read(fileData);
		if (mapList == null || mapList.size() <= 0) {
			return list;
		}
		for (Map<String, Object> map: mapList) {
			Map<String, Object> fieldValueMap = new HashMap<String, Object>();
			for (Field field: fields) {
				ExcelKey excelKeyAno = field.getAnnotation(ExcelKey.class);
				if (excelKeyAno == null) {
					continue;
				}

				String key = excelKeyAno.title() == null || "".equals(excelKeyAno.title().trim()) ? field.getName() : excelKeyAno.title().trim();
				Object v = map.get(key);
				if (v == null) {
					continue;
				}

				fieldValueMap.put(field.getName(), v);
			}
			T t = JSONObject.parseObject(JSONObject.toJSONString(fieldValueMap), clz);
			list.add(t);
		}
		return list;
	}

	/**
	 * 导出数据
	 * @param dataList 数据
	 */
	public static<T> byte[] export(List<T> dataList) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		export(out, dataList);
		byte[] data = out.toByteArray();

		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				log.error("", e);
			}
		}
		return data;
	}

	/**
	 * 导出数据到输出流
	 * @param dataList 数据
	 */
	public static<T> void export(OutputStream out, List<T> dataList) {
		Assert.notEmpty(dataList, "[dataList] is required.");
		log.info("list size: {}", dataList.size());
		SXSSFWorkbook workbook = null;
		try {
			workbook = new SXSSFWorkbook();
			workbook.setCompressTempFiles(true);
			/**
			 * 表头样式
			 */
			CellStyle titleStyle = workbook.createCellStyle();
			titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			Font titleFont = workbook.createFont();
			titleFont.setFontHeightInPoints((short) 20);
			titleFont.setBoldweight((short) 700);
			titleStyle.setFont(titleFont);

			SXSSFSheet sheet = workbook.createSheet();
			sheet.trackAllColumnsForAutoSizing();

			/**
			 * 输出头部
			 **/
			List<String> headList = getOutputHeadList(dataList.get(0));
			SXSSFRow headRow = sheet.createRow(0);
			for (int i = 0; i < headList.size(); i++) {
				String head = headList.get(i);
				SXSSFCell cell = headRow.createCell(i);
				cell.setCellValue(head);
				cell.setCellStyle(titleStyle);
				sheet.autoSizeColumn(i, true);
			}

			List<String> keyList = getOutputKeyList(dataList.get(0));
			/**
			 * 输出数据部分
			 **/
			for (int i = 0; i < dataList.size(); i++) {
				T t = dataList.get(i);
				SXSSFRow dataRow = sheet.createRow(i + 1);
				List<Object> dList = getOutputDataList(keyList, t);
				for (int j = 0; j < dList.size(); j++) {
					SXSSFCell cell = dataRow.createCell(j);
					setCellValue(cell, dList.get(j));
				}
			}

			workbook.write(out);
		} catch (Exception e) {
			log.error("", e);
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					log.error("", e);
				}
			}
		}
	}

	private static<T> List<String> getOutputHeadList(T t) {
		List<String> list = new ArrayList<String>();
		if (Map.class.isAssignableFrom(t.getClass())) {
			Map<?, ?> map = (Map<?, ?>) t;
			Iterator<?> itr = map.keySet().iterator();
			while (itr.hasNext()) {
				list.add(itr.next().toString());
			}
		} else {
			Field[] fields = t.getClass().getDeclaredFields();
			if (fields != null && fields.length > 0) {
				for (Field field: fields) {
					ExcelKey excelKeyAno = field.getAnnotation(ExcelKey.class);
					if (excelKeyAno != null) {
						list.add(excelKeyAno.title() == null || "".equals(excelKeyAno.title().trim()) ? field.getName() : excelKeyAno.title().trim());
					}
				}
			}
		}

		return list;
	}

	private static<T> List<String> getOutputKeyList(T t) {
		List<String> list = new ArrayList<String>();
		if (Map.class.isAssignableFrom(t.getClass())) {
			Map<?, ?> map = (Map<?, ?>) t;
			Iterator<?> itr = map.keySet().iterator();
			while (itr.hasNext()) {
				list.add(itr.next().toString());
			}
		} else {
			Field[] fields = t.getClass().getDeclaredFields();
			if (fields != null && fields.length > 0) {
				for (Field field: fields) {
					ExcelKey excelKeyAno = field.getAnnotation(ExcelKey.class);
					if (excelKeyAno != null) {
						list.add(field.getName());
					}
				}
			}
		}

		return list;
	}

	private static<T> List<Object> getOutputDataList(List<String> keyList, T t) {
		Assert.notEmpty(keyList, "[keyList] is required.");
		List<Object> list = new ArrayList<Object>();

		if (Map.class.isAssignableFrom(t.getClass())) {
			Map<?, ?> map = (Map<?, ?>) t;
			for (String key: keyList) {
				list.add(map.get(key));
			}
		} else {
			for (String key: keyList) {
				PropertyDescriptor pd = null;
				try {
					pd = new PropertyDescriptor(key, t.getClass());
					Method getMethod = pd.getReadMethod();
					Object o = getMethod.invoke(t);
					list.add(o);
				} catch (Exception e) {
					log.error("", e);
				}
			}
		}
		return list;
	}
	
	private static List<String> getInputHeadList(Row row) {
		List<String> list = new ArrayList<String>();
		for (Cell cell: row) {
			String head = cell.toString();
			if (head == null || "".equals(head.trim())) {
				continue;
			}
			list.add(head.trim());
		}
		return list;
	}
	
	private static List<Map<String, Object>> getInputDataList(Sheet sheet, List<String> keyList) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (Row row: sheet) {
			if (row.getRowNum() <= 0) {
				continue;
			}
			
			Map<String, Object> map = new HashMap<String, Object>();
			int i = 0;
			for (Cell cell: row) {
				map.put(keyList.get(i++), getCellValue(cell));
			}
			list.add(map);
		}
		return list;
	}

	private static void setCellValue(Cell cell, Object value) {
		if (value instanceof Number) {
			cell.setCellValue(value == null ? 0 : Double.parseDouble(value.toString()));
		} else if (value instanceof Boolean) {
			cell.setCellValue(value == null ? false : Boolean.valueOf(value.toString()));
		} else if (value instanceof Date) {
			cell.setCellValue(value == null ? "" : new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(value));
		} else {
			cell.setCellValue(value == null ? "" : value.toString());
		}
	}

	private static Object getCellValue(Cell cell) {
		int ct = cell.getCellType();
		if (Cell.CELL_TYPE_NUMERIC == ct) {
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue();
			}

			double d = cell.getNumericCellValue();
			int i = (int) d;
			if (d == i) {
				return Integer.valueOf(i);
			} else {
				return Double.valueOf(d);
			}
//			return NumberToTextConverter.toText(cell.getNumericCellValue());
		}
		if (Cell.CELL_TYPE_STRING == ct) {
			return cell.getRichStringCellValue().getString();
		}
		if (Cell.CELL_TYPE_FORMULA == ct) {
			Workbook wb = cell.getSheet().getWorkbook();
			CreationHelper crateHelper = wb.getCreationHelper();
			FormulaEvaluator evaluator = crateHelper.createFormulaEvaluator();
			return getCellValue(evaluator.evaluateInCell(cell));
		}
		if (Cell.CELL_TYPE_BLANK == ct) {
			return null;
		}
		if (Cell.CELL_TYPE_BOOLEAN == ct) {
			return cell.getBooleanCellValue();
		}
		if (Cell.CELL_TYPE_ERROR == ct) {
			return null;
		}
		return null;
	}
}

