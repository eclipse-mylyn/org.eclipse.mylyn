/*******************************************************************************
 * Copyright (c) 2013, 2016 Tasktop Technologies and others.
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.gerrit.tests.support.GerritFixture;
import org.eclipse.mylyn.gerrit.tests.support.GerritHarness;
import org.eclipse.mylyn.gerrit.tests.support.GerritProject.CommitResult;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritChange;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.spi.remote.JobRemoteService;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.tasks.core.TaskRepository;

@SuppressWarnings("nls")
class ReviewHarness {

	private static final Pattern SHORT_ID_PATTERN = Pattern.compile("(\\d+).*", Pattern.DOTALL);

	//The maximum difference between two dates to account for clock skew between test machines
	private static final long CREATION_TIME_DELTA = 30 * 60 * 1000; //30 Minutes

	private static final String DEFAULT_TEST_FILE = "testFile1.txt";

	private TestRemoteObserver<IRepository, IReview, String, Date> listener;

	private RemoteEmfConsumer<IRepository, IReview, String, GerritChange, String, Date> consumer;

	private String shortId;

	private String commitId;

	private String changeId;

	private final Git git;

	private final GerritClient client;

	private GerritClient adminClient;

	private final GerritRemoteFactoryProvider provider;

	private final TaskRepository taskRepository;

	private final GerritHarness gerritHarness;

	ReviewHarness() throws Exception {
		changeId = generateChangeId();
		gerritHarness = GerritFixture.current().harness();
		git = gerritHarness.project().getGitProject(PrivilegeLevel.USER);

		taskRepository = GerritFixture.current().singleRepository();
		client = gerritHarness.client(PrivilegeLevel.USER);

		provider = new GerritRemoteFactoryProvider(client);
		provider.setService(new JobRemoteService());
		provider.setDataLocator(new TestDataLocator());
	}

	static String generateChangeId() throws NoSuchAlgorithmException {
		byte[] bytes = new byte[22];
		new Random().nextBytes(bytes);
		return "I" + new BigInteger(bytes).toString(16).replace("-", "").substring(0, 40);
	}

	ReviewHarness duplicate() throws Exception {
		ReviewHarness reviewHarness = new ReviewHarness();
		reviewHarness.changeId = changeId;
		return reviewHarness;
	}

	void retrieve() {
		consumer.retrieve(false);
		listener.waitForResponse();
	}

	void init() throws Exception {
		init("HEAD:refs/for/master", PrivilegeLevel.USER, DEFAULT_TEST_FILE, true);
	}

	void init(String refSpec, PrivilegeLevel privilegeLevel, String fileName, boolean wait) throws Exception {
		provider.open();
		assertThat(getRepository().getReviews().size(), is(0));
		pushFileToReview(refSpec, privilegeLevel, fileName);
		listener = new TestRemoteObserver<>(provider.getReviewFactory());

		consumer = provider.getReviewFactory().getConsumerForRemoteKey(getRepository(), getShortId());
		consumer.setAsynchronous(false);
		consumer.addObserver(listener);
		if (!wait) {
			return;
		}
		retrieve();
		assertThat(getRepository().getReviews().size(), is(1));
		IReview review = getRepository().getReviews().get(0);
		IReview reviewDirect = provider.open(getShortId());
		assertThat(review, sameInstance(reviewDirect));
		assertThat(review, notNullValue());
		assertThat(review.getId(), is(getShortId()));
		assertThat(review.getKey(), is(changeId));
		assertThat(review.getSubject(), is("Test Change " + changeId));
		assertThat(review.getOwner().getDisplayName(), is("tests"));
		assertIsRecent(review.getCreationDate());
	}

	public void pushFileToReview(String refSpec, PrivilegeLevel privilegeLevel) throws Exception {
		pushFileToReview(refSpec, privilegeLevel, DEFAULT_TEST_FILE);
	}

	public void pushFileToReview(String refSpec, PrivilegeLevel privilegeLevel, String fileName) throws Exception {
		CommitCommand command = createCommitCommand(changeId);
		addFile(fileName);
		CommitResult result = commitAndPush(command, refSpec, privilegeLevel);
		shortId = parseShortId(result.push.getMessages());
		commitId = result.commit.getId().toString();
		assertThat("Bad Push: " + result.push.getMessages(), getShortId().length(), greaterThan(0));
	}

	static String parseShortId(String messages) {
		String tail = StringUtils.trimToEmpty(StringUtils.substringAfterLast(messages, "/"));
		Matcher m = SHORT_ID_PATTERN.matcher(tail);
		if (m.matches()) {
			return m.group(1);
		}
		fail("ShortId could not be parsed from \"" + tail + "\". Messages was: " + messages);
		return null;
	}

	void assertIsRecent(Date date) {
		long timeDelta = System.currentTimeMillis() - date.getTime();
		assertThat("Creation delta out of range : " + timeDelta + " ms",
				timeDelta > -CREATION_TIME_DELTA && timeDelta < CREATION_TIME_DELTA, is(true));
	}

	public void dispose() throws GerritException {
		if (listener != null) {
			consumer.removeObserver(listener);
		}
		if (consumer != null) {
			consumer.release();
		}
		gerritHarness.dispose();
	}

	CommitCommand createCommitCommand() {
		return createCommitCommand(changeId).setAmend(true);
	}

	CommitCommand createCommitCommand(String changeId) {
		return git.commit().setAll(true).setMessage("Test Change " + changeId + "\n\nChange-Id: " + changeId);
	}

	void addFile(String fileName) throws Exception {
		gerritHarness.project().addFile(fileName);
	}

	void addFile(String fileName, String text) throws Exception {
		gerritHarness.project().addFile(fileName, text);
	}

	void addFile(String fileName, File file) throws Exception {
		gerritHarness.project().addFile(fileName, file);
	}

	void removeFile(String fileName) throws Exception {
		gerritHarness.project().removeFile(fileName);
	}

	CommitResult commitAndPush(CommitCommand command) throws Exception {
		return gerritHarness.project().commitAndPush(command);
	}

	CommitResult commitAndPush(CommitCommand command, String refSpec, PrivilegeLevel privilegeLevel) throws Exception {
		return gerritHarness.project().commitAndPush(command, refSpec, privilegeLevel);
	}

	void checkoutPatchSet(int number) throws Exception {
		IReviewItemSet patchSet = getReview().getSets().get(0);
		ObjectId ref = git.getRepository().resolve(patchSet.getRevision());
		RevCommit targetCommit = parseCommit(ref);

		//make sure to checkout the correct commit
		assertThat(targetCommit.toString(), is(commitId));

		git.checkout()
		.setCreateBranch(true)
		.setName("change" + "/" + getReview().getId() + "/" + number)
		.setStartPoint(targetCommit)
		.call();
	}

	private RevCommit parseCommit(ObjectId ref) throws IOException {
		try (RevWalk walker = new RevWalk(git.getRepository())) {
			return walker.parseCommit(ref);
		}
	}

	GerritClient getClient() {
		return client;
	}

	GerritClient getAdminClient() {
		if (adminClient == null) {
			UserCredentials credentials = GerritFixture.current().getCredentials(PrivilegeLevel.ADMIN);

			WebLocation location = new WebLocation(GerritFixture.current().getRepositoryUrl(),
					credentials.getUserName(), credentials.getPassword(), (host, proxyType) -> WebUtil.getProxyForUrl(GerritFixture.current().getRepositoryUrl()));

			TaskRepository repository = TasksUiPlugin.getRepositoryManager()
					.getRepository(GerritFixture.current().getRepositoryUrl());
			adminClient = GerritClient.create(repository, location);
		}
		return adminClient;
	}

	IReview getReview() {
		return consumer.getModelObject();
	}

	IRepository getRepository() {
		return provider.getRoot();
	}

	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	String getShortId() {
		return shortId;
	}

	GerritRemoteFactoryProvider getProvider() {
		return provider;
	}
}
