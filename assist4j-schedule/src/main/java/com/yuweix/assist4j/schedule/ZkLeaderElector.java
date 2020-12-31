package com.yuweix.assist4j.schedule;


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
	private static final String ZK_NODE_NAME_PRE = "/Schedule_leader%s_";

	private static ZooKeeper zk;


	/**
	 * zookeeper链接地址.
	 * e.g. "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002"
	 */
	private String zkConn;
	/**
	 * zookeeper session timeout in milliseconds
	 */
	private int zkTimeout;

	private String appName;



	public ZkLeaderElector(String zkConn, int zkTimeout) {
		this(zkConn, zkTimeout, null);
	}
	public ZkLeaderElector(String zkConn, int zkTimeout, String appName) {
		super();
		this.zkConn = zkConn;
		this.zkTimeout = zkTimeout;
		this.appName = appName;
	}

	private ZooKeeper getZk() {
		if (null == zk) {
			synchronized (ZkLeaderElector.class) {
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
	public String acquire(String lock) {
		String key = String.format(ZK_NODE_NAME_PRE
				, this.appName == null || "".equals(this.appName.trim()) ? "" : "_" + this.appName.trim()) + lock;
		String node = getLocalNode();
		String leaderNode = getNodeValue(key);
		if (leaderNode == null) {
			try {
				String path = getZk().create(key, node.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
				log.info("Create server node ({} => {})", path, node);
				return node;
			} catch (Exception e) {
				log.error("{}", e.getMessage());
			}
		}
		return getNodeValue(key);
	}

	@Override
	public void release(String lock) {
		String key = String.format(ZK_NODE_NAME_PRE
				, this.appName == null || "".equals(this.appName.trim()) ? "" : "_" + this.appName.trim()) + lock;
		if (zk == null) {
			return;
		}
		String node = getLocalNode();
		String leaderNode = getNodeValue(key);
		if (node == null || !node.equals(leaderNode)) {
			return;
		}
		try {
			zk.delete(key, -1);
		} catch (InterruptedException | KeeperException e) {
			log.warn("", e);
		}
	}

	/**
	 * 获取指定节点的值，如果节点不存在，返回null
	 */
	private String getNodeValue(String key) {
		byte[] val = null;
		try {
			Stat stat = getZk().exists(key, false);
			if (stat == null) {
				return null;
			}
			val = getZk().getData(key, false, stat);
		} catch (Exception e) {
			log.warn("get " + key + " error, ", e);
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
