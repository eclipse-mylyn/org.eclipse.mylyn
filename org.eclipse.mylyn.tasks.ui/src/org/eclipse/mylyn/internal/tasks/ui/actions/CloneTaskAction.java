/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.wizards.CloneTaskWizard;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Maarten Meijer
 * @author Mik Kersten
 */
public class CloneTaskAction extends BaseSelectionListenerAction implements IViewActionDelegate {
	
	private static final String LABEL = "Clone This Task";

	private static final String ID = "org.eclipse.mylyn.tasklist.actions.clone";

	protected ISelection selection;
	
	public CloneTaskAction() {
		super(LABEL);
		setId(ID);
		setImageDescriptor(TasksUiImages.TASK_NEW);

		setAccelerator(SWT.MOD1 + 'd');
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			super.selectionChanged((IStructuredSelection)selection);
		}
	}
	
	public void run(IAction action) {
		run();
	}
	
	@Override
	public void run() {
		try {
			for (Object selectedObject : getStructuredSelection().toList()) {
				if (selectedObject instanceof AbstractTask) { 
					AbstractTask task = (AbstractTask) selectedObject;

					CloneTaskWizard wizard = new CloneTaskWizard(task);

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
						String description = task.getUrl() + "\n" + task.getSummary();

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

						AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(task.getConnectorKind());
						String taskKindLabel;
						if (connectorUi != null) {
							taskKindLabel = connectorUi.getTaskKindLabel(task);
						} else {
							taskKindLabel = "task";
						}
							
						editor.setSummaryText(task.getSummary() + " (cloned)");
						editor.setDescriptionText("Clone of " + taskKindLabel + " " + CopyTaskDetailsAction.getTextForTask(task));
						
						// TODO: add all settable AbstractTask fields here
					} else {
						// ignore
					}
				}
			}
		} catch (NullPointerException npe) {
			StatusHandler.fail(npe, "Could not remove task from category, it may still be refreshing.", true);
		}
	}

	public void init(IViewPart view) {
		// ignore
	}

}