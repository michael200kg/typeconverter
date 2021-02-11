package com.utils.oraclescriptconverter;

import com.utils.oraclescriptconverter.configuration.Configuration;
import com.utils.oraclescriptconverter.model.TableConfig;

import java.util.Objects;

public class OraclePostgresScriptConverter {
    public static void main(String[] args) {
        // String fileName = "c:\\dump\\oracle_dump.sql";
        // String configFileName = "c:\\dump\\converter.cfg.sql";
        if (Objects.nonNull(args) &&
                args.length == 2) {
            Configuration configuration = new Configuration(args[0]);
            Transformations.fixComments(args[1]);
            for (String excludeTable : configuration.getExcludedTables()) {
                Transformations.excludeUnnecessaryTable(args[1], excludeTable);
                System.out.printf("Table %s excluded!%n", excludeTable);
            }
            for (TableConfig tc : configuration.getTableConfig()) {
                Transformations.replaceBooleanValues(args[1], tc.getTableName(), tc.getFields());
                System.out.printf("Table name: %s, fields: %s%n", tc.getTableName(), tc.getFields());
            }
        } else {
            System.out.println("Invalid arguments!");
            System.out.println("Usage: java -jar typeconverter-1.0.jar <config file> <file to be converted>");
        }
    }
}
