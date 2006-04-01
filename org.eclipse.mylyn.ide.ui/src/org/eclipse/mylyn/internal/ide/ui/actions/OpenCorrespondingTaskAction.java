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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.internal.ide.team.MylarContextChangeSet;
import org.eclipse.mylar.internal.tasklist.ui.TaskListUiUtil;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.internal.ccvs.core.client.listeners.LogEntry;
import org.eclipse.team.internal.ui.synchronize.ChangeSetDiffNode;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.internal.ObjectPluginAction;

/**
 * @author Mik Kersten
 */
public class OpenCorrespondingTaskAction implements IViewActionDelegate {

	public void init(IViewPart view) {
		// ignore
	}

	public void run(IAction action) {

		if (action instanceof ObjectPluginAction) {
			ObjectPluginAction objectAction = (ObjectPluginAction) action;
			if (objectAction.getSelection() instanceof StructuredSelection) {
				StructuredSelection selection = (StructuredSelection) objectAction.getSelection();
				Object firstElement = selection.getFirstElement();
				String comment = null;
				boolean resolved = false;
				if (firstElement instanceof ChangeSetDiffNode) {
					comment = ((ChangeSetDiffNode) firstElement).getName();
				} else if (firstElement instanceof LogEntry) {
					comment = ((LogEntry) firstElement).getComment();
				} else if (firstElement instanceof IFileRevision) {
					comment = ((IFileRevision)firstElement).getComment();
				}
				if (comment != null) {
					String fullUrl = MylarContextChangeSet.getUrlFromComment(comment);
					AbstractRepositoryConnector connector = MylarTaskListPlugin.getRepositoryManager().getRepositoryForTaskUrl(fullUrl);
					if (connector != null) {
						String repositoryUrl = connector.getRepositoryUrlFromTaskUrl(fullUrl);
						String id = MylarContextChangeSet.getTaskIdFromComment(comment);					
						resolved = TaskListUiUtil.openRepositoryTask(repositoryUrl, id, fullUrl);
					} else {
						TaskListUiUtil.openUrl("Browser", "Browser", fullUrl);
						resolved = true;
					} 
				}

				if (!resolved) {
					MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Mylar Information",
							"Could not resolve report corresponding to change set comment.");
				}
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
	}

}
