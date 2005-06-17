package org.eclipse.mylar.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.mylar.core.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(AbstractTaskscapeTest.class);
		suite.addTestSuite(TaskscapeTest.class);
		//$JUnit-END$
		return suite;
	}

}
