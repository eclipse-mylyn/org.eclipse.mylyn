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
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * Discard outgoing changes on selected task TODO: Enable multi task discard?
 * 
 * @author Rob Elves
 */
public class ClearOutgoingAction extends Action {

	private static final String ACTION_NAME = "Clear outgoing";

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.mark.discard";

	private final List<IRepositoryElement> selectedElements;

	public ClearOutgoingAction(List<IRepositoryElement> selectedElements) {
		this.selectedElements = selectedElements;
		setText(ACTION_NAME);
		setToolTipText(ACTION_NAME);
		setImageDescriptor(CommonImages.CLEAR);
		setId(ID);
		if (selectedElements.size() == 1 && (selectedElements.get(0) instanceof ITask)) {
			ITask task = (ITask) selectedElements.get(0);
			setEnabled(hasOutgoingChanges(task));
		} else {
			setEnabled(false);
		}
	}

	public static boolean hasOutgoingChanges(ITask task) {
		return task.getSynchronizationState().equals(SynchronizationState.OUTGOING)
				|| task.getSynchronizationState().equals(SynchronizationState.CONFLICT);
	}

	@Override
	public void run() {
		ArrayList<AbstractTask> toClear = new ArrayList<AbstractTask>();
		for (Object selectedObject : selectedElements) {
			if (selectedObject instanceof ITask && hasOutgoingChanges((ITask) selectedObject)) {
				toClear.add(((AbstractTask) selectedObject));
			}
		}
		if (toClear.size() > 0) {
			AbstractTask task = toClear.get(0);
			boolean confirm = MessageDialog.openConfirm(null, "Confirm discard", "Discard all outgoing changes?\n\n"
					+ task.getSummary());
			if (confirm) {
				if (task.getClass() != TaskTask.class) {
					TasksUiPlugin.getTaskDataManager().discardOutgoing(task);
				} else {
					try {
						TasksUi.getTaskDataManager().discardEdits(task);
					} catch (CoreException e) {
						TasksUiInternal.displayStatus("Clear outgoing failed", e.getStatus());
					}
				}
			}
		}
	}
}
