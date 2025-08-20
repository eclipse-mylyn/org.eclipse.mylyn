/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation
 *     Tasktop Technologies - initial API and implementation
 *     Yatta Solutions - fix for bug 327262
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.workingsets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
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
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.monitor.ui.MonitorUi;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetUpdater;
import org.eclipse.ui.PlatformUI;

/**
 * @author Eugene Kuleshov
 * @author Mik Kersten
 * @author Steffen Pingel
 * @author Carsten Reckord
 */
public class TaskWorkingSetUpdater implements IWorkingSetUpdater, ITaskListChangeListener, IResourceChangeListener {

	public static String ID_TASK_WORKING_SET = "org.eclipse.mylyn.tasks.ui.workingSet"; //$NON-NLS-1$

	private final List<IWorkingSet> workingSets = new CopyOnWriteArrayList<>();

	private static class TaskWorkingSetDelta {

		private final IWorkingSet workingSet;

		private final List<Object> elements;

		private boolean changed;

		public TaskWorkingSetDelta(IWorkingSet workingSet) {
			this.workingSet = workingSet;
			elements = new ArrayList<>(Arrays.asList(workingSet.getElements()));
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

	private static boolean enabled = true;

	public TaskWorkingSetUpdater() {
		TasksUiInternal.getTaskList().addChangeListener(this);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	/**
	 * Set <code>enabled</code> to false to disable processing of task list changes, e.g. during import operations.
	 *
	 * @param enabled
	 */
	public static void setEnabled(boolean enabled) {
		TaskWorkingSetUpdater.enabled = enabled;
	}

	public static boolean isEnabled() {
		return enabled;
	}

	@Override
	public void dispose() {
		TasksUiInternal.getTaskList().removeChangeListener(this);
	}

	@Override
	public void add(IWorkingSet workingSet) {
		checkElementExistence(workingSet);
		synchronized (workingSets) {
			workingSets.add(workingSet);
		}
	}

	private void checkElementExistence(IWorkingSet workingSet) {
		ArrayList<IAdaptable> list = new ArrayList<>(Arrays.asList(workingSet.getElements()));
		boolean changed = false;
		for (Iterator<IAdaptable> iter = list.iterator(); iter.hasNext();) {
			IAdaptable adaptable = iter.next();
			boolean remove = false;
			if (adaptable instanceof AbstractTaskContainer) {
				String handle = ((AbstractTaskContainer) adaptable).getHandleIdentifier();
				remove = true;
				for (IRepositoryElement element : TasksUiPlugin.getTaskList().getRootElements()) {
					if (element != null && element.getHandleIdentifier().equals(handle)) {
						remove = false;
						break;
					}
				}
			} else if (adaptable instanceof IProject) {
				IProject project = ResourcesPlugin.getWorkspace()
						.getRoot()
						.getProject(((IProject) adaptable).getName());
				if (project == null || !project.exists()) {
					remove = true;
				}
			}
			if (remove) {
				iter.remove();
				changed = true;
			}
		}
		if (changed) {
			workingSet.setElements(list.toArray(new IAdaptable[list.size()]));
		}
	}

	@Override
	public boolean contains(IWorkingSet workingSet) {
		synchronized (workingSets) {
			return workingSets.contains(workingSet);
		}
	}

	@Override
	public boolean remove(IWorkingSet workingSet) {
		synchronized (workingSets) {
			return workingSets.remove(workingSet);
		}
	}

	@Override
	public void containersChanged(Set<TaskContainerDelta> delta) {
		if (!isEnabled()) {
			return;
		}
		for (TaskContainerDelta taskContainerDelta : delta) {
			if (taskContainerDelta.getElement() instanceof TaskCategory
					|| taskContainerDelta.getElement() instanceof IRepositoryQuery) {
				synchronized (workingSets) {
					switch (taskContainerDelta.getKind()) {
						case REMOVED:
							// Remove from all
							for (IWorkingSet workingSet : workingSets) {
								ArrayList<IAdaptable> elements = new ArrayList<>(
										Arrays.asList(workingSet.getElements()));
								elements.remove(taskContainerDelta.getElement());
								workingSet.setElements(elements.toArray(new IAdaptable[elements.size()]));
							}
							break;
						case ADDED:
							// Add to the active working set
							for (IWorkingSet workingSet : TaskWorkingSetUpdater.getEnabledSets()) {
								ArrayList<IAdaptable> elements = new ArrayList<>(
										Arrays.asList(workingSet.getElements()));
								elements.add(taskContainerDelta.getElement());
								workingSet.setElements(elements.toArray(new IAdaptable[elements.size()]));
							}
							break;
					}
				}
			}
		}
	}

	// TODO: consider putting back, but evaluate policy and note bug 197257
//	public void taskActivated(AbstractTask task) {
//		Set<AbstractTaskContainer> taskContainers = new HashSet<AbstractTaskContainer>(
//				TasksUiPlugin.getTaskList().getQueriesForHandle(task.getHandleIdentifier()));
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
		Set<IWorkingSet> workingSets = new HashSet<>();
		Set<IWorkbenchWindow> windows = MonitorUi.getMonitoredWindows();
		for (IWorkbenchWindow iWorkbenchWindow : windows) {
			IWorkbenchPage page = iWorkbenchWindow.getActivePage();
			if (page != null) {
				workingSets.addAll(Arrays.asList(page.getWorkingSets()));
			}
		}

		return workingSets.toArray(new IWorkingSet[workingSets.size()]);
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
		for (IWorkingSet enabledSet : enabledSets) {
			if (!enabledSet.equals(set)
					&& enabledSet.getId().equalsIgnoreCase(TaskWorkingSetUpdater.ID_TASK_WORKING_SET)) {
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

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		for (IWorkingSet workingSet : workingSets) {
			TaskWorkingSetDelta workingSetDelta = new TaskWorkingSetDelta(workingSet);
			if (event.getDelta() != null) {
				processResourceDelta(workingSetDelta, event.getDelta());
			}
			workingSetDelta.process();
		}
	}

	/**
	 * Must be called from the UI thread
	 */
	public static void applyWorkingSetsToAllWindows(Collection<IWorkingSet> workingSets) {
		IWorkingSet[] workingSetArray = workingSets.toArray(new IWorkingSet[workingSets.size()]);
		for (IWorkbenchWindow window : MonitorUi.getMonitoredWindows()) {
			for (IWorkbenchPage page : window.getPages()) {
				page.setWorkingSets(workingSetArray);
			}
		}
	}

	public static Set<IWorkingSet> getActiveWorkingSets(IWorkbenchWindow window) {
		if (window != null && window.getActivePage() != null) {
			Set<IWorkingSet> allSets = new HashSet<>(Arrays.asList(window.getActivePage().getWorkingSets()));
			Set<IWorkingSet> tasksSets = new HashSet<>(allSets);
			for (IWorkingSet workingSet : allSets) {
				if (workingSet.getId() == null
						|| !workingSet.getId().equalsIgnoreCase(TaskWorkingSetUpdater.ID_TASK_WORKING_SET)) {
					tasksSets.remove(workingSet);
				}
			}
			return tasksSets;
		} else {
			return Collections.emptySet();
		}
	}

}
