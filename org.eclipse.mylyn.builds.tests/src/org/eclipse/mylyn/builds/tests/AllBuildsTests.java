/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.builds.tests.util.JUnitResultGeneratorTest;

/**
 * @author Steffen Pingel
 */
public class AllBuildsTests {

	public static Test suite() {
		return suite(false);
	}

	public static Test suite(boolean defaultOnly) {
		TestSuite suite = new TestSuite("Tests for org.eclipse.mylyn.builds.tests");
		suite.addTestSuite(JUnitResultGeneratorTest.class);
		return suite;
	}

}
