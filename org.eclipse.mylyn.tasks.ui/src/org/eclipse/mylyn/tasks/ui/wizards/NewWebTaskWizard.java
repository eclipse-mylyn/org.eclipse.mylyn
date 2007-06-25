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

package org.eclipse.mylyn.tasks.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewWebTaskPage;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Wizard for creating new tickets through a web browser.
 * 
 * @author Eugene Kuleshov
 * @author Mik Kersten
 * @author Steffen Pingel
 * @since 2.0
 */
public class NewWebTaskWizard extends Wizard implements INewWizard {

	protected TaskRepository taskRepository;

	protected String newTaskUrl;

	public NewWebTaskWizard(TaskRepository taskRepository, String newTaskUrl) {
		this.taskRepository = taskRepository;
		this.newTaskUrl = newTaskUrl;

		setWindowTitle("New Task");
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public void addPages() {
		addPage(new NewWebTaskPage());
	}

	@Override
	public boolean canFinish() {
		return true;
	}

	@Override
	public boolean performFinish() {
		TasksUiUtil.openUrl(newTaskUrl, false);
		return true;
	}

}
