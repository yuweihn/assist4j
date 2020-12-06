package com.yuweix.assist4j.core.office;


import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author yuwei
 */
public abstract class WordUtil {
	private static final Logger log = LoggerFactory.getLogger(WordUtil.class);
	private static final String TYPE_DOC = "doc";
	private static final String TYPE_DOCX = "docx";


	private static byte[] replaceInDoc(byte[] fileData, Map<String, String> map) {
		InputStream inputStream = null;
		ByteArrayOutputStream outputStream = null;
		HWPFDocument document = null;
		try {
			inputStream = new ByteArrayInputStream(fileData);
			document = new HWPFDocument(inputStream);
			Range range = document.getRange();
			for (Map.Entry<String, String> entry: map.entrySet()) {
				range.replaceText(entry.getKey(), entry.getValue());
			}

			outputStream = new ByteArrayOutputStream();
			document.write(outputStream);
			return outputStream.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					log.error("", e);
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error("", e);
				}
			}
		}
	}

	private static byte[] replaceInDocx(byte[] fileData, Map<String, String> map) {
		InputStream inputStream = null;
		ByteArrayOutputStream outputStream = null;
		XWPFDocument document = null;
		try {
			inputStream = new ByteArrayInputStream(fileData);
			document = new XWPFDocument(inputStream);
			// 替换段落中的指定文字
			Iterator<XWPFParagraph> itPara = document.getParagraphsIterator();
			while (itPara.hasNext()) {
				replaceInAParagraph(itPara.next(), map);
			}

			// 替换表格中的指定文字
			Iterator<XWPFTable> itTable = document.getTablesIterator();
			while (itTable.hasNext()) {
				XWPFTable table = itTable.next();
				int rCount = table.getNumberOfRows();
				for (int i = 0; i < rCount; i++) {
					XWPFTableRow row = table.getRow(i);
					List<XWPFTableCell> cells = row.getTableCells();
					for (XWPFTableCell cell: cells) {
						List<XWPFParagraph> cellParas = cell.getParagraphs();
						for (XWPFParagraph cellPara: cellParas) {
							replaceInAParagraph(cellPara, map);
						}
//
//						String cellTextString = cell.getText();
//						for (Map.Entry<String, String> e : map.entrySet()) {
//							if (cellTextString.contains(e.getKey())) {
//								cellTextString = cellTextString.replace(e.getKey(), e.getValue());
//							}
//						}
//						cell.removeParagraph(0);
//						cell.setText(cellTextString);
					}
				}
			}

			outputStream = new ByteArrayOutputStream();
			document.write(outputStream);
			return outputStream.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					log.error("", e);
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error("", e);
				}
			}
			if (document != null) {
				try {
					document.close();
				} catch (IOException e) {
					log.error("", e);
				}
			}
		}
	}

	private static void replaceInAParagraph(XWPFParagraph paragraph, Map<String, String> map) {
		List<XWPFRun> runs = paragraph.getRuns();
		for (int i = 0; i < runs.size(); i++) {
			String textPos = runs.get(i).getText(runs.get(i).getTextPosition());
			for (Map.Entry<String, String> entry: map.entrySet()) {
				if(textPos != null){
					textPos = textPos.replace(entry.getKey(), entry.getValue());
				}
			}
			runs.get(i).setText(textPos, 0);
		}
	}

	/**
	 * 替换word中需要替换的特殊字符
	 * fileType取值：doc或docx
	 */
	public static byte[] replace(byte[] fileData, String fileType, Map<String, String> map) {
		if (fileData == null || fileData.length <= 0) {
			throw new IllegalArgumentException("[fileData] is required.");
		}
		Assert.notEmpty(map, "[map] is required.");

		if (TYPE_DOCX.equals(fileType)) {
			return replaceInDocx(fileData, map);
		} else if (TYPE_DOC.equals(fileType)) {
			return replaceInDoc(fileData, map);
		}

		throw new IllegalArgumentException("[fileType] must be doc or docx.");
	}
}

