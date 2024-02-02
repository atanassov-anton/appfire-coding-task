package org.aatanassov.corp;

import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class JiraQueryBuilder {
    public static final String JIRA_ATLASSIAN_REST_ENDPOINT = "https://jira.atlassian.com/rest/api/latest";
    public static final String SEARCH_BASE_URL = JIRA_ATLASSIAN_REST_ENDPOINT + "/search";

    public URI getSearchQuery(String jql, int startAt, int maxResults, List<String> fields) throws URISyntaxException {
        URIBuilder uri = new URIBuilder(SEARCH_BASE_URL);
        uri.addParameter("jql", jql);
        uri.addParameter("startAt", Integer.toString(startAt));
        uri.addParameter("maxResults", Integer.toString(maxResults));
        uri.addParameter("fields", String.join(",", fields));
        return uri.build();
    }
}
