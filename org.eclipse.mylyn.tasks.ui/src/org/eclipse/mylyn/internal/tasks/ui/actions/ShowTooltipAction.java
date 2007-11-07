/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListToolTip;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Steffen Pingel
 */
public class ShowTooltipAction extends Action implements IViewActionDelegate {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.showToolTip";

	private TaskListView taskListView;

	public void init(IViewPart view) {
		this.taskListView = (TaskListView) view;
	}

	@Override
	public void run() {
		TaskListToolTip toolTip = taskListView.getToolTip();
		if (toolTip.isVisible()) {
			toolTip.hide();
		} else {
			taskListView.getViewer().getControl().getBounds();
			Tree tree = taskListView.getViewer().getTree();
			TreeItem[] selection = tree.getSelection();
			if (selection.length > 0) {
				toolTip.show(new Point(selection[0].getBounds().x, selection[0].getBounds().y));
			}
		}
	}
	
	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}
	
}
