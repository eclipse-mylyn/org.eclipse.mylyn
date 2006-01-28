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


import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylar.bugzilla.core.IBugzillaBug;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositorySettingsPage;
import org.eclipse.mylar.internal.bugzilla.core.IOfflineBugListener;
import org.eclipse.mylar.internal.bugzilla.ui.actions.RefreshBugzillaReportsAction;
import org.eclipse.mylar.internal.bugzilla.ui.actions.SynchronizeReportsAction;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask.BugReportSyncState;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask.BugTaskState;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryClient;
import org.eclipse.mylar.internal.tasklist.IRepositoryQuery;
import org.eclipse.mylar.internal.tasklist.ITask;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.TaskRepository;
import org.eclipse.mylar.internal.tasklist.TaskRepositoryManager;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractAddExistingTaskWizard;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.internal.tasklist.ui.wizards.ExistingTaskWizardPage;
import org.eclipse.swt.widgets.Display;

/**
 * @author Mik Kersten
 */
public class BugzillaRepositoryClient extends AbstractRepositoryClient implements IOfflineBugListener {

	private static final String DESCRIPTION_DEFAULT = "<needs synchronize>";

	private static final String LABEL = "Bugzilla (supports uncustomized 2.16-2.20)";

	public BugzillaRepositoryClient() {
		super();
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

	public void synchronize() {
		// TODO: refactor out not to use action
		RefreshBugzillaReportsAction refresh = new RefreshBugzillaReportsAction();
		refresh.setShowProgress(false);
		refresh.run();
		refresh.setShowProgress(true);
	}
	
	@Override
	public Job synchronize(ITask task, boolean forceUpdate) {
		if (task instanceof BugzillaTask) {
			BugzillaTask bugzillaTask = (BugzillaTask)task;
		
			// TODO: refactor these conditions
			boolean canNotSynch = bugzillaTask.isDirty() || bugzillaTask.getState() != BugTaskState.FREE;
			boolean hasLocalChanges = bugzillaTask.getSyncState() == BugReportSyncState.OUTGOING
				|| bugzillaTask.getSyncState() == BugReportSyncState.CONFLICT;
			if (forceUpdate || (!canNotSynch && !hasLocalChanges)) {
				SynchronizeBugzillaReportJob synchronizeBugzillaReportJob = new SynchronizeBugzillaReportJob((BugzillaTask)task);
				synchronizeBugzillaReportJob.schedule();
				return synchronizeBugzillaReportJob;
			}
			if (bugzillaTask.getSyncState() == BugReportSyncState.INCOMING) {
				bugzillaTask.setSyncState(BugReportSyncState.SYNCHRONIZED);
			} else if (bugzillaTask.getSyncState() == BugReportSyncState.CONFLICT) {
				bugzillaTask.setSyncState(BugReportSyncState.OUTGOING);
			}
		}		
		return null;
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
			MessageDialog.openInformation(null, MylarTaskListPlugin.TITLE_DIALOG, "Invalid report id: " + id);
			return null;
		}

		BugzillaTask newTask = new BugzillaTask(TaskRepositoryManager.getHandle(repository.getUrl().toExternalForm(), bugId),
				DESCRIPTION_DEFAULT, true);

		addTaskToArchive(newTask);
		synchronize(newTask, true);
//		newTask.scheduleDownloadReport();
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
			state = BugReportSyncState.SYNCHRONIZED;
		} else if (status == BugzillaOfflineStaus.SAVED_WITH_INCOMMING_CHANGES) {
			state = BugReportSyncState.INCOMING;
		} else if (status == BugzillaOfflineStaus.CONFLICT) {
			state = BugReportSyncState.CONFLICT;
		} else if (status == BugzillaOfflineStaus.DELETED) {
			state = BugReportSyncState.SYNCHRONIZED;
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
			MylarTaskListPlugin.getTaskListManager().notifyRepositoryInfoChanged(bugTask);
		}
	}
}
