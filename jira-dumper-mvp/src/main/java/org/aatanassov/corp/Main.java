package org.aatanassov.corp;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.util.DefaultXmlPrettyPrinter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

public class Main {
    public static final String JIRA_ATLASSIAN_REST_ENDPOINT = "https://jira.atlassian.com/rest/api/latest";
    public static final String JIRA_ATLASSIAN_BROWSE_URL = "https://jira.atlassian.com/browse/";
    public static final String JQL = "issuetype in (Bug, Documentation, Enhancement) and updated > startOfWeek()";

    public static void main(String[] args) throws IOException, URISyntaxException {
        JiraIssuesExtractor issuesExtractor = new JiraIssuesExtractor(JIRA_ATLASSIAN_REST_ENDPOINT, JIRA_ATLASSIAN_BROWSE_URL, HttpClients.createDefault());
        List<JiraIssue> issues = issuesExtractor.getIssues(JQL, 0, 10);

        if (args.length == 0 || args[0].equals("json")) {
            saveToJson(issues, "jira-dumper-mvp/target/issues.json");
        } else {
            saveToXml(issues, "jira-dumper-mvp/target/issues.xml");
        }
    }

    private static void saveToJson(List<JiraIssue> issues, String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(new File(filePath), issues);
    }

    private static void saveToXml(List<JiraIssue> issues, String filePath) throws IOException {
        ObjectMapper xmlMapper = new XmlMapper();
        ObjectWriter writer = xmlMapper.writer(new DefaultXmlPrettyPrinter());
        writer.writeValue(new File(filePath), issues);
    }
}