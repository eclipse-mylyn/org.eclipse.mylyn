/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryTextViewer;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskHyperlinkDetector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Rob Elves
 */
public class BugzillaTaskHyperlinkDetectorTest extends TestCase {

	private static final String DUPLICATE_NUMBER = "112233";

	private static final String DUPLICATE = "duplicate of " + DUPLICATE_NUMBER;

	private String TASK_FORMAT_1 = "task#1";

	private String TASK_FORMAT_2 = "task# 1";

	private String TASK_FORMAT_3 = "task1";

	private String TASK_FORMAT_4 = "task #1";

	private String BUG_FORMAT_1 = "bug# 1";

	private String BUG_FORMAT_2 = "bug # 1";

	private String BUG_FORMAT_3 = "bug1";

	private String BUG_FORMAT_4 = "bug #1";

	private String BUG_FORMAT_1_2 = "bug# 2";

	//private BugzillaTaskHyperlinkDetector detector = new BugzillaTaskHyperlinkDetector();
	private TaskHyperlinkDetector detector = new TaskHyperlinkDetector();

	private TaskRepository dummyRepository = new TaskRepository(BugzillaCorePlugin.REPOSITORY_KIND, "repository_url");

	private RepositoryTextViewer viewer = new RepositoryTextViewer(dummyRepository, new Shell(), SWT.NONE);

	private String[] formats = { TASK_FORMAT_1, TASK_FORMAT_2, TASK_FORMAT_3, TASK_FORMAT_4, BUG_FORMAT_1,
			BUG_FORMAT_2, BUG_FORMAT_3, BUG_FORMAT_4 };

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testBeginning() {
		for (String format : formats) {
			String testString = format + " is at the beginning";
			viewer.setDocument(new Document(testString));
			Region region = new Region(0, testString.length());
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertEquals(testString.indexOf(format), links[0].getHyperlinkRegion().getOffset());
		}
	}

	public void testEnd() {
		for (String format : formats) {
			String testString = "is ends with " + format;
			viewer.setDocument(new Document(testString));
			Region region = new Region(testString.indexOf(format), testString.length());
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertEquals(testString.indexOf(format), links[0].getHyperlinkRegion().getOffset());
		}
	}

	public void testMiddle() {
		for (String format : formats) {
			String testString = "is a " + format + " in the middle";
			viewer.setDocument(new Document(testString));
			Region region = new Region(testString.indexOf(format), testString.length());
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertEquals(testString.indexOf(format), links[0].getHyperlinkRegion().getOffset());
		}
	}

	public void testTwoOnSingleLine() {
		String testString = "is a " + BUG_FORMAT_1 + " in the middle and at the end " + BUG_FORMAT_1_2;
		viewer.setDocument(new Document(testString));
		Region region = new Region(testString.indexOf(BUG_FORMAT_1_2), testString.length());
		IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals(testString.indexOf(BUG_FORMAT_1_2), links[0].getHyperlinkRegion().getOffset());
	}

	public void testMultiLine() {
		String testString = "is a the first line\n this is the second which ends with a bug, " + BUG_FORMAT_1_2;
		viewer.setDocument(new Document(testString));
		Region region = new Region(testString.indexOf(BUG_FORMAT_1_2), testString.length());
		IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals(testString.indexOf(BUG_FORMAT_1_2), links[0].getHyperlinkRegion().getOffset());
	}

	public void testDuplicate() {
		String testString = "*** This bug has been marked as a " + DUPLICATE + " ***";
		viewer.setDocument(new Document(testString));
		Region region = new Region(testString.indexOf(DUPLICATE), testString.length());
		IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals(testString.indexOf(DUPLICATE_NUMBER), links[0].getHyperlinkRegion().getOffset());
	}
}
