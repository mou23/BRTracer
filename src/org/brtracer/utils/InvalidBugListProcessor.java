package org.brtracer.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.brtracer.property.Property;


public class InvalidBugListProcessor {
    public static ArrayList<String> getInvalidBugIDs() {
        String filePath = Property.getInstance().InvalidBugFile;
        ArrayList<String> invalidBugIDs = new ArrayList();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            
            while ((line = br.readLine()) != null) {
                String[] ids = line.split(",");
                for (String id : ids) {
                    invalidBugIDs.add(id.trim());
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
        return invalidBugIDs;
    }
}

