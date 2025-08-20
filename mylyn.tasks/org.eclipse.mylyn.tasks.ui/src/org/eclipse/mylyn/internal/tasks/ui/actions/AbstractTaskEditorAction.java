/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Rob Elves
 */
public abstract class AbstractTaskEditorAction extends BaseSelectionListenerAction {

	protected TaskEditor editor;

	public AbstractTaskEditorAction(String text) {
		super(text);
	}

	public void setEditor(TaskEditor taskEditor) {
		editor = taskEditor;
	}

	protected boolean taskDirty(ITask task) {
		return editor != null && editor.isDirty()
				|| task.getSynchronizationState().equals(SynchronizationState.OUTGOING)
				|| task.getSynchronizationState().equals(SynchronizationState.CONFLICT);

	}

	protected void openInformationDialog(String label, String text) {

		Shell shell = null;
		if (editor != null) {
			shell = editor.getSite().getShell();
		}
		MessageDialog.openInformation(shell, label, text);
		if (editor != null) {
			editor.showBusy(false);
		}
		return;
	}

}