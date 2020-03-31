package com.xti.flowable.modeler.mavenplugin.filenaming;

import org.junit.jupiter.api.Test;

import com.xti.flowable.modeler.mavenplugin.filenaming.impl.KeyBasedFileNameStrategy;
import com.xti.flowable.modeler.mavenplugin.filenaming.impl.NameBasedFileNameStrategy;
import static org.junit.jupiter.api.Assertions.*;

public class FileNameStrategyOptionTest {

	@Test
	public void testNameBasedStrategy ( ) {
		FileNameStrategy strategy = FileNameStrategyOption.NAME.getStrategy();
		assertNotNull(strategy);
		assertEquals(NameBasedFileNameStrategy.class, strategy.getClass());
	}
	
	@Test
	public void testKeyBasedStrategy ( ) {
		FileNameStrategy strategy = FileNameStrategyOption.KEY.getStrategy();
		assertNotNull(strategy);
		assertEquals(KeyBasedFileNameStrategy.class, strategy.getClass());
	}

}
