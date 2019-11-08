package com.hs.dsch.launch;

import java.util.Timer;
import java.util.TimerTask;

import com.hs.dsch.consts.DSchClientConsts;
import com.hs.dsch.handler.DSchJobContext;
import com.hs.dsch.handler.DSchJobHandlerMgr;
import com.hs.dsch.handler.DSchHandlerType;

public class DSchedulerNodeHealthChecker {
	private Timer nodeHealthChecker = new Timer();
	
	public void scheduleTimer() {
		nodeHealthChecker.schedule(new TimerTask() {

			@Override
			public void run() {				
				DSchJobHandlerMgr.getInstance().handle(DSchHandlerType.DSCH_JOB_HANDLER_TYPE_NODE_HC , new DSchJobContext());
			}
		}, DSchClientConsts.DSCH_SERVICE_HC_TIMER_DELAY , DSchClientConsts.DSCH_SERVICE_HC_TIMER_PERIOD);
	}
}
