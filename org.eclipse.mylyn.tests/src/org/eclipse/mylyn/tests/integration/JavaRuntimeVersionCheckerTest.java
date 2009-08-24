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

import org.eclipse.mylyn.internal.compatibility.JavaRuntimeVersionChecker;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 */
public class JavaRuntimeVersionCheckerTest extends TestCase {

	public void testGetJavaVersion() {
		assertEquals(1.0f, JavaRuntimeVersionChecker.parseVersion("1.0"));
		assertEquals(1.0f, JavaRuntimeVersionChecker.parseVersion("1.0.3_abc"));
		assertEquals(1.6f, JavaRuntimeVersionChecker.parseVersion("1.6"));
		assertEquals(1.6f, JavaRuntimeVersionChecker.parseVersion("1.6."));
		assertEquals(1.6f, JavaRuntimeVersionChecker.parseVersion("1.6.0_14-b08"));
		assertEquals(1.5f, JavaRuntimeVersionChecker.parseVersion("1.5.0_16"));
		assertEquals(0.0f, JavaRuntimeVersionChecker.parseVersion("1.0ab.3_abc"));
		assertEquals(0.0f, JavaRuntimeVersionChecker.parseVersion("1.-5"));
		assertEquals(0.0f, JavaRuntimeVersionChecker.parseVersion("1.5_14"));
		assertEquals(0.0f, JavaRuntimeVersionChecker.parseVersion("abc"));
	}

}
