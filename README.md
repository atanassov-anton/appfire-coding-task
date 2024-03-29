# How to use the jira-dumper-mvp project
## Prerequisites
- maven is installed
- java 21 or higher is installed
- git is installed

## Steps to run single jar executable
1. Clone the git repo
2. Build the jira-dumper-mvp project with maven: `mvn clean install`
3. To get the help of the single jar execute:
   - `java -jar .\jira-dumper-mvp\target\jira-dumper-mvp-1.0-SNAPSHOT-jar-with-dependencies.jar`
4. Example execution of the single jar:
   - `java -jar .\jira-dumper-mvp\target\jira-dumper-mvp-1.0-SNAPSHOT-jar-with-dependencies.jar json .\jira-dumper-mvp\target\ 10 34`

# appfire-coding-task
## Requirements
The task is to obtain data from JIRA and persist it in both XML and JSON files.
It should be implemented in Java, and you can use any frameworks and libraries you like.

Use the JIRA REST API to obtain the data with the following query:
`issuetype in (Bug, Documentation, Enhancement) and updated > startOfWeek()`.
You can test it here - https://jira.atlassian.com/issues/?jql=

The results should be persisted in XML and JSON files, with the following information:

- Issue Summary
- Issue Key
- URL of the issue (e.g. https://jira.atlassian.com/browse/JRA-40180 )
- Issue Type
- Issue Priority
- Issue Description
- Reporter (username only)
- Issue Created Date
- Comments, with the following:
- comment text
- comment author (username only)

The code should be written in a way that you can easily select the type of output (either XML or JSON) in a generic manner.
Bonus - write a couple of simple tests with JUnit that will take an example input and verify the output - one for JSON and one for XML.

## Milestones

### POC
Verify we can make REST calls to https://jira.atlassian.com instance
#### Plan
- [x] Research JIRA REST API and SDK
- [x] Choose approach - direct HTTP vs SDK
- [x] Setup skeleton project
- [x] Do a POC to verify that access to search REST API for https://jira.atlassian.com instance
- [x] move POC into separate folder

### Demo
Have executable java jar that accepts 1 argument (type of output). It should get 1 page (10 issues) of results and persist them to xml json.
#### Plan
- [x] Find field names for required output. E.g. "Issue Summary" -> summary and craft the whole query against `<base url>/issue` API
- [x] Do the same for `<base url>/issue/{issueIdOrKey}/comment` API
- [x] Parse result into POJO
- [x] Serialize result into xml
- [x] Serialize result into json
- [x] handle serialization format parameter
- [x] Make jar executable

### MVP
Add capability to persist multiple pages in separate files. Additional arguments: page size, max number of results, output folder.
features:
- executable jar
- arguments
  - serialization format (json/xml)
  - page size
  - max number of results 
  - output folder
- queries the jira instance with the jql from requirements and dumps results into files (1 file per page) into outputfolder 
#### Plan
- [x] Refactor main logic into
  - Query
  - QueryBuilder
  - SearchQueryBuilder
  - CommentsQueryBuilder
  - SearchQuery
  - CommentsQuery
  - QueryParser
  - SearchQueryParser
  - CommentsQueryParser
- [x] Add arguments validation
- [x] Add Pagination and multifile output - with hardcoded page size and max number of pages
- [x] Add unit tests to 1 class - JiraSearchQuery
- [x] Add javadoc and documentation to 1 class - JiraSearchQuery
- [x] Add exception handling for 1 method - JiraJsonPersister#persistJiraElements
- [x] Add page size and max number of results main arguments
- [x] Update readme documentation
- [x] Add persister classes
- [x] Add integration tests

### Future Roadmap
Features for future development. These are not prioritized:
- parallel execution of dumping
- Add BasicAuth
- make the jql configurable
- make the output information configurable
- add configurable jira rest endpoint with configurable auth 
- yaml support
- Make the dump live and auto updatable. If this is relevant to customer.