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

1. Login to [Nexus Repository Manager](https://oss.sonatype.org/#stagingRepositories) and ensure that any old staging repositories are closed out or removed.
   To find the staging repository follow the [Maven Central instructions](http://central.sonatype.org/pages/releasing-the-deployment.html).
2. Run the [Mylyn Docs release build](https://ci.eclipse.org/docs/job/mylyn-docs-release/)
    - use the version numbers that correspond to the versions in the wikitext pom
    - wait until completed before starting the next step
    - verify the p2 artifacts are published at the release location, e.g.  http://download.eclipse.org/mylyn/docs/releases/3.0/
3. Kick off the [WikiText release build](https://ci.eclipse.org/docs/job/mylyn-wikitext-release/)
4. Login to the [Nexus Repository Manager](https://oss.sonatype.org/#stagingRepositories) and close the new staging repository.
5. Verify the Maven artifacts in the staging repository if necessary
6. From the [Nexus Repository Manager](https://oss.sonatype.org/#stagingRepositories) release the Maven artifacts to Maven central
7. Update the [project metadata (PMI)](https://www.eclipse.org/projects/handbook/#pmi) to indicate that the release has been performed.
    - navigate to the [Mylyn Docs project governance page](https://projects.eclipse.org/projects/mylyn.docs/governance)
      and create a new release by clicking 'Create new release' in the side bar
8. Run `./update-ui-version.sh` locally, commit and push the changes to Gerrit, +2/merge once successful
9. Email mylyn-docs-dev@eclipse.org to indicate that the release is complete
10. Add target milestone to bugzilla for the next release
    - https://dev.eclipse.org/committers/bugs/bugz_manager.php

Build Notes
-----------

* order of the steps matters - in particular the Mylyn Docs release build should occur before the WikiText release build,
  since otherwise the Mylyn Docs p2 artifacts end up with a 3-part version number instead of a 4-part version number.  See [bug 513511](https://bugs.eclipse.org/513511) for details.
