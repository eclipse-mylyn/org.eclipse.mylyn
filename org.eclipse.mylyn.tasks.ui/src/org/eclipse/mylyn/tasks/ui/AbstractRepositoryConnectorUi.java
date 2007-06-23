/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.ui.OpenRepositoryTaskJob;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.wizards.CommonAddExistingTaskWizard;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.AbstractTask.PriorityLevel;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Extend to provide connector-specific UI extensions.
 * 
 * TODO: consider refactoring into extension points
 * 
 * @author Mik Kersten
 * @author Eugene Kuleshov
 * @since 2.0
 */
public abstract class AbstractRepositoryConnectorUi {

	private static final String LABEL_TASK_DEFAULT = "Task";

	private boolean customNotificationHandling = false;

	/**
	 * @return the unique type of the repository, e.g. "bugzilla"
	 */
	public abstract String getConnectorKind();

	public abstract AbstractRepositorySettingsPage getSettingsPage();

	/**
	 * @param repository
	 * @param queryToEdit
	 *            can be null
	 */
	public abstract IWizard getQueryWizard(TaskRepository repository, AbstractRepositoryQuery queryToEdit);

	public abstract IWizard getNewTaskWizard(TaskRepository taskRepository);

	/**
	 * Override to return a custom task editor ID. If overriding this method the
	 * connector becomes responsible for showing the additional pages handled by
	 * the default task editor. As of Mylar 2.0M2 these are the Planning and
	 * Context pages.
	 */
	public String getTaskEditorId(AbstractTask repositoryTask) {
		return TaskEditor.ID_EDITOR;
	}

	public abstract boolean hasSearchPage();

	/**
	 * Contributions to the UI legend.
	 */
	public List<AbstractTaskContainer> getLegendItems() {
		return Collections.emptyList();
	}
	
	/**
	 * @param repositoryTask
	 *            can be null
	 */
	public String getTaskKindLabel(AbstractTask repositoryTask) {
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
	public ImageDescriptor getTaskListElementIcon(AbstractTaskContainer element) {
		if (element instanceof AbstractRepositoryQuery) {
			return TasksUiImages.QUERY;
		} else if (element instanceof AbstractTask) {
			return TasksUiImages.TASK;
		} else {
			return null;
		}
	}

	/**
	 * Task kind overlay, recommended to override with connector-specific overlay.
	 */
	public ImageDescriptor getTaskKindOverlay(AbstractTask task) {
		return null;
	}
	
	/**
	 * Connector-specific priority icons.  Not recommended to override since priority
	 * icons are used elsewhere in the Task List UI (e.g. filter selection in view menu).
	 */
	public ImageDescriptor getTaskPriorityOverlay(AbstractTask task) {
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
			StatusHandler.fail(e, e.getMessage(), true);
		}
	}

	public IWizard getAddExistingTaskWizard(TaskRepository repository) {
		return new CommonAddExistingTaskWizard(repository);
	}

	public WizardPage getSearchPage(TaskRepository repository, IStructuredSelection selection) {
		return null;
	}
	
	/**
	 * Override to return a URL that provides the user with an 
	 * account creation page for the repository
	 * @param taskRepository TODO
	 */
	public String getAccountCreationUrl(TaskRepository taskRepository) {
		return null;
	}

	/**
	 * Override to return a URL that provides the user with an 
	 * account management page for the repository
	 * @param taskRepository TODO
	 */
	public String getAccountManagementUrl(TaskRepository taskRepository) {
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
		AbstractRepositoryConnector connector = repositoryManager.getRepositoryConnector(getConnectorKind());
		String taskUrl = connector.getTaskUrl(repositoryUrl, id);
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

		OpenRepositoryTaskJob job = new OpenRepositoryTaskJob(getConnectorKind(), repositoryUrl, id, taskUrl, page);
		job.schedule();

		return true;
	}
	
	public IHyperlink[] findHyperlinks(TaskRepository repository, String text, int lineOffset, int regionOffset) {
		return null;
	}

	public void setCustomNotificationHandling(boolean customNotifications) {
		this.customNotificationHandling = customNotifications;
	}

	public boolean isCustomNotificationHandling() {
		return customNotificationHandling;
	}

	public boolean supportsDueDates(AbstractTask task) {
		return false;
	}

	public String getKindLabel(String kindLabel) {
		return null;
	}
}
