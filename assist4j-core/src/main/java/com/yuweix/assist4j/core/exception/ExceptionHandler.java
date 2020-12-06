package com.yuweix.assist4j.core.exception;


import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;


/**
 * 通用异常处理方式
 * @author yuwei
 */
public class ExceptionHandler implements HandlerExceptionResolver {
	private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);
	private ExceptionViewResolver viewResolver;
	private Map<Class<?>, String> errorMsgMap;
	private boolean showExceptionName;

	public void setViewResolver(ExceptionViewResolver viewResolver) {
		this.viewResolver = viewResolver;
	}

	public void setErrorMsgMap(Map<Class<?>, String> errorMsgMap) {
		this.errorMsgMap = errorMsgMap;
	}

	public void setShowExceptionName(boolean showExceptionName) {
		this.showExceptionName = showExceptionName;
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response
			, Object handler, Exception ex) {
		log.error("", ex);
		return viewResolver.createView(showMessage(ex));
	}

	private String showMessage(Exception ex) {
		Class<? extends Exception> aClz = ex.getClass();
		String msg = ex.getMessage();
		String defaultMsg = errorMsgMap == null ? null : errorMsgMap.get(aClz);

		if (msg != null && !"".equals(msg)) {
			return showExceptionName ? aClz.getName() + ": " + msg : msg;
		}

		if (defaultMsg != null && !"".equals(defaultMsg)) {
			return showExceptionName ? aClz.getName() + ": " + defaultMsg : defaultMsg;
		}

		return aClz.getName();
	}
}
