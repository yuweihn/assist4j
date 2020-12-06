package com.yuweix.assist4j.web.freemarker;


import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.AbstractTemplateView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author wei
 */
public class FreemarkerUtil {
    private static Configuration cfg;
    private static String ftlPath = "";

    private FreemarkerUtil() {

    }


    public static String merge(String template) {
        return merge(template, null);
    }
    public static String merge(String template, Map<String, Object> params) {
        return merge(template, params, null, null);
    }
    public static String merge(String template, Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        if (request != null) {
            params.put(AbstractTemplateView.SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE, new RequestContext(request
                    , response, request.getServletContext(), params));
        }

        try {
            Template temp = cfg.getTemplate(ftlPath + template);

            Writer out = new StringWriter();
            temp.process(params, out);
            out.flush();
            return out.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void setCfg(Configuration cfg) {
        FreemarkerUtil.cfg = cfg;
    }

    public void setFtlPath(String ftlPath) {
        FreemarkerUtil.ftlPath = ftlPath;
    }
}
