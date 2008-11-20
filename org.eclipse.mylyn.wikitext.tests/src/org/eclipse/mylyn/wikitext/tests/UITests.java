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
 * A test case that runs all UI tests that are available on the classpath
 * 
 * @author dgreen
 */
@NoDiscovery
public class UITests extends TestCase {

	public static Test suite() {
		final DiscoveryTestSuite testSuite = new DiscoveryTestSuite(new Filter());
		testSuite.setName("All UI Tests");
		return testSuite;
	}

	private static class Filter implements ClassFilter {
		public boolean filter(Class<?> clazz) {
			if (UITests.class == clazz) {
				return true;
			}
			if (clazz.getAnnotation(HeadRequired.class) == null) {
				return true;
			}
			return false;
		}
	}

}
