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
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetUpdater;
import org.eclipse.ui.PlatformUI;

/**
 * @author Eugene Kuleshov
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class TaskWorkingSetUpdater implements IWorkingSetUpdater, ITaskListChangeListener, IResourceChangeListener {

	public static String ID_TASK_WORKING_SET = "org.eclipse.mylyn.tasks.ui.workingSet";

	private final List<IWorkingSet> workingSets = new CopyOnWriteArrayList<IWorkingSet>();

	private static class TaskWorkingSetDelta {

		private final IWorkingSet workingSet;

		private final List<Object> elements;

		private boolean changed;

		public TaskWorkingSetDelta(IWorkingSet workingSet) {
			this.workingSet = workingSet;
			this.elements = new ArrayList<Object>(Arrays.asList(workingSet.getElements()));
		}

		public int indexOf(Object element) {
			return elements.indexOf(element);
		}

		public void set(int index, Object element) {
			elements.set(index, element);
			changed = true;
		}

		public void remove(int index) {
			if (elements.remove(index) != null) {
				changed = true;
			}
		}

		public void process() {
			if (changed) {
				workingSet.setElements(elements.toArray(new IAdaptable[elements.size()]));
			}
		}
	}

	public TaskWorkingSetUpdater() {
		TasksUi.getTaskListManager().getTaskList().addChangeListener(this);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	public void dispose() {
		TasksUi.getTaskListManager().getTaskList().removeChangeListener(this);
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
						// Remove from all
						for (IWorkingSet workingSet : workingSets) {
							ArrayList<IAdaptable> elements = new ArrayList<IAdaptable>(
									Arrays.asList(workingSet.getElements()));
							elements.remove(taskContainerDelta.getContainer());
							workingSet.setElements(elements.toArray(new IAdaptable[elements.size()]));
						}
						break;
					case ADDED:
						// Add to the active working set
						for (IWorkingSet workingSet : TaskWorkingSetUpdater.getEnabledSets()) {
							ArrayList<IAdaptable> elements = new ArrayList<IAdaptable>(
									Arrays.asList(workingSet.getElements()));
							elements.add(taskContainerDelta.getContainer());
							workingSet.setElements(elements.toArray(new IAdaptable[elements.size()]));
						}
						break;
					case CHANGED:
						// Ignore since containers change during synch with server
						break;
					}
				}
			}
		}
	}

	// TODO: consider putting back, but evaluate policy and note bug 197257
//	public void taskActivated(AbstractTask task) {
//		Set<AbstractTaskContainer> taskContainers = new HashSet<AbstractTaskContainer>(
//				TasksUiPlugin.getTaskListManager().getTaskList().getQueriesForHandle(task.getHandleIdentifier()));
//		taskContainers.addAll(task.getParentContainers());
//
//		Set<AbstractTaskContainer> allActiveWorkingSetContainers = new HashSet<AbstractTaskContainer>();
//		for (IWorkingSet workingSet : PlatformUI.getWorkbench()
//				.getActiveWorkbenchWindow()
//				.getActivePage()
//				.getWorkingSets()) {
//			ArrayList<IAdaptable> elements = new ArrayList<IAdaptable>(Arrays.asList(workingSet.getElements()));
//			for (IAdaptable adaptable : elements) {
//				if (adaptable instanceof AbstractTaskContainer) {
//					allActiveWorkingSetContainers.add((AbstractTaskContainer) adaptable);
//				}
//			}
//		}
//		boolean isContained = false;
//		for (AbstractTaskContainer taskContainer : allActiveWorkingSetContainers) {
//			if (taskContainers.contains(taskContainer)) {
//				isContained = true;
//				break;
//			}
//		}
//
//		;
//		if (!isContained) {
//			IWorkingSet matchingWorkingSet = null;
//			for (IWorkingSet workingSet : PlatformUI.getWorkbench().getWorkingSetManager().getAllWorkingSets()) {
//				ArrayList<IAdaptable> elements = new ArrayList<IAdaptable>(Arrays.asList(workingSet.getElements()));
//				for (IAdaptable adaptable : elements) {
//					if (adaptable instanceof AbstractTaskContainer) {
//						if (((AbstractTaskContainer)adaptable).contains(task.getHandleIdentifier())) {
//							matchingWorkingSet = workingSet;
//						}
//					}
//				}
//			}
//
//			if (matchingWorkingSet != null) {
//				new ToggleWorkingSetAction(matchingWorkingSet).run();
//			} else { 
//				new ToggleAllWorkingSetsAction(PlatformUI.getWorkbench().getActiveWorkbenchWindow()).run();
//			}
//		}
//	}

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
		for (IWorkingSet enabledSet : enabledSets) {
			if (enabledSet.equals(set)) {
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
			if (!enabledSets[i].equals(set)
					&& enabledSets[i].getId().equalsIgnoreCase(TaskWorkingSetUpdater.ID_TASK_WORKING_SET)) {
				return false;
			}
		}
		return true;
	}

	private void processResourceDelta(TaskWorkingSetDelta result, IResourceDelta delta) {
		IResource resource = delta.getResource();
		int type = resource.getType();
		int index = result.indexOf(resource);
		int kind = delta.getKind();
		int flags = delta.getFlags();
		if (kind == IResourceDelta.CHANGED && type == IResource.PROJECT && index != -1) {
			if ((flags & IResourceDelta.OPEN) != 0) {
				result.set(index, resource);
			}
		}
		if (index != -1 && kind == IResourceDelta.REMOVED) {
			if ((flags & IResourceDelta.MOVED_TO) != 0) {
				result.set(index, ResourcesPlugin.getWorkspace().getRoot().findMember(delta.getMovedToPath()));
			} else {
				result.remove(index);
			}
		}

		// Don't dive into closed or opened projects
		if (projectGotClosedOrOpened(resource, kind, flags)) {
			return;
		}

		IResourceDelta[] children = delta.getAffectedChildren();
		for (IResourceDelta element : children) {
			processResourceDelta(result, element);
		}
	}

	private boolean projectGotClosedOrOpened(IResource resource, int kind, int flags) {
		return resource.getType() == IResource.PROJECT && kind == IResourceDelta.CHANGED
				&& (flags & IResourceDelta.OPEN) != 0;
	}

	public void resourceChanged(IResourceChangeEvent event) {
		for (IWorkingSet workingSet : workingSets) {
			TaskWorkingSetDelta workingSetDelta = new TaskWorkingSetDelta(workingSet);
			if (event.getDelta() != null) {
				processResourceDelta(workingSetDelta, event.getDelta());
			}
			workingSetDelta.process();
		}
	}

	public void taskListRead() {
		// ignore
	}

}
