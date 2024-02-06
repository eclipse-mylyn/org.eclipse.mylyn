/*******************************************************************************
 * Copyright (c) 2015 Vaughan Hilts and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Vaughan Hilts, Kyle Ross - Initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core;

import java.util.List;

import org.eclipse.mylyn.reviews.internal.core.BuildResult;
import org.eclipse.mylyn.reviews.internal.core.BuildResult.BuildStatus;
import org.junit.Assert;
import org.junit.Test;

/**
 * Performs tests against the HudsonCommentParser with various different comment sets.
 */
@SuppressWarnings({ "nls", "restriction" })
public class HudsonCommentParserTest {

	private final HudsonCommentParser parser = new HudsonCommentParser();

	@Test
	public void canParseBuildStartedMessage() {
		String message = "Patch Set 1:\r\nBuild Started https://hudson.eclipse.org/mylyn/job/gerrit-mylyn-reviews/1017/";
		List<BuildResult> results = parser.getBuildResult(message);
		Assert.assertEquals(1, results.size());

		BuildResult result = results.get(0);
		Assert.assertEquals(1, result.getPatchSetNumber());
		Assert.assertEquals(1017, result.getBuildNumber());
		Assert.assertEquals("https://hudson.eclipse.org/mylyn/job/gerrit-mylyn-reviews/1017/", result.getBuildUrl());
		Assert.assertEquals("https://hudson.eclipse.org/mylyn/job/gerrit-mylyn-reviews", result.getJobName());
		Assert.assertEquals(BuildStatus.STARTED, result.getBuildStatus());
	}

	@Test
	public void canParseBuildGenericMessage() {
		String message = "Patch Set 4: Verified+1\r\n\r\nBuild Successful\r\n\r\nhttps://hudson.eclipse.org/mylyn/job/gerrit-mylyn-reviews/1023/ : SUCCESS";
		List<BuildResult> results = parser.getBuildResult(message);
		Assert.assertEquals(1, results.size());

		BuildResult result = results.get(0);

		Assert.assertEquals(4, result.getPatchSetNumber());
		Assert.assertEquals(1023, result.getBuildNumber());
		Assert.assertEquals("https://hudson.eclipse.org/mylyn/job/gerrit-mylyn-reviews/1023/", result.getBuildUrl());
		Assert.assertEquals("https://hudson.eclipse.org/mylyn/job/gerrit-mylyn-reviews", result.getJobName());
		Assert.assertEquals(BuildStatus.SUCCESS, result.getBuildStatus());

		String failedMessage = "Patch Set 9: Verified -1\r\n\r\nBuild failure\r\n\r\nhttps://hudson.eclipse.org/mylyn/job/gerrit-mylyn-reviews/1023/ : ABORTED";
		List<BuildResult> failedResults = parser.getBuildResult(failedMessage);
		Assert.assertEquals(1, failedResults.size());
		BuildResult failedResult = failedResults.get(0);
		Assert.assertEquals(BuildStatus.ABORTED, failedResult.getBuildStatus());
	}

	@Test
	public void doesNotParseQuotedHudsonComment() {
		String messageSpacesAfter = "> Patch Set 4: Verified+1\r\n> \r\n> Build Successful\r\n> \r\n> https://hudson.eclipse.org/mylyn/job/gerrit-mylyn-reviews/1023/ : SUCCESS";
		String messageSpacesBeforeAndAfter = " > Patch Set 4: Verified+1\r\n > \r\n > Build Successful\r\n > \r\n > https://hudson.eclipse.org/mylyn/job/gerrit-mylyn-reviews/1023/ : SUCCESS";
		List<BuildResult> results = parser.getBuildResult(messageSpacesAfter);
		Assert.assertEquals(0, results.size());
		List<BuildResult> resultsForDoubleSpacing = parser.getBuildResult(messageSpacesBeforeAndAfter);
		Assert.assertEquals(0, resultsForDoubleSpacing.size());
	}

	@Test
	public void canParseHudson3BuildMessages() {
		String newMessage = "Patch Set 9: Verified -1\r\n\r\nBuild successful\r\n\r\nhttps://hudson.eclipse.org/mylyn/job/gerrit-mylyn-reviews/1023/ : {}";
		for (BuildStatus status : BuildStatus.values()) {
			String message = newMessage.replace("{}", status.toString());
			List<BuildResult> results = parser.getBuildResult(message);
			Assert.assertEquals(1, results.size());
			BuildResult result = results.get(0);
			Assert.assertEquals(status, result.getBuildStatus());
		}
	}

	@Test
	public void canParseWithIntegerInUrl() {
		String message = "Patch Set 4: Verified+1\r\n\r\nBuild Successful\r\n\r\nhttps://hudson2.eclipse.org/mylyn/job/gerrit-mylyn-reviews/1023/ : SUCCESS";
		List<BuildResult> results = parser.getBuildResult(message);
		Assert.assertEquals(1, results.size());
		BuildResult result = results.get(0);

		Assert.assertNotNull(result);
		Assert.assertEquals(4, result.getPatchSetNumber());
		Assert.assertEquals(1023, result.getBuildNumber());
	}

	@Test
	public void canParseWithIntegersInJobUrl() {
		String message = "Patch Set 5: Verified+1\r\n\r\nBuild Successful\r\n\r\nhttps://hudson2.eclipse.org/2mylyn/job/2gerrit-mylyn-2reviews/1023/ : SUCCESS";
		List<BuildResult> results = parser.getBuildResult(message);
		Assert.assertEquals(1, results.size());
		BuildResult result = results.get(0);

		Assert.assertNotNull(result);
		Assert.assertEquals(5, result.getPatchSetNumber());
		Assert.assertEquals(1023, result.getBuildNumber());
	}

	@Test
	public void missingPatchSetNumberShouldReturnEmpty() {
		String message = "Patch Set :\r\nBuild Started https://hudson.eclipse.org/mylyn/job/gerrit-mylyn-reviews/1017/";
		List<BuildResult> results = parser.getBuildResult(message);
		Assert.assertEquals(0, results.size());
	}

	@Test
	public void nonHudsonCommentShouldReturnEmpty() {
		String message = "Uploaded patch set 2.";
		List<BuildResult> results = parser.getBuildResult(message);
		Assert.assertEquals(0, results.size());
	}

	@Test
	public void missingBuildNumberShouldReturnEmpty() {
		String message = "Patch Set 1:\r\nBuild Started https://hudson.eclipse.org/mylyn/job/gerrit-mylyn-reviews/";
		List<BuildResult> results = parser.getBuildResult(message);
		Assert.assertEquals(0, results.size());
	}

	@Test
	public void missingBuildStatusShouldReturnEmpty() {
		String message = "Patch Set 4: Verified+1\r\n\r\nBuild Successful\r\n\r\nhttps://hudson.eclipse.org/mylyn/job/gerrit-mylyn-reviews/1023/ :";
		String message2 = "Patch Set 4: Verified+1\r\n\r\nBuild Successful\r\n\r\nhttps://hudson.eclipse.org/mylyn/job/gerrit-mylyn-reviews/1023/";
		String message3 = "Patch Set 4:\r\nStarted https://hudson.eclipse.org/mylyn/job/gerrit-mylyn-reviews/1017/";

		List<BuildResult> result = parser.getBuildResult(message);
		List<BuildResult> result2 = parser.getBuildResult(message2);
		List<BuildResult> result3 = parser.getBuildResult(message3);

		Assert.assertEquals(0, result.size());
		Assert.assertEquals(0, result2.size());
		Assert.assertEquals(0, result3.size());
	}

	@Test
	public void missingUrlShouldReturnEmpty() {
		String message = "Patch Set 1:\r\nBuild Started";
		List<BuildResult> results = parser.getBuildResult(message);
		Assert.assertEquals(0, results.size());
	}

	@Test
	public void emptyCommentShouldReturnEmpty() {
		String message = "";
		List<BuildResult> results = parser.getBuildResult(message);
		Assert.assertEquals(0, results.size());
	}

	@Test
	public void multipleJobNamesInCommentAreParsedCorrectly() {
		/*
			Hudson CI
			Patch Set 1: Verified+1
			Build Successful
			https://hudson.eclipse.org/sirius/job/sirius.gerrit.tests-mars/2434/ : SUCCESS
			https://hudson.eclipse.org/sirius/job/sirius.gerrit.build-mars/2584/ : SUCCESS
		 */
		String multipleJobMessage = "Patch Set 1: Verified+1\r\nBuild Successful \r\nhttps://hudson.eclipse.org/sirius/job/sirius.gerrit.tests-mars/2434/ : SUCCESS\r\nhttps://hudson.eclipse.org/sirius/job/sirius.gerrit.build-mars/2584/ : SUCCESS";
		List<BuildResult> results = parser.getBuildResult(multipleJobMessage);
		Assert.assertEquals(2, results.size());

		BuildResult result1 = results.get(0);
		BuildResult result2 = results.get(1);

		Assert.assertEquals(1, result1.getPatchSetNumber());
		Assert.assertEquals(2434, result1.getBuildNumber());
		Assert.assertEquals("https://hudson.eclipse.org/sirius/job/sirius.gerrit.tests-mars/2434/",
				result1.getBuildUrl());
		Assert.assertEquals("https://hudson.eclipse.org/sirius/job/sirius.gerrit.tests-mars", result1.getJobName());
		Assert.assertEquals(BuildStatus.SUCCESS, result1.getBuildStatus());

		Assert.assertEquals(1, result2.getPatchSetNumber());
		Assert.assertEquals(2584, result2.getBuildNumber());
		Assert.assertEquals("https://hudson.eclipse.org/sirius/job/sirius.gerrit.build-mars/2584/",
				result2.getBuildUrl());
		Assert.assertEquals("https://hudson.eclipse.org/sirius/job/sirius.gerrit.build-mars", result2.getJobName());
		Assert.assertEquals(BuildStatus.SUCCESS, result2.getBuildStatus());
	}

	@Test
	public void multipleJobNamesWithBadDataIgnoresBadResults() {
		String multipleJobMessage = "Patch Set 1: Verified+1\r\nBuild Successful \r\nhttps://hudson.eclipse.org/sirius/job/sirius.gerrit.tests-mars/2434/ : SUCCESS\r\nhttps://hudson.eclipse.org/sirius/job/sirius.gerrit.build-mars";
		List<BuildResult> results = parser.getBuildResult(multipleJobMessage);
		Assert.assertEquals(1, results.size());
		BuildResult result1 = results.get(0);

		Assert.assertEquals(1, result1.getPatchSetNumber());
		Assert.assertEquals(2434, result1.getBuildNumber());
		Assert.assertEquals("https://hudson.eclipse.org/sirius/job/sirius.gerrit.tests-mars/2434/",
				result1.getBuildUrl());
		Assert.assertEquals("https://hudson.eclipse.org/sirius/job/sirius.gerrit.tests-mars", result1.getJobName());
		Assert.assertEquals(BuildStatus.SUCCESS, result1.getBuildStatus());

	}

}
