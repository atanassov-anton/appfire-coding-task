package org.aatanassov.corp.jira.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.aatanassov.corp.jira.model.Comment;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JiraCommentsQuery extends JiraListRestQuery {
    private final String issueId;

    public JiraCommentsQuery(String jiraRestEndpoint, String issueId) {
        super(jiraRestEndpoint);
        this.issueId = issueId;
    }

    @Override
    public URI getQuery(long startAt, int maxResults) throws URISyntaxException {
        URIBuilder uri = new URIBuilder(getJiraRestEndpoint());
        List<String> pathSegments = uri.getPathSegments();
        pathSegments.add("issue");
        pathSegments.add(issueId);
        pathSegments.add("comment");
        uri.setPathSegments(pathSegments);
        uri.addParameter("startAt", Long.toString(startAt));
        uri.addParameter("maxResults", Integer.toString(maxResults));
        uri.addParameter("orderBy", "created");
        return uri.build();
    }

    public List<Comment> parseResponse(JsonNode getCommentsQueryResponse) {
        if (getCommentsQueryResponse == null) {
            throw new NullPointerException("searchQueryResponse parameter must not be null");
        }
        List<Comment> result = new ArrayList<>();
        JsonNode commentsNode = getChildNodeByName(getCommentsQueryResponse, "root", "comments");
        for (Iterator<JsonNode> it = commentsNode.iterator(); it.hasNext(); ) {
            Comment comment = new Comment();
            JsonNode commentNode = it.next();
            JsonNode author = getChildNodeByName(commentNode, "comment", "author");
            comment.setAuthorUsername(getChildNodeByName(author, "author", "displayName").asText());
            comment.setText(getChildNodeByName(commentNode, "comment", "body").asText());
            result.add(comment);
        }
        return result;
    }
}
