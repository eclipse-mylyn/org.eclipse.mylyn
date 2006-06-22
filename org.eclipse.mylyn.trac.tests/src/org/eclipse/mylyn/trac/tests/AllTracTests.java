/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.trac.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class AllTracTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.mylar.trac.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(TracXmlRpcTest.class);
		suite.addTestSuite(TracSearchTest.class);
		suite.addTestSuite(TracTicketTest.class);
		suite.addTestSuite(TracXmlRpcRepositoryTest.class);
		suite.addTestSuite(TracXmlRpcRepositorySearchTest.class);
		suite.addTestSuite(Trac09RepositoryTest.class);
		suite.addTestSuite(Trac09RepositorySearchTest.class);
		//$JUnit-END$
		return suite;
	}

}
