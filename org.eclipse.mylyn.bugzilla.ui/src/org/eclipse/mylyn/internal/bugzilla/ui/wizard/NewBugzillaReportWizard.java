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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.internal.bugzilla.ui.OfflineView;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.internal.tasklist.ITaskHandler;
import org.eclipse.mylar.internal.tasklist.TaskCategory;
import org.eclipse.mylar.internal.tasklist.TaskRepositoryManager;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.TaskRepository;

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
		String bugIdString = this.getId();
		int bugId = -1;
		try {
			if (bugIdString != null) {
				bugId = Integer.parseInt(bugIdString);
			} else {
				return false;
			}
		} catch (NumberFormatException nfe) {
			// TODO handle error
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

		ITaskHandler taskHandler = MylarTaskListPlugin.getDefault().getHandlerForElement(newTask);
		if (taskHandler != null) {
			ITask addedTask = taskHandler.addTaskToRegistry(newTask);
			if (addedTask instanceof BugzillaTask) {
				BugzillaTask newTask2 = (BugzillaTask) addedTask;
				if (newTask2 != newTask) {
					newTask = newTask2;
				}
			}
		}

		if (selectedObject instanceof TaskCategory) {
			MylarTaskListPlugin.getTaskListManager().moveToCategory(((TaskCategory) selectedObject), newTask);
			// ((TaskCategory)selectedObject).addTask(newTask);
		} else {
			MylarTaskListPlugin.getTaskListManager().moveToRoot(newTask);
		}
		BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().addToBugzillaTaskRegistry((BugzillaTask) newTask);
		newTask.openTaskInEditor(false);

		if (!newTask.isBugDownloaded())
			newTask.scheduleDownloadReport();

		if (TaskListView.getDefault() != null) {
			TaskListView.getDefault().getViewer().setSelection(new StructuredSelection(newTask));
			TaskListView.getDefault().getViewer().refresh();
		}

		return true;
	}
}
