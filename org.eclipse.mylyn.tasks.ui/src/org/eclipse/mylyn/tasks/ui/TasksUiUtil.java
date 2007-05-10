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

package org.eclipse.mylar.tasks.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.ui.TaskListPreferenceConstants;
import org.eclipse.mylar.internal.tasks.ui.editors.CategoryEditorInput;
import org.eclipse.mylar.internal.tasks.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.internal.tasks.ui.wizards.EditRepositoryWizard;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.AbstractTaskContainer;
import org.eclipse.mylar.tasks.core.DateRangeActivityDelegate;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskListElement;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskCategory;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.mylar.tasks.ui.editors.TaskEditor;
import org.eclipse.mylar.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.browser.WebBrowserPreference;
import org.eclipse.ui.internal.browser.WorkbenchBrowserSupport;

/**
 * @author Mik Kersten
 */
public class TasksUiUtil {

	public static final String PREFS_PAGE_ID_COLORS_AND_FONTS = "org.eclipse.ui.preferencePages.ColorsAndFonts";

	public static final int FLAG_NO_RICH_EDITOR = 1 << 17;
	
	/**
	 * Resolves a rich editor for the task if available. Must be called from UI
	 * thread.
	 */
	public static void openUrl(String url, boolean useRichEditorIfAvailable) {
		try {
			if (useRichEditorIfAvailable) {
				AbstractRepositoryTask task = TasksUiPlugin.getTaskListManager().getTaskList().getRepositoryTask(url);
				if (task != null) {
					refreshAndOpenTaskListElement(task);
				} else {
					openUrl(url, false);
				}
			} else {
				if (WebBrowserPreference.getBrowserChoice() == WebBrowserPreference.EXTERNAL) {
					try {
						IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
						support.getExternalBrowser().openURL(new URL(url));
					} catch (Exception e) {
						MylarStatusHandler.fail(e, "could not open task url", true);
					}
				} else {
					IWebBrowser browser = null;
					int flags = 0;
					if (WorkbenchBrowserSupport.getInstance().isInternalWebBrowserAvailable()) {
						flags = WorkbenchBrowserSupport.AS_EDITOR | WorkbenchBrowserSupport.LOCATION_BAR
								| WorkbenchBrowserSupport.NAVIGATION_BAR | FLAG_NO_RICH_EDITOR;
					} else {
						flags = WorkbenchBrowserSupport.AS_EXTERNAL | WorkbenchBrowserSupport.LOCATION_BAR
								| WorkbenchBrowserSupport.NAVIGATION_BAR | FLAG_NO_RICH_EDITOR;
					}

					String generatedId = "org.eclipse.mylar.web.browser-" + Calendar.getInstance().getTimeInMillis();
					browser = WorkbenchBrowserSupport.getInstance().createBrowser(flags, generatedId, null, null);
					browser.openURL(new URL(url));
				}
			}
		} catch (PartInitException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Browser init error",
					"Browser could not be initiated");
		} catch (MalformedURLException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "URL not found", "URL Could not be opened");
		}
	}

	public static boolean openRepositoryTask(TaskRepository repository, String taskId) {
		boolean opened = false;
		ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(repository.getUrl(), taskId);
		if (task != null) {
			TasksUiUtil.refreshAndOpenTaskListElement(task);
			opened = true;
		} else {
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getRepositoryUi(repository.getKind());
			if (connectorUi != null) {
				try {
					opened = connectorUi.openRepositoryTask(repository.getUrl(), taskId);
				} catch (Exception e) {
					MylarStatusHandler.log(e, "Internal error while opening repository task");
				}
			}
		}
		return opened;
	}

	/**
	 * Either pass in a repository and taskId, or fullUrl, or all of them
	 */
	public static boolean openRepositoryTask(String repositoryUrl, String taskId, String fullUrl) {
		boolean opened = false;
		ITask task = null;
		// TODO: move, must current be first due to JIRA Connector use of key
		if (fullUrl != null) {
			task = TasksUiPlugin.getTaskListManager().getTaskList().getRepositoryTask(fullUrl);
		}
		if (task == null && repositoryUrl != null && taskId != null) {
			task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(repositoryUrl, taskId);
		}

		if (task != null) {
			TasksUiUtil.refreshAndOpenTaskListElement(task);
			opened = true;
		} else {
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager()
					.getConnectorForRepositoryTaskUrl(fullUrl);
			if (connector != null) {
				AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin
						.getRepositoryUi(connector.getRepositoryType());
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
		}
		if (!opened) {
			TasksUiUtil.openUrl(fullUrl, false);
			opened = true;
		}
		return opened;
	}

	public static void refreshAndOpenTaskListElement(ITaskListElement element) {
		if (element instanceof ITask || element instanceof AbstractQueryHit
				|| element instanceof DateRangeActivityDelegate) {
			final ITask task;
			if (element instanceof AbstractQueryHit) {
				if (((AbstractQueryHit) element).getCorrespondingTask() != null) {
					task = ((AbstractQueryHit) element).getCorrespondingTask();
				} else {
					task = ((AbstractQueryHit) element).getOrCreateCorrespondingTask();
					// NewLocalTaskAction.scheduleNewTask(task);
				}
			} else if (element instanceof DateRangeActivityDelegate) {
				task = ((DateRangeActivityDelegate) element).getCorrespondingTask();
			} else {
				task = (ITask) element;
			}

			if (task instanceof AbstractRepositoryTask) {

				final AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask) task;
				String repositoryKind = repositoryTask.getRepositoryKind();
				final AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager()
						.getRepositoryConnector(repositoryKind);

				TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(repositoryKind,
						repositoryTask.getRepositoryUrl());

				if (repository == null) {
					MylarStatusHandler.fail(null, "No repository found for task. Please create repository in "
							+ TasksUiPlugin.LABEL_VIEW_REPOSITORIES + ".", true);
					return;
				}

				if (connector != null) {

					RepositoryTaskData taskData = TasksUiPlugin.getDefault().getTaskDataManager().getNewTaskData(
							task.getHandleIdentifier());

					if (taskData != null) {
						TasksUiUtil.openEditor(task, true, false);
					} else {
						Job refreshJob = TasksUiPlugin.getSynchronizationManager().synchronize(connector,
								repositoryTask, true, new JobChangeAdapter() {
									@Override
									public void done(IJobChangeEvent event) {
										TasksUiUtil.openEditor(task, false);
									}
								});
						if (refreshJob == null) {
							TasksUiUtil.openEditor(task, false);
						}
					}
				}
			} else {
				TasksUiUtil.openEditor(task, false);
			}
		} else if (element instanceof TaskCategory) {
			TasksUiUtil.openEditor((AbstractTaskContainer) element);
		} else if (element instanceof AbstractRepositoryQuery) {
			AbstractRepositoryQuery query = (AbstractRepositoryQuery) element;
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getRepositoryUi(query.getRepositoryKind());
			connectorUi.openEditQueryDialog(query);
		}
	}

	public static void openEditor(final ITask task, boolean newTask) {
		openEditor(task, true, newTask);
	}

	private static String getTaskEditorId(final ITask task) {
		String taskEditorId = TaskListPreferenceConstants.TASK_EDITOR_ID;
		if (task instanceof AbstractRepositoryTask) {
			AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask) task;
			AbstractRepositoryConnectorUi repositoryUi = TasksUiPlugin.getRepositoryUi(repositoryTask
					.getRepositoryKind());
			String customTaskEditorId = repositoryUi.getTaskEditorId(repositoryTask);
			if (customTaskEditorId != null) {
				taskEditorId = customTaskEditorId;
			}
		}
		return taskEditorId;
	}

	/**
	 * @param task
	 * @param pageId
	 *            the taskId of the page to activate after opening
	 */
	public static void openEditor(ITask task, String pageId) {
		final IEditorInput editorInput = new TaskEditorInput(task, false);
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IEditorPart part = openEditor(editorInput, getTaskEditorId(task), window.getActivePage());
		if (part instanceof TaskEditor) {
			((TaskEditor) part).setActivePage(pageId);
		}
	}

	/**
	 * Set asyncExec false for testing purposes.
	 */
	public static void openEditor(final ITask task, boolean asyncExec, final boolean newTask) {

		final boolean openWithBrowser = TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(
				TaskListPreferenceConstants.REPORT_OPEN_INTERNAL);

		final String taskEditorId = getTaskEditorId(task);

		final IEditorInput editorInput = new TaskEditorInput(task, newTask);

		if (asyncExec) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				private boolean wasOpen = false;

				public void run() {
					IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					if (window != null) {						
						if (openWithBrowser) {
							openUrl(task.getTaskUrl(), false);
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
								if (task instanceof AbstractRepositoryTask) {
									AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask) task;
									if (!wasOpen) {
										TasksUiPlugin.getSynchronizationManager().setTaskRead(repositoryTask, true);
									}
									// Synchronization must happen after marked read.
									AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager()
											.getRepositoryConnector(repositoryTask.getRepositoryKind());
									if (connector != null) {
										TasksUiPlugin.getSynchronizationManager().synchronize(connector, repositoryTask, false,
												null);
									}

								}
								return Status.OK_STATUS;
							}};
							
							updateTaskData.setSystem(true);
							updateTaskData.schedule();

					}
				}
			});
		} else {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window != null) {
				if (openWithBrowser) {
					openUrl(task.getTaskUrl(), false);
				} else {
					IWorkbenchPage page = window.getActivePage();
					openEditor(editorInput, taskEditorId, page);
				}
				if (task instanceof AbstractRepositoryTask) {
					TasksUiPlugin.getSynchronizationManager().setTaskRead((AbstractRepositoryTask) task, true);
				}
			} else {
				MylarStatusHandler.log("Unable to open editor for " + task.getSummary(), TasksUiUtil.class);
			}
		}
	}

	/**
	 * If task is already open and has incoming, must force refresh in place
	 */
	private static boolean refreshIfOpen(ITask task, IEditorInput editorInput) {
		if (task instanceof AbstractRepositoryTask) {
			if (((AbstractRepositoryTask) task).getSyncState() == RepositoryTaskSyncState.INCOMING
					|| ((AbstractRepositoryTask) task).getSyncState() == RepositoryTaskSyncState.CONFLICT) {
				for (TaskEditor editor : getActiveRepositoryTaskEditors()) {
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

	public static IEditorPart openEditor(IEditorInput input, String editorId, IWorkbenchPage page) {
		try {
			return page.openEditor(input, editorId);
		} catch (PartInitException e) {
			MylarStatusHandler.fail(e, "Open for editor failed: " + input + ", taskId: " + editorId, true);
		}
		return null;
	}

	public static void openEditor(AbstractTaskContainer category) {
		final IEditorInput input = new CategoryEditorInput(category);
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window != null) {
					IWorkbenchPage page = window.getActivePage();
					openEditor(input, TaskListPreferenceConstants.CATEGORY_EDITOR_ID, page);
				}
			}
		});
	}

	public static int openEditRepositoryWizard(TaskRepository repository) {
		try {
			EditRepositoryWizard wizard = new EditRepositoryWizard(repository);
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			if (wizard != null && shell != null && !shell.isDisposed()) {
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.create();
				dialog.setBlockOnOpen(true);
				if (dialog.open() == Dialog.CANCEL) {
					dialog.close();
					return Dialog.CANCEL;
				}

			}

			if (TaskRepositoriesView.getFromActivePerspective() != null) {
				TaskRepositoriesView.getFromActivePerspective().getViewer().refresh();
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, e.getMessage(), true);
		}
		return Dialog.OK;
	}

	public static List<TaskEditor> getActiveRepositoryTaskEditors() {
		List<TaskEditor> repositoryTaskEditors = new ArrayList<TaskEditor>();
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (IWorkbenchWindow window : windows) {
			IEditorReference[] editorReferences = window.getActivePage().getEditorReferences();
			for (int i = 0; i < editorReferences.length; i++) {
				IEditorPart editor = editorReferences[i].getEditor(false);
				if (editor instanceof TaskEditor) {
					TaskEditor taskEditor = (TaskEditor) editor;
					if (taskEditor.getEditorInput() instanceof TaskEditorInput) {
						TaskEditorInput input = (TaskEditorInput) taskEditor.getEditorInput();
						if (input.getTask() instanceof AbstractRepositoryTask) {
							repositoryTaskEditors.add((TaskEditor) editor);
						}
					}
				}
			}
		}
		return repositoryTaskEditors;
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

		Object element = selection.getFirstElement();
		if (element instanceof TaskRepository) {
			return (TaskRepository) selection.getFirstElement();
		} else if (element instanceof AbstractRepositoryQuery) {
			AbstractRepositoryQuery query = (AbstractRepositoryQuery) element;
			return TasksUiPlugin.getRepositoryManager().getRepository(query.getRepositoryKind(),
					query.getRepositoryUrl());
		} else if (element instanceof AbstractQueryHit) {
			AbstractQueryHit queryHit = (AbstractQueryHit) element;
			if (queryHit.getParent() != null) {
				return TasksUiPlugin.getRepositoryManager().getRepository(queryHit.getParent().getRepositoryKind(),
						queryHit.getRepositoryUrl());
			} else {
				return TasksUiPlugin.getRepositoryManager().getRepository(queryHit.getRepositoryUrl());
			}
		} else if (element instanceof AbstractRepositoryTask) {
			AbstractRepositoryTask task = (AbstractRepositoryTask) element;
			return TasksUiPlugin.getRepositoryManager()
					.getRepository(task.getRepositoryKind(), task.getRepositoryUrl());
		} else if (element instanceof IResource) {
			IResource resource = (IResource) element;
			return TasksUiPlugin.getDefault().getRepositoryForResource(resource, true);
		} else if (element instanceof IAdaptable) {
			IAdaptable adaptable = (IAdaptable) element;
			IResource resource = (IResource) adaptable.getAdapter(IResource.class);
			if (resource != null) {
				return TasksUiPlugin.getDefault().getRepositoryForResource(resource, true);
			} else {
				ITask task = (ITask) adaptable.getAdapter(ITask.class);
				if (task instanceof AbstractRepositoryTask) {
					AbstractRepositoryTask rtask = (AbstractRepositoryTask) task;
					return TasksUiPlugin.getRepositoryManager().getRepository(rtask.getRepositoryKind(),
							rtask.getRepositoryUrl());
				}
			}
		}

		// TODO mapping between LogEntry.pliginId and repositories
		// TODO handle other selection types
		return null;
	}

	public static void showPreferencePage(String id, IPreferencePage page) {
		final IPreferenceNode targetNode = new PreferenceNode(id, page);

		PreferenceManager manager = new PreferenceManager();
		manager.addToRoot(targetNode);
		final PreferenceDialog dialog = new PreferenceDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
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
	
	public static void closeEditorInActivePage(ITask task) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			return;
		}
		IWorkbenchPage page = window.getActivePage();
		if (page == null) {
			return;
		}
		IEditorInput input = new TaskEditorInput(task, false);
		IEditorPart editor = page.findEditor(input);
		if (editor != null) {
			page.closeEditor(editor, false);
		}
	}
}