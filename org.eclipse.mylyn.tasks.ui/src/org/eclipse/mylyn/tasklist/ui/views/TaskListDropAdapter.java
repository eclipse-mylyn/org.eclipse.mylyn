/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.mylar.core.util.MylarStatusHandler;
import org.eclipse.mylar.tasklist.IQueryHit;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.internal.Task;
import org.eclipse.mylar.tasklist.internal.TaskCategory;
import org.eclipse.mylar.tasklist.ui.ITaskListElement;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * @author Mik Kersten
 * @author Robert Elves (added task creation support)
 */
public class TaskListDropAdapter extends ViewerDropAdapter {

	private Task newTask = null;

	public TaskListDropAdapter(Viewer viewer) {
		super(viewer);
		setFeedbackEnabled(true);
	}

	@Override
	public boolean performDrop(Object data) {

		if (isUrl(data)) {
			return createTaskFromUrl(data);
		} else {
			ISelection selection = ((TreeViewer) getViewer()).getSelection();
			Object currentTarget = getCurrentTarget();
			List<ITask> tasksToMove = new ArrayList<ITask>();
			for (Object selectedObject : ((IStructuredSelection) selection).toList()) {
				ITask toMove = null;
				if (selectedObject instanceof ITask) {
					toMove = (ITask) selectedObject;
				} else if (selectedObject instanceof IQueryHit) {
					toMove = ((IQueryHit) selectedObject).getOrCreateCorrespondingTask();
				}
				if (toMove != null) {
					tasksToMove.add(toMove);
				}
			}

			for (ITask task : tasksToMove) {
				if (currentTarget instanceof TaskCategory) {
					MylarTaskListPlugin.getTaskListManager().moveToCategory((TaskCategory) currentTarget, task);
				} else if (currentTarget instanceof ITask) {
					ITask targetTask = (ITask) currentTarget;
					if (targetTask.getCategory() == null) {
						MylarTaskListPlugin.getTaskListManager().moveToRoot(task);
					} else {
						MylarTaskListPlugin.getTaskListManager().moveToCategory(
								(TaskCategory) targetTask.getCategory(), task);
					}
				}
			}
			return true;
		}
	}

	/**
	 * @return true if string is a http(s) url
	 */
	public boolean isUrl(Object data) {
		String uri = "";
		if (data instanceof String) {
			uri = (String) data;
			if ((uri.startsWith("http://") || uri.startsWith("https://"))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param data
	 *            string containing url and title separated by <quote>\n</quote>
	 * @return true if task succesfully created, false otherwise
	 */
	public boolean createTaskFromUrl(Object data) {

		if (!(data instanceof String))
			return false;

		String[] urlTransfer = ((String) data).split("\n");

		String url = "";
		String urlTitle = "";

		if (urlTransfer.length > 0) {
			url = urlTransfer[0];
		} else {
			return false;
		}

		// If a Title is provided, use it.
		if (urlTransfer.length > 1) {
			urlTitle = urlTransfer[1];
		}

		if (urlTransfer.length < 2) { // no title provided
			retrieveTaskDescription(url);
		}

		newTask = new Task(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), urlTitle, true);

		if (newTask == null) {
			return false;
		}

		newTask.setPriority(MylarTaskListPlugin.PriorityLevel.P3.toString());
		newTask.setUrl(url);

		// Place new Task at root of task list
		MylarTaskListPlugin.getTaskListManager().moveToRoot(newTask);

		newTask.openTaskInEditor(true);

		// Make this new task the current selection in the view
		StructuredSelection ss = new StructuredSelection(newTask);
		getViewer().setSelection(ss);

		getViewer().refresh();

		return true;

	}

	@Override
	public boolean validateDrop(Object targetObject, int operation, TransferData transferType) {
		Object selectedObject = ((IStructuredSelection) ((TreeViewer) getViewer()).getSelection()).getFirstElement();
		if (selectedObject instanceof ITaskListElement && ((ITaskListElement) selectedObject).isDragAndDropEnabled()) {
			if (getCurrentTarget() instanceof TaskCategory) {
				return true;
			} else if (getCurrentTarget() instanceof ITaskListElement
					&& (getCurrentLocation() == ViewerDropAdapter.LOCATION_AFTER || getCurrentLocation() == ViewerDropAdapter.LOCATION_BEFORE)) {
				return true;
			} else {
				return false;
			}
		}

		return TextTransfer.getInstance().isSupportedType(transferType);
	}

	/**
	 * Attempts to set the task pageTitle to the title from the specified url
	 */
	protected void retrieveTaskDescription(final String url) {

		try {
//			final Shell shell = new Shell(Display.getDefault());
//			shell.setVisible(false);
//			Browser browser = new Browser(shell, SWT.NONE);

//			RetrievePageTitleFromUrlJob job = new RetrievePageTitleFromUrlJob("Retrieving task description", url) {
//
//				@Override
//				public void setTitle(String title) {
//					if (newTask != null) {
//						newTask.setDescription(title);
//						MylarTaskListPlugin.getTaskListManager().notifyTaskChanged(newTask);
//					}
//				}
//			};
//			browser.addTitleListener(job);
//			browser.setUrl(url);
//			job.schedule();
			
			RetrieveTitleFromUrlJob job = new RetrieveTitleFromUrlJob(url) {

				@Override
				protected void setTitle(final String pageTitle) {
					newTask.setDescription(pageTitle);
					MylarTaskListPlugin.getTaskListManager().notifyTaskChanged(newTask);
				}

			};
			job.schedule();

		} catch (RuntimeException e) {
			MylarStatusHandler.fail(e, "could not open task web page", false);
		}
	}

//	/**
//	 * Waits for the title from the browser
//	 * 
//	 * @author Wesley Coelho
//	 */
//	private class RetrievePageTitleFromUrlJob extends Job implements TitleListener {
//
//		private final static long MAX_WAIT_TIME_MILLIS = 1000 * 30; // (30
//																	// Seconds)
//
//		private final static long SLEEP_INTERVAL_MILLIS = 500;
//
//		private String taskURL = null;
//
//		private String pageTitle = null;
//
//		private boolean retrievalFailed = false;
//
//		private long timeWaitedMillis = 0;
//
//		/**
//		 * Determines when to ignore the second call to changed()
//		 */
//		boolean ignoreChangeCall = false; 
//
//		public RetrievePageTitleFromUrlJob(String name, String url) {
//			super(name);
//			taskURL = url;
//		}
//
//		@Override
//		protected IStatus run(IProgressMonitor monitor) {
//
//			while (pageTitle == null && !retrievalFailed && (timeWaitedMillis <= MAX_WAIT_TIME_MILLIS)) {
//
//				try {
//					Thread.sleep(SLEEP_INTERVAL_MILLIS);
//				} catch (InterruptedException e) {
//					MylarStatusHandler.fail(e, "Thread interrupted during sleep", false);
//				}
//				timeWaitedMillis += SLEEP_INTERVAL_MILLIS;
//			}
//
//			if (pageTitle != null) {
//				Display.getDefault().asyncExec(new Runnable() {
//					public void run() {
//						newTask.setDescription(pageTitle);
//						getViewer().refresh();
//					}
//				});
//				return Status.OK_STATUS;
//			} else {
//				Display.getDefault().asyncExec(new Runnable() {
//					public void run() {
//						MessageDialog.openError(Display.getDefault().getActiveShell(), "Task Description Error",
//								"Could not retrieve a description from the specified web page.");
//					}
//				});
//				return Status.CANCEL_STATUS;
//			}
//
//		}
//
//		public void changed(TitleEvent event) {
//			if (!ignoreChangeCall) {
//				if (event.title.equals(taskURL)) {
//					return;
//				} else {
//					ignoreChangeCall = true;
//					// Last one is bugzilla-specific
//					if (event.title.equals(taskURL + "/") || event.title.equals("Object not found!")
//							|| event.title.equals("No page to display") || event.title.equals("Cannot find server")
//							|| event.title.equals("Invalid Bug ID")) {
//						retrievalFailed = true;
//					} else {
//						pageTitle = event.title;
//					}
//				}
//			}
//		}
//	}

}
