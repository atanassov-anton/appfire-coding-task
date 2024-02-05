package org.aatanassov.corp.jira.client;

import com.fasterxml.jackson.databind.JsonNode;

import java.net.URI;
import java.net.URISyntaxException;

public abstract class JiraRestQuery {
    private final String jiraRestEndpoint;
    public static JsonNode getChildNodeByName(JsonNode parentNode, String parentNodeName, String childNodeName) {
        if (!parentNode.has(childNodeName)) {
            throw new RuntimeException("The node " + parentNodeName + " doesn't have a child named " + childNodeName);
        }
        return parentNode.get(childNodeName);
    }

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
