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

package org.eclipse.mylar.internal.tasks.ui.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylar.internal.tasks.ui.TaskListImages;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.RepositoryAttachment;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.PlatformUI;

/**
 * @author Rob Elves
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class ContextRetrieveWizard extends Wizard {

	public static final String TITLE = "Task Repository";

	public static final String WIZARD_TITLE = "Retrieve context";

	private final TaskRepository repository;

	private final AbstractRepositoryTask task;

	private ContextRetrieveWizardPage wizardPage;

	public ContextRetrieveWizard(AbstractRepositoryTask task) {
		repository = TasksUiPlugin.getRepositoryManager().getRepository(task.getRepositoryKind(),
				task.getRepositoryUrl());
		this.task = task;
		setWindowTitle(TITLE);
		setDefaultPageImageDescriptor(TaskListImages.BANNER_REPOSITORY_CONTEXT);
	}

	@Override
	public void addPages() {
		wizardPage = new ContextRetrieveWizardPage(repository, task);
		addPage(wizardPage);
		super.addPages();
	}

	@Override
	public final boolean performFinish() {

		RepositoryAttachment delegate = wizardPage.getSelectedContext();
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				this.repository.getKind());
		try {
			boolean wasActive = false;
			if (task.isActive()) {
				wasActive = true;
				TasksUiPlugin.getTaskListManager().deactivateTask(task);
			}
			try {
				if (!connector.retrieveContext(repository, task, delegate, TasksUiPlugin.getDefault().getDataDirectory())) {
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							ITasksUiConstants.TITLE_DIALOG, AbstractRepositoryConnector.MESSAGE_ATTACHMENTS_NOT_SUPPORTED
									+ connector.getLabel());
				} else {
					TasksUiPlugin.getTaskListManager().getTaskList().notifyLocalInfoChanged(task);
				}
			} finally {
				if (wasActive) {
					TasksUiPlugin.getTaskListManager().activateTask(task);
				}
			}
		} catch (CoreException e) {
			ErrorDialog.openError(null, ITasksUiConstants.TITLE_DIALOG, "Retrieval of task context FAILED.", e.getStatus());
			MylarStatusHandler.log(e.getStatus());
			return false;
		}
		return true;
	}

}
