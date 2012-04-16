/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Sascha Scholz (SAP) - improvements
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests.core.client;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.httpclient.Cookie;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.gerrit.tests.support.GerritFixture;
import org.eclipse.mylyn.gerrit.tests.support.GerritHarness;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritAuthenticationState;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritConfiguration;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.CommentLink;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gerrit.reviewdb.Account;

/**
 * @author Steffen Pingel
 * @author Sascha Scholz
 */
public class GerritClientTest extends TestCase {

	private GerritHarness harness;

	private GerritClient client;

	@Override
	@Before
	public void setUp() throws Exception {
		harness = GerritFixture.current().harness();
		client = harness.client();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		harness.dispose();
	}

	@Test
	public void testRefreshConfig() throws Exception {
		GerritConfiguration config = client.refreshConfig(null);
		assertNotNull(config);
		assertNotNull(config.getGerritConfig());
		assertNotNull(config.getProjects());
	}

	@Test
	public void testGetAccount() throws Exception {
		if (!GerritFixture.current().canAuthenticate()) {
			return; // skip
		}
		Account account = client.getAccount(null);
		assertEquals(CommonTestUtil.getShortUserName(harness.readCredentials()), account.getUserName());
	}

	@Test
	public void testGetAccountAnonymous() throws Exception {
		client = harness.clientAnonymous();
		try {
			client.getAccount(null);
			fail("Expected GerritException");
		} catch (GerritException e) {
			assertEquals("Not Signed In", e.getMessage());
		}
	}

	@Test
	public void testRefreshConfigCommentLinks() throws Exception {
		if (!GerritFixture.current().canAuthenticate()) {
			return; // skip
		}

		List<CommentLink> expected = new ArrayList<CommentLink>();
		expected.add(new CommentLink("(I[0-9a-f]{8,40})", "<a href=\"#q,$1,n,z\">$&</a>"));
		expected.add(new CommentLink("(bug\\s+)(\\d+)", "<a href=\"http://bugs.mylyn.org/show_bug.cgi?id=$2\">$&</a>"));
		expected.add(new CommentLink("([Tt]ask:\\s+)(\\d+)", "$1<a href=\"http://tracker.mylyn.org/$2\">$2</a>"));

		client = harness.client();
		GerritConfiguration config = client.refreshConfig(null);
		List<CommentLink> links = config.getGerritConfig().getCommentLinks2();
		assertEquals(expected, links);
	}

	@Test
	public void testInvalidXrsfKey() throws Exception {
		if (!GerritFixture.current().canAuthenticate()) {
			return;
		}

		WebLocation location = harness.location();
		GerritAuthenticationState authState = new GerritAuthenticationState();
		authState.setCookie(new Cookie(WebUtil.getHost(location.getUrl()), "xrsfKey", "invalid"));
		client = new GerritClient(location, null, authState, "invalid");
		client.getAccount(null);
	}

}
