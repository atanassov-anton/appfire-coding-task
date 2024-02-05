package org.aatanassov.corp.jira.persist;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.aatanassov.corp.jira.model.JiraElement;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JiraJsonPersister implements JiraFilePersister {
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public void persistJiraElements(List<? extends JiraElement> jiraElements, String filename) {
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            writer.writeValue(new File(filename + ".json"), jiraElements);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't write jira issues to " + filename + ".json", e);
        }

    }
}
