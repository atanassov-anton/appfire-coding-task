package org.aatanassov.corp.jira.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aatanassov.corp.JiraIssue;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class JiraSearchQueryTest {
    private JiraSearchQuery underTest;
    private ObjectMapper objectMapper;

    private static final String JIRA_BROWSE_URL = "www.test-jira.com/browse";

    @BeforeMethod
    public void beforeTest() {
        underTest = new JiraSearchQuery("www.test.com", JIRA_BROWSE_URL, "some jql");
        objectMapper = new ObjectMapper();
    }

    @Test(expectedExceptions = {URISyntaxException.class})
    public void testGetQueryWithInvalidUriForJiraRestEndpoint () throws URISyntaxException {
        underTest = new JiraSearchQuery("uri that has spaces :: \\", "doesn't matter", "doesn't matter");
        underTest.getQuery(1, 2);
    }

    @Test
    public void testGetQueryWithNullJiraRestEndpoint () throws URISyntaxException {
        boolean exceptionTrhown = false;
        try {
            underTest = new JiraSearchQuery(null, "doesn't matter", "doesn't matter");
        } catch (NullPointerException exception) {
            exceptionTrhown = true;
            Assert.assertTrue(exception.getMessage().contains("jiraRestEndpoint"));
        }
        Assert.assertTrue(exceptionTrhown);
    }

    @Test
    public void testGetQueryAndValidateOutput() throws URISyntaxException {
        underTest = new JiraSearchQuery("https://www.jira.com/rest/api", "doesn't matter", "project=AA");
        URI query = underTest.getQuery(0, 20);
        Assert.assertTrue(query.toString().startsWith("https://www"));
        Assert.assertEquals(query.getHost(), "www.jira.com");
        Assert.assertEquals(query.getPath(), "/rest/api/search");
        String rawQuery = query.getRawQuery();
        Assert.assertTrue(rawQuery.contains("jql=project%3DAA"));
        Assert.assertTrue(rawQuery.contains("fields="));
        Assert.assertTrue(rawQuery.contains("startAt=0"));
        Assert.assertTrue(rawQuery.contains("maxResults=20"));
        Assert.assertTrue(rawQuery.contains("summary"));
        Assert.assertTrue(rawQuery.contains("description"));
    }

    @Test
    public void testParseWithNull() {
        boolean exceptionTrhown = false;
        try {
            underTest.parseResponse(null);
        } catch (NullPointerException exception) {
            exceptionTrhown = true;
            Assert.assertTrue(exception.getMessage().contains("searchQueryResponse"));
        }
        Assert.assertTrue(exceptionTrhown);
    }

    @Test
    public void testParseWithEmptyNode() throws JsonProcessingException {
        JsonNode searchResponse = objectMapper.readTree("{}");
        boolean exceptionTrhown = false;
        try {
            underTest.parseResponse(searchResponse);
        } catch (RuntimeException exception) {
            exceptionTrhown = true;
            Assert.assertTrue(exception.getMessage().contains("issues"));
        }
        Assert.assertTrue(exceptionTrhown);
    }

    @Test
    public void testParseWithMissingKey() throws IOException {
        JsonNode searchResponse = loadFromResourcesFile("missing-key-search-response.json");
        boolean exceptionTrhown = false;
        try {
            underTest.parseResponse(searchResponse);
        } catch (RuntimeException exception) {
            exceptionTrhown = true;
            Assert.assertTrue(exception.getMessage().contains(" key"));
        }
        Assert.assertTrue(exceptionTrhown);
    }

    @Test
    public void testParseWithValidActualResponse() throws IOException {
        JsonNode searchResponse = loadFromResourcesFile("valid-actual-search-response.json");
        List<JiraIssue> parsedIssues = underTest.parseResponse(searchResponse);
        Assert.assertEquals(parsedIssues.size(), 1);
        JiraIssue issue = parsedIssues.get(0);
        Assert.assertEquals(issue.getKey(), "STATUS-729");
        Assert.assertEquals(issue.getPriority(), "High");
        Assert.assertEquals(issue.getReporter(), "Agaci Avinas");
        Assert.assertEquals(issue.getUrl(), JIRA_BROWSE_URL + "/" + "STATUS-729");
        Assert.assertEquals(issue.getDateCreated(), "2024-01-30T07:17:26.000+0000");
        Assert.assertTrue(issue.getDescription().startsWith("h3. Issue Summary"));
        Assert.assertTrue(issue.getSummary().startsWith("Mismatch between the uptime percentage"));
    }

    private JsonNode loadFromResourcesFile(String filename) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        JsonNode searchResponse = objectMapper.readTree(new File(classLoader.getResource(filename).getFile()));
        return searchResponse;
    }
}
