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

package org.eclipse.mylar.internal.team.ui.actions;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.internal.tasks.ui.TaskListImages;
import org.eclipse.mylar.internal.tasks.ui.TasksUiUtil;
import org.eclipse.mylar.internal.team.ILinkedTaskInfo;
import org.eclipse.mylar.internal.team.LinkedTaskInfo;
import org.eclipse.mylar.internal.team.template.CommitTemplateManager;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.core.TaskRepositoryManager;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.team.MylarTeamPlugin;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
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
		setImageDescriptor(TaskListImages.TASK_REPOSITORY);
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
		Object element = selection.getFirstElement();

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
			if (info.getTask() != null) {
				// XXX which one to use?
				// TaskUiUtil.openEditor(info.getTask(), false);
				TasksUiUtil.refreshAndOpenTaskListElement(info.getTask());
				return;
			}
			if (info.getRepositoryUrl() != null && info.getTaskId() != null) {
				TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(info.getRepositoryUrl());
				if (repository != null) {
					if (TasksUiUtil.openRepositoryTask(repository, info.getTaskId())) {
						return;
					}
				}
			}
			if (info.getTaskFullUrl() != null) {
				TasksUiUtil.openUrl(info.getTaskFullUrl());
				return;
			}
		}

		// TODO show Open Remote Task dialog?
		MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				"Unable to open correspond task", "Unable to open correspond task");
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
	private ILinkedTaskInfo reconsile(ILinkedTaskInfo info) {
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
		if(repositoryUrl!=null) {
			repository = repositoryManager.getRepository(repositoryUrl);
		}

		AbstractRepositoryConnector connector = null;
		if(taskFullUrl!=null) {
			connector = repositoryManager.getConnectorForRepositoryTaskUrl(taskFullUrl);
		}		
		if (connector == null && repository!=null) {
			connector = repositoryManager.getRepositoryConnector(repository.getKind());
		}

		if (repositoryUrl == null && connector != null) {
			repositoryUrl = connector.getRepositoryUrlFromTaskUrl(taskFullUrl);
		}

		if (taskId == null && connector != null) {
			taskId = connector.getTaskIdFromTaskUrl(taskFullUrl);
		}
		if (taskId == null && repository != null && comment != null) {
			String[] ids = connector.getTaskIdsFromComment(repository, comment);
			if (ids != null && ids.length > 0) {
				taskId = ids[0];
			}
		}
		if (taskId == null && comment!=null) {
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
				if(!taskId.contains(AbstractRepositoryTask.HANDLE_DELIM)) {
					String handle = AbstractRepositoryTask.getHandle(repositoryUrl, taskId);
					task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(handle);
				}
			}
			if (task == null && taskFullUrl != null) {
				// search by fullUrl
				for (ITask currTask : TasksUiPlugin.getTaskListManager().getTaskList().getAllTasks()) {
					if (currTask instanceof AbstractRepositoryTask) {
						String currUrl = ((AbstractRepositoryTask) currTask).getUrl();
						if (taskFullUrl.equals(currUrl)) {
							return new LinkedTaskInfo(currTask);
						}
					}
				}
			}
		}
		if (task != null) {
			return new LinkedTaskInfo(task);
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

}
