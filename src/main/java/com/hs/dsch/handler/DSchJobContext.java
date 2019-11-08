package com.hs.dsch.handler;

public class DSchJobContext {
	private String nodeId;
	private String jobId;
	private String jobName;
	private Long beginTime;
	private Long endTime;
	private String cron;
	private Long fixDelay;
	private Long fixRate;
	private Long initialDelay;
	
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public Long getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(Long beginTime) {
		this.beginTime = beginTime;
	}
	public Long getEndTime() {
		return endTime;
	}
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}
	public String getCron() {
		return cron;
	}
	public void setCron(String cron) {
		this.cron = cron;
	}
	public Long getFixDelay() {
		return fixDelay;
	}
	public void setFixDelay(Long fixDelay) {
		this.fixDelay = fixDelay;
	}
	public Long getFixRate() {
		return fixRate;
	}
	public void setFixRate(Long fixRate) {
		this.fixRate = fixRate;
	}
	public Long getInitialDelay() {
		return initialDelay;
	}
	public void setInitialDelay(Long initialDelay) {
		this.initialDelay = initialDelay;
	}
}
