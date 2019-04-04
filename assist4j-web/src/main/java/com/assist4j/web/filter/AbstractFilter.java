package com.assist4j.web.filter;


import com.assist4j.core.ActionUtil;
import com.assist4j.core.Constant;
import com.assist4j.core.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;


/**
 * @author yuwei
 */
public abstract class AbstractFilter<R extends HttpServletRequest, T extends HttpServletResponse> extends OncePerRequestFilter {
	private static final Logger log = LoggerFactory.getLogger(AbstractFilter.class);


	private static final String DEFAULT_METHOD_PARAM = "_method";
	private static final String DEFAULT_ENCODING = Constant.ENCODING_UTF_8;
	private static final String DEFAULT_STATIC_PATH = "/static/";

	private String methodParam = DEFAULT_METHOD_PARAM;
	private String encoding = DEFAULT_ENCODING;
	private String staticPath = DEFAULT_STATIC_PATH;
	private String scheme = null;
	private boolean allowCors = true;
	private boolean allowLogRequest = true;


	public void setMethodParam(String methodParam) {
		this.methodParam = methodParam;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setStaticPath(String staticPath) {
		this.staticPath = staticPath;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public void setAllowCors(boolean allowCors) {
		this.allowCors = allowCors;
	}

    public void setAllowLogRequest(boolean allowLogRequest) {
        this.allowLogRequest = allowLogRequest;
    }



    @Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		PathPattern exclusivePattern = InitParameter.getInstance().getExclusivePattern();
		if (exclusivePattern != null && exclusivePattern.matches(request)) {
			filterChain.doFilter(request, response);
			return;
		}

		long startTimeMillis = System.currentTimeMillis();
		R req = wrap(adjustMethod(request));
		T resp = wrap(response);
		beforeFilter(req, resp);

		setCharacterEncoding(req, resp);
		setContextPath(req);
		if (allowCors) {
			setAccessControl(req, resp);
		}

		filterChain.doFilter(req, resp);

		if (allowLogRequest) {
            logRequest(req);
		}
		afterFilter(req, resp);
		long endTimeMillis = System.currentTimeMillis();
		log.info("Status: {}. Time Cost: {} ms.", resp.getStatus(), endTimeMillis - startTimeMillis);
	}


	@SuppressWarnings("unchecked")
	protected R wrap(HttpServletRequest request) {
		return (R) request;
	}

	@SuppressWarnings("unchecked")
	protected T wrap(HttpServletResponse response) {
		return (T) response;
	}

	/**
	 * 浏览器不支持put、delete等method，需要将/service?_method=delete转换为标准的http delete方法
	 **/
	private HttpServletRequest adjustMethod(HttpServletRequest request) {
		if (!"post".equalsIgnoreCase(request.getMethod())) {
			return request;
		}

		String paramValue = request.getParameter(methodParam);
		if (paramValue == null || "".equals(paramValue.trim())) {
			return request;
		}

		final String method = paramValue.trim().toUpperCase(Locale.ENGLISH);
		return new HttpServletRequestWrapper(request) {
			@Override
			public String getMethod() {
				return method;
			}
		};
	}

	/**
	 * 打印请求参数
	 */
	protected void logRequest(R request) {
		String url = request.getRequestURL().toString();
		try {
			url = URLDecoder.decode(url, Constant.ENCODING_UTF_8);
		} catch (UnsupportedEncodingException e) {
			log.error("", e);
		}
		String contentType = request.getContentType();
		Map<String, String[]> params = request.getParameterMap();

		LinkedHashMap<String, Object> baseLogMap = new LinkedHashMap<String, Object>();
		baseLogMap.put("ip", ActionUtil.getRequestIP());
		baseLogMap.put("method", request.getMethod().toLowerCase());
		baseLogMap.put("url", url);
		if (contentType != null) {
			baseLogMap.put("contentType", contentType);
		}
		if (params != null && !params.isEmpty()) {
			baseLogMap.put("params", params);
		}

		LinkedHashMap<String, Object> firstLogMap = addLogFirst();
		LinkedHashMap<String, Object> lastLogMap = addLogLast();
		LinkedHashMap<String, Object> allLogMap = new LinkedHashMap<String, Object>();
		if (firstLogMap != null) {
			allLogMap.putAll(firstLogMap);
		}
		allLogMap.putAll(baseLogMap);
		if (lastLogMap != null) {
			allLogMap.putAll(lastLogMap);
		}

		log.info(JsonUtil.toJson(allLogMap));
	}

	protected LinkedHashMap<String, Object> addLogFirst() {
		return null;
	}

	protected LinkedHashMap<String, Object> addLogLast() {
		return null;
	}

	/**
	 * 设置字符集
	 **/
	protected void setCharacterEncoding(R request, T response) {
		try {
			request.setCharacterEncoding(encoding);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		response.setCharacterEncoding(encoding);
	}

	/**
	 * 将站点域名和static资源地址存入context
	 **/
	protected void setContextPath(R request) {
		ActionUtil.addContextPath(request, scheme);
		ActionUtil.addStaticPath(request, staticPath);
	}

	/**
	 * 跨域请求设置
	 */
	protected void setAccessControl(R request, T response) {
		String referrer = request.getHeader("Referer");
		if (referrer != null) {
			try {
				referrer = URLDecoder.decode(referrer, Constant.ENCODING_UTF_8);
			} catch (Exception e) {
				log.error("", e);
			}
			log.info("Referrer: {}", referrer);
		}

		if (!response.containsHeader("Access-Control-Allow-Origin")) {
			String requestOrigin = request.getHeader("origin");
			if (requestOrigin != null && !"".equals(requestOrigin.trim()) && !"null".equals(requestOrigin.trim())) {
				response.setHeader("Access-Control-Allow-Origin", requestOrigin);
				response.setHeader("Access-Control-Allow-Credentials", "true");
			} else {
				response.setHeader("Access-Control-Allow-Origin", "*");
			}
			response.setHeader("Access-Control-Max-Age", "3600");
		}

		if (!response.containsHeader("Access-Control-Allow-Methods")) {
			List<HttpMethod> allowedMethods = Arrays.asList(HttpMethod.values());
			response.setHeader("Access-Control-Allow-Methods", StringUtils.collectionToCommaDelimitedString(allowedMethods));
		}
		if (!response.containsHeader("Access-Control-Allow-Headers")) {
			response.setHeader("Access-Control-Allow-Headers", "*");
		}
	}


	protected void beforeFilter(R request, T response) throws IOException {

	}

	protected void afterFilter(R request, T response) throws IOException {

	}

	@Override
	public void initFilterBean() throws ServletException {
		super.initFilterBean();
		FilterConfig config = this.getFilterConfig();
		InitParameter initParameter = InitParameter.getInstance();

		String exclusive = config.getInitParameter("exclusive");
		if (exclusive != null && !"".equals(exclusive.trim())) {
			initParameter.setExclusivePattern(exclusive.split(","));
		}
	}
}
