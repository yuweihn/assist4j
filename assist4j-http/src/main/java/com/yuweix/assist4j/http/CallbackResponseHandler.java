package com.yuweix.assist4j.http;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.*;

import javax.servlet.http.Cookie;

import com.alibaba.fastjson.TypeReference;
import com.yuweix.assist4j.http.response.HttpResponse;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;


/**
 * 以回调方式处理返回结果
 * @author yuwei
 */
public class CallbackResponseHandler implements ResponseHandler<HttpResponse<? extends Object>> {
	private Class<?> typeClass;
	private TypeReference<?> typeReference;
	private HttpClientContext context;
	private String charset;


	private CallbackResponseHandler() {
		this.typeClass = String.class;
	}

	public static CallbackResponseHandler create() {
		return new CallbackResponseHandler();
	}

	public CallbackResponseHandler responseType(Class<?> typeClass) {
		this.typeClass = typeClass;
		return this;
	}

	public CallbackResponseHandler responseType(TypeReference<?> typeReference) {
		this.typeReference = typeReference;
		return this;
	}

	public CallbackResponseHandler context(HttpClientContext context) {
		this.context = context;
		return this;
	}

	public CallbackResponseHandler charset(String charset) {
		this.charset = charset;
		return this;
	}



	@Override
	public HttpResponse<? extends Object> handleResponse(org.apache.http.HttpResponse response) throws IOException {
		StatusLine statusLine = response.getStatusLine();

		/**
		 * status
		 */
		int status = statusLine.getStatusCode();
		HttpEntity entity = response.getEntity();
		if (entity == null) {
			BasicHttpResponse<Object> res = new BasicHttpResponse<Object>();
			res.setStatus(status);
			res.setErrorMessage("Entity is null.");
			return res;
		}

		/**
		 * headerList
		 */
		Header[] headerArr = response.getAllHeaders();
		List<Header> headerList = headerArr == null ? new ArrayList<Header>() : Arrays.asList(headerArr);

		/**
		 * cookieList
		 */
		List<Cookie> cookieList = new ArrayList<Cookie>();
		List<org.apache.http.cookie.Cookie> cookies = null;
		if (context != null) {
			CookieStore cookieStore = context.getCookieStore();
			if (cookieStore != null) {
				cookies = cookieStore.getCookies();
			}
		}
		if (cookies != null && cookies.size() > 0) {
			for (org.apache.http.cookie.Cookie c: cookies) {
				Date now = new Date();
				Date expiryDate = c.getExpiryDate();

				Cookie cookie = new Cookie(c.getName(), c.getValue());
				cookie.setComment(c.getComment());
				cookie.setDomain(c.getDomain());
				if (expiryDate == null) {
					cookie.setMaxAge(-1);
				} else {
					cookie.setMaxAge((int) (expiryDate.getTime() / 1000 - now.getTime() / 1000));
				}
				cookie.setPath(c.getPath());
				cookie.setSecure(c.isSecure());
				cookie.setVersion(c.getVersion());
				cookieList.add(cookie);
			}
		}

		/**
		 * contentType
		 */
		Header contentType = entity.getContentType();

		StringBuilder errorMessage = new StringBuilder(statusLine.toString());
		Object body = null;
		if (typeReference != null) {
			String txt = EntityUtils.toString(entity, charset != null ? charset : HttpConstant.ENCODING_UTF_8);
			if (HttpStatus.SC_OK == status) {
				body = JSONObject.parseObject(txt, typeReference);
			} else {
				errorMessage.append(". ").append(txt);
			}
		} else if (typeClass == null || String.class.isAssignableFrom(typeClass)) {
			/**
			 * 返回字符串类型
			 **/
			body = EntityUtils.toString(entity, charset != null ? charset : HttpConstant.ENCODING_UTF_8);
		} else if (byte[].class.isAssignableFrom(typeClass)) {
			/**
			 * 返回字节数组类型
			 **/
			body = read(entity.getContent());
		} else if (Decoder.class.isAssignableFrom(typeClass)) {
			String txt = EntityUtils.toString(entity, charset != null ? charset : HttpConstant.ENCODING_UTF_8);
			try {
				Constructor<?> constructor = typeClass.getDeclaredConstructor();
				constructor.setAccessible(true);
				Decoder<?> decoder = (Decoder<?>) constructor.newInstance();
				body = decoder.decode(txt);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			/**
			 * 返回指定的其它类型
			 **/
			String txt = EntityUtils.toString(entity, charset != null ? charset : HttpConstant.ENCODING_UTF_8);
			if (HttpStatus.SC_OK == status) {
				body = JSONObject.parseObject(txt, typeClass);
			} else {
				errorMessage.append(". ").append(txt);
			}
		}
		return createBasicHttpResponse(status, errorMessage.toString(), body, headerList, cookieList, contentType);
	}

	private static byte[] read(InputStream is) {
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len;
			while ((len = is.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			return out.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private <T>BasicHttpResponse<T> createBasicHttpResponse(int status, String errorMessage, T body
			, List<Header> headerList, List<Cookie> cookieList, Header contentType) {
		BasicHttpResponse<T> res = new BasicHttpResponse<T>();
		res.setStatus(status);
		res.setErrorMessage(errorMessage);
		res.setBody(body);
		res.setHeaderList(headerList);
		res.setCookieList(cookieList);
		res.setContentType(contentType == null ? null : contentType.getValue());
		return res;
	}

	private class BasicHttpResponse<B> implements HttpResponse<B> {
		private int status;
		private String errorMessage;
		private B body;
		private List<Cookie> cookieList;
		private List<Header> headerList;
		private String contentType;


		public BasicHttpResponse() {

		}


		@Override
		public boolean isSuccess() {
			return HttpStatus.SC_OK == getStatus();
		}
		@Override
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		@Override
		public String getErrorMessage() {
			return errorMessage;
		}
		public void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}
		@Override
		public B getBody() {
			return body;
		}
		public void setBody(B body) {
			this.body = body;
		}
		@Override
		public List<Cookie> getCookieList() {
			return cookieList;
		}
		public void setCookieList(List<Cookie> cookieList) {
			this.cookieList = cookieList;
		}
		@Override
		public List<Header> getHeaderList() {
			return headerList;
		}
		public void setHeaderList(List<Header> headerList) {
			this.headerList = headerList;
		}
		@Override
		public String getContentType() {
			return contentType;
		}
		public void setContentType(String contentType) {
			this.contentType = contentType;
		}

		@Override
		public String toString() {
			return JSONObject.toJSONString(this);
		}
	}
}
