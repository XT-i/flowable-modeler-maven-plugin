package com.xti.flowable.modeler.mavenplugin.filenaming.impl;

import com.xti.flowable.modeler.mavenplugin.filenaming.FileNameStrategy;

/**
 * Naming strategy that used the model key as the basis for the filename, which allows for a more structured approach to model file naming.
 */
public class KeyBasedFileNameStrategy implements FileNameStrategy {

	@Override
	public String determineFileName ( String modelKey, String modelName ) {
		return modelKey;
	}

}
