package com.yuweix.assist4j.core.exception;


import org.springframework.web.servlet.ModelAndView;


/**
 * 视图解析器
 * @author yuwei
 */
public interface ExceptionViewResolver {
	ModelAndView createView(String content);
}
