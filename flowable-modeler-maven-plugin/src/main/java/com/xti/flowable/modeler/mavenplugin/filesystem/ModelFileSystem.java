package com.xti.flowable.modeler.mavenplugin.filesystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.maven.plugin.logging.Log;

import com.xti.flowable.modeler.mavenplugin.common.ModelFileStatus;

public class ModelFileSystem {
	
	private Log log;
	private File baseDir;
	
	public ModelFileSystem ( Log log, File baseDir ) {
		this.log = log;
		this.baseDir = baseDir;
		init();
	}
	
	private void init ( ) { 
		log.info("Models file system: " + baseDir );
		ensurePathInternal(baseDir);
	}
	
	private void ensurePathInternal ( File dir ) {
		log.debug("  checking directory '" + dir);
		if ( ! dir.exists() ) {
			log.debug("directory '" + dir + "' doesn't exist yet - creating");
			dir.mkdirs();
		}
	}

	public void ensurePath ( String path ) {
		ensurePathInternal(new File(baseDir, path));
	}
	
	public String readFile ( String path ) {
		StringBuffer result = new StringBuffer();
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(baseDir, path))));
			String line = br.readLine();
			
			while ( line != null ) {
				result.append(line);
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if ( br != null ) {
				try {
					br.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		return result.toString();
	}
	
	public ModelFileStatus writeFile ( String path, String fileName, String content ) {
		ModelFileStatus status = ModelFileStatus.ADDED;
		File fullPath = new File(baseDir, path + "/" + fileName);
		log.debug("writing to " + fullPath);
		try {
			ensurePath(path);
			
			boolean mustWrite = true;
			
			if ( fullPath.exists() ) {
				
				try (InputStream is = new FileInputStream(fullPath)) {
				    String existingMD5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
				    String updatedMD5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(content);
				    
				    if ( existingMD5.equals(updatedMD5)) {
				    	status = ModelFileStatus.UNCHANGED;
				    	mustWrite = false;
				    	
				    } else {
						status = ModelFileStatus.UPDATED;
				    }
				}
				
			}

			if ( mustWrite ) {
				BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullPath)));
				br.write(content);
				br.flush();
				br.close();
			}

		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return status;
	}
	
	public ModelFiles scan ( String folder ) {
		ModelFiles result = new ModelFiles();
		File fileFolder = new File(baseDir, folder);
		result = scan ( fileFolder, folder, result );
		return result;
	}
	
	private ModelFiles scan ( File folder, String relativePath, ModelFiles result ) {
		
		File[] files = folder.listFiles();
		
		if ( files != null ) {
			for ( File file: files) {
				if ( file.isFile() ) {
					
					ModelFile modelFile = new ModelFile();
					modelFile.setName(null);
					modelFile.setKey(null);
					modelFile.setPath(relativePath + "/" + file.getName());
					result.getFiles().add(modelFile);
				} else {
					scan ( file, relativePath + "/" + file.getName(), result );
				}
			}
		}
		
		return result;
	}

}
