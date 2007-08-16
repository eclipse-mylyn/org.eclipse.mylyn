/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.planner;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;

/**
 * @author Mik Kersten
 * @author Ken Sueda
 * @author Rob Elves
 */
public class OpenTaskEditorAction extends Action {

	public static final String ID = "org.eclipse.mylyn.taskplannereditor.actions.open";

	private final TreeViewer viewer;

	/**
	 * @param view
	 */
	public OpenTaskEditorAction(TreeViewer view) {
		this.viewer = view;
		setText("Open");
		setToolTipText("Open Element");
		setId(ID);
	}

	@Override
	public void run() {
		ISelection selection = viewer.getSelection();
		Object object = ((IStructuredSelection) selection).getFirstElement();
		if (object instanceof AbstractTask) {
			TasksUiUtil.openEditor((AbstractTask) object, false);
		}
//		if (obj instanceof Task) {
//			((Task) obj).openTaskInEditor(false);
//		}
	}
}
