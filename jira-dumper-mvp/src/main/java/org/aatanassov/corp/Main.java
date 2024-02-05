package org.aatanassov.corp;

import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.lang.System.exit;

public class Main {
    public static final String JIRA_ATLASSIAN_REST_ENDPOINT = "https://jira.atlassian.com/rest/api/latest";
    public static final String JIRA_ATLASSIAN_BROWSE_URL = "https://jira.atlassian.com/browse/";
    public static final String JQL = "issuetype in (Bug, Documentation, Enhancement) and updated > startOfWeek()";

    public static void main(String[] args) throws IOException, URISyntaxException {
        validateArguments(args);
        JiraIssuesExtractor issuesExtractor = new JiraIssuesExtractor(JIRA_ATLASSIAN_REST_ENDPOINT,
                JIRA_ATLASSIAN_BROWSE_URL, HttpClients.createDefault(), args[1], args[0]);
        issuesExtractor.dumpIssues(JQL, 10, 57);
    }

    private static void validateArguments(String[] args) {
        if (args.length < 2) {
            printHelp();
            exit(1);
        }
        if (!(args[0].equals("json") || args[0].equals("xml"))) {
            printHelp();
            System.out.println("first argument (output_format) must be either 'json' or 'xml'");
            exit(1);
        }
        if (!Files.exists(Paths.get(args[1]))) {
            printHelp();
            System.out.println("output_folder points to a non-existing folder.");
            exit(1);
        }
    }

    private static void printHelp() {
        System.out.println("");
        System.out.println("jira-dumper Help");
        System.out.println("jira-dumper expects 2 arguments:");
        System.out.println("output_format(json|xml) output_folder");
        System.out.println("");
    }
}