package com.assist4j.schedule.base;


import com.assist4j.schedule.LeaderElector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author yuwei
 */
public abstract class AbstractTask {
	private static final Logger log = LoggerFactory.getLogger(AbstractTask.class);
	private static final LeaderElector DEFAULT_LEADER_ELECTOR = new LeaderElector() {
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

	private final LeaderElector leaderElector;
	private final String lockName;

	public AbstractTask() {
		this.leaderElector = getElector();
		this.lockName = getLockName();
	}

	public void execute() {
		long startTime = System.currentTimeMillis();
		if (leaderElector.acquire(lockName)) {
			before();
			executeTask();
			after();
			long timeCost = System.currentTimeMillis() - startTime;
			log.info("Job executed here, JobName: {}, TimeCost: {}s", this.getClass().getName(), timeCost / 1000.0);
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
		leaderElector.release(lockName);
	}
	protected abstract void executeTask();
	protected String getLockName() {
		return this.getClass().getName();
	}
	protected LeaderElector getLeaderElector() {
		return null;
	}
	private LeaderElector getElector() {
		LeaderElector leaderElector = getLeaderElector();
		return leaderElector != null ? leaderElector : DEFAULT_LEADER_ELECTOR;
	}
}
