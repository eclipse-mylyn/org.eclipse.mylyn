/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.bindings.keys.IKeyLookup;
import org.eclipse.jface.bindings.keys.KeyLookupFactory;
import org.eclipse.mylyn.internal.tasks.ui.workingsets.TaskWorkingSetUpdater;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkingSet;

/**
 * @author Mik Kersten
 */
public class ToggleWorkingSetAction extends Action {

	private final IWorkingSet workingSet;

	public ToggleWorkingSetAction(IWorkingSet set) {
		super(set.getLabel(), IAction.AS_CHECK_BOX);
		setImageDescriptor(set.getImageDescriptor());
		this.workingSet = set;
		setChecked(TaskWorkingSetUpdater.isWorkingSetEnabled(set));
	}

	@Override
	public void run() {
		runWithEvent(null);
	}

	@Override
	public void runWithEvent(Event event) {
		Set<IWorkingSet> newList = new HashSet<IWorkingSet>(Arrays.asList(TaskWorkingSetUpdater.getEnabledSets()));

		boolean modified = false;
		if (event != null) {
			modified = (event.stateMask & KeyLookupFactory.getDefault().formalModifierLookup(IKeyLookup.M1_NAME)) != 0;
		}

		if (!modified) {
			// Default behavior is to act as a radio button.
			Set<IWorkingSet> tempList = new HashSet<IWorkingSet>();
			Iterator<IWorkingSet> iter = newList.iterator();
			while (iter.hasNext()) {
				IWorkingSet workingSet = iter.next();
				if (workingSet != null && workingSet.getId() != null
						&& workingSet.getId().equalsIgnoreCase(TaskWorkingSetUpdater.ID_TASK_WORKING_SET)) {
					tempList.add(workingSet);
				}
			}
			newList.removeAll(tempList);

			if (isChecked()) {
				newList.add(workingSet);
			} else {
				// If multiples were previously selected, make this action active
				if (!TaskWorkingSetUpdater.isOnlyTaskWorkingSetEnabled(workingSet)) {
					newList.add(workingSet);
				}
			}
		} else {
			// If modifier key is pressed, de/selections are additive.
			if (isChecked()) {
				newList.add(workingSet);
			} else {
				newList.remove(workingSet);
			}
		}

		TaskWorkingSetUpdater.applyWorkingSetsToAllWindows(newList);
	}

}