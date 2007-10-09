/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
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
		this.editor = taskEditor;
	}

	protected boolean taskDirty(AbstractTask task) {
		return (((editor != null && editor.isDirty())
				|| task.getSynchronizationState().equals(RepositoryTaskSyncState.OUTGOING) || task.getSynchronizationState()
				.equals(RepositoryTaskSyncState.CONFLICT)));

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