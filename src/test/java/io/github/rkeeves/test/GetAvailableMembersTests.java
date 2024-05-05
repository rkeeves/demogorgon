package io.github.rkeeves.test;

import io.github.rkeeves.lib.api.SurveyApi;
import io.github.rkeeves.lib.arbitraries.SurveyId;
import io.github.rkeeves.lib.dto.Member;
import io.github.rkeeves.lib.dto.Status;
import io.restassured.http.ContentType;
import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.BeforeContainer;
import org.apache.http.HttpStatus;

import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.rkeeves.lib.common.Sets.oneFromIntersection;
import static io.github.rkeeves.lib.common.Sets.setBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class GetAvailableMembersTests {

    static SurveyApi API;

    @BeforeContainer
    static void beforeContainer() {
        API = SurveyApi.fromUnsafePropertyFileIO();
    }

    @Property
    @Label("200 for existing surveys")
    void http200(@ForAll(supplier = SurveyId.Existing.class) int surveyId) {
        API.getAvailableMembers(surveyId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON);
    }

    @Property
    @Label("404 for non existing surveys")
    void http404(@ForAll(supplier = SurveyId.NonExisting.class) int surveyId) {
        API.getAvailableMembers(surveyId)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .contentType(ContentType.JSON);
    }

    @Property
    @Label("available members are unique")
    void theyAreUnique(@ForAll(supplier = SurveyId.Existing.class) int surveyId) {
        var availables = API.getAvailableMembersOk(surveyId).stream().map(Member::getId).collect(Collectors.toList());
        assertThat(
                availables,
                hasSize(equalTo(new HashSet<>(availables).size()))
        );
    }

    @Property
    @Label("inactive members are not available")
    void inactivesAreNotAvailable(@ForAll(supplier = SurveyId.Existing.class) int surveyId) {
        assertThat(
                API.getAvailableMembersOk(surveyId).stream().filter(x -> !x.getActive()).findAny(),
                equalTo(Optional.empty())
        );
    }

    @Property
    @Label("rejected participants are not available")
    void rejectedsAreNotAvailable(@ForAll(supplier = SurveyId.Existing.class) int surveyId) {
        var availables = setBy(Member::getId, API.getAvailableMembersOk(surveyId));
        var rejecteds = setBy(Member::getId, API.getMembersOfSurveyOk(surveyId, Status.REJECTED.name));
        assertThat(
                oneFromIntersection(availables, rejecteds),
                equalTo(Optional.empty())
        );
    }

    @Property
    @Label("filtered participants are not available")
    void filteredAreNotAvailable(@ForAll(supplier = SurveyId.Existing.class) int surveyId) {
        var availables = setBy(Member::getId, API.getAvailableMembersOk(surveyId));
        var filtereds = setBy(Member::getId, API.getMembersOfSurveyOk(surveyId, Status.FILTERED.name));
        assertThat(
                oneFromIntersection(availables, filtereds),
                equalTo(Optional.empty())
        );
    }

    @Property
    @Label("completed participants are not available")
    void completedAreNotAvailable(@ForAll(supplier = SurveyId.Existing.class) int surveyId) {
        var availables = setBy(Member::getId, API.getAvailableMembersOk(surveyId));
        var completeds = setBy(Member::getId, API.getMembersOfSurveyOk(surveyId, Status.COMPLETED.name));
        assertThat(
                oneFromIntersection(availables, completeds),
                equalTo(Optional.empty())
        );
    }
}
