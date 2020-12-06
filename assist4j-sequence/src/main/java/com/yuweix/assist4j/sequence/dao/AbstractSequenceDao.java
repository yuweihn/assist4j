package com.yuweix.assist4j.sequence.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.sql.DataSource;

import com.yuweix.assist4j.sequence.exception.SequenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;


/**
 * @author yuwei
 */
public abstract class AbstractSequenceDao implements SequenceDao {
	private static final Logger log = LoggerFactory.getLogger(AbstractSequenceDao.class);


	private int retryTimes = 2;
	private int innerStep = 100;
	private String tableName;
	private String segmentColumnName;
	private String nameColumnName;
	private String valueColumnName;
	private String createTimeColumnName;
	private String updateTimeColumnName;

	public AbstractSequenceDao() {
		this.tableName = "sequence";
		this.segmentColumnName = "segment";
		this.nameColumnName = "name";
		this.valueColumnName = "current_value";
		this.createTimeColumnName = "create_time";
		this.updateTimeColumnName = "update_time";
	}

	public int getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(int retryTimes) {
		Assert.isTrue(retryTimes >= 0, "Property retryTimes cannot be less than zero, retryTimes = " + retryTimes);
		this.retryTimes = retryTimes;
	}

	public int getInnerStep() {
		return innerStep;
	}

	public void setInnerStep(int innerStep) {
		/**
		 * 步长取值在1-100000之间
		 */
		Assert.isTrue(innerStep >= 1 && innerStep <= 100000, "Property step out of range [" + 1 + "," + 100000 + "], step = " + innerStep);
		this.innerStep = innerStep;
	}

	@Override
	public void init() {

	}
	@Override
	public void destroy() {

	}


	private String getSeqInsertSql() {
		StringBuilder buf = new StringBuilder("");
		buf.append("insert into ")
			.append(tableName)
			.append("(")
			.append(segmentColumnName)
			.append(",")
			.append(nameColumnName)
			.append(",")
			.append(valueColumnName)
			.append(",")
			.append(createTimeColumnName)
			.append(",")
			.append(updateTimeColumnName)
			.append(") values(?,?,?,?,?);");
		return buf.toString();
	}
	private String getSeqUpdateSql() {
		StringBuilder buf = new StringBuilder("");
		buf.append("update ")
			.append(tableName)
			.append(" set ").append(valueColumnName).append(" = ? ,")
			.append(updateTimeColumnName).append(" = ? ")
			.append(" where ")
			.append(segmentColumnName).append(" = ? and ")
			.append(nameColumnName).append(" = ? and ")
			.append(valueColumnName).append(" = ? ");
		return buf.toString();
	}
	private String getSeqSelectSql() {
		StringBuilder buf = new StringBuilder("");
		buf.append("select ").append(valueColumnName)
			.append(" from ").append(tableName)
			.append(" where ")
			.append(segmentColumnName).append(" = ? and ")
			.append(nameColumnName).append(" = ?");
		return buf.toString();
	}
	
	private void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
				log.error("Could not close JDBC ResultSet", e);
			}
		}
	}
	private void closeStatement(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
				log.error("Could not close JDBC Statement", e);
			}
		}
	}
	private void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				log.error("Could not close JDBC Connection", e);
			}
		}
	}

	protected abstract DataSource getDataSource(int segment);
	
	protected int insertSeq(int segment, String seqName, long initValue) {
		return insertSeq(getDataSource(segment), segment, seqName, initValue);
	}
	private int insertSeq(DataSource dataSource, int segment, String seqName, long initValue) {
		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement(getSeqInsertSql());
			stmt.setInt(1, segment);
			stmt.setString(2, seqName);
			stmt.setLong(3, initValue);
			Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
			stmt.setTimestamp(4, timeStamp);
			stmt.setTimestamp(5, timeStamp);
			int affectedRows = stmt.executeUpdate();
			if (affectedRows <= 0) {
				throw new SequenceException("Failed to init value at " + seqName + " update affectedRow = 0");
			}

			if (log.isDebugEnabled()) {
				log.debug("插入初值:" + seqName + ", value:" + initValue);
			}
			return affectedRows;
		} catch (Exception e) {
			throw new SequenceException("插入初值值失败！sequence Name：" + seqName + "   value:" + initValue, e);
		} finally {
			closeStatement(stmt);
			closeConnection(conn);
		}
	}

	protected int updateSeqValue(int segment, String seqName, long oldValue, long newValue) {
		return updateSeqValue(getDataSource(segment), segment, seqName, oldValue, newValue);
	}
	private int updateSeqValue(DataSource dataSource, int segment, String seqName, long oldValue, long newValue) {
		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement(getSeqUpdateSql());
			stmt.setLong(1, newValue);
			stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			stmt.setInt(3, segment);
			stmt.setString(4, seqName);
			stmt.setLong(5, oldValue);
			int affectedRows = stmt.executeUpdate();
			if (affectedRows <= 0) {
				throw new SequenceException("Failed to update value at " + seqName + " update affectedRow = 0");
			}
			return affectedRows;
		} catch (Exception e) {
			throw new SequenceException(e);
		} finally {
			closeStatement(stmt);
			closeConnection(conn);
		}
	}

	protected Long selectSeqValue(int segment, String seqName) {
		return selectSeqValue(getDataSource(segment), segment, seqName);
	}
	private Long selectSeqValue(DataSource dataSource, int segment, String seqName) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement(getSeqSelectSql());
			stmt.setInt(1, segment);
			stmt.setString(2, seqName);
			rs = stmt.executeQuery();
			boolean hasNext = rs.next();
			return hasNext ? rs.getLong(1) : null;
		} catch (Exception e) {
			log.error("", e);
			return null;
		} finally {
			closeResultSet(rs);
			closeStatement(stmt);
			closeConnection(conn);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		if (tableName == null || "".equals(tableName.trim())) {
			return;
		}
		this.tableName = tableName.trim();
	}

	public String getNameColumnName() {
		return nameColumnName;
	}

	public void setNameColumnName(String nameColumnName) {
		if (nameColumnName == null || "".equals(nameColumnName.trim())) {
			return;
		}
		this.nameColumnName = nameColumnName.trim();
	}

	public String getValueColumnName() {
		return valueColumnName;
	}

	public void setValueColumnName(String valueColumnName) {
		if (valueColumnName == null || "".equals(valueColumnName.trim())) {
			return;
		}
		this.valueColumnName = valueColumnName.trim();
	}

	public String getSegmentColumnName() {
		return segmentColumnName;
	}

	public void setSegmentColumnName(String segmentColumnName) {
		if (segmentColumnName == null || "".equals(segmentColumnName.trim())) {
			return;
		}
		this.segmentColumnName = segmentColumnName.trim();
	}

	public String getCreateTimeColumnName() {
		return createTimeColumnName;
	}

	public void setCreateTimeColumnName(String createTimeColumnName) {
		if (createTimeColumnName == null || "".equals(createTimeColumnName.trim())) {
			return;
		}
		this.createTimeColumnName = createTimeColumnName.trim();
	}

	public String getUpdateTimeColumnName() {
		return updateTimeColumnName;
	}

	public void setUpdateTimeColumnName(String updateTimeColumnName) {
		if (updateTimeColumnName == null || "".equals(updateTimeColumnName.trim())) {
			return;
		}
		this.updateTimeColumnName = updateTimeColumnName.trim();
	}
}
