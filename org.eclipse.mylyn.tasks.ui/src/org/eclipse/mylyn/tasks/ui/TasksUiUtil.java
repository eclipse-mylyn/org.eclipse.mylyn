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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskDataStorageManager;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractLegacyRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.core.deprecated.TaskSelection;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiMessages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoriesView;
import org.eclipse.mylyn.internal.tasks.ui.wizards.EditRepositoryWizard;
import org.eclipse.mylyn.internal.tasks.ui.wizards.MultiRepositoryAwareWizard;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewLocalTaskWizard;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.ITaskList;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.SynchronizationState;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
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
// move to util package?
public class TasksUiUtil {

	/**
	 * Flag that is passed along to the workbench browser support when a task is opened in a browser because no rich
	 * editor was available.
	 * 
	 * @see #openTask(String)
	 */
	public static final int FLAG_NO_RICH_EDITOR = 1 << 17;

	public static final String PREFS_PAGE_ID_COLORS_AND_FONTS = "org.eclipse.ui.preferencePages.ColorsAndFonts";

	public static void closeEditorInActivePage(AbstractTask task, boolean save) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			return;
		}
		IWorkbenchPage page = window.getActivePage();
		if (page == null) {
			return;
		}
		TaskRepository taskRepository = TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(),
				task.getRepositoryUrl());
		IEditorInput input = new TaskEditorInput(taskRepository, task);
		IEditorPart editor = page.findEditor(input);
		if (editor != null) {
			page.closeEditor(editor, save);
		}
	}

	/**
	 * @since 3.0
	 */
	// API 3.0 consider moving this somewhere else and renaming to addToTaskList
	public static AbstractTask createTask(TaskRepository repository, String id, IProgressMonitor monitor)
			throws CoreException {
		monitor = Policy.monitorFor(monitor);
		ITaskList taskList = TasksUi.getTaskListManager().getTaskList();
		AbstractTask task = taskList.getTask(repository.getRepositoryUrl(), id);
		if (task == null) {
			AbstractRepositoryConnector connector = TasksUiPlugin.getConnector(repository.getConnectorKind());
			if (connector instanceof AbstractLegacyRepositoryConnector) {
				RepositoryTaskData taskData = ((AbstractLegacyRepositoryConnector) connector).getTaskData(repository,
						id, new SubProgressMonitor(monitor, 1));
				if (taskData != null) {
					task = createTaskFromTaskData((AbstractLegacyRepositoryConnector) connector, taskList, repository,
							taskData, true, new SubProgressMonitor(monitor, 1));
					if (task != null) {
						task.setSynchronizationState(SynchronizationState.INCOMING);
						taskList.addTask(task);
					}
				}
			}
			// FIXME 3.0 support new task data
		}
		return task;
	}

	/**
	 * Create new repository task, adding result to tasklist
	 */
	private static AbstractTask createTaskFromExistingId(AbstractLegacyRepositoryConnector connector,
			ITaskList taskList, TaskRepository repository, String id, boolean retrieveSubTasks, IProgressMonitor monitor)
			throws CoreException {
		AbstractTask task = taskList.getTask(repository.getRepositoryUrl(), id);
		if (task == null) {
			RepositoryTaskData taskData = connector.getTaskData(repository, id, new SubProgressMonitor(monitor, 1));
			if (taskData != null) {
				task = createTaskFromTaskData(connector, taskList, repository, taskData, retrieveSubTasks,
						new SubProgressMonitor(monitor, 1));
				if (task != null) {
					task.setSynchronizationState(SynchronizationState.INCOMING);
					taskList.addTask(task);
				}
			}
		}
		return task;
	}

	/**
	 * Creates a new task from the given task data. Does NOT add resulting task to the task list.
	 */
	private static AbstractTask createTaskFromTaskData(AbstractLegacyRepositoryConnector connector, ITaskList taskList,
			TaskRepository repository, RepositoryTaskData taskData, boolean retrieveSubTasks, IProgressMonitor monitor)
			throws CoreException {
		AbstractTask task = null;
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		try {
			TaskDataStorageManager taskDataManager = TasksUiPlugin.getTaskDataStorageManager();
			if (taskData != null) {
				// Use connector task factory
				task = connector.createTask(repository.getRepositoryUrl(), taskData.getTaskId(), taskData.getTaskId()
						+ ": " + taskData.getDescription());
				connector.updateTaskFromTaskData(repository, task, taskData);
				taskDataManager.setNewTaskData(taskData);

				if (retrieveSubTasks) {
					monitor.beginTask("Creating task", connector.getLegacyTaskDataHandler().getSubTaskIds(taskData).size());
					for (String subId : connector.getLegacyTaskDataHandler().getSubTaskIds(taskData)) {
						if (subId == null || subId.trim().equals("")) {
							continue;
						}
						AbstractTask subTask = createTaskFromExistingId(connector, taskList, repository, subId, false,
								new SubProgressMonitor(monitor, 1));
						if (subTask != null) {
							taskList.addTask(subTask, task);
						}
					}
				}
			}
		} finally {
			monitor.done();
		}
		return task;
	}

	/**
	 * @deprecated Use {@link TasksUiInternal#getActiveRepositoryTaskEditors()} instead
	 */
	@Deprecated
	public static List<TaskEditor> getActiveRepositoryTaskEditors() {
		return TasksUiInternal.getActiveRepositoryTaskEditors();
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
		} else if (element instanceof AbstractRepositoryQuery) {
			AbstractRepositoryQuery query = (AbstractRepositoryQuery) element;
			return TasksUi.getRepositoryManager().getRepository(query.getConnectorKind(), query.getRepositoryUrl());
		} else if (element instanceof AbstractTask) {
			AbstractTask task = (AbstractTask) element;
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
				AbstractTask task = (AbstractTask) adaptable.getAdapter(AbstractTask.class);
				if (task != null) {
					AbstractTask rtask = task;
					return TasksUi.getRepositoryManager().getRepository(rtask.getConnectorKind(),
							rtask.getRepositoryUrl());
				}
			}
		}

		// TODO mapping between LogEntry.pliginId and repositories
		// TODO handle other selection types
		return null;
	}

	private static String getTaskEditorId(final AbstractTask task) {
		String taskEditorId = TaskEditor.ID_EDITOR;
		if (task != null) {
			AbstractTask repositoryTask = task;
			AbstractRepositoryConnectorUi repositoryUi = TasksUiPlugin.getConnectorUi(repositoryTask.getConnectorKind());
			String customTaskEditorId = repositoryUi.getTaskEditorId(repositoryTask);
			if (customTaskEditorId != null) {
				taskEditorId = customTaskEditorId;
			}
		}
		return taskEditorId;
	}

	/**
	 * @deprecated use {@link #openTaskAndRefresh(AbstractTask)} instead
	 */
	@Deprecated
	public static void openEditor(final AbstractTask task, boolean newTask) {
		openEditor(task, true, newTask);
	}

	/**
	 * Set asyncExec false for testing purposes.
	 * 
	 * @deprecated use {@link #openTaskAndRefresh(AbstractTask)} instead
	 */
	@Deprecated
	public static void openEditor(final AbstractTask task, boolean asyncExec, final boolean newTask) {
		final boolean openWithBrowser = !TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(
				TasksUiPreferenceConstants.EDITOR_TASKS_RICH);

		final String taskEditorId = getTaskEditorId(task);

		final IEditorInput editorInput = new TaskEditorInput(task, newTask);

		if (asyncExec) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				private boolean wasOpen = false;

				public void run() {
					IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					if (window != null) {
						if (openWithBrowser) {
							openUrl(task.getUrl());
						} else {
							IWorkbenchPage page = window.getActivePage();
							wasOpen = refreshIfOpen(task, editorInput);

							if (!wasOpen) {
								IEditorPart part = openEditor(editorInput, taskEditorId, page);
								if (newTask && part instanceof TaskEditor) {
									TaskEditor taskEditor = (TaskEditor) part;
									taskEditor.setFocusOfActivePage();
								}
							}
						}

						Job updateTaskData = new Job("Update Task State") {

							@Override
							protected IStatus run(IProgressMonitor monitor) {
								if (task != null) {
									AbstractTask repositoryTask = task;
									if (!wasOpen) {
										TasksUiPlugin.getTaskDataManager().setTaskRead(repositoryTask, true);
									}
									// Synchronization must happen after marked
									// read.
									AbstractRepositoryConnector connector = TasksUi.getRepositoryManager()
											.getRepositoryConnector(repositoryTask.getConnectorKind());
									if (connector != null) {
										TasksUiInternal.synchronizeTask(connector, repositoryTask, false, null);
									}

								}
								return Status.OK_STATUS;
							}
						};

						updateTaskData.setSystem(true);
						updateTaskData.schedule();

					}
				}
			});
		} else {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window != null) {
				if (openWithBrowser) {
					openUrl(task.getUrl());
				} else {
					IWorkbenchPage page = window.getActivePage();
					openEditor(editorInput, taskEditorId, page);
				}
				if (task != null) {
					TasksUiPlugin.getTaskDataManager().setTaskRead(task, true);
				}
			} else {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Unable to open editor for \""
						+ task.getSummary() + "\": no active workbench window"));
			}
		}
	}

	/**
	 * @deprecated use {@link #openTask(AbstractTask)} instead
	 */
	@Deprecated
	public static void openEditor(AbstractTask task, String pageId) {
		final IEditorInput editorInput = new TaskEditorInput(task, false);
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IEditorPart part = openEditor(editorInput, getTaskEditorId(task), window.getActivePage());
		if (part instanceof TaskEditor) {
			((TaskEditor) part).setActivePage(pageId);
		}
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
	public static boolean openNewLocalTaskEditor(Shell shell, TaskSelection taskSelection) {
		return openNewTaskEditor(shell, new NewLocalTaskWizard(taskSelection), taskSelection);
	}

	private static boolean openNewTaskEditor(Shell shell, IWizard wizard, TaskSelection taskSelection) {
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
	public static boolean openNewTaskEditor(Shell shell, TaskSelection taskSelection, TaskRepository taskRepository) {
		final IWizard wizard;
		List<TaskRepository> repositories = TasksUi.getRepositoryManager().getAllRepositories();
		if (taskRepository == null && repositories.size() == 1) {
			// only the Local Tasks connector is available
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
	public static boolean openTaskInBackground(AbstractTask task, boolean bringToTop) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IEditorPart activeEditor = null;
			IWorkbenchPart activePart = null;
			IWorkbenchPage activePage = window.getActivePage();
			if (activePage != null) {
				activeEditor = activePage.getActiveEditor();
				activePart = activePage.getActivePart();
			}
			boolean opened = openTask(task);
			if (opened && activePage != null) {
				if (!bringToTop && activeEditor != null) {
					activePage.bringToTop(activeEditor);
				}
				if (activePart != null) {
					activePage.activate(activePart);
				}
			}
			return opened;
		} else {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Unable to open editor for \""
					+ task.getSummary() + "\": no active workbench window"));
		}
		return false;
	}

	/**
	 * @since 3.0
	 */
	public static boolean openTask(AbstractTask task) {
		Assert.isNotNull(task);

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			boolean openWithBrowser = !TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(
					TasksUiPreferenceConstants.EDITOR_TASKS_RICH);
			if (openWithBrowser) {
				openUrl(task.getUrl());
				return true;
			} else {
				TaskRepository taskRepository = TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(),
						task.getRepositoryUrl());
				IEditorInput editorInput = new TaskEditorInput(taskRepository, task);
				boolean wasOpen = refreshIfOpen(task, editorInput);
				if (wasOpen) {
					return true;
				} else {
					IWorkbenchPage page = window.getActivePage();
					IEditorPart editor = openEditor(editorInput, getTaskEditorId(task), page);
					if (editor != null) {
						TasksUiPlugin.getTaskDataManager().setTaskRead(task, true);
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

	/**
	 * Resolves a rich editor for the task if available.
	 * 
	 * @since 3.0
	 */
	public static void openTask(String url) {
		AbstractTask task = TasksUiUtil.getTaskByUrl(url);
		if (task != null && !(task instanceof LocalTask)) {
			openTaskAndRefresh(task);
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
			task = TasksUi.getTaskListManager().getTaskList().getTask(repositoryUrl, taskId);
		}
		if (task == null && fullUrl != null) {
			task = TasksUiUtil.getTaskByUrl(fullUrl);
		}
		if (task == null && repositoryUrl != null && taskId != null) {
			task = TasksUi.getTaskListManager().getTaskList().getTaskByKey(repositoryUrl, taskId);
		}

		if (task != null) {
			return TasksUiUtil.openTaskAndRefresh(task);
		}

		boolean opened = false;

		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getConnectorForRepositoryTaskUrl(
				fullUrl);
		if (connector != null) {
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(connector.getConnectorKind());
			if (repositoryUrl != null && taskId != null) {
				opened = connectorUi.openRepositoryTask(repositoryUrl, taskId);
			} else {
				repositoryUrl = connector.getRepositoryUrlFromTaskUrl(fullUrl);
				taskId = connector.getTaskIdFromTaskUrl(fullUrl);
				if (repositoryUrl != null && taskId != null) {
					opened = connectorUi.openRepositoryTask(repositoryUrl, taskId);
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
		Collection<AbstractTask> tasks = TasksUi.getTaskListManager().getTaskList().getAllTasks();
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

		AbstractTask task = TasksUi.getTaskListManager().getTaskList().getTask(repository.getRepositoryUrl(), taskId);
		if (task == null) {
			task = TasksUi.getTaskListManager().getTaskList().getTaskByKey(repository.getRepositoryUrl(), taskId);
		}
		if (task != null) {
			return TasksUiUtil.openTaskAndRefresh(task);
		} else {
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(repository.getConnectorKind());
			if (connectorUi != null) {
				try {
					return connectorUi.openRepositoryTask(repository.getRepositoryUrl(), taskId);
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
	public static boolean openTaskAndRefresh(final AbstractTask task) {
		if (openTask(task)) {
			Job updateTaskData = new Job("Refresh Task") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					if (task != null) {
						AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
								task.getConnectorKind());
						if (connector != null) {
							TasksUiInternal.synchronizeTask(connector, task, false, null);
						}
					}
					return Status.OK_STATUS;
				}
			};
			updateTaskData.setSystem(true);
			updateTaskData.schedule();
			return true;
		}
		return false;
	}

	/**
	 * @since 3.0
	 */
	public static void openUrl(String location) {
		openUrl(location, FLAG_NO_RICH_EDITOR);
	}

	/**
	 * @deprecated use {@link #openTask(String)} or {@link #openUrl(String)} instead
	 */
	@Deprecated
	public static void openUrl(String url, boolean useRichEditorIfAvailable) {
		if (useRichEditorIfAvailable && url != null) {
			openTask(url);
		} else {
			openUrl(url);
		}
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
	 * @deprecated Use {@link TasksUiInternal#refreshAndOpenTaskListElement(AbstractTaskContainer)} instead
	 */
	@Deprecated
	public static void refreshAndOpenTaskListElement(AbstractTaskContainer element) {
		TasksUiInternal.refreshAndOpenTaskListElement(element);
	}

	/**
	 * If task is already open and has incoming, must force refresh in place
	 */
	private static boolean refreshIfOpen(AbstractTask task, IEditorInput editorInput) {
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
	 * Use PreferencesUtil.createPreferenceDialogOn(..) instead.
	 * 
	 * @since 3.0
	 */
	@Deprecated
	public static void showPreferencePage(String id, IPreferencePage page) {
		final IPreferenceNode targetNode = new PreferenceNode(id, page);

		PreferenceManager manager = new PreferenceManager();
		manager.addToRoot(targetNode);
		final PreferenceDialog dialog = new PreferenceDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getShell(), manager);
		final boolean[] result = new boolean[] { false };
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				dialog.create();
				dialog.setMessage(targetNode.getLabelText());
				result[0] = (dialog.open() == Window.OK);
			}
		});
	}
}
