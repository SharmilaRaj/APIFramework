package com.albertsons.api.framework.support.common;

import com.albertsons.api.framework.support.constants.GlobalConstants;
import com.albertsons.api.framework.support.reportgeneration.ExtentTestManager;
import com.relevantcodes.extentreports.LogStatus;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.testng.Reporter;

/** Created by Kiran on 8/19/2017. */
public class AppGeneric {

  public GlobalConstants globalConstants = new GlobalConstants();

  public AppGeneric() {}

  /**
   * Description:
   * @param testStepName
   * @param testStepPassDetails
   * @param testStepFailDetails
   * @throws Exception
   */
  public void assignDetails(String testStepName,String testStepPassDetails,String testStepFailDetails) throws Exception {
    globalConstants.stepDescription = testStepName;
    globalConstants.stepPassActual = testStepPassDetails;
    globalConstants.stepFailActual = testStepFailDetails;
  }

  /**
   * Description:
   * @param testStepStatus
   * @throws Exception
   * Comments:
   * */
  public void generateExtentReport(String testStepStatus,String testName) throws Exception{

    if (testStepStatus.equalsIgnoreCase("Pass")){
      ExtentTestManager.getTest().log(LogStatus.PASS, globalConstants.stepDescription,globalConstants.stepPassActual);
      Reporter.log(globalConstants.stepDescription + "@@@" + globalConstants.stepPassActual);
    } else if(testStepStatus.equalsIgnoreCase("Fail")){
      // String screenShotPath = getScreenShotByExtent(testName+"_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
      //ExtentTestManager.getTest().log(LogStatus.FAIL,GlobalConstants.stepDescription,GlobalConstants.stepFailActual+ExtentTestManager.getTest().addBase64ScreenShot(screenShotPath));
      ExtentTestManager.getTest().log(LogStatus.FAIL,globalConstants.stepDescription,globalConstants.stepFailActual);
    }else if(testStepStatus.equalsIgnoreCase("Info")){
      ExtentTestManager.getTest().log(LogStatus.INFO,globalConstants.stepDescription,globalConstants.stepPassActual);
      Reporter.log(globalConstants.stepDescription + "@@@" + globalConstants.stepPassActual);
    }else if(testStepStatus.equalsIgnoreCase("Skip")){
      ExtentTestManager.getTest().log(LogStatus.SKIP,globalConstants.stepDescription,globalConstants.stepPassActual);
      Reporter.log(globalConstants.stepDescription + "@@@" + globalConstants.stepPassActual);
    }

  }


  /**
   * * Description: Get Host System OS Name
   *
   * @return
   * @throws Exception
   */
  public String getHostSysOS() throws Exception {

    if (System.getProperty("os.name").indexOf("Mac") >= 0) {
      globalConstants.hostOSName = "MAC";
    } else if (System.getProperty("os.name").indexOf("Windows") >= 0) {
      globalConstants.hostOSName = "WINDOWS";
    } else if (System.getProperty("os.name").indexOf("nux") >= 0) {
      globalConstants.hostOSName = "LINUX";
    } else if (System.getProperty("os.name").indexOf("nix") >= 0) {
      globalConstants.hostOSName = "UNIX";
    }
    return (globalConstants.hostOSName);
  }

  public String getComputerName() throws Exception {

    String hostname = "Unknown";
    try {
      InetAddress addr;
      addr = InetAddress.getLocalHost();
      hostname = addr.getHostName();
    } catch (UnknownHostException ex) {
      ex.fillInStackTrace();
      System.out.println("Hostname can not be resolved");
    }
    return (hostname);
  }
}
