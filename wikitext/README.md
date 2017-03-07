Building
=================================

From the command line:

```
mvn clean verify
```

Releasing
=================================

Firstly, follow the [EDP](https://eclipse.org/projects/dev_process/development_process.php)

1. Anything more than a service release requires a release review.
2. Update the [project metadata (PMI)](https://www.eclipse.org/projects/handbook/#pmi)

Mechanics of performing a release:

1. Login to [Nexus Repository Manager](https://oss.sonatype.org/#stagingRepositories) and ensure that any old staging repositories are closed out or removed
2. Kick off the [WikiText release build](https://hudson.eclipse.org/mylyn/job/mylyn-wikitext-release/)
3. Once completed and successful, fetch the latest from Git
    - note the release tag (e.g. `wikitext.core-3.0.2`)
4. Login to the [Nexus Repository Manager](https://oss.sonatype.org/#stagingRepositories) and close the new staging repository.
5. Verify the Maven artifacts in the staging repository if necessary
6. Run the [Mylyn Docs release build](https://hudson.eclipse.org/mylyn/view/Mylyn%20Docs/job/mylyn-docs-release/)
    - verify the p2 artifacts are published at the release location, e.g.  http://download.eclipse.org/mylyn/docs/releases/3.0/
7. From the [Nexus Repository Manager](https://oss.sonatype.org/#stagingRepositories) release the Maven artifacts to Maven central
8. Update the [project metadata (PMI)](https://www.eclipse.org/projects/handbook/#pmi) to indicate that the release has been performed
9. Run `./update-ui-version.sh` locally, commit and push the changes to Gerrit, +2/merge once successful
10. Email mylyn-docs-dev@eclipse.org to indicate that the release is complete
