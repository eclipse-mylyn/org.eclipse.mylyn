/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten
 */
public class GoIntoAction extends Action implements IViewActionDelegate {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.view.go.into"; //$NON-NLS-1$

	public GoIntoAction() {
		setId(ID);
		setText(Messages.GoIntoAction_Go_Into);
		setToolTipText(Messages.GoIntoAction_Go_Into);
		setImageDescriptor(CommonImages.GO_INTO);
	}

	@Override
	public void init(IViewPart view) {
		// TODO Auto-generated method stub
	}

	@Override
	public void run() {
		if (TaskListView.getFromActivePerspective() != null) {
			TaskListView.getFromActivePerspective().getFilteredTree().setFilterText(""); //$NON-NLS-1$
			TaskListView.getFromActivePerspective().goIntoCategory();
		}
	}

	@Override
	public void run(IAction action) {
		run();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
	}
}
