package com.yuweix.assist4j.http;


import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;


/**
 * @author yuwei
 */
public class HttpContextAdaptor extends HttpClientContext {
	public HttpContextAdaptor() {
		super();
	}
	public HttpContextAdaptor(HttpContext context) {
		super(context);
	}

	public static HttpContextAdaptor create() {
		HttpContextAdaptor ctx = new HttpContextAdaptor(new BasicHttpContext());
		ctx.setCookieStore(new BasicCookieStore());
		return ctx;
	}
}
