/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten
 */
public class CommitContextAction implements IViewActionDelegate {

	public void init(IViewPart view) {
		// ignore
	}

	public void run(IAction action) {
//		ITask task = TaskListView.getFromActivePerspective().getSelectedTask();
//		// TODO: consider corresponding tasks to change set managers to avoid iteration
//		for (AbstractContextChangeSetManager changeSetManager : MylarTeamPlugin.getDefault().getContextChangeSetManagers()) {
//			IResource[] resources = MylarTeamPlugin.getDefault().getChangeSetManager().getResources(task);
//			if (resources == null || resources.length == 0) {
//				MessageDialog.openInformation(null, "Mylar Information",
//						"There are no interesting resources in the corresponding change set.\nRefer to Synchronize view.");
//				return;
//			}
//
//			List<AbstractCommitWorkflowProvider> providers = TeamRespositoriesManager.getInstance().getProviders();
//			for (AbstractCommitWorkflowProvider element : providers) {
//				AbstractCommitWorkflowProvider provider = (AbstractTeamRepositoryProvider) element;
//				if (provider.hasOutgoingChanges(resources)) {
//					provider.commit(resources);
//				}
//			} catch (Exception e) {
//				MylarStatusHandler.fail(e, "Could not commit context.", false);
//			}	
//		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}
}
