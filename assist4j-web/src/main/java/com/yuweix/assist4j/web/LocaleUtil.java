package com.yuweix.assist4j.web;


import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

import com.yuweix.assist4j.core.ActionUtil;
import com.yuweix.assist4j.core.Constant;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;


/**
 * @author wei
 */
public abstract class LocaleUtil {
	public static Locale getLocale(){
		HttpServletRequest request = ActionUtil.getRequest();
		if (request == null) {
			return getLocaleFromString(Constant.LOCALE_ZH_CN);
		}
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		return localeResolver.resolveLocale(request);
	}

	public static void setLocale(String locale){
		setLocale(getLocaleFromString(locale));
	}

	private static void setLocale(Locale locale){
		HttpServletRequest request = ActionUtil.getRequest();
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		LocaleEditor localeEditor = new LocaleEditor();
		localeEditor.setAsText(locale.toString());
		localeResolver.setLocale(request, null, locale);
	}

	public static Locale getLocaleFromString(String localeStr){
		Locale defaultLocale = Locale.getDefault();
		if ((localeStr == null) || (localeStr.trim().length() == 0) || ("_".equals(localeStr))) {
			if (defaultLocale != null) {
				return defaultLocale;
			}
			return Locale.getDefault();
		}

		int index = localeStr.indexOf('_');
		if (index < 0) {
			return new Locale(localeStr);
		}

		String language = localeStr.substring(0, index);
		if (index == localeStr.length()) {
			return new Locale(language);
		}

		String localeStr0 = localeStr.substring(index + 1);
		index = localeStr0.indexOf('_');
		if (index < 0) {
			return new Locale(language, localeStr0);
		}

		String country = localeStr0.substring(0, index);
		if (index == localeStr0.length()) {
			return new Locale(language, country);
		}

		localeStr0 = localeStr0.substring(index + 1);
		return new Locale(language, country, localeStr0);
	}
}
