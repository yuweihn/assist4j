package com.yuweix.assist4j.core;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.sun.management.OperatingSystemMXBean;


/**
 * @author yuwei
 */
@SuppressWarnings("restriction")
public abstract class MonitorUtil {
	private static final int FAULT_LENGTH = 10;
	private static final String OS_NAME = System.getProperty("os.name");




	/**
	 * 获取cpu使用率
	 * @param sleep            两次检测cpu的时间间隔(ms)
	 * @return
	 */
	public static double getCpuUsage(long sleep) {
		Assert.isTrue(sleep > 0, "[sleep] must be larger than 0.");
		if (OS_NAME.toLowerCase().contains("windows") || OS_NAME.toLowerCase().contains("win")) {
			return getCpuUsageForWindows(sleep);
		} else {
			return getCpuUsageForLinux(sleep);
		}
	}

	/**
	 * 获取windows环境下cpu的使用率
	 * @return
	 */
	private static double getCpuUsageForWindows(long sleep) {
		try {
			String procCmd = System.getenv("windir")
					+ "//system32//wbem//wmic.exe process get Caption,CommandLine,"
					+ "KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount";
			// 取进程信息
			long[] c0 = readCpuForWindows(Runtime.getRuntime().exec(procCmd));
			Thread.sleep(sleep);
			long[] c1 = readCpuForWindows(Runtime.getRuntime().exec(procCmd));
			if (c0 != null && c1 != null) {
				long idleTime = c1[0] - c0[0];
				long busyTime = c1[1] - c0[1];
				double cpuRate = MathUtil.div(busyTime, busyTime + idleTime);
				if (cpuRate > 1) {
					cpuRate = 1;
				} else if (cpuRate < 0) {
					cpuRate = 0;
				}
				return cpuRate;
			} else {
				return 0;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	/**
	 * 获取linux环境下cpu的使用率
	 * @return
	 */
	private static double getCpuUsageForLinux(long sleep) {
		try {
			Map<String, String> map1 = readCpuForLinux();
			Thread.sleep(sleep);
			Map<String, String> map2 = readCpuForLinux();

			long user1 = Long.parseLong(map1.get("user"));
			long nice1 = Long.parseLong(map1.get("nice"));
			long system1 = Long.parseLong(map1.get("system"));
			long idle1 = Long.parseLong(map1.get("idle"));

			long user2 = Long.parseLong(map2.get("user"));
			long nice2 = Long.parseLong(map2.get("nice"));
			long system2 = Long.parseLong(map2.get("system"));
			long idle2 = Long.parseLong(map2.get("idle"));

			long total1 = user1 + system1 + nice1;
			long total2 = user2 + system2 + nice2;
			long total = total2 - total1;

			long totalIdle1 = user1 + nice1 + system1 + idle1;
			long totalIdle2 = user2 + nice2 + system2 + idle2;
			long totalIdle = totalIdle2 - totalIdle1;

			double cpuRate = MathUtil.div(total, totalIdle);
			if (cpuRate > 1) {
				cpuRate = 1;
			} else if (cpuRate < 0) {
				cpuRate = 0;
			}
			return cpuRate;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return 0;
		}
	}

	private static Map<String, String> readCpuForLinux() {
		InputStreamReader inputs = null;
		BufferedReader buffer = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			inputs = new InputStreamReader(new FileInputStream("/proc/stat"));
			buffer = new BufferedReader(inputs);
			String line = "";
			while (true) {
				line = buffer.readLine();
				if (line == null) {
					break;
				}
				if (line.startsWith("cpu")) {
					StringTokenizer tokenizer = new StringTokenizer(line);
					List<String> temp = new ArrayList<String>();
					while (tokenizer.hasMoreElements()) {
						String value = tokenizer.nextToken();
						temp.add(value);
					}
					map.put("user", temp.get(1));
					map.put("nice", temp.get(2));
					map.put("system", temp.get(3));
					map.put("idle", temp.get(4));
					map.put("iowait", temp.get(5));
					map.put("irq", temp.get(6));
					map.put("softirq", temp.get(7));
					map.put("stealstolen", temp.get(8));
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				buffer.close();
			} catch (Exception e) {
			}
			try {
				inputs.close();
			} catch (Exception e) {
			}
		}
		return map;
	}

	/**
	 * window读取cpu相关信息
	 * @param proc
	 * @return
	 */
	private static long[] readCpuForWindows(final Process proc) {
		long[] retn = new long[2];
		try {
			proc.getOutputStream().close();
			InputStreamReader ir = new InputStreamReader(proc.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			String line = input.readLine();
			if (line == null || line.length() < FAULT_LENGTH) {
				return null;
			}
			int capidx = line.indexOf("Caption");
			int cmdidx = line.indexOf("CommandLine");
			int rocidx = line.indexOf("ReadOperationCount");
			int umtidx = line.indexOf("UserModeTime");
			int kmtidx = line.indexOf("KernelModeTime");
			int wocidx = line.indexOf("WriteOperationCount");
			// Caption,CommandLine,KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount
			long idleTime = 0;
			long knelTime = 0;
			long userTime = 0;
			while ((line = input.readLine()) != null) {
				if (line.length() < wocidx) {
					continue;
				}
				// 字段出现顺序：Caption,CommandLine,KernelModeTime,ReadOperationCount,
				// ThreadCount,UserModeTime,WriteOperation
				String caption = substring(line, capidx, cmdidx - 1).trim();
				String cmd = substring(line, cmdidx, kmtidx - 1).trim();
				if (cmd.indexOf("javaw.exe") >= 0) {
					continue;
				}
				// log.info("line="+line);
				if (caption.equals("System Idle Process") || caption.equals("System")) {
					idleTime += Long.valueOf(substring(line, kmtidx, rocidx - 1).trim()).longValue();
					idleTime += Long.valueOf(substring(line, umtidx, wocidx - 1).trim()).longValue();
					continue;
				}

				knelTime += Long.valueOf(substring(line, kmtidx, rocidx - 1).trim()).longValue();
				userTime += Long.valueOf(substring(line, umtidx, wocidx - 1).trim()).longValue();
			}
			retn[0] = idleTime;
			retn[1] = knelTime + userTime;
			return retn;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				proc.getInputStream().close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private static String substring(String src, int start_idx, int end_idx) throws UnsupportedEncodingException {
		byte[] b = src.getBytes("utf-8");
		String tgt = "";
		for (int i = start_idx; i <= end_idx; i++) {
			tgt += (char) b[i];
		}
		return tgt;
	}


	/**
	 * 获取内存使用率
	 * @return
	 */
	public static double getMemUsage() {
		if (OS_NAME.toLowerCase().contains("windows") || OS_NAME.toLowerCase().contains("win")) {
			return getMemUsageForWindows();
		} else {
			return getMemUsageForLinux();
		}
	}

	private static double getMemUsageForWindows() {
		try {
			OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
			// 总的物理内存+虚拟内存
			long totalVirtualMemory = osmxb.getTotalSwapSpaceSize();
			// 剩余的物理内存
			long freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize();
			return MathUtil.div(totalVirtualMemory - freePhysicalMemorySize, totalVirtualMemory);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private static double getMemUsageForLinux() {
		Map<String, Object> map = new HashMap<String, Object>();
		InputStreamReader inputs = null;
		BufferedReader buffer = null;
		try {
			inputs = new InputStreamReader(new FileInputStream("/proc/meminfo"));
			buffer = new BufferedReader(inputs);
			String line = "";
			while (true) {
				line = buffer.readLine();
				if (line == null) {
					break;
				}
				int beginIndex = 0;
				int endIndex = line.indexOf(":");
				if (endIndex != -1) {
					String key = line.substring(beginIndex, endIndex);
					beginIndex = endIndex + 1;
					endIndex = line.length();
					String memory = line.substring(beginIndex, endIndex);
					String value = memory.replace("kB", "").trim();
					map.put(key, value);
				}
			}

			long memTotal = Long.parseLong(map.get("MemTotal").toString());
			long memFree = Long.parseLong(map.get("MemFree").toString());
			long memUsed = memTotal - memFree;
			long buffers = Long.parseLong(map.get("Buffers").toString());
			long cached = Long.parseLong(map.get("Cached").toString());

			return MathUtil.div(memUsed - buffers - cached, memTotal);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			try {
				buffer.close();
			} catch (Exception e) {
			}
			try {
				inputs.close();
			} catch (Exception e) {
			}
		}
	}


	/**
	 * 获取磁盘使用率
	 * @return
	 */
	public static double getDiskUsage() {
		if (OS_NAME.toLowerCase().contains("windows") || OS_NAME.toLowerCase().contains("win")) {
			return getDiskUsageForWindows();
		} else {
			return getDiskUsageForLinux();
		}
	}

	private static double getDiskUsageForWindows() {
		long allTotal = 0;
		long allFree = 0;
		for (char c = 'A'; c <= 'Z'; c++) {
			String dirName = c + ":/";
			File win = new File(dirName);
			if (win.exists()) {
				allTotal = allTotal + win.getTotalSpace();
				allFree = allFree + win.getFreeSpace();
			}
		}
		return MathUtil.div(allTotal - allFree, allTotal);
	}

	private static double getDiskUsageForLinux() {
		double totalHD = 0;
		double usedHD = 0;
		BufferedReader in = null;
		try {
			Runtime rt = Runtime.getRuntime();
			Process p = rt.exec("df -hl");// df -hl 查看硬盘空间
			in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String str = null;
			String[] strArray = null;
			while ((str = in.readLine()) != null) {
				int m = 0;
				strArray = str.split(" ");
				for (String tmp : strArray) {
					if (tmp.trim().length() == 0) {
						continue;
					}
					++m;
					if (tmp.indexOf("G") != -1) {
						if (m == 2) {
							if (!tmp.equals("") && !tmp.equals("0")) {
								totalHD += Double.parseDouble(tmp.substring(0, tmp.length() - 1)) * 1024;
							}
						}
						if (m == 3) {
							if (!tmp.equals("none") && !tmp.equals("0")) {
								usedHD += Double.parseDouble(tmp.substring(0, tmp.length() - 1)) * 1024;
							}
						}
					}
					if (tmp.indexOf("M") != -1) {
						if (m == 2) {
							if (!tmp.equals("") && !tmp.equals("0")) {
								totalHD += Double.parseDouble(tmp.substring(0, tmp.length() - 1));
							}
						}
						if (m == 3) {
							if (!tmp.equals("none") && !tmp.equals("0")) {
								usedHD += Double.parseDouble(tmp.substring(0, tmp.length() - 1));
							}
						}
					}
				}
			}
			return MathUtil.div(usedHD, totalHD);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 获取网口的上下行速率(B/s)
	 * @param sleep            测网速时线程睡眠时间(ms)
	 * @return
	 */
	public static NetSpeed getNetworkThroughput(long sleep) {
		Assert.isTrue(sleep > 0, "[sleep] must be larger than 0.");
		if (OS_NAME.toLowerCase().contains("windows") || OS_NAME.toLowerCase().contains("win")) {
			return getNetworkThroughputForWindows(sleep);
		} else {
			return getNetworkThroughputForLinux(sleep);
		}
	}

	private static NetSpeed getNetworkThroughputForWindows(long sleep) {
		Process pro1 = null;
		Process pro2 = null;
		Runtime r = Runtime.getRuntime();
		BufferedReader input1 = null;
		BufferedReader input2 = null;
		try {
			String command = "netstat -e";
			pro1 = r.exec(command);
			input1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
			NetDataBytes ndb1 = readInLine(input1, "windows");
			Thread.sleep(sleep);
			pro2 = r.exec(command);
			input2 = new BufferedReader(new InputStreamReader(pro2.getInputStream()));
			NetDataBytes ndb2 = readInLine(input2, "windows");
			double rx = MathUtil.div((ndb2.down - ndb1.down) * 1000, sleep);
			double tx = MathUtil.div((ndb2.up - ndb1.up) * 1000, sleep);
			return new NetSpeed(rx, tx);
		} catch (Exception e) {
			e.printStackTrace();
			return new NetSpeed(0, 0);
		} finally {
			try {
				input1.close();
			} catch (IOException e) {
			}
			try {
				input2.close();
			} catch (IOException e) {
			}
			pro1.destroy();
			pro2.destroy();
		}
	}

	private static NetSpeed getNetworkThroughputForLinux(long sleep) {
		Process pro1 = null;
		Process pro2 = null;
		Runtime r = Runtime.getRuntime();
		BufferedReader input1 = null;
		BufferedReader input2 = null;
		try {
			String command = "watch ifconfig";
			pro1 = r.exec(command);
			input1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
			NetDataBytes ndb1 = readInLine(input1, "linux");
			Thread.sleep(sleep);
			pro2 = r.exec(command);
			input2 = new BufferedReader(new InputStreamReader(pro2.getInputStream()));
			NetDataBytes ndb2 = readInLine(input2, "linux");
			double rx = MathUtil.div((ndb2.down - ndb1.down) * 1000, sleep);
			double tx = MathUtil.div((ndb2.up - ndb1.up) * 1000, sleep);
			return new NetSpeed(rx, tx);
		} catch (Exception e) {
			e.printStackTrace();
			return new NetSpeed(0, 0);
		} finally {
			try {
				input1.close();
			} catch (IOException e) {
			}
			try {
				input2.close();
			} catch (IOException e) {
			}
			pro1.destroy();
			pro2.destroy();
		}
	}

	private static NetDataBytes readInLine(BufferedReader input, String osType) {
		String rxResult = "";
		String txResult = "";
		StringTokenizer tokenStat = null;
		try {
			if ("linux".equalsIgnoreCase(osType)) {
				String result[] = input.readLine().split(" ");
				int j = 0, k = 0;
				for (int i = 0; i < result.length; i++) {
					if (result[i].indexOf("RX") != -1) {
						j++;
						if (j == 2) {
							rxResult = result[i + 1].split(":")[1];
						}
					}
					if (result[i].indexOf("TX") != -1) {
						k++;
						if (k == 2) {
							txResult = result[i + 1].split(":")[1];
							break;
						}
					}
				}
			} else {
				input.readLine();
				input.readLine();
				input.readLine();
				input.readLine();
				tokenStat = new StringTokenizer(input.readLine());
				tokenStat.nextToken();
				rxResult = tokenStat.nextToken();
				txResult = tokenStat.nextToken();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new NetDataBytes(Long.parseLong(txResult), Long.parseLong(rxResult));
	}

	private static class NetDataBytes {
		private long up;
		private long down;
		private NetDataBytes(long up, long down) {
			this.up = up;
			this.down = down;
		}
	}
	public static class NetSpeed {
		private Date time;
		private double up;
		private double down;
		private NetSpeed(double up, double down) {
			this.time = new Date();
			this.up = up;
			this.down = down;
		}
		public Date getTime() {
			return time;
		}
		public double getUp() {
			return up;
		}
		public double getDown() {
			return down;
		}
		@Override
		public String toString() {
			return JSON.toJSONString(NetSpeed.this);
		}
	}
}

