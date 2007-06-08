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

package org.eclipse.mylyn.internal.team.ui.actions;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.actions.OpenRepositoryTask;
import org.eclipse.mylyn.internal.team.LinkedTaskInfo;
import org.eclipse.mylyn.internal.team.template.CommitTemplateManager;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylyn.tasks.core.ILinkedTaskInfo;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.team.MylarTeamPlugin;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ObjectPluginAction;

/**
 * Action used to open linked task
 * 
 * @author Mik Kersten
 * @author Eugene Kuleshov
 */
public class OpenCorrespondingTaskAction extends Action implements IViewActionDelegate {

	private static final String LABEL = "Open Corresponding Task";

	private static final String PREFIX_HTTP = "http://";

	private static final String PREFIX_HTTPS = "https://";

	private ISelection selection;

	public OpenCorrespondingTaskAction() {
		setText(LABEL);
		setToolTipText(LABEL);
		setImageDescriptor(TasksUiImages.TASK_REPOSITORY);
	}

	public void init(IViewPart view) {
		// ignore
	}

	@Override
	public void run() {
		if (selection instanceof StructuredSelection) {
			run((StructuredSelection) selection);
		}
	}

	public void run(IAction action) {
		if (action instanceof ObjectPluginAction) {
			ObjectPluginAction objectAction = (ObjectPluginAction) action;
			if (objectAction.getSelection() instanceof StructuredSelection) {
				StructuredSelection selection = (StructuredSelection) objectAction.getSelection();
				run(selection);
			}
		}
	}

	private void run(StructuredSelection selection) {
		final Object element = selection.getFirstElement();

		Job job = new OpenCorrespondingTaskJob("Opening Corresponding Task", element);
		job.schedule();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	/**
	 * Reconcile <code>ILinkedTaskInfo</code> data.
	 * 
	 * This is used in order to keep LinkedTaskInfo lightweight with minimal
	 * dependencies.
	 */
	private static ILinkedTaskInfo reconsile(ILinkedTaskInfo info) {
		ITask task = info.getTask();
		if (task != null) {
			return info;
		}

		String repositoryUrl = info.getRepositoryUrl();
		String taskId = info.getTaskId();
		String taskFullUrl = info.getTaskFullUrl();
		String comment = info.getComment();

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
			connector = repositoryManager.getRepositoryConnector(repository.getKind());
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
		if (taskId == null && comment != null) {
			Collection<AbstractRepositoryConnector> connectors = connector != null ? Collections
					.singletonList(connector) : TasksUiPlugin.getRepositoryManager().getRepositoryConnectors();
			REPOSITORIES: 
			for (AbstractRepositoryConnector c : connectors) {
				Collection<TaskRepository> repositories = repository != null ? Collections.singletonList(repository)
						: TasksUiPlugin.getRepositoryManager().getRepositories(c.getRepositoryType());
				for (TaskRepository r : repositories) {
					String[] ids = c.getTaskIdsFromComment(r, comment);
					if (ids != null && ids.length > 0) {
						taskId = ids[0];
						connector = c;
						repository = r;
						repositoryUrl = r.getUrl();
						break REPOSITORIES;
					}
				}
			}
		}
		if (taskId == null && comment != null) {
			CommitTemplateManager commitTemplateManager = MylarTeamPlugin.getDefault().getCommitTemplateManager();
			taskId = commitTemplateManager.getTaskIdFromCommentOrLabel(comment);
			if (taskId == null) {
				taskId = getTaskIdFromLegacy07Label(comment);
			}
		}

		if (taskFullUrl == null && repositoryUrl != null && taskId != null && connector != null) {
			taskFullUrl = connector.getTaskWebUrl(repositoryUrl, taskId);
		}

		if (task == null) {
			if (taskId != null && repositoryUrl != null) {
				// XXX fix this hack (jira ids don't work here)
				if (!taskId.contains(RepositoryTaskHandleUtil.HANDLE_DELIM)) {
//					String handle = AbstractRepositoryTask.getHandle(repositoryUrl, taskId);
					task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(repositoryUrl, taskId);
				}
			}
			if (task == null && taskFullUrl != null) {
				// search by fullUrl
				for (ITask currTask : TasksUiPlugin.getTaskListManager().getTaskList().getAllTasks()) {
					if (currTask instanceof AbstractRepositoryTask) {
						String currUrl = ((AbstractRepositoryTask) currTask).getTaskUrl();
						if (taskFullUrl.equals(currUrl)) {
							return new LinkedTaskInfo(currTask, null);
						}
					}
				}
			}
		}
		if (task != null) {
			return new LinkedTaskInfo(task, null);
		}

		return new LinkedTaskInfo(repositoryUrl, taskId, taskFullUrl, comment);
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
			int idEnd = comment.indexOf(' ', idStart);
			if (idEnd == -1) {
				return comment.substring(idStart);
			} else if (idEnd != -1 && idStart < idEnd) {
				return comment.substring(idStart, idEnd);
			}
		}
		return null;
	}

	public static String getTaskIdFromLegacy07Label(String comment) {
		String PREFIX_DELIM = ":";
		String PREFIX_START_1 = "Progress on:";
		String PREFIX_START_2 = "Completed:";
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

	private static final class OpenCorrespondingTaskJob extends Job {
		private final Object element;

		private OpenCorrespondingTaskJob(String name, Object element) {
			super(name);
			this.element = element;
		}

		protected IStatus run(IProgressMonitor monitor) {
			ILinkedTaskInfo info = null;
			if (element instanceof ILinkedTaskInfo) {
				info = (ILinkedTaskInfo) element;
			} else if (element instanceof IAdaptable) {
				info = (ILinkedTaskInfo) ((IAdaptable) element).getAdapter(ILinkedTaskInfo.class);
			}
			if (info == null) {
				info = (ILinkedTaskInfo) Platform.getAdapterManager().getAdapter(element, ILinkedTaskInfo.class);
			}

			if (info != null) {
				info = reconsile(info);
				final ITask task = info.getTask();
				if (task != null) {
					TasksUiUtil.refreshAndOpenTaskListElement(task);
					return Status.OK_STATUS;
				}
				if (info.getRepositoryUrl() != null && info.getTaskId() != null) {
					TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(info.getRepositoryUrl());
					String taskId = info.getTaskId();
					if (repository != null && taskId != null) {
						AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getRepositoryUi(repository.getKind());
						if (connectorUi != null) {
							connectorUi.openRepositoryTask(repository.getUrl(), taskId);
							return Status.OK_STATUS;
						}
					}
				}
				final String taskFullUrl = info.getTaskFullUrl();
				if (taskFullUrl != null) {
					TasksUiUtil.openUrl(taskFullUrl, false);
					return Status.OK_STATUS;
				}
			}

			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					boolean openDialog = MessageDialog.openQuestion(window.getShell(), ITasksUiConstants.TITLE_DIALOG,
							"Unable to match task. Open Repository Task dialog?");
					if (openDialog) {
						new OpenRepositoryTask().run(null);
					}
				}
			});
			
			return Status.OK_STATUS;
		}
	}
	
}
