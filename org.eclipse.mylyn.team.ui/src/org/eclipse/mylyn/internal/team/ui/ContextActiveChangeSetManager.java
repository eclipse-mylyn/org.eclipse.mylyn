/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.resources.ResourcesUiBridgePlugin;
import org.eclipse.mylyn.tasks.core.AbstractTask;
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

	private final List<ActiveChangeSetManager> changeSetManagers = new ArrayList<ActiveChangeSetManager>();

	private final Map<String, ContextChangeSet> activeChangeSets = new HashMap<String, ContextChangeSet>();

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
		Set<AbstractActiveChangeSetProvider> providerList = FocusedTeamUiPlugin.getDefault()
				.getActiveChangeSetProviders();
		for (AbstractActiveChangeSetProvider provider : providerList) {
			ActiveChangeSetManager changeSetManager = provider.getActiveChangeSetManager();
			if (null != changeSetManager) {
				changeSetManagers.add(changeSetManager);
				changeSetManager.addListener(CHANGE_SET_LISTENER);
			}
		}
	}

	@Override
	protected void updateChangeSetLabel(AbstractTask task) {
		for (ActiveChangeSetManager collector : changeSetManagers) {
			ChangeSet[] sets = collector.getSets();
			for (ChangeSet set : sets) {
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
			for (ChangeSet restoredSet : sets) {
				if (!(restoredSet instanceof ContextChangeSet)) {
					String encodedTitle = restoredSet.getName();
					String taskHandle = ContextChangeSet.getHandleFromPersistedTitle(encodedTitle);
					AbstractTask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(taskHandle);
					if (task != null) {
						try {
							ContextChangeSet contextChangeSet = new ContextChangeSet(task, collector);
							contextChangeSet.restoreResources(restoredSet.getResources());
							collector.remove(restoredSet);
							collector.add(contextChangeSet);
						} catch (Exception e) {
							StatusHandler.log(new Status(IStatus.ERROR, FocusedTeamUiPlugin.PLUGIN_ID,
									"Could not restore change set", e));
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

	public IResource[] getResources(AbstractTask task) {
		for (ActiveChangeSetManager collector : changeSetManagers) {
			ChangeSet[] sets = collector.getSets();
			for (ChangeSet set : sets) {
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
			AbstractTask task = getTask(context);
			if (task != null && !activeChangeSets.containsKey(task.getHandleIdentifier())) {
				for (ActiveChangeSetManager collector : changeSetManagers) {
					ContextChangeSet contextChangeSet = new ContextChangeSet(task, collector);
					List<IResource> interestingResources = ResourcesUiBridgePlugin.getDefault()
							.getInterestingResources(context);
					contextChangeSet.add(interestingResources.toArray(new IResource[interestingResources.size()]));

					activeChangeSets.put(task.getHandleIdentifier(), contextChangeSet);

					if (!collector.contains(contextChangeSet)) {
						collector.add(contextChangeSet);
					}
				}
			}
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, FocusedTeamUiPlugin.PLUGIN_ID, "Could not update change set", e));
		}
	}

	public void contextDeactivated(IInteractionContext context) {
		for (ActiveChangeSetManager collector : changeSetManagers) {
			ChangeSet[] sets = collector.getSets();
			for (ChangeSet set : sets) {
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
								for (ChangeSet set : sets) {
									if (set instanceof ContextChangeSet) {
										set.remove(resource);
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.ERROR, FocusedTeamUiPlugin.PLUGIN_ID,
						"Could not manipulate change set resources", e));
			}
		}
	}

	public List<ContextChangeSet> getActiveChangeSets() {
		return new ArrayList<ContextChangeSet>(activeChangeSets.values());
	}

	private AbstractTask getTask(IInteractionContext context) {
		return TasksUiPlugin.getTaskListManager().getTaskList().getActiveTask();
	}

	/**
	 * Ignores decay.
	 */
	private boolean shouldRemove(IInteractionElement element) {
		// TODO: generalize this logic?
		return (element.getInterest().getValue() + element.getInterest().getDecayValue()) < ContextCore.getCommonContextScaling()
				.getInteresting();
	}
}
