package com.xti.flowable.modeler.mavenplugin.filenaming.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.xti.flowable.modeler.mavenplugin.filenaming.FileNameStrategy;
import com.xti.flowable.modeler.mavenplugin.filenaming.FileNameStrategyOption;

public class KeyBasedFileNameStrategyTest {

	private FileNameStrategy s;
	
	@BeforeEach
	public void setUp ( ) {
		this.s = FileNameStrategyOption.KEY.getStrategy();
	}
	
	@Test
	public void testSimple ( ) {
		String modelId = "some-model-id";
		String modelName = "some-model-name";
		String expected = "some-model-id";
		String name = s.determineFileName(modelId, modelName);
		assertEquals(expected, name);
	}
	
	@Test
	public void testNullId ( ) {
		String modelId = null;
		String modelName = "some-model-name";
		String expected = null;
		String name = s.determineFileName(modelId, modelName);
		assertEquals(expected, name);
	}
	
	@Test
	public void testEmptyId ( ) {
		String modelId = "";
		String modelName = "some-model-name";
		String expected = "";
		String name = s.determineFileName(modelId, modelName);
		assertEquals(expected, name);
	}
	
	@Test
	public void testWeirdId ( ) {
		String modelId = " an id with spaces and UPPERcases is kept ";
		String modelName = "some-model-name";
		String expected = " an id with spaces and UPPERcases is kept ";
		String name = s.determineFileName(modelId, modelName);
		assertEquals(expected, name);
	}

}
