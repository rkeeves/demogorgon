package io.github.rkeeves.test;

import io.github.rkeeves.lib.api.SurveyApi;
import io.github.rkeeves.lib.arbitraries.MemberId;
import io.github.rkeeves.lib.arbitraries.SurveyId;
import io.github.rkeeves.lib.dto.Member;
import io.github.rkeeves.lib.dto.Status;
import io.github.rkeeves.lib.dto.Survey;
import io.restassured.http.ContentType;
import net.jqwik.api.Assume;
import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.BeforeContainer;
import org.apache.http.HttpStatus;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

public class GetSurveysOfMemberTests {

    static SurveyApi API;

    @BeforeContainer
    static void beforeContainer() {
        API = SurveyApi.fromUnsafePropertyFileIO();
    }

    @Property
    @Label("404 for non existing member")
    public void http404(
            @ForAll(supplier = MemberId.NonExisting.class) int memberId) {
        API.getSurveysOfMember(memberId)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .contentType(ContentType.JSON);
    }

    @Property
    @Label("200 for existing member")
    public void http200(
            @ForAll(supplier = MemberId.Existing.class) int memberId) {
        API.getSurveysOfMember(memberId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON);
    }

    @Property
    @Label("survey does not show up for not asked participants")
    public void notAskedsAreNotParticipants(
            @ForAll(supplier = SurveyId.Existing.class) int surveyId,
            @ForAll(supplier = MemberId.Existing.class) int memberId) {
        var memberIds = Stream.of(
                        API.getMembersOfSurveyOk(surveyId, Status.REJECTED.name),
                        API.getMembersOfSurveyOk(surveyId, Status.FILTERED.name),
                        API.getMembersOfSurveyOk(surveyId, Status.COMPLETED.name)
                ).flatMap(Collection::stream)
                .map(Member::getId)
                .collect(Collectors.toSet());
        Assume.that(!memberIds.contains(memberId));

        var surveyIds = API.getSurveysOfMemberOk(memberId).stream().map(Survey::getId).collect(Collectors.toSet());
        assertThat(
                surveyIds,
                not(hasItem(surveyId))
        );
    }

    @Property
    @Label("survey shows up for rejected participants")
    public void rejectedsAreParticipants(@ForAll(supplier = SurveyId.Existing.class) int surveyId) {
        var members = API.getMembersOfSurveyOk(surveyId, Status.REJECTED.name);
        Assume.that(!members.isEmpty());
        var surveyIds = API.getSurveysOfMemberOk(members.get(0).getId()).stream().map(Survey::getId).collect(Collectors.toSet());
        assertThat(
                surveyIds,
                hasItem(surveyId)
        );
    }

    @Property
    @Label("survey shows up for filtered participants")
    public void filteredsAreParticipants(@ForAll(supplier = SurveyId.Existing.class) int surveyId) {
        var members = API.getMembersOfSurveyOk(surveyId, Status.FILTERED.name);
        Assume.that(!members.isEmpty());
        var surveyIds = API.getSurveysOfMemberOk(members.get(0).getId()).stream().map(Survey::getId).collect(Collectors.toSet());
        assertThat(
                surveyIds,
                hasItem(surveyId)
        );
    }

    @Property
    @Label("survey shows up for completed participants")
    public void completedsAreParticipants(@ForAll(supplier = SurveyId.Existing.class) int surveyId) {
        var members = API.getMembersOfSurveyOk(surveyId, Status.COMPLETED.name);
        Assume.that(!members.isEmpty());
        var surveyIds = API.getSurveysOfMemberOk(members.get(0).getId()).stream().map(Survey::getId).collect(Collectors.toSet());
        assertThat(
                surveyIds,
                hasItem(surveyId)
        );
    }
}
