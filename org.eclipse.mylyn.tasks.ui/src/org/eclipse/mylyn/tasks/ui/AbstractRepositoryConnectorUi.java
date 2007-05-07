/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.tasks.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.core.WebTask;
import org.eclipse.mylar.internal.tasks.ui.TaskListPreferenceConstants;
import org.eclipse.mylar.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylar.internal.tasks.ui.wizards.CommonAddExistingTaskWizard;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskListElement;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.core.TaskRepositoryManager;
import org.eclipse.mylar.tasks.core.Task.PriorityLevel;
import org.eclipse.mylar.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * TODO: refactor wizards into extension points
 * 
 * @author Mik Kersten
 * @author Eugene Kuleshov
 */
public abstract class AbstractRepositoryConnectorUi {

	private static final String LABEL_TASK_DEFAULT = "Task";

	private boolean customNotificationHandling = false;

	/**
	 * @return the unique type of the repository, e.g. "bugzilla"
	 */
	public abstract String getRepositoryType();

	public abstract AbstractRepositorySettingsPage getSettingsPage();

	/**
	 * @param repository
	 * @param queryToEdit
	 *            can be null
	 */
	public abstract IWizard getQueryWizard(TaskRepository repository, AbstractRepositoryQuery queryToEdit);

	public abstract IWizard getNewTaskWizard(TaskRepository taskRepository);

	public abstract boolean hasRichEditor();

	/**
	 * Override to return a custom task editor ID. If overriding this method the
	 * connector becomes responsible for showing the additional pages handled by
	 * the default task editor. As of Mylar 2.0M2 these are the Planning and
	 * Context pages.
	 */
	public String getTaskEditorId(AbstractRepositoryTask repositoryTask) {
		return TaskListPreferenceConstants.TASK_EDITOR_ID;
	}

	public abstract boolean hasSearchPage();

	/**
	 * @param repositoryTask
	 *            can be null
	 */
	public String getTaskKindLabel(AbstractRepositoryTask repositoryTask) {
		return LABEL_TASK_DEFAULT;
	}
	
	/**
	 * @param taskData
	 *            can be null
	 */
	public String getTaskKindLabel(RepositoryTaskData taskData) {
		return LABEL_TASK_DEFAULT;
	}
	
	/**
	 * Connector-specific task icons.  
	 * Not recommended to override unless providing custom icons and kind overlays.
	 * 
	 * For connectors that have a decorator that they want to reuse, the connector can 
	 * maintain a reference to the label provider and get the descriptor from the images it returns.
	 */
	public ImageDescriptor getTaskListElementIcon(ITaskListElement element) {
		if (element instanceof AbstractRepositoryQuery) {
			return TasksUiImages.QUERY;
		} else if (element instanceof AbstractQueryHit || element instanceof ITask) {
			return TasksUiImages.TASK;
		} else {
			return null;
		}
	}

	/**
	 * Task kind overlay, recommended to override with connector-specific overlay.
	 */
	public ImageDescriptor getTaskKindOverlay(AbstractRepositoryTask task) {
		if (!hasRichEditor() || task instanceof WebTask) {
			return TasksUiImages.OVERLAY_WEB;
		}
		return null;
	}
	
	/**
	 * Connector-specific priority icons.  Not recommended to override since priority
	 * icons are used elsewhere in the Task List UI (e.g. filter selection in view menu).
	 */
	public ImageDescriptor getTaskPriorityOverlay(AbstractRepositoryTask task) {
		return TasksUiImages.getImageDescriptorForPriority(PriorityLevel.fromString(task.getPriority()));
	}

	public void openEditQueryDialog(AbstractRepositoryQuery query) {
		try {
			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(query.getRepositoryKind(),
					query.getRepositoryUrl());
			if (repository == null)
				return;

			IWizard wizard = this.getQueryWizard(repository, query);

			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			if (wizard != null && shell != null && !shell.isDisposed()) {
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.create();
				dialog.setTitle("Edit Repository Query");
				dialog.setBlockOnOpen(true);
				if (dialog.open() == Dialog.CANCEL) {
					dialog.close();
					return;
				}
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, e.getMessage(), true);
		}
	}

	public IWizard getAddExistingTaskWizard(TaskRepository repository) {
		return new CommonAddExistingTaskWizard(repository);
	}

	public WizardPage getSearchPage(TaskRepository repository, IStructuredSelection selection) {
		return null;
	}

	/**
	 * Only override if task should be opened by a custom editor, default
	 * behavior is to open with a rich editor, falling back to the web browser
	 * if not available.
	 * 
	 * @return true if the task was successfully opened
	 */
	public boolean openRepositoryTask(String repositoryUrl, String id) {
		TaskRepositoryManager repositoryManager = TasksUiPlugin.getRepositoryManager();
		AbstractRepositoryConnector connector = repositoryManager.getRepositoryConnector(getRepositoryType());
		String taskUrl = connector.getTaskWebUrl(repositoryUrl, id);
		if (taskUrl == null) {
			return false;
		}

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			if (windows != null && windows.length > 0) {
				window = windows[0];
			}
		}
		if (window == null) {
			return false;
		}
		IWorkbenchPage page = window.getActivePage();

		OpenRepositoryTaskJob job = new OpenRepositoryTaskJob(getRepositoryType(), repositoryUrl, id, taskUrl, page);
		job.schedule();

		return true;
	}

	public IHyperlink[] findHyperlinks(TaskRepository repository, String text, int lineOffset, int regionOffset) {
		return null;
	}

	public void setCustomNotificationHandling(boolean customNotifications) {
		this.customNotificationHandling = customNotifications;
	}

	public boolean hasCustomNotificationHandling() {
		return customNotificationHandling;
	}

	public boolean handlesDueDates(AbstractRepositoryTask task) {
		return false;
	}

	public String getKindLabel(String kindLabel) {
		return null;
	}
}
