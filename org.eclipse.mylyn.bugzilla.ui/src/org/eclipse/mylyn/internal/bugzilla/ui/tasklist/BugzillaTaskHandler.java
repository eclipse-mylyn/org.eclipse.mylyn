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

package org.eclipse.mylar.internal.bugzilla.ui.tasklist;

import org.eclipse.jface.action.Action;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.internal.bugzilla.ui.actions.RefreshBugzillaReportsAction;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.ITaskHandler;
import org.eclipse.mylar.internal.tasklist.ui.ITaskListElement;
import org.eclipse.mylar.internal.tasklist.ui.actions.CopyDescriptionAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.DeleteAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.GoIntoAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.OpenTaskInExternalBrowserAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.OpenTaskListElementAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.RemoveFromCategoryAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.RenameAction;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Mik Kersten and Ken Sueda
 * 
 * TODO: refactor
 */
public class BugzillaTaskHandler implements ITaskHandler {

//	public void itemOpened(ITaskListElement element) {
//
//		boolean offline = MylarTaskListPlugin.getPrefs().getBoolean(TaskListPreferenceConstants.WORK_OFFLINE);
//
//		if (element instanceof BugzillaTask) {
//			BugzillaTask bugzillaTask = (BugzillaTask) element;
//			TaskListUiUtil.openEditor(bugzillaTask);
//			MylarTaskListPlugin.ReportOpenMode mode = MylarTaskListPlugin.getDefault().getReportMode();
//			if (mode == MylarTaskListPlugin.ReportOpenMode.EDITOR) {
//				bugzillaTask.openTaskInEditor(offline);
//			} else if (mode == MylarTaskListPlugin.ReportOpenMode.INTERNAL_BROWSER) {
//				if (offline) {
//					MessageDialog.openInformation(null, "Unable to open bug",
//							"Unable to open the selected bugzilla task since you are currently offline");
//					return;
//				}
//				String title = "Bug #" + TaskRepositoryManager.getTaskIdAsInt(bugzillaTask.getHandleIdentifier());
//				TaskListUiUtil.openUrl(title, title, bugzillaTask.getUrl());
//			} else {
//				// not supported
//			}
//		} else if (element instanceof BugzillaCustomQueryCategory) {
//			BugzillaCustomQueryCategory queryCategory = (BugzillaCustomQueryCategory) element;
//			BugzillaCustomQueryDialog sqd = new BugzillaCustomQueryDialog(Display.getCurrent().getActiveShell(),
//					queryCategory.getQueryUrl(), queryCategory.getDescription(), queryCategory.getMaxHits() + "");
//			if (sqd.open() == Dialog.OK) {
//				queryCategory.setDescription(sqd.getName());
//				queryCategory.setQueryUrl(sqd.getUrl());
//				int maxHits = -1;
//				try {
//					maxHits = Integer.parseInt(sqd.getMaxHits());
//				} catch (Exception e) {
//				}
//				queryCategory.setMaxHits(maxHits);
//
//				new SynchronizeReportsAction(queryCategory).run();
//			}
//		} else if (element instanceof BugzillaQueryCategory) {
//			BugzillaQueryCategory queryCategory = (BugzillaQueryCategory) element;
//			BugzillaQueryDialog queryDialog = new BugzillaQueryDialog(Display.getCurrent().getActiveShell(),
//					queryCategory.getRepositoryUrl(), queryCategory.getQueryUrl(), queryCategory.getDescription(),
//					queryCategory.getMaxHits() + "");
//			if (queryDialog.open() == Dialog.OK) {
//				queryCategory.setDescription(queryDialog.getName());
//				queryCategory.setQueryUrl(queryDialog.getUrl());
//				queryCategory.setRepositoryUrl(queryDialog.getRepository().getUrl().toExternalForm());
//				int maxHits = -1;
//				try {
//					maxHits = Integer.parseInt(queryDialog.getMaxHits());
//				} catch (Exception e) {
//				}
//				queryCategory.setMaxHits(maxHits);
//
//				new SynchronizeReportsAction(queryCategory).run();
//			}
//		} else if (element instanceof BugzillaQueryHit) {
//			BugzillaQueryHit hit = (BugzillaQueryHit) element;
//			MylarTaskListPlugin.ReportOpenMode mode = MylarTaskListPlugin.getDefault().getReportMode();
//			if (mode == MylarTaskListPlugin.ReportOpenMode.EDITOR) {
//				if (hit.getCorrespondingTask() != null) {
//					hit.getCorrespondingTask().openTaskInEditor(offline);
//				} else {
//					if (offline) {
//						MessageDialog.openInformation(null, "Unable to open bug",
//								"Unable to open the selected bugzilla report since you are currently offline");
//						return;
//					}
//					BugzillaOpenStructure open = new BugzillaOpenStructure(((BugzillaQueryHit) element)
//							.getRepositoryUrl(), ((BugzillaQueryHit) element).getId(), -1);
//					List<BugzillaOpenStructure> selectedBugs = new ArrayList<BugzillaOpenStructure>();
//					selectedBugs.add(open);
//					ViewBugzillaAction viewBugs = new ViewBugzillaAction("Display bugs in editor", selectedBugs);
//					viewBugs.schedule();
//				}
//			}
//			else if (mode == MylarTaskListPlugin.ReportOpenMode.INTERNAL_BROWSER) {
//				if (offline) {
//					MessageDialog.openInformation(null, "Unable to open bug",
//							"Unable to open the selected bugzilla report since you are currently offline");
//					return;
//				}
//				String title = "Bug #" + TaskRepositoryManager.getTaskIdAsInt(hit.getHandleIdentifier());
//				TaskListUiUtil.openUrl(title, title, hit.getBugUrl());
//			} else {
//				// not supported
//			}
//		}
//	}

	public boolean acceptsItem(ITaskListElement element) {
		return element instanceof BugzillaTask || element instanceof BugzillaQueryHit
				|| element instanceof BugzillaQueryCategory;
	}

	public void taskClosed(ITask element, IWorkbenchPage page) {
		try {
			IEditorInput input = null;
			if (element instanceof BugzillaTask) {
				input = new BugzillaTaskEditorInput((BugzillaTask) element, true);
			}
			IEditorPart editor = page.findEditor(input);

			if (editor != null) {
				page.closeEditor(editor, false);
			}
		} catch (Exception e) {
			MylarStatusHandler.log(e, "Error while trying to close a bugzilla task");
		}
	}

	public ITask addTaskToRegistry(ITask newTask) {
		if (newTask instanceof BugzillaTask) {
			BugzillaTask bugTask = BugzillaUiPlugin.getDefault().getBugzillaTaskListManager()
					.getFromBugzillaTaskRegistry(newTask.getHandleIdentifier());
			if (bugTask == null) {
				BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().addToBugzillaTaskRegistry(
						(BugzillaTask) newTask);
				bugTask = (BugzillaTask) newTask;
			}
			return bugTask;
		}
		return null;
	}

	public void restoreState(TaskListView taskListView) {
		if (BugzillaPlugin.getDefault().refreshOnStartUpEnabled()) {
			RefreshBugzillaReportsAction refresh = new RefreshBugzillaReportsAction();
			refresh.setShowProgress(false);
			refresh.run();
			refresh.setShowProgress(true);
		}
	}

	public boolean enableAction(Action action, ITaskListElement element) {

		if (element instanceof BugzillaQueryHit) {
			BugzillaQueryHit hit = (BugzillaQueryHit) element;
			if (hit.getCorrespondingTask() != null && hit.getCorrespondingTask().hasValidUrl()) {
				return true;
			}
			return false;
		} else if (element instanceof BugzillaTask) {
			if (action instanceof OpenTaskInExternalBrowserAction) {
				if (((ITask) element).hasValidUrl()) {
					return true;
				} else {
					return false;
				}
			} else if (action instanceof DeleteAction || action instanceof CopyDescriptionAction
					|| action instanceof OpenTaskListElementAction || action instanceof RemoveFromCategoryAction) {
				return true;
			} else {
				return false;
			}
		} else if (element instanceof BugzillaQueryCategory) {
			if (action instanceof DeleteAction || action instanceof CopyDescriptionAction
					|| action instanceof OpenTaskListElementAction || action instanceof RenameAction) {
				return true;
			} else if (action instanceof GoIntoAction) {
				BugzillaQueryCategory cat = (BugzillaQueryCategory) element;
				if (cat.getHits().size() > 0) {
					return true;
				}
			} else {
				return false;
			}
		}
		return false;
	}

}

// public ITask getCorrespondingTask(IQueryHit queryHit) {
// if (queryHit instanceof BugzillaQueryHit) {
// BugzillaQueryHit hit = (BugzillaQueryHit) queryHit;
// return hit.getOrCreateCorrespondingTask();
// } else {
// return null;
// }
// }

// public void itemRemoved(ITaskListElement element, ITaskCategory category) {
// if (element instanceof BugzillaTask) {
// BugzillaTask task = (BugzillaTask) element;
// if (category instanceof TaskCategory) {
// MylarTaskListPlugin.getTaskListManager().removeFromCategoryAndRoot((TaskCategory)
// category, task);
// // category.removeTask(task);
// } else {
// String message = MESSAGE_CONFIRM_DELETE;
// boolean deleteConfirmed =
// MessageDialog.openQuestion(Workbench.getInstance().getActiveWorkbenchWindow()
// .getShell(), "Confirm delete", message);
// if (!deleteConfirmed)
// return;
// MylarTaskListPlugin.getTaskListManager().deleteTask(task);
// }
// }
// }

// private static final String MESSAGE_CONFIRM_DELETE = "Remove this report from
// the task list, and discard any task context or local notes?";

// public boolean deleteElement(ITaskListElement element) {
// if (element instanceof BugzillaQueryCategory) {
// boolean deleteConfirmed =
// MessageDialog.openQuestion(Workbench.getInstance().getActiveWorkbenchWindow()
// .getShell(), "Confirm delete", "Delete the selected query and all contained
// tasks?");
// if (!deleteConfirmed)
// return false;
// BugzillaQueryCategory query = (BugzillaQueryCategory) element;
// MylarTaskListPlugin.getTaskListManager().deleteQuery(query);
// } else if (element instanceof BugzillaTask) {
// BugzillaTask task = (BugzillaTask) element;
// if (task.isActive()) {
// MessageDialog.openError(Workbench.getInstance().getActiveWorkbenchWindow().getShell(),
// "Delete failed",
// "Task must be deactivated in order to delete.");
// return false;
// }
//
// // String message = task.getDeleteConfirmationMessage();
// boolean deleteConfirmed =
// MessageDialog.openQuestion(Workbench.getInstance().getActiveWorkbenchWindow()
// .getShell(), "Confirm delete", MESSAGE_CONFIRM_DELETE);
// if (!deleteConfirmed)
// return false;
//
// // task.removeReport();
// MylarTaskListPlugin.getTaskListManager().deleteTask(task);
// MylarPlugin.getContextManager().contextDeleted(task.getHandleIdentifier());
// IWorkbenchPage page =
// MylarTaskListPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()
// .getActivePage();
//
// // if we couldn't get the page, get out of here
// if (page == null)
// return true;
// try {
// TaskListView.getDefault().closeTaskEditors(task, page);
// } catch (Exception e) {
// MylarStatusHandler.log(e, " deletion failed");
// }
// }
// TaskListView.getDefault().getViewer().refresh();
// return true;
// }

// public void dropItem(ITaskListElement element, TaskCategory cat) {
// if (element instanceof BugzillaQueryHit) {
// BugzillaQueryHit bugzillaHit = (BugzillaQueryHit) element;
// if (bugzillaHit.getAssociatedTask() != null) {
// MylarTaskListPlugin.getTaskListManager().moveToCategory(cat,
// bugzillaHit.getAssociatedTask());
// } else {
// BugzillaTask bugzillaTask = new BugzillaTask(bugzillaHit, true);
// bugzillaHit.setAssociatedTask(bugzillaTask);
// MylarTaskListPlugin.getTaskListManager().moveToCategory(cat,
// bugzillaTask);
// BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().addToBugzillaTaskRegistry(bugzillaTask);
// }
// }
// }
