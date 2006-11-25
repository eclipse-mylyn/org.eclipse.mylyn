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

import org.eclipse.compare.structuremergeviewer.IDiffElement;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
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
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.variants.IResourceVariant;
import org.eclipse.team.internal.ccvs.core.client.listeners.LogEntry;
import org.eclipse.team.internal.ccvs.core.mapping.CVSCheckedInChangeSet;
import org.eclipse.team.internal.ccvs.core.resources.RemoteResource;
import org.eclipse.team.internal.core.subscribers.DiffChangeSet;
import org.eclipse.team.internal.ui.synchronize.ChangeSetDiffNode;
import org.eclipse.team.internal.ui.synchronize.SynchronizeModelElement;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.internal.ObjectPluginAction;

/**
 * @author Mik Kersten
 */
public class OpenCorrespondingTaskAction extends Action implements IViewActionDelegate {

	private static final String LABEL = "Open Corresponding Task";

	private ISelection selection;

	private static final String PREFIX_HTTP = "http://";

	private static final String PREFIX_HTTPS = "https://";

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
		boolean opened = false;

		if (element instanceof ChangeSetDiffNode) {
			ChangeSetDiffNode diffNode = (ChangeSetDiffNode) element;
			if (diffNode.getSet() instanceof ContextChangeSet) {
				ITask task = ((ContextChangeSet) diffNode.getSet()).getTask();
				TaskUiUtil.openEditor(task, false);
				opened = true;
			} 
		} else if (element instanceof ContextChangeSet) {
			ITask task = ((ContextChangeSet) element).getTask();
			if (task != null) {
				TaskUiUtil.openEditor(task, false);
				opened = true;
			}
		}

		if (!opened) {
			IProject project = findCorrespondingProject(element);
			String comment = getCommentFromSelection(element);
			
			if (comment != null) {
				String id = MylarTeamPlugin.getDefault().getCommitTemplateManager()
						.getTaskIdFromCommentOrLabel(comment);
				if (id == null) {
					id = getTaskIdFromLegacy07Label(comment);
				}
				
				if (project != null) {
					TaskRepository repository = TasksUiPlugin.getDefault().getRepositoryForResource(project, false);
					if (repository != null) {
						AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getRepositoryUi(repository.getKind());
						if (connectorUi != null && id != null) {
							opened = TaskUiUtil.openRepositoryTask(repository, id);
						}
					}
				}

				// try opening via URL if present
				if (!opened) {
					String fullUrl = getUrlFromComment(comment);

					String repositoryUrl = null;
					if (fullUrl != null) {
						AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager()
								.getConnectorForRepositoryTaskUrl(fullUrl);
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

					opened = TaskUiUtil.openRepositoryTask(repositoryUrl, id, fullUrl);
					if (!opened) {
						TaskUiUtil.openUrl(fullUrl);
					}
				}
			}
		}
	}

	private String getCommentFromSelection(Object element) {
		if (element instanceof DiffChangeSet) {
			return ((CVSCheckedInChangeSet) element).getComment();
		} else if (element instanceof ChangeSetDiffNode) {
			return ((ChangeSetDiffNode) element).getName();
		} else if (element instanceof LogEntry) {
			return ((LogEntry) element).getComment();
		} else if (element instanceof IFileRevision) {
			return ((IFileRevision) element).getComment();
		}
		return null;
	}

	private IProject findCorrespondingProject(Object element) {
		if (element instanceof DiffChangeSet) {
			IResource[] resources = ((DiffChangeSet) element).getResources();
			if (resources.length > 0) {
				// TODO: only checks first resource
				return resources[0].getProject();
			}
		} else if (element instanceof SynchronizeModelElement) {
			SynchronizeModelElement modelElement = (SynchronizeModelElement)element;
			IResource resource = modelElement.getResource();
			if (resource != null) {
				return resource.getProject();
			} else {
				IDiffElement[] elements = modelElement.getChildren();
				if (elements.length > 0) {
					// TODO: only checks first diff
					if (elements[0] instanceof SynchronizeModelElement) {
						return ((SynchronizeModelElement)elements[0]).getResource().getProject();
					}
				}
			}
		} else if (element instanceof IAdaptable) {
			// TODO: there must be a better way to get at the local resource
			IResourceVariant resourceVariant = (IResourceVariant) ((IAdaptable) element)
					.getAdapter(IResourceVariant.class);
			if (resourceVariant != null && resourceVariant instanceof RemoteResource) {
				RemoteResource remoteResource = (RemoteResource) resourceVariant;
				String path = remoteResource.getRepositoryRelativePath();
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				return root.getProject(new Path(path).removeFirstSegments(1).uptoSegment(1).toString());
			}
		} else {
			
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
	

//	private Object findParent(ISynchronizeModelElement element) {
//		if (element instanceof ChangeSetDiffNode) {
//			return element;
//		} else if (element.getParent() instanceof ISynchronizeModelElement) {
//			return findParent((ISynchronizeModelElement) element.getParent());
//		}
//		return null;
//	}
	

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
