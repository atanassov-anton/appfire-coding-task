package org.aatanassov.corp.jira.persist;

import org.aatanassov.corp.jira.model.JiraElement;

import java.util.List;

public interface JiraFilePersister {
    public void persistJiraElements(List<? extends JiraElement> jiraElements, String filename);
}
