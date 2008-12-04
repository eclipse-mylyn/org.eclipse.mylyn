/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.tests;

import junit.framework.Test;
import junit.framework.TestCase;

/**
 * A test case that runs all headless tests that are available on the classpath
 * 
 * @author David Green
 */
@NoDiscovery
public class HeadlessTests extends TestCase {

	public static Test suite() {
		boolean inEclipseRuntime = DiscoveryTestSuite.inEclipseRuntime();
		final DiscoveryTestSuite testSuite = new DiscoveryTestSuite(new Filter(inEclipseRuntime));
		testSuite.setName("Test for org.eclipse.mylyn.wikitext.tests Headless Tests"
				+ (inEclipseRuntime ? " Stand-Alone" : " In Eclipse"));
		return testSuite;
	}

	private static class Filter implements ClassFilter {
		private final boolean inEclipseRuntime;

		public Filter(boolean inEclipseRuntime) {
			this.inEclipseRuntime = inEclipseRuntime;
		}

		public boolean filter(Class<?> clazz) {
			if (HeadlessTests.class == clazz) {
				return true;
			}
			if (clazz.getAnnotation(HeadRequired.class) != null) {
				return true;
			}
			if (!inEclipseRuntime && clazz.getAnnotation(EclipseRuntimeRequired.class) != null) {
				return true;
			}

			return false;
		}
	}

}
