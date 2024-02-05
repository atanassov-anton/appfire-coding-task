package org.aatanassov.corp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.aatanassov.corp.jira.model.Comment;
import org.aatanassov.corp.jira.model.JiraIssue;
import org.apache.http.impl.client.HttpClients;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class JiraIssuesExtractorIntegrationTest {
    public static final String JQL = "project = JRASERVER AND status = Closed ORDER BY updated ASC";
    private JiraIssuesExtractor underTest;
    private File outputFolder;
    private XmlMapper xmlMapper;
    private ObjectMapper mapper;

    @BeforeTest
    public void init() {
        ClassLoader classLoader = getClass().getClassLoader();
        File resoureFile = new File(classLoader.getResource("missing-key-search-response.json").getFile());
        outputFolder = new File(resoureFile.getParentFile().getParentFile().getParentFile(), "target");
        xmlMapper = new XmlMapper();
        mapper = new ObjectMapper();
    }

    @Test
    public void testXmlSeparateFiles() throws URISyntaxException, IOException {
        underTest = new JiraIssuesExtractor(Main.JIRA_ATLASSIAN_REST_ENDPOINT, Main.JIRA_ATLASSIAN_BROWSE_URL,
                HttpClients.createDefault(), outputFolder.getAbsolutePath(), "xml");
        underTest.dumpIssues(JQL, 1, 2);
        JiraIssue firstIssue = xmlMapper.readValue(new File(outputFolder, "issues_0.xml"), JiraIssue[].class)[0];
        assert_JRASERVER_3943(firstIssue);
        JiraIssue secondIssue = xmlMapper.readValue(new File(outputFolder, "issues_1.xml"), JiraIssue[].class)[0];
        assert_JRASERVER_5064(secondIssue);
    }

    @Test
    public void testXmlSameFile()  throws URISyntaxException, IOException {
        underTest = new JiraIssuesExtractor(Main.JIRA_ATLASSIAN_REST_ENDPOINT, Main.JIRA_ATLASSIAN_BROWSE_URL,
                HttpClients.createDefault(), outputFolder.getAbsolutePath(), "xml");
        underTest.dumpIssues(JQL, 2, 2);
        JiraIssue[] issues = xmlMapper.readValue(new File(outputFolder, "issues_0.xml"), JiraIssue[].class);
        assert_JRASERVER_3943(issues[0]);
        assert_JRASERVER_5064(issues[1]);
    }

    @Test
    public void testJsonSeparateFiles() throws URISyntaxException, IOException {
        underTest = new JiraIssuesExtractor(Main.JIRA_ATLASSIAN_REST_ENDPOINT, Main.JIRA_ATLASSIAN_BROWSE_URL,
                HttpClients.createDefault(), outputFolder.getAbsolutePath(), "json");
        underTest.dumpIssues(JQL, 1, 2);
        JiraIssue firstIssue = mapper.readValue(new File(outputFolder, "issues_0.json"), JiraIssue[].class)[0];
        assert_JRASERVER_3943(firstIssue);
        JiraIssue secondIssue = mapper.readValue(new File(outputFolder, "issues_1.json"), JiraIssue[].class)[0];
        assert_JRASERVER_5064(secondIssue);
    }

    @Test
    public void testJsonSameFile()  throws URISyntaxException, IOException {
        underTest = new JiraIssuesExtractor(Main.JIRA_ATLASSIAN_REST_ENDPOINT, Main.JIRA_ATLASSIAN_BROWSE_URL,
                HttpClients.createDefault(), outputFolder.getAbsolutePath(), "json");
        underTest.dumpIssues(JQL, 2, 2);
        JiraIssue[] issues = mapper.readValue(new File(outputFolder, "issues_0.json"), JiraIssue[].class);
        assert_JRASERVER_3943(issues[0]);
        assert_JRASERVER_5064(issues[1]);
    }

    private void assert_JRASERVER_3943(JiraIssue jiraIssue) {
        assertEquals(jiraIssue.getKey(), "JRASERVER-3943");
        assertEquals(jiraIssue.getDateCreated(), "2004-06-17T09:30:16.000+0000");
        assertEquals(jiraIssue.getReporter(), "Fred Wong");
        assertEquals(jiraIssue.getPriority(), "High");
        assertEquals(jiraIssue.getType(), "Support Request");
        assertEquals(jiraIssue.getSummary(), "Error in SQL Server 2000");
        assertTrue(jiraIssue.getDescription().startsWith("I have installed JIRA"));
        assertEquals(jiraIssue.getUrl(), Main.JIRA_ATLASSIAN_BROWSE_URL + "JRASERVER-3943");

        assertEquals(jiraIssue.getComments().size(), 3);

        Comment firstComment = jiraIssue.getComments().get(0);
        assertTrue(firstComment.getText().startsWith("This appears to be an error with SQLServer"));
        assertEquals(firstComment.getAuthorUsername(), "Scott Farquhar");

        Comment secondComment = jiraIssue.getComments().get(1);
        assertTrue(secondComment.getText().startsWith("Many thanks"));
        assertEquals(secondComment.getAuthorUsername(), "Fred Wong");

        Comment thirdComment = jiraIssue.getComments().get(2);
        assertTrue(thirdComment.getText().startsWith("Fred, I've assumed you solved the JIRA problem with"));
        assertEquals(thirdComment.getAuthorUsername(), "Jeff Turner");
    }

    private void assert_JRASERVER_5064(JiraIssue jiraIssue) {
        assertEquals(jiraIssue.getKey(), "JRASERVER-5064");
        assertEquals(jiraIssue.getDateCreated(), "2004-10-26T14:43:45.000+0000");
        assertEquals(jiraIssue.getReporter(), "Reto Peter");
        assertEquals(jiraIssue.getPriority(), "Medium");
        assertEquals(jiraIssue.getType(), "Support Request");
        assertEquals(jiraIssue.getSummary(), "How to delete a Logged work");
        assertTrue(jiraIssue.getDescription().startsWith("I got a simple question: How can I delete a work logged"));
        assertEquals(jiraIssue.getUrl(), Main.JIRA_ATLASSIAN_BROWSE_URL + "JRASERVER-5064");

        assertEquals(jiraIssue.getComments().size(), 2);

        Comment firstComment = jiraIssue.getComments().get(0);
        assertTrue(firstComment.getText().startsWith("Hi,"));
        assertEquals(firstComment.getAuthorUsername(), "AntonA");

        Comment secondComment = jiraIssue.getComments().get(1);
        assertTrue(secondComment.getText().startsWith("Please vote for the linked issue to"));
        assertEquals(secondComment.getAuthorUsername(), "AntonA");
    }
}
