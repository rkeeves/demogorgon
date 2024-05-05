package io.github.rkeeves.test;

import io.github.rkeeves.lib.api.SurveyApi;
import io.github.rkeeves.lib.arbitraries.MemberId;
import io.github.rkeeves.lib.arbitraries.StatusString;
import io.github.rkeeves.lib.arbitraries.SurveyId;
import io.github.rkeeves.lib.common.Sets;
import io.github.rkeeves.lib.dto.Member;
import io.github.rkeeves.lib.dto.Status;
import io.restassured.http.ContentType;
import net.jqwik.api.Assume;
import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.BeforeContainer;
import org.apache.http.HttpStatus;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import static io.github.rkeeves.lib.common.Sets.oneFromIntersection;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

public class GetMembersOfSurveyTests {

    static SurveyApi API;

    @BeforeContainer
    static void beforeContainer() {
        API = SurveyApi.fromUnsafePropertyFileIO();
    }

    @Property
    @Label("404 for non existing survey")
    public void http404(
            @ForAll(supplier = SurveyId.NonExisting.class) int surveyId,
            @ForAll(supplier = StatusString.Valid.class) String status) {
        API.getMembersOfSurvey(surveyId, status)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .contentType(ContentType.JSON);
    }

    @Property
    @Label("400 for invalid status string")
    public void http400(
            @ForAll(supplier = SurveyId.Existing.class) int surveyId,
            @ForAll(supplier = StatusString.Invalid.class) String status) {
        API.getMembersOfSurvey(surveyId, status)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .contentType(ContentType.JSON);
    }

    @Property
    @Label("200 for existing survey and valid status string")
    public void http200(
            @ForAll(supplier = SurveyId.Existing.class) int surveyId,
            @ForAll(supplier = StatusString.Valid.class) String status) {
        API.getMembersOfSurvey(surveyId, status)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON);
    }

    @Property
    @Label("participant count cannot be more than member count")
    public void participantCountLteMemberCount(
            @ForAll(supplier = SurveyId.Existing.class) int surveyId) {
        var participantCount = Stream.of(
                API.getMembersOfSurveyOk(surveyId, Status.NOT_ASKED.name),
                API.getMembersOfSurveyOk(surveyId, Status.REJECTED.name),
                API.getMembersOfSurveyOk(surveyId, Status.FILTERED.name),
                API.getMembersOfSurveyOk(surveyId, Status.COMPLETED.name)
        ).mapToLong(Collection::size).sum();
        assertThat(
                participantCount,
                lessThanOrEqualTo((long) MemberId.CARDINALITY)
        );
    }

    @Property
    @Label("participants can have only one status")
    public void membersHaveOneStatus(
            @ForAll(supplier = SurveyId.Existing.class) int surveyId,
            @ForAll(supplier = StatusString.Valid.class) String statusA,
            @ForAll(supplier = StatusString.Valid.class) String statusB) {
        Assume.that(!statusA.equals(statusB));
        var as = Sets.setBy(Member::getId, API.getMembersOfSurveyOk(surveyId, statusA));
        var bs = Sets.setBy(Member::getId, API.getMembersOfSurveyOk(surveyId, statusB));
        assertThat(
                oneFromIntersection(as, bs),
                equalTo(Optional.empty())
        );
    }
}
