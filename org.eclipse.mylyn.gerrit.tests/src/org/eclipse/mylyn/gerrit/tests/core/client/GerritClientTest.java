/*******************************************************************************
 /*******************************************************************************
 * Copyright (c) 2011, 2015 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Sascha Scholz (SAP) - improvements
 *     Francois Chouinard (Ericsson)  - Bug 414219 Add new Test
 *     Jacques Bouthillier (Ericsson) - Fix comments for Bug 414219
 *     Jacques Bouthillier (Ericsson) - Bug 414253 Adjust some Test
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests.core.client;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.EnumSet;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.httpclient.Cookie;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.gerrit.tests.support.GerritFixture;
import org.eclipse.mylyn.gerrit.tests.support.GerritHarness;
import org.eclipse.mylyn.internal.gerrit.core.GerritUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritAuthenticationState;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient29;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritConfiguration;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritSystemInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.PatchScriptX;
import org.eclipse.mylyn.internal.gerrit.core.client.data.GerritQueryResult;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.verification.VerificationMode;

import com.google.gerrit.reviewdb.Account;
import com.google.gerrit.reviewdb.Patch.ChangeType;
import com.google.gerrit.reviewdb.Patch.Key;
import com.google.gerrit.reviewdb.PatchSet.Id;

/**
 * @author Steffen Pingel
 * @author Sascha Scholz
 * @author Francois Chouinard
 * @author Jacques Bouthillier
 */
public class GerritClientTest extends TestCase {
	public class TestGerritClient extends GerritClient29 {

		public TestGerritClient(TaskRepository repository, AbstractWebLocation location) {
			super(repository, GerritFixture.current().getGerritVersion());
			initialize(location, null, null, null, null);
		}

		@Override
		protected byte[] fetchBinaryContent(String url, IProgressMonitor monitor) throws GerritException {
			return super.fetchBinaryContent(url, monitor);
		}

		@Override
		protected void fetchRightBinaryContent(PatchScriptX patchScript, Key key, Id rightId, IProgressMonitor monitor)
				throws GerritException {
			super.fetchRightBinaryContent(patchScript, key, rightId, monitor);
		}

		@Override
		protected void fetchLeftBinaryContent(PatchScriptX patchScript, Key key, Id leftId, IProgressMonitor monitor)
				throws GerritException {
			super.fetchLeftBinaryContent(patchScript, key, leftId, monitor);
		}

		@Override
		protected String getUrlForPatchSet(Key key, Id id) throws GerritException {
			return super.getUrlForPatchSet(key, id);
		}

		@Override
		protected String getUrlForPatchSetOrBase(Key key, Id id) throws GerritException {
			return super.getUrlForPatchSetOrBase(key, id);
		}
	}

	private static final String GET_LABELS_OPTION = "LABELS"; //$NON-NLS-1$

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
		GerritConfiguration config = client.refreshConfig(new NullProgressMonitor());
		assertNotNull(config);
		assertNotNull(config.getGerritConfig());
		assertNotNull(config.getProjects());
	}

	@Test
	public void testGetAccount() throws Exception {
		if (!GerritFixture.current().canAuthenticate()) {
			return; // skip
		}
		Account account = client.getAccount(new NullProgressMonitor());
		assertEquals(CommonTestUtil.getShortUserName(harness.readCredentials()), account.getUserName());
	}

	@Test
	public void testGetAccountAnonymous() throws Exception {
		client = harness.clientAnonymous();
		try {
			client.getAccount(new NullProgressMonitor());
			fail("Expected GerritException");
		} catch (GerritException e) {
			assertEquals("Not Signed In", e.getMessage());
		}
	}

	@Test
	public void testGetInfo() throws Exception {
		GerritSystemInfo info = client.getInfo(new NullProgressMonitor());
		if (GerritFixture.current().canAuthenticate()) {
			assertEquals(CommonTestUtil.getShortUserName(harness.readCredentials()), info.getFullName());
		} else {
			assertEquals("Anonymous", info.getFullName());
		}
	}

	@Test
	public void testInvalidXrsfKey() throws Exception {
		if (!GerritFixture.current().canAuthenticate()) {
			return;
		}

		WebLocation location = harness.location();
		GerritAuthenticationState authState = new GerritAuthenticationState();
		authState.setCookie(new Cookie(WebUtil.getHost(location.getUrl()), "xrsfKey", "invalid"));
		client = GerritClient.create(null, location, null, authState, "invalid", null);
		client.getAccount(null);
	}

	@Test
	public void testGetVersion() throws Exception {
		assertEquals(GerritFixture.current().getGerritVersion(), client.getVersion(new NullProgressMonitor()));
	}

	private List<GerritQueryResult> executeQuery(String query) throws GerritException {
		List<GerritQueryResult> results = null;
		results = client.getRestClient().executeQuery(new NullProgressMonitor(), query);
		return results;
	}

	private List<GerritQueryResult> executeQuery(String query, String option) throws GerritException {
		List<GerritQueryResult> results = null;
		results = client.getRestClient().executeQuery(new NullProgressMonitor(), query, option);
		return results;
	}

	@Test
	public void testExecuteQueryWithoutOption() throws Exception {
		String query = "status:open";
		List<GerritQueryResult> results = executeQuery(query);
		assertNotNull(results);
	}

	@Test
	public void testExecuteQueryWithNullOption() throws Exception {
		String query = "status:open";
		String option = null;
		List<GerritQueryResult> results = executeQuery(query, option);
		assertNotNull(results);
	}

	@Test
	public void testExecuteQueryWithEmptyOption() throws Exception {
		String query = "status:open";
		String option = "";
		List<GerritQueryResult> results = executeQuery(query, option);
		assertNotNull(results);
	}

	@Test
	public void testExecuteQueryWithOption() throws Exception {
		String query = "status:open";
		String option = GET_LABELS_OPTION;
		List<GerritQueryResult> results = executeQuery(query, option);
		assertNotNull(results);
		for (int index = 0; index < results.size(); index++) {
			assertNotNull(results.get(index));
			assertNotNull(results.get(index).getReviewLabel());
		}
	}

	private List<GerritQueryResult> executeQueryRest(String query) throws GerritException {
		return client.getRestClient().executeQuery(new NullProgressMonitor(), query);
	}

	private List<GerritQueryResult> executeQueryRest(String query, String option) throws GerritException {
		return client.getRestClient().executeQuery(new NullProgressMonitor(), query, option);
	}

	@Test
	public void testExecuteQueryRestWithoutOption() throws Exception {
		String query = "status:open";
		List<GerritQueryResult> results = executeQueryRest(query);

		assertNotNull(results);
	}

	@Test
	public void testExecuteQueryRestWithNullOption() throws Exception {
		String query = "status:open";
		String option = null;
		List<GerritQueryResult> results = executeQueryRest(query, option);
		assertNotNull(results);
	}

	@Test
	public void testExecuteQueryRestWithEmptyOption() throws Exception {
		String query = "status:open";
		String option = "";
		List<GerritQueryResult> results = null;
		results = executeQueryRest(query, option);
		assertNotNull(results);
	}

	@Test
	public void testExecuteQueryvWithOption() throws Exception {
		String query = "status:open";
		String option = GET_LABELS_OPTION;
		List<GerritQueryResult> results = executeQuery(query, option);
		assertNotNull(results);
	}

	@Test
	public void testExecuteQueryAllMerged() throws GerritException {
		String query = "status:merged";
		String option = GET_LABELS_OPTION;
		List<GerritQueryResult> results = executeQuery(query, option);
		assertNotNull(results);
	}

	@Test
	public void testExecuteQueryAllAbandoned() throws GerritException {
		String query = "status:abandoned";
		String option = GET_LABELS_OPTION;
		List<GerritQueryResult> results = executeQuery(query, option);
		assertNotNull(results);
	}

	@Test
	public void testExecuteQueryisStarred() throws GerritException {
		String query = "is:starred status:open";
		String option = GET_LABELS_OPTION;
		List<GerritQueryResult> results = executeQuery(query, option);
		assertNotNull(results);
	}

	@Test
	public void testExecuteQueryHasComments() throws GerritException {
		String query = "has:draft";
		List<GerritQueryResult> results = executeQuery(query);
		assertNotNull(results);
	}

	@Test
	public void testExecuteQueryDraftsCommentsReviews() throws GerritException {
		String query = "has:draft";
		List<GerritQueryResult> results = executeQuery(query);
		assertNotNull(results);
	}

	@Test
	public void testExecuteQueryDraftsReviews() throws GerritException {
		String query = "is:draft";
		List<GerritQueryResult> results = executeQuery(query);
		assertNotNull(results);
	}

	@Test
	public void testToReviewId() throws GerritException {
		assertEquals("123", client.toReviewId("123", null));
		assertEquals("1", client.toReviewId("1", null));
	}

	@Test
	public void testToReviewIdWithInvalidId() {
		try {
			client.toReviewId("invalidid", null);
			fail("Expected GerritException");
		} catch (GerritException e) {
			assertEquals("invalidid is not a valid review ID", e.getMessage());

		}
	}

	@Test
	public void testToReviewIdWithChangeId() throws Exception {
		harness.ensureOneReviewExists();
		List<GerritQueryResult> results = executeQuery("status:open");
		GerritQueryResult result = results.get(0);
		String reviewId = Integer.toString(result.getNumber());
		String changeId = GerritUtil.toChangeId(result.getId());
		assertEquals(reviewId, client.toReviewId(changeId, null));
	}

	@Test
	public void testIsZippedContent() throws Exception {
		assertTrue(GerritClient.isZippedContent("PK\u0003\u0004somezippedcontent".getBytes()));
		assertFalse(GerritClient.isZippedContent("PK\u0003notzippedcontent".getBytes()));
		assertFalse(GerritClient.isZippedContent("PKnotzippedcontent".getBytes()));
		assertFalse(GerritClient.isZippedContent("notzippedcontent".getBytes()));
		assertFalse(GerritClient.isZippedContent("PK".getBytes()));
		assertFalse(GerritClient.isZippedContent("".getBytes()));
		assertFalse(GerritClient.isZippedContent(null));
	}

	@Test
	public void testFetchLeftBinaryContent() throws Exception {
		fetchBinaryContentForSide(false);
	}

	@Test
	public void testFetchRightBinaryContent() throws Exception {
		fetchBinaryContentForSide(true);
	}

	private void fetchBinaryContentForSide(boolean rightSide) throws Exception, GerritException {
		Id ps2 = Id.fromRef("refs/changes/34/1234/2");
		Id ps4 = Id.fromRef("refs/changes/34/1234/4");
		Key key = new Key(ps4, "/mylyn/gerrit/File.jpg");
		PatchScriptX script = mock(PatchScriptX.class);

		for (ChangeType type : ChangeType.values()) {
			TestGerritClient client = createSpy();
			doReturn(null).when(client).fetchBinaryContent(any(String.class), any(IProgressMonitor.class));
			when(script.getChangeType()).thenReturn(type);
			VerificationMode fetchBinaryContentExpected = times(1);
			if (rightSide) {
				client.fetchRightBinaryContent(script, key, ps2, new NullProgressMonitor());
				if (type == ChangeType.DELETED) {
					fetchBinaryContentExpected = never();
				}
			} else {
				client.fetchLeftBinaryContent(script, key, ps2, new NullProgressMonitor());
				if (!EnumSet.of(ChangeType.DELETED, ChangeType.MODIFIED).contains(type)) {
					fetchBinaryContentExpected = never();
				}
			}
			verify(client, fetchBinaryContentExpected).fetchBinaryContent(any(String.class),
					any(IProgressMonitor.class));
		}
	}

	@Test
	public void testGetUrlForPatchSet() throws Exception {
		TestGerritClient client = createSpy();
		Id ps2 = Id.fromRef("refs/changes/34/1234/2");
		Id ps4 = Id.fromRef("refs/changes/34/1234/4");
		Key key = new Key(ps4, "/mylyn/gerrit/File.jpg");
		assertEquals(encode("1234,4,/mylyn/gerrit/File.jpg^1"), client.getUrlForPatchSetOrBase(key, null));
		assertEquals(encode("1234,2,/mylyn/gerrit/File.jpg^0"), client.getUrlForPatchSetOrBase(key, ps2));
		assertEquals(encode("1234,4,/mylyn/gerrit/File.jpg^0"), client.getUrlForPatchSetOrBase(key, ps4));
		assertEquals(encode("1234,2,/mylyn/gerrit/File.jpg^0"), client.getUrlForPatchSet(key, ps2));
		assertEquals(encode("1234,4,/mylyn/gerrit/File.jpg^0"), client.getUrlForPatchSet(key, ps4));
	}

	private TestGerritClient createSpy() throws Exception {
		return spy(new TestGerritClient(GerritFixture.current().repository(), GerritFixture.current().location()));
	}

	private String encode(String s) throws UnsupportedEncodingException {
		return URLEncoder.encode(s, "UTF-8");
	}
}
