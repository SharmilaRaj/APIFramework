package com.albertsons.api.framework.support.common;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.Map;
import org.json.simple.JSONObject;

/**
 * Created by kedupuganti on 8/18/2017. Description: Contains all the generic methods around Rest
 * Assured API
 */
public class Generic {

  // Constructor
  public Generic() {}

  /**
   * Description: Verify Single JSON Element value
   *
   * @param testURL
   * @param targetJsonNode
   * @param targetTextToVerify
   * @return
   * @throws Exception
   */
  public boolean verifySingleValueInJSONElement(
      String testURL, String targetJsonNode, String targetTextToVerify) throws Exception {
    boolean retval;
    try {
      given().get(testURL).then().body(targetJsonNode, equalTo(targetTextToVerify));
      retval = true;
    } catch (Exception e) {
      retval = false;
    }
    return (retval);
  }

  /**
   * Descrption: Verify Multiple JSON Element values in one statement , CURRENTLY THIS IS NOT
   * WORKING
   *
   * @param testURL
   * @param targetJsonNode
   * @param targetValuesToVerify
   * @return
   * @throws Exception
   */
  public boolean verifyMultipleValuesInJSONElement(
      String testURL, String targetJsonNode, String... targetValuesToVerify) throws Exception {
    boolean retval;
    try {
      given().get(testURL).then().assertThat().body(targetJsonNode, hasItems(targetValuesToVerify));
      retval = true;
    } catch (Exception e) {
      retval = false;
    }
    return (retval);
  }

  /**
   * Description: Verify the Content type based on the received response
   * @param response
   * @return
   * @throws Exception
   */
  public boolean verifyResponseContentType(Response response) throws Exception {
    boolean retVal = false;
    try{
      if (response.contentType().contains("json")){
        System.out.println("contenttype:" + response.contentType().toString());
        retVal = true;
      } else {
        retVal = false;
      }

    }catch (Exception e){
      e.printStackTrace();
    }
    return(retVal);
  }

  /**
   * Description: Verify Response ContentType using Query or Headers one at a time
   *
   * @param serviceURL
   * @param getCallParams
   * @param getCallParamsProcess
   * @return
   * @throws Exception
   */
  public boolean verifyResponseContentType(
      String serviceURL, Map<String, String> getCallParams, String getCallParamsProcess)
      throws Exception {
    boolean retVal = false;

    try {
      if (getCallParamsProcess.equalsIgnoreCase("query")) {

        given()
            .params(getCallParams)
            .when()
            .get(serviceURL)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
        retVal = true;
      } else if (getCallParamsProcess.equalsIgnoreCase("headers")) {
        given()
            .headers(getCallParams)
            .when()
            .get(serviceURL)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
        retVal = true;
      } else if (getCallParamsProcess.equalsIgnoreCase("path")) {
        given()
            .pathParams(getCallParams)
            .when()
            .get(serviceURL)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
        retVal = true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return (retVal);
  }

  /**
   * Description: Verify Response ContentType using Query and Headers at a time
   *
   * @param serviceURL
   * @param queryParams
   * @param headerParams
   * @return
   * @throws Exception
   */
  public boolean verifyResponseContentType(
      String serviceURL, Map<String, String> queryParams, Map<String, String> headerParams)
      throws Exception {
    boolean retVal = false;
    try {
      given()
          .params(queryParams)
          .headers(headerParams)
          .when()
          .get(serviceURL)
          .then()
          .statusCode(200)
          .contentType(ContentType.JSON);
      retVal = true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return (retVal);
  }

  /**
   * Description: Verify the Content Type of the Response by Query , Path and Header Params
   *
   * @param serviceURL
   * @param queryParams
   * @param headerParams
   * @param pathParams
   * @return
   * @throws Exception
   */
  public boolean verifyResponseContentType(
      String serviceURL,
      Map<String, String> queryParams,
      Map<String, String> headerParams,
      Map<String, String> pathParams)
      throws Exception {
    boolean retVal = false;
    try {
      given()
          .params(queryParams)
          .headers(headerParams)
          .pathParams(pathParams)
          .when()
          .get(serviceURL)
          .then()
          .statusCode(200)
          .contentType(ContentType.JSON);
      retVal = true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return (retVal);
  }

  /**
   * Description: Verify the Response Schema with Predefined Input Schema
   *
   * @param serviceURL
   * @param queryParams
   * @param jsonFilePath
   * @return
   * @throws Exception
   */
  public boolean verifyJSONSchema(
      String serviceURL, Map<String, String> queryParams, String jsonFilePath) throws Exception {
    boolean retVal = false;
    try {
      given()
          .params(queryParams)
          .when()
          .get(serviceURL)
          .then()
          .assertThat()
          .body(matchesJsonSchemaInClasspath(jsonFilePath));
      retVal = true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return (retVal);
  }

  /**
   * Description: Verify the Response Schema with Predefined Input Schema
   *
   * @param serviceURL
   * @param jsonFilePath
   * @return
   * @throws Exception
   */
  public boolean verifyJSONSchema(String serviceURL, String jsonFilePath) throws Exception {
    boolean retVal = false;
    try {
      given().get(serviceURL).then().assertThat().body(matchesJsonSchemaInClasspath(jsonFilePath));
      retVal = true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return (retVal);
  }

  /**
   * Description: Verify the Status Code Got from the Service Call Response
   *
   * @param responseStatusCode
   * @return
   * @throws Exception
   */
  public boolean verifyStatusCode(Integer responseStatusCode) throws Exception {
    boolean retVal = false;
    switch (responseStatusCode) {
      case 200:
      case 201:
        retVal = true;
        break;
      case 400:
      case 401:
      case 403:
      case 404:
      case 407:
      case 408:
        retVal = false;
        break;
      default:
        retVal = false;
        break;
    }
    return (retVal);
  }

  /**
   * Description: It allows you to get the Token Authentication based on given User Name and
   * Password
   *
   * @param tokenDataJsonObj
   * @return
   * @throws Exception
   */
  public String getTokenId(JSONObject tokenDataJsonObj) throws Exception {
    String tokenID = null;
    try {

      Response response =
          given()
              .param("username", tokenDataJsonObj.get("username").toString())
              .param("password", tokenDataJsonObj.get("password").toString())
              .when()
              .post(tokenDataJsonObj.get("uri").toString())
              .then()
              .statusCode(200)
              .extract()
              .response();
      String responseAsString = response.asString();
      tokenID = responseAsString.substring(responseAsString.indexOf("=") + 1).trim();
      System.out.println("TokenID:::" + tokenID);

    } catch (Exception e) {
      e.printStackTrace();
    }

    return (tokenID);
  }
}
