XTi Flowable Modeler Maven Plugin
=================================

Working with Flowable BPM in a Java project?  Then you've probably found yourself downloading changed models to your codebase over and over again.
This Flowable Maven plugin that allows for easy integration of Flowable Modelering in your development cycle.
It takes a Java + Spring Boot setup as default configuration, as this is the most common habitat of an embedded Flowable BPM implementation

### Plugin Features

Automate the **download of all your models** to your maven project (Spring Boot layout as default).
This allows you to edit your models in a Flowable Modeler instance, and get the BPMN, CMMN, DMN and FORM files in your codebase so you can commit them in your SCM.
No more need for the Download button in Flowable modeler.

**Upload existing model files** to a Flowable Modeler instance.
This makes it easy to load all relevant models of your current development branch in a (temporary) Flowable Modeler instance and do model updates as required 

### Status: PRE-ALPHA

NOTE: The current plugin is a very early (and very drafty) implementation - use at own risk.  Always backup your models!


## Prerequisites

### Make sure you have Flowable Modeler running.

The default configuration of the plugin assumes that your Flowable Modeler is running at *http://localhost:8080/* (http://localhost:8080/flowable-modeler), and assumes an *admin* user with password *test* to exist.

The easiest way to hande this is to start the all-in-one docker image maintained by the Flowable project:

	docker run -p 8080:8080 flowable/all-in-one



### Add the maven repository containing the plugin to your Maven project

Add the XTi maven plugin repositories to your project's *pom.xml*:

NOTE: There currently isn't a *release*, so you'll have to work with the *snapshot* repository for now.


```xml
<pluginRepositories>
	  <pluginRepository>
		<id>xti-maven-plugin-repository-releases</id>
		<url>http://maven.xt-i.cloud/releases</url>
		<releases>
			<enabled>true</enabled>
		</releases>
		<snapshots>
			<enabled>false</enabled>
		</snapshots>
	</pluginRepository>
	<pluginRepository>
		<id>xti-maven-plugin-repository-snapshots</id>
		<url>http://maven.xt-i.cloud/snapshots</url>
		<releases>
			<enabled>true</enabled>
		</releases>
		<snapshots>
			<enabled>true</enabled>
			<updatePolicy>always</updatePolicy>
		</snapshots>
	</pluginRepository>
</pluginRepositories>
```

### Enabling the plugin in your project

Configure your project to use the plugin by adding it to your *pom.xml*.


```xml
<build>
	<plugins>
	
		<plugin>
			<groupId>com.xti.flowable</groupId>
			<artifactId>flowable-modeler-maven-plugin</artifactId>
			<version>0.0.1-SNAPSHOT</version>
        </plugin>
        
    </plugins>
</build>
```

This allows you to download the plugin and then use it from the command line (see further)

### (Optional) facilitate easier commandline usage 

If you're planning to use the plugin only from the commandline, you'll need complete statements like

	mvn com.xti.flowable:flowable-modeler-maven-plugin:0.0.1-SNAPSHOT:download-models

You can make this shorthand work 

	mvn flowable-modeler:download-models

By adding the following to your *~/.m2/settings.xml* file:

```xml
	<pluginGroups>
		<pluginGroup>com.xti.flowable</pluginGroup>
	</pluginGroups>
```

### Integrating plugin execution in your builds

If you want your models to be downloaded automatically with each build, during the *generate-resources* phase, you can elaborate the configuration with an execution:

```xml
<build>
	<plugins>
	
		<plugin>
			<groupId>com.xti.flowable</groupId>
			<artifactId>flowable-modeler-maven-plugin</artifactId>
			<version>0.0.1-SNAPSHOT</version>
			<executions>
				<execution>
					<id>execution-upon-generate-resources</id>
					<phase>generate-resources</phase>
					<goals>
						<goal>download-models</goal>
					</goals>
		          </execution>
			</executions>
        </plugin>
        
    </plugins>
</build>
```

## Configuring the plugin

If you don't want to use the default settings, you can pass the necessary parameters to the plugin.

As a reference, a full default configuration example can be found underneath.
As all these values are the defaults, it's no use copying them in your pom.xml if you don't need to set them.

You can also use *-D<name>=<value>* on the command prompt if you wish, or externalise them to *settings.xml*.
Refer to the Maven documentation (http://maven.apache.org/examples/injecting-properties-via-settings.html) for more details.


```xml
<build>
		<pluginManagement>
			<plugins>
			
				<plugin>
					<groupId>com.xti.flowable</groupId>
					<artifactId>flowable-modeler-maven-plugin</artifactId>
					<version>0.0.1-SNAPSHOT</version>
						<configuration>
							<url>http://localhost:8080</url> <!-- URL on which your Flowable Modeler instance runs -->
							<username>admin</username> <!-- USERNAME to login on your Flowable Modeler instance -->
							<password>test</password> <!-- PASSWORD to login on your Flowable Modeler instance -->
							<path>src/main/resources</path><!-- PATH where model files in your project reside  -->
							<types><!-- TYPES you want the plugin to handle - BPMN, CMMN, DMN and FORM -->
								<type>BPMN</type>
								<type>CMMN</type>
								<type>DMN</type>
								<type>FORM</type>
							</types>
							<bpmnFileExtension>.bpmn20.xml</bpmnFileExtension> <!-- FILE EXTENSION to use for BPMN files (".bpmn20.xml", ".bpmn.xml", ".bpmn", ...) -->
							<cmmnFileExtension>.cmmn.xml</cmmnFileExtension> <!-- FILE EXTENSION to use for CMMN files (".cmmn.xml", ".cmmn", ...) -->
							<dmnFileExtension>.dmn</dmnFileExtension><!-- FILE EXTENSION to use for DMN files (".dmn", ".dmn.xml", ...) -->
							<formFileExtension>.form</formFileExtension><!-- FILE EXTENSION to use for FORM files -->
							<fileNameStrategyOption>NAME</fileNameStrategyOption> <!-- NAME is compliant with Flowable Modeler download names, KEY is an alternative -->
						</configuration>
		           </plugin>
		           
			</plugins>
			
		</pluginManagement>

	<plugins>
		
		<plugin>
			<groupId>com.xti.flowable</groupId>
			<artifactId>flowable-modeler-maven-plugin</artifactId>
			<executions>
				<execution>
					<id>execution-upon-generate-resources</id>
					<phase>generate-resources</phase>
					<goals>
						<goal>download-models</goal>
					</goals>
		          </execution>
			</executions>
        </plugin>
        
	</plugins>
</build>
```
	
## Using the plugin

### Downloading models

Downloading model from Flowable Modeler to the local filesystem

	mvn flowable-modeler:download-models

With the default configuration, your downloaded model files can be found in *src/main/resources/*
The plugin logs on the consolse for each file whether it was ADDED since last download, UPDATED of UNCHANGED.

 
### Importing models

Importing local model files in Flowable Modeler (make sure that no duplicates exist):

	mvn flowable-modeler:import-models

With the default configuration, the following local folders are scanned and all model files are imported in Flowable Modeler:

* BPMN model files in *src/main/resources/processes*
* CMMN model files in *src/main/resources/cases*
* DMN model files in *src/main/resources/dmn*
* FORM model files in *src/main/resources/forms*

You can now edit the models in Flowable Modeler, and after that download them again in batch with the plugin


