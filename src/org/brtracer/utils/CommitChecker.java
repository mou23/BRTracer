package org.brtracer.utils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.brtracer.property.Property;

public class CommitChecker {

    private final String repoPath = Property.getInstance().SourceCodeDir;
    private final String commitHash = Property.getInstance().CommitHash;

    public CommitChecker() throws IOException, ParseException{}

    public void create() throws IOException, InterruptedException{
        try {
            // Build the Git command to check out to before fix command
            ProcessBuilder processBuilder = new ProcessBuilder("git", "checkout", commitHash + "~1");
            processBuilder.directory(new File(repoPath)); // Set the working directory to the repo

            Process process = processBuilder.start();

//            todo: uncomment this part for more error handling
//            // Read any errors from the command
//            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
//                String errorLine;
//                while ((errorLine = errorReader.readLine()) != null) {
//                    System.err.println("Error: " + errorLine);
//                }
//            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Successfully checked out to previous fix commit " + commitHash);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
