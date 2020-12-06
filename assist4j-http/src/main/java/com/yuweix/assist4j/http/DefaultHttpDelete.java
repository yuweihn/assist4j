package com.yuweix.assist4j.http;


import java.net.URI;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;


/**
 * @author yuwei
 */
public class DefaultHttpDelete extends HttpEntityEnclosingRequestBase {
	public static final String METHOD_NAME = "DELETE";


	@Override
	public String getMethod() {
		return METHOD_NAME;
	}

	public DefaultHttpDelete(String uri) {
		this(URI.create(uri));
	}

	public DefaultHttpDelete(URI uri) {
		super();
		setURI(uri);
	}

	public DefaultHttpDelete() {
		super();
	}
}
