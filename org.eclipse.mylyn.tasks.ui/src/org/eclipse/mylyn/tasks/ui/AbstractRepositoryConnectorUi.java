/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.ui.OpenRepositoryTaskJob;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.wizards.CommonAddExistingTaskWizard;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.ITaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskSelection;
import org.eclipse.mylyn.tasks.core.AbstractTask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.data.ITaskComment;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentModel;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylyn.tasks.ui.wizards.TaskAttachmentPage;
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
	 * 		can be null
	 */
	public abstract IWizard getQueryWizard(TaskRepository repository, AbstractRepositoryQuery queryToEdit);

	/**
	 * @since 2.2
	 */
	public abstract IWizard getNewTaskWizard(TaskRepository taskRepository, TaskSelection selection);

	/**
	 * @deprecated use {@link #getNewTaskWizard(TaskRepository, TaskSelection)} instead
	 */
	@Deprecated
	public IWizard getNewTaskWizard(TaskRepository taskRepository) {
		return null;
	}

	/**
	 * Override to return a custom task editor ID. If overriding this method the connector becomes responsible for
	 * showing the additional pages handled by the default task editor. As of Mylyn 2.0M2 these are the Planning and
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
	 * 		can be null
	 */
	public String getTaskKindLabel(AbstractTask task) {
		return LABEL_TASK_DEFAULT;
	}

	/**
	 * @deprecated use {@link #getTaskKindLabel(AbstractTask)} instead
	 */
	@Deprecated
	public String getTaskKindLabel(RepositoryTaskData taskData) {
		return LABEL_TASK_DEFAULT;
	}

	/**
	 * Connector-specific task icons. Not recommended to override unless providing custom icons and kind overlays.
	 * 
	 * For connectors that have a decorator that they want to reuse, the connector can maintain a reference to the label
	 * provider and get the descriptor from the images it returns.
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
	 * Connector-specific priority icons. Not recommended to override since priority icons are used elsewhere in the
	 * Task List UI (e.g. filter selection in view menu).
	 */
	public ImageDescriptor getTaskPriorityOverlay(AbstractTask task) {
		return TasksUiImages.getImageDescriptorForPriority(PriorityLevel.fromString(task.getPriority()));
	}

	public void openEditQueryDialog(AbstractRepositoryQuery query) {
		try {
			TaskRepository repository = TasksUi.getRepositoryManager().getRepository(query.getConnectorKind(),
					query.getRepositoryUrl());
			if (repository == null) {
				return;
			}

			IWizard wizard = this.getQueryWizard(repository, query);

			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			if (wizard != null && shell != null && !shell.isDisposed()) {
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.create();
				dialog.setTitle("Edit Repository Query");
				dialog.setBlockOnOpen(true);
				if (dialog.open() == Window.CANCEL) {
					dialog.close();
					return;
				}
			}
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Failed to open query dialog", e));
		}
	}

	public IWizard getAddExistingTaskWizard(TaskRepository repository) {
		return new CommonAddExistingTaskWizard(repository);
	}

	public WizardPage getSearchPage(TaskRepository repository, IStructuredSelection selection) {
		return null;
	}

	/**
	 * Override to return a URL that provides the user with an account creation page for the repository
	 * 
	 * @param taskRepository
	 * 		TODO
	 */
	public String getAccountCreationUrl(TaskRepository taskRepository) {
		return null;
	}

	/**
	 * Override to return a URL that provides the user with an account management page for the repository
	 * 
	 * @param taskRepository
	 * 		TODO
	 */
	public String getAccountManagementUrl(TaskRepository taskRepository) {
		return null;
	}

	/**
	 * Override to return a URL that provides the user with a history page for the task
	 * 
	 * @return a url of a page for the history of the task; null, if no history url is available
	 * @since 3.0
	 */
	public String getTaskHistoryUrl(TaskRepository taskRepository, AbstractTask task) {
		return null;
	}

	/**
	 * Override to return a textual reference to a comment, e.g. for Bugzilla this method returns <code>#12</code> for
	 * comment 12. This reference is used when generating replies to comments
	 * 
	 * @return a reference to <code>comment</code>; null, if no reference is available
	 * @since 3.0
	 */
	public String getReply(TaskRepository taskRepository, AbstractTask task, ITaskComment taskComment,
			boolean includeTask) {
		return null;
	}

	/**
	 * Only override if task should be opened by a custom editor, default behavior is to open with a rich editor,
	 * falling back to the web browser if not available.
	 * 
	 * @return true if the task was successfully opened
	 */
	// API 3.0 review, move to tasks ui
	public boolean openRepositoryTask(String repositoryUrl, String id) {
		ITaskRepositoryManager repositoryManager = TasksUi.getRepositoryManager();
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

	/**
	 * @since 2.1
	 * @return true if connector doesn't support non-grouping (flattening) of subtasks
	 */
	public boolean forceSubtaskHierarchy() {
		return false;
	}

	/**
	 * @since 3.0
	 */
	public IWizardPage getAttachmentPage(TaskAttachmentModel model) {
		return new TaskAttachmentPage(model);
	}
}
