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
import org.eclipse.mylar.internal.tasks.ui.TaskListPreferenceConstants;
import org.eclipse.mylar.internal.tasks.ui.TaskUiUtil;
import org.eclipse.mylar.internal.tasks.ui.editors.NewBugEditorInput;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * Wizard for creating new Trac tickets through a rich editor..
 * 
 * @author Steffen Pingel
 */
public class NewTracTaskWizard extends Wizard implements INewWizard {

	private TaskRepository taskRepository;

	private NewTracTaskPage newTaskPage;

	public NewTracTaskWizard(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;

		newTaskPage = new NewTracTaskPage(taskRepository);
		
		setWindowTitle("New Repository Task");
		setDefaultPageImageDescriptor(TaskListImages.BANNER_REPOSITORY);
		
		setNeedsProgressMonitor(true);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public void addPages() {
		addPage(newTaskPage);
	}

	@Override
	public boolean canFinish() {
		return true;
	}

	@Override
	public boolean performFinish() {
		NewBugEditorInput editorInput = new NewBugEditorInput(taskRepository, newTaskPage.getRepositoryTaskData());
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		TaskUiUtil.openEditor(editorInput, TaskListPreferenceConstants.TASK_EDITOR_ID, page);
		return true;
	}

}
