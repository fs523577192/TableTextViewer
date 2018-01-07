package com.gnete.tool;

import java.io.*;
import java.util.*;

public class TableTextViewer {

    public static void main(String[] args) {
        if (!validateArguments(args)) {
            return;
        }

        Properties properties = parseConfigFile(args[0]);
        if (null == properties) {
            return;
        }

        String inputFile = properties.getProperty("inputFile");
        String inputCharset = properties.getProperty("inputCharset");
        String inputSeperator = properties.getProperty("inputSeperator");
        List<String[]> table = readInput(
                inputFile, inputCharset, inputSeperator);
        List<Integer> maxLengths = getFieldMaxLength(table);

        String outputFile = properties.getProperty("outputFile");
        String outputCharset = properties.getProperty("outputCharset");
        String outputSeperator = properties.getProperty("outputSeperator");
        output(table, maxLengths, outputFile, outputCharset, outputSeperator);
    }

    private static boolean validateArguments(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage:");
            System.out.println("  java -jar TableTextViewer.jar config_file");
            return false;
        }
        return true;
    }

    private static Properties parseConfigFile(String configFile) {
        try {
            Properties properties = new Properties();
            FileInputStream inputStream = new FileInputStream(configFile);
            properties.load(inputStream);
            inputStream.close();
            return properties;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static int showLength(String str) {
        int result = 0;
        for (int i = str.length(); i > 0; ) {
            i -= 1;
            char c = str.charAt(i);
            if (' ' <= c && c <= '~') {
                result += 1;
            } else if ('\t' == c) {
                int temp = result & (4 - 1);
                if (temp > 0) {
                    result += 4 - temp;
                }
            } else if (c > '~' + 1) {
                result += 2;
            }
        }
        return result;
    }

    private static List<String[]> readInput(
            String inputFile,
            String inputCharset,
            String inputSeperator
    ) {
        try {
            Scanner scanner = new Scanner(new File(inputFile), inputCharset);
            List<String[]> table = new ArrayList<String[]>();
            while (scanner.hasNextLine()) {
                String temp = scanner.nextLine();
                table.add(temp.split(inputSeperator));
            }
            return table;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static List<Integer> getFieldMaxLength(List<String[]> table) {
        List<Integer> maxLengths = new ArrayList<Integer>();
        for (String[] row : table) {
            for (int i = row.length - maxLengths.size(); i > 0; i -= 1) {
                maxLengths.add(0);
            }
            for (int i = row.length; i > 0; ) {
                i -= 1;
                if (null == row[i]) {
                    continue;
                }
                if (maxLengths.get(i) < row[i].length()) {
                    maxLengths.set(i, row[i].length());
                }
            }
        }
        return maxLengths;
    }

    private static void output(
            List<String[]> table,
            List<Integer> maxLengths,
            String outputFile,
            String outputCharset,
            String outputSeperator
    ) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(outputFile, outputCharset);
            for (String[] row : table) {
                for (int i = 0; i < maxLengths.size(); i += 1) {
                    StringBuilder buffer = new StringBuilder();
                    if (row.length > i && null != row[i]) {
                        buffer.append(row[i]);
                    }
                    for (int j = maxLengths.get(i) - buffer.length();
                            j > 0; j -= 1) {
                        buffer.append(' ');
                    }
                    buffer.append(outputSeperator);
                    writer.println(buffer.toString());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            writer = safelyClose(writer);
        }
    }
    
    private static PrintWriter safelyClose(PrintWriter writer) {
        try {  
            if (null != writer) {  
                writer.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();  
        }
        return null;
    }
}
