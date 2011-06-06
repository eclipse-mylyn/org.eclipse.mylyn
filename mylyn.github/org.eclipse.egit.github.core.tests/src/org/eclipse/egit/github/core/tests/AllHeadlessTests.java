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

import org.eclipse.egit.github.core.tests.CommentTest;
import org.eclipse.egit.github.core.tests.GistRevisionTest;
import org.eclipse.egit.github.core.tests.GistServiceTest;
import org.eclipse.egit.github.core.tests.GistTest;
import org.eclipse.egit.github.core.tests.GitHubClientTest;
import org.eclipse.egit.github.core.tests.IssueServiceTest;
import org.eclipse.egit.github.core.tests.IssueTest;
import org.eclipse.egit.github.core.tests.LabelServiceTest;
import org.eclipse.egit.github.core.tests.MilestoneServiceTest;
import org.eclipse.egit.github.core.tests.MilestoneTest;
import org.eclipse.egit.github.core.tests.PullRequestServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ PullRequestDiscussionTest.class, RepositoryTest.class,
		PullRequestTest.class, CommentTest.class, GistTest.class,
		GistRevisionTest.class, IssueTest.class, MilestoneTest.class,
		GitHubClientTest.class, IssueServiceTest.class, LabelServiceTest.class,
		MilestoneServiceTest.class, GistServiceTest.class,
		PullRequestServiceTest.class, RequestErrorTest.class })
public class AllHeadlessTests {

}
