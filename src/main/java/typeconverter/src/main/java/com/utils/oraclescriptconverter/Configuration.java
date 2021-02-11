package com.utils.oraclescriptconverter;

import com.utils.oraclescriptconverter.model.TableConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.utils.oraclescriptconverter.Constants.BEGIN_BOOLEAN_TAG;
import static com.utils.oraclescriptconverter.Constants.BEGIN_EXCLUDE_TAG;
import static com.utils.oraclescriptconverter.Constants.END_BOOLEAN_TAG;
import static com.utils.oraclescriptconverter.Constants.END_EXCLUDE_TAG;

public class Configuration {

    private final List<TableConfig> config = new ArrayList<>();
    private final List<String> excludedTables = new ArrayList<>();

    public Configuration(String configFileName) {
        File file = new File(configFileName);
        FileReader fr;
        try {
            fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            boolean insideBooleanSection = false;
            boolean insideExcludeSection = false;
            while ((line = br.readLine()) != null) {
                if(line.toUpperCase().contains(BEGIN_BOOLEAN_TAG)) {
                    insideBooleanSection = true;
                    insideExcludeSection = false;
                    line = br.readLine();
                } else if (line.toUpperCase().contains(END_BOOLEAN_TAG)) {
                    insideBooleanSection = false;
                    insideExcludeSection = false;
                } else if (line.toUpperCase().contains(BEGIN_EXCLUDE_TAG)) {
                    insideBooleanSection = false;
                    insideExcludeSection = true;
                    line = br.readLine();
                } else if (line.toUpperCase().contains(END_EXCLUDE_TAG)) {
                    insideBooleanSection = false;
                    insideExcludeSection = false;
                }
                if(insideBooleanSection) {
                    String tableName = line.substring(0, line.indexOf('\t')).trim().toUpperCase();
                    String fields = line.substring(line.indexOf('\t')).trim().toUpperCase();
                    config.add(new TableConfig(tableName, fields));
                }
                if(insideExcludeSection) {
                    String tableName = line.trim().toUpperCase();
                    excludedTables.add(tableName);
                }
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<TableConfig> getTableConfig() {
        return config;
    }
    public List<String> getExcludedTables() {
        return excludedTables;
    }

}
