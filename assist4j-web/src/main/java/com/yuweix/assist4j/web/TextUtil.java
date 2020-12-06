package com.yuweix.assist4j.web;


import java.util.Locale;
import org.springframework.context.MessageSource;


/**
 * @author wei
 */
public class TextUtil {
	private static MessageSource messageSource;

	private TextUtil() {

	}

	public static String getText(String key) {
		return getText(key, LocaleUtil.getLocale());
	}


	public static String getText(String key, Locale locale) {
		return messageSource.getMessage(key, new Object[] {}, locale);
	}

	public static String getText(String key, String locale) {
		return messageSource.getMessage(key, new Object[] {}, LocaleUtil.getLocaleFromString(locale));
	}

	public static String getText(String key, Object[] params) {
		return messageSource.getMessage(key, params, LocaleUtil.getLocale());
	}

	public static String getText(String key, Object[] params, String locale) {
		return messageSource.getMessage(key, params, LocaleUtil.getLocaleFromString(locale));
	}

	//////////////////////////////////////////////////////////////////////////////////////
	public void setMessageSource(MessageSource messageSource) {
		TextUtil.messageSource = messageSource;
	}
}
