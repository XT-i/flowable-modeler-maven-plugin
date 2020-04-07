package com.xti.flowable.modeler.mavenplugin.model;

public enum ModelType {
	
	BPMN ("processes"),
	CMMN ("cases"),
	DMN  ("dmn"),
	FORM ("forms");
	
	private String folderName;
	
	private ModelType ( String folderName ) {
		this.folderName = folderName;
	}
	
	public String getFolderName ( ) {
		return folderName;
	}
	
}
