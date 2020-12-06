package com.yuweix.assist4j.sequence.dao;


import javax.sql.DataSource;
import org.springframework.util.Assert;


/**
 * @author yuwei
 */
public class SegmentSequenceDao extends AbstractGroupSequenceDao {
	/**
	 * 逻辑库
	 */
	private DataSource dataSource;
	/**
	 * 物理分片个数
	 */
	private int segmentCount = 1;

	public SegmentSequenceDao() {
		
	}

	@Override
	public void init() {
		Assert.notNull(dataSource, "The dataSource is null.");
		super.init();
	}

	public void setSegmentCount(int segmentCount) {
		Assert.isTrue(segmentCount > 0, "Property segmentCount cannot be less than 1, segmentCount = " + segmentCount);
		this.segmentCount = segmentCount;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	protected int getSegmentCount() {
		return segmentCount;
	}

	@Override
	protected DataSource getDataSource(int index) {
		return dataSource;
	}
}
