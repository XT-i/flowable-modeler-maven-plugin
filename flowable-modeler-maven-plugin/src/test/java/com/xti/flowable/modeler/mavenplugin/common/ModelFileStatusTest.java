package com.xti.flowable.modeler.mavenplugin.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ModelFileStatusTest {

	private ModelFileStatus LONGEST = ModelFileStatus.UNCHANGED;
	
	@Test
	public void testMaximumNameLength ( ) {
		assertEquals(LONGEST.toString().length(), ModelFileStatus.getMaxNameLength());
	}
	
}
