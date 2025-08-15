/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Eugene Kuleshov - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.internal.ObjectPluginAction;

/**
 * Action used to open linked task.
 *
 * @author Mik Kersten
 * @author Eugene Kuleshov
 */
public class OpenCorrespondingTaskAction extends Action implements IViewActionDelegate {

	private static final String LABEL = Messages.OpenCorrespondingTaskAction_Open_Corresponding_Task;

	private ISelection selection;

	public OpenCorrespondingTaskAction() {
		setText(LABEL);
		setToolTipText(LABEL);
		setImageDescriptor(TasksUiImages.TASK_REPOSITORY);
	}

	@Override
	public void init(IViewPart view) {
		// ignore
	}

	@Override
	public void run() {
		if (selection instanceof StructuredSelection) {
			run((StructuredSelection) selection);
		}
	}

	@Override
	public void run(IAction action) {
		if (action instanceof ObjectPluginAction objectAction) {
			if (objectAction.getSelection() instanceof StructuredSelection) {
				StructuredSelection selection = (StructuredSelection) objectAction.getSelection();
				run(selection);
			}
		}
	}

	private void run(StructuredSelection selection) {
		final Object element = selection.getFirstElement();

		TaskFinder finder = new TaskFinder(element);
		finder.open();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
