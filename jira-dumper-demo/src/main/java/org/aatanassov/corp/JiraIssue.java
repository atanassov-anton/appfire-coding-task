package org.aatanassov.corp;

import java.util.List;

public class JiraIssue {
    private String key;
    private String url;
    private String summary;
    private String type;
    private String priority;
    private String description;
    private String reporter;
    private String dateCreated;
    private List<Comment> comments;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public static class Comment {
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

    @Override
    public String toString() {
        return "JiraIssue{" +
                "key='" + key + '\'' +
                ", url='" + url + '\'' +
                ", summary='" + summary + '\'' +
                ", type='" + type + '\'' +
                ", priority='" + priority + '\'' +
                ", description='" + description + '\'' +
                ", reporter='" + reporter + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                ", comments=" + comments +
                '}';
    }
}
