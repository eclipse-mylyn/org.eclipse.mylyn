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

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivationListener;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;

/**
 * @author Mik Kersten
 */
public class ToggleTaskActivationAction extends Action implements ITaskActivationListener {

	public static final String ID = "org.eclipse.mylyn.tasks.ui.actions.task.activation.toggle"; //$NON-NLS-1$

	private final ITask task;

	/**
	 * @param task
	 *            cannot be null
	 * @param toolBarManager
	 *            cannot be null
	 */
	public ToggleTaskActivationAction(ITask task) {
		Assert.isNotNull(task);
		this.task = task;
		setId(ID);
		setImageDescriptor(TasksUiImages.CONTEXT_ACTIVE_CENTERED);
		update();
		TasksUi.getTaskActivityManager().addActivationListener(this);
	}

	public void dispose() {
		TasksUi.getTaskActivityManager().removeActivationListener(this);
	}

	private void update() {
		setChecked(task.isActive());
		if (task.isActive()) {
			setText(Messages.ToggleTaskActivationAction_Deactivate_Task);
			setToolTipText(Messages.ToggleTaskActivationAction_Deactivate_Task);
		} else {
			setText(Messages.ToggleTaskActivationAction_Activate_Task);
			setToolTipText(Messages.ToggleTaskActivationAction_Activate_Task);
		}
	}

	@Override
	public void run() {
		if (!task.isActive()) {
			TasksUi.getTaskActivityManager().activateTask(task);
		} else {
			TasksUi.getTaskActivityManager().deactivateTask(task);
		}
		update();
	}

	public void taskActivated(ITask task) {
		update();
	}

	public void taskDeactivated(ITask task) {
		update();
	}

	public void preTaskActivated(ITask task) {
		// ignore
	}

	public void preTaskDeactivated(ITask task) {
		// ignore		
	}

}
