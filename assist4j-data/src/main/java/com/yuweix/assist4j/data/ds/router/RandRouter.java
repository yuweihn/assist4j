package com.yuweix.assist4j.data.ds.router;


import com.yuweix.assist4j.data.ds.KvPair;
import com.yuweix.assist4j.data.ds.DataSourceCluster;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Random;


/**
 * 数据库从库路由策略(随机选择)
 * @author yuwei
 */
public class RandRouter implements Router {
	private DataSourceCluster dsc;



	public RandRouter() {
		
	}
	public RandRouter(DataSourceCluster dsc) {
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
		return slaveList.get(new Random().nextInt(slaveList.size()));
	}
}
