package com.assist4j.schedule;


import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;


/**
 * @author yuwei
 */
public class SingleNodeTaskScheduler extends ThreadPoolTaskScheduler {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(SingleNodeTaskScheduler.class);


	protected LeaderElector leaderElector;
	protected boolean release = true;


	protected Runnable taskWrapper(final Runnable task) {
		return new Runnable() {
			@Override
			public void run() {
				if (leaderElector.acquire()) {
					task.run();
					String localNode = leaderElector.getLocalNode();
					if (release) {
						leaderElector.release();
					}
					log.info("Job executed here, {}", localNode);
				} else {
					String leaderNode = leaderElector.getLeaderNode();
					if (leaderNode == null) {
						log.info("Not leader, job didn't execute!");
					} else {
						log.info("Not leader, job didn't execute! Leader: {}", leaderNode);
					}
				}
			}
		};
	}

	@Override
	public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
		return super.schedule(taskWrapper(task), trigger);
	}

	@Override
	public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
		return super.schedule(taskWrapper(task), startTime);
	}

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
		return super.scheduleAtFixedRate(taskWrapper(task), startTime, period);
	}

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
		return super.scheduleAtFixedRate(taskWrapper(task), period);
	}

	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
		return super.scheduleWithFixedDelay(taskWrapper(task), startTime, delay);
	}

	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
		return super.scheduleWithFixedDelay(taskWrapper(task), delay);
	}

	public void setLeaderElector(LeaderElector leaderElector) {
		this.leaderElector = leaderElector;
	}

	public void setRelease(boolean release) {
		this.release = release;
	}
}
