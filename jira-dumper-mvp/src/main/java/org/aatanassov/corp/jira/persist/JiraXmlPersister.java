package org.aatanassov.corp.jira.persist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.util.DefaultXmlPrettyPrinter;
import org.aatanassov.corp.jira.model.JiraElement;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JiraXmlPersister implements JiraFilePersister {
    private final XmlMapper xmlMapper = new XmlMapper();
    @Override
    public void persistJiraElements(List<? extends JiraElement> jiraElements, String filename) {
        ObjectWriter writer = xmlMapper.writer(new DefaultXmlPrettyPrinter());
        try {
            writer.writeValue(new File(filename + ".xml"), jiraElements);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't write jira issues to " + filename + ".xml", e);
        }

    }
}
