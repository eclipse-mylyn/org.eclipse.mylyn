/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Jason Tsay (GitHub Inc.) - initial API and implementation
 *    See git history
 *******************************************************************************/
package org.eclipse.egit.github.core.tests;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Tests for all subclasses of EventPayload
 */
@Suite
@SelectClasses({ CommitCommentPayloadTest.class, //
	CreatePayloadTest.class, //
	DeletePayloadTest.class, //
	DownloadPayloadTest.class, //
	FollowPayloadTest.class, //
	ForkApplyPayloadTest.class, //
	ForkPayloadTest.class, //
	GistPayloadTest.class, //
	GollumPayloadTest.class, //
	IssueCommentPayloadTest.class, //
	IssuesPayloadTest.class, //
	MemberPayloadTest.class, //
	PullRequestPayloadTest.class, //
	PullRequestReviewCommentPayloadTest.class, //
	PushPayloadTest.class, //
	TeamAddPayloadTest.class, //
	WatchPayloadTest.class })
public class EventPayloadTest {
	// EventPayload is an empty class, no tests to run.
}
