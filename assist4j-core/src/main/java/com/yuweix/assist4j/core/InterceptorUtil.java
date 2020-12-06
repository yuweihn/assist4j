package com.yuweix.assist4j.core;


import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;


/**
 * @author wei
 */
public abstract class InterceptorUtil {
	public static boolean hasResponseBodyAnnotation(Object handler) {
		if (!(handler instanceof HandlerMethod)) {
			return false;
		}

		HandlerMethod handlerMethod = (HandlerMethod) handler;
		ResponseBody responseBodyAnn = handlerMethod.getMethod().getAnnotation(ResponseBody.class);

		if (responseBodyAnn != null) {
			return true;
		}

		RestController restControllerAnn = handlerMethod.getClass().getAnnotation(RestController.class);
		return restControllerAnn != null;
	}
}
