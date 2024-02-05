package org.aatanassov.corp.jira.client;

import org.aatanassov.corp.jira.model.Comment;
import org.aatanassov.corp.jira.model.JiraElement;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JiraCommentsQueryPaginator extends JiraListQueryPaginator {

    private final List<Comment> aggregatedComments;

    public JiraCommentsQueryPaginator(CloseableHttpClient httpClient) {
        super(httpClient);
        this.aggregatedComments = new ArrayList<>();
    }

    @Override
    protected void handleResult(List<? extends JiraElement> jiraElements) {
        aggregatedComments.addAll((Collection<? extends Comment>) jiraElements);
    }

    public List<Comment> getAggregatedComments() {
        return aggregatedComments;
    }
}
