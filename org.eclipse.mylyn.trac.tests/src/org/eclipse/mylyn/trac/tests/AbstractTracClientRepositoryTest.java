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

import org.eclipse.mylar.internal.trac.core.TracException;
import org.eclipse.mylar.internal.trac.core.TracLoginException;
import org.eclipse.mylar.internal.trac.core.ITracClient.Version;

/**
 * Test cases for classes that implement {@link ITracClient}.
 * 
 * @author Steffen Pingel
 */
public class AbstractTracClientRepositoryTest extends AbstractTracClientTest {

	public AbstractTracClientRepositoryTest(Version version) {
		super(version);
	}

	public void testValidate010() throws Exception {
		connect010();
		validate();
	}	

	public void testValidate010DigestAuth() throws Exception {
		connect010DigestAuth();
		validate();
	}	

	protected void validate() throws TracException {
		repository.validate();
	}

	public void testValidateFailWrongUrl() throws Exception {
		connect("http://non.existant/repository");
		try {
			repository.validate();
			fail("Expected TracException");
		} catch (TracException e) {
		}
	}

	public void testValidateFailAuthWrongPassword() throws Exception {
		connect(Constants.TEST_REPOSITORY1_URL, Constants.TEST_REPOSITORY1_ADMIN_USERNAME, "wrongpassword");
		try {
			repository.validate();
			fail("Expected TracLoginException");
		} catch (TracLoginException e) {
		}
	}

	public void testValidateFailAuthWrongUsername() throws Exception {
		connect(Constants.TEST_REPOSITORY1_URL, "wrongusername", Constants.TEST_REPOSITORY1_ADMIN_PASSWORD);
		try {
			repository.validate();
			fail("Expected TracLoginException");
		} catch (TracLoginException e) {
		}
	}

}
