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
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.core.util.ZipFileUtil;
import org.eclipse.mylar.internal.tasklist.RepositoryAttachment;
import org.eclipse.mylar.internal.tasklist.RepositoryTaskAttribute;
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
	
	private final MutexRule rule = new MutexRule();

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

	public String[] repositoryPropertyNames() {
		return new String[] { TaskRepositoryManager.PROPERTY_VERSION, TaskRepositoryManager.PROPERTY_TIMEZONE,
				TaskRepositoryManager.PROPERTY_ENCODING };
	}

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
	/**
	 * @return true if call results in change of syc state
	 */
	public synchronized boolean updateOfflineState(final AbstractRepositoryTask repositoryTask,
			final RepositoryTaskData newTaskData, boolean forceSync) {
		RepositoryTaskSyncState startState = repositoryTask.getSyncState();
		RepositoryTaskSyncState status = repositoryTask.getSyncState();

		if (newTaskData == null) {
			MylarStatusHandler.log("Download of " + repositoryTask.getDescription() + " from "
					+ repositoryTask.getRepositoryUrl() + " failed.", this);
			return false;
		}

		RepositoryTaskData offlineTaskData = repositoryTask.getTaskData();
		// loadOfflineTaskData(repositoryTask)

		if (newTaskData.hasLocalChanges()) {
			// Special case for saving changes to local task data
			status = RepositoryTaskSyncState.OUTGOING;
		} else {

			switch (status) {
			case OUTGOING:
				if (!forceSync) {
					// Never overwrite local task data unless forced
					return false;
				}
			case CONFLICT:
				// use a parameter rather than this null check
				if (offlineTaskData != null) {
					// TODO: pull this ui out of here
					if (!forceSyncExecForTesting) {
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
					} else {
						updateLocalCopy = true;
					}
					if (!updateLocalCopy) {
						newTaskData.setNewComment(offlineTaskData.getNewComment());
						newTaskData.setHasLocalChanges(true);
						status = RepositoryTaskSyncState.CONFLICT;
					} else {
						newTaskData.setHasLocalChanges(false);
						if (checkHasIncoming(repositoryTask, newTaskData)) {
							status = RepositoryTaskSyncState.INCOMING;
						} else {
							status = RepositoryTaskSyncState.SYNCHRONIZED;
						}
					}
				} else {
					newTaskData.setHasLocalChanges(false);
					status = RepositoryTaskSyncState.SYNCHRONIZED;
				}
				break;
			case INCOMING:
				if (!forceSync && checkHasIncoming(repositoryTask, newTaskData)) {
					status = RepositoryTaskSyncState.INCOMING;
				} else {
					status = RepositoryTaskSyncState.SYNCHRONIZED;
				}
				break;
			case SYNCHRONIZED:
				if (checkHasIncoming(repositoryTask, newTaskData)) {
					status = RepositoryTaskSyncState.INCOMING;
				} else {
					status = RepositoryTaskSyncState.SYNCHRONIZED;
				}
				break;
			}

			repositoryTask.setModifiedDateStamp(newTaskData.getLastModified());
		}
		// if (offlineTaskData != null) {
		// removeOfflineTaskData(offlineTaskData);
		// }

		repositoryTask.setTaskData(newTaskData);
		repositoryTask.setSyncState(status);
		saveOffline(newTaskData);
		if(status == RepositoryTaskSyncState.INCOMING) {
			repositoryTask.setNotified(false);
		}
		return startState == repositoryTask.getSyncState();
		// } catch (final CoreException e) {
		// if (forceSync) {
		// PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
		// public void run() {
		// ErrorDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
		// "Error Downloading Report", "Unable to synchronize " +
		// repositoryTask.getDescription(),
		// e.getStatus());
		// }
		// });
		// }
		// // else {
		// // MylarStatusHandler.fail(e, "Unable to synchronize " +
		// // repositoryTask.getDescription()
		// // + " on " + repository.getUrl(), false);
		// // }
		// return;
		// }
	}

	/** public for testing purposes */
	public boolean checkHasIncoming(AbstractRepositoryTask repositoryTask, RepositoryTaskData newData) {

		RepositoryTaskAttribute modifiedDateAttribute = newData.getAttribute(RepositoryTaskAttribute.DATE_MODIFIED);
		if (repositoryTask.getLastModifiedDateStamp() != null && modifiedDateAttribute != null
				&& modifiedDateAttribute.getValue() != null) {
			Date newModifiedDate = getOfflineTaskHandler().getDateForAttributeType(
					RepositoryTaskAttribute.DATE_MODIFIED, modifiedDateAttribute.getValue());
			Date oldModifiedDate = getOfflineTaskHandler().getDateForAttributeType(
					RepositoryTaskAttribute.DATE_MODIFIED, repositoryTask.getLastModifiedDateStamp());
			if (oldModifiedDate != null && newModifiedDate != null) {
				if (newModifiedDate.compareTo(oldModifiedDate) <= 0) {
					// leave in SYNCHRONIZED state
					return false;
				}
			}
		}
		return true;

		// THE FOLLOWING CODE CAN BE USED AFTER MIGRATION TO 0.6.0 IS COMPLETE
		// RepositoryTaskAttribute modifiedDateAttribute =
		// newData.getAttribute(RepositoryTaskAttribute.DATE_MODIFIED);
		// if (repositoryTask.getLastModifiedDateStamp() != null &&
		// modifiedDateAttribute != null && modifiedDateAttribute.getValue() !=
		// null) {
		// if(repositoryTask.getLastModifiedDateStamp().trim().compareTo(modifiedDateAttribute.getValue().trim())
		// == 0) {
		// return false;
		// }
		// }
		// return true;
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
	private Job synchronize(Set<AbstractRepositoryTask> repositoryTasks, boolean forceSynch,
			final IJobChangeListener listener) {

		final SynchronizeTaskJob synchronizeJob = new SynchronizeTaskJob(this, repositoryTasks);
		synchronizeJob.setForceSynch(forceSynch);
		synchronizeJob.setPriority(Job.DECORATE);
		synchronizeJob.setRule(rule);
		if (listener != null) {
			synchronizeJob.addJobChangeListener(listener);
		}

		if (!forceSyncExecForTesting) {
			synchronizeJob.schedule();
		} else {
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				public void run() {
					synchronizeJob.run(new NullProgressMonitor());
					if (listener != null) {
						listener.done(null);
					}
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
	 * TaskRepository.syncTime to now if sync was successful for all tasks.
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
				// MylarStatusHandler.log("Changed: "+repository.getUrl()+" **
				// "+task.getDescription(), this);
			}
		}

		if (tasksToSync.size() == 0) {
			return;
		}

		synchronize(tasksToSync, false, new JobChangeAdapter() {

			@Override
			public void done(IJobChangeEvent event) {
				Date mostRecent = new Date(0);
				String mostRecentTimeStamp = repository.getSyncTimeStamp();
				for (AbstractRepositoryTask task : tasksToSync) {
					Date taskModifiedDate;

					if (getOfflineTaskHandler() != null && task.getTaskData() != null
							&& task.getTaskData().getLastModified() != null) {
						taskModifiedDate = getOfflineTaskHandler().getDateForAttributeType(
								RepositoryTaskAttribute.DATE_MODIFIED, task.getTaskData().getLastModified());
					} else {
						continue;
					}

					if (taskModifiedDate != null && taskModifiedDate.after(mostRecent)) {
						mostRecent = taskModifiedDate;
						mostRecentTimeStamp = task.getTaskData().getLastModified();
					}
				}
				// TODO: Get actual time stamp of query from repository rather
				// than above hack
				MylarTaskListPlugin.getRepositoryManager().setSyncTime(repository, mostRecentTimeStamp);
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

	/** non-final for testing purposes */
	protected void removeOfflineTaskData(RepositoryTaskData bug) {
		if (bug == null)
			return;

		ArrayList<RepositoryTaskData> bugList = new ArrayList<RepositoryTaskData>();
		bugList.add(bug);
		MylarTaskListPlugin.getDefault().getOfflineReportsFile().remove(bugList);
	}

	// /** non-final for testing purposes */
	// protected RepositoryTaskData loadOfflineTaskData(AbstractRepositoryTask
	// repositoryTask) {
	//
	// String url =
	// AbstractRepositoryTask.getRepositoryUrl(repositoryTask.getHandleIdentifier());
	// String id =
	// AbstractRepositoryTask.getTaskId(repositoryTask.getHandleIdentifier());
	// try {
	// return OfflineTaskManager.findBug(url, Integer.parseInt(id));
	// } catch (Exception e) {
	// return null;
	// }
	//
	// }

	/** non-final for testing purposes */
	public void saveOffline(RepositoryTaskData taskData) {
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
	
	private class MutexRule implements ISchedulingRule {
		public boolean isConflicting(ISchedulingRule rule) {
			return rule == this;
		}

		public boolean contains(ISchedulingRule rule) {
			return rule == this;
		}
	}
}
