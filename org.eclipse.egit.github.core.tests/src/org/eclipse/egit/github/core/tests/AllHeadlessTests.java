/*******************************************************************************
 * Copyright (c) 2011 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green <david.green@tasktop.com> - initial contribution
 *     Christian Trutz <christian.trutz@gmail.com> - initial contribution
 *     Chris Aniszczyk <caniszczyk@gmail.com> - initial contribution
 *******************************************************************************/
package org.eclipse.egit.github.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * All headless unit tests of GitHub Java API
 */
@RunWith(Suite.class)
@SuiteClasses({ ApplicationTest.class, //
		AuthorizationTest.class, //
		BlobTest.class, //
		CollaboratorServiceTest.class, //
		CommentTest.class, //
		CommitCommentTest.class, //
		CommitFileTest.class, //
		CommitServiceTest.class, //
		CommitStatsTest.class, //
		CommitStatusTest.class, //
		CommitTest.class, //
		CommitUserTest.class, //
		ContentsServiceTest.class, //
		ContributorTest.class, //
		DataServiceTest.class, //
		DateFormatterTest.class, //
		DateUtilTests.class, //
		DeployKeyServiceTest.class, //
		DownloadServiceTest.class, //
		DownloadTest.class, //
		DownloadResourceTest.class, //
		EncodingUtilsTest.class, //
		EventFormatterTest.class, //
		EventPayloadTest.class, //
		EventRepositoryTest.class, //
		EventServiceTest.class, //
		EventTest.class, //
		FieldErrorTest.class, //
		GistChangeStatusTest.class, //
		GistFileTest.class, //
		GistRevisionTest.class, //
		GistServiceTest.class, //
		GistTest.class, //
		GitHubClientTest.class, //
		GollumPageTest.class, //
		GsonUtilsTest.class, //
		IdTest.class, //
		IssueEventTest.class, //
		IssueServiceTest.class, //
		IssueTest.class, //
		KeyTest.class, //
		LabelComparatorTest.class, //
		LabelServiceTest.class, //
		LabelTest.class, //
		LanguageTest.class, //
		MarkdownServiceTest.class, //
		MergeStatusTest.class, //
		MilestoneComparatorTest.class, //
		MilestoneServiceTest.class, //
		MilestoneTest.class, //
		OAuthServiceTest.class, //
		OrganizationServiceTest.class, //
		PagedRequestTest.class, //
		PullRequestMarkerTest.class, //
		PullRequestServiceTest.class, //
		PullRequestTest.class, //
		ReferenceTest.class, //
		RepositoryBranchTest.class, //
		RepositoryCommitCompareTest.class, //
		RepositoryCommitTest.class, //
		RepositoryContentsTest.class, //
		RepositoryHookResponseTest.class, //
		RepositoryHookTest.class, //
		RepositoryIdTest.class, //
		RepositoryIssueTest.class, //
		RepositoryServiceTest.class, //
		RepositoryTagTest.class, //
		RepositoryTest.class, //
		RequestErrorTest.class, //
		RequestExceptionTest.class, //
		SearchIssueTest.class, //
		SearchRepositoryTest.class, //
		ShaResourceTest.class, //
		TagTest.class, //
		TeamServiceTest.class, //
		TeamTest.class, //
		TreeEntryTest.class, //
		TreeTest.class, //
		TypedResourceTest.class, //
		UrlUtilsTest.class, //
		UserPlanTest.class, //
		UserServiceTest.class, //
		UserTest.class, //
		WatcherServiceTest.class //
})
public class AllHeadlessTests {

}
