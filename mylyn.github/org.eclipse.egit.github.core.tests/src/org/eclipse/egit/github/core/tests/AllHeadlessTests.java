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
@SuiteClasses({ BlobTest.class, CommentTest.class, CommitCommentTest.class,
		CommitFileTest.class, CommitTest.class, CommitUserTest.class,
		DateFormatterTest.class, DateUtilTests.class, DownloadTest.class,
		DownloadResourceTest.class, EncodingUtilsTest.class,
		FieldErrorTest.class, GistChangeStatusTest.class, GistFileTest.class,
		GistRevisionTest.class, GistServiceTest.class, GistTest.class,
		GitHubClientTest.class, IdTest.class, IssueServiceTest.class,
		IssueTest.class, KeyTest.class, LabelComparatorTest.class,
		LabelServiceTest.class, LabelTest.class, LanguageTest.class,
		MergeStatusTest.class, MilestoneComparatorTest.class,
		MilestoneServiceTest.class, MilestoneTest.class,
		PagedRequestTest.class, PullRequestMarkerTest.class,
		PullRequestServiceTest.class, PullRequestTest.class,
		ReferenceTest.class, RepositoryCommitTest.class,
		RepositoryIdTest.class, RepositoryTest.class, RequestErrorTest.class,
		SearchRepositoryTest.class, ShaResourceTest.class, TagTest.class,
		TeamTest.class, TreeEntryTest.class, TreeTest.class,
		TypedResourceTest.class, UrlUtilsTest.class, UserPlanTest.class,
		UserTest.class })
public class AllHeadlessTests {

}
