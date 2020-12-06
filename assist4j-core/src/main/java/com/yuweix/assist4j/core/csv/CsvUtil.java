package com.yuweix.assist4j.core.csv;


import java.beans.PropertyDescriptor;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.yuweix.assist4j.core.Constant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;


/**
 * @author yuwei
 */
public abstract class CsvUtil {
	private static final Logger log = LoggerFactory.getLogger(CsvUtil.class);


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
		Assert.notEmpty(dataList, "[dataList] must not be empty.");
		log.info("list size: {}", dataList.size());
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		try {
			osw = new OutputStreamWriter(out, Constant.ENCODING_UTF_8);
			bw = new BufferedWriter(osw);
			
			/**
			 * 输出头部
			 **/
			List<String> headList = getOutputHeadList(dataList.get(0));
			for (String head: headList) {
				bw.append(head).append(",");
			}
			bw.newLine();

			List<String> keyList = getOutputKeyList(dataList.get(0));
			/**
			 * 输出数据部分
			 **/
			for (T t: dataList) {
				List<Object> dList = getOutputDataList(keyList, t);
				for (Object d: dList) {
					bw.append(convertToString(d)).append(",");
				}
				bw.newLine();
			}
		} catch (Exception e) {
			log.error("", e);
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					log.error("", e);
				}
			}
			if (osw != null) {
				try {
					osw.close();
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
					CsvKey csvKeyAno = field.getAnnotation(CsvKey.class);
					if (csvKeyAno != null) {
						list.add(csvKeyAno.value() == null || "".equals(csvKeyAno.value().trim()) ? field.getName() : csvKeyAno.value());
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
					CsvKey csvKeyAno = field.getAnnotation(CsvKey.class);
					if (csvKeyAno != null) {
						list.add(field.getName());
					}
				}
			}
		}

		return list;
	}

	private static<T> List<Object> getOutputDataList(List<String> keyList, T t) {
		Assert.notEmpty(keyList, "[keyList] must not be empty.");
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

	private static String convertToString(Object value) {
		if (value == null) {
			return "";
		}
		if (value instanceof Date) {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value);
		}
		return value.toString();
	}
}
