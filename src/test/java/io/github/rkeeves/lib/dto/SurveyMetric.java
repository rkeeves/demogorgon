package io.github.rkeeves.lib.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyMetric {
    Integer surveyId;
    String surveyName;
    Integer numberOfCompletes;
    Integer numberOfFiltered;
    Integer numberOfRejected;
    Integer avgLengthOfSurvey;
}
