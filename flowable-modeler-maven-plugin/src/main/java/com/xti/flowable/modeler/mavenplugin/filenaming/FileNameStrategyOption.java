package com.xti.flowable.modeler.mavenplugin.filenaming;

import com.xti.flowable.modeler.mavenplugin.filenaming.impl.KeyBasedFileNameStrategy;
import com.xti.flowable.modeler.mavenplugin.filenaming.impl.NameBasedFileNameStrategy;

/**
 *  Supported file name strategies:
 *  - NAME: default naming based on the model name, as implemented by Flowable Modeler UI upon download (eg "Quote to Order");
 *  - KEY: based on the model key, which gives better control over naming ("eg: sales.quote-to-order", etc...)
 */
public enum FileNameStrategyOption {

	NAME,
	KEY;
	
	public FileNameStrategy getStrategy ( ) {
		FileNameStrategy result = null;
		switch ( this ) {
			case NAME:
				result = new NameBasedFileNameStrategy();
				break;
			case KEY:
				result = new KeyBasedFileNameStrategy();
				break;
		}
		return result;
	}
	
	
}
