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

import org.eclipse.mylar.context.tests.support.MylarTestUtils;
import org.eclipse.mylar.context.tests.support.MylarTestUtils.Credentials;
import org.eclipse.mylar.context.tests.support.MylarTestUtils.PrivilegeLevel;
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
		validate(Constants.TEST_TRAC_010_URL);
	}	

	public void testValidate010DigestAuth() throws Exception {
		validate(Constants.TEST_TRAC_010_DIGEST_AUTH_URL);
	}	

	protected void validate(String url) throws Exception {
		Credentials credentials = MylarTestUtils.readCredentials(PrivilegeLevel.USER);
		
		// standard connect
		connect(url);
		repository.validate();

		// invalid url
		connect("http://non.existant/repository");
		try {
			repository.validate();
			fail("Expected TracException");
		} catch (TracException e) {
		}

		// invalid password
		connect(url, credentials.username, "wrongpassword");
		try {
			repository.validate();
			fail("Expected TracLoginException");
		} catch (TracLoginException e) {
		}

		// invalid username
		connect(url, "wrongusername", credentials.password);
		try {
			repository.validate();
			fail("Expected TracLoginException");
		} catch (TracLoginException e) {
		}
	}

}
