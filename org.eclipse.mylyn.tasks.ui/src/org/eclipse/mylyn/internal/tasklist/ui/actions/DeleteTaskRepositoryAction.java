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

package org.eclipse.mylar.internal.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.TaskRepository;
import org.eclipse.mylar.internal.tasklist.ui.TaskRepositoriesView;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchImages;

/**
 * @author Mik Kersten
 */
public class DeleteTaskRepositoryAction extends Action {

	private static final String ID = "org.eclipse.mylar.tasklist.repositories.delete";

	private TaskRepositoriesView repositoriesView;

	public DeleteTaskRepositoryAction(TaskRepositoriesView repositoriesView) {
		this.repositoriesView = repositoriesView;
		setImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		setText("Delete Repository");
		setId(ID);
	}

	public void init(IViewPart view) {
		// ignore
	}

	public void run() {
		try {
			boolean deleteConfirmed = MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getShell(), "Confirm Delete", "Delete the selected task repositories?");
			if (deleteConfirmed) {
				IStructuredSelection selection = (IStructuredSelection) repositoriesView.getViewer().getSelection();
				for (Object selectedObject : selection.toList()) {
					if (selectedObject instanceof TaskRepository) {
						MylarTaskListPlugin.getRepositoryManager().removeRepository((TaskRepository) selectedObject);
					}
				}
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, e.getMessage(), true);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
	}
}
