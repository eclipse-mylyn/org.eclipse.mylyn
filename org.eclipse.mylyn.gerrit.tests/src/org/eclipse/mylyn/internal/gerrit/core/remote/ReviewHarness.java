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

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
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
		this.testIdent = testIdent;
		gerritHarness = GerritFixture.current().harness();
		git = gerritHarness.project().getGitProject();

		connector = new GerritConnector();
		repository = GerritFixture.current().singleRepository();
		client = gerritHarness.client();

		provider = new GerritRemoteFactoryProvider(repository, client);
		provider.setService(new JobRemoteService());
		provider.setDataLocator(new TestDataLocator());
	}

	public void init() throws Exception {
		provider.open();
		pushFileToReview(testIdent);
		listener = new TestRemoteObserver<IRepository, IReview, String, Date>(provider.getReviewFactory());

		consumer = provider.getReviewFactory().getConsumerForRemoteKey(getRepository(), shortId);
		consumer.addObserver(listener);
		consumer.retrieve(false);
		listener.waitForResponse(1, 1);
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

	public void pushFileToReview(String testIdent) throws Exception {
		changeId = "I" + StringUtils.rightPad(testIdent, 40, "a");
		CommitCommand command = git.commit()
				.setAll(true)
				.setMessage("Test Change " + testIdent + "\n\nChange-Id: " + changeId);
		gerritHarness.project().addFile("testFile1.txt");
		CommitResult result = gerritHarness.project().commitAndPush(command);
		shortId = StringUtils.trimToEmpty(StringUtils.substringAfterLast(result.push.getMessages(), "/"));
		commitId = result.commit.getId().toString();
		assertThat("Bad Push: " + result.push.getMessages(), shortId.length(), greaterThan(0));

	}

	void assertIsRecent(Date date) {
		long timeDelta = System.currentTimeMillis() - date.getTime();
		assertThat("Creation delta out of range : " + timeDelta + " ms", timeDelta > -CREATION_TIME_DELTA
				&& timeDelta < CREATION_TIME_DELTA, is(true));
	}

	public void dispose() {
		gerritHarness.dispose();
	}

	IRepository getRepository() {
		return provider.getRoot();
	}
}