/*******************************************************************************
* Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests.client;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.context.tests.support.TestUtil.Credentials;
import org.eclipse.mylyn.context.tests.support.TestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.TracException;
import org.eclipse.mylyn.internal.trac.core.client.TracLoginException;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.trac.tests.support.TracTestConstants;

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
		validate(TracTestConstants.TEST_TRAC_010_URL);
	}

	public void testValidate010DigestAuth() throws Exception {
		validate(TracTestConstants.TEST_TRAC_010_DIGEST_AUTH_URL);
	}

	public void testValidate011() throws Exception {
		validate(TracTestConstants.TEST_TRAC_011_URL);
	}

	public void testValidate010FormAuth() throws Exception {
		validate(TracTestConstants.TEST_TRAC_010_FORM_AUTH_URL);
	}

	protected void validate(String url) throws Exception {
		Credentials credentials = TestUtil.readCredentials(PrivilegeLevel.USER);

		// standard connect
		connect(url);
		repository.validate(callback);

		// invalid url
		connect("http://non.existant/repository");
		try {
			repository.validate(callback);
			fail("Expected TracException");
		} catch (TracException e) {
		}

		// invalid password
		connect(url, credentials.username, "wrongpassword");
		try {
			repository.validate(callback);
			fail("Expected TracLoginException");
		} catch (TracLoginException e) {
		}

		// invalid username
		connect(url, "wrongusername", credentials.password);
		try {
			repository.validate(callback);
			fail("Expected TracLoginException");
		} catch (TracLoginException e) {
		}
	}

	public void testProxy() throws Exception {
		connect(TracTestConstants.TEST_TRAC_010_URL, "", "", new Proxy(Type.HTTP, new InetSocketAddress(
				"invalidhostname", 8080)));
		try {
			repository.validate(callback);
			fail("Expected IOException");
		} catch (TracException e) {
		}
	}

}
