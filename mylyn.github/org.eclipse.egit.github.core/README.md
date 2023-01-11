# GitHub Java API (org.eclipse.egit.github.core)

This project is a Java library for communicating with the
[GitHub API](http://developer.github.com/).  The goal of the library is to
support 100% of the GitHub v3 API.  The library is currently used by the GitHub
Mylyn connector for working with GitHub issues, pull requests, gists, and
repositories from within Eclipse.

Built versions of the GitHub Java library are currently available from the
[Sonatype OSS repository](https://oss.sonatype.org/index.html#nexus-search;quick~org.eclipse.egit.github.core).
Javadoc and source JARs are available as well from the Sonatype OSS repository.

## Packages

The library is composed of 3 main packages.

### Core (org.eclipse.egit.github.core)
This package contains all the model classes representing the resources available
through the API such as repositories, commits, user, teams, and issues.  The
model classes contains getters and setters for all the properties present in
the GitHub API JSON response.  The [Google Gson](http://code.google.com/p/google-gson/)
library is used serialize and deserialize these objects to/from JSON.

### Client (org.eclipse.egit.github.core.client)
This package contains classes communicate with the GitHub API over HTTPS. 
The client package is also responsible for converting JSON responses to
appropriate Java model classes as well as generating request exceptions based on
HTTP status codes.

### Service (org.eclipse.egit.github.core.service)
This package contains the classes that invoke API calls and return model classes
representing resources that were created, read, updated, or deleted.  Service
classes are defined for the resources they interact with such as `IssueService`,
`PullRequestService`, and `RepositoryService`.

## Examples

### Authenticating
```java
//Basic authentication
GitHubClient client = new GitHubClient();
client.setCredentials("user", "passw0rd");
```
```java
//OAuth2 token authentication
GitHubClient client = new GitHubClient();
client.setOAuth2Token("SlAV32hkKG");
```

### Get a user's repositories
The following example prints the number of watchers for each repository
associated with the `defunkt` user.

```java
RepositoryService service = new RepositoryService();
for (Repository repo : service.getRepositories("defunkt"))
  System.out.println(repo.getName() + " Watchers: " + repo.getWatchers());
```

### Merge a Pull Request
The following example checks if Pull Request #45 is mergeable and if it is then
it automatically merges it.

```java
PullRequestService service = new PullRequestService();
service.getClient().setCredentials("user", "passw0rd");
RepositoryId repo = new RepositoryId("rails", "rails");
if (service.getPullRequest(repo, 45).isMergeable())
  service.merge(repo, 45, "merging a pull request");
```

### Fork a repository
The following examples forks the `rails/rails` repository into the currently
authenticated user's account.

```java
RepositoryService service = new RepositoryService();
service.getClient().setCredentials("user", "passw0rd");
RepositoryId toBeForked = new RepositoryId("rails", "rails");
service.forkRepository(toBeForked);
```

### Creating a Gist
The following examples creates a [Gist](https://gist.github.com/) that contains
a single file.

```java
GistFile file = new GistFile();
file.setContent("System.out.println(\"Hello World\");");
Gist gist = new Gist();
gist.setDescription("Prints a string to standard out");
gist.setFiles(Collections.singletonMap("Hello.java", file));
GistService service = new GistService();
service.getClient().setCredentials("user", "passw0rd");
gist = service.createGist(gist); //returns the created gist
```

### Using GitHub Enterprise
Clients use an address of `api.github.com` by default but this can be
overridden when the client is created for the case where you are using
[GitHub Enterprise](http://enterprise.github.com/).

```java
GitHubClient client = new GitHubClient("github.mycompany.com");
UserService service = new UserService(client);
service.getUser("internaluser");
```
## Building
The GitHub Java API is built using [Apache Maven](http://maven.apache.org/).

Run the following command to build a JAR file containing the GitHub Java API
without dependencies:

`$ mvn -f pom-jar.xml clean install`

### All-in-one
The GitHub Java API can also be built as a JAR that includes all the
dependencies (Google Gson).  This technique uses
the [Maven Shade Plugin](http://maven.apache.org/plugins/maven-shade-plugin/) to
build an all-in-one JAR file.

`$ mvn -f pom-jar.xml clean install -P shade`

