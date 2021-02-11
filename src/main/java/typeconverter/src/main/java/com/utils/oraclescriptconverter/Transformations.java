package com.utils.oraclescriptconverter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import static com.utils.oraclescriptconverter.Constants.END_INSERT_STATEMENT;
import static com.utils.oraclescriptconverter.Constants.SEARCH_PREFIX;
import static com.utils.oraclescriptconverter.Constants.TEMP_FILE_NAME;

public class Transformations {

    public static void excludeUnnecessaryTable(String sourceFileName, String tableName) {
        try {

            String tempFile = sourceFileName.substring(0, sourceFileName.lastIndexOf('\\')) + "\\" + TEMP_FILE_NAME;
            File file = new File(tempFile);
            if (!file.exists()) {
                file = new File(sourceFileName);
            }
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);

            StringBuilder sb = new StringBuilder();
            String line;
            boolean insideExclusion = false;
            while ((line = br.readLine()) != null) {
                if (line.toUpperCase().contains(SEARCH_PREFIX + tableName.toUpperCase() + " ")) {
                    insideExclusion = true;
                }
                if (!insideExclusion) {
                    sb.append(line).append("\n");
                }
                if (line.contains(END_INSERT_STATEMENT)) {
                    insideExclusion = false;
                }
            }
            br.close();
            isr.close();
            fis.close();

            System.out.println("Table " + tableName + " excluded successfully!");

            Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(tempFile), StandardCharsets.UTF_8));
            writer.write(sb.toString());
            writer.close();

        } catch (IOException e) {
            System.out.println("Converter error: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public static void replaceBooleanValues(String sourceFileName, String tableName, String targetColumns) {
        try {

            String tempFile = sourceFileName.substring(0, sourceFileName.lastIndexOf('\\')) + "\\" + TEMP_FILE_NAME;
            File file = new File(tempFile);
            if (!file.exists()) {
                file = new File(sourceFileName);
            }
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.toUpperCase().contains(SEARCH_PREFIX + tableName.toUpperCase() + " ")) {
                    StringBuilder tempString = new StringBuilder(line);
                    while (!tempString.toString().trim().endsWith(");")) {
                        tempString.append(br.readLine());
                    }
                    System.out.println("Line=" + tempString.toString());
                    for (String targetColumn : targetColumns.split(",")) {
                        tempString = integerToBoolean(tempString.toString(), targetColumn);
                    }
                    sb.append(tempString);
                } else {
                    sb.append(line);
                }
                sb.append("\n");
            }
            br.close();
            isr.close();
            fis.close();

            System.out.println("Converted successfully!");

            Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(tempFile), StandardCharsets.UTF_8));
            writer.write(sb.toString());
            writer.close();

        } catch (IOException e) {
            System.out.println("Converter error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static StringBuilder integerToBoolean(String sourceLine, String targetColumn) {
        int fieldPosition = getFieldPosition(sourceLine, targetColumn);
        if (fieldPosition == -1) {
            return new StringBuilder();
        }
        int currentPosition1 = sourceLine.toUpperCase().indexOf("VALUES");
        int currentPosition2;
        currentPosition1 = sourceLine.indexOf("(", currentPosition1) + 1;
        StringBuilder resultLine = new StringBuilder();
        int currentNumber = 0;
        while (currentPosition1 < sourceLine.length()) {
            if (sourceLine.charAt(currentPosition1) == '\'') {
                currentPosition1++;
                while (sourceLine.charAt(currentPosition1) != '\''/* && currentPosition1 < sourceLine.length()*/) {
                    currentPosition1++;
                }
                currentNumber++;
                currentPosition1 += 2;
            } else if (sourceLine.substring(currentPosition1).trim().toUpperCase().startsWith("TO_DATE")) {
                while (sourceLine.charAt(currentPosition1) != ')') {
                    currentPosition1++;
                }
                currentNumber++;
                currentPosition1 = sourceLine.indexOf(",", currentPosition1) + 1;
            } else {
                currentPosition2 = sourceLine.indexOf(",", currentPosition1);
                if (currentPosition2 == -1) {
                    currentPosition2 = sourceLine.indexOf(")", currentPosition1);
                    if (sourceLine.charAt(currentPosition2 - 1) == '\'') {
                        currentPosition2 -= 1;
                    }
                }
                if (currentNumber == fieldPosition) {
                    String value = sourceLine.substring(currentPosition1, currentPosition2);
                    System.out.println("FIELD AT POSITION " + fieldPosition + " VALUE: " + value);
                    resultLine = new StringBuilder(sourceLine.substring(0, currentPosition1)
                            + (value.equals("1") ? "true" : "false")
                            + sourceLine.substring(currentPosition2));
                    break;
                }
                currentPosition1 = currentPosition2 + 1;
                currentNumber++;
            }

        }
        return resultLine;
    }

    private static int getFieldPosition(String sourceLine, String targetColumn) {
        String fieldDefs = sourceLine.substring(sourceLine.indexOf("(") + 1, sourceLine.indexOf(")"));
        String[] fields = fieldDefs.split(",");
        for (int ii = 0; ii < fields.length; ii++) {
            if (targetColumn.equalsIgnoreCase(fields[ii])) {
                System.out.println("Field position: " + ii);
                return ii;
            }
        }
        System.out.println("Couldn't find position for field " + targetColumn);
        return -1;
    }

}

