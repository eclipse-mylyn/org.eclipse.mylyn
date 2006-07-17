/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.context.core.IMylarContext;
import org.eclipse.mylar.context.core.IMylarContextListener;
import org.eclipse.mylar.context.core.IMylarElement;
import org.eclipse.mylar.context.core.IMylarStructureBridge;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.context.core.MylarContextManager;
import org.eclipse.mylar.resources.MylarResourcesPlugin;
import org.eclipse.mylar.tasks.core.AbstractTaskContainer;
import org.eclipse.mylar.tasks.core.DateRangeContainer;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskActivityListener;
import org.eclipse.mylar.tasks.core.ITaskListChangeListener;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.team.TeamRepositoryProvider;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSetManager;
import org.eclipse.team.internal.core.subscribers.ChangeSet;
import org.eclipse.team.internal.core.subscribers.IChangeSetChangeListener;

/**
 * @author Mik Kersten
 */
public class ContextChangeSetManager implements IMylarContextListener {

	private final IChangeSetChangeListener CHANGE_SET_LISTENER = new IChangeSetChangeListener() {
		public void setRemoved(ChangeSet set) {
			if (set instanceof ContextChangeSet) {
				ContextChangeSet contextChangeSet = (ContextChangeSet) set;
				if (contextChangeSet.getTask().isActive()) {
					for (ActiveChangeSetManager collector : collectors) {
						collector.add(contextChangeSet); // put it back
					}
				}
			}
		}

		public void setAdded(ChangeSet set) {
			// ignore
		}

		public void defaultSetChanged(ChangeSet previousDefault, ChangeSet set) {
			// ignore
		}

		public void nameChanged(ChangeSet set) {
			// ignore
		}

		public void resourcesChanged(ChangeSet set, IPath[] paths) {
			// ignore
		}
	};

	private List<ActiveChangeSetManager> collectors = new ArrayList<ActiveChangeSetManager>();

	private Map<String, ContextChangeSet> activeChangeSets = new HashMap<String, ContextChangeSet>();

	private ITaskActivityListener TASK_ACTIVITY_LISTENER = new ITaskActivityListener() {
		
		public void taskListRead() {
			initContextChangeSets();
		}
		
		public void taskActivated(ITask task) {
			// ignore
		}

		public void tasksActivated(List<ITask> tasks) {
			// ignore
		}

		public void taskDeactivated(ITask task) {
			// ignore			
		}

		public void activityChanged(DateRangeContainer week) {
			// ignore	
		}

		public void calendarChanged() {
			// ignore
		}
	};
	
	private ITaskListChangeListener TASK_CHANGE_LISTENER = new ITaskListChangeListener() {

		public void localInfoChanged(ITask task) {
			for (ActiveChangeSetManager collector : collectors) {
				ChangeSet[] sets = collector.getSets();
				for (int i = 0; i < sets.length; i++) {
					ChangeSet set = sets[i];
					if (set instanceof ContextChangeSet) {
						ContextChangeSet contextChangeSet = (ContextChangeSet) set;
						if (contextChangeSet.getTask().equals(task)) {
							contextChangeSet.initTitle();
						}
					}
				}
			}
		}

		public void repositoryInfoChanged(ITask task) {
			// ignore
		}

		public void taskMoved(ITask task, AbstractTaskContainer fromContainer, AbstractTaskContainer toContainer) {
			// ignore
		}

		public void taskDeleted(ITask task) {
			// ignore
		}

		public void containerAdded(AbstractTaskContainer container) {
			// ignore
		}

		public void containerDeleted(AbstractTaskContainer container) {
			// ignore
		}

		public void taskAdded(ITask task) {
			// ignore
		}

		public void containerInfoChanged(AbstractTaskContainer container) {
			// ignore
		}
	};

	private boolean isEnabled = false;

	public ContextChangeSetManager() {
		List<TeamRepositoryProvider> providerList = TeamRespositoriesManager.getInstance().getProviders();
		for (TeamRepositoryProvider provider : providerList) {
			ActiveChangeSetManager changeSetManager = provider.getActiveChangeSetManager();
			if(null != changeSetManager)
				collectors.add(changeSetManager);
		}
	}

	public void enable() {
		if (!isEnabled) {
			ContextCorePlugin.getContextManager().addListener(this);
			TasksUiPlugin.getTaskListManager().getTaskList().addChangeListener(TASK_CHANGE_LISTENER);
			TasksUiPlugin.getTaskListManager().addActivityListener(TASK_ACTIVITY_LISTENER);
			if (TasksUiPlugin.getTaskListManager().isTaskListInitialized()) {
				initContextChangeSets(); // otherwise listener will do it
			}
			for (ActiveChangeSetManager collector : collectors) {
				collector.addListener(CHANGE_SET_LISTENER);
			}
			isEnabled = true;
		}
	}

	public void disable() {
		ContextCorePlugin.getContextManager().removeListener(this);
		TasksUiPlugin.getTaskListManager().removeActivityListener(TASK_ACTIVITY_LISTENER);
		TasksUiPlugin.getTaskListManager().getTaskList().removeChangeListener(TASK_CHANGE_LISTENER);
		for (ActiveChangeSetManager collector : collectors) {
			collector.removeListener(CHANGE_SET_LISTENER);
		}
		isEnabled = false;
	}

	private void initContextChangeSets() {
		for (ActiveChangeSetManager collector : collectors) {
			ChangeSet[] sets = collector.getSets();
			for (int i = 0; i < sets.length; i++) {
				ChangeSet restoredSet = sets[i];
				if (!(restoredSet instanceof ContextChangeSet)) {
					String encodedTitle = restoredSet.getName();
					String taskHandle = ContextChangeSet.getHandleFromPersistedTitle(encodedTitle);
					ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(taskHandle);
					if (task != null) {
						try {
							ContextChangeSet contextChangeSet = new ContextChangeSet(task, collector);
							contextChangeSet.restoreResources(restoredSet.getResources());
							collector.remove(restoredSet);
							collector.add(contextChangeSet);
						} catch (Exception e) {
							MylarStatusHandler.fail(e, "could not restore change set", false);
						}
					}
				}
			}
		}
	}

	/**
	 * For testing.
	 */
	public void clearActiveChangeSets() {
		activeChangeSets.clear();
	}

	public IResource[] getResources(ITask task) {
		for (ActiveChangeSetManager collector : collectors) {
			ChangeSet[] sets = collector.getSets();
			for (int i = 0; i < sets.length; i++) {
				ChangeSet set = sets[i];
				if (set instanceof ContextChangeSet) {
					ContextChangeSet contextChangeSet = (ContextChangeSet) set;
					if (contextChangeSet.getTask().equals(task)) {
						return contextChangeSet.getResources();
					}
				}
			}
		}
		return null;
	}

	public void contextActivated(IMylarContext context) {
		try {
			ITask task = getTask(context);
			if (task != null && !activeChangeSets.containsKey(task.getHandleIdentifier())) {
				for (ActiveChangeSetManager collector : collectors) {
					ContextChangeSet contextChangeSet = new ContextChangeSet(task, collector);
					List<IResource> interestingResources = MylarResourcesPlugin.getDefault().getInterestingResources();
					contextChangeSet.add(interestingResources.toArray(new IResource[interestingResources.size()]));
	
					activeChangeSets.put(task.getHandleIdentifier(), contextChangeSet);

					if (!collector.contains(contextChangeSet)) {
						collector.add(contextChangeSet);
					}
//					collector.makeDefault(contextChangeSet);
//					IdeUiUtil.forceSynchronizeViewUpdate();
//					DiffChangeEvent event = new DiffChangeEvent(contextChangeSet.getDiffTree());
//					collector.diffsChanged(event, new NullProgressMonitor());
				}
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "could not update change set", false);
		}
	}

	public void contextDeactivated(IMylarContext context) {
		for (ActiveChangeSetManager collector : collectors) {
			ChangeSet[] sets = collector.getSets();
			for (int i = 0; i < sets.length; i++) {
				ChangeSet set = sets[i];
				if (set instanceof ContextChangeSet) {
					IResource[] resources = set.getResources();
					if (resources == null || resources.length == 0) {
						collector.remove(set);
					}
				}
			}
		}
		activeChangeSets.clear();
	}

	public List<ContextChangeSet> getActiveChangeSets() {
		return new ArrayList<ContextChangeSet>(activeChangeSets.values());
	}

	private ITask getTask(IMylarContext context) {
		List<ITask> activeTasks = TasksUiPlugin.getTaskListManager().getTaskList().getActiveTasks();

		// TODO: support multiple tasks
		if (activeTasks.size() > 0) {
			return activeTasks.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Ignores decay.
	 */
	private boolean shouldRemove(IMylarElement element) {
		// TODO: generalize this logic?
		return (element.getInterest().getValue() + element.getInterest().getDecayValue()) < MylarContextManager
				.getScalingFactors().getInteresting();
	}

	public void interestChanged(List<IMylarElement> elements) {
		for (IMylarElement element : elements) {
			IMylarStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(element.getContentType());
			try {
				if (bridge.isDocument(element.getHandleIdentifier())) {
					IResource resource = MylarResourcesPlugin.getDefault().getResourceForElement(element, false);
					if (resource != null && resource.exists()) {
						for (ContextChangeSet activeContextChangeSet : getActiveChangeSets()) {
							if (!activeContextChangeSet.contains(resource)) {
								if (element.getInterest().isInteresting()) {
									activeContextChangeSet.add(new IResource[] { resource });
								}
							}
						}
						if (shouldRemove(element)) {
							for (ActiveChangeSetManager collector : collectors) {
								ChangeSet[] sets = collector.getSets();
								for (int i = 0; i < sets.length; i++) {
									if (sets[i] instanceof ContextChangeSet) {
										sets[i].remove(resource);
									}
								}
							}
						}
//						if (shouldRemove(element)) {
//							ChangeSet[] sets = collector.getSets();
//							for (int i = 0; i < sets.length; i++) {
//								if (sets[i] instanceof MylarActiveChangeSet) {
//									sets[i].remove(resource);
//								}
//							}
//						}
					}
				}
			} catch (Exception e) {
				MylarStatusHandler.fail(e, "could not manipulate change set resources", false);
			}
		}
	}

	public void nodeDeleted(IMylarElement node) {
		// TODO: handle?
	}

	public void landmarkAdded(IMylarElement node) {
		// ignore
	}

	public void landmarkRemoved(IMylarElement node) {
		// ignore
	}

	public void edgesChanged(IMylarElement node) {
		// ignore
	}

	public void presentationSettingsChanging(UpdateKind kind) {
		// ignore
	}

	public void presentationSettingsChanged(UpdateKind kind) {
		// ignore
	}
}
