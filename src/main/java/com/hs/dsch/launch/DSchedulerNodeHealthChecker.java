package com.hs.dsch.launch;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.dsch.conf.DSchConfiguration;
import com.hs.dsch.conf.DSchContext;
import com.hs.dsch.consts.DSchClientConsts;
import com.hs.dsch.proto.DSchAdminProto.DSchNode;
import com.hs.dsch.proto.DSchAdminProto.DSchNodeHealthCheckRequest;
import com.hs.dsch.proto.DSchAdminProto.DSchNodeHealthCheckResponse;
import com.hs.dsch.proto.DSchAdminProto.DSchResponseCode;
import com.hs.dsch.util.HttpClient;

public class DSchedulerNodeHealthChecker {
	private static Logger logger = LoggerFactory.getLogger(DSchedulerNodeHealthChecker.class);
	
	private Timer nodeHealthChecker = new Timer();
	
	private HttpClient httpClient = DSchContext.getInstance().getHttpClient();
	private DSchConfiguration dschConfiguration = DSchContext.getInstance().getDSchConfiguration();
	
	public void scheduleTimer() {
		nodeHealthChecker.schedule(new TimerTask() {

			@Override
			public void run() {
				logger.info("节点健康检查");
				
				DSchNodeHealthCheckRequest.Builder builder = DSchNodeHealthCheckRequest.newBuilder();
				builder.setNodeId(DSchContext.getInstance().getNodeId());
				builder.setNode(DSchNode.newBuilder().setNodeId(DSchContext.getInstance().getNodeId()).setUpdateTime(System.currentTimeMillis()).setStatus(0));
				
				try {
					HttpResponse httpResponse = httpClient.post(dschConfiguration.getHost() , dschConfiguration.getPort() ,
						DSchClientConsts.DSCH_SERVICE_NODE_HC_INF_NAME , builder.build().toByteArray());
					DSchNodeHealthCheckResponse response = DSchNodeHealthCheckResponse.parseFrom(httpResponse.getEntity().getContent());
					if (response.getResCode() == DSchResponseCode.RESP_CODE_FAILED) {
						logger.error("注册健康检查失败,{}/{}/{}" , DSchContext.getInstance().getNodeId());
					}
				} catch (Exception e) {
					logger.error("节点健康检查发生异常：{}" , e);
				}
			}
			
		}, DSchClientConsts.DSCH_SERVICE_HC_TIMER_DELAY , DSchClientConsts.DSCH_SERVICE_HC_TIMER_PERIOD);
	}
}
