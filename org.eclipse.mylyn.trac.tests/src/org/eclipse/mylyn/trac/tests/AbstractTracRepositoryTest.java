/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.trac.tests;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.trac.core.TracLoginException;
import org.eclipse.mylar.trac.tests.support.AbstractTracRepositoryFactory;

/**
 * Test cases for classes that implement {@link ITracRepositor}.
 * 
 * @author Steffen Pingel
 */
public abstract class AbstractTracRepositoryTest extends TestCase {

	protected AbstractTracRepositoryFactory factory;

	public AbstractTracRepositoryTest(AbstractTracRepositoryFactory factory) {
		this.factory = factory;
	}

	public void testValidate() throws Exception {
		factory.connectRepository1();
		factory.repository.validate();
	}

	public void testValidateFailAuthWrongPassword() throws Exception {
		factory.connect(Constants.TEST_REPOSITORY1_URL, Constants.TEST_REPOSITORY1_ADMIN_USERNAME, "wrongpassword");
		try {
			factory.repository.validate();
			fail("Expected TracLoginException");
		} catch (TracLoginException e) {
		}
	}

	public void testValidateFailAuthWrongUsername() throws Exception {
		factory.connect(Constants.TEST_REPOSITORY1_URL, "wrongusername", Constants.TEST_REPOSITORY1_ADMIN_PASSWORD);
		try {
			factory.repository.validate();
			fail("Expected TracLoginException");
		} catch (TracLoginException e) {
		}
	}

}
