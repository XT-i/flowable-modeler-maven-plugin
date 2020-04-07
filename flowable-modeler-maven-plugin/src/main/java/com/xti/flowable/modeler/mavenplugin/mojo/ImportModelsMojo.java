package com.xti.flowable.modeler.mavenplugin.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;

import com.xti.flowable.modeler.mavenplugin.common.ModelFileStatus;
import com.xti.flowable.modeler.mavenplugin.filesystem.ModelFile;
import com.xti.flowable.modeler.mavenplugin.filesystem.ModelFileSystem;
import com.xti.flowable.modeler.mavenplugin.filesystem.ModelFiles;
import com.xti.flowable.modeler.mavenplugin.integration.ModelerIntegration;
import com.xti.flowable.modeler.mavenplugin.model.ModelType;

@Mojo(name = "import-models")
public class ImportModelsMojo extends AbstractModelsMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
    	Log log = getLog();
    	
        log.info("Importing models in Flowable Modeler");
    	
    	ModelerIntegration mi = new ModelerIntegration(log, getUrl());
    	mi.login(getUsername(), getPassword());
    	
    	ModelFileSystem mfs = new ModelFileSystem(log, getModelsBaseDir());
    	
        for ( ModelType mt: getModelTypes() ) {
        	log.info("Importing " + mt + " models");

        	ModelFiles files;
        	
        	switch ( mt ) {
        		case BPMN:
        			files = mfs.scan(mt.getFolderName());
        			for ( ModelFile file: files.getFiles() ) {
        				String content = mfs.readFile(file.getPath());
        				ModelFileStatus status = mi.importProcess(content);
        				log.info(format(status, file.getPath()));
        			}
        			break;
        		case CMMN:
        			files = mfs.scan(mt.getFolderName());
        			for ( ModelFile file: files.getFiles() ) {
        				String content = mfs.readFile(file.getPath());
        				ModelFileStatus status = mi.importCaseModel(content);
        				log.info(format(status, file.getPath()));
        			}
        			break;
        		case DMN:
        			files = mfs.scan(mt.getFolderName());
        			for ( ModelFile file: files.getFiles() ) {
        				String content = mfs.readFile(file.getPath());
        				ModelFileStatus status = mi.importDecisionModel(content);
        				log.info(format(status, file.getPath()));
        			}
        			break;
        		case FORM:
        			files = mfs.scan(mt.getFolderName());
        			for ( ModelFile file: files.getFiles() ) {
        				String content = mfs.readFile(file.getPath());
        				ModelFileStatus status = mi.importFormModel(content);
        				log.info(format(status, file.getPath()));
        			}
        			break;
    			default:
        	}
        }
    }
    

}