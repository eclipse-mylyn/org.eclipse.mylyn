/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListToolTip;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Steffen Pingel
 */
public class TaskListToolTipHandler extends AbstractTaskListViewHandler {

	@Override
	protected void execute(ExecutionEvent event, TaskListView taskListView) throws ExecutionException {
		TaskListToolTip toolTip = taskListView.getToolTip();
		if (toolTip.isVisible()) {
			toolTip.hide();
		} else {
			taskListView.getViewer().getControl().getBounds();
			Tree tree = taskListView.getViewer().getTree();
			TreeItem[] selection = tree.getSelection();
			if (selection.length > 0) {
				toolTip.show(new Point(selection[0].getBounds().x + 1, selection[0].getBounds().y + 1));
			}
		}
	}

}
