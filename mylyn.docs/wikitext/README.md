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

1. Run the [Mylyn Wikitext release build](https://ci.eclipse.org/mylyn/view/Docs/job/github-mylyn-docs-release/) with the PERFORM_RELEASE parameter = true.
2. Login to [Nexus Repository Manager](https://oss.sonatype.org/) and search for artifacts under the group-id 'org.eclipse.mylyn.docs'. They should already be present in the release repository.
3. Verify the p2 repository artifacts are published to the download.eclipse.org fileshare. e.g.  http://download.eclipse.org/mylyn/docs/releases/3.0/ and http://download.eclipse.org/mylyn/docs/releases/3.0.xx/ where 'xx' is substituted for the actual patch version.
4. Create a new release entry from the [Mylyn Docs project page](https://projects.eclipse.org/projects/mylyn.docs/governance). Click the 'Create new release' link in the side bar.
