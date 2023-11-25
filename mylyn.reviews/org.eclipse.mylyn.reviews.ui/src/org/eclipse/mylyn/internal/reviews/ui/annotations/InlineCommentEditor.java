/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.annotations;

import java.util.Collection;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class InlineCommentEditor {

	public enum CommentEditorState {
		VIEW, REPLY, EDIT, DISCARD;
	}

	private static final int MIN_COMMENT_EDITOR_HEIGHT = 100;

	private static MultiValuedMap<String, Integer> editMap = new HashSetValuedHashMap<>();

	private final CommentPopupDialog dialog;

	private final String reviewItemId;

	private final int startLine;

	private CommentEditorState state;

	private Composite editorComposite;

	private Text commentEditorText;

	private IComment currentComment;

	private Button saveButton;

	private Button discardOrDoneButton;

	private Button cancelButton;

	private String originalComment;

	/**
	 * Creates a comment editor for the provided dialog and sets its reviewItemId and startLine. Also initializes the
	 * state of the editor to VIEW.
	 *
	 * @param dialog
	 *            the {@link CommentPopupDialog} that the editor is adding to
	 */
	protected InlineCommentEditor(CommentPopupDialog dialog) {
		this.dialog = dialog;
		reviewItemId = dialog.getReviewItem().getId();
		startLine = dialog.getRange().getStartLine();
		state = CommentEditorState.VIEW;
	}

	/**
	 * Creates the UI for the comment editor
	 *
	 * @param parent
	 *            the parent {@link Composite} of the comment editor
	 * @param clickedComment
	 *            the comment that was clicked on in the dialog
	 */
	protected void createControl(Composite parent, final IComment clickedComment) {
		dialog.hideHelpText();
		addToEditMap(reviewItemId, startLine);

		if (editorComposite == null) {
			editorComposite = new Composite(parent, SWT.NONE);
			editorComposite.setSize(editorComposite.getSize().x, MIN_COMMENT_EDITOR_HEIGHT);
			editorComposite.setBackground(dialog.getToolkit().getColors().getBackground());

			GridData textGridData = new GridData(GridData.FILL, GridData.FILL, true, true);
			commentEditorText = new Text(editorComposite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
			commentEditorText.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					if (saveButton != null) {
						if (StringUtils.isEmpty(((Text) e.widget).getText())) {
							saveButton.setEnabled(false);
							discardOrDoneButton.setEnabled(true);
						} else {
							discardOrDoneButton.setEnabled(currentComment == null || currentComment.isDraft());
							saveButton.setEnabled(true);
						}
					}
				}
			});
			commentEditorText.addKeyListener(createAddSaveKeyListener());
			textGridData.minimumHeight = commentEditorText.getLineHeight() * 4;
			textGridData.verticalSpan = 4;
			commentEditorText.setLayoutData(textGridData);

			Composite buttonContainer = new Composite(editorComposite, SWT.NONE);
			buttonContainer.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
			GridData buttonGridData = new GridData(GridData.FILL, GridData.FILL, true, true);
			buttonGridData.verticalSpan = 1;
			GridLayoutFactory.fillDefaults().spacing(1, 1).numColumns(3).applyTo(buttonContainer);

			saveButton = createButton(buttonContainer, Messages.CommentPopupDialog_Save, buttonGridData,
					new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					saveCommentAction();
				}
			});
			discardOrDoneButton = createButton(buttonContainer, null, buttonGridData, new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (currentComment.isDraft()) {
						setState(CommentEditorState.DISCARD);
					} else {
						commentEditorText.setText(Messages.CommentPopupDialog_Done);
						setState(CommentEditorState.REPLY);
					}
					saveComment();
				}
			});
			cancelButton = createButton(buttonContainer, Messages.CommentPopupDialog_Cancel, buttonGridData,
					new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					removeControl();
				}
			});

			editorComposite.setLayout(new GridLayout(1, false));
			editorComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

			dialog.recomputeSize();
		}

		if (clickedComment != null && currentComment == null) {
			IComment lastDraft = dialog.getLastCommentDraft();
			if (!clickedComment.isDraft() && lastDraft != null) {
				currentComment = lastDraft;
			} else {
				currentComment = clickedComment;
			}

			if (currentComment.isDraft()) {
				commentEditorText.setText(currentComment.getDescription());
				discardOrDoneButton.setText(Messages.CommentPopupDialog_Discard);
			} else {
				commentEditorText.setText(""); //$NON-NLS-1$
				discardOrDoneButton.setText(Messages.CommentPopupDialog_ReplyDone);
			}

			if (originalComment == null) {
				originalComment = commentEditorText.getText();
			}
			commentEditorText.setFocus();
		}
	}

	/**
	 * Removes the comment editor from the UI
	 */
	protected void removeControl() {
		if (commentEditorText != null && StringUtils.isNotEmpty(commentEditorText.getText())) {
			addToEditMap(reviewItemId, startLine);
			originalComment = null;
		}
		if (editorComposite != null) {
			editorComposite.dispose();
		}
		editorComposite = null;
		commentEditorText = null;
		currentComment = null;

		setState(CommentEditorState.VIEW);
		dialog.recomputeSize();
	}

	/**
	 * Force disposes the comment dialog (which will also dispose the comment editor)
	 */
	protected void forceDispose() {
		dialog.dispose(true);
	}

	/**
	 * Checks if the text in the comment editor has changed from the original comment
	 *
	 * @return true if the text in the editor is different than the original comment text, false otherwise
	 */
	protected boolean hasEdits() {
		return editorComposite != null && !editorComposite.isDisposed() && commentEditorText != null
				&& !commentEditorText.getText().equals(originalComment);
	}

	protected Composite getEditorComposite() {
		return editorComposite;
	}

	protected Text getCommentEditorText() {
		return commentEditorText;
	}

	protected Button getSaveButton() {
		return saveButton;
	}

	protected Button getDiscardOrDoneButton() {
		return discardOrDoneButton;
	}

	protected Button getCancelButton() {
		return cancelButton;
	}

	protected IComment getCurrentComment() {
		return currentComment;
	}

	protected CommentEditorState getState() {
		return state;
	}

	protected void setState(CommentEditorState state) {
		this.state = state;
	}

	/**
	 * Adds an entry to the static edit map for a particular review item and start line
	 *
	 * @param reviewItemId
	 *            the id of a {@link IReviewItem}
	 * @param startLine
	 *            the start line number of a {@link LineRange}
	 */
	protected static void addToEditMap(String reviewItemId, int startLine) {
		editMap.put(reviewItemId, startLine);
	}

	/**
	 * Removes an entry from the static edit map for a particular review item and start line
	 *
	 * @param reviewItemId
	 *            the id of a {@link IReviewItem}
	 * @param startLine
	 *            the start line number of a {@link LineRange}
	 */
	protected static void removeFromEditMap(String reviewItemId, int startLine) {
//		editMap.remove(reviewItemId, startLine);
		Collection<Integer> lines = editMap.get(reviewItemId);
		lines.remove(startLine);
	}

	/**
	 * Checks the edit map for an entry for a particular review item and start line
	 *
	 * @param reviewItemId
	 *            the id of a {@link IReviewItem}
	 * @param startLine
	 *            the start line number of a {@link LineRange}
	 * @return true if there is an entry in the edit map, false otherwise
	 */
	protected static boolean canAddCommentEditor(String reviewItemId, int startLine) {
//		return !editMap.containsEntry(reviewItemId, startLine);
		Collection<Integer> comments = editMap.get(reviewItemId);
		if (comments != null) {
			return !comments.contains(startLine);
		}
		return true;
	}

	/**
	 * Helper method to add a {@link Button} to the UI
	 *
	 * @param parent
	 *            the parent {@link Composite} that we want to add the button to
	 * @param text
	 *            the button's text
	 * @param gridData
	 *            the button's layout grid data
	 * @param listener
	 *            the {@link SelectionListener} that will be added to the button
	 * @return a {@link Button} with properties provided by the method's parameters
	 */
	private Button createButton(Composite parent, String text, GridData gridData, SelectionListener listener) {
		Button button = new Button(parent, SWT.PUSH);
		if (text != null) {
			button.setText(text);
		}
		button.setLayoutData(gridData);
		button.addSelectionListener(listener);
		return button;
	}

	/**
	 * Add a key listener for Ctrl/Cmd+Enter/Return to save the comment
	 *
	 * @return the {@link KeyAdapater} that will save the comment on Ctrl/Cmd+Enter/Return
	 */
	private KeyAdapter createAddSaveKeyListener() {
		return new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if ((e.stateMask & SWT.CTRL) != 0 && (e.keyCode == SWT.CR || e.keyCode == SWT.LF)) {
					saveCommentAction();
				}
			}
		};
	}

	/**
	 * Sets the state of the editor based on the current comment's draft status (true if the submitted comment is a
	 * comment edit, false if it is a new comment draft) and invokes a save comment action
	 */
	private void saveCommentAction() {
		if (currentComment.isDraft()) {
			setState(CommentEditorState.EDIT);
		} else {
			setState(CommentEditorState.REPLY);
		}
		saveComment();
	}

	/**
	 * Creates a {@link InlineCommentSubmitter} to submit a comment
	 */
	private void saveComment() {
		InlineCommentSubmitter submitter = new InlineCommentSubmitter(dialog.getReviewItem(), dialog.getRange(),
				dialog.getAnnotationInput(), this);
		submitter.saveComment();
	}
}
