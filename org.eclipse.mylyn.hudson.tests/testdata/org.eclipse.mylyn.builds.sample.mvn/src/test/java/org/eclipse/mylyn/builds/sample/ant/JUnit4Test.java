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
package org.eclipse.mylyn.builds.sample.ant;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
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
