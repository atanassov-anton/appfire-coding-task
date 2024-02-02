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
import java.net.URISyntaxException;

public class POCMain {
    public static void main(String[] args) throws IOException, URISyntaxException {
        System.out.println("Hello world!");
        invokeGet();
    }

    public static void invokeGet() throws IOException, URISyntaxException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            HttpGet request = new HttpGet("https://jira.atlassian.com/rest/api/latest/search");
            URIBuilder uri = new URIBuilder(request.getURI());
            uri.addParameter("jql", "issuetype in (Bug, Documentation, Enhancement) and updated > startOfWeek()");
            uri.addParameter("startAt", "0");
            uri.addParameter("maxResults", "2");
            uri.addParameter("fields", "summary,status,assignee");
            request.setURI(uri.build());

            // add request headers
            request.addHeader(HttpHeaders.ACCEPT, "application/json");
            CloseableHttpResponse response = httpClient.execute(request);

            try {
                System.out.println(response.getProtocolVersion());              // HTTP/1.1
                System.out.println(response.getStatusLine().getStatusCode());   // 200
                System.out.println(response.getStatusLine().getReasonPhrase()); // OK
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