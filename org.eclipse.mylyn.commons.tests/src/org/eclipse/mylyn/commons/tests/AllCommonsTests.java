/*******************************************************************************
 * Copyright (c) 2000, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.commons.tests.net.SslProtocolSocketFactoryTest;
import org.eclipse.mylyn.commons.tests.net.WebUtilTest;

/**
 * @author Mik Kersten
 */
public class AllCommonsTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.mylyn.commons.tests");
		suite.addTestSuite(SslProtocolSocketFactoryTest.class);
		suite.addTestSuite(WebUtilTest.class);
		return suite;
	}

}
