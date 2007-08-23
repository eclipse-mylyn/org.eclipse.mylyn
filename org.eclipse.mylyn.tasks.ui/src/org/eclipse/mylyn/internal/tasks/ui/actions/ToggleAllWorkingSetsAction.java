/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.mylyn.internal.tasks.ui.workingsets.TaskWorkingSetUpdater;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;

/**
 * @author Mik Kersten
 */
public class ToggleAllWorkingSetsAction extends Action {

	private IWorkbenchWindow window;

	public ToggleAllWorkingSetsAction(IWorkbenchWindow window) {
		super("Show All", IAction.AS_CHECK_BOX);
		super.setChecked(TaskWorkingSetUpdater.areNoTaskWorkingSetsEnabled());
		this.window = window;
	}

	@Override
	public void run() {
		Set<IWorkingSet> newList = new HashSet<IWorkingSet>(Arrays.asList(TaskWorkingSetUpdater.getEnabledSets()));

		Set<IWorkingSet> tempList = new HashSet<IWorkingSet>();
		Iterator<IWorkingSet> iter = newList.iterator();
		while (iter.hasNext()) {
			IWorkingSet workingSet = iter.next();
			if (workingSet != null && workingSet.getId() != null && workingSet.getId().equalsIgnoreCase(TaskWorkingSetUpdater.ID_TASK_WORKING_SET)) {
				tempList.add(workingSet);
			}
		}
		newList.removeAll(tempList);
		window.getActivePage().setWorkingSets(newList.toArray(new IWorkingSet[newList.size()]));
	}

	@Override
	public void runWithEvent(Event event) {
		run();
	}
}