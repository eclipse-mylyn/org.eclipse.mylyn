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

package org.eclipse.mylar.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.internal.tasks.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiUtil;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten
 */
public class EditRepositoryPropertiesAction extends Action {

	private static final String ID = "org.eclipse.mylar.tasklist.repositories.properties";

	private TaskRepositoriesView repositoriesView;

	public EditRepositoryPropertiesAction(TaskRepositoriesView repositoriesView) {
		this.repositoriesView = repositoriesView;
		setText("Properties");
		setId(ID);
	}

	public void init(IViewPart view) {
		// ignore
	}

	@Override
	public void run() {
		IStructuredSelection selection = (IStructuredSelection) repositoriesView.getViewer().getSelection();
		if (selection.getFirstElement() instanceof TaskRepository) {
			TasksUiUtil.openEditRepositoryWizard((TaskRepository) selection.getFirstElement());
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
	}
}
