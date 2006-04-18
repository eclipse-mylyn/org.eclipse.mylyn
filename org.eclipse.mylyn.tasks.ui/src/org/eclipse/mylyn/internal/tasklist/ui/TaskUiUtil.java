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

package org.eclipse.mylar.internal.tasklist.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.TaskListPreferenceConstants;
import org.eclipse.mylar.internal.tasklist.ui.editors.CategoryEditorInput;
import org.eclipse.mylar.internal.tasklist.ui.editors.TaskEditorInput;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.DateRangeActivityDelegate;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.AbstractTaskContainer;
import org.eclipse.mylar.provisional.tasklist.ITaskListElement;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.Task;
import org.eclipse.mylar.provisional.tasklist.TaskCategory;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart; 
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.internal.browser.WorkbenchBrowserSupport;

/**
 * @author Mik Kersten
 */
public class TaskUiUtil {

	/**
	 * TODO: move
	 */
	public static Image getImageForPriority(Task.PriorityLevel priorityLevel) {
		if (priorityLevel == null) {
			return null;
		}
		switch (priorityLevel) {
		case P1: 
			return TaskListImages.getImage(TaskListImages.PRIORITY_1);
		case P2:
			return TaskListImages.getImage(TaskListImages.PRIORITY_2);
		case P3:
			return TaskListImages.getImage(TaskListImages.PRIORITY_3);
		case P4:
			return TaskListImages.getImage(TaskListImages.PRIORITY_4);
		case P5:
			return TaskListImages.getImage(TaskListImages.PRIORITY_5);
		default:
			return null;
		}
	}
	
	public static void closeEditorInActivePage(ITask task) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page == null) {
			return;
		}
		IEditorInput input = new TaskEditorInput(task, false);
		IEditorPart editor = page.findEditor(input);
		if (editor != null) {
			page.closeEditor(editor, false);
		}
	}

	/**
	 * Either pass in a repository and id, or fullUrl, or all of them
	 */
	public static boolean openRepositoryTask(String repositoryUrl, String taskId, String fullUrl) {
		boolean opened = false;
		String handle = AbstractRepositoryTask.getHandle(repositoryUrl, taskId);
		ITask task = MylarTaskListPlugin.getTaskListManager().getTaskList().getTask(handle); 
		if (task == null) { 
			// search for it
			for (ITask currTask : MylarTaskListPlugin.getTaskListManager().getTaskList().getAllTasks()) {
				if (currTask instanceof AbstractRepositoryTask) {
					String currUrl = ((AbstractRepositoryTask)currTask).getUrl();
					if (currUrl != null && currUrl.equals(fullUrl)) {
						task = currTask;
						break; 
					}
				}
			}
		}
		if (task != null) {
			TaskUiUtil.refreshAndOpenTaskListElement(task);
			opened = true;
		} else {
			AbstractRepositoryConnector connector = MylarTaskListPlugin.getRepositoryManager().getRepositoryForTaskUrl(fullUrl);
			if (connector != null) {
				connector.openRemoteTask(repositoryUrl, taskId);
				opened = true;
			}
		} 
		if (!opened) { 
			TaskUiUtil.openUrl("Web Browser", "Web Browser", fullUrl);
			opened = true;
		}
		return opened;
	}
	
	public static void refreshAndOpenTaskListElement(ITaskListElement element) {
		if (element instanceof ITask || element instanceof AbstractQueryHit
				|| element instanceof DateRangeActivityDelegate) {
			final ITask task;
			if (element instanceof AbstractQueryHit) {
				task = ((AbstractQueryHit) element).getOrCreateCorrespondingTask();
			} else if (element instanceof DateRangeActivityDelegate) {
				task = ((DateRangeActivityDelegate) element).getCorrespondingTask();
			} else {
				task = (ITask) element;
			}

			boolean forceUpdate = false;
			if (task instanceof AbstractRepositoryTask) {
				final AbstractRepositoryConnector connector = MylarTaskListPlugin.getRepositoryManager()
						.getRepositoryConnector(((AbstractRepositoryTask) task).getRepositoryKind());
				if (connector != null) {
					Job refreshJob = connector.synchronize((AbstractRepositoryTask) task, forceUpdate,
							new IJobChangeListener() {

								public void done(IJobChangeEvent event) {
									TaskUiUtil.openEditor(task, false);
								}

								public void aboutToRun(IJobChangeEvent event) {
									// ignore
								}

								public void awake(IJobChangeEvent event) {
									// ignore
								}

								public void running(IJobChangeEvent event) {
									// ignore
								}

								public void scheduled(IJobChangeEvent event) {
									// ignore
								}

								public void sleeping(IJobChangeEvent event) {
									// ignore
								}
							});
					if (refreshJob == null) {
						TaskUiUtil.openEditor(task, false);
					}
				}
			} else {
				TaskUiUtil.openEditor(task, false);
			}
		} else if (element instanceof TaskCategory) {
			TaskUiUtil.openEditor((AbstractTaskContainer) element);
		} else if (element instanceof AbstractRepositoryQuery) {
			AbstractRepositoryQuery query = (AbstractRepositoryQuery) element;
			AbstractRepositoryConnector client = MylarTaskListPlugin.getRepositoryManager().getRepositoryConnector(
					query.getRepositoryKind());
			client.openEditQueryDialog(query);
		}
	}
	
	public static void openEditor(final ITask task, boolean newTask) {
		openEditor(task, true, newTask);
	}

	/**
	 * Set asyncExec false for testing purposes.
	 */
	public static void openEditor(final ITask task, boolean asyncExec, boolean newTask) {

		final IEditorInput editorInput = new TaskEditorInput(task, newTask);

		if (asyncExec) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					openEditor(editorInput, TaskListPreferenceConstants.TASK_EDITOR_ID, page);
				}
			});
		} else {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			openEditor(editorInput, TaskListPreferenceConstants.TASK_EDITOR_ID, page);
		}
	}

	public static IEditorPart openEditor(IEditorInput input, String editorId, IWorkbenchPage page) {
		try {
			return page.openEditor(input, editorId);
		} catch (PartInitException e) {
			MylarStatusHandler.fail(e, "Open for editor failed: " + input + ", id: " + editorId, true);
		}
		return null;
	}

	public static void openEditor(AbstractTaskContainer category) {
		final IEditorInput input = new CategoryEditorInput(category);
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				openEditor(input, TaskListPreferenceConstants.CATEGORY_EDITOR_ID, page);
			}
		});
	}

	public static void openUrl(String title, String tooltip, String url) {
		try {
			IWebBrowser browser = null;
			int flags = 0;
			if (WorkbenchBrowserSupport.getInstance().isInternalWebBrowserAvailable()) {
				flags = WorkbenchBrowserSupport.AS_EDITOR | WorkbenchBrowserSupport.LOCATION_BAR
						| WorkbenchBrowserSupport.NAVIGATION_BAR;

			} else {
				flags = WorkbenchBrowserSupport.AS_EXTERNAL | WorkbenchBrowserSupport.LOCATION_BAR
						| WorkbenchBrowserSupport.NAVIGATION_BAR;
			}
			browser = WorkbenchBrowserSupport.getInstance().createBrowser(flags, MylarTaskListPlugin.PLUGIN_ID + title,
					title, tooltip);
			browser.openURL(new URL(url));
		} catch (PartInitException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Browser init error",
					"Browser could not be initiated");
		} catch (MalformedURLException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "URL not found", "URL Could not be opened");
		}
	}
}
