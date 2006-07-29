/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.mylar.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.themes.IThemeManager;

/**
 * @author Rob Elves
 */
public class TaskFormPage extends FormPage {

	protected boolean isDirty;

	protected TaskEditorActionContributor actionContributor;

	protected List<TextViewer> textViewers = new ArrayList<TextViewer>();

	private void addTextViewer(TextViewer viewer) {
		textViewers.add(viewer);
	}

	public TaskFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	public boolean canDoAction(String actionId) {
		Control focusControl = getFocusControl();
		if (focusControl instanceof StyledText) {
			StyledText text = (StyledText) focusControl;
			for (TextViewer viewer : textViewers) {
				if (viewer.getTextWidget() == text) {
					return canDoGlobalAction(actionId, viewer);
				}
			}
		}
		return false;
	}

	public void doAction(String actionId) {
		Control focusControl = getFocusControl();
		if (focusControl instanceof StyledText) {
			StyledText text = (StyledText) focusControl;
			for (TextViewer viewer : textViewers) {
				if (viewer.getTextWidget() == text) {
					doGlobalAction(actionId, viewer);
					return;
				}
			}
		}
	}

	protected Control getFocusControl() {
		IManagedForm form = getManagedForm();
		if (form == null)
			return null;
		Control control = form.getForm();
		if (control == null || control.isDisposed())
			return null;
		Display display = control.getDisplay();
		Control focusControl = display.getFocusControl();
		if (focusControl == null || focusControl.isDisposed())
			return null;
		return focusControl;
	}

	private boolean doGlobalAction(String actionId, TextViewer textViewer) {
		if (actionId.equals(ActionFactory.CUT.getId())) {
			textViewer.doOperation(ITextOperationTarget.CUT);
			return true;
		} else if (actionId.equals(ActionFactory.COPY.getId())) {
			textViewer.doOperation(ITextOperationTarget.COPY);
			return true;
		} else if (actionId.equals(ActionFactory.PASTE.getId())) {
			textViewer.doOperation(ITextOperationTarget.PASTE);
			return true;
		} else if (actionId.equals(ActionFactory.DELETE.getId())) {
			textViewer.doOperation(ITextOperationTarget.DELETE);
			return true;
		} else if (actionId.equals(ActionFactory.UNDO.getId())) {
			textViewer.doOperation(ITextOperationTarget.UNDO);
			return true;
		} else if (actionId.equals(ActionFactory.REDO.getId())) {
			textViewer.doOperation(ITextOperationTarget.REDO);
			return true;
		} else if (actionId.equals(ActionFactory.SELECT_ALL.getId())) {
			textViewer.doOperation(ITextOperationTarget.SELECT_ALL);
			return true;
		}
		return false;
	}

	private boolean canDoGlobalAction(String actionId, TextViewer textViewer) {
		if (actionId.equals(ActionFactory.CUT.getId())) {
			return textViewer.canDoOperation(ITextOperationTarget.CUT);
		} else if (actionId.equals(ActionFactory.COPY.getId())) {
			return textViewer.canDoOperation(ITextOperationTarget.COPY);
		} else if (actionId.equals(ActionFactory.PASTE.getId())) {
			return textViewer.canDoOperation(ITextOperationTarget.PASTE);
		} else if (actionId.equals(ActionFactory.DELETE.getId())) {
			return textViewer.canDoOperation(ITextOperationTarget.DELETE);
		} else if (actionId.equals(ActionFactory.UNDO.getId())) {
			return textViewer.canDoOperation(ITextOperationTarget.UNDO);
		} else if (actionId.equals(ActionFactory.REDO.getId())) {
			return textViewer.canDoOperation(ITextOperationTarget.REDO);
		} else if (actionId.equals(ActionFactory.SELECT_ALL.getId())) {
			return textViewer.canDoOperation(ITextOperationTarget.SELECT_ALL);
		}
		return false;
	}

	protected TextViewer addRepositoryTextViewer(TaskRepository repository, Composite composite, String text, int style) {

		if (actionContributor == null) {
			actionContributor = ((MylarTaskEditor) getEditor()).getContributor();
		}

		final RepositoryTextViewer commentViewer = new RepositoryTextViewer(repository, composite, style);

		IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();

		commentViewer.getTextWidget().setFont(
				themeManager.getCurrentTheme().getFontRegistry().get(TaskListColorsAndFonts.TASK_EDITOR_FONT));

		commentViewer.addSelectionChangedListener(actionContributor);

		commentViewer.getTextWidget().addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {

				actionContributor.registerGlobalHandlers(getEditorSite().getActionBars());

				actionContributor.updateSelectableActions(commentViewer.getSelection());

			}

			public void focusLost(FocusEvent e) {
				commentViewer.setSelectedRange(0, 0);
				actionContributor.unregisterGlobalHandlers(getEditorSite().getActionBars());

			}
		});

		commentViewer.addTextListener(new ITextListener() {
			public void textChanged(TextEvent event) {
				actionContributor.updateSelectableActions(commentViewer.getSelection());

			}
		});

		commentViewer.setEditable(false);
		commentViewer.getTextWidget().setMenu(getManagedForm().getForm().getMenu());
		commentViewer.setDocument(new Document(text));
		addTextViewer(commentViewer);
		return commentViewer;
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	public void markDirty(boolean dirty) {
		isDirty = dirty;
		getManagedForm().dirtyStateChanged();
		return;
	}
}
