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
package org.eclipse.mylyn.internal.bugzilla.ui.wizard;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttributeFactory;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.NewTaskEditorInput;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class NewBugzillaTaskWizard extends Wizard implements INewWizard {

	private static final String TITLE = "New Bugzilla Task";

	private IWorkbench workbenchInstance;

	private final TaskRepository repository;

	private final BugzillaProductPage productPage;

	/**
	 * Flag to indicate if the wizard can be completed (finish button enabled)
	 */
	protected boolean completed = false;

	/** The taskData used to store all of the data for the wizard */
	protected RepositoryTaskData taskData;

	// TODO: Change taskData to a RepositoryTaskData
	// protected RepositoryTaskData taskData;

	public NewBugzillaTaskWizard(TaskRepository repository) {
		this(false, repository);
		taskData = new RepositoryTaskData(new BugzillaAttributeFactory(), BugzillaCorePlugin.REPOSITORY_KIND,
				repository.getUrl(), TasksUiPlugin.getDefault().getNextNewRepositoryTaskId());
		taskData.setNew(true);
		super.setDefaultPageImageDescriptor(BugzillaUiPlugin.imageDescriptorFromPlugin(
				"org.eclipse.mylyn.internal.bugzilla.ui", "icons/wizban/bug-wizard.gif"));
		super.setWindowTitle(TITLE);
		setNeedsProgressMonitor(true);
	}

	public NewBugzillaTaskWizard(boolean fromDialog, TaskRepository repository) {
		super();
		this.repository = repository;
		this.productPage = new BugzillaProductPage(workbenchInstance, this, repository);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbenchInstance = workbench;
	}

	@Override
	public void addPages() {
		super.addPages();
		addPage(productPage);
	}

	@Override
	public boolean canFinish() {
		return completed;
	}

	@Override
	public boolean performFinish() {

		try {
			productPage.saveDataToModel();
			NewTaskEditorInput editorInput = new NewTaskEditorInput(repository, taskData);
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			TasksUiUtil.openEditor(editorInput, TaskEditor.ID_EDITOR, page);
			return true;
		} catch (Exception e) {
			productPage.applyToStatusLine(new Status(IStatus.ERROR, "not_used", 0,
					"Problem occurred retrieving repository configuration from " + repository.getUrl(), null));
		}
		return false;
	}

}

// @Override
// protected void saveBugOffline() {
// // AbstractRepositoryConnector client = (AbstractRepositoryConnector)
// // MylarTaskListPlugin.getRepositoryManager()
// // .getRepositoryConnector(BugzillaPlugin.REPOSITORY_KIND);
// // client.saveOffline(taskData);
// }
//
// @Override
// protected AbstractBugzillaWizardPage getWizardDataPage() {
// return null;
// }

// Open new bug editor

// if (super.performFinish()) {
//
// String bugIdString = this.getId();
// int bugId = -1;
// // boolean validId = false;
// try {
// if (bugIdString != null) {
// bugId = Integer.parseInt(bugIdString);
// // validId = true;
// }
// } catch (NumberFormatException nfe) {
// MessageDialog.openError(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG,
// "Could not create bug taskId, no valid taskId");
// return false;
// }
// // if (!validId) {
// // MessageDialog.openError(null,
// // IBugzillaConstants.TITLE_MESSAGE_DIALOG,
// // "Could not create bug taskId, no valid taskId");
// // return false;
// // }
//
// BugzillaTask newTask = new
// BugzillaTask(AbstractTask.getHandle(repository.getUrl(), bugId),
// "<bugzilla info>", true);
// Object selectedObject = null;
// if (TaskListView.getFromActivePerspective() != null)
// selectedObject = ((IStructuredSelection)
// TaskListView.getFromActivePerspective().getViewer()
// .getSelection()).getFirstElement();
//
// // MylarTaskListPlugin.getTaskListManager().getTaskList().addTask(newTask);
//
// if (selectedObject instanceof TaskCategory) {
// MylarTaskListPlugin.getTaskListManager().getTaskList()
// .addTask(newTask, ((TaskCategory) selectedObject));
// } else {
// MylarTaskListPlugin.getTaskListManager().getTaskList().addTask(newTask,
// MylarTaskListPlugin.getTaskListManager().getTaskList().getRootCategory());
// }
//
// TasksUiUtil.refreshAndOpenTaskListElement(newTask);
// MylarTaskListPlugin.getSynchronizationManager().synchNow(0);
//
// return true;
// }
