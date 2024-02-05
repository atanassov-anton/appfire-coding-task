package org.aatanassov.corp;

import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static java.lang.System.exit;

public class Main {
    public static final String JIRA_ATLASSIAN_REST_ENDPOINT = "https://jira.atlassian.com/rest/api/latest";
    public static final String JIRA_ATLASSIAN_BROWSE_URL = "https://jira.atlassian.com/browse/";
    public static final String JQL = "issuetype in (Bug, Documentation, Enhancement) and updated > startOfWeek()";

    public static void main(String[] args) throws IOException, URISyntaxException {
        validateArguments(args);
        JiraIssuesExtractor issuesExtractor = new JiraIssuesExtractor(JIRA_ATLASSIAN_REST_ENDPOINT,
                JIRA_ATLASSIAN_BROWSE_URL, HttpClients.createDefault(), args[1], args[0]);
        issuesExtractor.dumpIssues(JQL, Integer.parseInt(args[2]), Integer.parseInt(args[3]));
    }

    private static void validateArguments(String[] args) {
        if (args.length < 4) {
            System.out.println("ERROR: provided arguments are less than 4");
            printHelp();
            exit(1);
        }
        if (args.length > 4) {
            System.out.println("ERROR: provided arguments are more than 4");
            printHelp();
            exit(1);
        }
        if (!(args[0].equals("json") || args[0].equals("xml"))) {
            System.out.println("ERROR: first argument (output_format) must be either 'json' or 'xml'");
            printHelp();
            exit(1);
        }
        File outputFolder = new File(args[1]);
        if (!outputFolder.exists()) {
            System.out.println("ERROR: output_folder \"" + args[1] + "\" points to a non-existing folder.");
            printHelp();
            exit(1);
        }
        if (!outputFolder.isDirectory()) {
            System.out.println("ERROR: output_folder \"" + args[1] + "\" is not a folder.");
            printHelp();
            exit(1);
        }
        try {
            if (Integer.parseInt(args[2]) <= 0) {
                System.out.println("ERROR: page_size \"" + args[2] + "\" must be greater than 0.");
                printHelp();
                exit(1);
            }
        } catch (NumberFormatException exception) {
            System.out.println("ERROR: page_size \"" + args[2] + "\" is not a valid integer.");
            printHelp();
            exit(1);
        }
        try {
            Integer.parseInt(args[3]);
        } catch (NumberFormatException exception) {
            System.out.println("ERROR: max_results \"" + args[3] + "\" is not a valid integer.");
            printHelp();
            exit(1);
        }
    }

    private static void printHelp() {
        System.out.println();
        System.out.println("jira-dumper Help");
        System.out.println("jira-dumper expects 4 arguments: output_format(json|xml) output_folder page_size max_results");
        System.out.println("  output_format - must be either json or xml");
        System.out.println("  output_folder - must be a valid path of a folder. The folder must exist");
        System.out.println("  page_size - must be an integer greater than 0");
        System.out.println("  max_results - must be an integer. If it is less than 0 then all results are returned");
    }
}