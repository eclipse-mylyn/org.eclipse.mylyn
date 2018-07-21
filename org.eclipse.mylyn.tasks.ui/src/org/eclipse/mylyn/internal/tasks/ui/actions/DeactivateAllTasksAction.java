/*******************************************************************************
 * Copyright (c) 2004, 2008 Willian Mitsuda and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Willian Mitsuda - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * @author Willian Mitsuda
 */
public class DeactivateAllTasksAction extends ActionDelegate implements IWorkbenchWindowActionDelegate {

	public void init(IWorkbenchWindow window) {
		// ignore
	}

	@Override
	public void run(IAction action) {
		TasksUi.getTaskActivityManager().deactivateActiveTask();
	}

}
