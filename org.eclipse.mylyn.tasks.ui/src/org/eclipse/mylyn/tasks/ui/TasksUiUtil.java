/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiMessages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoriesView;
import org.eclipse.mylyn.internal.tasks.ui.wizards.EditRepositoryWizard;
import org.eclipse.mylyn.internal.tasks.ui.wizards.MultiRepositoryAwareWizard;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewLocalTaskWizard;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.browser.WebBrowserPreference;
import org.eclipse.ui.internal.browser.WorkbenchBrowserSupport;

/**
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

	private static final String ATTRIBUTE_OUTGOING_NEW_CONNECTOR_KIND = "outgoingNewConnectorKind";

	/**
	 * @since 3.0
	 */
	public static ITask createOutgoingNewTask(String connectorKind) {
		Assert.isNotNull(connectorKind);
		LocalTask task = TasksUiInternal.createNewLocalTask(null);
		task.setAttribute(ATTRIBUTE_OUTGOING_NEW_CONNECTOR_KIND, connectorKind);
		task.setSynchronizationState(SynchronizationState.OUTGOING_NEW);
		return task;
	}

	/**
	 * @since 3.0
	 */
	public static boolean isOutgoingNewTask(ITask task, String connectorKind) {
		Assert.isNotNull(task);
		Assert.isNotNull(connectorKind);
		return connectorKind.equals(task.getAttribute(ATTRIBUTE_OUTGOING_NEW_CONNECTOR_KIND));
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

	private static String getTaskEditorId(final ITask task) {
		String taskEditorId = TaskEditor.ID_EDITOR;
		if (task != null) {
			ITask repositoryTask = task;
			AbstractRepositoryConnectorUi repositoryUi = TasksUiPlugin.getConnectorUi(repositoryTask.getConnectorKind());
			String customTaskEditorId = repositoryUi.getTaskEditorId(repositoryTask);
			if (customTaskEditorId != null) {
				taskEditorId = customTaskEditorId;
			}
		}
		return taskEditorId;
	}

	public static IEditorPart openEditor(IEditorInput input, String editorId, IWorkbenchPage page) {
		try {
			return page.openEditor(input, editorId);
		} catch (PartInitException e) {
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Open for editor failed: " + input
					+ ", taskId: " + editorId, e));
		}
		return null;
	}

	public static int openEditRepositoryWizard(TaskRepository repository) {
		try {
			EditRepositoryWizard wizard = new EditRepositoryWizard(repository);
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			if (shell != null && !shell.isDisposed()) {
				WizardDialog dialog = new WizardDialog(shell, wizard);
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
		List<TaskRepository> repositories = TasksUi.getRepositoryManager().getAllRepositories();
		if (taskRepository == null && repositories.size() == 1) {
			// only the Local repository connector is available
			taskRepository = repositories.get(0);
		}

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
		Assert.isNotNull(task);
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			boolean openWithBrowser = !TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(
					ITasksUiPreferenceConstants.EDITOR_TASKS_RICH);
			if (openWithBrowser) {
				openUrl(task.getUrl());
				return true;
			} else {
				TaskRepository taskRepository = TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(),
						task.getRepositoryUrl());
				IEditorInput editorInput = new TaskEditorInput(taskRepository, task);
				boolean wasOpen = refreshEditorContentsIfOpen(task, editorInput);
				if (wasOpen) {
					synchronizeTask(task);
					return true;
				} else {
					IWorkbenchPage page = window.getActivePage();
					IEditorPart editor = openEditor(editorInput, getTaskEditorId(task), page);
					if (editor != null) {
						synchronizeTask(task);
						return true;
					}
				}
			}
		} else {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Unable to open editor for \""
					+ task.getSummary() + "\": no active workbench window"));
		}
		return false;
	}

	private static void synchronizeTask(ITask task) {
		if (task instanceof LocalTask) {
			return;
		}

		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				task.getConnectorKind());
		TasksUiInternal.synchronizeTask(connector, task, false, null);
	}

	/**
	 * Resolves a rich editor for the task if available.
	 * 
	 * @since 3.0
	 */
	public static void openTask(String url) {
		AbstractTask task = TasksUiUtil.getTaskByUrl(url);
		if (task != null && !(task instanceof LocalTask)) {
			openTask(task);
		} else {
			boolean opened = false;
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager()
					.getConnectorForRepositoryTaskUrl(url);
			if (connector != null) {
				String repositoryUrl = connector.getRepositoryUrlFromTaskUrl(url);
				String id = connector.getTaskIdFromTaskUrl(url);
				TaskRepository repository = TasksUi.getRepositoryManager().getRepository(connector.getConnectorKind(),
						repositoryUrl);
				opened = openTask(repository, id);
			}
			if (!opened) {
				openUrl(url, 0);
			}
		}
	}

	/**
	 * Either pass in a repository and taskId, or fullUrl, or all of them
	 * 
	 * @since 3.0
	 */
	public static boolean openTask(String repositoryUrl, String taskId, String fullUrl) {
		AbstractTask task = null;
		if (repositoryUrl != null && taskId != null) {
			task = (AbstractTask) TasksUiInternal.getTaskList().getTask(repositoryUrl, taskId);
		}
		if (task == null && fullUrl != null) {
			task = TasksUiUtil.getTaskByUrl(fullUrl);
		}
		if (task == null && repositoryUrl != null && taskId != null) {
			task = TasksUiPlugin.getTaskList().getTaskByKey(repositoryUrl, taskId);
		}

		if (task != null) {
			return TasksUiUtil.openTask(task);
		}

		boolean opened = false;

		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getConnectorForRepositoryTaskUrl(
				fullUrl);
		if (connector != null) {
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(connector.getConnectorKind());
			if (repositoryUrl != null && taskId != null) {
				opened = TasksUiInternal.openRepositoryTask(connectorUi.getConnectorKind(), repositoryUrl, taskId);
			} else {
				repositoryUrl = connector.getRepositoryUrlFromTaskUrl(fullUrl);
				taskId = connector.getTaskIdFromTaskUrl(fullUrl);
				if (repositoryUrl != null && taskId != null) {
					opened = TasksUiInternal.openRepositoryTask(connectorUi.getConnectorKind(), repositoryUrl, taskId);
				}
			}
		}

		if (!opened) {
			TasksUiUtil.openUrl(fullUrl);
		}

		return true;
	}

	/**
	 * Searches for a task whose URL matches
	 * 
	 * @return first task with a matching URL.
	 * @since 2.0
	 */
	private static AbstractTask getTaskByUrl(String taskUrl) {
		Collection<AbstractTask> tasks = TasksUiPlugin.getTaskList().getAllTasks();
		for (AbstractTask task : tasks) {
			String currUrl = task.getUrl();
			if (currUrl != null && !currUrl.equals("") && currUrl.equals(taskUrl)) {
				return task;
			}
		}
		return null;
	}

	/**
	 * @since 3.0
	 */
	public static boolean openTask(TaskRepository repository, String taskId) {
		Assert.isNotNull(repository);
		Assert.isNotNull(taskId);

		AbstractTask task = (AbstractTask) TasksUiInternal.getTaskList().getTask(repository.getRepositoryUrl(), taskId);
		if (task == null) {
			task = TasksUiPlugin.getTaskList().getTaskByKey(repository.getRepositoryUrl(), taskId);
		}
		if (task != null) {
			return TasksUiUtil.openTask(task);
		} else {
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(repository.getConnectorKind());
			if (connectorUi != null) {
				try {
					return TasksUiInternal.openRepositoryTask(connectorUi.getConnectorKind(),
							repository.getRepositoryUrl(), taskId);
				} catch (Exception e) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
							"Internal error while opening repository task", e));
				}
			}
		}
		return false;
	}

	/**
	 * @since 3.0
	 */
	public static void openUrl(String location) {
		openUrl(location, FLAG_NO_RICH_EDITOR);
	}

	private static void openUrl(String location, int customFlags) {
		try {
			URL url = null;

			if (location != null) {
				url = new URL(location);
			}
			if (WebBrowserPreference.getBrowserChoice() == WebBrowserPreference.EXTERNAL) {
				try {
					IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
					support.getExternalBrowser().openURL(url);
				} catch (Exception e) {
					StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not open task url", e));
				}
			} else {
				IWebBrowser browser = null;
				int flags = 0;
				if (WorkbenchBrowserSupport.getInstance().isInternalWebBrowserAvailable()) {
					flags = IWorkbenchBrowserSupport.AS_EDITOR | IWorkbenchBrowserSupport.LOCATION_BAR
							| IWorkbenchBrowserSupport.NAVIGATION_BAR;
				} else {
					flags = IWorkbenchBrowserSupport.AS_EXTERNAL | IWorkbenchBrowserSupport.LOCATION_BAR
							| IWorkbenchBrowserSupport.NAVIGATION_BAR;
				}

				String generatedId = "org.eclipse.mylyn.web.browser-" + Calendar.getInstance().getTimeInMillis();
				browser = WorkbenchBrowserSupport.getInstance().createBrowser(flags, generatedId, null, null);
				browser.openURL(url);
			}
		} catch (PartInitException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Browser init error",
					"Browser could not be initiated");
		} catch (MalformedURLException e) {
			if (location != null && location.trim().equals("")) {
				MessageDialog.openInformation(Display.getDefault().getActiveShell(), TasksUiMessages.DIALOG_EDITOR,
						"No URL to open." + location);
			} else {
				MessageDialog.openInformation(Display.getDefault().getActiveShell(), TasksUiMessages.DIALOG_EDITOR,
						"Could not open URL: " + location);
			}
		}
	}

	/**
	 * If task is already open and has incoming, must force refresh in place
	 */
	private static boolean refreshEditorContentsIfOpen(ITask task, IEditorInput editorInput) {
		if (task != null) {
			if (task.getSynchronizationState() == SynchronizationState.INCOMING
					|| task.getSynchronizationState() == SynchronizationState.CONFLICT) {
				for (TaskEditor editor : TasksUiInternal.getActiveRepositoryTaskEditors()) {
					if (editor.getEditorInput().equals(editorInput)) {
						editor.refreshEditorContents();
						editor.getEditorSite().getPage().activate(editor);
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @since 3.0
	 */
	public static IViewPart openTasksViewInActivePerspective() {
		try {
			return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(TaskListView.ID);
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not show Task List view", e));
			return null;
		}
	}
}
