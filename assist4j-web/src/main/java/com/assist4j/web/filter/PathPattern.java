package com.assist4j.web.filter;


import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


/**
 * @author yuwei
 */
public class PathPattern {
    private static final String PATTERN_MATCH_ALL = "/*";


    /**
     * 精确匹配
     */
    private final List<String> exactMatches = new ArrayList<String>();
    /**
     * 前置匹配
     */
    private final List<String> startsWithMatches = new ArrayList<String>();


    public PathPattern(String... urlPatterns) {
        for (String urlPattern: urlPatterns) {
            addUrlPattern(urlPattern);
        }
    }

    private void addUrlPattern(String urlPattern) {
        if (urlPattern.equals(PATTERN_MATCH_ALL)) {
            this.startsWithMatches.add("");
        } else if (urlPattern.endsWith(PATTERN_MATCH_ALL)) {
            this.startsWithMatches.add(urlPattern.substring(0, urlPattern.length() - 1));
            this.exactMatches.add(urlPattern.substring(0, urlPattern.length() - 1));
            this.exactMatches.add(urlPattern.substring(0, urlPattern.length() - 2));
        } else {
            this.exactMatches.add(urlPattern);
        }
    }

    public boolean matches(HttpServletRequest request) {
        return matches(request.getRequestURI());
    }

    public boolean matches(String requestPath) {
        for (String pattern: this.exactMatches) {
            if (pattern.equals(requestPath)) {
                return true;
            }
        }
        if (!requestPath.startsWith("/")) {
            return false;
        }
        for (String pattern: this.startsWithMatches) {
            if (requestPath.startsWith(pattern)) {
                return true;
            }
        }
        return false;
    }
}