/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class SynchronizeEditorAction extends BaseSelectionListenerAction {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.synchronize.editor"; //$NON-NLS-1$

	public SynchronizeEditorAction() {
		super(Messages.SynchronizeEditorAction_Synchronize);
		setToolTipText(Messages.SynchronizeEditorAction_Synchronize_Incoming_Changes);
		setId(ID);
		setImageDescriptor(CommonImages.REFRESH_SMALL);
		// setAccelerator(SWT.MOD1 + 'r');
	}

	@Override
	public void run() {
		IStructuredSelection selection = getStructuredSelection();
		if (selection == null) {
			return;
		}
		Object selectedObject = selection.getFirstElement();
		if (!(selectedObject instanceof TaskEditor)) {
			return;
		}

		final TaskEditor editor = (TaskEditor) selectedObject;
		final ITask task = editor.getTaskEditorInput().getTask();
		if (task == null) {
			return;
		}

		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				task.getConnectorKind());
		if (connector == null) {
			return;
		}

		TasksUiInternal.synchronizeTask(connector, task, true, new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						try {
							editor.refreshPages();
						} finally {
							if (editor != null) {
								editor.showBusy(false);
							}
						}
					}
				});
			}
		});
		if (editor != null) {
			editor.showBusy(true);
		}
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		Object selectedObject = selection.getFirstElement();
		if (selectedObject instanceof TaskEditor) {
			TaskEditor editor = (TaskEditor) selectedObject;
			ITask task = editor.getTaskEditorInput().getTask();
			return !(task instanceof LocalTask);
		}
		return false;
	}

}
