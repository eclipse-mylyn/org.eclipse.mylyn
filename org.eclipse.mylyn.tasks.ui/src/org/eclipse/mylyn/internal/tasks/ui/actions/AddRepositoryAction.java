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

import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.core.MylarStatusHandler;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.TaskCommandIds;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * @author Mik Kersten
 */
public class AddRepositoryAction extends Action {

	public static final String TITLE = "Add Task Repository";

	private static final String ID = "org.eclipse.mylyn.tasklist.repositories.add";

	public AddRepositoryAction() {
		setImageDescriptor(TasksUiImages.REPOSITORY_NEW);
		setText(TITLE);
		setId(ID);
		setEnabled(TasksUiPlugin.getRepositoryManager().hasUserManagedRepositoryConnectors());
	}

	@Override
	public void run() {
		IHandlerService handlerSvc = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
		try {
			handlerSvc.executeCommand(TaskCommandIds.ADD_TASK_REPOSITORY, null);
		} catch (Exception e) {
			MylarStatusHandler.fail(e, e.getMessage(), true);
		}
	}

}
