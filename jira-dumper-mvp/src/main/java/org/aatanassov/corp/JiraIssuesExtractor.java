package org.aatanassov.corp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aatanassov.corp.jira.client.JiraCommentsQuery;
import org.aatanassov.corp.jira.client.JiraSearchQuery;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class JiraIssuesExtractor {
    private final String jiraRestEndpoint;
    private final String jiraBrowseUrl;
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public JiraIssuesExtractor(String jiraRestEndpoint, String jiraBrowseUrl, CloseableHttpClient httpClient) {
        this.jiraRestEndpoint = jiraRestEndpoint;
        this.jiraBrowseUrl = (jiraBrowseUrl.endsWith("/")) ? jiraBrowseUrl : jiraBrowseUrl + "/";
        this.httpClient = httpClient;
        objectMapper = new ObjectMapper();

    }

    public List<JiraIssue> getIssues(String jql, long startAt, int maxResults) throws URISyntaxException, IOException {
        JiraSearchQuery searchQuery = new JiraSearchQuery(jiraRestEndpoint, jiraBrowseUrl, jql);
        URI searchQueryUri = searchQuery.getQuery(startAt, maxResults);
        String searchResponse = executeGet(searchQueryUri);
        List<JiraIssue> jiraIssues = searchQuery.parseResponse(objectMapper.readTree(searchResponse));
        populateComments(jiraIssues);
        return jiraIssues;
    }

    private void populateComments(List<JiraIssue> jiraIssues) throws URISyntaxException, IOException {
        for (JiraIssue jiraIssue : jiraIssues) {
            JiraCommentsQuery commentsQuery = new JiraCommentsQuery(jiraRestEndpoint, jiraIssue.getKey());
            URI getCommentsQuery = commentsQuery.getQuery(0, 10);
            String commentsQueryResponse = executeGet(getCommentsQuery);
            List<JiraIssue.Comment> comments = commentsQuery.parseResponse(objectMapper.readTree(commentsQueryResponse));
            jiraIssue.setComments(comments);
        }
    }

    private String executeGet(URI getQuery) throws IOException {
        HttpGet request = new HttpGet(getQuery);
        request.addHeader(HttpHeaders.ACCEPT, "application/json");
        CloseableHttpResponse response = httpClient.execute(request);

        try {
            System.out.println(response.getStatusLine().toString());
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity);
            } else {
                return null;
            }
        } finally {
            response.close();
        }
    }

}
