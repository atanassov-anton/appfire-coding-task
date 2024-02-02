package org.aatanassov.corp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

public class Main {
    private static JiraQueryBuilder queryBuilder = new JiraQueryBuilder();

    public static void main(String[] args) throws IOException, URISyntaxException {
        System.out.println("Hello world!");
        testSearchQuery();
        testCommentsQuery();
    }

    private static void testCommentsQuery() throws IOException, URISyntaxException {
        URI getCommentsQuery = queryBuilder.getCommentsQuery("JRASERVER-9091", 0, 2);
        System.out.println("=========== executing get comments query");
        executeGet(getCommentsQuery);
    }

    private static void testSearchQuery() throws IOException, URISyntaxException {
        String jql = "issuetype in (Bug, Documentation, Enhancement) and updated > startOfWeek()";
        List<String> fields = new LinkedList<>();
        fields.add("summary");
        fields.add("type");
        fields.add("priority");
        fields.add("description");
        fields.add("reporter");
        fields.add("created");

        URI searchQuery = queryBuilder.getSearchQuery(jql, 0, 1, fields);
        System.out.println("=========== executing search query");
        executeGet(searchQuery);
    }

    private static void executeGet(URI getQuery) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            HttpGet request = new HttpGet(getQuery);
            request.addHeader(HttpHeaders.ACCEPT, "application/json");
            CloseableHttpResponse response = httpClient.execute(request);

            try {
                System.out.println(response.getStatusLine().toString());        // HTTP/1.1 200 OK
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // return it as a String
                    String result = EntityUtils.toString(entity);
                    System.out.println(result);
                }
            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }

    }
}