package com.xti.flowable.modeler.mavenplugin.mojo;



import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.xti.flowable.modeler.mavenplugin.common.ModelFileStatus;
import com.xti.flowable.modeler.mavenplugin.filenaming.FileNameStrategy;
import com.xti.flowable.modeler.mavenplugin.filenaming.FileNameStrategyOption;
import com.xti.flowable.modeler.mavenplugin.filesystem.ModelFileSystem;
import com.xti.flowable.modeler.mavenplugin.integration.GetModel;
import com.xti.flowable.modeler.mavenplugin.integration.ModelerIntegration;
import com.xti.flowable.modeler.mavenplugin.integration.json.GetModelsResponse;
import com.xti.flowable.modeler.mavenplugin.integration.json.GetModelsResponseItem;
import com.xti.flowable.modeler.mavenplugin.model.ModelType;

@Mojo(name = "download-models", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class DownloadModelsMojo extends AbstractModelsMojo {

    @Parameter(property="naming")
    private FileNameStrategyOption fileNameStrategyOption;

	
    public void execute() throws MojoExecutionException, MojoFailureException {
    	Log log = getLog();
    	
        log.info("Downloading models from Flowable Modeler");
    	
    	ModelerIntegration mi = new ModelerIntegration(log, getUrl());
    	mi.login(getUsername(), getPassword());
    	
    	ModelFileSystem mfs = new ModelFileSystem(log, getModelsBaseDir());

    	FileNameStrategy fileNameStrategy = getFileNameStrategy();
    	log.info("Using file naming strategy based on model " + getFileNameStrategyOption());
    	
        for ( ModelType mt: getModelTypes() ) {
        	log.info("Downloading " + mt + " models");

        	switch ( mt ) {
        		case BPMN:
        			GetModelsResponse processes = mi.getProcesses();
        			saveModels(log, processes, mt, mi::getProcessModel, mfs, fileNameStrategy, getBpmnFileExtension());
        			break;
        		case CMMN:
        			GetModelsResponse cases = mi.getCases();
        			saveModels(log, cases, mt, mi::getCaseModel, mfs, fileNameStrategy, getCmmnFileExtension());
        			break;
        		case DMN:
        			GetModelsResponse decisions = mi.getDecisions();
        			saveModels(log, decisions, mt, mi::getDecisionModel, mfs, fileNameStrategy, getDmnFileExtension());
        			break;
        		case FORM:
        			GetModelsResponse forms = mi.getForms();
        			saveModels(log, forms, mt, mi::getFormModel, mfs, fileNameStrategy, getFormFileExtension());
        			break;
    			default:
        	}
        }
    }
    
    public void saveModels ( Log log, GetModelsResponse models, ModelType mt, GetModel getModel, ModelFileSystem mfs, FileNameStrategy fileNameStrategy, String fileExtension ) {
		log.debug("found " + models.getData().size() + " " + mt + " model(s)");
		
		int longestKey = models.getLongestKeyLength(getMinimumColumnWidth());
		int longestName = models.getLongestNameLength(getMinimumColumnWidth());
		
		for ( GetModelsResponseItem item: models.getData() ) {
			String fileName = fileNameStrategy.determineFileName(item.getKey(), item.getName()) + fileExtension;
			String content = getModel.getModel(item.getId());
			String relativeFilePath = getModelsPath() + "/" + mt.getFolderName() + "/" + fileName;
			
			ModelFileStatus status = mfs.writeFile(mt.getFolderName(), fileName, content);
			log.info(format(status, item.getKey(), item.getName(), item.getId(), relativeFilePath, longestKey, longestName));
		}
    }
    
    public FileNameStrategyOption getFileNameStrategyOption ( ) {
    	return fileNameStrategyOption == null? FileNameStrategyOption.NAME : fileNameStrategyOption;
    }
    
    public FileNameStrategy getFileNameStrategy ( ) {
    	return getFileNameStrategyOption().getStrategy();
    }

}