package com.xti.flowable.modeler.mavenplugin.filenaming.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.xti.flowable.modeler.mavenplugin.filenaming.FileNameStrategy;
import com.xti.flowable.modeler.mavenplugin.filenaming.FileNameStrategyOption;

public class NameBasedFileNameStrategyTest {

	private FileNameStrategy s;
	
	@BeforeEach
	public void setUp ( ) {
		this.s = FileNameStrategyOption.NAME.getStrategy();
	}
	
	@Test
	public void testSimple ( ) {
		String modelId = "some-business-process";
		String modelName = "A simple Business Process";
		String expected = "A_simple_Business_Process";
		String name = s.determineFileName(modelId, modelName);
		assertEquals(expected, name);
	}
	
	@Test
	public void testNullId ( ) {
		String modelId = "model.id";
		String modelName = null;
		String expected = null;
		String name = s.determineFileName(modelId, modelName);
		assertEquals(expected, name);
	}
	
	@Test
	public void testEmptyId ( ) {
		String modelId = "model.id";
		String modelName = "";
		String expected = "";
		String name = s.determineFileName(modelId, modelName);
		assertEquals(expected, name);
	}
		
	@Test
	public void testWeirdName ( ) {
		String modelId = "model.id";
		String modelName = " an id with spaces special é§è!çà chars and UPPERcases ";
		String expected = "an_id_with_spaces_special_é§è!çà_chars_and_UPPERcases";
		String name = s.determineFileName(modelId, modelName);
		assertEquals(expected, name);
	}

}
