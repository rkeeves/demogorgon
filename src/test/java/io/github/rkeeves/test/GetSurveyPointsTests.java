package io.github.rkeeves.test;

import io.github.rkeeves.lib.api.SurveyApi;
import io.github.rkeeves.lib.arbitraries.MemberId;
import io.github.rkeeves.lib.dto.SurveyPoint;
import io.restassured.http.ContentType;
import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.BeforeContainer;
import org.apache.http.HttpStatus;

import java.util.Optional;
import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GetSurveyPointsTests {

    static SurveyApi API;

    @BeforeContainer
    static void beforeContainer() {
        API = SurveyApi.fromUnsafePropertyFileIO();
    }

    @Property
    @Label("200 for existing members")
    void http200(@ForAll(supplier = MemberId.Existing.class) int memberId) {
        API.getSurveyPoints(memberId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON);
    }

    @Property
    @Label("404 for non existing members")
    void http404(@ForAll(supplier = MemberId.NonExisting.class) int memberId) {
        API.getSurveyPoints(memberId)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .contentType(ContentType.JSON);
    }

    @Property
    @Label("member can gain either zero, completion or filtered points")
    void pointIsEitherCompletionPointOrFilteredPoint(@ForAll(supplier = MemberId.Existing.class) int memberId) {
        var surveyPoints = API.getSurveyPointsOk(memberId);
        Predicate<SurveyPoint> hasValidPoint = (sp) -> {
            var x = sp.getPoint();
            var a = sp.getSurvey().getCompletionPoints();
            var b = sp.getSurvey().getFilteredPoints();
            return x == 0 || x == a || x == b;
        };
        assertThat(
                surveyPoints.stream().filter(hasValidPoint.negate()).findAny(),
                equalTo(Optional.empty()));
    }
}
