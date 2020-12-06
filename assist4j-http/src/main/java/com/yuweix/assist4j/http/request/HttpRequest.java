package com.yuweix.assist4j.http.request;


import com.yuweix.assist4j.http.response.HttpResponse;


/**
 * @author yuwei
 */
public interface HttpRequest {
	<B> HttpResponse<B> execute();
}
