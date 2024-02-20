/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/
package org.eclipse.mylyn.builds.sample.ant;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class JUnit4Test {

	@Test
	public void error() throws Exception {
		System.out.println("error");
		throw new Exception();
	}

	@Test
	public void fail() {
		System.out.println("fail");
		assertEquals(1, 2);
	}

	@Ignore
	@Test
	public void ignore() {
		System.out.println("ignore");
	}

	@Test
	public void pass() {
		System.out.println("pass");
	}
	
}
