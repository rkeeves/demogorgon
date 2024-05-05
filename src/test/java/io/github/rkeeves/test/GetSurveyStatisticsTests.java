package io.github.rkeeves.test;

import io.github.rkeeves.lib.api.SurveyApi;
import io.github.rkeeves.lib.arbitraries.MemberId;
import io.github.rkeeves.lib.arbitraries.SurveyId;
import io.github.rkeeves.lib.dto.Status;
import io.github.rkeeves.lib.dto.SurveyMetric;
import io.restassured.http.ContentType;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.BeforeContainer;
import org.apache.http.HttpStatus;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.rkeeves.lib.common.Predicates.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GetSurveyStatisticsTests {

    static SurveyApi API;

    @BeforeContainer
    static void beforeContainer() {
        API = SurveyApi.fromUnsafePropertyFileIO();
    }

    @Example
    @Label("always 200")
    void http200() {
        API.getSurveyStatistics()
                .then()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON);
    }

    @Example
    @Label("Contains unique surveyIds")
    void surveyIdsAreUnique() {
        var xs = API.getSurveyStatisticsOk();
        assertThat(xs,
                hasSize(equalTo(xs.stream().map(SurveyMetric::getSurveyId).collect(Collectors.toSet()).size()))
        );
    }

    @Example
    @Label("there is at least one survey")
    void returnsNonEmptyArray() {
        assertThat(API.getSurveyStatisticsOk(), not(empty()));
    }

    @Example
    @Label("0 <= numberOfRejected <= member count")
    void numberOfRejected() {
        assertThat(
                API.getSurveyStatisticsOk().stream()
                        .filter(contramap(SurveyMetric::getNumberOfRejected, outside(0, MemberId.CARDINALITY)))
                        .findAny(),
                equalTo(Optional.empty())
        );
    }

    @Example
    @Label("0 <= numberOfFiltered <= member count")
    void numberOfFiltered() {
        assertThat(
                API.getSurveyStatisticsOk().stream()
                        .filter(contramap(SurveyMetric::getNumberOfFiltered, outside(0, MemberId.CARDINALITY)))
                        .findAny(),
                equalTo(Optional.empty())
        );
    }

    @Example
    @Label("0 <= numberOfCompletes <= member count")
    void numberOfCompletes() {
        assertThat(
                API.getSurveyStatisticsOk().stream()
                        .filter(contramap(SurveyMetric::getNumberOfCompletes, outside(0, MemberId.CARDINALITY)))
                        .findAny(),
                equalTo(Optional.empty())
        );
    }

    static Function<SurveyMetric, Integer> SUM_OF_COUNTERS = sm -> sm.getNumberOfRejected() + sm.getNumberOfFiltered() + sm.getNumberOfCompletes();

    @Example
    @Label("0 <= sum numberOfCompletes numberOfFiltered numberOfRejected <= member count")
    void sumOfCompletesRejectedFiltered() {
        assertThat(
                API.getSurveyStatisticsOk().stream()
                        .filter(contramap(SUM_OF_COUNTERS, outside(0, MemberId.CARDINALITY)))
                        .findAny(),
                equalTo(Optional.empty())
        );
    }

    @Example
    @Label("a survey exists which has zero participants, this enables us to see whether average calculation divides by zero")
    void averageCalculationDivisionByZeroMustBeTestable() {
        assertThat(
                API.getSurveyStatisticsOk().stream()
                        .filter(contramap(SUM_OF_COUNTERS, eq(0)))
                        .findAny(),
                not(equalTo(Optional.empty()))
        );
    }

    @Example
    @Label("avgLengthOfSurvey cannot be less than zero")
    void avgLengthOfSurvey() {
        assertThat(
                API.getSurveyStatisticsOk().stream()
                        .filter(contramap(SurveyMetric::getAvgLengthOfSurvey, lt(0)))
                        .findAny(),
                equalTo(Optional.empty())
        );
    }

    static SurveyMetric surveyMetricById(int surveyId) {
        return API.getSurveyStatisticsOk().stream().filter(e -> e.getSurveyId().equals(surveyId))
                .findAny().orElseThrow();
    }

    @Property
    @Label("rejected count is the same both in the metric and in the members of survey")
    public void rejectedLength(@ForAll(supplier = SurveyId.Existing.class) int surveyId) {
        assertThat(
                surveyMetricById(surveyId).getNumberOfRejected(),
                equalTo(
                        API.getMembersOfSurveyOk(surveyId, Status.REJECTED.name).size()
                )
        );
    }

    @Property
    @Label("filtered count is the same both in the metric and in the members of survey")
    public void filteredLength(@ForAll(supplier = SurveyId.Existing.class) int surveyId) {
        assertThat(
                surveyMetricById(surveyId).getNumberOfFiltered(),
                equalTo(
                        API.getMembersOfSurveyOk(surveyId, Status.FILTERED.name).size()
                )
        );
    }

    @Property
    @Label("completed count is the same both in the metric and in the members of survey")
    public void completedLength(@ForAll(supplier = SurveyId.Existing.class) int surveyId) {
        assertThat(
                surveyMetricById(surveyId).getNumberOfCompletes(),
                equalTo(
                    API.getMembersOfSurveyOk(surveyId, Status.COMPLETED.name).size()
                )
        );
    }
}
