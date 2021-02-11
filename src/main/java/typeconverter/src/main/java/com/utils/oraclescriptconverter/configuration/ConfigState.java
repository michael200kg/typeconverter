package com.utils.oraclescriptconverter.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class ConfigState {
    private Boolean insideBooleanSection;
    private Boolean insideExcludeSection;
}
