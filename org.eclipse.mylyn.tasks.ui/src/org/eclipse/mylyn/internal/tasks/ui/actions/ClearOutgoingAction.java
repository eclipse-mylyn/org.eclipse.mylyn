/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.provisional.workbench.ui.CommonImages;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;

/**
 * Discard outgoing changes on selected task TODO: Enable multi task discard?
 * 
 * @author Rob Elves
 */
public class ClearOutgoingAction extends Action {

	private static final String ACTION_NAME = "Clear outgoing";

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.mark.discard";

	private final List<AbstractTaskContainer> selectedElements;

	public ClearOutgoingAction(List<AbstractTaskContainer> selectedElements) {
		this.selectedElements = selectedElements;
		setText(ACTION_NAME);
		setToolTipText(ACTION_NAME);
		setImageDescriptor(CommonImages.CLEAR);
		setId(ID);
		if (selectedElements.size() == 1 && (selectedElements.get(0) instanceof AbstractTask)) {
			AbstractTask task = (AbstractTask) selectedElements.get(0);
			setEnabled(hasOutgoingChanges(task));
		} else {
			setEnabled(false);
		}
	}

	private boolean hasOutgoingChanges(AbstractTask task) {
		return task.getSynchronizationState().equals(RepositoryTaskSyncState.OUTGOING)
				|| task.getSynchronizationState().equals(RepositoryTaskSyncState.CONFLICT);
	}

	@Override
	public void run() {
		ArrayList<AbstractTask> toClear = new ArrayList<AbstractTask>();
		for (Object selectedObject : selectedElements) {
			if (selectedObject instanceof AbstractTask && hasOutgoingChanges((AbstractTask) selectedObject)) {
				toClear.add(((AbstractTask) selectedObject));
			}
		}
		if (toClear.size() > 0) {
			AbstractTask task = toClear.get(0);
			boolean confirm = MessageDialog.openConfirm(null, "Confirm discard", "Discard all outgoing changes?\n\n"
					+ task.getSummary());
			if (confirm) {
				TasksUiPlugin.getTaskDataManager().discardOutgoing(task);
				try {
					TasksUiPlugin.getTaskDataManager().discardEdits(task, task.getConnectorKind());
				} catch (CoreException e) {
					StatusHandler.displayStatus("Clear outgoing failed", e.getStatus());
				}
			}
		}
	}
}
