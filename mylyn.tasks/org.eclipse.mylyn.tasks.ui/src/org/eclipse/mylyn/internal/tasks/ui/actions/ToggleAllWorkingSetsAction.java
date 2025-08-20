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

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.mylyn.internal.tasks.ui.workingsets.TaskWorkingSetUpdater;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkingSet;

/**
 * @author Mik Kersten
 */
public class ToggleAllWorkingSetsAction extends Action {

	public ToggleAllWorkingSetsAction() {
		super(Messages.ToggleAllWorkingSetsAction_Show_All, IAction.AS_CHECK_BOX);
		super.setChecked(TaskWorkingSetUpdater.areNoTaskWorkingSetsEnabled());
	}

	@Override
	public void run() {
		Set<IWorkingSet> newList = new HashSet<>(Arrays.asList(TaskWorkingSetUpdater.getEnabledSets()));

		Set<IWorkingSet> tempList = new HashSet<>();
		for (IWorkingSet workingSet : newList) {
			if (workingSet != null && workingSet.getId() != null
					&& workingSet.getId().equalsIgnoreCase(TaskWorkingSetUpdater.ID_TASK_WORKING_SET)) {
				tempList.add(workingSet);
			}
		}
		newList.removeAll(tempList);
		TaskWorkingSetUpdater.applyWorkingSetsToAllWindows(newList);
	}

	@Override
	public void runWithEvent(Event event) {
		run();
	}
}
