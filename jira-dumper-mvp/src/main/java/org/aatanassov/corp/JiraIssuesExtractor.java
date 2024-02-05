package org.aatanassov.corp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final List<String> fields;
    private final ObjectMapper objectMapper;

    public JiraIssuesExtractor(String jiraRestEndpoint, String jiraBrowseUrl, CloseableHttpClient httpClient) {
        this.jiraRestEndpoint = jiraRestEndpoint;
        this.jiraBrowseUrl = (jiraBrowseUrl.endsWith("/")) ? jiraBrowseUrl : jiraBrowseUrl + "/";
        this.httpClient = httpClient;
        fields = new LinkedList<>();
        fields.add("summary");
        fields.add("type");
        fields.add("priority");
        fields.add("description");
        fields.add("reporter");
        fields.add("created");
        objectMapper = new ObjectMapper();

    }

    public List<JiraIssue> getIssues(String jql, long startAt, int maxResults) throws URISyntaxException, IOException {
        URI searchQuery = getSearchQuery(jql, startAt, maxResults, fields);
        String searchResponse = executeGet(searchQuery);
        List<JiraIssue> jiraIssues = parseSearchResponse(searchResponse);
        populateComments(jiraIssues);
        return jiraIssues;
    }

    private void populateComments(List<JiraIssue> jiraIssues) throws URISyntaxException, IOException {
        for (JiraIssue jiraIssue : jiraIssues) {
            URI getCommentsQuery = getCommentsQuery(jiraIssue.getKey(), 0, 10);
            String commentsQueryResponse = executeGet(getCommentsQuery);
            List<JiraIssue.Comment> comments = parseGetCommentsResponse(commentsQueryResponse);
            jiraIssue.setComments(comments);
        }
    }

    private List<JiraIssue.Comment> parseGetCommentsResponse(String commentsQueryResponse) throws IOException {
        List<JiraIssue.Comment> result = new ArrayList<>();
        JsonNode jsonNode = objectMapper.readTree(commentsQueryResponse);
        JsonNode commentsNode = jsonNode.get("comments");
        for (Iterator<JsonNode> it = commentsNode.iterator(); it.hasNext(); ) {
            JiraIssue.Comment comment = new JiraIssue.Comment();
            JsonNode commentNode = it.next();
            comment.setAuthorUsername(commentNode.get("author").get("displayName").asText());
            comment.setText(commentNode.get("body").asText());
            result.add(comment);
        }
        return result;

    }

    private URI getSearchQuery(String jql, long startAt, int maxResults, List<String> fields) throws URISyntaxException {
        URIBuilder uri = new URIBuilder(jiraRestEndpoint + "/search");
        uri.addParameter("jql", jql);
        uri.addParameter("startAt", Long.toString(startAt));
        uri.addParameter("maxResults", Integer.toString(maxResults));
        uri.addParameter("fields", String.join(",", fields));
        return uri.build();
    }

    private URI getCommentsQuery(String issueId, long startAt, int maxResults) throws URISyntaxException {
        String baseURL = jiraRestEndpoint + "/issue/" + issueId + "/comment";
        URIBuilder uri = new URIBuilder(baseURL);
        uri.addParameter("startAt", Long.toString(startAt));
        uri.addParameter("maxResults", Integer.toString(maxResults));
        uri.addParameter("orderBy", "created");
        return uri.build();
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

    private List<JiraIssue> parseSearchResponse(String searchQueryResponse) throws IOException {
        List<JiraIssue> result = new ArrayList<>();
        JsonNode jsonNode = objectMapper.readTree(searchQueryResponse);
        JsonNode issuesNode = jsonNode.get("issues");
        for (Iterator<JsonNode> it = issuesNode.iterator(); it.hasNext(); ) {
            JiraIssue jiraIssue = new JiraIssue();
            JsonNode issueNode = it.next();
            jiraIssue.setKey(issueNode.get("key").asText());
            JsonNode fields = issueNode.get("fields");
            jiraIssue.setDateCreated(fields.get("created").asText());
            jiraIssue.setDescription(fields.get("description").asText());
            jiraIssue.setSummary(fields.get("summary").asText());
            jiraIssue.setPriority(fields.get("priority").get("name").asText());
            jiraIssue.setReporter(fields.get("reporter").get("displayName").asText());
            jiraIssue.setUrl(jiraBrowseUrl + jiraIssue.getKey());
            result.add(jiraIssue);
        }
        return result;
    }

}
