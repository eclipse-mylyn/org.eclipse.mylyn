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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.internal.tasks.ui.TaskListImages;
import org.eclipse.mylar.internal.tasks.ui.TaskUiUtil;
import org.eclipse.mylar.internal.team.ContextChangeSet;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.team.MylarTeamPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.variants.IResourceVariant;
import org.eclipse.team.internal.ccvs.core.client.listeners.LogEntry;
import org.eclipse.team.internal.ccvs.core.mapping.CVSCheckedInChangeSet;
import org.eclipse.team.internal.ccvs.core.resources.RemoteResource;
import org.eclipse.team.internal.ui.synchronize.ChangeSetDiffNode;
import org.eclipse.team.ui.synchronize.ISynchronizeModelElement;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.internal.ObjectPluginAction;

/**
 * @author Mik Kersten
 */
public class OpenCorrespondingTaskAction extends Action implements IViewActionDelegate {

	private static final String LABEL = "Open Corresponding Task";

	private ISelection selection;

	public OpenCorrespondingTaskAction() {
		setText(LABEL);
		setToolTipText(LABEL);
		setImageDescriptor(TaskListImages.TASK_REPOSITORY);
	}

	public void init(IViewPart view) {
		// ignore
	}

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

	// TODO: clean up
	private void run(StructuredSelection selection) {
		Object element = selection.getFirstElement();
		IProject project = null;
		String comment = null;
		boolean resolved = false;

		if (element instanceof IAdaptable) {
			// TODO: there must be a better way to get at the local resource
			IResourceVariant resourceVariant = (IResourceVariant) ((IAdaptable) element)
					.getAdapter(IResourceVariant.class);
			if (resourceVariant != null && resourceVariant instanceof RemoteResource) {
				RemoteResource remoteResource = (RemoteResource) resourceVariant;
				String path = remoteResource.getRepositoryRelativePath();
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				project = root.getProject(new Path(path).removeFirstSegments(1).uptoSegment(1).toString());
			}
		}

		if (element instanceof ISynchronizeModelElement) {
			// find change set if available
			element = findParent((ISynchronizeModelElement) element);
		}

		if (element instanceof ContextChangeSet) {
			ITask task = ((ContextChangeSet) element).getTask();
			if (task != null) {
				TaskUiUtil.openEditor(task, false);
				resolved = true;
			}
		} else {
			if (element instanceof CVSCheckedInChangeSet) {
				comment = ((CVSCheckedInChangeSet) element).getComment();
			} else if (element instanceof ChangeSetDiffNode) {
				ChangeSetDiffNode diffNode = (ChangeSetDiffNode) element;
				if (diffNode.getSet() instanceof ContextChangeSet) {
					ITask task = ((ContextChangeSet) diffNode.getSet()).getTask();
					TaskUiUtil.openEditor(task, false);
					return;
				} else {
					comment = ((ChangeSetDiffNode) element).getName();
				}
			} else if (element instanceof LogEntry) {
				comment = ((LogEntry) element).getComment();
			} else if (element instanceof IFileRevision) {
				comment = ((IFileRevision) element).getComment();				
			}

			if (comment != null) {

				String id = MylarTeamPlugin.getDefault().getCommitTemplateManager().getTaskIdFromCommentOrLabel(comment);

				if (project != null) {
					TaskRepository repository = TasksUiPlugin.getDefault().getRepositoryForResource(project, false);
					if (repository != null) {
						AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getRepositoryUi(repository.getKind());
						if (connectorUi != null && id != null) {
							resolved = TaskUiUtil.openRepositoryTask(repository, id);
						}
					}
				}

				// Legacy:
				if (!resolved) {
					String fullUrl = ContextChangeSet.getUrlFromComment(comment);

					String repositoryUrl = null;
					if (fullUrl != null) {
						AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager()
								.getRepositoryForTaskUrl(fullUrl);
						if (connector != null) {
							repositoryUrl = connector.getRepositoryUrlFromTaskUrl(fullUrl);
						}
					} else {
						ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getActiveTask();
						if (task instanceof AbstractRepositoryTask) {
							repositoryUrl = ((AbstractRepositoryTask) task).getRepositoryUrl();
						} else if (TasksUiPlugin.getRepositoryManager().getAllRepositories().size() == 1) {
							repositoryUrl = TasksUiPlugin.getRepositoryManager().getAllRepositories().get(0).getUrl();
						}
					}

					resolved = TaskUiUtil.openRepositoryTask(repositoryUrl, id, fullUrl);

					if (!resolved) {
						TaskUiUtil.openUrl(fullUrl);
						resolved = true;
					}
				}
			}
		}
		if (!resolved) {
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Mylar Information",
					"Could not resolve report corresponding to change set comment.");
		}
	}

	private Object findParent(ISynchronizeModelElement element) {
		if (element instanceof ChangeSetDiffNode) {
			return element;
		} else if (element.getParent() instanceof ISynchronizeModelElement) {
			return findParent((ISynchronizeModelElement) element.getParent());
		}
		return null;
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
