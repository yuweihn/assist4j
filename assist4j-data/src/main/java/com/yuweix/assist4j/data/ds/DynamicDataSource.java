package com.yuweix.assist4j.data.ds;


import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author yuwei
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
	public DynamicDataSource(List<DataSourceCluster> dsClusterList) {
		super();
		Assert.notEmpty(dsClusterList, "[dsClusterList] is required.");
		Map<Object, Object> targetDataSources = new HashMap<Object, Object>();
		for (DataSourceCluster dsCluster: dsClusterList) {
			if (dsCluster.getIsDefault()) {
				this.setDefaultTargetDataSource(dsCluster.getMaster().getValue());
			}
			targetDataSources.put(dsCluster.getMaster().getKey(), dsCluster.getMaster().getValue());
			List<KvPair> slaveList = dsCluster.getSlaveList();
			if (slaveList != null && slaveList.size() > 0) {
				for (KvPair dsSlave: slaveList) {
					targetDataSources.put(dsSlave.getKey(), dsSlave.getValue());
				}
			}
		}
		this.setTargetDataSources(targetDataSources);
	}

	@Override
	protected Object determineCurrentLookupKey() {
		return DataSourceHolder.getDataSource();
	}
}
