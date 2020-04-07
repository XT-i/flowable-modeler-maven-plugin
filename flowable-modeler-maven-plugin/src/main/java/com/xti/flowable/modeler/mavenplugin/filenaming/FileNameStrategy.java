package com.xti.flowable.modeler.mavenplugin.filenaming;

public interface FileNameStrategy {
	
	String determineFileName ( String modelKey, String modelName );
	
}
