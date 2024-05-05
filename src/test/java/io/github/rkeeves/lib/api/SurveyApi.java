package io.github.rkeeves.lib.api;

import io.github.rkeeves.lib.config.ApiConfig;
import io.github.rkeeves.lib.dto.Member;
import io.github.rkeeves.lib.dto.Survey;
import io.github.rkeeves.lib.dto.SurveyMetric;
import io.github.rkeeves.lib.dto.SurveyPoint;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;

import java.util.List;

import static io.restassured.RestAssured.given;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC, staticName = "fromBaseUri")
public class SurveyApi {

    private final String baseUri;

    public static SurveyApi fromUnsafePropertyFileIO() {
        return SurveyApi.fromBaseUri(ApiConfig.readIO().baseUri());
    }

    public Response getSurveyPoints(int memberId) {
        return given()
                .baseUri(baseUri)
                .accept(ContentType.JSON)
                .when()
                .get("/api/members/{memberId}/points", memberId);
    }

    public List<SurveyPoint> getSurveyPointsOk(int memberId) {
        return getSurveyPoints(memberId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON)
                .extract()
                .body()
                .jsonPath().getList(".", SurveyPoint.class);
    }

    public Response getSurveyStatistics() {
        return given()
                .baseUri(baseUri)
                .accept(ContentType.JSON)
                .when()
                .get("/api/surveys/statistics");
    }

    public List<SurveyMetric> getSurveyStatisticsOk() {
        return getSurveyStatistics()
                .then()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON)
                .extract()
                .body()
                .jsonPath().getList(".", SurveyMetric.class);
    }

    public Response getAvailableMembers(int surveyId) {
        return given()
                .baseUri(baseUri)
                .accept(ContentType.JSON)
                .when()
                .get("/api/surveys/{surveyId}/members/not-invited", surveyId);
    }

    public List<Member> getAvailableMembersOk(int surveyId) {
        return getAvailableMembers(surveyId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON)
                .extract()
                .body()
                .jsonPath().getList(".", Member.class);
    }

    public Response getSurveysOfMember(int memberId) {
        return given()
                .baseUri(baseUri)
                .accept(ContentType.JSON)
                .when()
                .get("/api/members/{memberId}", memberId);
    }

    public List<Survey> getSurveysOfMemberOk(int memberId) {
        return getSurveysOfMember(memberId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON)
                .extract()
                .body()
                .jsonPath().getList(".", Survey.class);
    }

    public Response getMembersOfSurvey(int surveyId, String status) {
        return given()
                .baseUri(baseUri)
                .accept(ContentType.JSON)
                .when()
                .get("/api/surveys/{surveyId}/members?status={status}", surveyId, status);
    }

    public List<Member> getMembersOfSurveyOk(int surveyId, String status) {
        return getMembersOfSurvey(surveyId, status)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON)
                .extract()
                .body()
                .jsonPath().getList(".", Member.class);
    }
}
