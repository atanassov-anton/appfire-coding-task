package org.aatanassov.corp.jira.client;

import org.aatanassov.corp.jira.model.JiraElement;
import org.aatanassov.corp.jira.model.JiraIssue;
import org.aatanassov.corp.jira.persist.JiraFilePersister;
import org.aatanassov.corp.jira.persist.JiraJsonPersister;
import org.aatanassov.corp.jira.persist.JiraXmlPersister;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class JiraSearchQueryPaginator extends JiraListQueryPaginator {
    private final JiraFilePersister filePersister;
    private final File outputFolder;
    private final String jiraRestEndpoint;
    private int fileCounter;

    public JiraSearchQueryPaginator(CloseableHttpClient httpClient, String outputFormat, String outputFolder, String jiraRestEndpoint) {
        super(httpClient);
        if (outputFormat.equals("json")) {
            filePersister = new JiraJsonPersister();
        } else {
            filePersister = new JiraXmlPersister();
        }
        this.outputFolder = new File(outputFolder);
        this.jiraRestEndpoint = jiraRestEndpoint;
        this.fileCounter = 0;
    }

    @Override
    protected void handleResult(List<? extends JiraElement> jiraElements) {
        populateComments((List<JiraIssue>) jiraElements);
        String filePath = new File(outputFolder, "issues_" + Integer.toString(fileCounter)).getPath();
        filePersister.persistJiraElements(jiraElements, filePath);
        fileCounter++;
    }

    private void populateComments(List<JiraIssue> jiraIssues) {
        for (JiraIssue jiraIssue : jiraIssues) {
            JiraCommentsQuery commentsQuery = new JiraCommentsQuery(jiraRestEndpoint, jiraIssue.getKey());
            JiraCommentsQueryPaginator commentsPaginator = new JiraCommentsQueryPaginator(httpClient);
            try {
                commentsPaginator.iteratePagesAndHandle(commentsQuery, 100, -1);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            jiraIssue.setComments(commentsPaginator.getAggregatedComments());
        }
    }
}
