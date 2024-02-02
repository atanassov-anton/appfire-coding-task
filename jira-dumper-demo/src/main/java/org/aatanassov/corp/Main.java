package org.aatanassov.corp;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

public class Main {
    public static final String JIRA_ATLASSIAN_REST_ENDPOINT = "https://jira.atlassian.com/rest/api/latest";
    public static final String JIRA_ATLASSIAN_BROWSE_URL = "https://jira.atlassian.com/browse/";
    public static final String JQL = "issuetype in (Bug, Documentation, Enhancement) and updated > startOfWeek()";

    public static void main(String[] args) throws IOException, URISyntaxException {
        testJiraIssuesExtractor();
    }

    public static void testJiraIssuesExtractor() throws URISyntaxException, IOException {
        JiraIssuesExtractor issuesExtractor = new JiraIssuesExtractor(JIRA_ATLASSIAN_REST_ENDPOINT, JIRA_ATLASSIAN_BROWSE_URL, HttpClients.createDefault());
        List<JiraIssue> issues = issuesExtractor.getIssues(JQL, 10, 10);
        for (JiraIssue issue : issues) {
            System.out.println();
            System.out.println("===========================================");
            System.out.println();
            System.out.println(issue);
        }
    }

}