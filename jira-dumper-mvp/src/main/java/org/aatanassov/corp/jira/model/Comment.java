package org.aatanassov.corp.jira.model;

public class Comment extends JiraElement {
    private String text;
    private String authorUsername;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "text='" + text + '\'' +
                ", authorUsername='" + authorUsername + '\'' +
                '}';
    }
}
