package com.hs.dsch.handler;

public enum DSchHandlerType {
	DSCH_JOB_HANDLER_TYPE_COMMAND(0),
	DSCH_JOB_HANDLER_TYPE_JOB_HC(1),
	DSCH_JOB_HANDLER_TYPE_NODE_HC(2),
	DSCH_JOB_HANDLER_TYPE_REG(3);
	
	private int type;
	
	DSchHandlerType(int type) {
		this.setType(type);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
