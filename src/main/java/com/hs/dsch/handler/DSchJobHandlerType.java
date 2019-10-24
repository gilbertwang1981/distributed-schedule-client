package com.hs.dsch.handler;

public enum DSchJobHandlerType {
	DSCH_JOB_HANDLER_TYPE_PRE(0),
	DSCH_JOB_HANDLER_TYPE_POST(1);
	
	private int type;
	
	DSchJobHandlerType(int type) {
		this.setType(type);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
