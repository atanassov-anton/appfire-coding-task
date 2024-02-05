package org.aatanassov.corp.jira.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aatanassov.corp.jira.model.JiraElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public abstract class JiraListQueryPaginator {
    protected final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    protected JiraListQueryPaginator(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
    }

    public void iteratePagesAndHandle(JiraListRestQuery listQuery, int pageSize, int maxResults) throws URISyntaxException, IOException {
        if (maxResults < 0) {
            maxResults = Integer.MAX_VALUE;
        }
        int i;
        for (i = 0; i < maxResults; i += pageSize) {
            URI jiraQuery;
            if (i + pageSize > maxResults) {
                jiraQuery = listQuery.getQuery(i, maxResults - i);
            } else {
                jiraQuery = listQuery.getQuery(i, pageSize);
            }
            String queryResponse = executeGet(jiraQuery);
            List<? extends JiraElement> jiraElements = listQuery.parseResponse(objectMapper.readTree(queryResponse));
            if (i + pageSize < maxResults && jiraElements.size() < pageSize) {
                break;
            }
            handleResult(jiraElements);
        }
    }

    private String executeGet(URI getQuery) throws IOException {
        HttpGet request = new HttpGet(getQuery);
        request.addHeader(HttpHeaders.ACCEPT, "application/json");
        CloseableHttpResponse response = httpClient.execute(request);

        try {
            System.out.println(response.getStatusLine().toString());
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity);
            } else {
                return null;
            }
        } finally {
            response.close();
        }
    }

    protected abstract void handleResult(List<? extends JiraElement> jiraElements);
}
