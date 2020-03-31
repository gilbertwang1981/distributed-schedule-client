package com.hs.dsch.handler;

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
import com.hs.dsch.proto.DSchAdminProto.DSchNode.Builder;
import com.hs.dsch.util.HttpClient;
import com.hs.dsch.util.SystemUtils;

public class DSchNodeHealthCheckHandler implements DSchJobHandler {
	private DSchConfiguration dschConfiguration = DSchContext.getInstance().getDSchConfiguration();
	private HttpClient httpClient = DSchContext.getInstance().getHttpClient();
	
	private static Logger logger = LoggerFactory.getLogger(DSchNodeHealthCheckHandler.class);

	@Override
	public void handle(DSchJobContext context) {
		DSchNodeHealthCheckRequest.Builder builder = DSchNodeHealthCheckRequest.newBuilder();
		builder.setNodeId(DSchContext.getInstance().getNodeId());
		Builder node = DSchNode.newBuilder();
		node.setActiveThreads(SystemUtils.getThreadCount());
		node.setMem(SystemUtils.getMemUtil());
		node.setCpu(SystemUtils.getCpuUtil());
		node.setUpdateTime(System.currentTimeMillis());
		builder.setNode(node);
		
		try {
			HttpResponse httpResponse = httpClient.post(dschConfiguration.getHost() , dschConfiguration.getPort() ,
				DSchClientConsts.DSCH_SERVICE_NODE_HC_INF_NAME , builder.build().toByteArray());
			DSchNodeHealthCheckResponse response = DSchNodeHealthCheckResponse.parseFrom(httpResponse.getEntity().getContent());
			if (response.getResCode() == DSchResponseCode.RESP_CODE_FAILED) {
				logger.error("节点健康检查失败 {}" , DSchContext.getInstance().getNodeId());
				
				DSchContext.getInstance().shutdownNode();
			} else {
				logger.info("节点健康检查成功 {}" , DSchContext.getInstance().getNodeId());
			}
		} catch (Exception e) {
			logger.error("节点健康检查发生异常 {}" , e);
		}
	}
}
