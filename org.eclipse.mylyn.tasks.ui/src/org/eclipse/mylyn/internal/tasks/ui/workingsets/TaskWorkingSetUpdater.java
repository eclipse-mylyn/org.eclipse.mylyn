/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.workingsets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.ui.actions.ToggleAllWorkingSetsAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.ToggleWorkingSetAction;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetUpdater;
import org.eclipse.ui.PlatformUI;

/**
 * @author Eugene Kuleshov
 * @author Mik Kersten
 */
public class TaskWorkingSetUpdater implements IWorkingSetUpdater, ITaskListChangeListener, ITaskActivityListener {

	public static String ID_TASK_WORKING_SET = "org.eclipse.mylyn.tasks.ui.workingSet";

	private List<IWorkingSet> workingSets = new ArrayList<IWorkingSet>();

	public TaskWorkingSetUpdater() {
		TasksUiPlugin.getTaskListManager().getTaskList().addChangeListener(this);
		TasksUiPlugin.getTaskListManager().addActivityListener(this);
	}

	public void dispose() {
		TasksUiPlugin.getTaskListManager().getTaskList().removeChangeListener(this);
		TasksUiPlugin.getTaskListManager().removeActivityListener(this);
	}

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
					if (element != null && element.getHandleIdentifier().equals(handle)) {
						list.add(adaptable);
					}
				}
			} else if (adaptable instanceof IProject) {
				IProject project = ResourcesPlugin.getWorkspace()
						.getRoot()
						.getProject(((IProject) adaptable).getName());
				if (project != null && project.exists()) {
					list.add(project);
				}
			}
		}
		workingSet.setElements(list.toArray(new IAdaptable[list.size()]));
	}

	public boolean contains(IWorkingSet workingSet) {
		synchronized (workingSets) {
			return workingSets.contains(workingSet);
		}
	}

	public boolean remove(IWorkingSet workingSet) {
		synchronized (workingSets) {
			return workingSets.remove(workingSet);
		}
	}

	public void containersChanged(Set<TaskContainerDelta> delta) {
		for (TaskContainerDelta taskContainerDelta : delta) {
			if (taskContainerDelta.getContainer() instanceof TaskCategory
					|| taskContainerDelta.getContainer() instanceof AbstractRepositoryQuery) {
				synchronized (workingSets) {
					switch (taskContainerDelta.getKind()) {
					case REMOVED:
						for (IWorkingSet workingSet : workingSets) {
							ArrayList<IAdaptable> elements = new ArrayList<IAdaptable>(
									Arrays.asList(workingSet.getElements()));
							elements.remove(taskContainerDelta.getContainer());
							workingSet.setElements(elements.toArray(new IAdaptable[elements.size()]));
						}
						break;
					case ADDED:
						for (IWorkingSet workingSet : workingSets) {
							ArrayList<IAdaptable> elements = new ArrayList<IAdaptable>(
									Arrays.asList(workingSet.getElements()));
							elements.add(taskContainerDelta.getContainer());
							workingSet.setElements(elements.toArray(new IAdaptable[elements.size()]));
						}
					}
					break;
				}
			}
		}
	}

	public void activityChanged(ScheduledTaskContainer week) {
		// ignore
	}

	public void taskActivated(AbstractTask task) {
		Set<AbstractTaskContainer> taskContainers = new HashSet<AbstractTaskContainer>(
				TasksUiPlugin.getTaskListManager().getTaskList().getQueriesForHandle(task.getHandleIdentifier()));
		taskContainers.addAll(task.getParentContainers());

		Set<AbstractTaskContainer> allActiveWorkingSetContainers = new HashSet<AbstractTaskContainer>();
		for (IWorkingSet workingSet : PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage()
				.getWorkingSets()) {
			ArrayList<IAdaptable> elements = new ArrayList<IAdaptable>(Arrays.asList(workingSet.getElements()));
			for (IAdaptable adaptable : elements) {
				if (adaptable instanceof AbstractTaskContainer) {
					allActiveWorkingSetContainers.add((AbstractTaskContainer) adaptable);
				}
			}
		}
		boolean isContained = false;
		for (AbstractTaskContainer taskContainer : allActiveWorkingSetContainers) {
			if (taskContainers.contains(taskContainer)) {
				isContained = true;
				break;
			}
		}

		;
		if (!isContained) {
			IWorkingSet matchingWorkingSet = null;
			for (IWorkingSet workingSet : PlatformUI.getWorkbench().getWorkingSetManager().getAllWorkingSets()) {
				ArrayList<IAdaptable> elements = new ArrayList<IAdaptable>(Arrays.asList(workingSet.getElements()));
				for (IAdaptable adaptable : elements) {
					if (adaptable instanceof AbstractTaskContainer) {
						if (((AbstractTaskContainer)adaptable).contains(task.getHandleIdentifier())) {
							matchingWorkingSet = workingSet;
						}
					}
				}
			}

			if (matchingWorkingSet != null) {
				new ToggleWorkingSetAction(matchingWorkingSet).run();
			} else { 
				new ToggleAllWorkingSetsAction(PlatformUI.getWorkbench().getActiveWorkbenchWindow()).run();
			}
		}
	}

	public void taskDeactivated(AbstractTask task) {
		// ignore
	}

	public void taskListRead() {
		// ignore
	}

	public static IWorkingSet[] getEnabledSets() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getWorkingSets();
	}

	/**
	 * TODO: move
	 */
	public static boolean areNoTaskWorkingSetsEnabled() {
		IWorkingSet[] workingSets = PlatformUI.getWorkbench().getWorkingSetManager().getWorkingSets();
		for (IWorkingSet workingSet : workingSets) {
			if (workingSet != null && workingSet.getId().equalsIgnoreCase(ID_TASK_WORKING_SET)) {
				if (isWorkingSetEnabled(workingSet)) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean isWorkingSetEnabled(IWorkingSet set) {
		IWorkingSet[] enabledSets = TaskWorkingSetUpdater.getEnabledSets();
		for (int i = 0; i < enabledSets.length; i++) {
			if (enabledSets[i].equals(set)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isOnlyTaskWorkingSetEnabled(IWorkingSet set) {
		if (!TaskWorkingSetUpdater.isWorkingSetEnabled(set)) {
			return false;
		}

		IWorkingSet[] enabledSets = TaskWorkingSetUpdater.getEnabledSets();
		for (int i = 0; i < enabledSets.length; i++) {
			if (!enabledSets[i].equals(set) && enabledSets[i].getId().equalsIgnoreCase(TaskWorkingSetUpdater.ID_TASK_WORKING_SET)) {
				return false;
			}
		}
		return true;
	}
}
