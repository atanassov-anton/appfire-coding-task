package org.aatanassov.corp.jira.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aatanassov.corp.jira.model.JiraIssue;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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
            assertTrue(exception.getMessage().contains("jiraRestEndpoint"));
        }
        assertTrue(exceptionTrhown);
    }

    @Test
    public void testGetQueryAndValidateOutput() throws URISyntaxException {
        underTest = new JiraSearchQuery("https://www.jira.com/rest/api", "doesn't matter", "project=AA");
        URI query = underTest.getQuery(0, 20);
        assertTrue(query.toString().startsWith("https://www"));
        assertEquals(query.getHost(), "www.jira.com");
        assertEquals(query.getPath(), "/rest/api/search");
        String rawQuery = query.getRawQuery();
        assertTrue(rawQuery.contains("jql=project%3DAA"));
        assertTrue(rawQuery.contains("fields="));
        assertTrue(rawQuery.contains("startAt=0"));
        assertTrue(rawQuery.contains("maxResults=20"));
        assertTrue(rawQuery.contains("summary"));
        assertTrue(rawQuery.contains("description"));
    }

    @Test
    public void testParseWithNull() {
        boolean exceptionTrhown = false;
        try {
            underTest.parseResponse(null);
        } catch (NullPointerException exception) {
            exceptionTrhown = true;
            assertTrue(exception.getMessage().contains("searchQueryResponse"));
        }
        assertTrue(exceptionTrhown);
    }

    @Test
    public void testParseWithEmptyNode() throws JsonProcessingException {
        JsonNode searchResponse = objectMapper.readTree("{}");
        boolean exceptionTrhown = false;
        try {
            underTest.parseResponse(searchResponse);
        } catch (RuntimeException exception) {
            exceptionTrhown = true;
            assertTrue(exception.getMessage().contains("issues"));
        }
        assertTrue(exceptionTrhown);
    }

    @Test
    public void testParseWithMissingKey() throws IOException {
        JsonNode searchResponse = loadFromResourcesFile("missing-key-search-response.json");
        boolean exceptionTrhown = false;
        try {
            underTest.parseResponse(searchResponse);
        } catch (RuntimeException exception) {
            exceptionTrhown = true;
            assertTrue(exception.getMessage().contains(" key"));
        }
        assertTrue(exceptionTrhown);
    }

    @Test
    public void testParseWithValidActualResponse() throws IOException {
        JsonNode searchResponse = loadFromResourcesFile("valid-actual-search-response.json");
        List<JiraIssue> parsedIssues = underTest.parseResponse(searchResponse);
        assertEquals(parsedIssues.size(), 1);
        JiraIssue issue = parsedIssues.get(0);
        assertEquals(issue.getKey(), "STATUS-729");
        assertEquals(issue.getPriority(), "High");
        assertEquals(issue.getReporter(), "Agaci Avinas");
        assertEquals(issue.getUrl(), JIRA_BROWSE_URL + "/" + "STATUS-729");
        assertEquals(issue.getDateCreated(), "2024-01-30T07:17:26.000+0000");
        assertTrue(issue.getDescription().startsWith("h3. Issue Summary"));
        assertTrue(issue.getSummary().startsWith("Mismatch between the uptime percentage"));
    }

    private JsonNode loadFromResourcesFile(String filename) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        JsonNode searchResponse = objectMapper.readTree(new File(classLoader.getResource(filename).getFile()));
        return searchResponse;
    }
}
