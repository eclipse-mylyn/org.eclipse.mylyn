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
package org.eclipse.mylyn.builds.sample.mvn.reactor.module2;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class JUnit3Test extends TestCase {

	public void testPass() {
		System.out.println("pass");
	}

	public void testFail() {
		System.out.println("fail");
		assertEquals(1, 2);
	}

	public void testError() throws Exception {
		System.out.println("error");
		throw new Exception();
	}

}
