package org.eclipse.mylar.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllCoreTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.mylar.core.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(DegreeOfInterestTest.class);
		suite.addTestSuite(ContextTest.class);
		//$JUnit-END$
		return suite;
	}

}
