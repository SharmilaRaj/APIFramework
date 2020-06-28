package com.albertsons.api.framework.support.common;

import com.albertsons.api.framework.support.constants.GlobalConstants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;

import static org.testng.AssertJUnit.fail;

/**
 * This class provided methods to extractor data in a format consumable by test.
 */
public class DataExtractor {

    public JSONObject currentTestDataJSONObj = null;
    public JSONObject currentEnvTokenDataJSONObj = null;
    public JSONArray currentTSTestDataArray = null;
    private AppGeneric appGeneric = new AppGeneric();
    private String testEnvironment;
    private String apiGroupName;
    private String apiName;
    private String bannerName;

    /**
     * Initialise Test Environment.
     *
     * @param testEnvironment
     * @param apiGroupName
     * @param apiName
     */
    public void setup(String testEnvironment, String apiGroupName, String apiName, String bannerName) {
        this.testEnvironment = testEnvironment;
        this.apiGroupName = apiGroupName;
        this.apiName = apiName;
        this.bannerName=bannerName;
    }

    /**
     * A wrapper for get test data with test script name.
     *
     * @param testScriptName
     * @return
     */
    public JSONArray getTestData(String testScriptName) {
        return getJSONParseTestData(testEnvironment, apiGroupName, apiName, testScriptName);
    }

    /**
     * Description: Get Test Data from the respective JSON file for test script under execution
     *
     * @param testEnvironment
     * @param apiGroupName
     * @param apiName
     * @param testScriptName
     * @return
     * @throws Exception
     */
    public JSONArray getJSONParseTestData(
            String testEnvironment, String apiGroupName, String apiName, String testScriptName) {

        String filePath = null;
        // Get current OS Name and path for Test Data
        try {
            if (appGeneric.getHostSysOS().equalsIgnoreCase("mac")) {
                filePath =
                        GlobalConstants.testDataFilePathMac
                                + apiGroupName
                                + "//"
                                + apiName.toUpperCase()
                                + ".json";
            } else if (appGeneric.getHostSysOS().equalsIgnoreCase("windows")) {
                filePath =
                        GlobalConstants.testDataFilePathWindows
                                + apiGroupName
                                + "\\"
                                + apiName.toUpperCase()
                                + ".json";
            } else if ((appGeneric.getHostSysOS().equalsIgnoreCase("linux"))
                    || (appGeneric.getHostSysOS().equalsIgnoreCase("unix"))) {
                filePath =
                        GlobalConstants.testDataFilePathLinux
                                + apiGroupName
                                + "/"
                                + apiName.toUpperCase()
                                + ".json";
            }
            // read the json file
            FileReader reader = new FileReader(filePath);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

            // handle a structure into the json object
            currentTestDataJSONObj = (JSONObject) jsonObject.get(testEnvironment);
            currentTSTestDataArray = (JSONArray) currentTestDataJSONObj.get(testScriptName);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        return (currentTSTestDataArray);
    }

    //read file
    public JSONArray readJson(String filePath, String testEnvironment, String testCaseName) {
        try {
            FileReader reader = new FileReader(filePath);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

            // handle a structure into the json object
            currentTestDataJSONObj = (JSONObject) jsonObject.get(testEnvironment);
            currentTSTestDataArray = (JSONArray) currentTestDataJSONObj.get(testCaseName);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return (currentTSTestDataArray);
    }

    //read file
    public JSONArray readJson(String filePath, String testEnvironment, String bannerName, String testCaseName) {
        JSONArray testCaseArray = null;
        try {
            FileReader reader = new FileReader(filePath);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

            // handle a structure into the json object
            JSONObject environmentObject = (JSONObject) jsonObject.get(testEnvironment);
           /* if (bannerName == null) {
                testCaseArray = (JSONArray) environmentObject.get(testCaseName);
            } else {
                JSONObject bannerObject = (JSONObject) environmentObject.get(bannerName);
                testCaseArray = (JSONArray) bannerObject.get(testCaseName);
            }*/
           System.out.println("environment:"+ testEnvironment);
            System.out.println("bannerName:"+ bannerName);
            JSONObject bannerObject = (JSONObject) environmentObject.get(bannerName);
            testCaseArray = (JSONArray) bannerObject.get(testCaseName);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return testCaseArray;
    }

    //read file for token generation
    public JSONObject readJsonforToken(String filePath, String testEnvironment) {
        try {
            FileReader reader = new FileReader(filePath);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

            // handle a structure into the json object
            currentTestDataJSONObj = (JSONObject) jsonObject.get(testEnvironment);
            //currentTSTestDataArray = (JSONArray) currentTestDataJSONObj.get(testCaseName);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        return (currentTestDataJSONObj);
    }
    public JSONObject readJsonforStoreId(String filePath, String testEnvironment, String bannerName) {
        JSONObject currentObj = null;
        try {
            FileReader reader = new FileReader(filePath);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

            // handle a structure into the json object
            currentTestDataJSONObj = (JSONObject) jsonObject.get(testEnvironment);
            currentObj= (JSONObject) currentTestDataJSONObj.get(bannerName);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        return (currentObj);
    }

    /**
     * Description: Get the Token Information based on Environment
     *
     * @param testEnvironment
     * @param apiGroupName
     * @param apiName
     * @return
     * @throws Exception
     */
    public JSONObject getJSONParseTokenTestData(
            String testEnvironment, String apiGroupName, String apiName) {
        try {
            String filePath = null;
            // Get current OS Name and path for Test Data
            if (appGeneric.getHostSysOS().equalsIgnoreCase("mac")) {
                filePath =
                        GlobalConstants.testDataFilePathMac
                                + apiGroupName
                                + "//"
                                + apiName.toUpperCase()
                                + ".json";
            } else if (appGeneric.getHostSysOS().equalsIgnoreCase("windows")) {
                filePath =
                        GlobalConstants.testDataFilePathWindows
                                + apiGroupName
                                + "\\"
                                + apiName.toUpperCase()
                                + ".json";
            } else if ((appGeneric.getHostSysOS().equalsIgnoreCase("linux"))
                    || (appGeneric.getHostSysOS().equalsIgnoreCase("unix"))) {
                filePath =
                        GlobalConstants.testDataFilePathLinux
                                + apiGroupName
                                + "/"
                                + apiName.toUpperCase()
                                + ".json";
            }
            // read the json file
            FileReader reader = new FileReader(filePath);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            // handle a structure into the json object
            currentEnvTokenDataJSONObj = (JSONObject) jsonObject.get(testEnvironment);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        return (currentEnvTokenDataJSONObj);
    }

    public String getTokenEndPoint(String testEnvironment) {
        switch (testEnvironment.toUpperCase()) {
            case "QA":
                GlobalConstants.endPoint = "https://ngcp-qa2.safeway.com";
                //GlobalConstants.endPoint= "https://b2cgw-qa.safeway.com";
                break;
            case "DEV":
                GlobalConstants.endPoint = "https://ngcp-qi.safeway.com";
                break;
            case "QA2":
                GlobalConstants.endPoint = "https://ngcp-qa2.safeway.com";
                break;
            case "Stage":
                GlobalConstants.endPoint = " https://ngcp-prf.safeway.com";
                break;


        }
        return GlobalConstants.endPoint;
    }

    public String getTestEnvironmentEndPoint(String testEnvironment) {
        switch (testEnvironment.toUpperCase()) {
            case "QAI":
                GlobalConstants.endPoint = "";
                break;
            case "QA1":
                GlobalConstants.endPoint = "https://catalog-service-qa.apps.np.stratus.albertsons.com";;
                break;
            case "QA2":
                GlobalConstants.endPoint = "https://acceptance.apim.azwestus.stratus.albertsons.com/abs/acceptancepub/erums/cartservice/api/v1";
                break;
            case "QA3":
                GlobalConstants.endPoint = "https://product-service-xapi-qa.apps.np.stratus.albertsons.com";
                break;
            case "QA4":
                GlobalConstants.endPoint = "https://catalog-support-qa.apps.np.stratus.albertsons.com";
                break;
            case "PERF":
                GlobalConstants.endPoint = "https://perf.apim.azwestus.stratus.albertsons.com/abs/perfpub/erums/cartservice/api/v1";
                break;
            case "DEV":
                GlobalConstants.endPoint = "https://dev.apim.azwestus.stratus.albertsons.com/erums/cartservice/api/v1";
                break;
            case "QA":
                GlobalConstants.endPoint = "https://qa.apim.azwestus.stratus.albertsons.com/abs/qapub/erums/cartservice/api/v1";
                break;
            case "STAGE":
                GlobalConstants.endPoint = " https://stage.apim.azwestus.stratus.albertsons.com/abs/stgpub/erums/cartservice/api/v1";
                break;
            case "PROD":
                GlobalConstants.endPoint = "https://api.prod.stratus.albertsons.com/abs/pub/erums/cartservice/api/v1";
                break;



        }
        return GlobalConstants.endPoint;
    }

    public String getApiEndpointUrl(String testEnvironment) {
        switch (testEnvironment.toUpperCase()) {
            case "QAI":
                GlobalConstants.endPoint = "";
                break;
            case "QA1":
                GlobalConstants.endPoint = "";
                break;
            case "QA2":
                GlobalConstants.endPoint = "https://acceptance.apim.azwestus.stratus.albertsons.com/abs/acceptancepub/erums/SERVICENAME/api";
                break;
            case "PERF":
                GlobalConstants.endPoint = "https://perf.apim.azwestus.stratus.albertsons.com/abs/perfpub/erums/SERVICENAME/api";
                break;
            case "DEV":
                GlobalConstants.endPoint = "";
                break;
            case "QA":
                GlobalConstants.endPoint = "https://osms-SERVICENAME-qa.apps.np.stratus.albertsons.com/api";
                break;
            case "STAGE":
                GlobalConstants.endPoint = "https://stage.apim.azwestus.stratus.albertsons.com/abs/stgpub/erums/SERVICENAME/api";
                break;
            case "PROD":
                GlobalConstants.endPoint = "https://api.prod.stratus.albertsons.com/abs/pub/erums/SERVICENAME/api";
                break;

        }
        return GlobalConstants.endPoint;
    }

    public String getTestEnvironmentEndPoint(String testEnvironment, String serviceName) {
        String serviceEndPoint = null;
        switch (testEnvironment.toUpperCase()) {
            case "QAI":
                serviceEndPoint = "";
                break;
            case "QA1":
                serviceEndPoint = "";
                break;
            case "QA2":
                serviceEndPoint = "https://acceptance.apim.azwestus.stratus.albertsons.com/abs/acceptancepub/"+serviceName+"/api/v1";
                break;
            case "PERF":
                serviceEndPoint = "https://perf.apim.azwestus.stratus.albertsons.com/abs/perfpub/"+serviceName+"/api/v1";
                break;
            case "DEV":
                serviceEndPoint = "https://dev.apim.azwestus.stratus.albertsons.com/abs/devpub/"+serviceName+"/api/v1";
                break;
            case "QA":
                serviceEndPoint = "https://qa.apim.azwestus.stratus.albertsons.com/abs/qapub/"+serviceName+"/api/v1";
                break;
            case "STAGE":
                serviceEndPoint = "https://stage.apim.azwestus.stratus.albertsons.com/abs/stgpub/"+serviceName+"/api/v1";
                break;
            case "PROD":
                serviceEndPoint = "https://api.prod.stratus.albertsons.com/abs/pub/"+serviceName+"/api/v1";
                break;

            case "SLICE3QA2":
                serviceEndPoint = "https://acceptance.apim.azwestus.stratus.albertsons.com/abs/acceptancepub/"+serviceName+"/api/v1";
                break;

        }
        return serviceEndPoint;
    }

    public String getUCAEndPoint(String testEnvironment) {
        String serviceEndPoint = null;
        switch (testEnvironment.toUpperCase()) {
            case "QA1":
                serviceEndPoint = "https://qa.apim.azwestus.stratus.albertsons.com/abs/qapub/cnc/ucaservice/api/uca/customers/";
                break;
            case "QA2":
                serviceEndPoint = "https://acceptance.apim.azwestus.stratus.albertsons.com/abs/acceptancepub/cnc/ucaservice/api/uca/customers/";
                break;
            case "PERF":
                serviceEndPoint = "https://perf.apim.azwestus.stratus.albertsons.com/abs/perfpub/cnc/ucaservice/api/uca/customers/";
                break;
            case "STAGE":
                serviceEndPoint = "https://api-stage.tomthumb.com/abs/stgpub/api/uca/customers/";
                break;
            case "PROD":
                serviceEndPoint = "https://api-prod.tomthumb.com/abs/pub/api/uca/customers/";
                break;

        }
        return serviceEndPoint;
    }

    public String getTenderIdEndPoint(String testEnvironment, String customerID) {
        String serviceEndPoint = null;
        switch (testEnvironment.toUpperCase()) {
            case "QAI":
                serviceEndPoint = "";
                break;
            case "QA1":
                serviceEndPoint = "";
                break;
            case "QA2":
                serviceEndPoint = "https://acceptance.apim.azwestus.stratus.albertsons.com/api/cwms/customers/" + customerID + "/tenders";
                break;
            case "PERF":
                serviceEndPoint = "https://perf.apim.azwestus.stratus.albertsons.com/api/cwms/customers/" + customerID + "/tenders";
                break;
            case "DEV":
                serviceEndPoint = "";
                break;
            case "QA":
                serviceEndPoint = "https://qa.apim.azwestus.stratus.albertsons.com/api/cwms/customers/" + customerID + "/tenders";
                break;
            case "STAGE":
                serviceEndPoint = "https://stage.apim.azwestus.stratus.albertsons.com/api/cwms/customers/" + customerID + "/tenders";
                break;
            case "PROD":
                serviceEndPoint = "https://prod.apim.azwestus.stratus.albertsons.com/api/cwms/customers/" + customerID + "/tenders";
                break;
        }
        return serviceEndPoint;
    }

    /**
     * Description: Builds file path based on OS
     *
     * @param bodyDataFolderPath
     * @param bodyDatafileName
     * @return
     * @throws Exception
     */
    public File getBodyDataFile(
            String bodyDataFolderPath, String testEnvironment, String bodyDatafileName) throws Exception {
        File bodyDataFile = null;
        try {
            String bodyDataFilePath =
                    GlobalConstants.testDataFilePathWindows
                            + "\\"
                            + bodyDataFolderPath
                            + "\\"
                            + testEnvironment.toLowerCase()
                            + "\\"
                            + bodyDatafileName;

            // Get current OS Name and path for Test Data
            if (appGeneric.getHostSysOS().equalsIgnoreCase("mac")) {
                bodyDataFilePath = bodyDataFilePath.replaceAll("\\\\", "//");
                bodyDataFile = new File(bodyDataFilePath);
            } else if (appGeneric.getHostSysOS().equalsIgnoreCase("windows")) {
                bodyDataFile = new File(bodyDataFilePath);
            } else if ((appGeneric.getHostSysOS().equalsIgnoreCase("linux"))
                    || (appGeneric.getHostSysOS().equalsIgnoreCase("unix"))) {
                bodyDataFilePath = bodyDataFilePath.replaceAll("\\\\", "/");
                bodyDataFile = new File(bodyDataFilePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (bodyDataFile);
    }
}
