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
package org.eclipse.mylyn.builds.sample.mvn.reactor.module2;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 */
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
