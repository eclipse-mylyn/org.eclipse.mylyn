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

package org.eclipse.mylar.provisional.tasklist;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.core.util.ZipFileUtil;
import org.eclipse.mylar.internal.tasklist.OfflineTaskManager;
import org.eclipse.mylar.internal.tasklist.RepositoryAttachment;
import org.eclipse.mylar.internal.tasklist.RepositoryTaskData;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.ui.PlatformUI;

/**
 * Encapsulates synchronization policy.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 */
public abstract class AbstractRepositoryConnector {

	private static final String MESSAGE_ATTACHMENTS_NOT_SUPPORTED = "Attachments not supported by connector: ";

	private static final int RETRY_DELAY = 3000;

	private static final int MAX_QUERY_ATTEMPTS = 3;

	public static final String MYLAR_CONTEXT_DESCRIPTION = "mylar/context/zip";

	private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

	private static final String ZIPFILE_EXTENSION = ".zip";

	protected List<String> supportedVersions;

	protected boolean forceSyncExecForTesting = false;

	private boolean updateLocalCopy = false;

	/**
	 * @return null if not supported
	 */
	public abstract IAttachmentHandler getAttachmentHandler();

	/**
	 * @return null if not supported
	 */
	public abstract IOfflineTaskHandler getOfflineTaskHandler();

	public abstract String getRepositoryUrlFromTaskUrl(String url);

	public abstract boolean canCreateTaskFromKey();

	public abstract boolean canCreateNewTask();

	/**
	 * Implementors must execute query synchronously.
	 * 
	 * @param query
	 * @param monitor
	 * @param queryStatus
	 *            set an exception on queryStatus.getChildren[0] to indicate
	 *            failure
	 */
	public abstract List<AbstractQueryHit> performQuery(AbstractRepositoryQuery query, IProgressMonitor monitor,
			MultiStatus queryStatus);

	public abstract String getLabel();

	/**
	 * @return the unique type of the repository, e.g. "bugzilla"
	 */
	public abstract String getRepositoryType();

	/**
	 * @param id
	 *            identifier, e.g. "123" bug Bugzilla bug 123
	 * @return null if task could not be created
	 */
	public abstract ITask createTaskFromExistingKey(TaskRepository repository, String id);

	public abstract AbstractRepositorySettingsPage getSettingsPage();

	public abstract IWizard getNewQueryWizard(TaskRepository repository);

	public abstract void openEditQueryDialog(AbstractRepositoryQuery query);

	public abstract IWizard getAddExistingTaskWizard(TaskRepository repository);

	public abstract IWizard getNewTaskWizard(TaskRepository taskRepository);

	public abstract List<String> getSupportedVersions();

	protected abstract void updateTaskState(AbstractRepositoryTask repositoryTask);

	/**
	 * returns all tasks if date is null or an error occurs
	 */
	public abstract Set<AbstractRepositoryTask> getChangedSinceLastSync(TaskRepository repository,
			Set<AbstractRepositoryTask> tasks) throws Exception;

	/**
	 * Implementors of this repositoryOperations must perform it locally without
	 * going to the server since it is used for frequent repositoryOperations
	 * such as decoration.
	 * 
	 * @return an emtpy set if no contexts
	 */
	public final Set<RepositoryAttachment> getContextAttachments(TaskRepository repository, AbstractRepositoryTask task) {
		Set<RepositoryAttachment> contextAttachments = new HashSet<RepositoryAttachment>();
		if (task.getTaskData() != null) {
			for (RepositoryAttachment attachment : task.getTaskData().getAttachments()) {
				if (attachment.getDescription().equals(MYLAR_CONTEXT_DESCRIPTION)) {
					contextAttachments.add(attachment);
				}
			}
		}
		return contextAttachments;
	}

	// TODO: move
	public final boolean hasRepositoryContext(TaskRepository repository, AbstractRepositoryTask task) {
		if (repository == null || task == null) {
			return false;
		} else {
			Set<RepositoryAttachment> remoteContextAttachments = getContextAttachments(repository, task);
			return (remoteContextAttachments != null && remoteContextAttachments.size() > 0);
		}
	}

	public final void attachContext(TaskRepository repository, AbstractRepositoryTask task, String longComment)
			throws CoreException {
		if (!repository.hasCredentials()) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					MylarTaskListPlugin.TITLE_DIALOG, "Repository credentials missing or invalid.");
			return;
		} else {
			MylarPlugin.getContextManager().saveContext(task.getHandleIdentifier());
			File sourceContextFile = MylarPlugin.getContextManager().getFileForContext(task.getHandleIdentifier());

			if (sourceContextFile != null && sourceContextFile.exists()) {
				try {
					List<File> filesToZip = new ArrayList<File>();
					filesToZip.add(sourceContextFile);

					File destinationFile = File.createTempFile(sourceContextFile.getName(), ZIPFILE_EXTENSION);
					destinationFile.deleteOnExit();
					ZipFileUtil.createZipFile(destinationFile, filesToZip, new NullProgressMonitor());

					IAttachmentHandler handler = getAttachmentHandler();
					if (handler != null) {
						handler.uploadAttachment(repository, task, longComment, MYLAR_CONTEXT_DESCRIPTION,
								destinationFile, APPLICATION_OCTET_STREAM, false, null);
						synchronize(task, false, null);
					} else {
						MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
								MylarTaskListPlugin.TITLE_DIALOG, MESSAGE_ATTACHMENTS_NOT_SUPPORTED + getLabel());
					}
				} catch (Exception e) {
					MylarStatusHandler.fail(e, "Could not export task context as zip file", true);
				}
			}
		}
	}

	public final void retrieveContext(TaskRepository repository, AbstractRepositoryTask task,
			RepositoryAttachment attachment) throws CoreException, IOException {
		boolean wasActive = false;
		if (task.isActive()) {
			wasActive = true;
			MylarTaskListPlugin.getTaskListManager().deactivateTask(task);
		}

		File destinationContextFile = MylarPlugin.getContextManager().getFileForContext(task.getHandleIdentifier());

		File destinationZipFile = new File(destinationContextFile.getPath() + ZIPFILE_EXTENSION);

		Proxy proxySettings = MylarTaskListPlugin.getDefault().getProxySettings();
		IAttachmentHandler attachmentHandler = getAttachmentHandler();
		if (attachmentHandler != null) {
			attachmentHandler.downloadAttachment(repository, task, attachment.getId(), destinationZipFile,
					proxySettings);
			ZipFileUtil.unzipFiles(destinationZipFile, MylarPlugin.getDefault().getDataDirectory());
			if (destinationContextFile.exists()) {
				MylarTaskListPlugin.getTaskListManager().getTaskList().notifyLocalInfoChanged(task);
				if (wasActive) {
					MylarTaskListPlugin.getTaskListManager().activateTask(task);
				}
			}
		} else {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					MylarTaskListPlugin.TITLE_DIALOG, MESSAGE_ATTACHMENTS_NOT_SUPPORTED + getLabel());
		}
	}

	// Precondition of note: offline file is removed upon submit to repository
	// resulting in a synchronized state.
	void updateOfflineState(final AbstractRepositoryTask repositoryTask, boolean forceSync) {
		IOfflineTaskHandler offlineTaskHandler = getOfflineTaskHandler();
		if (offlineTaskHandler == null) {
			return;
		}
		RepositoryTaskSyncState status = repositoryTask.getSyncState();
		RepositoryTaskData downloadedTaskData = null;

		final TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getRepository(
				repositoryTask.getRepositoryKind(), repositoryTask.getRepositoryUrl());

		if (repository == null) {
			MylarStatusHandler.log("No repository associated with task " + repositoryTask.getDescription()
					+ ". Unable to retrieve timezone information.", this);
			return;
		}

		try {
			downloadedTaskData = offlineTaskHandler.downloadTaskData(repositoryTask);
		} catch (final CoreException e) {
			if (forceSync) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						ErrorDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
								"Error Downloading Report", "Unable to synchronize " + repositoryTask.getDescription()
										+ " on " + repository.getUrl(), e.getStatus());
					}
				});
			}
			return;
		}

		if (downloadedTaskData == null) {
			MylarStatusHandler.log("Download of " + repositoryTask.getDescription() + " from "
					+ repositoryTask.getRepositoryUrl() + " failed.", this);
			return;
		}

		RepositoryTaskData offlineTaskData = OfflineTaskManager.findBug(downloadedTaskData.getRepositoryUrl(),
				downloadedTaskData.getId());

		if (offlineTaskData != null) {
			switch (status) {
			case OUTGOING:
				// Should not occur if forceSync = false
			case CONFLICT:
				// Should not occur if forceSync = false
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
					public void run() {
						updateLocalCopy = MessageDialog
								.openQuestion(
										null,
										"Update Local Copy",
										"Local copy of Report "
												+ repositoryTask.getDescription()
												+ " on "
												+ repositoryTask.getRepositoryUrl()
												+ " has changes.\nWould you like to override local changes? \n\nNote: if you select No, only the new comment will be saved with the updated bug, all other changes will be lost.");
					}
				});
				if (!updateLocalCopy) {
					downloadedTaskData.setNewComment(offlineTaskData.getNewComment());
					downloadedTaskData.setHasChanged(true);
					status = RepositoryTaskSyncState.CONFLICT;
				} else {
					downloadedTaskData.setHasChanged(false);
					status = RepositoryTaskSyncState.SYNCHRONIZED;
				}
				break;
			case INCOMING:
				status = RepositoryTaskSyncState.SYNCHRONIZED;
				break;
			case SYNCHRONIZED:
				if (repositoryTask.getLastSynchronized() == null
						|| downloadedTaskData.getLastModified(repository.getTimeZoneId()).compareTo(
								repositoryTask.getLastSynchronized()) > 0) {
					status = RepositoryTaskSyncState.INCOMING;
				}
				break;
			}
			removeOfflineTaskData(offlineTaskData);
		} else {
			status = RepositoryTaskSyncState.SYNCHRONIZED;
		}
		repositoryTask.setLastSynchronized(downloadedTaskData.getLastModified(repository.getTimeZoneId()));
		repositoryTask.setTaskData(downloadedTaskData);
		repositoryTask.setSyncState(status);
		saveOffline(downloadedTaskData);
	}

	/**
	 * Sychronize a single task. Note that if you have a collection of tasks to
	 * synchronize with this connector then you should call synchronize(Set<Set<AbstractRepositoryTask>
	 * repositoryTasks, ...)
	 * 
	 * @param listener
	 *            can be null
	 */
	public final Job synchronize(AbstractRepositoryTask repositoryTask, boolean forceSynch, IJobChangeListener listener) {
		Set<AbstractRepositoryTask> toSync = new HashSet<AbstractRepositoryTask>();
		toSync.add(repositoryTask);
		return synchronize(toSync, forceSynch, listener);
	}

	/**
	 * @param listener
	 *            can be null
	 */
	private Job synchronize(Set<AbstractRepositoryTask> repositoryTasks, boolean forceSynch, IJobChangeListener listener) {

		final SynchronizeTaskJob synchronizeJob = new SynchronizeTaskJob(this, repositoryTasks);
		synchronizeJob.setForceSynch(forceSynch);
		synchronizeJob.setPriority(Job.DECORATE);
		if (listener != null) {
			synchronizeJob.addJobChangeListener(listener);
		}

		if (!forceSyncExecForTesting) {
			synchronizeJob.schedule();
		} else {
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				public void run() {
					synchronizeJob.run(new NullProgressMonitor());
				}
			});
		}
		return synchronizeJob;

	}

	/**
	 * For synchronizing a single query. Use synchronize(Set,
	 * IJobChangeListener) if synchronizing multiple queries at a time.
	 */
	public final Job synchronize(final AbstractRepositoryQuery repositoryQuery, IJobChangeListener listener) {
		HashSet<AbstractRepositoryQuery> items = new HashSet<AbstractRepositoryQuery>();
		items.add(repositoryQuery);
		return synchronize(items, listener, Job.LONG, 0, true);
	}

	public final Job synchronize(final Set<AbstractRepositoryQuery> repositoryQueries, IJobChangeListener listener,
			int priority, long delay, boolean syncTasks) {
		SynchronizeQueryJob job = new SynchronizeQueryJob(this, repositoryQueries);
		job.setSynchTasks(syncTasks);
		if (listener != null) {
			job.addJobChangeListener(listener);
		}
		job.setPriority(priority);
		job.schedule(delay);
		return job;
	}

	/**
	 * Synchronizes only those tasks that have changed since the last time the
	 * given repository was synchronized. Calls to this method set
	 * TaskRepository.syncTime to now.
	 */
	public final void synchronizeChanged(final TaskRepository repository) {
		TaskList taskList = MylarTaskListPlugin.getTaskListManager().getTaskList();
		Set<AbstractRepositoryTask> repositoryTasks = Collections.unmodifiableSet(taskList
				.getRepositoryTasks(repository.getUrl()));

		final Set<AbstractRepositoryTask> tasksToSync = new HashSet<AbstractRepositoryTask>();
		Set<AbstractRepositoryTask> changedTasks = null;
		int attempts = 0;

		while (attempts < MAX_QUERY_ATTEMPTS && changedTasks == null) {
			attempts++;
			try {
				changedTasks = getChangedSinceLastSync(repository, repositoryTasks);
			} catch (Exception e) {
				if (attempts == MAX_QUERY_ATTEMPTS) {
					Date now = new Date();
					MylarStatusHandler.log(e, "Could not determine modified tasks for " + repository.getUrl() + ". ["
							+ now.toString() + "]");
					return;
				}
				try {
					Thread.sleep(RETRY_DELAY);
				} catch (InterruptedException e1) {
					return;
				}
			}
		}

		for (AbstractRepositoryTask task : changedTasks) {
			if (task.getSyncState() == RepositoryTaskSyncState.SYNCHRONIZED) {
				tasksToSync.add(task);
			}
		}

		synchronize(tasksToSync, false, new JobChangeAdapter() {

			@Override
			public void done(IJobChangeEvent event) {
				for (AbstractRepositoryTask task : Collections.unmodifiableSet(tasksToSync)) {
					if (repository.getSyncTime().after(task.getLastSynchronized())) {
						return;
					}
				}
				MylarTaskListPlugin.getRepositoryManager().setSyncTime(repository, new Date());
			}
		});
	}

	/**
	 * Force the given task to be refreshed from the repository
	 */
	public final void forceRefresh(AbstractRepositoryTask task) {
		Set<AbstractRepositoryTask> toRefresh = new HashSet<AbstractRepositoryTask>();
		toRefresh.add(task);
		synchronize(toRefresh, true, null);
	}

	/**
	 * For testing
	 */
	public final void setForceSyncExec(boolean forceSyncExec) {
		this.forceSyncExecForTesting = forceSyncExec;
	}

	protected final void removeOfflineTaskData(RepositoryTaskData bug) {
		if (bug == null)
			return;
		// bug.setOfflineState(false);
		// offlineStatusChange(bug, RepositoryTaskSyncState.SYNCHRONIZED);
		ArrayList<RepositoryTaskData> bugList = new ArrayList<RepositoryTaskData>();
		bugList.add(bug);
		MylarTaskListPlugin.getDefault().getOfflineReportsFile().remove(bugList);
	}

	public final void saveOffline(RepositoryTaskData taskData) {
		try {
			MylarTaskListPlugin.getDefault().getOfflineReportsFile().add(taskData);
		} catch (CoreException e) {
			MylarStatusHandler.fail(e, e.getMessage(), false);
		}
	}

	public void openRemoteTask(String repositoryUrl, String idString) {
		MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				MylarTaskListPlugin.TITLE_DIALOG, "Not supported by connector: " + getLabel());
	}
}
