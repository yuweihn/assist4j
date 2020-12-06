package com.yuweix.assist4j.data.ds.router;


import com.yuweix.assist4j.data.ds.KvPair;


/**
 * 数据库从库路由策略
 * @author yuwei
 */
public interface Router {
	KvPair getTargetDataSource();
}
