package com.hs.dsch.consts;

public interface DSchClientConsts {
	public static final String DSCH_SERVICE_DEFAULT_HOST = "127.0.0.1";
	public static final Integer DSCH_SERVICE_DEFUALT_PORT = 10012;
	public static final String DSCH_SERVICE_REG_NODE_INF_NAME = "/dsch/registerNode";
	public static final String DSCH_SERVICE_REG_JOB_INF_NAME = "/dsch/registerJob";
	public static final String DSCH_SERVICE_HEALTH_CHECK_INF_NAME = "/dsch/healthCheck";
	public static final String DSCH_SERVICE_GET_COMMAND_INF_NAME = "/dsch/getCommand";
	public static final String DSCH_SERVICE_NODE_HC_INF_NAME = "/dsch/nodeHealthCheck";
	
	public static final Long DSCH_SERVICE_HC_TIMER_DELAY = 1000L;
	public static final Long DSCH_SERVICE_HC_TIMER_PERIOD = 5000L;
}
