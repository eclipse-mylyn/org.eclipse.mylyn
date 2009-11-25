/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tests.integration;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.compatibility.JavaRuntimeVersionChecker;

/**
 * @author Steffen Pingel
 */
public class JavaRuntimeVersionCheckerTest extends TestCase {

	public void testParseVersion() {
		assertEquals(1.0f, JavaRuntimeVersionChecker.parseVersion("1.0"));
		assertEquals(1.0f, JavaRuntimeVersionChecker.parseVersion("1.0.3_abc"));
		assertEquals(1.6f, JavaRuntimeVersionChecker.parseVersion("1.6"));
		assertEquals(1.6f, JavaRuntimeVersionChecker.parseVersion("1.6."));
		assertEquals(1.6f, JavaRuntimeVersionChecker.parseVersion("1.6.0"));
		assertEquals(1.6f, JavaRuntimeVersionChecker.parseVersion("1.6.0_14-b08"));
		assertEquals(1.5f, JavaRuntimeVersionChecker.parseVersion("1.5.0_16"));
		assertEquals(1.5f, JavaRuntimeVersionChecker.parseVersion("1.5.0.07-_20_mar_2007_05_31"));

		assertEquals(0.0f, JavaRuntimeVersionChecker.parseVersion("1.0ab.3_abc"));
		assertEquals(0.0f, JavaRuntimeVersionChecker.parseVersion("1.-5"));
		assertEquals(0.0f, JavaRuntimeVersionChecker.parseVersion("1.5_14"));
		assertEquals(0.0f, JavaRuntimeVersionChecker.parseVersion("abc"));
		assertEquals(0.0f, JavaRuntimeVersionChecker.parseVersion("jvmxp3260-20080816_22093"));
	}

	public void testIsJavaVersionSmallerThanDefault() {
		assertFalse(JavaRuntimeVersionChecker.isJavaVersionSmallerThan(1.0f));
		assertFalse(JavaRuntimeVersionChecker.isJavaVersionSmallerThan(1.5f));
		assertTrue(JavaRuntimeVersionChecker.isJavaVersionSmallerThan(10.0f));
	}

	public void testIsJavaVersionSmallerThan() {
		String runtimeVersion = System.getProperty("java.runtime.version");
		String version = System.getProperty("java.version");
		try {
			System.setProperty("java.runtime.version", "jvmxp3260-20080816_22093");
			System.setProperty("java.version", "1.5.0");
			assertFalse(JavaRuntimeVersionChecker.isJavaVersionSmallerThan(1.5f));
			assertTrue(JavaRuntimeVersionChecker.isJavaVersionSmallerThan(1.6f));
			System.setProperty("java.version", "invalid");
			assertFalse(JavaRuntimeVersionChecker.isJavaVersionSmallerThan(0.0f));
			assertFalse(JavaRuntimeVersionChecker.isJavaVersionSmallerThan(10.0f));
		} finally {
			System.setProperty("java.runtime.version", runtimeVersion);
			System.setProperty("java.version", version);
		}
	}

}
