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

package org.eclipse.mylar.internal.ide.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.internal.ide.team.MylarActiveChangeSet;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylar.internal.tasklist.ui.TaskUiUtil;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.swt.widgets.Display;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.internal.ccvs.core.client.listeners.LogEntry;
import org.eclipse.team.internal.ccvs.core.mapping.CVSCheckedInChangeSet;
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

	private void run(StructuredSelection selection) {
		Object element = selection.getFirstElement();
		String comment = null;
		boolean resolved = false;
		if (element instanceof ISynchronizeModelElement) {
			// find change set if available
			element = findParent((ISynchronizeModelElement) element);
		}
		
		if (element instanceof MylarActiveChangeSet) {
			ITask task = ((MylarActiveChangeSet)element).getTask();
			if (task != null) {
				TaskUiUtil.openEditor(task, false);
				resolved = true;
			}
		} else {
			if (element instanceof CVSCheckedInChangeSet) {
				comment = ((CVSCheckedInChangeSet)element).getComment();
			} else if (element instanceof ChangeSetDiffNode) {
				ChangeSetDiffNode diffNode = (ChangeSetDiffNode) element;
				if (diffNode.getSet() instanceof MylarActiveChangeSet) {
					ITask task = ((MylarActiveChangeSet) diffNode.getSet()).getTask();
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
				String fullUrl = MylarActiveChangeSet.getUrlFromComment(comment);
				String repositoryUrl = null;
				if (fullUrl != null) {
					AbstractRepositoryConnector connector = MylarTaskListPlugin.getRepositoryManager()
							.getRepositoryForTaskUrl(fullUrl);
					if (connector != null) {
						repositoryUrl = connector.getRepositoryUrlFromTaskUrl(fullUrl);
					}
				} else {
					ITask task = MylarTaskListPlugin.getTaskListManager().getTaskList().getActiveTask();
					if (task instanceof AbstractRepositoryTask) {
						repositoryUrl = ((AbstractRepositoryTask) task).getRepositoryUrl();
					} else if (MylarTaskListPlugin.getRepositoryManager().getAllRepositories().size() == 1) {
						repositoryUrl = MylarTaskListPlugin.getRepositoryManager().getAllRepositories().get(0).getUrl();
					}
				}
				String id = MylarActiveChangeSet.getTaskIdFromCommentOrLabel(comment);
				resolved = TaskUiUtil.openRepositoryTask(repositoryUrl, id, fullUrl);
	
				if (!resolved) {
					TaskUiUtil.openUrl(fullUrl);
					resolved = true;
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
