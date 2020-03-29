package com.xti.flowable.modeler.mavenplugin.common;


public enum ModelFileStatus {
	
	ADDED,
	UPDATED,
	UNCHANGED,
	
	UNKNOWN,
	IMPORTED,
	ERROR;

	public static int getMaxNameLength ( ) {
		int max = 1;
		for ( ModelFileStatus s: ModelFileStatus.values() ) {
			if ( s.toString().length() > max ) {
				max = s.toString().length();
			}
		}	
		return max;
	}
	
}
