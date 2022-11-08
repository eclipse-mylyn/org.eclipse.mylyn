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

1. Run the [Mylyn Wikitext release build](https://ci.eclipse.org/docs/job/mylyn-wikitext-release/).
2. Login to [Nexus Repository Manager](https://oss.sonatype.org/#stagingRepositories) and search for artifacts under the group-id 'org.eclipse.mylyn.docs'. Wikitext core jars for the just released version should be present in the Release repository.
3. Verify the p2 artifacts are published at the release location, e.g.  http://download.eclipse.org/mylyn/docs/releases/3.0/ and http://download.eclipse.org/mylyn/docs/releases/3.0.xx/
4. Create a new release entry from the [Mylyn Docs project page](https://projects.eclipse.org/projects/mylyn.docs/governance). Click the 'Create new release' link in the side bar.
