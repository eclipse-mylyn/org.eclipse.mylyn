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

package org.eclipse.mylar.internal.trac.ui.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.internal.tasks.ui.TaskListImages;
import org.eclipse.mylar.internal.tasks.ui.TaskUiUtil;
import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Wizard used to create new Trac tickets through a web browser.
 * 
 * @author Steffen Pingel
 */
public class NewTracTaskWizard extends Wizard implements INewWizard {

	private final TaskRepository taskRepository;

	public NewTracTaskWizard(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;

		setWindowTitle("New Trac Task");
		setDefaultPageImageDescriptor(TaskListImages.BANNER_REPOSITORY);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public void addPages() {
		addPage(new NewTracTaskPage());
	}

	@Override
	public boolean canFinish() {
		return true;
	}

	@Override
	public boolean performFinish() {
		TaskUiUtil.openUrl(taskRepository.getUrl() + ITracClient.NEW_TICKET_URL);
		return true;
	}

}
