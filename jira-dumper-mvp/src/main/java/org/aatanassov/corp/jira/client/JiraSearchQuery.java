package org.aatanassov.corp.jira.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.aatanassov.corp.JiraIssue;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that can build a jira search query GET REST request and a method to parse the json response of the REST request.
 * <p>
 * The jira search query has a fixed set of fields: summary,
 */
public class JiraSearchQuery extends JiraRestQuery {
    private final List<String> fields;
    private final String jiraBrowseUrl;
    private final String jql;

    public JiraSearchQuery(String jiraRestEndpoint, String jiraBrowseUrl, String jql) {
        super(jiraRestEndpoint);
        this.jiraBrowseUrl = (jiraBrowseUrl.endsWith("/")) ? jiraBrowseUrl : jiraBrowseUrl + "/";
        this.jql = jql;
        fields = new LinkedList<>();
        fields.add("summary");
        fields.add("issuetype");
        fields.add("priority");
        fields.add("description");
        fields.add("reporter");
        fields.add("created");
    }

    public URI getQuery(long startAt, int maxResults) throws URISyntaxException {
        URIBuilder uri = new URIBuilder(getJiraRestEndpoint());
        List<String> pathSegments = uri.getPathSegments();
        pathSegments.add("search");
        uri.setPathSegments(pathSegments);
        uri.addParameter("jql", jql);
        uri.addParameter("startAt", Long.toString(startAt));
        uri.addParameter("maxResults", Integer.toString(maxResults));
        uri.addParameter("fields", String.join(",", fields));
        return uri.build();
    }

    public List<JiraIssue> parseResponse(JsonNode searchQueryResponse) {
        if (searchQueryResponse == null) {
            throw new NullPointerException("searchQueryResponse parameter must not be null");
        }
        List<JiraIssue> result = new ArrayList<>();
        JsonNode issuesNode = getChildNodeByName(searchQueryResponse, "root", "issues");
        for (Iterator<JsonNode> it = issuesNode.iterator(); it.hasNext(); ) {
            JiraIssue jiraIssue = new JiraIssue();
            JsonNode issueNode = it.next();

            jiraIssue.setKey(getChildNodeByName(issueNode, "issue", "key").asText());
            JsonNode fields = getChildNodeByName(issueNode, "issue", "fields");

            jiraIssue.setDateCreated(getChildNodeByName(fields, "fields", "created").asText());

            jiraIssue.setDescription(getChildNodeByName(fields, "fields", "description").asText());

            jiraIssue.setSummary(getChildNodeByName(fields, "fields", "summary").asText());

            JsonNode priority = getChildNodeByName(fields, "fields", "priority");
            jiraIssue.setPriority(getChildNodeByName(priority, "priority", "name").asText());

            JsonNode reporter = getChildNodeByName(fields, "fields", "reporter");
            jiraIssue.setReporter(getChildNodeByName(reporter, "reporter", "displayName").asText());

            JsonNode issuetype = getChildNodeByName(fields, "fields","issuetype");
            jiraIssue.setType(getChildNodeByName(issuetype, "issuetype","name").asText());

            jiraIssue.setUrl(jiraBrowseUrl + jiraIssue.getKey());
            result.add(jiraIssue);
        }
        return result;
    }

    private JsonNode getChildNodeByName(JsonNode parentNode, String parentNodeName, String childNodeName) {
        if (!parentNode.has(childNodeName)) {
            throw new RuntimeException("The node " + parentNodeName + " doesn't have a child named " + childNodeName);
        }
        return parentNode.get(childNodeName);
    }
}
