/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui.actions;

import org.eclipse.jdt.internal.junit.model.TestCaseElement;
import org.eclipse.jdt.internal.junit.model.TestElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewTaskWizard;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * Creates a new task from the selected JUnit Test.
 * 
 * @author Frank Becker
 */
public class NewTaskFromJunitResultViewAction implements IViewActionDelegate, ISelectionChangedListener {

	public static final String ID = "org.eclipse.mylyn.bugzilla.actions.newTaskFromJunitResultView";

	private String traceString;

	private TestCaseElement testCaseElement;

	public void run(IAction action) {
		if (traceString == null || testCaseElement == null) {
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
			String description = "\n-- Failure Log from JUnit --\nClass: " + testCaseElement.getTestClassName()
					+ "\nMethod: " + testCaseElement.getTestMethodName() + "\nActual: " + testCaseElement.getActual()
					+ "\nExpected: " + testCaseElement.getExpected() + "\nStack Trace:\n" + traceString;

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

	public void selectionChanged(SelectionChangedEvent event) {
		// ignore		
	}

	public void init(IViewPart view) {
		// ignore
	}

	public void selectionChanged(IAction action, ISelection selection) {
		traceString = null;
		testCaseElement = null;
		if (selection instanceof TreeSelection) {
			TreeSelection t = (TreeSelection) selection;
			TestElement testElement = (TestElement) t.getFirstElement();
			if (testElement instanceof TestCaseElement) {
				testCaseElement = (TestCaseElement) testElement;
				traceString = testCaseElement.getTrace();
			}
		}
		action.setEnabled(traceString != null);
	}
}
