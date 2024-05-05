package io.github.rkeeves.lib.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Survey {
    Integer id;
    String name;
    Integer expectedCompletes;
    Integer completionPoints;
    Integer filteredPoints;
}
