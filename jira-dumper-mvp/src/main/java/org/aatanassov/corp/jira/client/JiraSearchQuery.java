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
 * The jira search query has a fixed set of fields: summary, issuetype, priority, description, reporter, created
 * </p>
 */
public class JiraSearchQuery extends JiraRestQuery {
    private final List<String> fields;
    private final String jiraBrowseUrl;
    private final String jql;

    /**
     * Constructs a jira search query object initialized with the provided parameters
     *
     * @param jiraRestEndpoint base REST url of a jira instance which is used to build the search query URI.
     *                         Must not be {@code null}.
     * @param jiraBrowseUrl    base "browse" url for the jira instance which is used to build individual issue urls.
     * @param jql              query which is used to build the jira search query.
     */
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

    /**
     * Builds a jira search query URI using the {@code jiraRestEndpoint}, {@code jql} constructor parameters and
     * the {@code startAt} and {@code maxResults} method parameters.
     * <p>
     * The built get query adheres to the
     * <a href="https://docs.atlassian.com/software/jira/docs/api/REST/9.13.0/#api/2/search-search">
     * search jira REST API specification version 9.13.0</a>
     * </p>
     * <p>
     * The returned URI should be used directly to make a GET REST request. The response of the REST request should be
     * parsed using the {@link JiraSearchQuery#parseResponse(JsonNode)}
     * </p>
     *
     * @param startAt    must be equal or greater than 0
     * @param maxResults the maximum number of issues to return (defaults to 50). The maximum allowable value is
     *                   dictated by the Jira property 'jira.search.views.default.max'. If you specify a value that is
     *                   higher than this number, your search results will be truncated.
     * @return URI representing a jira search query
     * @throws URISyntaxException
     */
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

            JsonNode issuetype = getChildNodeByName(fields, "fields", "issuetype");
            jiraIssue.setType(getChildNodeByName(issuetype, "issuetype", "name").asText());

            jiraIssue.setUrl(jiraBrowseUrl + jiraIssue.getKey());
            result.add(jiraIssue);
        }
        return result;
    }
}
