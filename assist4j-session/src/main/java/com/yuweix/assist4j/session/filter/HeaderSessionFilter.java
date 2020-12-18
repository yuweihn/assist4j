package com.yuweix.assist4j.session.filter;


import com.yuweix.assist4j.session.cache.SessionCache;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;


/**
 * 从header中取出某个值作为sessionId
 * @author yuwei
 */
public class HeaderSessionFilter extends AbstractSessionFilter {
	protected String key;


	public HeaderSessionFilter(SessionCache cache) {
		super(cache);
	}
	public HeaderSessionFilter() {
		super();
	}

	public void setKey(String key) {
		this.key = key;
	}


	@Override
	protected String getSessionId(HttpServletRequest request, HttpServletResponse response) {
		if (key == null || "".equals(key)) {
			throw new RuntimeException("[key]不能为空！！！");
		}

		String sid = request.getHeader(key);
		if (sid == null || "".equals(sid.trim())) {
			sid = UUID.randomUUID().toString().replace("-", "");
		}
		response.setHeader(key, sid);
		return sid;
	}
}
