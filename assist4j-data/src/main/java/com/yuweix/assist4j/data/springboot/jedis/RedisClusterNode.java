package com.yuweix.assist4j.data.springboot.jedis;


import redis.clients.jedis.HostAndPort;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * @author yuwei
 */
public abstract class RedisClusterNode {
	private static final Pattern pattern = Pattern.compile("^.+[:]\\d{1,5}\\s*$");

	protected abstract List<String> getNodeList();

	public List<HostAndPort> getHostAndPortList() {
		List<HostAndPort> list = new ArrayList<HostAndPort>();
		List<String> nodes = getNodeList();
		if (nodes == null || nodes.size() <= 0) {
			return list;
		}

		for (String node: nodes) {
			boolean isIpPort = pattern.matcher(node).matches();
			if (!isIpPort) {
				throw new IllegalArgumentException("ip 或 port 不合法");
			}

			String[] ipAndPort = node.split(":");
			HostAndPort hap = new HostAndPort(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
			list.add(hap);
		}
		return list;
	}
}
