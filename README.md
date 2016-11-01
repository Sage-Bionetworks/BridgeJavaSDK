# Bridge Java SDK 
[![Build Status](https://travis-ci.org/Sage-Bionetworks/BridgeJavaSDK.svg?branch=develop)](https://travis-ci.org/Sage-Bionetworks/BridgeJavaSDK)

This is the Java SDK for the Bridge Server. For documentation on the REST services provided by Bridge, see our [Developer Portal](https://developer.sagebridge.org/).

This SDK project has three sub-projects:
* The [api-codegen/](api-codegen/) package takes a swagger.json description of our REST services and converts these into Java classes;
* The [rest-client/](rest-client/) package creates a minimal REST API client for working with the Bridge server, useful for any kind of Java environment (including Android). It is the library we use to write integration tests for both our client and server software.

## Using the Java SDK

Here's an example of referencing the JAR via Maven:

	<project>
	    ...
		<dependencies>
			<dependency>
			    <groupId>org.sagebionetworks</groupId>
			    <artifactId>rest-client</artifactId>
			    <version>0.12.3</version>
			</dependency>
		</dependencies>
		...
		<repositories>
			<repository>
				<id>org-sagebridge-repo-maven-releases</id>
				<name>org-sagebridge-repo-maven-releases</name>
				<url>http://repo-maven.sagebridge.org/</url>
			</repository>
		</repositories>	
	</project>

## For developers working on the libraries

This project requires that you have Java and Maven installed, and you'll need to fork this repository. As is the case with other projects at Sage Bionetworks, we recommend you fork and pull down a local copy, and then set the origin repository as a remote:

    git remote add upstream https://github.com/Sage-Bionetworks/BridgeJavaSDK.git

## Updating this SDK

* Clone the [BridgeDocs](../BridgeDocs/) repository and add the markdown description of service changes to the `bridge-api/` folder. then run the tools in that project to produce an updated swagger.json file (this is documented in the project's README file);
* Increment the build number of all sub-projects using the command `mvn versions:set -DnewVersion=x.y.z`
* Copy the swagger.json file to `api-codegen/swagger.json`;
* Run the build so that all three projects are rebuilt, and the new REST API and model classes should be part of the REST and Java SDKs.
* After checking in this project, [BridgeIntegrationTests](../BridgeIntegrationTests/) can be updated to the new Java SDK version and tests can be written for the new end-to-end functionality;
* The BridgeDocs project should be deployed to update the documentation, after you update the client libraries.
