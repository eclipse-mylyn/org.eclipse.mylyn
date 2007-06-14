/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.workingset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetUpdater;

/**
 * @author Eugene Kuleshov
 */
public class TaskWorkingSetUpdater implements IWorkingSetUpdater, ITaskListChangeListener {

	private List<IWorkingSet> workingSets = new ArrayList<IWorkingSet>();
	
	public TaskWorkingSetUpdater() {
		TasksUiPlugin.getTaskListManager().getTaskList().addChangeListener(this);
	}
	
	// IWorkingSetUpdater
	
	public void add(IWorkingSet workingSet) {
		checkElementExistence(workingSet);
		synchronized (workingSets) {
			workingSets.add(workingSet);
		}
	}

	private void checkElementExistence(IWorkingSet workingSet) {
		ArrayList<IAdaptable> list = new ArrayList<IAdaptable>();
		for (IAdaptable adaptable : workingSet.getElements()) {
			if (adaptable instanceof AbstractTaskContainer) {
				String handle = ((AbstractTaskContainer) adaptable).getHandleIdentifier();
				for (AbstractTaskContainer element : TasksUiPlugin.getTaskListManager().getTaskList().getRootElements()) {
					if (element instanceof AbstractTaskContainer && element.getHandleIdentifier().equals(handle)) {
						list.add(adaptable);
					}
				}
			}
		}
		workingSet.setElements(list.toArray(new IAdaptable[list.size()]));
	}

	public boolean contains(IWorkingSet workingSet) {
		synchronized(workingSets) {
			return workingSets.contains(workingSet);
		}
	}

	public boolean remove(IWorkingSet workingSet) {
		synchronized(workingSets) {
			return workingSets.remove(workingSet);
		}
	}

	public void dispose() {
		TasksUiPlugin.getTaskListManager().getTaskList().removeChangeListener(this);
	}

	public void containersChanged(Set<TaskContainerDelta> containers) {
		for (TaskContainerDelta taskContainerDelta : containers) {
				switch (taskContainerDelta.getKind()) {
				case REMOVED:
					synchronized (workingSets) {
						for (IWorkingSet workingSet : workingSets) {
							// TODO could filter by working set id
							Set<IAdaptable> remove = new HashSet<IAdaptable>(); 
							for (IAdaptable adaptable : workingSet.getElements()) {
								if (adaptable instanceof AbstractTaskContainer
										&& ((AbstractTaskContainer) adaptable).getHandleIdentifier().equals(
												taskContainerDelta.getContainer().getHandleIdentifier())) {
									remove.add(adaptable);
								}
							}
							if(!remove.isEmpty()) {
								ArrayList<IAdaptable> elements = new ArrayList<IAdaptable>(Arrays.asList(workingSet.getElements()));
								elements.removeAll(remove);
								workingSet.setElements(elements.toArray(new IAdaptable[elements.size()]));
							}
						}
					}
					break;
			}
		}
	}

	public void taskAdded(AbstractTask task) {
	}

	public void taskDeleted(AbstractTask task) {
	}

	public void taskMoved(AbstractTask task, AbstractTaskContainer fromContainer, AbstractTaskContainer toContainer) {
	}
	
}
