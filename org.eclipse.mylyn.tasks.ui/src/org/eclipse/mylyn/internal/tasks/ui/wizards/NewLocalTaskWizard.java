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

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class NewLocalTaskWizard extends Wizard implements INewWizard {

	private final ITaskMapping taskSelection;

	public NewLocalTaskWizard(ITaskMapping taskSelection) {
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
			if (taskSelection.getSummary() != null) {
				task.setSummary(taskSelection.getSummary());
			}
			if (taskSelection.getDescription() != null) {
				task.setNotes(taskSelection.getDescription());
			}
		}
		TasksUiUtil.openTask(task);
		return true;
	}

}
