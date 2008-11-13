/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
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

	private TextViewer viewer;

	private String[] formats;

	private TaskRepositoryManager repositoryManager;

	private Shell shell;

	protected TaskRepository activeRepository;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		detector = new TaskHyperlinkDetector();

		repository1 = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND, "repository_url1");
		repository2 = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND, "repository_url2");

		detector.setContext(new IAdaptable() {
			@SuppressWarnings("unchecked")
			public Object getAdapter(Class adapter) {
				return (adapter == TaskRepository.class) ? activeRepository : null;
			}
		});
		setRepository(repository1);

		shell = new Shell();
		viewer = new TextViewer(shell, SWT.NONE);

		repositoryManager = TasksUiPlugin.getRepositoryManager();
		repositoryManager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());

		formats = new String[] { TASK_FORMAT_1, TASK_FORMAT_2, TASK_FORMAT_3, TASK_FORMAT_4, BUG_FORMAT_1,
				BUG_FORMAT_2, BUG_FORMAT_3, BUG_FORMAT_4 };
	}

	private void setRepository(final TaskRepository repository) {
		this.activeRepository = repository;
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
			int i = testString.indexOf(format);
			Region region = new Region(i, testString.length() - i);
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertEquals(i, links[0].getHyperlinkRegion().getOffset());
		}
	}

	public void testMiddle() {
		for (String format : formats) {
			String testString = "is a " + format + " in the middle";
			viewer.setDocument(new Document(testString));
			int i = testString.indexOf(format);
			Region region = new Region(i, testString.length() - i);
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertEquals(testString.indexOf(format), links[0].getHyperlinkRegion().getOffset());
		}
	}

	public void testTwoOnSingleLine() {
		String testString = "is a " + BUG_FORMAT_1 + " in the middle and at the end " + BUG_FORMAT_1_2;
		viewer.setDocument(new Document(testString));
		Region region = new Region(0, testString.length());
		IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
		assertNotNull(links);
		assertEquals(2, links.length);
		assertEquals(testString.indexOf(BUG_FORMAT_1), links[0].getHyperlinkRegion().getOffset());
		assertEquals(testString.indexOf(BUG_FORMAT_1_2), links[1].getHyperlinkRegion().getOffset());
	}

	public void testMultiLine() {
		String testString = "is a the first line\n this is the second which ends with a bug, " + BUG_FORMAT_1_2;
		viewer.setDocument(new Document(testString));
		int i = testString.indexOf(BUG_FORMAT_1_2);
		Region region = new Region(i, testString.length() - i);
		IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals(i, links[0].getHyperlinkRegion().getOffset());
	}

	public void testDuplicate() {
		String testString = "*** This bug has been marked as a " + DUPLICATE + " ***";
		viewer.setDocument(new Document(testString));
		int i = testString.indexOf(DUPLICATE);
		Region region = new Region(i, testString.length() - i);
		IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertEquals(testString.indexOf(DUPLICATE_NUMBER), links[0].getHyperlinkRegion().getOffset());
	}

	public void testNoRepositoryInViewNoRepositoryInManager() {
		String testString = "bug 123";
		viewer.setDocument(new Document(testString));
		Region region = new Region(0, testString.length());
		setRepository(null);

		IHyperlink[] links = detector.detectHyperlinks(viewer, region, true);
		assertNull(links);
	}

	public void testRepositoryInViewNoRepositoryInManager() {
		String testString = "bug 123";
		viewer.setDocument(new Document(testString));
		Region region = new Region(0, testString.length());
		setRepository(repository1);

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
		setRepository(null);

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
		setRepository(repository1);
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
		setRepository(null);
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
		setRepository(repository1);
		repositoryManager.addRepository(repository1);
		repositoryManager.addRepository(repository2);

		IHyperlink[] links = detector.detectHyperlinks(viewer, region, true);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertTrue(links[0] instanceof TaskHyperlink);
		assertEquals(((TaskHyperlink) links[0]).getRepository(), repository1);
	}

	public void testMatchMultipleEmptyRegion() {
		String testString = "bug 123 bug 345";
		viewer.setDocument(new Document(testString));
		Region region = new Region(10, 0);
		setRepository(repository1);
		repositoryManager.addRepository(repository1);

		IHyperlink[] links = detector.detectHyperlinks(viewer, region, true);
		assertNotNull(links);
		assertEquals(1, links.length);
		assertTrue(links[0] instanceof TaskHyperlink);
		assertEquals("345", ((TaskHyperlink) links[0]).getTaskId());
	}

}
