/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Steffen Pingel
 */
public class AllCoreTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.mylyn.core.tests");
		suite.addTestSuite(WebUtilTest.class);
		suite.addTestSuite(SslProtocolSocketFactoryTest.class);
		return suite;
	}

}
