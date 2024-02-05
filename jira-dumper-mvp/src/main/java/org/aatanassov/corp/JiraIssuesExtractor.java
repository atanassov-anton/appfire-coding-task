package org.aatanassov.corp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aatanassov.corp.jira.client.JiraSearchQuery;
import org.aatanassov.corp.jira.client.JiraSearchQueryPaginator;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.net.URISyntaxException;

public class JiraIssuesExtractor {
    private final String jiraRestEndpoint;
    private final String jiraBrowseUrl;
    private final CloseableHttpClient httpClient;
    private final String outputFolder;
    private final String outputFormat;

    public JiraIssuesExtractor(String jiraRestEndpoint,
                               String jiraBrowseUrl,
                               CloseableHttpClient httpClient,
                               String outputFolder,
                               String outputFormat) {
        this.jiraRestEndpoint = jiraRestEndpoint;
        this.jiraBrowseUrl = (jiraBrowseUrl.endsWith("/")) ? jiraBrowseUrl : jiraBrowseUrl + "/";
        this.httpClient = httpClient;
        this.outputFolder = outputFolder;
        this.outputFormat = outputFormat;
    }

    public void dumpIssues(String jql, int pageSize, int maxResults) throws URISyntaxException, IOException {
        JiraSearchQueryPaginator searchQueryPaginator = new JiraSearchQueryPaginator(httpClient, outputFormat, outputFolder, jiraRestEndpoint);
        JiraSearchQuery searchQuery = new JiraSearchQuery(jiraRestEndpoint, jiraBrowseUrl, jql);
        searchQueryPaginator.iteratePagesAndHandle(searchQuery, pageSize, maxResults);
    }

}
