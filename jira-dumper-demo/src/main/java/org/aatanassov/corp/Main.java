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
    public static void main(String[] args) throws IOException, URISyntaxException {
        System.out.println("Hello world!");
        invokeGet();
    }

    public static void invokeGet() throws IOException, URISyntaxException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            JiraQueryBuilder queryBuilder = new JiraQueryBuilder();
            String jql = "issuetype in (Bug, Documentation, Enhancement) and updated > startOfWeek()";
            List<String> fields = new LinkedList<>();
            fields.add("summary");
            fields.add("type");
            fields.add("priority");
            fields.add("description");
            fields.add("reporter");
            fields.add("created");

            URI searchRequest = queryBuilder.getSearchQuery(jql, 0, 1, fields);
            HttpGet request = new HttpGet(searchRequest);
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