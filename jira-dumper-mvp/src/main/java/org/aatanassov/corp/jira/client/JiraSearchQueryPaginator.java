package org.aatanassov.corp.jira.client;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.util.DefaultXmlPrettyPrinter;
import org.aatanassov.corp.jira.model.JiraElement;
import org.aatanassov.corp.jira.model.JiraIssue;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class JiraSearchQueryPaginator extends JiraListQueryPaginator {
    private final String outputFormat;
    private final File outputFolder;
    private final String jiraRestEndpoint;
    private int fileCounter;

    public JiraSearchQueryPaginator(CloseableHttpClient httpClient, String formatOutput, String outputFolder, String jiraRestEndpoint) {
        super(httpClient);
        this.outputFormat = formatOutput;
        this.outputFolder = new File(outputFolder);
        this.jiraRestEndpoint = jiraRestEndpoint;
        this.fileCounter = 0;
    }

    @Override
    protected void handleResult(List<? extends JiraElement> jiraElements) {
        populateComments((List<JiraIssue>) jiraElements);
        String filePath = new File(outputFolder, "issues_" + Integer.toString(fileCounter)).getPath();
        if (outputFormat.equals("json")) {
            saveToJson(jiraElements, filePath);
        } else {
            saveToXml(jiraElements, filePath);
        }
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

    private static void saveToJson(List<? extends JiraElement> issues, String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            writer.writeValue(new File(filePath + ".json"), issues);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't write jira issues to " + filePath + ".json", e);
        }
    }

    private static void saveToXml(List<? extends JiraElement> issues, String filePath) {
        ObjectMapper xmlMapper = new XmlMapper();
        ObjectWriter writer = xmlMapper.writer(new DefaultXmlPrettyPrinter());
        try {
            writer.writeValue(new File(filePath + ".xml"), issues);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't write jira issues to " + filePath + ".xml", e);
        }
    }
}
