/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Christian Trutz - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.github.internal;

import java.io.IOException;

import org.eclipse.core.runtime.AssertionFailedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link IssueService}
 */
@SuppressWarnings("restriction")
@RunWith(MockitoJUnitRunner.class)
public class IssueServiceTest {

	@Mock
	private GitHubClient gitHubClient;

	private IssueService issueService;

	@Before
	public void before() {
		issueService = new IssueService(gitHubClient);
	}

	@Test(expected = AssertionFailedException.class)
	public void constructor_NullArgument() {
		new IssueService(null);
	}

	@Test(expected = AssertionFailedException.class)
	public void getIssue_NullUser() throws IOException{
		issueService.getIssue(null, "not null", "not null");
	}

	@Test(expected = AssertionFailedException.class)
	public void getIssue_NullRepository() throws IOException{
		issueService.getIssue("not null", null, "not null");
	}

	@Test(expected = AssertionFailedException.class)
	public void getIssue_NullId() throws IOException{
		issueService.getIssue("not null", "not null", null);
	}

}
