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

package org.eclipse.mylyn.internal.team.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.core.MylarStatusHandler;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.resources.ResourcesUiBridgePlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.team.ui.AbstractActiveChangeSetProvider;
import org.eclipse.mylyn.team.ui.AbstractContextChangeSetManager;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSetManager;
import org.eclipse.team.internal.core.subscribers.ChangeSet;
import org.eclipse.team.internal.core.subscribers.IChangeSetChangeListener;

/**
 * NOTE: this class contains several work-arounds for change set limitations in the Platform/Team support.
 * 
 * @author Mik Kersten
 */
public class ContextActiveChangeSetManager extends AbstractContextChangeSetManager {

	private List<ActiveChangeSetManager> changeSetManagers = new ArrayList<ActiveChangeSetManager>();

	private Map<String, ContextChangeSet> activeChangeSets = new HashMap<String, ContextChangeSet>();

	/**
	 * Used to restore change sets managed with task context when platform deletes them, bug 168129
	 */
	private final IChangeSetChangeListener CHANGE_SET_LISTENER = new IChangeSetChangeListener() {
		public void setRemoved(ChangeSet set) {
			if (set instanceof ContextChangeSet) {
				ContextChangeSet contextChangeSet = (ContextChangeSet) set;
				if (contextChangeSet.getTask() != null && contextChangeSet.getTask().isActive()) {
					for (ActiveChangeSetManager collector : changeSetManagers) {
						// put it back
						collector.add(contextChangeSet); 
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

	public ContextActiveChangeSetManager() {
		Set<AbstractActiveChangeSetProvider> providerList = FocusedTeamUiPlugin.getDefault().getActiveChangeSetProviders();
		for (AbstractActiveChangeSetProvider provider : providerList) {
			ActiveChangeSetManager changeSetManager = provider.getActiveChangeSetManager();
			if (null != changeSetManager) {
				changeSetManagers.add(changeSetManager);
				changeSetManager.addListener(CHANGE_SET_LISTENER);
			}
		}
	}

	@Override
	protected void updateChangeSetLabel(ITask task) {
		for (ActiveChangeSetManager collector : changeSetManagers) {
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

	@Override
	public void enable() {
		super.enable();
		if (!isEnabled) {
			for (ActiveChangeSetManager collector : changeSetManagers) {
				collector.addListener(CHANGE_SET_LISTENER);
			}
		}
	}

	@Override
	public void disable() {
		super.disable();
		for (ActiveChangeSetManager collector : changeSetManagers) {
			collector.removeListener(CHANGE_SET_LISTENER);
		}
	}

	@Override
	protected void initContextChangeSets() {
		for (ActiveChangeSetManager collector : changeSetManagers) {
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
		for (ActiveChangeSetManager collector : changeSetManagers) {
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

	public void contextActivated(IInteractionContext context) {
		try {
			ITask task = getTask(context);
			if (task != null && !activeChangeSets.containsKey(task.getHandleIdentifier())) {
				for (ActiveChangeSetManager collector : changeSetManagers) {
					ContextChangeSet contextChangeSet = new ContextChangeSet(task, collector);
					List<IResource> interestingResources = ResourcesUiBridgePlugin.getDefault().getInterestingResources(
							context);
					contextChangeSet.add(interestingResources.toArray(new IResource[interestingResources.size()]));

					activeChangeSets.put(task.getHandleIdentifier(), contextChangeSet);

					if (!collector.contains(contextChangeSet)) {
						collector.add(contextChangeSet);
					}
				}
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "could not update change set", false);
		}
	}

	public void contextDeactivated(IInteractionContext context) {
		for (ActiveChangeSetManager collector : changeSetManagers) {
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

	public void contextCleared(IInteractionContext context) {
		// ignore
	}
	
	public void interestChanged(List<IInteractionElement> elements) {
		for (IInteractionElement element : elements) {
			AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(
					element.getContentType());
			try {
				if (bridge.isDocument(element.getHandleIdentifier())) {
					IResource resource = ResourcesUiBridgePlugin.getDefault().getResourceForElement(element, false);
					if (resource != null && resource.exists()) {
						for (ContextChangeSet activeContextChangeSet : getActiveChangeSets()) {
							if (!activeContextChangeSet.contains(resource)) {
								if (element.getInterest().isInteresting()) {
									activeContextChangeSet.add(new IResource[] { resource });
								}
							}
						}
						if (shouldRemove(element)) {
							for (ActiveChangeSetManager collector : changeSetManagers) {
								ChangeSet[] sets = collector.getSets();
								for (int i = 0; i < sets.length; i++) {
									if (sets[i] instanceof ContextChangeSet) {
										sets[i].remove(resource);
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				MylarStatusHandler.fail(e, "could not manipulate change set resources", false);
			}
		}
	}

	public List<ContextChangeSet> getActiveChangeSets() {
		return new ArrayList<ContextChangeSet>(activeChangeSets.values());
	}

	private ITask getTask(IInteractionContext context) {
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
	private boolean shouldRemove(IInteractionElement element) {
		// TODO: generalize this logic?
		return (element.getInterest().getValue() + element.getInterest().getDecayValue()) < InteractionContextManager
				.getScalingFactors().getInteresting();
	}
}
