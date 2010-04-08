/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoriesView;
import org.eclipse.mylyn.internal.tasks.ui.wizards.EditRepositoryWizard;
import org.eclipse.mylyn.internal.tasks.ui.wizards.MultiRepositoryAwareWizard;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewLocalTaskWizard;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.TaskRepositoryWizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @since 2.0
 * @author Mik Kersten
 * @author Steffen Pingel
 * @author Shawn Minto
 */
public class TasksUiUtil {

	/**
	 * Flag that is passed along to the workbench browser support when a task is opened in a browser because no rich
	 * editor was available.
	 * 
	 * @see #openTask(String)
	 */
	public static final int FLAG_NO_RICH_EDITOR = 1 << 17;

	/**
	 * @since 3.0
	 */
	public static ITask createOutgoingNewTask(String connectorKind, String repositoryUrl) {
		Assert.isNotNull(connectorKind);
		LocalTask task = TasksUiInternal.createNewLocalTask(null);
		task.setAttribute(ITasksCoreConstants.ATTRIBUTE_OUTGOING_NEW_CONNECTOR_KIND, connectorKind);
		task.setAttribute(ITasksCoreConstants.ATTRIBUTE_OUTGOING_NEW_REPOSITORY_URL, repositoryUrl);
		task.setSynchronizationState(SynchronizationState.OUTGOING_NEW);
		return task;
	}

	/**
	 * @since 3.0
	 */
	public static boolean isOutgoingNewTask(ITask task, String connectorKind) {
		Assert.isNotNull(task);
		Assert.isNotNull(connectorKind);
		return connectorKind.equals(task.getAttribute(ITasksCoreConstants.ATTRIBUTE_OUTGOING_NEW_CONNECTOR_KIND));
	}

	/**
	 * @since 3.1
	 */
	public static TaskRepository getOutgoingNewTaskRepository(ITask task) {
		Assert.isNotNull(task);
		String connectorKind = task.getAttribute(ITasksCoreConstants.ATTRIBUTE_OUTGOING_NEW_CONNECTOR_KIND);
		String repositoryUrl = task.getAttribute(ITasksCoreConstants.ATTRIBUTE_OUTGOING_NEW_REPOSITORY_URL);
		if (connectorKind != null && repositoryUrl != null) {
			return TasksUi.getRepositoryManager().getRepository(connectorKind, repositoryUrl);
		}
		return null;
	}

	public static TaskRepository getSelectedRepository() {
		return getSelectedRepository(null);
	}

	/**
	 * Will use the workbench window's selection if viewer's selection is null
	 */
	public static TaskRepository getSelectedRepository(StructuredViewer viewer) {
		IStructuredSelection selection = null;
		if (viewer != null) {
			selection = (IStructuredSelection) viewer.getSelection();
		}
		if (selection == null || selection.isEmpty()) {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			ISelection windowSelection = window.getSelectionService().getSelection();
			if (windowSelection instanceof IStructuredSelection) {
				selection = (IStructuredSelection) windowSelection;
			}
		}

		if (selection == null) {
			return null;
		}

		Object element = selection.getFirstElement();
		if (element instanceof TaskRepository) {
			return (TaskRepository) selection.getFirstElement();
		} else if (element instanceof IRepositoryQuery) {
			IRepositoryQuery query = (IRepositoryQuery) element;
			return TasksUi.getRepositoryManager().getRepository(query.getConnectorKind(), query.getRepositoryUrl());
		} else if (element instanceof ITask) {
			ITask task = (ITask) element;
			return TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(), task.getRepositoryUrl());
		} else if (element instanceof IResource) {
			IResource resource = (IResource) element;
			return TasksUiPlugin.getDefault().getRepositoryForResource(resource);
		} else if (element instanceof IAdaptable) {
			IAdaptable adaptable = (IAdaptable) element;
			IResource resource = (IResource) adaptable.getAdapter(IResource.class);
			if (resource != null) {
				return TasksUiPlugin.getDefault().getRepositoryForResource(resource);
			} else {
				ITask task = (ITask) adaptable.getAdapter(AbstractTask.class);
				if (task != null) {
					ITask rtask = task;
					return TasksUi.getRepositoryManager().getRepository(rtask.getConnectorKind(),
							rtask.getRepositoryUrl());
				}
			}
		}

		// TODO mapping between LogEntry.pliginId and repositories
		// TODO handle other selection types
		return null;
	}

	public static IEditorPart openEditor(IEditorInput input, String editorId, IWorkbenchPage page) {
		if (page == null) {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window != null) {
				page = window.getActivePage();
			}
		}
		if (page == null) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Unable to open editor for \"" + input //$NON-NLS-1$
					+ "\": no active workbench window")); //$NON-NLS-1$
			return null;
		}
		try {
			return page.openEditor(input, editorId);
		} catch (PartInitException e) {
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Open for editor failed: " + input //$NON-NLS-1$
					+ ", taskId: " + editorId, e)); //$NON-NLS-1$
		}
		return null;
	}

	public static int openEditRepositoryWizard(TaskRepository repository) {
		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				repository.getConnectorKind());
		if (connector == null
				|| (!connector.isUserManaged() && !connector.getConnectorKind().equals(
						LocalRepositoryConnector.CONNECTOR_KIND))) {
			return Window.CANCEL;
		}

		try {
			EditRepositoryWizard wizard = new EditRepositoryWizard(repository);
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			if (shell != null && !shell.isDisposed()) {
				WizardDialog dialog = new TaskRepositoryWizardDialog(shell, wizard);
				dialog.create();
				dialog.setBlockOnOpen(true);
				if (dialog.open() == Window.CANCEL) {
					dialog.close();
					return Window.CANCEL;
				}
			}

			if (TaskRepositoriesView.getFromActivePerspective() != null) {
				TaskRepositoriesView.getFromActivePerspective().getViewer().refresh();
			}
		} catch (Exception e) {
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, e.getMessage(), e));
		}
		return Window.OK;
	}

	/**
	 * @since 3.0
	 */
	public static boolean openNewLocalTaskEditor(Shell shell, ITaskMapping taskSelection) {
		return openNewTaskEditor(shell, new NewLocalTaskWizard(taskSelection), taskSelection);
	}

	private static boolean openNewTaskEditor(Shell shell, IWizard wizard, ITaskMapping taskSelection) {
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.setBlockOnOpen(true);

		// make sure the wizard has created its pages
		dialog.create();
		if (!(wizard instanceof MultiRepositoryAwareWizard) && wizard.canFinish()) {
			wizard.performFinish();
			return true;
		}

		int result = dialog.open();
		return result == Window.OK;
	}

	/**
	 * @since 3.0
	 */
	public static boolean openNewTaskEditor(Shell shell, ITaskMapping taskSelection, TaskRepository taskRepository) {
		final IWizard wizard;
//		List<TaskRepository> repositories = TasksUi.getRepositoryManager().getAllRepositories();
//		if (taskRepository == null && repositories.size() == 1) {
//			// only the Local repository connector is available
//			taskRepository = repositories.get(0);
//		}

		if (taskRepository != null) {
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(taskRepository.getConnectorKind());
			wizard = connectorUi.getNewTaskWizard(taskRepository, taskSelection);
		} else {
			wizard = TasksUiInternal.createNewTaskWizard(taskSelection);
		}

		return openNewTaskEditor(shell, wizard, taskSelection);
	}

	/**
	 * Either pass in a repository and taskId, or fullUrl, or all of them
	 * 
	 * @deprecated Use {@link #openTask(String,String,String)} instead
	 */
	@Deprecated
	public static boolean openRepositoryTask(String repositoryUrl, String taskId, String fullUrl) {
		return openTask(repositoryUrl, taskId, fullUrl);
	}

	/**
	 * @deprecated Use {@link #openTask(TaskRepository,String)} instead
	 */
	@Deprecated
	public static boolean openRepositoryTask(TaskRepository repository, String taskId) {
		return openTask(repository, taskId);
	}

	/**
	 * @since 3.0
	 */
	public static boolean openTask(ITask task) {
		return TasksUiInternal.openTask(task, task.getTaskId()) != null;
	}

	/**
	 * Resolves a rich editor for the task if available.
	 * 
	 * @since 3.0
	 */
	public static void openTask(String url) {
		AbstractTask task = TasksUiInternal.getTaskByUrl(url);
		if (task != null && !(task instanceof LocalTask)) {
			openTask(task);
		} else {
			boolean opened = false;
			if (url != null) {
				AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager()
						.getConnectorForRepositoryTaskUrl(url);
				if (connector != null) {
					String repositoryUrl = connector.getRepositoryUrlFromTaskUrl(url);
					if (repositoryUrl != null) {
						String id = connector.getTaskIdFromTaskUrl(url);
						if (id != null) {
							TaskRepository repository = TasksUi.getRepositoryManager().getRepository(
									connector.getConnectorKind(), repositoryUrl);
							if (repository != null) {
								opened = openTask(repository, id);
							}
						}
					}
				}
			}
			if (!opened) {
				WorkbenchUtil.openUrl(url, 0);
			}
		}
	}

	/**
	 * Either pass in a repository and taskId, or fullUrl, or all of them
	 * 
	 * @since 3.0
	 */
	public static boolean openTask(String repositoryUrl, String taskId, String fullUrl) {
		AbstractTask task = TasksUiInternal.getTask(repositoryUrl, taskId, fullUrl);

		if (task != null) {
			return TasksUiUtil.openTask(task);
		}

		boolean opened = false;

		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getConnectorForRepositoryTaskUrl(
				fullUrl);
		if (connector != null) {
			if (repositoryUrl != null && taskId != null) {
				opened = TasksUiInternal.openRepositoryTask(connector.getConnectorKind(), repositoryUrl, taskId);
			} else {
				repositoryUrl = connector.getRepositoryUrlFromTaskUrl(fullUrl);
				taskId = connector.getTaskIdFromTaskUrl(fullUrl);
				if (repositoryUrl != null && taskId != null) {
					opened = TasksUiInternal.openRepositoryTask(connector.getConnectorKind(), repositoryUrl, taskId);
				}
			}
		}

		if (!opened) {
			TasksUiUtil.openUrl(fullUrl);
		}

		return true;
	}

	/**
	 * @since 3.0
	 */
	public static boolean openTask(TaskRepository repository, String taskId) {
		Assert.isNotNull(repository);
		Assert.isNotNull(taskId);
		return TasksUiInternal.openTask(repository, taskId, null);
	}

	/**
	 * @since 3.0
	 */
	public static void openUrl(String location) {
		WorkbenchUtil.openUrl(location, FLAG_NO_RICH_EDITOR);
	}

	/**
	 * Opens <code>element</code> in a browser using an authenticated URL if available.
	 * 
	 * @since 3.4
	 */
	public static boolean openWithBrowser(IRepositoryElement element) {
		IRepositoryManager repositoryManager = TasksUi.getRepositoryManager();
		TaskRepository repository = null;
		if (element instanceof ITask) {
			repository = repositoryManager.getRepository(((ITask) element).getConnectorKind(),
					((ITask) element).getRepositoryUrl());
		} else if (element instanceof IRepositoryQuery) {
			repository = repositoryManager.getRepository(((IRepositoryQuery) element).getConnectorKind(),
					((IRepositoryQuery) element).getRepositoryUrl());
		}
		return (repository != null) ? openWithBrowser(repository, element) : null;
	}

	/**
	 * Opens <code>element</code> in a browser using an authenticated URL if available.
	 * 
	 * @since 3.4
	 */
	public static boolean openWithBrowser(TaskRepository repository, IRepositoryElement element) {
		String url = TasksUiInternal.getAuthenticatedUrl(repository, element);
		if (url != null) {
			openUrl(url);
			return true;
		}
		return false;
	}

	/**
	 * @since 3.0
	 */
	public static IViewPart openTasksViewInActivePerspective() {
		try {
			return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
					ITasksUiConstants.ID_VIEW_TASKS);
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not show Task List view", e)); //$NON-NLS-1$
			return null;
		}
	}

	/**
	 * Returns if the current line in the task editor should be highlighted.
	 * 
	 * @return true, if line highlighting is enabled
	 * @since 3.4
	 */
	public static boolean getHighlightCurrentLine() {
		return TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(
				ITasksUiPreferenceConstants.EDITOR_CURRENT_LINE_HIGHLIGHT);
	}
}
