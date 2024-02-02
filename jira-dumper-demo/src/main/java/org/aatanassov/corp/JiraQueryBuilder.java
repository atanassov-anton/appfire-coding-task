package org.aatanassov.corp;

import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class JiraQueryBuilder {
    public static final String JIRA_ATLASSIAN_REST_ENDPOINT = "https://jira.atlassian.com/rest/api/latest";
    public static final String SEARCH_BASE_URL = JIRA_ATLASSIAN_REST_ENDPOINT + "/search";

    public URI getSearchQuery(String jql, long startAt, int maxResults, List<String> fields) throws URISyntaxException {
        URIBuilder uri = new URIBuilder(SEARCH_BASE_URL);
        uri.addParameter("jql", jql);
        uri.addParameter("startAt", Long.toString(startAt));
        uri.addParameter("maxResults", Integer.toString(maxResults));
        uri.addParameter("fields", String.join(",", fields));
        return uri.build();
    }

    public URI getCommentsQuery(String issueId, long startAt, int maxResults) throws URISyntaxException {
        String baseURL = JIRA_ATLASSIAN_REST_ENDPOINT + "/issue/" + issueId + "/comment";
        URIBuilder uri = new URIBuilder(baseURL);
        uri.addParameter("startAt", Long.toString(startAt));
        uri.addParameter("maxResults", Integer.toString(maxResults));
        uri.addParameter("orderBy", "created");
        return uri.build();
    }
}
