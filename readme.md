# eRums API Automation

This project contains solution, source code and test suites to test eRums Webservices.

## Pre-requisites

Download and Install Java latest version like java 8 and add its bin path to the Environment variables.( https://www3.ntu.edu.sg/home/ehchua/programming/howto/JDK_Howto.html)

Download and Install Maven and add its bin path to the Environment variables. (https://maven.apache.org/install.html)


## Technologies

Java

RestAssured Framework

TestNG

Maven

## Test Execution Guidelines
Get the latest code from the GitHub repository "OSMS-ApiAutomation"

Start the IDE and open as mvn Project

All the TestNG automated tests.xml are under below location

* src/test/apiTestsExecutionDriver/apiTestSuites/

* From the Project path, navigate to file **pom.xml** and right click > maven > reimport to update your local repository

* Open CMD of POM.xml project and execute below command based on the target environment

* QA2 Environment

```
mvn clean compile test -DapiGroupName=ecomintegration -DcurrentTestEnvironment=QA2 -Dsurefire.suiteXmlFiles=src/test/apiTestsExecutionDriver/apiTestSuites/master/RunAllSuites.xml

```

To view the report after execution , refer APITestExecutionReport.html report
