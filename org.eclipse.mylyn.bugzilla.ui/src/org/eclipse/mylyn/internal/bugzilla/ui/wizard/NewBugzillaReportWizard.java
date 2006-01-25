/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.bugzilla.ui.wizard;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.ui.OfflineView;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.internal.tasklist.AbstractTaskRepositoryClient;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.TaskCategory;
import org.eclipse.mylar.internal.tasklist.TaskRepository;
import org.eclipse.mylar.internal.tasklist.TaskRepositoryManager;
import org.eclipse.mylar.internal.tasklist.ui.TaskListUiUtil;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;

/**
 * @author Mik Kersten
 */
public class NewBugzillaReportWizard extends AbstractBugWizard {

	/**
	 * The wizard page where the attributes are selected and the bug is
	 * submitted
	 */
	private WizardAttributesPage attributePage;

	private final TaskRepository repository;

	public NewBugzillaReportWizard(TaskRepository repository) {
		this(false, repository);
	}

	public NewBugzillaReportWizard(boolean fromDialog, TaskRepository repository) {
		super(repository);
		this.repository = repository;
		this.fromDialog = fromDialog;
	}

	@Override
	public void addPages() {
		super.addPages();
		addPage(new BugzillaProductPage(workbenchInstance, this, repository));
	}

	@Override
	public boolean canFinish() {
		return attributeCompleted;
	}

	@Override
	protected void saveBugOffline() {
		OfflineView.saveOffline(model, true);
	}

	@Override
	protected AbstractWizardDataPage getWizardDataPage() {
		return attributePage;
	}

	public WizardAttributesPage getAttributePage() {
		return attributePage;
	}

	public void setAttributePage(WizardAttributesPage attributePage) {
		this.attributePage = attributePage;
	}

	@Override
	public boolean performFinish() {
		super.performFinish();
		String bugIdString = this.getId();
		int bugId = -1;
		boolean validId = false;
		try {
			if (bugIdString != null) {
				bugId = Integer.parseInt(bugIdString);
				validId = true;
			} 
		} catch (NumberFormatException nfe) {
			// ignore
		}
		if (!validId) {
			MessageDialog.openError(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG, 
					"Could not create bug id, no valid id");
			return false;
		} 

		// TaskRepository repository =
		// MylarTaskListPlugin.getRepositoryManager().getDefaultRepository(BugzillaPlugin.REPOSITORY_KIND);
		BugzillaTask newTask = new BugzillaTask(TaskRepositoryManager.getHandle(repository.getUrl().toExternalForm(),
				bugId), "<bugzilla info>", true, true);
		Object selectedObject = null;
		if (TaskListView.getDefault() != null)
			selectedObject = ((IStructuredSelection) TaskListView.getDefault().getViewer().getSelection())
					.getFirstElement();

		AbstractTaskRepositoryClient repositoryClient = MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(BugzillaPlugin.REPOSITORY_KIND);
		if (repositoryClient != null) {
			repositoryClient.addTaskToArchive(newTask);
		}
		
//		ITaskHandler taskHandler = MylarTaskListPlugin.getDefault().getHandlerForElement(newTask);
//		if (taskHandler != null) {
//			ITask addedTask = taskHandler.addTaskToArchive(newTask);
//			if (addedTask instanceof BugzillaTask) {
//				BugzillaTask newTask2 = (BugzillaTask) addedTask;
//				if (newTask2 != newTask) {
//					newTask = newTask2;
//				}
//			}
//		}

		if (selectedObject instanceof TaskCategory) {
			MylarTaskListPlugin.getTaskListManager().moveToCategory(((TaskCategory) selectedObject), newTask);
			// ((TaskCategory)selectedObject).addTask(newTask);
		} else {
			MylarTaskListPlugin.getTaskListManager().moveToRoot(newTask);
		}
		
		AbstractTaskRepositoryClient client = MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(BugzillaPlugin.REPOSITORY_KIND);
		client.addTaskToArchive(newTask);
//		BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().addToBugzillaTaskArchive((BugzillaTask) newTask);
		
//		newTask.openTaskInEditor(false);
		TaskListUiUtil.openEditor(newTask);

		if (!newTask.isBugDownloaded())
			newTask.scheduleDownloadReport();

		if (TaskListView.getDefault() != null) {
			TaskListView.getDefault().getViewer().setSelection(new StructuredSelection(newTask));
			TaskListView.getDefault().getViewer().refresh();
		}

		return true;
	}
}
