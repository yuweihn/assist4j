package com.yuweix.assist4j.data.ds.router;


import com.yuweix.assist4j.data.ds.KvPair;
import com.yuweix.assist4j.data.ds.DataSourceCluster;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 数据库从库路由策略(轮询)
 * @author yuwei
 */
public class RoundRobinRouter implements Router {
	private AtomicInteger count = new AtomicInteger(0);
	private DataSourceCluster dsc;



	public RoundRobinRouter() {
		
	}
	public RoundRobinRouter(DataSourceCluster dsc) {
		this.dsc = dsc;
	}


	public void setDsc(DataSourceCluster dsc) {
		this.dsc = dsc;
	}


	@Override
	public KvPair getTargetDataSource() {
		Assert.notNull(dsc, "[dsc] is required.");

		List<KvPair> slaveList = dsc.getSlaveList();
		if (slaveList == null || slaveList.size() <= 0) {
			return dsc.getMaster();
		}

		int index = Math.abs(count.incrementAndGet()) % slaveList.size();
		return slaveList.get(index);
	}
}
