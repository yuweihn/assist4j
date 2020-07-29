package com.assist4j.schedule.base;


import com.assist4j.schedule.LeaderElector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author yuwei
 */
public abstract class AbstractTask {
	private static final Logger log = LoggerFactory.getLogger(AbstractTask.class);

	public AbstractTask() {
		
	}

	public void execute() {
		long startTime = System.currentTimeMillis();
		String lockName = getLockName();
		LeaderElector leaderElector= getLeaderElector();
		if (leaderElector == null) {
			leaderElector = getDefaultLeaderElector();
		}
		boolean release = getRelease();
		if (leaderElector.acquire(lockName)) {
			before();
			executeTask();
			after();
			if (release) {
				leaderElector.release(lockName);
			}
			long timeCost = System.currentTimeMillis() - startTime;
			log.info("Job executed here, JobName: {}, TimeCost: {}s"
					, this.getClass().getName(), timeCost / 1000.0);
		} else {
			String leaderNode = leaderElector.getLeaderNode(lockName);
			if (leaderNode == null) {
				log.info("Not leader, job didn't execute! JobName: {}", this.getClass().getName());
			} else {
				log.info("Not leader, job didn't execute! JobName: {}, Leader: {}", this.getClass().getName(), leaderNode);
			}
		}
	}

	protected void before() {

	}
	protected void after() {
		
	}
	protected abstract void executeTask();
	protected String getLockName() {
		return this.getClass().getName();
	}
	protected LeaderElector getLeaderElector() {
		return null;
	}
	private LeaderElector getDefaultLeaderElector() {
		return new LeaderElector() {
			@Override
			public boolean acquire(String lock) {
				return true;
			}

			@Override
			public void release(String lock) {

			}

			@Override
			public String getLocalNode(String lock) {
				return "Local";
			}

			@Override
			public String getLeaderNode(String lock) {
				return "Local";
			}
		};
	}
	protected boolean getRelease() {
		return true;
	}
}
