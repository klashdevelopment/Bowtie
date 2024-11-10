package dev.klash.bowtie.utility;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JSONUtility {
    public static Map<String, String> parseSSJsonFile(File json) throws IOException, ParseException {
        // Read the JSON file into a String
        StringBuilder jsonString = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(json))) {
            String line;
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
        }

        // Parse the JSON String into a JSONObject
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(jsonString.toString());

        // Convert the JSONObject to a Map<String, String>
        Map<String, String> resultMap = new HashMap<>();
        for (Object key : jsonObject.keySet()) {
            resultMap.put((String) key, (String) jsonObject.get(key));
        }

        return resultMap;
    }
}
