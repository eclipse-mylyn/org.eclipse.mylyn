/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
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

	@Override
	public void init(IViewPart view) {
		// ignore
	}

	@Override
	public void run(IAction action) {
//		ITask task = TaskListView.getFromActivePerspective().getSelectedTask();
//		// TODO: consider corresponding tasks to change set managers to avoid iteration
//		for (AbstractContextChangeSetManager changeSetManager : MylynTeamPlugin.getDefault().getContextChangeSetManagers()) {
//			IResource[] resources = MylynTeamPlugin.getDefault().getChangeSetManager().getResources(task);
//			if (resources == null || resources.length == 0) {
//				MessageDialog.openInformation(null, "Mylyn Information",
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
//				StatusHandler.fail(e, "Could not commit context.", false);
//			}
//		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}
}
