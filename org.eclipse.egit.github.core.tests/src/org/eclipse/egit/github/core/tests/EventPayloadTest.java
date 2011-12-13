/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Jason Tsay (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Tests for all subclasses of EventPayload
 */
@RunWith(Suite.class)
@SuiteClasses({ CommitCommentPayloadTest.class, CreatePayloadTest.class,
		DeletePayloadTest.class, DownloadPayloadTest.class, FollowPayloadTest.class,
		ForkApplyPayloadTest.class, ForkPayloadTest.class, GistPayloadTest.class,
		GollumPayloadTest.class, IssueCommentPayloadTest.class, IssuesPayloadTest.class,
		MemberPayloadTest.class, PullRequestPayloadTest.class, PushPayloadTest.class,
		TeamAddPayloadTest.class, WatchPayloadTest.class })
public class EventPayloadTest {
	//EventPayload is an empty class, no tests to run.
}
