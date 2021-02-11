package com.utils.oraclescriptconverter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TableConfig {
    String tableName;
    String fields;
}
