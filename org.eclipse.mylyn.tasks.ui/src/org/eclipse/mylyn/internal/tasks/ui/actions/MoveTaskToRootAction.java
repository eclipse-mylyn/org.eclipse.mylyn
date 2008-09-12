/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Ken Sueda - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;

/**
 * @author Mik Kersten 
 * @author Ken Sueda
 */
public class MoveTaskToRootAction extends Action {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.move.toroot";

	// private final TaskListView view;
	public MoveTaskToRootAction(TaskListView view) {
		// this.view = view;
		setText("Move Task to Root");
		setToolTipText("Move Task to Root");
		setId(ID);
	}

	@Override
	public void run() {
		throw new RuntimeException("unimplemented");
	}
}
