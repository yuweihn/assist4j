package com.assist4j.schedule;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author yuwei
 */
public class ZkLeaderElector extends AbstractLeaderElector {
	private static final Logger log = LoggerFactory.getLogger(ZkLeaderElector.class);
	private static final String ZK_NODE_NAME_PRE = "/Schedule_leader_";

	private ZooKeeper zk;


	/**
	 * zookeeper链接地址.
	 * e.g. "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002"
	 */
	private String zkConn;
	/**
	 * zookeeper session timeout in milliseconds
	 */
	private int zkTimeout;
	/**
	 * zookeeper node name
	 */
	private String zkNodeName;



	public ZkLeaderElector(String zkConn, int zkTimeout, String prjNo) {
		this.zkConn = zkConn;
		this.zkTimeout = zkTimeout;
		this.zkNodeName = ZK_NODE_NAME_PRE + prjNo;
	}

	private ZooKeeper getZk() {
		if (null == zk) {
			synchronized (this) {
				if (null == zk) {
					final CountDownLatch latch = new CountDownLatch(1);
					try {
						log.info("Connecting......");
						zk = new ZooKeeper(zkConn, zkTimeout, new Watcher() {
							@Override
							public void process(WatchedEvent event) {
								log.info("ZooKeeper event occurs here: " + event);
								if (event.getState() == Event.KeeperState.SyncConnected) {
									latch.countDown();
									log.info("Connected");
								}
							}
						});
						latch.await(5, TimeUnit.SECONDS);
					} catch (Exception e) {
						log.error("", e);
						throw new RuntimeException(e);
					}
				}
			}
		}

		if (zk == null) {
			throw new RuntimeException("Can't connect zookeeper.");
		}
		return zk;
	}

	@Override
	public boolean acquire() {
		String node = getLocalNode();
		String leaderNode = getLeaderNode();
		if (leaderNode == null) {
			try {
				String path = getZk().create(zkNodeName, node.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
				log.info("Create server node ({} => {})", path, node);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return node.equals(leaderNode);
	}

	@Override
	public void release() {
		try {
			getZk().delete(zkNodeName, -1);
		} catch (InterruptedException | KeeperException e) {
			log.error("", e);
		}
	}

	/**
	 * 获取当前leader
	 */
	@Override
	public String getLeaderNode() {
		byte[] val = null;
		try {
			Stat stat = getZk().exists(zkNodeName, false);
			if (stat == null) {
				return null;
			}
			val = getZk().getData(zkNodeName, false, stat);
		} catch (Exception e) {
			log.error("get " + zkNodeName + " error, ", e);
		}
		return val == null ? null : new String(val);
	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {
		if (zk != null) {
			try {
				zk.close();
			} catch (InterruptedException e) {
				log.error("", e);
			}
			zk = null;
		}
	}
}
