/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ide.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewTaskWizard;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.pde.internal.runtime.logview.LogEntry;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * Creates a new task from the selected error log entry.
 * 
 * @author Jeff Pound
 */
public class NewTaskFromErrorAction implements IViewActionDelegate, ISelectionChangedListener {

	public static final String ID = "org.eclipse.mylyn.tasklist.ui.repositories.actions.create";

	private TreeViewer treeViewer;

	public void run() {
		TreeItem[] items = treeViewer.getTree().getSelection();
		LogEntry selection = null;
		if (items.length > 0) {
			selection = (LogEntry) items[0].getData();
		}
		if (selection == null) {
			return;
		}
		NewTaskWizard wizard = new NewTaskWizard();

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		if (shell != null && !shell.isDisposed()) {
			WizardDialog dialog = new WizardDialog(shell, wizard);
			dialog.setBlockOnOpen(true);
			if (dialog.open() == WizardDialog.CANCEL) {
				return;
			}

			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			AbstractRepositoryTaskEditor editor = null;

			String summary = "";
//				selection.getSeverityText() + ": \"" + selection.getMessage() + "\" in "
//					+ selection.getPluginId();
			String description = "\n\n-- Error Log --\nDate: " + selection.getDate() + "\nMessage: "
					+ selection.getMessage() + "\nSeverity: " + selection.getSeverityText() + "\nPlugin ID: "
					+ selection.getPluginId() + "\nStack Trace:\n"
					+ ((selection.getStack() == null) ? "no stack trace available" : selection.getStack());

			try {
				TaskEditor taskEditor = (TaskEditor) page.getActiveEditor();
				editor = (AbstractRepositoryTaskEditor) taskEditor.getActivePageInstance();
			} catch (ClassCastException e) {
				Clipboard clipboard = new Clipboard(page.getWorkbenchWindow().getShell().getDisplay());
				clipboard.setContents(new Object[] { summary + "\n" + description },
						new Transfer[] { TextTransfer.getInstance() });

				MessageDialog.openInformation(
						page.getWorkbenchWindow().getShell(),
						ITasksUiConstants.TITLE_DIALOG,
						"This connector does not provide a rich task editor for creating tasks.\n\n"
								+ "The error contents have been placed in the clipboard so that you can paste them into the entry form.");
				return;
			}

			editor.setSummaryText(summary);
			editor.setDescriptionText(description);

		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// this selection is always empty? explicitly register a listener in
		// init() instead
	}

	public void init(IViewPart view) {
		ISelectionProvider sp = view.getViewSite().getSelectionProvider();
		sp.addSelectionChangedListener(this);
		sp.setSelection(sp.getSelection());
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(SelectionChangedEvent event) {
		treeViewer = (TreeViewer) event.getSource();
	}
}
