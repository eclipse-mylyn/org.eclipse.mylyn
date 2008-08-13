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
