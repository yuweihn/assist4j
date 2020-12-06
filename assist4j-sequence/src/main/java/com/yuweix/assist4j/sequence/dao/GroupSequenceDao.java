package com.yuweix.assist4j.sequence.dao;


import java.util.List;
import javax.sql.DataSource;

import org.springframework.util.Assert;


/**
 * @author yuwei
 */
public class GroupSequenceDao extends AbstractGroupSequenceDao {
	private List<DataSource> dataSources;

	public GroupSequenceDao() {
		
	}

	@Override
	public void init() {
		Assert.notEmpty(dataSources, "[Assertion failed] - this dataSources must not be empty: it must contain at least 1 element");
		super.init();
	}


	public void setDataSources(List<DataSource> dataSources) {
		this.dataSources = dataSources;
	}

	@Override
	protected int getSegmentCount() {
		return dataSources.size();
	}

	@Override
	protected DataSource getDataSource(int index) {
		return dataSources.get(index);
	}
}
