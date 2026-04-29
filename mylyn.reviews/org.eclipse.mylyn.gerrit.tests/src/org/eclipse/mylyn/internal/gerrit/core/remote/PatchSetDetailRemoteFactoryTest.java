/*******************************************************************************
 * Copyright (c) 2014, 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.gerrit.core.remote;

import static org.eclipse.mylyn.internal.gerrit.core.remote.TestRemoteObserverConsumer.retrieveForLocalKey;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.gerrit.tests.AbstractGerritFixtureTest;
import org.eclipse.mylyn.gerrit.tests.support.GerritFixture;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ChangeDetailX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.PatchSetPublishDetailX;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.PatchSet;

@SuppressWarnings("nls")
@Disabled("No gerrit instance available")
public class PatchSetDetailRemoteFactoryTest extends AbstractGerritFixtureTest {
	@BeforeEach
	void skipIfExcluded() {
		assumeFalse(fixture.isExcluded(), "Fixture is excluded");
	}

	private static final String NON_DRAFT_BRANCH = "HEAD:refs/for/master";

	private static final String DRAFT_BRANCH = "HEAD:refs/drafts/master";

	private ReviewHarness reviewHarness;

	@BeforeEach
	void setUp() throws Exception {
		// sets who is signed-in to view the review (performs the retrieval)
		reviewHarness = new ReviewHarness();
		// set who makes the initial commit (and consequentially, becomes the review owner)
		reviewHarness.init(DRAFT_BRANCH, PrivilegeLevel.ADMIN, "testFile1.txt", false);
	}

	@AfterEach
	void tearDown() throws Exception {
		reviewHarness.dispose();
	}

	@Test
	public void testUserHasNoAccessToAdminDraft() throws Exception {
		createPatchSet(NON_DRAFT_BRANCH, PrivilegeLevel.ADMIN, List.of("testFile2.txt", "testFile3.txt"));

		reviewHarness.retrieve();
		assertThat(reviewHarness.getReview().getSets().size(), is(1));
		assertThat(reviewHarness.getReview().getSets().get(0).getId(), is("2"));

		assertNull(retrievePatchSetDetail("1"));
		PatchSetDetail detail = retrievePatchSetDetail("2");
		assertThat(detail.getInfo().getKey().get(), is(2));
	}

	@Test
	public void testUserHasAccessToAdminDraft() throws Exception {
		createPatchSet(NON_DRAFT_BRANCH, PrivilegeLevel.ADMIN, List.of("testFile2.txt", "testFile3.txt"));
		reviewHarness.getClient()
		.addReviewers(reviewHarness.getShortId(),
				List.of(GerritFixture.current().getCredentials(PrivilegeLevel.USER).getUserName()),
				new NullProgressMonitor());

		reviewHarness.retrieve();
		assertThat(reviewHarness.getReview().getSets().size(), is(2));
		assertThat(reviewHarness.getReview().getSets().get(0).getId(), is("1"));
		assertThat(reviewHarness.getReview().getSets().get(1).getId(), is("2"));

		PatchSetDetail detail = retrievePatchSetDetail("1");
		assertThat(detail.getInfo().getKey().get(), is(1));
		detail = retrievePatchSetDetail("2");
		assertThat(detail.getInfo().getKey().get(), is(2));

	}

	@Test
	public void testGetPatchSetPublishDetailOfDraftIffAdmin() throws Exception {
		int reviewId = Integer.parseInt(reviewHarness.getShortId());
		PatchSet.Id id = new PatchSet.Id(new Change.Id(reviewId), 1);
		assertThrows(GerritException.class,
				() -> reviewHarness.getClient()
				.getPatchSetPublishDetail(id, new NullProgressMonitor()));

		// Needs admin client to view admin-created draft
		PatchSetPublishDetailX patchSetDetail;

		patchSetDetail = reviewHarness.getAdminClient().getPatchSetPublishDetail(id, new NullProgressMonitor());

		assertThat(patchSetDetail, notNullValue());
		// DRAFT is not correctly parsed for ChangeInfo since Change.Status does not define the corresponding enum field
		assertThat(patchSetDetail.getChange().getStatus(), is(Change.Status.NEW));
	}

	@Test
	public void testGetChangeDetailOfDraftIffAdmin() throws Exception {
		int reviewId = Integer.parseInt(reviewHarness.getShortId());
		ChangeDetailX changeDetail;
		assertThrows(GerritException.class,
				() -> reviewHarness.getClient().getChangeDetail(reviewId, new NullProgressMonitor()));
		fail("Expected Gerrit Exception");

		// Needs admin client to view admin-created draft
		changeDetail = reviewHarness.getAdminClient().getChangeDetail(reviewId, new NullProgressMonitor());

		assertThat(changeDetail, notNullValue());
		// DRAFT is not correctly parsed for ChangeInfo since Change.Status does not define the corresponding enum field
		assertThat(changeDetail.getChange().getStatus(), is(Change.Status.NEW));
	}

	private void createPatchSet(String pushTo, PrivilegeLevel privilegeLevel, List<String> files) throws Exception {
		CommitCommand command = reviewHarness.createCommitCommand();
		for (String fileName : files) {
			reviewHarness.addFile(fileName);
		}
		reviewHarness.commitAndPush(command, pushTo, privilegeLevel);
	}

	private PatchSetDetail retrievePatchSetDetail(String patchSetId) {
		TestRemoteObserverConsumer<IReview, IReviewItemSet, String, PatchSetDetail, PatchSetDetail, String> itemSetObserver //
		= retrieveForLocalKey(reviewHarness.getProvider().getReviewItemSetFactory(), reviewHarness.getReview(),
				patchSetId, false);
		PatchSetDetail detail = itemSetObserver.getRemoteObject();
		return detail;
	}
}
