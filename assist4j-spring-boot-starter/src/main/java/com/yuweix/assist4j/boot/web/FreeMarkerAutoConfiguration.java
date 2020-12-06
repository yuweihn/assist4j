package com.yuweix.assist4j.boot.web;


import cn.org.rapid_framework.freemarker.directive.BlockDirective;
import cn.org.rapid_framework.freemarker.directive.ExtendsDirective;
import cn.org.rapid_framework.freemarker.directive.OverrideDirective;
import cn.org.rapid_framework.freemarker.directive.SuperDirective;
import com.yuweix.assist4j.web.Page;
import com.yuweix.assist4j.web.freemarker.FreemarkerUtil;
import freemarker.cache.ClassTemplateLoader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * @author yuwei
 */
@Configuration
@ConditionalOnProperty(name = "assist4j.boot.freemarker.enabled")
public class FreeMarkerAutoConfiguration {
	@ConditionalOnMissingBean
	@Bean(name = "freemarkerViewResolver")
	public FreeMarkerViewResolver freeMarkerViewResolver() {
		FreeMarkerViewResolver viewResolver = new FreeMarkerViewResolver();
		viewResolver.setViewClass(FreeMarkerView.class);
		viewResolver.setPrefix("");
		viewResolver.setSuffix(".ftl");
		viewResolver.setContentType("text/html;charset=utf-8");
		viewResolver.setExposeSpringMacroHelpers(true);
		viewResolver.setExposeRequestAttributes(true);
		viewResolver.setExposeSessionAttributes(true);
		viewResolver.setRequestContextAttribute("request");
		viewResolver.setCache(false);
		viewResolver.setOrder(0);
		return viewResolver;
	}

	@ConditionalOnMissingBean
	@Bean(name = "freemarkerConfig")
	public FreeMarkerConfigurer freemarkerConfig(
			@Value("${freemarker.template.path:classpath:/templates/}") String template_path,
			@Value("${freemarker.template.update.delay:0}") String template_update_delay,
			@Value("${freemarker.default.encoding:utf-8}") String default_encoding,
			@Value("${freemarker.output.encoding:utf-8}") String output_encoding,
			@Value("${freemarker.locale:zh_CN}") String locale,
			@Value("${freemarker.number.format:0.##########}") String number_format,
			@Value("${freemarker.date.format:yyyy-MM-dd}") String date_format,
			@Value("${freemarker.time.format:HH:mm:ss}") String time_format,
			@Value("${freemarker.datetime.format:yyyy-MM-dd HH:mm:ss}") String datetime_format,
			@Value("${freemarker.classic.compatible:true}") String classic_compatible,
			@Value("${freemarker.template.exception.handler:ignore}") String template_exception_handler,
			@Value("${freemarker.whitespace.stripping:true}") String whitespace_stripping,
			@Value("${freemarker.registered.custom.output.formats:}") String customOutputFormats,
			@Value("${freemarker.auto.import:}") String auto_import) {
		FreeMarkerConfigurer config = new FreeMarkerConfigurer();
		config.setTemplateLoaderPath(template_path);
		config.setPreTemplateLoaders(new ClassTemplateLoader(Page.class, ""));

		Properties settings = new Properties();
		settings.put("template_update_delay", template_update_delay);
		settings.put("default_encoding", default_encoding);
		settings.put("output_encoding", output_encoding);
		settings.put("locale", locale);
		settings.put("number_format", number_format);
		settings.put("date_format", date_format);
		settings.put("time_format", time_format);
		settings.put("datetime_format", datetime_format);
		settings.put("classic_compatible", classic_compatible);
		settings.put("template_exception_handler", template_exception_handler);
		settings.put("whitespace_stripping", whitespace_stripping);
		if (customOutputFormats != null && !"".equals(customOutputFormats)) {
			settings.put("registered_custom_output_formats", customOutputFormats);
		}
		if (auto_import != null && !"".equals(auto_import)) {
			settings.put("auto_import", auto_import);
		}
		config.setFreemarkerSettings(settings);

		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("extends", new ExtendsDirective());
		variables.put("override", new OverrideDirective());
		variables.put("block", new BlockDirective());
		variables.put("super", new SuperDirective());
		config.setFreemarkerVariables(variables);
		return config;
	}

	@ConditionalOnMissingBean
	@Bean
	public FreemarkerUtil freemarkerUtil(@Qualifier("freemarkerConfig") FreeMarkerConfigurer freemarkerConfig) {
		try {
			Class<?> clz = Class.forName(FreemarkerUtil.class.getName());
			Constructor<?> constructor = clz.getDeclaredConstructor();
			constructor.setAccessible(true);
			FreemarkerUtil ftl = (FreemarkerUtil) constructor.newInstance();
			ftl.setCfg(freemarkerConfig.getConfiguration());
			return ftl;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
