package org.eclipse.mylyn.gerrit.core;


import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for com.stericsson.eclipse.mylyn.gerrit.core");
        //$JUnit-BEGIN$
        suite.addTestSuite(GerritAttributeTest.class);
        suite.addTestSuite(GerritConnectorTest.class);
        suite.addTestSuite(GerritTaskAttributeMapperPDETest.class);
        suite.addTestSuite(GerritTaskTest.class);
        //$JUnit-END$
        return suite;
    }

}
