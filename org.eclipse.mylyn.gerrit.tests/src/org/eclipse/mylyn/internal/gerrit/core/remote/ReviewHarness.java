/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.remote;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.gerrit.tests.support.GerritFixture;
import org.eclipse.mylyn.gerrit.tests.support.GerritHarness;
import org.eclipse.mylyn.gerrit.tests.support.GerritProject.CommitResult;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritChange;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.spi.remote.JobRemoteService;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.tasks.core.TaskRepository;

class ReviewHarness {

	//The maximum difference between two dates to account for clock skew between test machines
	static final long CREATION_TIME_DELTA = 30 * 60 * 1000; //30 Minutes

	TestRemoteObserver<IRepository, IReview, String, Date> listener;

	RemoteEmfConsumer<IRepository, IReview, String, GerritChange, String, Date> consumer;

	String shortId;

	String commitId;

	String changeId;

	String testIdent;

	Git git;

	GerritClient client;

	GerritRemoteFactoryProvider provider;

	GerritConnector connector;

	TaskRepository repository;

	GerritHarness gerritHarness;

	ReviewHarness(String testIdent) throws Exception {
		this(testIdent, PrivilegeLevel.USER);
	}

	ReviewHarness(String testIdent, PrivilegeLevel privilegeLevel) throws Exception {
		this.testIdent = testIdent;
		gerritHarness = GerritFixture.current().harness();
		git = gerritHarness.project().getGitProject(privilegeLevel);

		connector = new GerritConnector();
		repository = GerritFixture.current().singleRepository();
		client = gerritHarness.client(privilegeLevel);

		provider = new GerritRemoteFactoryProvider(client);
		provider.setService(new JobRemoteService());
		provider.setDataLocator(new TestDataLocator());
	}

	public void init() throws Exception {
		init("HEAD:refs/for/master", PrivilegeLevel.USER);
	}

	public void init(String refSpec, PrivilegeLevel privilegeLevel) throws Exception {
		provider.open();
		pushFileToReview(testIdent, refSpec, privilegeLevel);
		listener = new TestRemoteObserver<IRepository, IReview, String, Date>(provider.getReviewFactory());

		consumer = provider.getReviewFactory().getConsumerForRemoteKey(getRepository(), shortId);
		consumer.addObserver(listener);
		consumer.retrieve(false);
		listener.waitForResponse();
		assertThat(getRepository().getReviews().size(), is(1));
		IReview review = getRepository().getReviews().get(0);
		IReview reviewDirect = provider.open(shortId);
		assertThat(review, sameInstance(reviewDirect));
		assertThat(review, notNullValue());
		assertThat(review.getId(), is(shortId));
		assertThat(review.getKey(), is(changeId));
		assertThat(review.getSubject(), is("Test Change " + testIdent));
		assertThat(review.getMessage(), allOf(startsWith("Test Change"), endsWith("aaa")));
		assertThat(review.getOwner().getDisplayName(), is("tests"));
		assertIsRecent(review.getCreationDate());
	}

	public void pushFileToReview(String testIdent, String refSpec, PrivilegeLevel privilegeLevel) throws Exception {
		changeId = "I" + StringUtils.rightPad(testIdent, 40, "a");
		CommitCommand command = createCommitCommand(changeId);
		addFile("testFile1.txt");
		CommitResult result = commitAndPush(command, refSpec, privilegeLevel);
		shortId = StringUtils.trimToEmpty(StringUtils.substringAfterLast(result.push.getMessages(), "/"));
		shortId = StringUtils.removeEnd(shortId, " [DRAFT]");
		commitId = result.commit.getId().toString();
		assertThat("Bad Push: " + result.push.getMessages(), shortId.length(), greaterThan(0));

	}

	void assertIsRecent(Date date) {
		long timeDelta = System.currentTimeMillis() - date.getTime();
		assertThat("Creation delta out of range : " + timeDelta + " ms", timeDelta > -CREATION_TIME_DELTA
				&& timeDelta < CREATION_TIME_DELTA, is(true));
	}

	public void dispose() {
		if (listener != null) {
			consumer.removeObserver(listener);
		}
		if (consumer != null) {
			consumer.release();
		}
		gerritHarness.dispose();
	}

	IRepository getRepository() {
		return provider.getRoot();
	}

	public CommitCommand createCommitCommand() {
		return createCommitCommand(changeId).setAmend(true);
	}

	public CommitCommand createCommitCommand(String changeId) {
		return git.commit().setAll(true).setMessage("Test Change " + testIdent + "\n\nChange-Id: " + changeId);
	}

	public void addFile(String fileName) throws Exception {
		gerritHarness.project().addFile(fileName);
	}

	public void addFile(String fileName, String text) throws Exception {
		gerritHarness.project().addFile(fileName, text);
	}

	public void addFile(String fileName, File file) throws Exception {
		gerritHarness.project().addFile(fileName, file);
	}

	public void removeFile(String fileName) throws Exception {
		gerritHarness.project().removeFile(fileName);
	}

	public CommitResult commitAndPush(CommitCommand command) throws Exception {
		return gerritHarness.project().commitAndPush(command);
	}

	public CommitResult commitAndPush(CommitCommand command, String refSpec, PrivilegeLevel privilegeLevel)
			throws Exception {
		return gerritHarness.project().commitAndPush(command, refSpec, privilegeLevel);
	}
}