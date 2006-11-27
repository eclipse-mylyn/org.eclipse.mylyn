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

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylar.internal.team.TeamRespositoriesManager;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.team.MylarTeamPlugin;
import org.eclipse.mylar.team.AbstractTeamRepositoryProvider;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten
 */
public class CommitContextAction implements IViewActionDelegate {

	public void init(IViewPart view) {
		// TODO Auto-generated method stub
	}

	public void run(IAction action) {
		ITask task = TaskListView.getFromActivePerspective().getSelectedTask();
		IResource[] resources = MylarTeamPlugin.getDefault().getChangeSetManager().getResources(task);
		if (resources == null || resources.length == 0) {
			MessageDialog.openInformation(null, "Mylar Information",
					"There are no interesting resources in the corresponding change set.\nRefer to Synchronize view.");
			return;
		}

		List<AbstractTeamRepositoryProvider> providers = TeamRespositoriesManager.getInstance().getProviders();
		for (Object element : providers) {
			try {
				AbstractTeamRepositoryProvider provider = (AbstractTeamRepositoryProvider) element;
				if (provider.hasOutgoingChanges(resources)) {
					provider.commit(resources);
				}
			} catch (Exception e) {
				MylarStatusHandler.fail(e, "Could not commit context.", false);
			}
		}

	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}
}
