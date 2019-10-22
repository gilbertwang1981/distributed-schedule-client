package com.hs.dsch.conf;

public class DSchContext {
	private String nodeId;
	
	private static DSchContext instance = null;

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	
	public static DSchContext getInstance() {
		if (instance == null) {
			synchronized (DSchContext.class) {
				if (instance == null) {
					instance = new DSchContext();
				}
			}
		}
		
		return instance;
	}
}
