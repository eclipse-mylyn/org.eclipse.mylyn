/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist.internal;

import java.util.List;

import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskActivityListener;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;

/**
 * @author Mik Kersten
 */
public class TaskListSavePolicy implements ITaskActivityListener, DisposeListener {

	public void taskActivated(ITask task) {

	}

	public void tasksActivated(List<ITask> tasks) {
		// TODO Auto-generated method stub

	}

	public void taskDeactivated(ITask task) {
		// TODO Auto-generated method stub

	}

	public void taskChanged(ITask task) {
		if (MylarTaskListPlugin.getDefault() != null) {
			MylarTaskListPlugin.getDefault().saveTaskListAndContexts();
		}
	}

	public void tasklistRead() {
		// TODO Auto-generated method stub

	}

	public void tasklistModified() {
		if (MylarTaskListPlugin.getDefault() != null) {
			MylarTaskListPlugin.getDefault().saveTaskListAndContexts();
		}
	}
	
	public void widgetDisposed(DisposeEvent e) {
		if (MylarTaskListPlugin.getDefault() != null) {
			MylarTaskListPlugin.getDefault().saveTaskListAndContexts();
		}
	}
}
