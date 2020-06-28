package com.albertsons.api.framework.support.common;

import com.albertsons.api.framework.support.constants.GlobalConstants;
import com.albertsons.api.framework.support.constants.ResourceEndpointUri;
import com.albertsons.api.framework.support.constants.TestDataFileNames;
import com.albertsons.api.framework.support.pojo.UserRegistrationDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.repackaged.com.google.gson.JsonObject;
import com.google.appengine.repackaged.com.google.gson.JsonParser;
import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.SSLConfig;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

/**
 * Class contains all operations related to RestAssured Calls.
 */
public class BaseApiTest extends ConfigTestBase {

    protected static final String JSON_SCHEMA_PATH = "./src/test/resources/testdata/jsonschema/";
    protected static final String JSON_TESTDATA_BASE_PATH = "./src/test/resources/testdata/";
    protected static final String JSON_RESPONSE_PATH = "./src/test/resources/testdata/response/";
    protected static final String JSON_BODYDATA_PATH = "./src/test/resources/testdata/ecomintegration/";
    protected static final String JSON_BODYDATA_PATH_LEGACY = "./src/test/resources/testdata/DataMigration/";


    protected String tokenId = null;
    protected JSONObject tokenJsonDataObj;
    protected JSONArray currentTsDataRowsJsonArray;
    protected JSONObject currentTsJsonObject;
    protected String expectedJsonString;
    protected String authenticatedGalleryEndpoint;
    private UserRegistrationDetails tokenResponse;
    protected JsonObject expectedJsonObject;
    protected JsonObject actualJsonObject;
    protected JsonParser jsonParser;
    protected Map<String, Object> queryParams;
    protected Map<String, Object> headerParams;
    protected Map<String, Object> pathParams;
    protected Map<String, String> useracc;
    protected Map<String, String> tokenDetail;
    protected String catalogserviceendpoint;
    protected String gettermsserviceendpoint;
    protected String getsubscriptionuserdetails;
    protected String getplansserviceendpoint;
    protected String getcancellationreasonsendpoint;

    protected BaseApiTest() {
    }

    /**
     * Invokes the service using Rest-Assured API's.
     *
     * @param methodType           the httpd method type
     * @param restServiceUri       the endpoint uri
     * @param queryParams          the query params
     * @param pathParams           the path params
     * @param headerParams         the header params
     * @param getCallParamsProcess the get call param enum type
     * @param pathParamsNames      the path params name
     * @param bodyDataFile         the request body
     * @return the response from the endpoint
     */

    public Response invokeService(
            HTTPMethod methodType,
            String restServiceUri,
            Map<String, Object> queryParams,
            Map<String, Object> pathParams,
            Map<String, Object> headerParams,
            String getCallParamsProcess,
            List<String> pathParamsNames,
            File bodyDataFile) {
        Response response = null;
        RestAssured.useRelaxedHTTPSValidation();//this is required to fix ssl handshake error.
        switch (methodType) {
            case GET:
                resetProxy();
                if (getCallParamsProcess.equalsIgnoreCase("query")) {
                    response =
                            given().params(queryParams).when().get(restServiceUri).then().extract().response();
                } else if (getCallParamsProcess.equalsIgnoreCase("headers")) {
                    response =
                            given().headers(headerParams).when().get(restServiceUri).then().extract().response();
                } else if (getCallParamsProcess.equalsIgnoreCase("path")) {
                    // Below is the method call to append {storeId} to RequestURI
                    restServiceUri = buildPathParameter(restServiceUri, pathParamsNames);
                    response =
                            given().pathParams(pathParams).when().get(restServiceUri).then().extract().response();
                } else if (getCallParamsProcess.equalsIgnoreCase("QueryHeaders")) {
                    response =
                            given()
                                    .params(queryParams)
                                    .headers(headerParams)
                                    .when()
                                    .get(restServiceUri)
                                    .then()
                                    .extract()
                                    .response();
                } else if (getCallParamsProcess.equalsIgnoreCase("QueryPath")) {
                    // Below is the method call to append {storeId} to RequestURI
                    restServiceUri = buildPathParameter(restServiceUri, pathParamsNames);
                    response =
                            given()
                                    .params(queryParams)
                                    .pathParams(pathParams)
                                    .when()
                                    .get(restServiceUri)
                                    .then()
                                    .extract()
                                    .response();
                } else if (getCallParamsProcess.equalsIgnoreCase("PathHeaders")) {
                    // Below is the method call to append {storeId} to RequestURI
                    restServiceUri = buildPathParameter(restServiceUri, pathParamsNames);
                    response =
                            given()
                                    .pathParams(pathParams)
                                    .headers(headerParams)
                                    .when()
                                    .get(restServiceUri)
                                    .then()
                                    .extract()
                                    .response();
                } else if (getCallParamsProcess.equalsIgnoreCase("AllParams")) {

                    // Below is the method call to append {storeId} to RequestURI
                    restServiceUri = buildPathParameter(restServiceUri, pathParamsNames);
                    response =
                            given()
                                    .pathParams(pathParams)
                                    .params(queryParams)
                                    .headers(headerParams)
                                    .when()
                                    .get(restServiceUri)
                                    .then()
                                    .extract()
                                    .response();
                } else if (getCallParamsProcess.equalsIgnoreCase("NoParams")) {
                    response = when().get(restServiceUri).then().extract().response();
                }
                break;

            case POST:
                if (getCallParamsProcess.equalsIgnoreCase("query")) {
                    response =
                            given()
                                    .queryParams(queryParams)
                                    .body(bodyDataFile)
                                    .contentType("application/json")
                                    .when()
                                    .post(restServiceUri);
                } else if (getCallParamsProcess.equalsIgnoreCase("headers")) {
                    response =
                            given()
                                    .headers(headerParams)
                                    .body(bodyDataFile)
                                    .contentType("application/json")
                                    .when()
                                    .post(restServiceUri);


                } else if (getCallParamsProcess.equalsIgnoreCase("path")) {
                    restServiceUri =
                            buildPathParameter(
                                    restServiceUri, pathParamsNames); // to build path string in the url
                    response =
                            given()
                                    .pathParams(pathParams)
                                    .body(bodyDataFile)
                                    .contentType("application/json")
                                    .when()
                                    .post(restServiceUri);

                } else if (getCallParamsProcess.equalsIgnoreCase("QueryHeaders")) {

                    response =
                            given()
                                    .headers(headerParams)
                                    .queryParams(queryParams)
                                    .body(bodyDataFile)
                                    .contentType("application/json")
                                    .when()
                                    .post(restServiceUri);

                } else if (getCallParamsProcess.equalsIgnoreCase("QueryHeaders_Different_ContentType")) {
                    response =
                            given()
                                    .headers(headerParams)
                                    .queryParams(queryParams)
                                    .body(bodyDataFile)
                                    .when()
                                    .post(restServiceUri);

                } else if (getCallParamsProcess.equalsIgnoreCase("QueryPath")) {
                    restServiceUri =
                            buildPathParameter(
                                    restServiceUri, pathParamsNames); // to build path string in the url
                    response =
                            given()
                                    .queryParams(queryParams)
                                    .pathParams(pathParams)
                                    .body(bodyDataFile)
                                    .contentType("application/json")
                                    .when()
                                    .post(restServiceUri);

                } else if (getCallParamsProcess.equalsIgnoreCase("PathHeaders")) {
                    restServiceUri =
                            buildPathParameter(
                                    restServiceUri, pathParamsNames); // to build path string in the url
                    response =
                            given()
                                    .headers(headerParams)
                                    .pathParams(pathParams)
                                    .body(bodyDataFile)
                                    .contentType("application/json")
                                    .when()
                                    .post(restServiceUri);
                }
                break;
            case HEAD:
                break;
            case PUT:
                resetProxy();
                if (getCallParamsProcess.equalsIgnoreCase("query")) { //Query params null - so skips this condition

                    response =
                            given()
                                    .queryParams(queryParams)
                                    .body(bodyDataFile)
                                    .contentType("application/json")
                                    .when()
                                    .put(restServiceUri);
                } else if (getCallParamsProcess.equalsIgnoreCase("headers")) {//This is executed.
                    response =
                            given()
                                    .headers(headerParams)
                                    .body(bodyDataFile)
                                    .contentType("application/json")
                                    .when()
                                    .put(restServiceUri);

                } else if (getCallParamsProcess.equalsIgnoreCase("path")) {
                    restServiceUri =
                            buildPathParameter(
                                    restServiceUri, pathParamsNames); // to build path string in the url
                    response =
                            given()
                                    .pathParams(pathParams)
                                    .body(bodyDataFile)
                                    .contentType("application/json")
                                    .when()
                                    .put(restServiceUri);

                } else if (getCallParamsProcess.equalsIgnoreCase("QueryHeaders")) {

                    response =
                            given()
                                    .headers(headerParams)
                                    .queryParams(queryParams)
                                    .body(bodyDataFile)
                                    .contentType("application/json")
                                    .when()
                                    .put(restServiceUri);

                } else if (getCallParamsProcess.equalsIgnoreCase("QueryPath")) {
                    restServiceUri =
                            buildPathParameter(
                                    restServiceUri, pathParamsNames); // to build path string in the url
                    response =
                            given()
                                    .queryParams(queryParams)
                                    .pathParams(pathParams)
                                    .body(bodyDataFile)
                                    .contentType("application/json")
                                    .when()
                                    .put(restServiceUri);

                } else if (getCallParamsProcess.equalsIgnoreCase("PathHeaders")) {
                    restServiceUri =
                            buildPathParameter(
                                    restServiceUri, pathParamsNames); // to build path string in the url
                    response =
                            given()
                                    .headers(headerParams)
                                    .pathParams(pathParams)
                                    .body(bodyDataFile)
                                    .contentType("application/json")
                                    .when()
                                    .put(restServiceUri);
                }

                break;
            case DELETE:
                if (getCallParamsProcess.equalsIgnoreCase("query")) { //Query params null - so skips this condition
                    response =
                            given()
                                    .queryParams(queryParams)
                                    .when()
                                    .delete(restServiceUri);
                } else if (getCallParamsProcess.equalsIgnoreCase("headers")) {//This is executed.
                    response =
                            given()
                                    .headers(headerParams)
                                    .body(bodyDataFile)
                                    .contentType("application/json")
                                    .when()
                                    .delete(restServiceUri);

                } else if (getCallParamsProcess.equalsIgnoreCase("path")) {
                    restServiceUri =
                            buildPathParameter(
                                    restServiceUri, pathParamsNames); // to build path string in the url
                    response =
                            given()
                                    .pathParams(pathParams)
                                    .when()
                                    .delete(restServiceUri);

                } else if (getCallParamsProcess.equalsIgnoreCase("QueryHeaders")) {

                    response =
                            given()
                                    .headers(headerParams)
                                    .queryParams(queryParams)
                                    .when()
                                    .delete(restServiceUri);

                } else if (getCallParamsProcess.equalsIgnoreCase("QueryPath")) {
                    restServiceUri =
                            buildPathParameter(
                                    restServiceUri, pathParamsNames); // to build path string in the url
                    response =
                            given()
                                    .queryParams(queryParams)
                                    .pathParams(pathParams)
                                    .when()
                                    .delete(restServiceUri);

                } else if (getCallParamsProcess.equalsIgnoreCase("PathHeaders")) {
                    restServiceUri =
                            buildPathParameter(
                                    restServiceUri, pathParamsNames); // to build path string in the url
                    response =
                            given()
                                    .headers(headerParams)
                                    .pathParams(pathParams)
                                    .when()
                                    .delete(restServiceUri);
                }

                break;
            case PATCH:
                if (getCallParamsProcess.equalsIgnoreCase("query")) {
                    response =
                            given()
                                    .queryParams(queryParams)
                                    .body(bodyDataFile)
                                    .contentType("application/json")
                                    .when()
                                    .patch(restServiceUri);
                } else if (getCallParamsProcess.equalsIgnoreCase("headers")) {
                    response =
                            given()
                                    .headers(headerParams)
                                    .body(bodyDataFile)
                                    .contentType("application/json")
                                    .when()
                                    .patch(restServiceUri);


                } else if (getCallParamsProcess.equalsIgnoreCase("path")) {
                    restServiceUri =
                            buildPathParameter(
                                    restServiceUri, pathParamsNames); // to build path string in the url
                    response =
                            given()
                                    .pathParams(pathParams)
                                    .body(bodyDataFile)
                                    .contentType("application/json")
                                    .when()
                                    .patch(restServiceUri);

                } else if (getCallParamsProcess.equalsIgnoreCase("QueryHeaders")) {

                    response =
                            given()
                                    .headers(headerParams)
                                    .queryParams(queryParams)
                                    .body(bodyDataFile)
                                    .contentType("application/json")
                                    .when()
                                    .patch(restServiceUri);

                } else if (getCallParamsProcess.equalsIgnoreCase("QueryPath")) {
                    restServiceUri =
                            buildPathParameter(
                                    restServiceUri, pathParamsNames); // to build path string in the url
                    response =
                            given()
                                    .queryParams(queryParams)
                                    .pathParams(pathParams)
                                    .body(bodyDataFile)
                                    .contentType("application/json")
                                    .when()
                                    .patch(restServiceUri);

                } else if (getCallParamsProcess.equalsIgnoreCase("PathHeaders")) {
                    restServiceUri =
                            buildPathParameter(
                                    restServiceUri, pathParamsNames); // to build path string in the url
                    response =
                            given()
                                    .headers(headerParams)
                                    .pathParams(pathParams)
                                    .body(bodyDataFile)
                                    .contentType("application/json")
                                    .when()
                                    .patch(restServiceUri);
                }
                break;
            default:
                Assert.fail("Unsupported format");
                break;
        }
        return (response);
    }

    RequestSpecBuilder builder;

    public Response invokeService(
            Method methodType,
            String restServiceUri,
            Map<String, String> headerParams,
            Map<String, String> queryParams,
            Map<String, String> pathParams,
            String body) {
        resetProxy();
        builder = new RequestSpecBuilder();
        builder.setBaseUri(restServiceUri);
        builder.setBasePath("");
        if (MapUtils.isNotEmpty(headerParams)) {
            builder.addHeaders(headerParams);
        }
        if (MapUtils.isNotEmpty(queryParams)) {
            builder.addParams(queryParams);
        }
        if (MapUtils.isNotEmpty(pathParams)) {
            builder.addPathParams(pathParams);
        }
        if (StringUtils.isNotBlank(body)) {
            builder.setBody(body);
        }
        return RestAssured.given().spec(builder.build()).when()
                .request(methodType);
    }

    /**
     * Build the path parameter.
     *
     * @param restServiceUri  the endpoint uri
     * @param pathParamsNames the path param names
     * @return the endpoint build with path params
     */
    public String buildPathParameter(String restServiceUri, List<String> pathParamsNames) {
        try {

            for (int cnt = 0; cnt < pathParamsNames.size(); cnt++) {
                String decodedVal = getPathParamDecoded(pathParamsNames.get(cnt).toString());
                if (cnt == 0) {
                    restServiceUri = restServiceUri + decodedVal;
                } else {
                    restServiceUri = restServiceUri + decodedVal + "/";
                }
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        return (restServiceUri);
    }

    /**
     * Return the decoded UTF-8 path param.
     *
     * @param pathParam the path param
     * @return the decoded UTF-8 path param.
     * @throws Exception an unsupported encoding exception
     */
    public String getPathParamDecoded(String pathParam) throws Exception {
        return "{" + (URLDecoder.decode(pathParam, "UTF-8")) + "}";
    }

    /**
     * Returns true if response content type has json.
     *
     * @param response the response from service
     * @return
     */
    public boolean verifyResponseContentType(Response response) {
        boolean retVal = false;
        if (response.contentType().contains("json")) {
            retVal = true;
        } else {
            retVal = false;
        }
        return (retVal);
    }

    /**
     * Returns token created.
     *
     * @return the auth token
     */
    protected String getAuthToken(String username, String password) {
        tokenJsonDataObj =
                dataExtractor.getJSONParseTokenTestData(
                        GlobalConstants.testEnvironment,
                        globalConstants.tokenServiceGroupName,
                        globalConstants.tokenServiceAPIName);
        return (getTokenId(tokenJsonDataObj, username, password));
    }

    /**
     * Gets an authentication based on environment.
     *
     * @param tokenDataJsonObj auth token credential
     * @return the auth token
     */
    private String getTokenId(JSONObject tokenDataJsonObj, String username, String password) {
        RestAssured.reset();
        Response response =
                given()
                        .param("username", username)
                        .param("password", password)
                        .when()
                        .post(tokenDataJsonObj.get("uri").toString())
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        return response.getBody().asString().substring(9).trim();
    }

    /**
     * Gets an authentication from OKTA token based on environment.
     *
     * @param username to use to get token
     * @param password to use to get token
     * @return Token provided by OKTA
     */
    protected String getOktaToken(String username, String password) {
        resetProxy();
        tokenJsonDataObj =
                dataExtractor.getJSONParseTokenTestData(
                        GlobalConstants.testEnvironment,
                        globalConstants.tokenServiceGroupName,
                        globalConstants.tokenServiceAPIName);
        return (getOktaTokenId(tokenJsonDataObj, username, password));
    }

    /**
     * Method to implement retrieval of token from OKTA.
     *
     * @param tokenDataJsonObj is the JSON object parsed
     * @param username         to use to get token
     * @param password         to use to get token
     * @return token parsed from response.
     */
    private String getOktaTokenId(JSONObject tokenDataJsonObj, String username, String password) {

        Response response =
                given()
                        .auth()
                        .preemptive()
                        .basic(
                                tokenDataJsonObj.get("ClientId").toString(),
                                tokenDataJsonObj.get("ClientSecret").toString())
                        .headers(
                                "Accept",
                                tokenDataJsonObj.get("Accept").toString(),
                                "Content-Type",
                                "application/x-www-form-urlencoded")
                        .formParams(
                                "grant_type",
                                tokenDataJsonObj.get("grant_type").toString(),
                                "username",
                                username,
                                "password",
                                password,
                                "scope",
                                tokenDataJsonObj.get("scope").toString())
                        .when()
                        .post(tokenDataJsonObj.get("uri").toString())
                        .then()
                        .extract()
                        .response();
        return response.getBody().jsonPath().getString("access_token");
    }


    public HashMap<String, Object> generateQueryPrams(JSONObject jsonObject) {
        return new Gson().fromJson(jsonObject.toString(), HashMap.class);
    }

    public File generateBodyFile(String filePath, JSONObject orderedJson) throws IOException {
        // Create a new FileWriter object
        FileWriter fileWriter = null;
        fileWriter = new FileWriter(filePath);

        // Writting the jsonObject into sample.json
        fileWriter.write(orderedJson.toJSONString());
        fileWriter.close();
        File bodyFile = new File(filePath);
        return bodyFile;
    }

    public File generateBodyFile(String filePath, JSONArray orderedJson) throws IOException {
        // Create a new FileWriter object
        FileWriter fileWriter = null;
        fileWriter = new FileWriter(filePath);

        // Writting the jsonObject into sample.json
        fileWriter.write(orderedJson.toJSONString());
        fileWriter.close();
        File bodyFile = new File(filePath);
        return bodyFile;
    }

    public Map<String, String> generateTokenForRegisteredUser(String testCaseName) throws Exception {

        //read json file:
        String filePath = JSON_BODYDATA_PATH + TestDataFileNames.JSON_TOKENFILE_PATH_PARTICULARUSER;
        System.out.println("User id file path is " + filePath);
        currentTsDataRowsJsonArray = dataExtractor.readJson(filePath, GlobalConstants.testEnvironment, testCaseName);
        currentTsJsonObject = (JSONObject) currentTsDataRowsJsonArray.get(0);
        System.out.println("Credentials read from file is " + currentTsJsonObject);

        //arrange json object for user id first
        // LinkedHashMap<String, String> jsonOrderedMap = new LinkedHashMap<String, String>();
        //jsonOrderedMap.put("userId",currentTsJsonObject.get("userid").toString());
        //jsonOrderedMap.put("password",currentTsJsonObject.get("password").toString());
        //JSONObject orderedJson = new JSONObject(jsonOrderedMap);
        try {
            //generate run time json
            File tokenDetails = generateBodyFile(TestDataFileNames.JSON_TOKENDATA_PATH, currentTsJsonObject);
            String authenticatedTokenEndpoint1 = dataExtractor.getTokenEndPoint(GlobalConstants.testEnvironment)
                    + ResourceEndpointUri.TOKEN_URI;
            System.out.println("Token End Point is " + authenticatedTokenEndpoint1);

            headerParams.put("Content-Type", "application/json");
            headerParams.put("banner", "tomthumb");
            headerParams.put("appversion", "3.1.0");
            headerParams.put("platform", "aem");
            headerParams.put("Accept", "application/json");

            Response tokenResponse =
                    invokeService(
                            HTTPMethod.POST,
                            authenticatedTokenEndpoint1,
                            null,
                            null,
                            headerParams,
                            GlobalConstants.GetCallArgs.HEADERPARAM.toString(),
                            null,
                            tokenDetails);

            System.out.println("token respone is: " + tokenResponse.getBody().asString());
            String tokenId = tokenResponse.getBody().jsonPath().getString("token");
            useracc = tokenResponse.getBody().jsonPath().get("userAccount");
            String custId = useracc.get("guid");
            System.out.println("token id: " + tokenId);
            System.out.println("customer id: " + custId);
            tokenDetail = new HashMap<String, String>();
            tokenDetail.put("tokenId", tokenId);
            tokenDetail.put("custId", custId);
            return tokenDetail;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tokenDetail;
    }

    public Map<String, String> generateTokenForRegisteredUser1() throws Exception {

        //read json file:
        String filePath = JSON_BODYDATA_PATH + TestDataFileNames.JSON_TOKENFILE_PATH;
        System.out.println("User id file path is " + filePath);
        currentTsJsonObject = dataExtractor.readJsonforToken(filePath, GlobalConstants.testEnvironment);
        //currentTsJsonObject = (JSONObject) currentTsDataRowsJsonArray.get(0);
        System.out.println("Credentials read from file is " + currentTsJsonObject);

        //arrange json object for user id first
        // LinkedHashMap<String, String> jsonOrderedMap = new LinkedHashMap<String, String>();
        //jsonOrderedMap.put("userId",currentTsJsonObject.get("userid").toString());
        //jsonOrderedMap.put("password",currentTsJsonObject.get("password").toString());
        //JSONObject orderedJson = new JSONObject(jsonOrderedMap);
        try {
            //generate run time json
            File tokenDetails = generateBodyFile(TestDataFileNames.JSON_TOKENDATA_PATH, currentTsJsonObject);
            String authenticatedTokenEndpoint1 = dataExtractor.getTokenEndPoint(GlobalConstants.testEnvironment)
                    + ResourceEndpointUri.TOKEN_URI;
            System.out.println("Token End Point is " + authenticatedTokenEndpoint1);

            headerParams.put("Content-Type", "application/json");
            headerParams.put("banner", "tomthumb");
            headerParams.put("appversion", "3.1.0");
            headerParams.put("platform", "aem");
            headerParams.put("Accept", "application/json");

            Response tokenResponse =
                    invokeService(
                            HTTPMethod.POST,
                            authenticatedTokenEndpoint1,
                            null,
                            null,
                            headerParams,
                            GlobalConstants.GetCallArgs.HEADERPARAM.toString(),
                            null,
                            tokenDetails);

            System.out.println("token respone is: " + tokenResponse.getBody().asString());
            String tokenId = tokenResponse.getBody().jsonPath().getString("token");
     /* String extractedValue = DecryptingToken.get("gid",DecryptingToken.getHeaderClaimsJwt(tokenId));
      System.out.println(DecryptingToken.get("gid",DecryptingToken.getHeaderClaimsJwt(tokenId)));*/
            useracc = tokenResponse.getBody().jsonPath().get("userAccount");
            String custId = useracc.get("guid");
            System.out.println("token id: " + tokenId);
            System.out.println("customer id: " + custId);
            tokenDetail = new HashMap<String, String>();
            tokenDetail.put("tokenId", tokenId);
            tokenDetail.put("custId", custId);
            return tokenDetail;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tokenDetail;
    }

    public Map<String, String> generateOktaToken(String testCaseName) {
        String filePath = JSON_BODYDATA_PATH + TestDataFileNames.JSON_OKTA_TOKENFILE_PARTICULARUSER_PATH;
        System.out.println(" OKTA token file path is " + filePath);
        currentTsDataRowsJsonArray = dataExtractor.readJson(filePath, GlobalConstants.testEnvironment, GlobalConstants.bannerName, testCaseName);
        currentTsJsonObject = (JSONObject) currentTsDataRowsJsonArray.get(0);
        System.out.println("Credentials read from file is " + currentTsJsonObject);
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.reset();
        Response response =
                given()
                        .auth()
                        .preemptive()
                        .basic(currentTsJsonObject.get("client_id").toString(),
                                currentTsJsonObject.get("client_secret").toString())
                        .headers(
                                "Accept",
                                "application/json",
                                "Content-Type",
                                "application/x-www-form-urlencoded",
                                "Content",
                                "application/json")
                        .formParams(
                                "username",
                                currentTsJsonObject.get("username").toString(),
                                "password",
                                currentTsJsonObject.get("password").toString(),
                                "grant_type",
                                currentTsJsonObject.get("grant_type").toString(),
                                "scope",
                                currentTsJsonObject.get("scope").toString()
                        )
                        .when()
                        .post(currentTsJsonObject.get("uri").toString())
                        .then()
                        .extract()
                        .response();

        String tokenId = response.getBody().jsonPath().getString("access_token");
        System.out.println("tokenId:" + tokenId);
        String custId = DecryptingToken.get("gid", DecryptingToken.getHeaderClaimsJwt(tokenId));
        System.out.println("Decrepted CustomerID" + custId);

        tokenDetail = new HashMap<String, String>();
        System.out.println("TokenId:" + tokenId);
        tokenDetail.put("tokenId", tokenId);
        tokenDetail.put("custId", custId);
        return tokenDetail;

    }

    public Map<String, String> generateOktaToken1(String testCaseName) {
        String filePath = JSON_BODYDATA_PATH + TestDataFileNames.JSON_OKTA_TOKENFILE_PARTICULARUSER_PATH;
        System.out.println(" OKTA token file path is " + filePath);
        currentTsDataRowsJsonArray = dataExtractor.readJson(filePath, GlobalConstants.testEnvironment, GlobalConstants.bannerName, testCaseName);
        currentTsJsonObject = (JSONObject) currentTsDataRowsJsonArray.get(0);
        System.out.println("Credentials read from file is " + currentTsJsonObject);
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.reset();
        Response response =
                given()
                        .auth()
                        .preemptive()
                        .basic(currentTsJsonObject.get("client_id").toString(),
                                currentTsJsonObject.get("client_secret").toString())
                        .headers(
                                "Accept",
                                "application/json",
                                "Content-Type",
                                "application/x-www-form-urlencoded",
                                "Content",
                                "application/json")
                        .formParams(
                                "username",
                                currentTsJsonObject.get("username").toString(),
                                "password",
                                currentTsJsonObject.get("password").toString(),
                                "grant_type",
                                currentTsJsonObject.get("grant_type").toString(),
                                "scope",
                                currentTsJsonObject.get("scope").toString()
                        )
                        .when()
                        .post(currentTsJsonObject.get("uri").toString())
                        .then()
                        .extract()
                        .response();
        System.out.println("Token StatusCode:" + response.getStatusCode());
        System.out.println("Token Response:" + response.getBody().asString());
        Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK);
        String tokenId = response.getBody().jsonPath().getString("access_token");
        System.out.println("Token Response:" + response.getBody().asString());
        String custId = DecryptingToken.get("gid", DecryptingToken.getHeaderClaimsJwt(tokenId));
        System.out.println("Decrepted CustomerID" + custId);

        tokenDetail = new HashMap<String, String>();
        tokenDetail.put("tokenId", tokenId);
        tokenDetail.put("custId", custId);
        return tokenDetail;

    }

    public Map<String, String> generateOktaTokenForRegistration(String userId, String password) {
        System.out.println("UserName,PAssword:" + userId + "" + password);
        String filePath = JSON_BODYDATA_PATH + TestDataFileNames.JSON_OKTA_TOKENFILE_PATH;
        System.out.println(" OKTA token file path is " + filePath);
        System.out.println(GlobalConstants.testEnvironment);
        JSONObject tokenDataJsonObj = dataExtractor.readJsonforToken(filePath, GlobalConstants.testEnvironment);
        System.out.println("OKTA Credentials read from file is " + tokenDataJsonObj);
        System.out.println("Token URI " + tokenDataJsonObj.get("uri").toString());
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.reset();
        Response response =
                given()
                        .auth()
                        .preemptive()
                        .basic(tokenDataJsonObj.get("client_id").toString(),
                                tokenDataJsonObj.get("client_secret").toString())
                        .headers(
                                "Accept",
                                "application/json",
                                "Content-Type",
                                "application/x-www-form-urlencoded",
                                "Content",
                                "application/json")
                        .formParams(
                                "username",
                                userId,
                                "password",
                                password,
                                "grant_type",
                                tokenDataJsonObj.get("grant_type").toString(),
                                "scope",
                                tokenDataJsonObj.get("scope").toString()
                        )
                        .when()
                        .post(tokenDataJsonObj.get("uri").toString())
                        .then()
                        .extract()
                        .response();
        System.out.println(response.getStatusCode());
        System.out.println(response.getBody().asString());

        String tokenId = response.getBody().jsonPath().getString("access_token");
        String custId = DecryptingToken.get("gid", DecryptingToken.getHeaderClaimsJwt(tokenId));
        System.out.println("Decrepted CustomerID" + custId);

        tokenDetail = new HashMap<String, String>();
        tokenDetail.put("tokenId", tokenId);
        tokenDetail.put("custId", custId);
        return tokenDetail;

    }

    public Map<String, String> generateOktaToken() {
        String filePath = JSON_BODYDATA_PATH + TestDataFileNames.JSON_OKTA_TOKENFILE_PATH;
        System.out.println(" OKTA token file path is " + filePath);
        System.out.println(GlobalConstants.testEnvironment);
        JSONObject tokenDataJsonObj = dataExtractor.readJsonforToken(filePath, GlobalConstants.testEnvironment);
        System.out.println("OKTA Credentials read from file is " + tokenDataJsonObj);
        System.out.println("Token URI " + tokenDataJsonObj.get("uri").toString());
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.reset();
        Response response =
                given()
                        .auth()
                        .preemptive()
                        .basic(tokenDataJsonObj.get("client_id").toString(),
                                tokenDataJsonObj.get("client_secret").toString())
                        .headers(
                                "Accept",
                                "application/json",
                                "Content-Type",
                                "application/x-www-form-urlencoded",
                                "Content",
                                "application/json")
                        .formParams(
                                "username",
                                tokenDataJsonObj.get("username").toString(),
                                "password",
                                tokenDataJsonObj.get("password").toString(),
                                "grant_type",
                                tokenDataJsonObj.get("grant_type").toString(),
                                "scope",
                                tokenDataJsonObj.get("scope").toString()
                        )
                        .when()
                        .post(tokenDataJsonObj.get("uri").toString())
                        .then()
                        .extract()
                        .response();
        System.out.println(response.getStatusCode());
        System.out.println(response.getBody().asString());

        String tokenId = response.getBody().jsonPath().getString("access_token");
        String custId = DecryptingToken.get("gid", DecryptingToken.getHeaderClaimsJwt(tokenId));
        System.out.println("Decrepted CustomerID" + custId);

        tokenDetail = new HashMap<String, String>();
        tokenDetail.put("tokenId", tokenId);
        tokenDetail.put("custId", custId);
        return tokenDetail;

    }

    public String generateUAAToken() throws Exception {

        //read json file:
        String filePath = JSON_BODYDATA_PATH + TestDataFileNames.JSON_UAA_TOKENFILE_PATH;
        System.out.println("UAA token file path is " + filePath);
        JSONObject tokenDataJsonObj = dataExtractor.readJsonforToken(filePath, GlobalConstants.testEnvironment);
        System.out.println("UAA Credentials read from file is " + tokenDataJsonObj);

        Response response =
                given()
                        .auth()
                        .preemptive()
                        .basic(
                                tokenDataJsonObj.get("client_id").toString(),
                                tokenDataJsonObj.get("client_secret").toString())
                        .headers(
                                "Accept",
                                "application/json",
                                "Content-Type",
                                "application/x-www-form-urlencoded")
                        .formParams(
                                "grant_type",
                                "client_credentials",
                                "response_type",
                                "token")
                        .when()
                        .post(tokenDataJsonObj.get("uri").toString())
                        .then()
                        .extract()
                        .response();
        return response.getBody().jsonPath().getString("access_token");
    }

    public Map<String, String> generateLegacyToken(JSONObject obj) throws Exception {


        //arrange json object for user id first
        LinkedHashMap<String, String> jsonOrderedMap = new LinkedHashMap<String, String>();

        jsonOrderedMap.put("userId", obj.get("userid").toString());
        jsonOrderedMap.put("password", obj.get("password").toString());
        JSONObject orderedJson = new JSONObject(jsonOrderedMap);
        try {
            //generate run time json
            File tokenDetails = generateBodyFile(TestDataFileNames.JSON_TOKENDATA_PATH_LEGACY, orderedJson);
            String authenticatedTokenEndpoint1 = "https://ngcp-qa2.safeway.com/iaaw/service/authenticate";

            headerParams.put("Content-Type", "application/json");
            headerParams.put("X-SWY_BANNER", "safeway");
            headerParams.put("Accept", "application/json");

            Response tokenResponse =
                    invokeService(
                            HTTPMethod.POST,
                            authenticatedTokenEndpoint1,
                            null,
                            null,
                            headerParams,
                            GlobalConstants.GetCallArgs.HEADERPARAM.toString(),
                            null,
                            tokenDetails);

            System.out.println("token: " + tokenResponse.getBody().asString());
            String tokenId = tokenResponse.getBody().jsonPath().getString("token");
            useracc = tokenResponse.getBody().jsonPath().get("userAccount");
            String custId = useracc.get("guid");
            System.out.println("token id: " + tokenId);
            System.out.println("customer id: " + custId);
            tokenDetail = new HashMap<String, String>();
            tokenDetail.put("tokenId", tokenId);
            tokenDetail.put("custId", custId);
            return tokenDetail;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tokenDetail;
    }

    /**
     * Converts the Json string to pojo.
     *
     * @param jsonAsString the response from the service
     * @param classType    the generic class type
     * @return the generic class
     */
    protected <T> T stringToPojo(String jsonAsString, Class<T> classType) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonAsString, classType);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        return null;
    }

    /**
     * Creates a string from JSON.
     *
     * @param resource the file name
     * @return json as string
     */
    protected String getJsonAsString(String resource) {
        JSONParser parser = new JSONParser();
        Object obj = null;
        try {
            obj = parser.parse(new FileReader(resource));
        } catch (IOException | ParseException e) {
            Assert.fail(e.getMessage());
        }
        JSONObject jsonObject = (JSONObject) obj;
        return jsonObject.toJSONString();
    }

    /**
     * Creates a string from JSON.
     *
     * @param resource the file name
     * @return json as string
     */
    protected String getJsonArrayAsString(String resource) {
        JSONParser parser = new JSONParser();
        Object obj = null;
        try {
            obj = parser.parse(new FileReader(resource));
        } catch (IOException | ParseException e) {
            Assert.fail(e.getMessage());
        }
        JSONArray jsonObject = (JSONArray) obj;
        return jsonObject.toJSONString();
    }

  /*public void getCartByCustId(String custId){
    Response response2 =
              invokeService(
                      HTTPMethod.GET,
                      authenticatedGalleryEndpoint+ResourceEndpointUri.GET_CUSTOMER+"/"+custId,
                      queryParams,
                      null,
                      headerParams,
                      GlobalConstants.GetCallArgs.QUERYPARAM.toString(),
                      null,
                      null);

      getRegisteredCartToDelete=stringToPojo(response2.getBody().asString(),GetCartDetails.class);
      int cartId=getRegisteredCartToDelete.getCartId();
      //call method to delete cart id
      System.out.println("endpoint to delete: "+authenticatedGalleryEndpoint+"/"+registerCartId);
      Response deleteCart =
              invokeService(
                      HTTPMethod.DELETE,
                      authenticatedGalleryEndpoint+"/"+registerCartId,
                      null,
                      null,
                      headerParams,
                      GlobalConstants.GetCallArgs.HEADERPARAM.toString(),
                      null,
                      null);


  }*/

    /**
     * Set the proxy globally.
     */
    protected void setProxy() {
        RestAssured.proxy("xphxbc02-int.safeway.com", Integer.parseInt("8080"));
    }

    /**
     * Reset the Proxy globally.
     */
    protected void resetProxy() {
        RestAssured.reset();
    }

    /**
     * Sets the SSL Config.
     */
    protected void setSslConfig() {
        RestAssured.config = RestAssured.config().sslConfig(SSLConfig.sslConfig().allowAllHostnames());
    }


    public String getDateAfter() {
        Date date = new Date();
        int random21 = RandomUtils.nextInt(1, 7);
        long randomDay21 = date.getTime() + TimeUnit.DAYS.toMillis(random21);
        date.setTime(randomDay21);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println("SlotDate= " + date);
        return dateFormat.format(date);
    }

    // generate coaID
    public String generateCoaID() {
        return RandomStringUtils.randomAlphanumeric(36);

    }

    public void setSubscriptionKeyHeaderParam(String environment, boolean isXapi, Map<String, Object> headerPrams) {
        if (isXapi) {
            if (environment.equals("QA")) {
                headerPrams.put(GlobalConstants.Ocp_Apim_Subscription_Key, GlobalConstants.Ocp_Apim_Subscription_xapi_QA_Value);
                System.out.println("key is" + GlobalConstants.Ocp_Apim_Subscription_xapi_QA_Value);
            } else if (environment.equals("QA2")) {
                headerPrams.put(GlobalConstants.Ocp_Apim_Subscription_Key, GlobalConstants.Ocp_Apim_Subscription_xapi_QA2_Value);
                System.out.println("key is" + GlobalConstants.Ocp_Apim_Subscription_xapi_QA2_Value);
            } else if (environment.equals("STAGE")) {
                headerPrams.put(GlobalConstants.Ocp_Apim_Subscription_Key, GlobalConstants.Ocp_Apim_Subscription_Xapi_STAGE_Value);
                System.out.println("key is" + GlobalConstants.Ocp_Apim_Subscription_Xapi_STAGE_Value);
            } else if (environment.equals("PROD")) {
                headerPrams.put(GlobalConstants.Ocp_Apim_Subscription_Key, GlobalConstants.Ocp_Apim_Subscription_Xapi_PROD_Value);
                System.out.println("key is" + GlobalConstants.Ocp_Apim_Subscription_Xapi_PROD_Value);

            } else if (environment.equals("PERF")) {
                headerPrams.put(GlobalConstants.Ocp_Apim_Subscription_Key, GlobalConstants.Ocp_Apim_Subscription__Xapi_PERF_Value);
                System.out.println("key is" + GlobalConstants.Ocp_Apim_Subscription__Xapi_PERF_Value);
            }
        } else {
            if (environment.equals("QA")) {
                headerPrams.put(GlobalConstants.Ocp_Apim_Subscription_Key, GlobalConstants.Ocp_Apim_Subscription_QA_Value);
                System.out.println("key is" + GlobalConstants.Ocp_Apim_Subscription_QA_Value);
            } else if (environment.equals("QA2")) {
                headerPrams.put(GlobalConstants.Ocp_Apim_Subscription_Key, GlobalConstants.Ocp_Apim_Subscription_QA2_Value);
                System.out.println("key is" + GlobalConstants.Ocp_Apim_Subscription_QA2_Value);
            } else if (environment.equals("STAGE")) {
                headerPrams.put(GlobalConstants.Ocp_Apim_Subscription_Key, GlobalConstants.Ocp_Apim_Subscription_STAGE_Value);
                System.out.println("key is" + GlobalConstants.Ocp_Apim_Subscription_STAGE_Value);

            } else if (environment.equals("PROD")) {
                headerPrams.put(GlobalConstants.Ocp_Apim_Subscription_Key, GlobalConstants.Ocp_Apim_Subscription_PROD_Value);
                System.out.println("key is" + GlobalConstants.Ocp_Apim_Subscription_PROD_Value);

            } else if (environment.equals("PERF")) {
                headerPrams.put(GlobalConstants.Ocp_Apim_Subscription_Key, GlobalConstants.Ocp_Apim_Subscription__PERF_Value);
                System.out.println("key is" + GlobalConstants.Ocp_Apim_Subscription__PERF_Value);
            }


        }
    }
}
