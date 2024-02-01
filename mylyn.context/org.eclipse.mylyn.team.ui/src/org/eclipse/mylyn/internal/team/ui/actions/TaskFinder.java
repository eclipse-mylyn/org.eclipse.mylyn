/*******************************************************************************
 * Copyright (c) 2004, 2016 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Eugene Kuleshov - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ui.actions;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.commons.workbench.EditorHandle;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.OpenRepositoryTaskJob;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.OpenRepositoryTaskAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.team.ui.FocusedTeamUiPlugin;
import org.eclipse.mylyn.internal.team.ui.LinkedTaskInfo;
import org.eclipse.mylyn.internal.team.ui.templates.CommitTemplateManager;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.team.ui.AbstractTaskReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Action used to open linked task. TODO: this class has evolved into a complete mess and has to be fixed.
 *
 * @author Mik Kersten
 * @author Eugene Kuleshov
 */
public class TaskFinder {

	private static final String PREFIX_HTTP = "http://"; //$NON-NLS-1$

	private static final String PREFIX_HTTPS = "https://"; //$NON-NLS-1$

	public static String getTaskIdFromLegacy07Label(String comment) {
		String PREFIX_DELIM = ":"; //$NON-NLS-1$
		String PREFIX_START_1 = Messages.OpenCorrespondingTaskAction_Progress_on;
		String PREFIX_START_2 = Messages.OpenCorrespondingTaskAction_Completed;
		String usedPrefix = PREFIX_START_1;
		int firstDelimIndex = comment.indexOf(PREFIX_START_1);
		if (firstDelimIndex == -1) {
			firstDelimIndex = comment.indexOf(PREFIX_START_2);
			usedPrefix = PREFIX_START_2;
		}
		if (firstDelimIndex != -1) {
			int idStart = firstDelimIndex + usedPrefix.length();
			int idEnd = comment.indexOf(PREFIX_DELIM, firstDelimIndex + usedPrefix.length());// comment.indexOf(PREFIX_DELIM);
			if (idEnd != -1 && idStart < idEnd) {
				String id = comment.substring(idStart, idEnd);
				if (id != null) {
					return id.trim();
				}
			} else {
				return comment.substring(0, firstDelimIndex);
			}
		}
		return null;
	}

	public static String getUrlFromComment(String comment) {
		int httpIndex = comment.indexOf(PREFIX_HTTP);
		int httpsIndex = comment.indexOf(PREFIX_HTTPS);
		int idStart = -1;
		if (httpIndex != -1) {
			idStart = httpIndex;
		} else if (httpsIndex != -1) {
			idStart = httpsIndex;
		}
		if (idStart != -1) {
			int idEnd;
			for (idEnd = idStart; idEnd < comment.length() && !Character.isWhitespace(comment.charAt(idEnd)); idEnd++) {

			}
			return comment.substring(idStart, idEnd);
		}
		return null;
	}

	/**
	 * Reconcile <code>ILinkedTaskInfo</code> data. This is used in order to keep LinkedTaskInfo lightweight with minimal dependencies.
	 */
	private static AbstractTaskReference reconcile(AbstractTaskReference info) {
		ITask task;
		long timestamp;

		if (info instanceof LinkedTaskInfo) {
			task = ((LinkedTaskInfo) info).getTask();
			timestamp = ((LinkedTaskInfo) info).getTimestamp();
		} else {
			task = null;
			timestamp = 0;
		}

		if (task != null) {
			return info;
		}

		String repositoryUrl = info.getRepositoryUrl();
		String taskId = info.getTaskId();
		String taskFullUrl = info.getTaskUrl();
		String comment = info.getText();

		TaskRepositoryManager repositoryManager = TasksUiPlugin.getRepositoryManager();

		TaskRepository repository = null;
		if (repositoryUrl != null) {
			repository = repositoryManager.getRepository(repositoryUrl);
		}

		if (taskFullUrl == null && comment != null) {
			taskFullUrl = getUrlFromComment(comment);
		}

		AbstractRepositoryConnector connector = null;
		if (taskFullUrl != null) {
			connector = repositoryManager.getConnectorForRepositoryTaskUrl(taskFullUrl);
		}
		if (connector == null && repository != null) {
			connector = repositoryManager.getRepositoryConnector(repository.getConnectorKind());
		}

		if (repositoryUrl == null && connector != null) {
			repositoryUrl = connector.getRepositoryUrlFromTaskUrl(taskFullUrl);
			if (repository == null) {
				repository = repositoryManager.getRepository(repositoryUrl);
			}
		}

		if (taskId == null && connector != null) {
			taskId = connector.getTaskIdFromTaskUrl(taskFullUrl);
		}

		// XXX: clean up and remove break to label
		if (taskId == null && comment != null) {
			Collection<AbstractRepositoryConnector> connectors = connector != null
					? Collections.singletonList(connector)
					: TasksUi.getRepositoryManager().getRepositoryConnectors();
			REPOSITORIES: for (AbstractRepositoryConnector c : connectors) {
				Collection<TaskRepository> repositories = repository != null
						? Collections.singletonList(repository)
						: TasksUi.getRepositoryManager().getRepositories(c.getConnectorKind());
				for (TaskRepository r : repositories) {
					String[] ids = c.getTaskIdsFromComment(r, comment);
					if (ids != null && ids.length > 0) {
						taskId = ids[0];
						connector = c;
						repository = r;
						repositoryUrl = r.getRepositoryUrl();
						break REPOSITORIES;
					}
				}
			}
		}
		if (taskId == null && comment != null) {
			CommitTemplateManager commitTemplateManager = FocusedTeamUiPlugin.getDefault().getCommitTemplateManager();
			taskId = commitTemplateManager.getTaskIdFromCommentOrLabel(comment);
			if (taskId == null) {
				taskId = getTaskIdFromLegacy07Label(comment);
			}
		}

		if (taskFullUrl == null && repositoryUrl != null && taskId != null && connector != null) {
			taskFullUrl = connector.getTaskUrl(repositoryUrl, taskId);
		}

		if (task == null) {
			if (taskId != null && repositoryUrl != null) {
				// XXX fix this hack (jira ids don't work here)
				if (!taskId.contains(RepositoryTaskHandleUtil.HANDLE_DELIM)) {
					task = TasksUiInternal.getTaskList().getTask(repositoryUrl, taskId);
				}
			}
			if (task == null && taskFullUrl != null) {
				task = TasksUiInternal.getTaskByUrl(taskFullUrl);
			}
		}

		return new LinkedTaskInfo(repositoryUrl, taskId, taskFullUrl, comment, timestamp, task);
	}

	private final AbstractTaskReference reference;

	private ITask task;

	private long timestamp;

	public TaskFinder(Object element) {
		reference = initialize(element);
	}

	public AbstractTaskReference getReference() {
		return reference;
	}

	public ITask getTask() {
		return task;
	}

	private AbstractTaskReference initialize(Object element) {
		AbstractTaskReference info = null;
		if (element instanceof AbstractTaskReference) {
			info = (AbstractTaskReference) element;
		} else if (element instanceof IAdaptable) {
			info = ((IAdaptable) element).getAdapter(AbstractTaskReference.class);
		}
		if (info == null) {
			info = Platform.getAdapterManager().getAdapter(element, AbstractTaskReference.class);
		}

		if (info != null) {
			info = reconcile(info);
			if (info instanceof LinkedTaskInfo) {
				task = ((LinkedTaskInfo) info).getTask();
				timestamp = ((LinkedTaskInfo) info).getTimestamp();
			}
		}
		return info;
	}

	public EditorHandle openTaskByKey(IWorkbenchPage page) {
		if (reference != null) {
			String taskUrl = reference.getTaskUrl();
			if (taskUrl != null && openTaskByKey(page, taskUrl)) {
				return new EditorHandle(Status.OK_STATUS);
			}
		}
		return null;
	}

	public IStatus open() {
		if (reference != null) {
			if (task != null) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(() -> TasksUiUtil.openTask(task));
				return Status.OK_STATUS;
			}

			if (reference.getRepositoryUrl() != null && reference.getTaskId() != null) {
				TaskRepository repository = TasksUiPlugin.getRepositoryManager()
						.getRepository(reference.getRepositoryUrl());
				String taskId = reference.getTaskId();
				if (repository != null && taskId != null) {
					AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin
							.getConnectorUi(repository.getConnectorKind());
					if (connectorUi != null) {
						TasksUiInternal.openRepositoryTask(connectorUi.getConnectorKind(),
								repository.getRepositoryUrl(), taskId, null, timestamp);
						return Status.OK_STATUS;
					}
				}
			}

			final String taskFullUrl = reference.getTaskUrl();
			if (taskFullUrl != null) {
				if (!openTaskByKey(getActivePage(), taskFullUrl)) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(() -> TasksUiUtil.openUrl(taskFullUrl));
				}
				return Status.OK_STATUS;
			}
		}

		// task not found
		PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			boolean openDialog = MessageDialog.openQuestion(window.getShell(),
					Messages.OpenCorrespondingTaskAction_Open_Task,
					Messages.OpenCorrespondingTaskAction_Unable_to_match_task);
			if (openDialog) {
				new OpenRepositoryTaskAction().run(null);
			}
		});
		return Status.OK_STATUS;
	}

	private boolean openTaskByKey(IWorkbenchPage page, final String taskFullUrl) {
		String taskKey = null;
		TaskRepository repository = guessRepository(taskFullUrl);
		if (repository != null) {
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager()
					.getRepositoryConnector(repository.getConnectorKind());
			if (connector != null) {
				taskKey = guessTaskKey(connector, repository, taskFullUrl);
			}
		}
		if (taskKey != null && page != null) {
			new OpenRepositoryTaskJob(repository, taskKey, taskFullUrl, page).schedule();
			return true;
		}
		return false;
	}

	private IWorkbenchPage getActivePage() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			return window.getActivePage();
		}
		return null;
	}

	private TaskRepository guessRepository(String taskFullUrl) {
		for (TaskRepository repository : TasksUi.getRepositoryManager().getAllRepositories()) {
			if (taskFullUrl.startsWith(repository.getRepositoryUrl())) {
				return repository;
			}
		}
		return null;
	}

	/**
	 * Using a task for which we know the task key and browser URL, try to guess the task key from the given URL.
	 */
	private static String guessTaskKey(AbstractRepositoryConnector connector, TaskRepository repository,
			String taskFullUrl) {
		Set<ITask> tasks = ((TaskList) TasksUiInternal.getTaskList()).getTasks(repository.getRepositoryUrl());
		if (!tasks.isEmpty()) {
			ITask task = tasks.iterator().next();
			URL browserUrl = connector.getBrowserUrl(repository, task);
			if (browserUrl != null) {
				return guessTaskKey(taskFullUrl, browserUrl.toExternalForm(), task.getTaskKey());
			}
		}
		return null;
	}

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public static String guessTaskKey(String taskFullUrl, String knownTaskUrl, String knownTaskKey) {
		int index = knownTaskUrl.indexOf(knownTaskKey);
		if (index != -1) {
			String prefix = knownTaskUrl.substring(0, index);
			String postfix = knownTaskUrl.substring(index + knownTaskKey.length());
			return extractTaskId(taskFullUrl, prefix, postfix);
		}
		return null;
	}

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public static String extractTaskId(String taskFullUrl, String prefix, String postfix) {
		if (!prefix.isEmpty() && taskFullUrl.startsWith(prefix) && taskFullUrl.endsWith(postfix)) {
			return taskFullUrl.substring(prefix.length(), taskFullUrl.length() - postfix.length());
		}
		return null;
	}
}
