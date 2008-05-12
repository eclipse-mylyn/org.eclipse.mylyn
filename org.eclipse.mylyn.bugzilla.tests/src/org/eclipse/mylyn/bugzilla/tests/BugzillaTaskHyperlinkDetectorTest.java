/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryTextViewer;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskHyperlinkDetector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Rob Elves
 * @author Terry Hon
 */
public class BugzillaTaskHyperlinkDetectorTest extends TestCase {

	private static final String DUPLICATE_NUMBER = "112233";

	private static final String DUPLICATE = "duplicate of " + DUPLICATE_NUMBER;

	private final String TASK_FORMAT_1 = "task#1";

	private final String TASK_FORMAT_2 = "task# 1";

	private final String TASK_FORMAT_3 = "task1";

	private final String TASK_FORMAT_4 = "task #1";

	private final String BUG_FORMAT_1 = "bug# 1";

	private final String BUG_FORMAT_2 = "bug # 1";

	private final String BUG_FORMAT_3 = "bug1";

	private final String BUG_FORMAT_4 = "bug #1";

	private final String BUG_FORMAT_1_2 = "bug# 2";

	//private BugzillaTaskHyperlinkDetector detector = new BugzillaTaskHyperlinkDetector();
	private TaskHyperlinkDetector detector;

	private TaskRepository repository1;

	private TaskRepository repository2;

	private RepositoryTextViewer viewer;

	private String[] formats;

	private TaskRepositoryManager repositoryManager;

	private Shell shell;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		detector = new TaskHyperlinkDetector();

		repository1 = new TaskRepository(BugzillaCorePlugin.REPOSITORY_KIND, "repository_url1");
		repository2 = new TaskRepository(BugzillaCorePlugin.REPOSITORY_KIND, "repository_url2");

		shell = new Shell();
		viewer = new RepositoryTextViewer(repository1, shell, SWT.NONE);

		repositoryManager = TasksUiPlugin.getRepositoryManager();
		repositoryManager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());

		formats = new String[] { TASK_FORMAT_1, TASK_FORMAT_2, TASK_FORMAT_3, TASK_FORMAT_4, BUG_FORMAT_1,
				BUG_FORMAT_2, BUG_FORMAT_3, BUG_FORMAT_4 };
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		detector.dispose();
		repositoryManager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());

		shell.dispose();
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

	public void testNoRepositoryInViewNoRepositoryInManager() {
		String testString = "bug 123";
		viewer.setDocument(new Document(testString));
		Region region = new Region(0, testString.length());
		viewer.setRepository(null);

		IHyperlink[] links = detector.detectHyperlinks(viewer, region, true);
		assertNull(links);
	}

	public void testRepositoryInViewNoRepositoryInManager() {
		String testString = "bug 123";
		viewer.setDocument(new Document(testString));
		Region region = new Region(0, testString.length());
		viewer.setRepository(repository1);

		IHyperlink[] links = detector.detectHyperlinks(viewer, region, true);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertTrue(links[0] instanceof TaskHyperlink);
		assertEquals(((TaskHyperlink) links[0]).getRepository(), repository1);
	}

	public void testNoRepositoryInViewOneRepositoryInManager() {
		String testString = "bug 123";
		viewer.setDocument(new Document(testString));
		Region region = new Region(0, testString.length());
		viewer.setRepository(null);

		repositoryManager.addRepository(repository1);
		IHyperlink[] links = detector.detectHyperlinks(viewer, region, true);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertTrue(links[0] instanceof TaskHyperlink);
		assertEquals(((TaskHyperlink) links[0]).getRepository(), repository1);
	}

	public void testRepositoryInViewOneRepositoryInManager() {
		String testString = "bug 123";
		viewer.setDocument(new Document(testString));
		Region region = new Region(0, testString.length());
		viewer.setRepository(repository1);
		repositoryManager.addRepository(repository1);

		IHyperlink[] links = detector.detectHyperlinks(viewer, region, true);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertTrue(links[0] instanceof TaskHyperlink);
		assertEquals(((TaskHyperlink) links[0]).getRepository(), repository1);
	}

	public void testNoRepositoryInViewTwoRepositoryInManager() {
		String testString = "bug 123";
		viewer.setDocument(new Document(testString));
		Region region = new Region(0, testString.length());
		viewer.setRepository(null);
		repositoryManager.addRepository(repository1);
		repositoryManager.addRepository(repository2);

		IHyperlink[] links = detector.detectHyperlinks(viewer, region, true);
		assertNotNull(links);
		assertEquals(2, links.length);
		assertTrue(links[0] instanceof TaskHyperlink);
		assertEquals(((TaskHyperlink) links[0]).getRepository(), repository1);
		assertTrue(links[1] instanceof TaskHyperlink);
		assertEquals(((TaskHyperlink) links[1]).getRepository(), repository2);
	}

	public void testRepositoryInViewTwoRepositoryInManager() {
		String testString = "bug 123";
		viewer.setDocument(new Document(testString));
		Region region = new Region(0, testString.length());
		viewer.setRepository(repository1);
		repositoryManager.addRepository(repository1);
		repositoryManager.addRepository(repository2);

		IHyperlink[] links = detector.detectHyperlinks(viewer, region, true);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertTrue(links[0] instanceof TaskHyperlink);
		assertEquals(((TaskHyperlink) links[0]).getRepository(), repository1);
	}

}
