/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Chris Aniszczyk <zx@us.ibm.com> - bug 208819
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ide.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewTaskWizard;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.views.log.LogEntry;

/**
 * Creates a new task from the selected error log entry.
 * 
 * @author Jeff Pound
 */
public class NewTaskFromErrorAction implements IObjectActionDelegate, IViewActionDelegate, ISelectionChangedListener {

	public static final String ID = "org.eclipse.mylyn.tasklist.ui.repositories.actions.create";

	private LogEntry entry;

	private TreeViewer treeViewer;

	public void run() {
		if (entry == null) {
			// TODO: remove if we can use an object contribution
			TreeItem[] items = treeViewer.getTree().getSelection();
			if (items.length > 0) {
				entry = (LogEntry) items[0].getData();
			}
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
			String description = "\n\n-- Error Log --\nDate: " + entry.getDate() + "\nMessage: " + entry.getMessage()
					+ "\nSeverity: " + entry.getSeverityText() + "\nPlugin ID: " + entry.getPluginId()
					+ "\nStack Trace:\n" + ((entry.getStack() == null) ? "no stack trace available" : entry.getStack());

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

	public void run(IAction action) {
		run();
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
		Object object = ((IStructuredSelection)selection).getFirstElement();
		if (object instanceof LogEntry) {
			entry = (LogEntry) object;
		}
	}

	public void init(IViewPart view) {
		ISelectionProvider sp = view.getViewSite().getSelectionProvider();
		sp.addSelectionChangedListener(this);
		sp.setSelection(sp.getSelection());
	}

	public void selectionChanged(SelectionChangedEvent event) {
		treeViewer = (TreeViewer) event.getSource();
	}

}
