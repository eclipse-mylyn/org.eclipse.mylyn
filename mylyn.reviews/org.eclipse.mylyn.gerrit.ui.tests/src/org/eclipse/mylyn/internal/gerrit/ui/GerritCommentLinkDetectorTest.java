/*******************************************************************************
 * Copyright (c) 2012, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.text.Region;
import org.eclipse.mylyn.commons.workbench.browser.UrlHyperlink;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.CommentLink;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
public class GerritCommentLinkDetectorTest {

	TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://review.mylyn.org");

	List<CommentLink> commentLinks = new ArrayList<>();

	private static CommentLink COMMENT_LINK_TASK = new CommentLink("([Tt]ask:\\s+)(\\d+)",
			"$1<a href=\"http://tracker.mylyn.org/$2\">$2</a>");

	private static CommentLink COMMENT_LINK_TASK_INVALID = new CommentLink("([Tt]ask:\\s+)(\\d+)",
			"http://tracker.mylyn.org/$2");

	private static CommentLink COMMENT_LINK_CHANGE = new CommentLink("(I[0-9a-f]{8,40})",
			"<a href=\"#q,$1,n,z\">$&</a>");

	private static CommentLink COMMENT_LINK_BUG = new CommentLink("(bug\\s+)(\\d+)",
			"<a href=\"http://bugs.mylyn.org/show_bug.cgi?id=$2\">$&</a>");

	@Test
	public void testTaskPattern() {
		commentLinks.add(COMMENT_LINK_TASK);
		GerritCommentLinkDetector detector = new GerritCommentLinkDetector(repository, commentLinks);
		UrlHyperlink expected = new UrlHyperlink(new Region(0, 9), "http://tracker.mylyn.org/123");
		assertEquals(String.valueOf(Collections.singletonList(expected)),
				String.valueOf(detector.findHyperlinks("Task: 123", 0, 0)));
	}

	@Test
	public void testLinkChangeId() {
		commentLinks.add(COMMENT_LINK_CHANGE);
		GerritCommentLinkDetector detector = new GerritCommentLinkDetector(repository, commentLinks);
		UrlHyperlink expected = new UrlHyperlink(new Region(0, 41),
				"http://review.mylyn.org/#q,If1d90fa42e1e98f03a68fb59c2b5bedc3d371f0f,n,z");
		assertEquals(String.valueOf(Collections.singletonList(expected)),
				String.valueOf(detector.findHyperlinks("If1d90fa42e1e98f03a68fb59c2b5bedc3d371f0f", 0, 0)));
	}

	@Test
	public void testTaskPatternInvalid() {
		commentLinks.add(COMMENT_LINK_TASK_INVALID);
		GerritCommentLinkDetector detector = new GerritCommentLinkDetector(repository, commentLinks);
		assertEquals("null", String.valueOf(detector.findHyperlinks("Task: 123", 0, 0)));
	}

	@Test
	public void testTaskPatternTwice() {
		commentLinks.add(COMMENT_LINK_TASK);
		GerritCommentLinkDetector detector = new GerritCommentLinkDetector(repository, commentLinks);
		UrlHyperlink expected1 = new UrlHyperlink(new Region(4, 9), "http://tracker.mylyn.org/123");
		UrlHyperlink expected2 = new UrlHyperlink(new Region(18, 7), "http://tracker.mylyn.org/4");
		assertEquals(String.valueOf(Arrays.asList(expected1, expected2)),
				String.valueOf(detector.findHyperlinks("abc Task: 123 and Task: 4", -1, 0)));
	}

	@Test
	public void testTaskPatternNoMatch() {
		commentLinks.add(COMMENT_LINK_TASK);
		GerritCommentLinkDetector detector = new GerritCommentLinkDetector(repository, commentLinks);
		assertEquals("null", String.valueOf(detector.findHyperlinks("Task 123", 0, 0)));
	}

	@Test
	public void testTaskPatternNoMatchBug() {
		commentLinks.add(COMMENT_LINK_TASK);
		GerritCommentLinkDetector detector = new GerritCommentLinkDetector(repository, commentLinks);
		assertEquals("null", String.valueOf(detector.findHyperlinks("Bug 123", 0, 0)));
	}

	@Test
	public void testTaskBugPatternTwice() {
		commentLinks.add(COMMENT_LINK_TASK);
		commentLinks.add(COMMENT_LINK_BUG);
		GerritCommentLinkDetector detector = new GerritCommentLinkDetector(repository, commentLinks);
		UrlHyperlink expected1 = new UrlHyperlink(new Region(4, 9), "http://tracker.mylyn.org/123");
		UrlHyperlink expected2 = new UrlHyperlink(new Region(18, 5), "http://bugs.mylyn.org/show_bug.cgi?id=4");
		assertEquals(String.valueOf(Arrays.asList(expected1, expected2)),
				String.valueOf(detector.findHyperlinks("abc Task: 123 and bug 4", -1, 0)));
	}

	@Test
	public void testNoPattern() {
		GerritCommentLinkDetector detector = new GerritCommentLinkDetector(repository, (List<CommentLink>) null);
		assertEquals("null", String.valueOf(detector.findHyperlinks("Bug 123", 0, 0)));
	}

}
