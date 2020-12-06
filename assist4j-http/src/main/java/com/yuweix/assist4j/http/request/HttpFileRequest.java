package com.yuweix.assist4j.http.request;


import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yuweix.assist4j.http.HttpMethod;
import com.yuweix.assist4j.http.HttpConstant;
import com.yuweix.assist4j.http.response.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;


/**
 * 上传文件
 * @author yuwei
 */
public class HttpFileRequest extends AbstractHttpRequest<HttpFileRequest> {
	private List<FileField> fileFieldList;
	private List<FormField> formFieldList;


	private HttpFileRequest() {
		super();
		fileFieldList = new ArrayList<FileField>();
		formFieldList = new ArrayList<FormField>();
		method(HttpMethod.POST);
	}
	public static HttpFileRequest create() {
		return new HttpFileRequest();
	}


	public HttpFileRequest addFile(String fieldName, byte[] content, String fileName) {
		FileWrapper fw = new FileWrapper();
		fw.setContent(content);
		fw.setFileName(fileName);

		for (FileField ff: fileFieldList) {
			if (fieldName.equals(ff.getFieldName())) {
				ff.addFile(fw);
				return this;
			}
		}

		FileField ff = new FileField();
		ff.setFieldName(fieldName);
		ff.addFile(fw);
		fileFieldList.add(ff);
		return this;
	}

	public HttpFileRequest formFieldList(Map<String, ? extends Object> map) {
		if (map == null || map.isEmpty()) {
			return this;
		}

		List<FormField> formFieldList = new ArrayList<FormField>();
		for (Map.Entry<String, ? extends Object> entry: map.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (key == null || value == null) {
				continue;
			}
			formFieldList.add(new FormField(key, value));
		}
		return formFieldList(formFieldList);
	}
	public HttpFileRequest formFieldList(List<FormField> formFieldList) {
		this.formFieldList.clear();
		this.formFieldList.addAll(formFieldList);
		return this;
	}
	public HttpFileRequest addFormField(String key, String value) {
		if (key == null || "".equals(key) || value == null || "".equals(value)) {
			return this;
		}

		formFieldList.add(new FormField(key, value));
		return this;
	}

	@Override
	public <B> HttpResponse<B> execute() {
		String charset = getCharset();
		charset = charset != null ? charset : HttpConstant.ENCODING_UTF_8;
		
		ContentType textContentType = ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), charset);
		ContentType multipartContentType = ContentType.create(ContentType.MULTIPART_FORM_DATA.getMimeType(), charset);

		MultipartEntityBuilder builder = MultipartEntityBuilder.create()
															.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
															.setCharset(Charset.forName(charset));
		if (fileFieldList != null && fileFieldList.size() > 0) {
			for (FileField fileField: fileFieldList) {
				List<FileWrapper> fileList = fileField.getFileList();
				if (fileList != null && fileList.size()> 0) {
					for (FileWrapper fw: fileList) {
						builder.addBinaryBody(fileField.getFieldName(), fw.getContent(), multipartContentType, fw.getFileName());
					}
				}
			}
		}

		if (formFieldList != null && formFieldList.size() > 0) {
			for (FormField ff: formFieldList) {
				String k = ff.getKey();
				String v = ff.getValue();
				if (k == null || "".equals(k) || v == null || "".equals(v)) {
					continue;
				}
				builder.addTextBody(k, v, textContentType);
			}
		}
		HttpEntity entity = builder.build();

		HttpEntityEnclosingRequestBase requestBase = getRequestBase();
		requestBase.addHeader("Content-Disposition", "attachment");
		requestBase.setEntity(entity);
		this.setHttpUriRequest(requestBase);
		return execute0();
	}
}
