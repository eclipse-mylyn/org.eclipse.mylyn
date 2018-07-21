/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - improvements
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.ui;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.commons.ui.PlatformUiUtil;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskHyperlinkDetector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tests.util.TasksUiTestUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Rob Elves
 * @author Terry Hon
 * @author Frank Becker
 */
public class BugzillaTaskHyperlinkDetectorTest extends TestCase {

	private static final String DUPLICATE_NUMBER = "112233";

	private static final String DUPLICATE = "duplicate of " + DUPLICATE_NUMBER;

	private static final String ATTACHMENT_NUMBER = "445566";

	private static final String ATTACHMENT_OLD = "Created an attachment (id=" + ATTACHMENT_NUMBER + ")";

	private static final String ATTACHMENT_NEW = "Created attachment" + ATTACHMENT_NUMBER;

	private final String TASK_FORMAT_1 = "task#123";

	private final String TASK_FORMAT_2 = "task# 123";

	private final String TASK_FORMAT_3 = "task123";

	private final String TASK_FORMAT_4 = "task #123";

	private final String BUG_FORMAT_1 = "bug# 123";

	private final String BUG_FORMAT_2 = "bug # 123";

	private final String BUG_FORMAT_3 = "bug123";

	private final String BUG_FORMAT_4 = "bug #123";

	private final String BUG_FORMAT_1_2 = "bug# 2";

	private final String TASK_FORMAT_1_COMMENT_2 = "task#123 comment #44556677";

	private final String TASK_FORMAT_2_COMMENT_2 = "task# 123 comment #44556677";

	private final String TASK_FORMAT_3_COMMENT_2 = "task123 comment #44556677";

	private final String TASK_FORMAT_4_COMMENT_2 = "task #123 comment #44556677";

	private final String BUG_FORMAT_1_COMMENT_2 = "bug# 123 comment #44556677";

	private final String BUG_FORMAT_2_COMMENT_2 = "bug # 123 comment #44556677";

	private final String BUG_FORMAT_3_COMMENT_2 = "bug123 comment #44556677";

	private final String BUG_FORMAT_4_COMMENT_2 = "bug #123 comment #44556677";

	private final String TASK_FORMAT_1_COMMENT_3 = "task#123 comment#44556677";

	private final String TASK_FORMAT_2_COMMENT_3 = "task# 123 comment#44556677";

	private final String TASK_FORMAT_3_COMMENT_3 = "task123 comment#44556677";

	private final String TASK_FORMAT_4_COMMENT_3 = "task #123 comment#44556677";

	private final String BUG_FORMAT_1_COMMENT_3 = "bug# 123 comment#44556677";

	private final String BUG_FORMAT_2_COMMENT_3 = "bug # 123 comment#44556677";

	private final String BUG_FORMAT_3_COMMENT_3 = "bug123 comment#44556677";

	private final String BUG_FORMAT_4_COMMENT_3 = "bug #123 comment#44556677";

	private final String TASK_FORMAT_1_COMMENT_4 = "task#123 comment # 44556677";

	private final String TASK_FORMAT_2_COMMENT_4 = "task# 123 comment # 44556677";

	private final String TASK_FORMAT_3_COMMENT_4 = "task123 comment # 44556677";

	private final String TASK_FORMAT_4_COMMENT_4 = "task #123 comment # 44556677";

	private final String BUG_FORMAT_1_COMMENT_4 = "bug# 123 comment # 44556677";

	private final String BUG_FORMAT_2_COMMENT_4 = "bug # 123 comment # 44556677";

	private final String BUG_FORMAT_3_COMMENT_4 = "bug123 comment # 44556677";

	private final String BUG_FORMAT_4_COMMENT_4 = "bug #123 comment # 44556677";

	private final String COMMENT_1 = "comment#44556677";

	private final String COMMENT_2 = "comment #44556677";

	private final String COMMENT_3 = "comment # 44556677";

	private final String COMMENT_4 = "comment# 44556677";

	private final String COMMENT_5 = "comment 44556677";

	private TaskHyperlinkDetector detector;

	private TaskRepository repository1;

	private TaskRepository repository2;

	private TextViewer viewer;

	private String[] commentFormats;

	private String[] bugFormats;

	private String[] bugCommentFormats;

	private TaskRepositoryManager repositoryManager;

	private Shell shell;

	protected TaskRepository activeRepository;

	protected ITask task;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		detector = new TaskHyperlinkDetector();
		TasksUiTestUtil.ensureTasksUiInitialization();

		repository1 = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND, "repository_url1");
		repository2 = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND, "repository_url2");
		task = TasksUi.getRepositoryModel().createTask(repository1, "123");
		detector.setContext(new IAdaptable() {
			@SuppressWarnings("rawtypes")
			public Object getAdapter(Class adapter) {
				if (adapter == TaskRepository.class) {
					return activeRepository;
				} else if (adapter == ITask.class) {
					return task;
				}
				return null;
			}
		});
		setRepository(repository1);

		shell = new Shell();
		viewer = new TextViewer(shell, SWT.NONE);

		repositoryManager = TasksUiPlugin.getRepositoryManager();
		repositoryManager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());

		commentFormats = new String[] { COMMENT_1, COMMENT_2, COMMENT_3, COMMENT_4, COMMENT_5 };
		bugFormats = new String[] { TASK_FORMAT_1, TASK_FORMAT_2, TASK_FORMAT_3, TASK_FORMAT_4, BUG_FORMAT_1,
				BUG_FORMAT_2, BUG_FORMAT_3, BUG_FORMAT_4 };
		bugCommentFormats = new String[] { TASK_FORMAT_1_COMMENT_2, TASK_FORMAT_2_COMMENT_2, TASK_FORMAT_3_COMMENT_2,
				TASK_FORMAT_4_COMMENT_2, BUG_FORMAT_1_COMMENT_2, BUG_FORMAT_2_COMMENT_2, BUG_FORMAT_3_COMMENT_2,
				BUG_FORMAT_4_COMMENT_2, TASK_FORMAT_1_COMMENT_3, TASK_FORMAT_2_COMMENT_3, TASK_FORMAT_3_COMMENT_3,
				TASK_FORMAT_4_COMMENT_3, BUG_FORMAT_1_COMMENT_3, BUG_FORMAT_2_COMMENT_3, BUG_FORMAT_3_COMMENT_3,
				BUG_FORMAT_4_COMMENT_3, TASK_FORMAT_1_COMMENT_4, TASK_FORMAT_2_COMMENT_4, TASK_FORMAT_3_COMMENT_4,
				TASK_FORMAT_4_COMMENT_4, BUG_FORMAT_1_COMMENT_4, BUG_FORMAT_2_COMMENT_4, BUG_FORMAT_3_COMMENT_4,
				BUG_FORMAT_4_COMMENT_4 };
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

	public void testBeginningWithSpecialChars() {
		for (String format : bugFormats) {
			String testString = "First line\n:" + format + " is at the beginning";
			viewer.setDocument(new Document(testString));
			Region region = new Region(0, testString.length());
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertTrue(links[0] instanceof TaskHyperlink);
			TaskHyperlink taskLink = (TaskHyperlink) links[0];
			assertEquals("123", taskLink.getTaskId());
			assertEquals(testString.indexOf(format), taskLink.getHyperlinkRegion().getOffset());
			Object comment = taskLink.getSelection();
			assertNull(comment);
		}
		for (String format : bugCommentFormats) {
			String testString = "First line\n:" + format + " is at the beginning";
			viewer.setDocument(new Document(testString));
			Region region = new Region(0, testString.length());
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertTrue(links[0] instanceof TaskHyperlink);
			TaskHyperlink taskLink = (TaskHyperlink) links[0];
			assertEquals("123", taskLink.getTaskId());
			assertEquals(testString.indexOf(format), taskLink.getHyperlinkRegion().getOffset());
			Object comment = taskLink.getSelection();
			assertNotNull(comment);
			assertEquals(TaskAttribute.PREFIX_COMMENT + "44556677", comment);
		}
		for (String format : commentFormats) {
			String testString = "First line\n:" + format + " is at the beginning";
			viewer.setDocument(new Document(testString));
			Region region = new Region(0, testString.length());
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertTrue(links[0] instanceof TaskHyperlink);
			TaskHyperlink taskLink = (TaskHyperlink) links[0];
			assertEquals("123", taskLink.getTaskId());
			assertEquals(testString.indexOf(format), taskLink.getHyperlinkRegion().getOffset());
			Object comment = taskLink.getSelection();
			assertNotNull(comment);
			assertEquals(TaskAttribute.PREFIX_COMMENT + "44556677", comment);
		}
	}

	public void testBeginningOfSecondLine() {
		for (String format : bugFormats) {
			String testString = "First line\n" + format + " is at the beginning";
			viewer.setDocument(new Document(testString));
			Region region = new Region(0, testString.length());
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertTrue(links[0] instanceof TaskHyperlink);
			TaskHyperlink taskLink = (TaskHyperlink) links[0];
			assertEquals("123", taskLink.getTaskId());
			assertEquals(testString.indexOf(format), taskLink.getHyperlinkRegion().getOffset());
			Object comment = taskLink.getSelection();
			assertNull(comment);
		}
		for (String format : bugCommentFormats) {
			String testString = "First line\n" + format + " is at the beginning";
			viewer.setDocument(new Document(testString));
			Region region = new Region(0, testString.length());
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertTrue(links[0] instanceof TaskHyperlink);
			TaskHyperlink taskLink = (TaskHyperlink) links[0];
			assertEquals("123", taskLink.getTaskId());
			assertEquals(testString.indexOf(format), taskLink.getHyperlinkRegion().getOffset());
			Object comment = taskLink.getSelection();
			assertNotNull(comment);
			assertEquals(TaskAttribute.PREFIX_COMMENT + "44556677", comment);
		}
		for (String format : commentFormats) {
			String testString = "First line\n" + format + " is at the beginning";
			viewer.setDocument(new Document(testString));
			Region region = new Region(0, testString.length());
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertTrue(links[0] instanceof TaskHyperlink);
			TaskHyperlink taskLink = (TaskHyperlink) links[0];
			assertEquals("123", taskLink.getTaskId());
			assertEquals(testString.indexOf(format), taskLink.getHyperlinkRegion().getOffset());
			Object comment = taskLink.getSelection();
			assertNotNull(comment);
			assertEquals(TaskAttribute.PREFIX_COMMENT + "44556677", comment);
		}
	}

	public void testBeginningOfSecondLineWithisWhitespace() {
		for (String format : bugFormats) {
			String testString = "First line\n \t " + format + " is at the beginning";
			viewer.setDocument(new Document(testString));
			Region region = new Region(0, testString.length());
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertTrue(links[0] instanceof TaskHyperlink);
			TaskHyperlink taskLink = (TaskHyperlink) links[0];
			assertEquals("123", taskLink.getTaskId());
			assertEquals(testString.indexOf(format), taskLink.getHyperlinkRegion().getOffset());
			Object comment = taskLink.getSelection();
			assertNull(comment);
		}
		for (String format : bugCommentFormats) {
			String testString = "First line\n \t " + format + " is at the beginning";
			viewer.setDocument(new Document(testString));
			Region region = new Region(0, testString.length());
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertTrue(links[0] instanceof TaskHyperlink);
			TaskHyperlink taskLink = (TaskHyperlink) links[0];
			assertEquals("123", taskLink.getTaskId());
			assertEquals(testString.indexOf(format), taskLink.getHyperlinkRegion().getOffset());
			Object comment = taskLink.getSelection();
			assertNotNull(format, comment);
			assertEquals(TaskAttribute.PREFIX_COMMENT + "44556677", comment);
		}
		for (String format : commentFormats) {
			String testString = "First line\n \t " + format + " is at the beginning";
			viewer.setDocument(new Document(testString));
			Region region = new Region(0, testString.length());
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertTrue(links[0] instanceof TaskHyperlink);
			TaskHyperlink taskLink = (TaskHyperlink) links[0];
			assertEquals("123", taskLink.getTaskId());
			assertEquals(testString.indexOf(format), taskLink.getHyperlinkRegion().getOffset());
			Object comment = taskLink.getSelection();
			assertNotNull(comment);
			assertEquals(TaskAttribute.PREFIX_COMMENT + "44556677", comment);
		}
	}

	public void testBeginning() {
		for (String format : bugFormats) {
			String testString = format + " is at the beginning";
			viewer.setDocument(new Document(testString));
			Region region = new Region(0, testString.length());
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertTrue(links[0] instanceof TaskHyperlink);
			TaskHyperlink taskLink = (TaskHyperlink) links[0];
			assertEquals("123", taskLink.getTaskId());
			assertEquals(testString.indexOf(format), taskLink.getHyperlinkRegion().getOffset());
			Object comment = taskLink.getSelection();
			assertNull(comment);
		}
		for (String format : bugCommentFormats) {
			String testString = format + " is at the beginning";
			viewer.setDocument(new Document(testString));
			Region region = new Region(0, testString.length());
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertTrue(links[0] instanceof TaskHyperlink);
			TaskHyperlink taskLink = (TaskHyperlink) links[0];
			assertEquals("123", taskLink.getTaskId());
			assertEquals(testString.indexOf(format), taskLink.getHyperlinkRegion().getOffset());
			Object comment = taskLink.getSelection();
			assertNotNull(comment);
			assertEquals(TaskAttribute.PREFIX_COMMENT + "44556677", comment);
		}
		for (String format : commentFormats) {
			String testString = format + " is at the beginning";
			viewer.setDocument(new Document(testString));
			Region region = new Region(0, testString.length());
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertTrue(links[0] instanceof TaskHyperlink);
			TaskHyperlink taskLink = (TaskHyperlink) links[0];
			assertEquals("123", taskLink.getTaskId());
			assertEquals(testString.indexOf(format), taskLink.getHyperlinkRegion().getOffset());
			Object comment = taskLink.getSelection();
			assertNotNull(comment);
			assertEquals(TaskAttribute.PREFIX_COMMENT + "44556677", comment);
		}
	}

	public void testEnd() {
		for (String format : bugFormats) {
			String testString = "is ends with " + format;
			viewer.setDocument(new Document(testString));
			int i = testString.indexOf(format);
			Region region = new Region(i, testString.length() - i);
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertTrue(links[0] instanceof TaskHyperlink);
			TaskHyperlink taskLink = (TaskHyperlink) links[0];
			assertEquals("123", taskLink.getTaskId());
			assertEquals(testString.indexOf(format), taskLink.getHyperlinkRegion().getOffset());
			Object comment = taskLink.getSelection();
			assertNull(comment);
		}
		for (String format : bugCommentFormats) {
			String testString = "is ends with " + format;
			viewer.setDocument(new Document(testString));
			int i = testString.indexOf(format);
			Region region = new Region(i, testString.length() - i);
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertTrue(links[0] instanceof TaskHyperlink);
			TaskHyperlink taskLink = (TaskHyperlink) links[0];
			assertEquals("123", taskLink.getTaskId());
			assertEquals(testString.indexOf(format), taskLink.getHyperlinkRegion().getOffset());
			Object comment = taskLink.getSelection();
			assertNotNull(comment);
			assertEquals(TaskAttribute.PREFIX_COMMENT + "44556677", comment);
		}
		for (String format : commentFormats) {
			String testString = "is ends with " + format;
			viewer.setDocument(new Document(testString));
			Region region = new Region(0, testString.length());
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertTrue(links[0] instanceof TaskHyperlink);
			TaskHyperlink taskLink = (TaskHyperlink) links[0];
			assertEquals("123", taskLink.getTaskId());
			assertEquals(testString.indexOf(format), taskLink.getHyperlinkRegion().getOffset());
			Object comment = taskLink.getSelection();
			assertNotNull(comment);
			assertEquals(TaskAttribute.PREFIX_COMMENT + "44556677", comment);
		}
	}

	public void testMiddle() {
		for (String format : bugFormats) {
			String testString = "is a " + format + " in the middle";
			viewer.setDocument(new Document(testString));
			int i = testString.indexOf(format);
			Region region = new Region(i, testString.length() - i);
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertTrue(links[0] instanceof TaskHyperlink);
			TaskHyperlink taskLink = (TaskHyperlink) links[0];
			assertEquals("123", taskLink.getTaskId());
			assertEquals(testString.indexOf(format), taskLink.getHyperlinkRegion().getOffset());
			Object comment = taskLink.getSelection();
			assertNull(comment);
		}
		for (String format : bugCommentFormats) {
			String testString = "is a " + format + " in the middle";
			viewer.setDocument(new Document(testString));
			Region region = new Region(0, testString.length());
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertTrue(links[0] instanceof TaskHyperlink);
			TaskHyperlink taskLink = (TaskHyperlink) links[0];
			assertEquals("123", taskLink.getTaskId());
			assertEquals(testString.indexOf(format), taskLink.getHyperlinkRegion().getOffset());
			Object comment = taskLink.getSelection();
			assertNotNull(comment);
			assertEquals(TaskAttribute.PREFIX_COMMENT + "44556677", comment);
		}
		for (String format : commentFormats) {
			String testString = "is a " + format + " in the middle";
			viewer.setDocument(new Document(testString));
			Region region = new Region(0, testString.length());
			IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
			assertNotNull(links);
			assertEquals(1, links.length);
			assertTrue(links[0] instanceof TaskHyperlink);
			TaskHyperlink taskLink = (TaskHyperlink) links[0];
			assertEquals("123", taskLink.getTaskId());
			assertEquals(testString.indexOf(format), taskLink.getHyperlinkRegion().getOffset());
			Object comment = taskLink.getSelection();
			assertNotNull(comment);
			assertEquals(TaskAttribute.PREFIX_COMMENT + "44556677", comment);
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
		assertEquals(testString.indexOf(DUPLICATE), links[0].getHyperlinkRegion().getOffset());
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
		assertTrue(links[1] instanceof TaskHyperlink);

		// order of repository is not defined so we must test the two cases
		if (((TaskHyperlink) links[0]).getRepository() == repository1) {
			assertEquals(((TaskHyperlink) links[1]).getRepository(), repository2);
		} else {
			assertEquals(((TaskHyperlink) links[0]).getRepository(), repository2);
		}
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

	public void testAttachmentOld() {
		String testString = ATTACHMENT_OLD;
		viewer.setDocument(new Document(testString));
		Region region = new Region(0, testString.length());
		IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
		assertNotNull(links);
		if (PlatformUiUtil.supportsMultipleHyperlinkPresenter()) {
			assertEquals(2, links.length);
			assertEquals(testString.indexOf(ATTACHMENT_OLD), links[0].getHyperlinkRegion().getOffset());
			assertEquals(testString.indexOf(ATTACHMENT_OLD), links[1].getHyperlinkRegion().getOffset());
		} else {
			assertEquals(1, links.length);
			assertEquals(testString.indexOf(ATTACHMENT_OLD), links[0].getHyperlinkRegion().getOffset());
		}
	}

	public void testAttachmentNew() {
		String testString = ATTACHMENT_NEW;
		viewer.setDocument(new Document(testString));
		Region region = new Region(0, testString.length());
		IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
		assertNotNull(links);
		if (PlatformUiUtil.supportsMultipleHyperlinkPresenter()) {
			assertEquals(2, links.length);
			assertEquals(testString.indexOf(ATTACHMENT_NEW), links[0].getHyperlinkRegion().getOffset());
			assertEquals(testString.indexOf(ATTACHMENT_NEW), links[1].getHyperlinkRegion().getOffset());
		} else {
			assertEquals(1, links.length);
			assertEquals(testString.indexOf(ATTACHMENT_NEW), links[0].getHyperlinkRegion().getOffset());
		}
	}

	public void testCommentLotsOfWhitespace() {
		String testString = "bug 123     d bug 245 comment 1";
		viewer.setDocument(new Document(testString));
		Region region = new Region(0, testString.length());
		IHyperlink[] links = detector.detectHyperlinks(viewer, region, false);
		assertNotNull(links);
		assertEquals(2, links.length);
		assertEquals(testString.indexOf("bug 123"), links[0].getHyperlinkRegion().getOffset());
		assertEquals(testString.indexOf("bug 245 comment 1"), links[1].getHyperlinkRegion().getOffset());
	}

}
