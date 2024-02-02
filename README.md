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
- [ ] Serialize result into xml
- [ ] Serialize result into json
- [ ] Add unit tests
- [ ] Add javadoc and documentation

### MVP
Add capability to persist multiple pages in separate files. Additional arguments: page size, max number of pages, output folder.

### Future Roadmap
Features for future development. These are not prioritized:
- Introduce QueryBuilder so that if customer wants to change the "base" query it is easier and also if he wants multiple queries it is easier.
- Add BasicAuth
- parallel execution of dumping
- make the jql and output information configurable
- yaml support
- Make the dump live and auto updatable. If this is relevant to customer.