# Bridge Java SDK 
[![Build Status](https://travis-ci.org/Sage-Bionetworks/BridgeJavaSDK.svg?branch=develop)](https://travis-ci.org/Sage-Bionetworks/BridgeJavaSDK)
This is the Java SDK for the Bridge Server (http://github.com/Sage-Bionetworks/BridgePF). It was developed on a 2014 Dell running Ubuntu 14.04 LTS.

## Using the SDK

Only a pre-release version exists at this time. Here's an example of referencing the JAR via Maven:

	<project>
	    ...
		<dependencies>
			<dependency>
			    <groupId>org.sagebionetworks.bridge</groupId>
			    <artifactId>java-sdk</artifactId>
			    <version>develop-SNAPSHOT</version>
			</dependency>
		</dependencies>
		...
	    <repositories>
			<repository>
				<id>sagebionetworks</id>
				<url>http://sagebionetworks.artifactoryonline.com/sagebionetworks/libs-snapshots-local</url>
			</repository>        
	    </repositories>	
	</project>

Or download it [here](http://sagebionetworks.artifactoryonline.com/sagebionetworks/libs-snapshots-local/org/sagebionetworks/bridge/java-sdk/develop-SNAPSHOT/java-sdk-develop-SNAPSHOT.jar).

## Developer Bootstrap
To run this SDK, you must have both Java and Maven installed.

### Setting Up a Development Workflow Using Git and Github.
Now that you have Java and Maven installed, the hard part is out of the way. Next we need to fork and clone the repository, and then set up a local repository. If you are unfamiliar with Git or Github, check out this excellent (and free) [ebook](http://git-scm.com/book).

#### Fork the Repository
First, you will want a personal version of the project to work on (this is the only way to save any changes you make to Github). To do this, on this page you should look for a button that says "Fork this repo." Click it, and follow the instructions.

#### Clone the Repository Locally
Next, you will want to open up the terminal application (or use the Github App, if you so wish), navigate to the directory you want to work in, and run the following command, with {github-username} replaced with your actual username:
```
git clone https://github.com/{github-username}/BridgeJavaSDK
```
An example using my Github name, [SartoriusEarth](https://github.com/SartoriusEarth):
```
git clone https://github.com/SartoriusEarth/BridgeJavaSDK
```
This will create an exact copy of the repository into a folder called "BridgeJavaSDK". That folder will be placed into your current directory.

#### Setup Remote Pointing to Official Repo
Next, we need to setup a shortcut name for the official project, titled `upstream`. This can be done by adding a "remote":
```
git remote add upstream https://github.com/Sage-Bionetworks/BridgeJavaSDK.git
```
You should then be able to see all your remotes, with the command `git remote -v`, with the following output:
```
$> git remote -v
origin  https://github.com/SartoriusEarth/BridgeJavaSDK
upstream    https://github.com/Sage-Bionetworks/BridgeJavaSDK.git
```


### Java
What follows are the instructions for installing Java on your machine.
#### Ubuntu (Debian)
To install Java, you can run the following on a Debian machine:
```
sudo apt-get install openjdk-7-jdk
```
#### Mac OS X
On Mac, use the package manager of your choice (e.g. brew) or see the [Oracle instructions for Mac](http://www.java.com/en/download/help/mac_install.xml).
#### Windows
On Windows, you can try the [Oracle instructions for Windows](http://www.java.com/en/download/help/windows_manual_download.xml).


### Maven
What follows are the instructions for installing Maven on your machine.
#### Ubuntu (Debian)
If you have Maven 2 already installed on your machine, the first thing you will need to do is run:

```
sudo apt-get remove maven2
```
If you do not, simply ignore the previous step. If you do not know, run `mvn -version`. If you get an error, you do not have Maven installed. If the first line says something like "Apache Maven 2.x", then you have Maven 2 installed and need to delete it.

To install Maven 3, run the following two commands:
```
sudo apt-get update
sudo apt-get install maven
```
After this successfully completes, run `mvn -version` and you should get an output similar to this:
```
Apache Maven 3.0.5
Maven home: /usr/share/maven
Java version: 1.7.0_65, vendor: Oracle Corporation
Java home: /usr/lib/jvm/java-7-openjdk-amd64/jre
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "3.13.0-35-generic", arch: "amd64", family: "unix"
```
If you run into problems, try a Google search describing your problem or check the [Apache Maven documentation](http://maven.apache.org/guides/).

#### Mac OS X
OS X prior to Mavericks (10.9) has Maven already installed. Congrats! For OS X versions after Mavericks (e.g. Lion), Java does not come automatically installed. Simply run `java` on the terminal application, and if Java is not already installed, the terminal will prompt you to do so, with instructions. During the Java install, it should coincidentally install Maven. Afterwared, run `mvn -version` and you should get something like:
```
Apache Maven 3.0.3 (r1075438; 2011-02-28 12:31:09-0500)
Maven home: /usr/share/maven
Java version: 1.6.0_29, vendor: Apple Inc.
Java home: /System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home
Default locale: en_US, platform encoding: MacRoman
OS name: "mac os x", version: "10.7.2", arch: "x86_64", family: "mac"
```
If you run into problems, try a Google search describing your problem or check the [Apache Maven documentation](http://maven.apache.org/guides/).

#### Windows
Godspeed. Try a Google search like "how to install Maven on Windows". For example, here's an untested [online tutorial](http://www.mkyong.com/maven/how-to-install-maven-in-windows/), or you can alternatively try the [Apache Maven documentation](http://maven.apache.org/guides/).


### (Optional) Using Eclipse
If you are using Eclipse, the first thing you will want to do is install the [Maven Eclipse Plugin](http://maven.apache.org/plugins/maven-eclipse-plugin/). That link also offers the plugins full suite of features.

Once you have installed the plugin, open the terminal application, navigate to the project location, and run `mvn eclipse:eclipse`. This will generate all the Eclipse metadata it will need to keep track of your project. Then go into Eclipse and import the project as a Maven Project.