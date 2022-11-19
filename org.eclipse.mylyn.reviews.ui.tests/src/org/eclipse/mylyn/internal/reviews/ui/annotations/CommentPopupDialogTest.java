/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.annotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.commons.ui.ShellDragSupport;
import org.eclipse.mylyn.internal.gerrit.ui.GerritReviewBehavior;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.ILineLocation;
import org.eclipse.mylyn.reviews.core.model.ILineRange;
import org.eclipse.mylyn.reviews.core.model.ILocation;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.ui.forms.widgets.Section;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import junit.framework.TestCase;

public class CommentPopupDialogTest extends TestCase {

	private final static String USER_ID = "1";

	private final static String USER_NAME = "Test User";

	private final static String USER_EMAIL = "test@test.test";

	private Shell shell;

	private TaskRepository repository;

	private ITask task;

	private ReviewBehavior behavior;

	private IReviewItem reviewItem;

	private ILineRange range;

	private CommentPopupDialog commentPopupDialog = null;

	private CommentPopupDialog secondPopupDialog;

	@Override
	public void setUp() {
		shell = new Shell();
		repository = new TaskRepository("mock", "url");
		TasksUi.getRepositoryManager().addRepository(repository);
		task = TasksUi.getRepositoryModel().createTask(repository, "1");
		ReviewBehavior reviewBehavior = new GerritReviewBehavior(task);
		behavior = spy(reviewBehavior);
		reviewItem = IReviewsFactory.INSTANCE.createFileItem();
		reviewItem.setId("item.java");
		range = IReviewsFactory.INSTANCE.createLineRange();
		range.setStart(0);
		range.setEnd(10);
	}

	@Override
	public void tearDown() {
		if (secondPopupDialog != null) {
			secondPopupDialog.dispose(true);
			secondPopupDialog = null;
		}
		commentPopupDialog.dispose(true);
		commentPopupDialog = null;
		reviewItem = null;
		range = null;
		behavior = null;
		TasksUiPlugin.getTaskList().deleteTask(task);
		shell.dispose();

		IJobManager jobManager = Job.getJobManager();
		jobManager.cancel(InlineCommentSubmitter.JOB_FAMILY);
	}

	/**
	 * Tests that the comment dialog closes properly
	 */
	@Test
	public void testClose() {
		commentPopupDialog = createPopupWithXComments(1, false);
		assertTrue(commentPopupDialog.close());
		assertNotNull(commentPopupDialog.getCommentEditor());
		assertNull(commentPopupDialog.getCommentEditor().getEditorComposite());
	}

	@Test
	public void testMovable() {
		commentPopupDialog = createPopupWithXComments(1, false);
		Listener[] listeners = commentPopupDialog.getComposite().getListeners(SWT.MouseMove);
		Iterables.find(Arrays.asList(listeners), new Predicate<Listener>() {
			public boolean apply(Listener listener) {
				return listener instanceof ShellDragSupport;
			}
		});
	}

	/**
	 * Tests that the comment editor closes properly
	 */
	@Test
	public void testCloseForCommentEditorText() {
		Text text = editOneComment();
		assertFalse(commentPopupDialog.close());
		assertNotNull(commentPopupDialog.getCommentEditor());
		Composite editorComposite = commentPopupDialog.getCommentEditor().getEditorComposite();
		assertNotNull(editorComposite);

		text.setText("");
		assertTrue(commentPopupDialog.close());
		assertTrue(editorComposite.isDisposed());
	}

	/**
	 * Tests that the comment dialog closes if forced
	 */
	@Test
	public void testForceClose() {
		editOneComment();
		assertTrue(commentPopupDialog.close(true));
		assertTrue(commentPopupDialog.getCommentEditor().getEditorComposite().isDisposed());
	}

	/**
	 * Tests that the comment dialog is created with a help text and removed when the user invokes the comment editor
	 */
	@Test
	public void testHelpText() {
		commentPopupDialog = createPopupWithXComments(1, false);
		Text helpText = commentPopupDialog.getHelpText();
		assertNotNull(helpText);
		assertEquals(Messages.CommentPopupDialog_HelpText, helpText.getText());

		addCommentEditor(commentPopupDialog, retrieveCommentFromUI(1));
		assertTrue(helpText.isDisposed());
		commentPopupDialog.getCommentEditor().removeControl();
		assertTrue(helpText.isDisposed());

	}

	/**
	 * Tests that buttons save and discard/done buttons are interacting properly depending if there is text in the
	 * comment editor. First, we check that invoking the comment editor on a draft with text enables the save and
	 * discard/done buttons. Second, removing the text in the comment editor disables the save button. Third, we check
	 * that an additional invocation of the comment popup does not result in a change in the editor. Then, removing the
	 * popup and making the same invocation of the comment popup causes it to open the last comment in the popup which
	 * is a draft and contains text so both buttons are enabled
	 */
	@Test
	public void testTextListenerWithDraft() {
		setupDraft();
		InlineCommentEditor commentEditor = commentPopupDialog.getCommentEditor();
		Button saveButton = commentEditor.getSaveButton();
		Button discardOrDoneButton = commentEditor.getDiscardOrDoneButton();

		addCommentEditor(commentPopupDialog, retrieveCommentFromUI(2));
		assertTrue(saveButton.getEnabled());
		assertTrue(discardOrDoneButton.getEnabled());

		commentEditor.getCommentEditorText().setText("");
		assertFalse(saveButton.getEnabled());
		assertTrue(discardOrDoneButton.getEnabled());

		addCommentEditor(commentPopupDialog, retrieveCommentFromUI(1));
		assertTrue(commentEditor.getCommentEditorText().getText().isEmpty());
		assertFalse(saveButton.getEnabled());
		assertTrue(discardOrDoneButton.getEnabled());

		commentEditor.removeControl();
		addCommentEditor(commentPopupDialog, retrieveCommentFromUI(1));
		assertTrue(commentPopupDialog.getCommentEditor().getSaveButton().getEnabled());
		assertTrue(commentPopupDialog.getCommentEditor().getDiscardOrDoneButton().getEnabled());
		assertEquals("Test Comment 2", commentEditor.getCommentEditorText().getText());
	}

	/**
	 * Tests that buttons save and discard/done buttons are interacting properly depending if there is text in the
	 * comment editor. First, we check that invoking the comment editor on a saved comment (non-draft) causes the editor
	 * to open with no text and with both the save and discard/done buttons disabled. Second, we check that adding text
	 * to the editor enables these buttons.
	 */
	@Test
	public void testTextListenerNoDraft() {
		commentPopupDialog = createPopupWithXComments(1, false);
		InlineCommentEditor commentEditor = commentPopupDialog.getCommentEditor();
		addCommentEditor(commentPopupDialog, retrieveCommentFromUI(1));
		Button saveButton = commentEditor.getSaveButton();
		Button discardOrDoneButton = commentEditor.getDiscardOrDoneButton();

		assertFalse(saveButton.getEnabled());
		assertTrue(discardOrDoneButton.getEnabled());

		commentEditor.getCommentEditorText().setText("test");
		assertTrue(saveButton.getEnabled());
		assertFalse(discardOrDoneButton.getEnabled());
	}

	/**
	 * Tests that the comment editor (and its UI component children) are removed properly
	 */
	@Test
	public void testRemoveEditor() {
		editOneComment();
		InlineCommentEditor commentEditor = commentPopupDialog.getCommentEditor();
		assertNotNull(commentEditor);
		assertNotNull(commentEditor.getEditorComposite());

		commentEditor.removeControl();
		assertNotNull(commentEditor);
		assertNull(commentEditor.getEditorComposite());
		assertNull(commentEditor.getCommentEditorText());
	}

	/**
	 * Tests that the comment popup displays comments with accurate information
	 *
	 * @throws Exception
	 */
	@Test
	public void testCommentSections() throws Exception {
		commentPopupDialog = createPopupWithXComments(2, false);
		Composite scrolledComposite = commentPopupDialog.getScrolledComposite();
		checkMouseDownListener(scrolledComposite);
		List<Section> sections = commentPopupDialog.getScrolledCompositeSections();
		assertEquals(2, sections.size());
		assertSection(sections.get(0), "Test Comment 1", new Date(1), false);
		assertSection(sections.get(1), "Test Comment 2", new Date(2), true);
	}

	/**
	 * Tests that the comment editor UI displays accurate information for comment threads with drafts
	 *
	 * @throws Exception
	 */
	@Test
	public void testCommentEditorButtonsWithDraft() throws Exception {
		commentPopupDialog = createPopupWithXComments(2, false);
		addCommentEditor(commentPopupDialog, retrieveCommentFromUI(1));
		assertCommentEditorContents(true);

		addCommentEditor(commentPopupDialog, retrieveCommentFromUI(2));
		assertCommentEditorContents(true);
	}

	/**
	 * Tests that the comment editor UI displays accurate information for comment threads with no drafts
	 *
	 * @throws Exception
	 */
	@Test
	public void testCommentEditorButtonsNoDraft() throws Exception {
		commentPopupDialog = createPopupWithXComments(1, false);
		addCommentEditor(commentPopupDialog, retrieveCommentFromUI(1));
		assertCommentEditorContents(false);
	}

	/**
	 * Tests submitting a new draft using the save button
	 *
	 * @throws Exception
	 */
	@Test
	public void testSaveComment() throws Exception {
		editOneComment();
		assertSavingComment(commentPopupDialog.getCommentEditor().getSaveButton(), "test", false, false);
		assertEquals(2, reviewItem.getComments().size());
		assertEquals("test", reviewItem.getComments().get(1).getDescription());
	}

	/**
	 * Tests submitting a draft edit using the save button
	 *
	 * @throws Exception
	 */
	@Test
	public void testSaveDraft() throws Exception {
		setupDraft();
		assertSavingComment(commentPopupDialog.getCommentEditor().getSaveButton(), "test", true, false);
		assertEquals(2, reviewItem.getComments().size());
		assertEquals("test", reviewItem.getComments().get(1).getDescription());
	}

	/**
	 * Tests submitting a new draft with the discard/done button
	 *
	 * @throws Exception
	 */
	@Test
	public void testReplyDoneComment() throws Exception {
		editOneComment();
		assertSavingComment(commentPopupDialog.getCommentEditor().getDiscardOrDoneButton(), "Done", false, false);
		assertEquals(2, reviewItem.getComments().size());
		assertEquals(Messages.CommentPopupDialog_Done, reviewItem.getComments().get(1).getDescription());
	}

	/**
	 * Tests discarding a draft with the discard/done button
	 *
	 * @throws Exception
	 */
	@Test
	public void testDiscardDraft() throws Exception {
		setupDraft();
		assertSavingComment(commentPopupDialog.getCommentEditor().getDiscardOrDoneButton(), "test", true, true);
		assertEquals(1, reviewItem.getComments().size());
	}

	/**
	 * Tests cancel the comment editor with the cancel button
	 *
	 * @throws Exception
	 */
	@Test
	public void testCancel() throws Exception {
		editOneComment();
		InlineCommentEditor commentEditor = commentPopupDialog.getCommentEditor();
		assertNotNull(commentEditor);
		assertNotNull(commentEditor.getEditorComposite());
		pressButton(commentEditor.getCancelButton());
		assertNotNull(commentEditor);
		assertNull(commentEditor.getEditorComposite());
		assertNotNull(commentPopupDialog.getShell());
	}

	/**
	 * Tests the functionality of the last comment helper method in {@link CommentPopupDialog}
	 */
	@Test
	public void testGetLastComment() {
		commentPopupDialog = createPopupWithXComments(3, false);
		assertEquals(retrieveCommentFromUI(2), commentPopupDialog.getLastCommentDraft());
	}

	/**
	 * Tests that having two hover comment popups will only result in one being editable
	 */
	@Test
	public void testDuplicateHoverCommentEditors() {
		assertDuplicateCommentEditor(false);
	}

	/**
	 * Tests that having one hover comment popup and one comment navigator comment popup will only result in one being
	 * editable
	 */
	@Test
	public void testDuplicateHoverAndNavigatorCommentEditor() {
		assertDuplicateCommentEditor(true);
	}

	/**
	 * Tests that opening the comment navigator popup first will make a hover comment popup not editable
	 */
	@Test
	public void testEditableNavigatorCommentOpennedFirst() {
		commentPopupDialog = createPopupWithXComments(1, true);
		assertTrue(commentPopupDialog.getEditable());
		secondPopupDialog = createPopupWithXComments(1, false);
		assertFalse(secondPopupDialog.getEditable());
	}

	/**
	 * Tests that opening the hover comment popup first will make a comment navigator popup editable (since hover
	 * comment popups are not added to the edit map until the editor is invoked)
	 */
	@Test
	public void testEditableHoverCommentOpennedFirst() {
		commentPopupDialog = createPopupWithXComments(1, false);
		assertTrue(commentPopupDialog.getEditable());
		secondPopupDialog = createPopupWithXComments(1, true);
		assertTrue(secondPopupDialog.getEditable());
	}

	/**
	 * Tests that opening the hover comment popup first and then its comment editor will make a comment navigator popup
	 * not editable
	 */
	@Test
	public void testEditableHoverCommentAndEditorOpennedFirst() {
		commentPopupDialog = createPopupWithXComments(1, false);
		assertTrue(commentPopupDialog.getEditable());
		addCommentEditor(commentPopupDialog, retrieveCommentFromUI(1));
		secondPopupDialog = createPopupWithXComments(1, true);
		assertFalse(secondPopupDialog.getEditable());
	}

	/**
	 * Tests that setting the height based on the y-coordinate of the mouse works for one or more monitors
	 */
	@Test
	public void testSetHeightBasedOnMouse() {
		commentPopupDialog = spy(createPopupWithXComments(100, true));

		// Monitor is the primary display
		Rectangle monitorArea = new Rectangle(0, 0, 1680, 1050);
		doReturn(monitorArea).when(commentPopupDialog).getMonitorArea();
		commentPopupDialog.setHeightBasedOnMouse(500);
		assertEquals(534, commentPopupDialog.getShell().getSize().y);

		// Monitor to the left and below the top of primary display
		monitorArea = new Rectangle(-1920, 480, 1920, 1080);
		doReturn(monitorArea).when(commentPopupDialog).getMonitorArea();
		commentPopupDialog.setHeightBasedOnMouse(500);
		assertEquals(1044, commentPopupDialog.getShell().getSize().y);

		// Monitor to the right and below the top of primary display
		monitorArea = new Rectangle(1680, 480, 1920, 1080);
		doReturn(monitorArea).when(commentPopupDialog).getMonitorArea();
		commentPopupDialog.setHeightBasedOnMouse(500);
		assertEquals(1044, commentPopupDialog.getShell().getSize().y);

		// Monitor to the left above the top of primary display
		monitorArea = new Rectangle(-1920, -420, 1920, 1080);
		doReturn(monitorArea).when(commentPopupDialog).getMonitorArea();
		commentPopupDialog.setHeightBasedOnMouse(500);
		assertEquals(144, commentPopupDialog.getShell().getSize().y);

		// Monitor to the right above the top of primary display
		monitorArea = new Rectangle(1680, -420, 1920, 1080);
		doReturn(monitorArea).when(commentPopupDialog).getMonitorArea();
		commentPopupDialog.setHeightBasedOnMouse(500);
		assertEquals(144, commentPopupDialog.getShell().getSize().y);
	}

	/**
	 * Returns a comment from the dialog depending on the ordering from the top of the comment list
	 *
	 * @param commentNumber
	 *            the number of comments from the top of the comment list (counting starts at 1)
	 * @return a comment from the UI
	 */
	private IComment retrieveCommentFromUI(int commentNumber) {
		if (commentNumber > 0) {
			return (IComment) commentPopupDialog.getScrolledCompositeSections().get(commentNumber - 1).getData();
		}
		return null;
	}

	/**
	 * Creates a comment dialog with one comment (a submitted comment)
	 *
	 * @return the text of that submitted comment
	 */
	private Text editOneComment() {
		commentPopupDialog = createPopupWithXComments(1, false);
		assertEquals(1, reviewItem.getComments().size());
		addCommentEditor(commentPopupDialog, retrieveCommentFromUI(1));
		Text commentEditorText = commentPopupDialog.getCommentEditor().getCommentEditorText();

		commentEditorText.setText("test");
		return commentEditorText;
	}

	/**
	 * Creates a comment dialog with two comments (first comment is a submitted comment, second comment is a draft)
	 */
	private void setupDraft() {
		commentPopupDialog = createPopupWithXComments(2, false);
		assertEquals(2, reviewItem.getComments().size());
		addCommentEditor(commentPopupDialog, retrieveCommentFromUI(2));
		commentPopupDialog.getCommentEditor().getCommentEditorText().setText("test");
		assertEquals("Test Comment 2", reviewItem.getComments().get(1).getDescription());
	}

	/**
	 * Adds a {@link CommentPopupDialog}
	 *
	 * @param x
	 *            the number of comments that will be added to the dialog
	 * @param isCommentNavigator
	 *            true if the dialog will be from the next/previous button, false if the dialog will be from a hover
	 *            input
	 * @return the created {@link CommentPopupDialog}
	 */
	private CommentPopupDialog createPopupWithXComments(int x, boolean isCommentNavigator) {
		CommentAnnotationHoverInput commentInput = spy(new CommentAnnotationHoverInput(addXComments(x), behavior));
		doReturn(behavior).when(commentInput).getBehavior();

		CommentPopupDialog commentDialog = new CommentPopupDialog(shell, SWT.NO_FOCUS | SWT.ON_TOP, null, null,
				isCommentNavigator);
		commentDialog.create();

		commentDialog.setInput(commentInput);
		commentDialog.open();
		return commentDialog;
	}

	/**
	 * Adds comments to a comment dialog (note that every even numbered comment is a draft)
	 *
	 * @param x
	 *            the number of comments that will be created
	 * @return the created list of {@link CommentAnnotation}
	 */
	private List<CommentAnnotation> addXComments(int x) {
		List<CommentAnnotation> annotations = new ArrayList<CommentAnnotation>();
		for (int i = 1; i <= x; i++) {
			boolean isDraft = i % 2 == 0;
			annotations.add(
					createAnnotation(createComment("Test Comment " + i, new Date(i), Integer.toString(i), isDraft)));
		}
		return annotations;
	}

	/**
	 * Creates a {@link CommentAnnotation} for a provided comment
	 *
	 * @param comment
	 *            the provided comment
	 * @return the created {@link CommentAnnotation}
	 */
	private CommentAnnotation createAnnotation(IComment comment) {
		return new CommentAnnotation(1, 1, comment);
	}

	/**
	 * Creates a {@link IComment} with the provided parameters
	 *
	 * @param text
	 *            the text content of the comment
	 * @param date
	 *            the date of the comment
	 * @param id
	 *            the comment id
	 * @param isDraft
	 *            true if the comment created is a draft, false otherwise
	 * @return the created {@link IComment}
	 */
	private IComment createComment(String text, Date date, String id, boolean isDraft) {
		IComment comment = IReviewsFactory.INSTANCE.createComment();
		comment.getLocations().add(createLocation());
		comment.setDescription(text);
		comment.setCreationDate(date);
		comment.setDraft(isDraft);
		comment.setId(id);
		comment.setAuthor(createUser());
		comment.setItem(reviewItem);
		return comment;
	}

	/**
	 * Creates a {@link ILocation} for the comment dialog
	 *
	 * @return the created {@link ILocation}
	 */
	private ILocation createLocation() {
		ILineLocation location = IReviewsFactory.INSTANCE.createLineLocation();
		location.getRanges().add(range);
		return location;
	}

	/**
	 * Creates a {@link IUser} for the comment dialog
	 *
	 * @return the created {@link IUser}
	 */
	private IUser createUser() {
		IUser user = IReviewsFactory.INSTANCE.createUser();
		user.setId(USER_ID);
		user.setDisplayName(USER_NAME);
		user.setEmail(USER_EMAIL);
		return user;
	}

	/**
	 * Ensures that the {@link Control} has only 1 {@link Listener}
	 *
	 * @param c
	 *            the {@link Control} that is checked
	 */
	private void checkMouseDownListener(Control c) {
		Listener[] listeners = c.getListeners(SWT.MouseDown);
		List<TypedListener> typedListeners = new ArrayList<TypedListener>();
		for (Listener listener : listeners) {
			if (listener instanceof TypedListener) {
				TypedListener tl = (TypedListener) listener;
				if (tl.getEventListener()
						.toString()
						.contains("org.eclipse.mylyn.internal.reviews.ui.annotations.CommentPopupDialog")) {
					typedListeners.add(tl);
				}
			}
		}
		assertEquals(1, typedListeners.size());
	}

	/**
	 * Asserts the UI for the provided {@link Section}
	 *
	 * @param s
	 *            the {@link Section} that will be checked
	 * @param commentContent
	 *            the comment text
	 * @param commentDate
	 *            the date of the comment
	 * @param isDraft
	 *            true if the comment is a draft, false otherwise
	 * @throws Exception
	 */
	private void assertSection(Section s, String commentContent, Date commentDate, boolean isDraft) throws Exception {
		Control[] controls = s.getChildren();
		assertEquals(3, controls.length);

		/* 1. Check if listeners are added correctly
		 *
		 * Structure of a Section
		 * - Section
		 *   - Label (the comment information)
		 *   - LayoutComposite
		 *     - LayoutComposite
		 *       - Label
		 *       - Label (the DRAFT text)
		 *     - Toolbar
		 *   - SizedComposite
		 *     - Composite
		 *       - Label
		 *       - StyledText (the comment content)
		 */
		assertEquals(1, recursivelyFindCommentEditorListener(controls[0]));
		assertEquals(5, recursivelyFindCommentEditorListener(controls[1]));
		assertEquals(4, recursivelyFindCommentEditorListener(controls[2]));

		// 2. Check contents of the section
		assertTrue(controls[0] instanceof Label);
		String[] commentLabel = ((Label) controls[0]).getText().split("   ");
		assertEquals(2, commentLabel.length);
		assertEquals(USER_NAME, commentLabel[0]);
		DateFormat format = new SimpleDateFormat("MMM d, yyyy h:mm aa");
		assertEquals(format.format(commentDate), commentLabel[1]);

		if (isDraft) {
			assertEquals("DRAFT",
					((Label) ((Composite) ((Composite) controls[1]).getChildren()[0]).getChildren()[1]).getText());
		}

		assertEquals(commentContent,
				((StyledText) ((Composite) ((Composite) controls[2]).getChildren()[0]).getChildren()[1]).getText());

	}

	/**
	 * Traverses the {@link Control} to find mouse listeners
	 *
	 * @param c
	 *            the {@link Control} that will be traversed
	 * @return the number of listeners for the provided {@link Control} and its children
	 */
	private int recursivelyFindCommentEditorListener(Control c) {
		checkMouseDownListener(c);
		int counter = 1;

		if (c instanceof Composite) {
			Control[] controls = ((Composite) c).getChildren();
			for (Control control : controls) {
				counter = counter + recursivelyFindCommentEditorListener(control);
			}
		}

		return counter;
	}

	/**
	 * Asserts that the UI elements are properly created
	 *
	 * @param isDraft
	 *            true if the comment is a draft edit, false if the comment is a new draft
	 */
	private void assertCommentEditorContents(boolean isDraft) {
		InlineCommentEditor commentEditor = commentPopupDialog.getCommentEditor();
		Composite editorComposite = commentEditor.getEditorComposite();
		Control[] editorContents = editorComposite.getChildren();
		assertTrue(editorContents[0] instanceof Text);
		assertTrue(editorContents[1] instanceof Composite);
		Control[] buttons = ((Composite) editorContents[1]).getChildren();
		Button saveButton = commentEditor.getSaveButton();
		Button discardOrDoneButton = commentEditor.getDiscardOrDoneButton();
		Button cancelButton = commentEditor.getCancelButton();
		assertEquals(buttons[0], saveButton);
		assertEquals(Messages.CommentPopupDialog_Save, saveButton.getText());
		assertEquals(buttons[1], discardOrDoneButton);
		if (isDraft) {
			assertEquals(Messages.CommentPopupDialog_Discard, discardOrDoneButton.getText());
		} else {
			assertEquals(Messages.CommentPopupDialog_ReplyDone, discardOrDoneButton.getText());
		}
		assertEquals(buttons[2], cancelButton);
		assertEquals(Messages.CommentPopupDialog_Cancel, cancelButton.getText());
	}

	/**
	 * Asserts a save/discard action and determines if comments are correctly formated to be sent
	 *
	 * @param button
	 *            the button that will be pressed to invoke the action
	 * @param description
	 *            the comment text
	 * @param isDraft
	 *            true if the comment is a draft, false if the comment will be a new draft
	 * @param isDiscard
	 *            true if the action is a discard action, false if it is a save action
	 * @throws Exception
	 */
	private void assertSavingComment(Button button, String description, boolean isDraft, boolean isDiscard)
			throws Exception {
		doReturn(Status.OK_STATUS).when(behavior)
				.addComment(any(IReviewItem.class), any(IComment.class), any(IProgressMonitor.class));
		doReturn(Status.OK_STATUS).when(behavior)
				.discardComment(any(IReviewItem.class), any(IComment.class), any(IProgressMonitor.class));

		pressButton(button);

		IJobManager jobManager = Job.getJobManager();
		jobManager.join(InlineCommentSubmitter.JOB_FAMILY, null);

		if (isDiscard) {
			verify(behavior, never()).addComment(any(IReviewItem.class), any(IComment.class),
					any(IProgressMonitor.class));
			verify(behavior, times(1)).discardComment(any(IReviewItem.class),
					argThat(new NewCommentMatcher(description, isDraft)), any(IProgressMonitor.class));
		} else {
			verify(behavior, times(1)).addComment(any(IReviewItem.class),
					argThat(new NewCommentMatcher(description, isDraft)), any(IProgressMonitor.class));
			verify(behavior, never()).discardComment(any(IReviewItem.class), any(IComment.class),
					any(IProgressMonitor.class));
		}
	}

	/**
	 * Simulates a button press
	 *
	 * @param b
	 *            the button that is being pressed
	 */
	private void pressButton(Button b) {
		b.setSelection(true);
		b.notifyListeners(SWT.Selection, new Event());
	}

	/**
	 * Ensures that only one comment editor will be editable for the same item and line
	 *
	 * @param isCommentNavigator
	 *            true if the tested comment is from the next/previous comment button, false if it is from a hover input
	 */
	private void assertDuplicateCommentEditor(boolean isCommentNavigator) {
		editOneComment();
		assertTrue(commentPopupDialog.getEditable());

		secondPopupDialog = createPopupWithXComments(1, isCommentNavigator);
		assertNull(secondPopupDialog.getCommentEditor());
		assertNull(secondPopupDialog.getHelpText());
		assertFalse(secondPopupDialog.getEditable());

		addCommentEditor(secondPopupDialog, retrieveCommentFromUI(1));
		assertNull(secondPopupDialog.getCommentEditor());
		assertFalse(secondPopupDialog.getEditable());
	}

	/**
	 * Adds a comment editor to the popup (note that if the comment provided is not a draft, you will be editing the
	 * last draft in that comment thread)
	 *
	 * @param popup
	 *            the comment dialog popup that will be edited
	 * @param comment
	 *            the provided comment
	 */
	private void addCommentEditor(CommentPopupDialog popup, IComment comment) {
		if (popup.getCommentEditor() != null) {
			popup.getCommentEditor().createControl(popup.getComposite(), comment);
		}
	}

	class NewCommentMatcher extends ArgumentMatcher<IComment> {

		private final String description;

		private final boolean isDraft;

		public NewCommentMatcher(String description, boolean isDraft) {
			this.description = description;
			this.isDraft = isDraft;
		}

		@Override
		public boolean matches(Object argument) {
			if (argument instanceof IComment) {
				IComment argComment = (IComment) argument;
				// For there to be a match, the description of the comments need to be the same and if:
				//	isDraft is true, then it is a draft edit so there must be an ID
				//  isDraft is false, then it is a new draft being created so there is no ID
				return description.equals(argComment.getDescription())
						&& (isDraft ^ StringUtils.isEmpty(argComment.getId()));
			}
			return false;
		}
	}
}
