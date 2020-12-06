package com.assist4j.web;


import com.yuweix.assist4j.core.Constant;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


/**
 * 分页组件
 * @author yuwei
 */
public class Page {
	/**
	 * 当前页
	 */
	private int currentPage;
	/**
	 * 每页显示的记录条数
	 */
	private int pageSize;
	/**
	 * 记录总行数
	 */
	private int rowCount;
	/**
	 * 页码个数
	 */
	private int num;
	/**
	 * 总页数
	 */
	private int pageCount;
	/**
	 * 显示的页码
	 */
	private List<Integer> pageNos;
	private String url;
	private boolean hasPrev;
	private boolean hasNext;


	public Page(int rowCount, int currentPage, int pageSize, HttpServletRequest request) {
		this(rowCount, currentPage, pageSize, 10, request);
	}
	public Page(int rowCount, int currentPage, int pageSize, int num, HttpServletRequest request) {
		if (rowCount <= 0) {
			rowCount = 0;
		}
		if (currentPage <= 0) {
			currentPage = 1;
		}
		if (pageSize <= 0) {
			pageSize = 10;
		}
		if (num <= 0) {
			num = 10;
		}

		this.rowCount = rowCount;
		this.currentPage = currentPage;
		this.pageSize = pageSize;
		this.num = num;

		this.pageCount = rowCount % pageSize == 0 ? rowCount / pageSize : rowCount / pageSize + 1;
		this.hasPrev = this.currentPage > 1;
		this.hasNext = this.currentPage < this.pageCount;
		this.url = initURL(request);
		this.pageNos = initPageNos();
	}

	private List<Integer> initPageNos() {
		List<Integer> list = new ArrayList<Integer>();
		/**
		 * 如果当前页currentPage超过合法范围(1~pageCount)，则返回空
		 **/
		if (this.currentPage < 1 || this.currentPage > this.pageCount) {
			return list;
		}
		/**
		 * 确定起止页码
		 **/
		int start = 0;
		int end = 0;
		if (this.num >= this.pageCount) {
			start = 1;
			end = this.pageCount;
		} else {
			start = this.currentPage - this.num / 2;
			if (start < 1) {
				start = 1;
			}
			end = start + this.num - 1;
			if (end > this.pageCount) {
				end = this.pageCount;
				start = end - this.num + 1;
			}
		}

		for (int i = start; i <= end; i++) {
			list.add(i);
		}
		return list;
	}

	private String initURL(HttpServletRequest request) {
		StringBuilder buf = new StringBuilder("");
		buf.append(request.getRequestURI()).append("?");

		Enumeration<?> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			if ("pageNo".equals(paramName)) {
				continue;
			}
			if ("pageSize".equals(paramName)) {
				continue;
			}

			String[] paramValues = request.getParameterValues(paramName);
			for (String paramValue: paramValues) {
				if (paramValue == null || "".equals(paramValue)) {
					continue;
				}
				try {
					paramValue = URLEncoder.encode(paramValue, Constant.ENCODING_UTF_8);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				buf.append(paramName).append("=").append(paramValue).append("&");
			}
		}
		if (buf.substring(buf.length() - 1).equals("&")) {
			buf.deleteCharAt(buf.length() - 1);
		}
		return buf.toString();
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public List<Integer> getPageNos() {
		return pageNos;
	}

	public void setPageNos(List<Integer> pageNos) {
		this.pageNos = pageNos;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public boolean isHasPrev() {
		return hasPrev;
	}

	public void setHasPrev(boolean hasPrev) {
		this.hasPrev = hasPrev;
	}

	public boolean isHasNext() {
		return hasNext;
	}

	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}

	public int getTotalPage() {
		return this.pageCount;
	}
}
