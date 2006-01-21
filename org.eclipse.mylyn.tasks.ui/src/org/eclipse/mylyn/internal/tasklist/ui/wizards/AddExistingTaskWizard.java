/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasklist.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.TaskCategory;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskRepositoryClient;
import org.eclipse.ui.IWorkbench;

/**
 * @author Mik Kersten
 */
public class AddExistingTaskWizard extends AbstractRepositoryWizard {

	private ExistingTaskWizardPage existingTaskWizardPage = new ExistingTaskWizardPage();
	
	public AddExistingTaskWizard() {
		super();
		init();
	}

	@Override
	public boolean performFinish() {
		String handle = existingTaskWizardPage.getTaskId();
		ITaskRepositoryClient client = MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(super.repository.getKind());
		ITask newTask = client.createTaskFromExistingId(super.repository, handle);
		
		if (newTask != null && TaskListView.getDefault() != null) {
			Object selectedObject = ((IStructuredSelection) TaskListView.getDefault().getViewer().getSelection()).getFirstElement();

			if (selectedObject instanceof TaskCategory) {
				MylarTaskListPlugin.getTaskListManager().moveToCategory(((TaskCategory) selectedObject), newTask);
			} else {
				MylarTaskListPlugin.getTaskListManager().moveToRoot(newTask);
			}
			if (TaskListView.getDefault() != null) {
				TaskListView.getDefault().getViewer().setSelection(new StructuredSelection(newTask));
			}
		}
		
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	private void init() {
		super.setForcePreviousAndNextButtons(true);
	}

	@Override
	public void addPages() {
		super.addPages();
		addPage(existingTaskWizardPage);
	}

	@Override
	public boolean canFinish() {
		return super.canFinish() && existingTaskWizardPage.isPageComplete();
	}
}
