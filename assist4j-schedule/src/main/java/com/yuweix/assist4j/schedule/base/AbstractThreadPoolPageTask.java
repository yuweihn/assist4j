package com.yuweix.assist4j.schedule.base;


import java.util.List;
import java.util.concurrent.ExecutorService;


/**
 * @author yuwei
 */
public abstract class AbstractThreadPoolPageTask<T> extends AbstractThreadPoolTask<T> {
	private static final int DEFAULT_MAX_TIMES = -1;


	@Override
	protected void executeInThreadPool(ExecutorService executor) {
		int times = 0;
		int maxTimes = getMaxTimes();

		while (true) {
			boolean stop = maxTimes != DEFAULT_MAX_TIMES && ++times > maxTimes;
			if (stop) {
				break;
			}

			List<T> taskList = findTaskList();
			if (taskList == null || taskList.size() <= 0) {
				break;
			}
			executeInThreadPool(executor, taskList);
		}
	}

	protected int getMaxTimes() {
		return DEFAULT_MAX_TIMES;
	}
}


