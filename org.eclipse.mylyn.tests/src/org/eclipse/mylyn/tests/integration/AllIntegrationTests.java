/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tests.integration;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Mik Kersten
 */
public class AllIntegrationTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllIntegrationTests.class.getName());
		suite.addTestSuite(ChangeDataDirTest.class);
		suite.addTest(RepositoryConnectorsTest.suite());
		return suite;
	}

}
