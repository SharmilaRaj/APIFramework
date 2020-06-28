package com.albertsons.api.framework.support.common;

import com.albertsons.api.framework.support.constants.GlobalConstants;
import com.albertsons.api.framework.support.reportgeneration.ExtentManager;
import com.albertsons.api.framework.support.reportgeneration.ExtentTestManager;
import com.relevantcodes.extentreports.LogStatus;
import io.restassured.RestAssured;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

/**
 * Base class with test configuration for environment, test data and reports.
 */
public class ConfigTestBase {
    public GlobalConstants gConstants = new GlobalConstants();
    public AppGeneric appGeneric;
    public DataExtractor dataExtractor;
    public GlobalConstants globalConstants;
    private String environment = null;
    private String currentApiGroupName = null;
    private String currentApiName = null;
    private String bannerName=null;

    /**
     * Initialize the test suite.
     *
     * @param environment         system under test
     * @param currentApiGroupName rest api group name
     * @param currentApiName      rest api name
     * @throws Exception if any system details cannot be retrieved
     */
    @Parameters({"currentTestEnvironment", "apiGroupName", "apiName", "bannerName"})
    @BeforeClass
    public void setupSuite(String environment, String currentApiGroupName, String currentApiName, String bannerName)
            throws Exception {
        appGeneric = new AppGeneric();
        dataExtractor = new DataExtractor();
        globalConstants = new GlobalConstants();
        // Set Proxy for Safeway VDI Machines to run Rest-Assured Test Scripts
        if (appGeneric.getHostSysOS().equalsIgnoreCase("windows")) {
            if (!(appGeneric.getComputerName().contains("CSC"))) {
                RestAssured.proxy("xphxbc02-int.safeway.com", Integer.parseInt("8080"));
            }
        }
        this.environment = environment;
        // Turnoff the Extent Report Logger
        System.setProperty("org.freemarker.loggerLibrary", "none");
        if (System.getProperty("currentTestEnvironment") != null) { // through jenkins execution
            this.environment = System.getProperty("currentTestEnvironment");
            this.currentApiGroupName = System.getProperty("apiGroupName");
            this.currentApiName = this.currentApiName;
            this.bannerName=System.getProperty("bannerName");
            GlobalConstants.testEnvironment = this.environment;
            GlobalConstants.currentTestAPIGroupName = this.currentApiGroupName;
            GlobalConstants.currentTestAPI = this.currentApiName;
            GlobalConstants.bannerName=this.bannerName;

        } else { // through local execution
            GlobalConstants.testEnvironment = environment;
            System.out.println("environment in config test is " + environment);
            GlobalConstants.currentTestAPIGroupName = currentApiGroupName;
            GlobalConstants.currentTestAPI = currentApiName;
            GlobalConstants.bannerName=bannerName;
        }
        dataExtractor.setup(environment, currentApiGroupName, currentApiName,bannerName);
    }

    /**
     * Initialize the test suite.
     *
     * @param environment         system under test
     * @param currentAPIGroupName rest api group name
     * @param currentAPIName      rest api name
     * @throws Exception if any system details cannot be retrieved
     */
    @Parameters({"currentTestEnvironment", "apiGroupName", "apiName", "serviceName", "bannerName", "loginName"})
    @BeforeClass
    public void setupSuite(String environment, String currentAPIGroupName, String currentAPIName, @Optional String serviceName,  String bannerName,  @Optional String loginName)
            throws Exception {
        if (serviceName != null) {
            GlobalConstants.serviceName = serviceName;
        }
        if (bannerName != null) {
            GlobalConstants.bannerName = bannerName;
        }
        if (loginName != null) {
            GlobalConstants.loginName = loginName;
        }
        setupSuite(environment, currentAPIGroupName, currentAPIName,bannerName);
    }

    /**
     * Initialize the extent report before each test method.
     *
     * @param method instance of the {@code Method.class} }
     */
    @BeforeMethod
    public void setupReport(Method method) {
        ExtentTestManager.startTest(method.getName());
    }

    /**
     * Holds results parameters
     *
     * @param result
     */
    @AfterMethod
    protected void afterMethod(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            ExtentTestManager.getTest().log(LogStatus.FAIL, "Test Failed", result.getThrowable());
        } else if (result.getStatus() == ITestResult.SKIP) {
            ExtentTestManager.getTest().log(LogStatus.SKIP, "Test Skipped", result.getThrowable());
        } else {
            ExtentTestManager.getTest().log(LogStatus.PASS, "Test Passed", "Test passed");
        }

        ExtentManager.getReporter().endTest(ExtentTestManager.getTest());
        ExtentManager.getReporter().flush();
    }

    /**
     * Current method is to Print the Stack trace of a thrown Exception
     *
     * @param t
     * @return
     */
    protected String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }
}
