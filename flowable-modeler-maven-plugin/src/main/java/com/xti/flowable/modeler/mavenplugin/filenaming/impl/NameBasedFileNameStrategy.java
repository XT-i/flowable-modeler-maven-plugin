package com.xti.flowable.modeler.mavenplugin.filenaming.impl;

import com.xti.flowable.modeler.mavenplugin.filenaming.FileNameStrategy;

/**
 * Naming strategy in line with the file names generated for a download by the Flowable Modeler software itself.
 * Basically adheres to the name of the model, but replacing spaces with an underscore
 * Names are processed in a case-sensitive way 
 */
public class NameBasedFileNameStrategy implements FileNameStrategy {

	@Override
	public String determineFileName(String modelKey, String modelName) {
		String result = null;
		if ( modelName != null ) {
			result = modelName.trim();
			result = result.replace(' ', '_');
		}
		return result;
	}

	
	
}
