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
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaRepositoryConnector;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.internal.tasklist.ui.TaskUiUtil;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskCategory;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;

/**
 * @author Mik Kersten
 */
public class NewBugzillaReportWizard extends AbstractBugzillaReportWizard {
 	
	private static final String TITLE = "New Bugzilla Task";

	/**
	 * The wizard page where the attributes are selected and the bug is
	 * submitted
	 */
	private WizardAttributesPage attributePage;

	private final TaskRepository repository;

	public NewBugzillaReportWizard(TaskRepository repository) {
		this(false, repository);
		super.setWindowTitle(TITLE);
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
		BugzillaRepositoryConnector client = (BugzillaRepositoryConnector)MylarTaskListPlugin.getRepositoryManager().getRepositoryConnector(BugzillaPlugin.REPOSITORY_KIND);
		client.saveOffline(model, true);
	}

	@Override
	protected AbstractBugzillaWizardPage getWizardDataPage() {
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
		if (super.performFinish()) {
			String bugIdString = this.getId();
			int bugId = -1;
			// boolean validId = false;
			try {
				if (bugIdString != null) {
					bugId = Integer.parseInt(bugIdString);
					// validId = true;
				}
			} catch (NumberFormatException nfe) {
				MessageDialog.openError(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG,
						"Could not create bug id, no valid id");
				return false;
			}
			// if (!validId) {
			// MessageDialog.openError(null,
			// IBugzillaConstants.TITLE_MESSAGE_DIALOG,
			// "Could not create bug id, no valid id");
			// return false;
			// }

			BugzillaTask newTask = new BugzillaTask(AbstractRepositoryTask.getHandle(repository.getUrl(), bugId),
					"<bugzilla info>", true);
			Object selectedObject = null;
			if (TaskListView.getDefault() != null)
				selectedObject = ((IStructuredSelection) TaskListView.getDefault().getViewer().getSelection())
						.getFirstElement();

			// MylarTaskListPlugin.getTaskListManager().getTaskList().addTask(newTask);

			if (selectedObject instanceof TaskCategory) {
				MylarTaskListPlugin.getTaskListManager().getTaskList()
						.addTask(newTask, ((TaskCategory) selectedObject));
			} else {
				MylarTaskListPlugin.getTaskListManager().getTaskList().addTask(newTask,
						MylarTaskListPlugin.getTaskListManager().getTaskList().getRootCategory());
			}

			AbstractRepositoryConnector client = MylarTaskListPlugin.getRepositoryManager().getRepositoryConnector(
					BugzillaPlugin.REPOSITORY_KIND);
			// client.addTaskToArchive(newTask);
			TaskUiUtil.openEditor(newTask, true);

			if (!newTask.isDownloaded()) {
				client.synchronize(newTask, true, null);
			}

			return true;
		}
		return false;
	}
}
