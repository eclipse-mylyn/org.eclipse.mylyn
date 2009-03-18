Requirements
============

- Java 5.0 or later
- Eclipse 3.4
- Eclipse 3.3


Setup
=====

Copy local.sh-template to local.sh and customize it for your local
system.

By default the following directory structure is expected under
org.eclipse.mylyn.releng:

 jdk                Java 5.0
 eclipse-3.3        Eclipse 3.3 + Test Framework for building
 eclipse-3.4        Eclipse 3.4 + Test Framework for building
 eclipse-test-3.3   Eclipse 3.3 + Test Framework for testing
 eclipse-test-3.4   Eclipse 3.4 + Test Framework for testing

Setup on Mac OS X
=================

1. copy local.sh-template to local.sh
 - alter JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Home
2. chmod 0755 *.sh
3. alter 3.4/config/build.properties
 - bootclasspath=${java.home}/../Classes/classes.jar
4. alter 3.3/config/build.properties
 - bootclasspath=${java.home}/../Classes/classes.jar


Using Headless Build
====================

Building:

 build.sh

Running tests (requires building):

 run-tests-3.4.sh

 
Release Howto
=============


==== Before the Release ====
* Run AllTests
* Update the user guide from the wiki
** Run org.eclipse.mylyn.help.ui/build-helper.xml as an Ant Build (ensure org.eclipse.mylyn.wikitext* is checked out into your workspace)
** Review the user guide and commit changes to CVS
* Backport changes to the 3.3 branch
** Update 3.3 workspace to the latest
** Select branched plug-ins
** Team > Merge
*** end tag: HEAD
*** start tag: Merged_e_3_3_m_3_x
*** select Preview the merge in the synchronize perspective
** Merge changes and resolve conflicts
** Tag branched plug-ins in '''3.4''' workspace (i.e. cvs head) with Merged_e_3_3_m_3_x
*** select Move tag if already exists

==== The Release ====
''Major releases only''
* Tag the release as R_x_y_z and R_x_y_y_e_3_3
----
* Build the release:
 ssh releng@mylyn.eclipse.org
 cd weekly
 
 # for major releases set QUALIFIER to v2008...:
 emacs local.sh
 ./build.sh -rebuild
 
 # for milestone releases use auto generated version:
 ./build.sh

* Upload the release:
 ./upload.sh [username]
* Prompts twice for password, if the second login times out:
 ./upload.sh [username] -extract
----
* Sign the release (e.g. major=3.0.2, build=v20080815-2300)
 ssh [username]@build.eclipse.org
 cd /shared/tools/mylyn/weekly
 ./sign-update-site.sh [major] [build]
* If signing times out rerun the ./sign-update-site.sh script it will resume the signing
* The version in the update archive is now signed
----
''Major releases only''
* Move the release from the weekly site to the main update site:
 ssh [username]@build.eclipse.org
 cd /shared/tools/mylyn/weekly
 ./promote.sh [major] [build]
* Update the version number on the download page
* Create a new section in the download archive
* Create a new section in the New &amp; Noteworthy
----
''Ganymede releases only''
* Copy build to ganymede update site:
 ssh [username]@build.eclipse.org
 cd ~/downloads/tools/mylyn/update-archive/[major]/[minor]
 cp -a e3.4/* extras/ incubator/ *-e3.4.zip ~/downloads/tools/mylyn/update/ganymede
* Update mylyn.sc file

==== When the build has propagated to mirrors ====
''Major releases only''
* Add the mirror attribute to all site.xml files and regenerate meta-data with correct update site identifiers:
 ssh [username]@build.eclipse.org
 cd /shared/tools/mylyn/weekly
 ./add-mirrors.sh
 ./generate-p2-metadata.sh
* Check that the site.xml files have the following URL set:
<source lang="xml">
   mirrorsURL="http://www.eclipse.org/downloads/download.php?file=/tools/mylyn/update/[location]/site.xml&amp;protocol=http&amp;format=xml"
</source>

==== After the release ====
''Major releases only''
* For head and e3.3 branch: Replace version numbers in all MANIFEST.MF and feature.xml files, e.g. 2.3.0.qualifier -> 2.3.1.qualifier and 2.3.0.mylynQualifier -> 2.3.1.mylynQualifier
* Make sure changes are applied to the 3.3 branch as well as the 3.4 branch

 