/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.builds.sample.mvn;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
public class JUnit4IgnoreTest {

	@Test
	@Ignore
	public void ignoredTest() {
	}

	@Test
	public void emptyTest() {
	}
	
}
