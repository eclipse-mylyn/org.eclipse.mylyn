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

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;

/**
 * @author Mik Kersten
 */
public class OpenTaskListElementAction extends Action {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.open";

	private final StructuredViewer viewer;

	public OpenTaskListElementAction(StructuredViewer view) {
		this.viewer = view;
		setText("Open");
		setToolTipText("Open Task List Element");
		setId(ID);
	}

	@Override
	public void run() {
		ISelection selection = viewer.getSelection();
		List<?> list = ((IStructuredSelection) selection).toList();
		for (Iterator<?> iterator = list.iterator(); iterator.hasNext();) {
			Object element = iterator.next();
//			if (element instanceof ITaskListElement && !(element instanceof AbstractTaskContainer)) {
			if (element instanceof AbstractTask) {
				TasksUiUtil.refreshAndOpenTaskListElement((AbstractTaskContainer) element);
			}
		}
	}
}
