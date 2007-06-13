/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * Discard outgoing changes on selected task TODO: Enable multi task discard?
 * 
 * @author Rob Elves
 */
public class ClearOutgoingAction extends Action {

	private static final String ACTION_NAME = "Clear outgoing";

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.mark.discard";

	private List<AbstractTaskContainer> selectedElements;

	public ClearOutgoingAction(List<AbstractTaskContainer> selectedElements) {
		this.selectedElements = selectedElements;
		setText(ACTION_NAME);
		setToolTipText(ACTION_NAME);
		setId(ID);
		if (selectedElements.size() == 1 && (selectedElements.get(0) instanceof AbstractTask)) {
			AbstractTask task = (AbstractTask) selectedElements.get(0);
			setEnabled(task.getSyncState().equals(RepositoryTaskSyncState.OUTGOING));
		} else {
			setEnabled(false);
		}
	}

	@Override
	public void run() {
		ArrayList<AbstractTask> toClear = new ArrayList<AbstractTask>();
		for (Object selectedObject : selectedElements) {
			if (selectedObject instanceof AbstractTask
					&& ((AbstractTask) selectedObject).getSyncState()
							.equals(RepositoryTaskSyncState.OUTGOING)) {
				toClear.add(((AbstractTask) selectedObject));
			}
		}
		if (toClear.size() > 0) {
			boolean confirm = MessageDialog.openConfirm(null, "Confirm discard", "Discard all outgoing changes?\n\n"
					+ (toClear.get(0)).getSummary());
			if (confirm) {
				TasksUiPlugin.getSynchronizationManager().discardOutgoing(toClear.get(0));
			}
		}
	}
}
