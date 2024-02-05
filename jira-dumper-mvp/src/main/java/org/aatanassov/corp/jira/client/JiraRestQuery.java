package org.aatanassov.corp.jira.client;

import java.net.URI;
import java.net.URISyntaxException;

public abstract class JiraRestQuery {
    private final String jiraRestEndpoint;

    public JiraRestQuery(String jiraRestEndpoint) {
        if (jiraRestEndpoint == null) {
            throw new NullPointerException("jiraRestEndpoint constructor argument must not be null");
        }
        this.jiraRestEndpoint = jiraRestEndpoint;
    }

    public String getJiraRestEndpoint() {
        return jiraRestEndpoint;
    }

    public abstract URI getQuery(long startAt, int maxResults) throws URISyntaxException;
}
