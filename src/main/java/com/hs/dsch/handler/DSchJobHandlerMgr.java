package com.hs.dsch.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DSchJobHandlerMgr {
	private static DSchJobHandlerMgr instance = null;
	
	private Map<DSchJobHandlerType , DSchJobHandler> handlers = new ConcurrentHashMap<>();
	
	private DSchJobHandlerMgr() {
		handlers.put(DSchJobHandlerType.DSCH_JOB_HANDLER_TYPE_PRE , new DSchJobPreHandler());
		handlers.put(DSchJobHandlerType.DSCH_JOB_HANDLER_TYPE_POST , new DSchJobPostHandler());
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
	
	public void handle(DSchJobHandlerType type , DSchJobContext context) {
		handlers.get(type).handle(context);
	}
}
