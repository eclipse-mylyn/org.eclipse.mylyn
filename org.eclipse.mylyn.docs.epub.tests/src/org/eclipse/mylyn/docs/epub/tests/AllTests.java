/*******************************************************************************
 * Copyright (c) 2011-2014 Torkild U. Resheim.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.docs.epub.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.docs.epub.tests.api.TestEPUB;
import org.eclipse.mylyn.docs.epub.tests.api.TestOPS2Publication;
import org.eclipse.mylyn.docs.epub.tests.api.TestOPSPublication;
import org.eclipse.mylyn.docs.epub.tests.core.TestEPUBFileUtil;
import org.eclipse.mylyn.docs.epub.tests.core.TestEclipseTocImporter;
import org.eclipse.mylyn.docs.epub.tests.core.TestOPSValidator;
import org.eclipse.mylyn.docs.epub.tests.core.TestTOCGenerator;

@SuppressWarnings("nls")
public class AllTests {
	public static Test suite() {
		return suite(false);
	}

	public static Test suite(boolean defaultOnly) {
		TestSuite suite = new TestSuite("Tests for org.eclipse.mylyn.docs.epub");
		// API tests
		suite.addTestSuite(TestEPUB.class);
		suite.addTestSuite(TestOPS2Publication.class);
		suite.addTestSuite(TestOPSPublication.class);
		// Core tests
		suite.addTestSuite(TestEPUBFileUtil.class);
		suite.addTestSuite(TestOPSValidator.class);
		suite.addTestSuite(TestTOCGenerator.class);
		suite.addTestSuite(TestEclipseTocImporter.class);
		// Ant tests
		//suite.addTestSuite(TestAntTask.class);
		return suite;
	}
}
