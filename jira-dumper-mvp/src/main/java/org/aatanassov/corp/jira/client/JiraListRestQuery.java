package org.aatanassov.corp.jira.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.aatanassov.corp.jira.model.JiraElement;

import java.util.List;

public abstract class JiraListRestQuery extends JiraRestQuery {
    public JiraListRestQuery(String jiraRestEndpoint) {
        super(jiraRestEndpoint);
    }

    public abstract List<? extends JiraElement> parseResponse(JsonNode response);
}
