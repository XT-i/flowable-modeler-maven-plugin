package com.xti.flowable.modeler.mavenplugin.mojo;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.xti.flowable.modeler.mavenplugin.common.ModelFileStatus;
import com.xti.flowable.modeler.mavenplugin.model.ModelType;

public abstract class AbstractModelsMojo extends AbstractMojo {

	private static final String DEFAULT_FLOWABLE_MODELER_URL = "http://localhost:8080";
	private static final String DEFAULT_FLOWABLE_USERNAME = "admin";
	private static final String DEFAULT_FLOWABLE_PASSWORD = "test";
	private static final String DEFAULT_MODELS_PATH = "src/main/resources";
	private static final String DEFAULT_BPMN_FILE_EXTENSION = ".bpmn20.xml";
	private static final String DEFAULT_CMMN_FILE_EXTENSION = ".cmmn.xml";
	private static final String DEFAULT_DMN_FILE_EXTENSION = ".dmn";
	private static final String DEFAULT_FORM_FILE_EXTENSION = ".form";
	
    @Parameter(property = "types")
    private ModelType[] types;

    @Parameter(property = "url", defaultValue=DEFAULT_FLOWABLE_MODELER_URL)
    private String url;

    @Parameter(property = "username", defaultValue=DEFAULT_FLOWABLE_USERNAME)
    private String username;

    @Parameter(property = "password", defaultValue=DEFAULT_FLOWABLE_PASSWORD)
    private String password;
    
    @Parameter(property = "bpmn-file-extension", defaultValue=DEFAULT_BPMN_FILE_EXTENSION)
    private String bpmnFileExtension;

    @Parameter(property = "cmmn-file-extension", defaultValue=DEFAULT_CMMN_FILE_EXTENSION)
    private String cmmnFileExtension;

    @Parameter(property = "dmn-file-extension", defaultValue=DEFAULT_DMN_FILE_EXTENSION)
    private String dmnFileExtension;

    @Parameter(property = "form-file-extension", defaultValue=DEFAULT_FORM_FILE_EXTENSION)
    private String formFileExtension;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Parameter(property = "path", defaultValue=DEFAULT_MODELS_PATH)
    private String path;

    public String getModelsPath ( ) {
    	if ( path == null ) {
    		return "src/main/resources";
    	} else {
    		return path;
    	}
    }
    
    public File getModelsBaseDir ( ) {
    	return new File(project.getBasedir(), getModelsPath());
    }
    
    public int getMinimumColumnWidth ( ) { 
    	return 30; // arbitrary default column layout
    }
    
    public String getUrl ( ) {
    	return url;
    }
    
    public String getUsername ( ) {
    	return username;
    }

    public String getPassword ( ) {
    	return password;
    }

    public ModelType[] getModelTypes ( ) {
    	ModelType[] result = ModelType.values();
    	if ( types != null && types.length > 0 ) {
    		result = types;
    	}
    	return result;
    }

	public String getBpmnFileExtension() {
		return bpmnFileExtension;
	}

	public String getCmmnFileExtension() {
		return cmmnFileExtension;
	}

	public String getDmnFileExtension() {
		return dmnFileExtension;
	}

	public String getFormFileExtension() {
		return formFileExtension;
	}

    public String format ( ModelFileStatus status, String key, String name, String id, String fileName, int longestKey, int longestName ) {
    	// model key not shown as it didn't seem very useful
    	return String.format("  [ %" + ModelFileStatus.getMaxNameLength() + "s ] %-" + longestKey + "s | %-" + longestName + "s | %s", status, key, name, fileName);
    }
    
    public String format ( ModelFileStatus status, String fileName) {
    	return String.format("  [ %" + ModelFileStatus.getMaxNameLength() + "s ] %s", status, fileName);
    }

}
