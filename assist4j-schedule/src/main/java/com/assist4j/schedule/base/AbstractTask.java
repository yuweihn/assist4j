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
		public String acquire(String lock) {
			return getLocalNode(lock);
		}
		@Override
		public void release(String lock) {
			//NO-OP
		}
		@Override
		public String getLocalNode(String lock) {
			return "Local";
		}
	};

	public AbstractTask() {

	}

	public void execute() {
		long startTime = System.currentTimeMillis();
		LeaderElector leaderElector = getElector();
		String lockName = getLockName();
		boolean release  = getRelease();
		String localNode = leaderElector.getLocalNode(lockName);
		String leaderNode = leaderElector.acquire(lockName);
		if (localNode != null && localNode.equals(leaderNode)) {
			before();
			executeTask();
			if (release) {
				leaderElector.release(lockName);
			}
			after();
			long timeCost = System.currentTimeMillis() - startTime;
			log.info("Job executed here, JobName: {}, LocalNode: {}, TimeCost: {}s"
					, this.getClass().getName(), localNode, timeCost / 1000.0);
		} else {
			log.info("Not leader, job didn't execute! JobName: {}, Leader: {}", this.getClass().getName(), leaderNode);
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
	public boolean getRelease() {
		return true;
	}
	protected LeaderElector getLeaderElector() {
		return null;
	}
	private LeaderElector getElector() {
		LeaderElector leaderElector = getLeaderElector();
		return leaderElector != null ? leaderElector : DEFAULT_LEADER_ELECTOR;
	}
}
