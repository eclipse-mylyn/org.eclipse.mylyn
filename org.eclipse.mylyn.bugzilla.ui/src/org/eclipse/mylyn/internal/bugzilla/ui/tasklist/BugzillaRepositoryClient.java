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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylar.bugzilla.core.IBugzillaBug;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositorySettingsPage;
import org.eclipse.mylar.internal.bugzilla.core.IOfflineBugListener;
import org.eclipse.mylar.internal.bugzilla.ui.actions.SynchronizeReportsAction;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask.BugReportSyncState;
import org.eclipse.mylar.internal.tasklist.TaskCategory;
import org.eclipse.mylar.internal.tasklist.TaskRepositoryManager;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractAddExistingTaskWizard;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.internal.tasklist.ui.wizards.ExistingTaskWizardPage;
import org.eclipse.mylar.tasklist.IRepositoryQuery;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskRepositoryClient;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.TaskRepository;
import org.eclipse.swt.widgets.Display;

/**
 * @author Mik Kersten
 */
public class BugzillaRepositoryClient implements ITaskRepositoryClient, IOfflineBugListener {

	private static final String LABEL = "Bugzilla (supports uncustomized 2.16-2.20)";

	private Map<String, BugzillaTask> bugzillaTaskArchive = new HashMap<String, BugzillaTask>();

	private TaskCategory archiveCategory = null;
	
	public BugzillaRepositoryClient() {
		// TODO: remove on dispose?
		BugzillaPlugin.getDefault().addOfflineStatusListener(this);
	}
	
	public String getLabel() {
		return LABEL;
	}

	public String toString() {
		return getLabel();
	}

	public AbstractRepositorySettingsPage getSettingsPage() {
		return new BugzillaRepositorySettingsPage();
	}

	public String getKind() {
		return BugzillaPlugin.REPOSITORY_KIND;
	}

	public void addTaskToArchive(ITask newTask) {
		if (newTask instanceof BugzillaTask 
				&& !bugzillaTaskArchive.containsKey(newTask.getHandleIdentifier())) { 
			bugzillaTaskArchive.put(newTask.getHandleIdentifier(), (BugzillaTask)newTask);
			if (archiveCategory != null) {
				archiveCategory.internalAddTask(newTask);
			}
		}
	}
			 
//			ITask taskToAdd = getFromBugzillaTaskRegistry(newTask.getHandleIdentifier());
//			BugzillaTask bugTask = BugzillaUiPlugin.getDefault().getBugzillaTaskListManager()
//					.getFromBugzillaTaskRegistry(newTask.getHandleIdentifier());
			 
//			if (retrievedTask instanceof BugzillaTask) {
//				if (bugzillaTaskArchive.get(newTask.getHandleIdentifier()) == null) {

//				}
//				retrievedTask = (BugzillaTask) newTask;
//			}
//		}
//	}

	public ITask getFromBugzillaTaskRegistry(String handle) {
		return bugzillaTaskArchive.get(handle);
	}

	public List<ITask> getArchiveTasks() {
		List<ITask> archiveTasks = new ArrayList<ITask>();
		archiveTasks.addAll(bugzillaTaskArchive.values());
		return archiveTasks;
	}
	
//	public Map<String, BugzillaTask> getBugzillaTaskRegistry() {
//		return bugzillaTaskArchive;
//	}

	public void setArchiveCategory(TaskCategory category) {
		this.archiveCategory = category;
	}
 
	
	public ITask createTaskFromExistingId(TaskRepository repository, String id) {
		int bugId = -1;
		try {
			if (id != null) {
				bugId = Integer.parseInt(id);
			} else {
				return null;
			}
		} catch (NumberFormatException nfe) {
			TaskListView.getDefault().showMessage("Invalid report id.");
			return null;
		}

		BugzillaTask newTask = new BugzillaTask(TaskRepositoryManager.getHandle(repository.getUrl().toExternalForm(), bugId),
				"<bugzilla info>", true, true);

		// ITaskHandler taskHandler =
		// MylarTaskListPlugin.getDefault().getHandlerForElement(newTask);
		// if (taskHandler != null) {
		addTaskToArchive(newTask);
		newTask.scheduleDownloadReport();
//		if (addedTask instanceof BugzillaTask) {
//			BugzillaTask newTask2 = (BugzillaTask) addedTask;
//			if (newTask2 == newTask) {
//				((BugzillaTask) newTask).scheduleDownloadReport();
//			} else {
//				newTask = newTask2;
//				((BugzillaTask) newTask).updateTaskDetails();
//			}
//			// }
//		} else {
//			((BugzillaTask) newTask).scheduleDownloadReport();
//		}
		return newTask;
	}

	public IWizard getQueryWizard(TaskRepository repository) {
		return new AddBugzillaQueryWizard(repository);
	}

	public IWizard getAddExistingTaskWizard(TaskRepository repository) {

		// TODO create a propper subclass for Bugzilla
		return new AbstractAddExistingTaskWizard(repository) {

			private ExistingTaskWizardPage page;

			public void addPages() {
				super.addPages();
				this.page = new ExistingTaskWizardPage();
				addPage(page);
			}

			protected String getTaskId() {
				return page.getTaskId();
			}
		};
	}

	public void openEditQueryDialog(IRepositoryQuery query) {
		if (query instanceof BugzillaCustomQueryCategory) {
			BugzillaCustomQueryCategory queryCategory = (BugzillaCustomQueryCategory) query;
			BugzillaCustomQueryDialog sqd = new BugzillaCustomQueryDialog(Display.getCurrent().getActiveShell(),
					queryCategory.getQueryUrl(), queryCategory.getDescription(), queryCategory.getMaxHits() + "");
			if (sqd.open() == Dialog.OK) {
				queryCategory.setDescription(sqd.getName());
				queryCategory.setQueryUrl(sqd.getUrl());
				int maxHits = -1;
				try {
					maxHits = Integer.parseInt(sqd.getMaxHits());
				} catch (Exception e) {
				}
				queryCategory.setMaxHits(maxHits);

				new SynchronizeReportsAction(queryCategory).run();
			}
		} else if (query instanceof BugzillaQueryCategory) {
			BugzillaQueryCategory queryCategory = (BugzillaQueryCategory) query;
			BugzillaQueryDialog queryDialog = new BugzillaQueryDialog(Display.getCurrent().getActiveShell(),
					queryCategory.getRepositoryUrl(), queryCategory.getQueryUrl(), queryCategory.getDescription(),
					queryCategory.getMaxHits() + "");
			if (queryDialog.open() == Dialog.OK) {
				queryCategory.setDescription(queryDialog.getName());
				queryCategory.setQueryUrl(queryDialog.getUrl());
				queryCategory.setRepositoryUrl(queryDialog.getRepository().getUrl().toExternalForm());
				int maxHits = -1;
				try {
					maxHits = Integer.parseInt(queryDialog.getMaxHits());
				} catch (Exception e) {
				}
				queryCategory.setMaxHits(maxHits);

				new SynchronizeReportsAction(queryCategory).run();
			}
		}
	}
	
	public void offlineStatusChange(IBugzillaBug bug, BugzillaOfflineStaus status) {
		BugReportSyncState state = null;
		if (status == BugzillaOfflineStaus.SAVED_WITH_OUTGOING_CHANGES) {
			state = BugReportSyncState.OUTGOING;
		} else if (status == BugzillaOfflineStaus.SAVED) {
			state = BugReportSyncState.OK;
		} else if (status == BugzillaOfflineStaus.SAVED_WITH_INCOMMING_CHANGES) {
			state = BugReportSyncState.INCOMMING;
		} else if (status == BugzillaOfflineStaus.CONFLICT) {
			state = BugReportSyncState.CONFLICT;
		}
		if (state == null) {
			// this means that we got a status that we didn't understand
			return;
		}

		String handle = TaskRepositoryManager.getHandle(bug.getRepository(), bug.getId());
		ITask task = MylarTaskListPlugin.getTaskListManager().getTaskForHandle(handle, true);
		if (task != null && task instanceof BugzillaTask) {
			BugzillaTask bugTask = (BugzillaTask) task;
			bugTask.setSyncState(state);
			MylarTaskListPlugin.getTaskListManager().notifyTaskChanged(bugTask);
		}
	}
}
