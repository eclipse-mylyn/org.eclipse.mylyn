/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;

/**
 * Discard outgoing changes on selected task TODO: Enable multi task discard?
 * 
 * @author Rob Elves
 */
public class ClearOutgoingAction extends Action {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.mark.discard"; //$NON-NLS-1$

	private final List<IRepositoryElement> selectedElements;

	private AbstractTaskEditorPage taskEditorPage;

	public ClearOutgoingAction(List<IRepositoryElement> selectedElements) {
		this.selectedElements = selectedElements;
		setText(Messages.ClearOutgoingAction_Clear_outgoing);
		setToolTipText(Messages.ClearOutgoingAction_Clear_outgoing);
		setImageDescriptor(CommonImages.CLEAR);
		setId(ID);
		if (selectedElements.size() == 1 && (selectedElements.get(0) instanceof ITask)) {
			ITask task = (ITask) selectedElements.get(0);
			setEnabled(hasOutgoingChanges(task));
		} else {
			setEnabled(false);
		}
	}

	public AbstractTaskEditorPage getTaskEditorPage() {
		return taskEditorPage;
	}

	public void setTaskEditorPage(AbstractTaskEditorPage taskEditorPage) {
		this.taskEditorPage = taskEditorPage;
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
			boolean confirm = MessageDialog.openConfirm(
					null,
					Messages.ClearOutgoingAction_Confirm_discard, Messages.ClearOutgoingAction_Discard_all_outgoing_changes_ + "\n\n" //$NON-NLS-1$
					+ task.getSummary());
			if (confirm) {
				if (taskEditorPage != null) {
					taskEditorPage.doSave(null);
				}
				try {
					TasksUi.getTaskDataManager().discardEdits(task);
				} catch (CoreException e) {
					TasksUiInternal.displayStatus(
							Messages.ClearOutgoingAction_Clear_outgoing_failed, e.getStatus());
				}
			}
		}
	}
}
