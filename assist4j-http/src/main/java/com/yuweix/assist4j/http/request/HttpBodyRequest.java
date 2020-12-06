package com.yuweix.assist4j.http.request;


import com.yuweix.assist4j.http.HttpConstant;
import com.yuweix.assist4j.http.HttpMethod;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import com.yuweix.assist4j.http.response.HttpResponse;


/**
 * body请求
 * @author yuwei
 */
public class HttpBodyRequest extends AbstractHttpRequest<HttpBodyRequest> {
	private String content;
	private ContentType contentType;


	private HttpBodyRequest() {
		super();
		contentType(ContentType.TEXT_PLAIN);
		method(HttpMethod.POST);
	}
	public static HttpBodyRequest create() {
		return new HttpBodyRequest();
	}


	public HttpBodyRequest content(String content) {
		this.content = content;
		return this;
	}
	public HttpBodyRequest contentType(ContentType contentType) {
		this.contentType = contentType;
		return this;
	}


	@Override
	public <B>HttpResponse<B> execute() {
		String charset = getCharset();
		charset = charset != null ? charset : HttpConstant.ENCODING_UTF_8;

		StringEntity entity = new StringEntity(content, charset);
		entity.setContentType(contentType.getMimeType());
		entity.setContentEncoding(charset);

		HttpEntityEnclosingRequestBase requestBase = getRequestBase();
		requestBase.setEntity(entity);
		this.setHttpUriRequest(requestBase);
		return execute0();
	}

	@Override
	protected ContentType getHeaderContentType() {
		String charset = getCharset();
		charset = charset != null ? charset : HttpConstant.ENCODING_UTF_8;
		return contentType == null ? null : contentType.withCharset(charset);
	}
}
