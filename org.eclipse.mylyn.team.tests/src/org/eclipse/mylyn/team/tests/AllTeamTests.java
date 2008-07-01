/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.team.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Mik Kersten
 */
public class AllTeamTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.mylyn.team.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestSyncViewRefresh.class);
		suite.addTestSuite(ChangeSetManagerTest.class);
		suite.addTestSuite(CommitTemplateTest.class);
		suite.addTestSuite(TeamPropertiesLinkProviderTest.class);
		//$JUnit-END$
		return suite;
	}

}
