/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.TaskSelection;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class NewLocalTaskWizard extends Wizard implements INewWizard {

	private final TaskSelection taskSelection;

	public NewLocalTaskWizard(TaskSelection taskSelection) {
		this.taskSelection = taskSelection;
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
		setNeedsProgressMonitor(true);
	}

	public NewLocalTaskWizard() {
		this(null);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setForcePreviousAndNextButtons(false);
	}

	@Override
	public void addPages() {
		// ignore
	}

	@Override
	public boolean canFinish() {
		return true;
	}

	@Override
	public boolean performFinish() {
		LocalTask task = TasksUiInternal.createNewLocalTask(null);
		if (taskSelection != null) {
			task.setSummary(taskSelection.getTaskData().getSummary());
			task.setNotes(taskSelection.getTaskData().getDescription());
		}
		if (task != null) {
			TasksUiUtil.openEditor(task, true);
			return true;
		} else {
			return false;
		}
	}

}
