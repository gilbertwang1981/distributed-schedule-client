package com.hs.dsch.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DSchJobHandlerMgr {
	private static DSchJobHandlerMgr instance = null;
	
	private Map<DSchHandlerType , DSchJobHandler> handlers = new ConcurrentHashMap<>();
	
	private DSchJobHandlerMgr() {
		handlers.put(DSchHandlerType.DSCH_JOB_HANDLER_TYPE_COMMAND , new DSchJobCommandHandler());
		handlers.put(DSchHandlerType.DSCH_JOB_HANDLER_TYPE_JOB_HC , new DSchJobHealthCheckHandler());
		handlers.put(DSchHandlerType.DSCH_JOB_HANDLER_TYPE_NODE_HC , new DSchNodeHealthCheckHandler());
		handlers.put(DSchHandlerType.DSCH_JOB_HANDLER_TYPE_REG , new DSchJobRegHandler());
	}
	
	public static DSchJobHandlerMgr getInstance() {
		if (instance == null) {
			synchronized (DSchJobHandlerMgr.class) {
				if (instance == null) {
					instance = new DSchJobHandlerMgr();
				}
			}
		}
		
		return instance;
	}
	
	public void handle(DSchHandlerType type , DSchJobContext context) {
		handlers.get(type).handle(context);
	}
}
