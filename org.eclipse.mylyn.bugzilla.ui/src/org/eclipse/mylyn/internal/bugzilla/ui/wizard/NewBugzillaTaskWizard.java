/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.bugzilla.ui.wizard;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttributeFactory;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskSelection;
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

	private TaskSelection taskSelection;

	// TODO: Change taskData to a RepositoryTaskData
	// protected RepositoryTaskData taskData;

	public NewBugzillaTaskWizard(TaskRepository repository) {
		this(false, repository);
		taskData = new RepositoryTaskData(new BugzillaAttributeFactory(), BugzillaCorePlugin.REPOSITORY_KIND,
				repository.getRepositoryUrl(), TasksUiPlugin.getDefault().getNextNewRepositoryTaskId());
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

	/**
	 * @since 2.2
	 */
	public NewBugzillaTaskWizard(TaskRepository taskRepository, TaskSelection taskSelection) {
		this(taskRepository);
		this.taskSelection = taskSelection;
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

			if (taskSelection != null) {
				AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
						repository.getConnectorKind());
				AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
				if (taskDataHandler != null) {
					taskDataHandler.cloneTaskData(taskSelection.getTaskData(), taskData);
				}
			}

			NewTaskEditorInput editorInput = new NewTaskEditorInput(repository, taskData);
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			TasksUiUtil.openEditor(editorInput, TaskEditor.ID_EDITOR, page);
			return true;
		} catch (Exception e) {
			productPage.applyToStatusLine(new Status(IStatus.ERROR, "not_used", 0,
					"Problem occurred retrieving repository configuration from " + repository.getRepositoryUrl(), null));
		}
		return false;
	}
}
