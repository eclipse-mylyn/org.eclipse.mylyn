/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiBridgePlugin;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.team.ui.AbstractActiveChangeSetProvider;
import org.eclipse.mylyn.team.ui.AbstractContextChangeSetManager;
import org.eclipse.mylyn.team.ui.IContextChangeSet;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSet;
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

	private final Map<String, IContextChangeSet> activeChangeSets = new HashMap<String, IContextChangeSet>();

	private static final String LABEL_NO_TASK = "<No Active Task>";

	private static final String HANDLE_NO_TASK = "org.eclipse.mylyn.team.ui.inactive.proxy";

	private final Map<ActiveChangeSetManager, ActiveChangeSet> noTaskSetMap = new HashMap<ActiveChangeSetManager, ActiveChangeSet>();;

	private final ITask noTaskActiveProxy = new LocalTask(HANDLE_NO_TASK, LABEL_NO_TASK);

	/**
	 * Used to restore change sets managed with task context when platform deletes them, bug 168129
	 */
	private final IChangeSetChangeListener CHANGE_SET_LISTENER = new IChangeSetChangeListener() {

		public void setRemoved(ChangeSet set) {
			if (set instanceof IContextChangeSet) {
				IContextChangeSet contextChangeSet = (IContextChangeSet) set;
				if (contextChangeSet.getTask() != null && contextChangeSet.getTask().isActive()) {
					for (ActiveChangeSetManager collector : changeSetManagers) {
						// put it back
						collector.add((ActiveChangeSet) contextChangeSet);
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
		Collection<AbstractActiveChangeSetProvider> providerList = FocusedTeamUiPlugin.getDefault()
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
	protected void updateChangeSetLabel(ITask task) {
		for (ActiveChangeSetManager collector : changeSetManagers) {
			ChangeSet[] sets = collector.getSets();
			for (ChangeSet set : sets) {
				if (set instanceof IContextChangeSet) {
					IContextChangeSet contextChangeSet = (IContextChangeSet) set;
					if (contextChangeSet.getTask().equals(task)) {
						contextChangeSet.updateLabel();
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
		for (ActiveChangeSetManager manager : changeSetManagers) {
			ChangeSet[] sets = manager.getSets();
			for (ChangeSet restoredSet : sets) {
				if (!(restoredSet instanceof IContextChangeSet)) {
					String encodedTitle = restoredSet.getName();
					String taskHandle = ContextChangeSet.getHandleFromPersistedTitle(encodedTitle);
					ITask task = TasksUiPlugin.getTaskList().getTask(taskHandle);
					if (task != null) {
						try {
							AbstractActiveChangeSetProvider changeSetProvider = FocusedTeamUiPlugin.getDefault()
									.getActiveChangeSetProvider(manager);
							IContextChangeSet contextChangeSet = changeSetProvider.createChangeSet(task);

							if (contextChangeSet instanceof ActiveChangeSet) {
//							IContextChangeSet contextChangeSet = new ContextChangeSet(task, collector);
								contextChangeSet.restoreResources(restoredSet.getResources());
								manager.remove(restoredSet);
								manager.add((ActiveChangeSet) contextChangeSet);
							}
						} catch (Exception e) {
							StatusHandler.log(new Status(IStatus.ERROR, FocusedTeamUiPlugin.ID_PLUGIN,
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

	public IResource[] getResources(ITask task) {
		for (ActiveChangeSetManager collector : changeSetManagers) {
			ChangeSet[] sets = collector.getSets();
			for (ChangeSet set : sets) {
				if (set instanceof IContextChangeSet) {
					IContextChangeSet contextChangeSet = (IContextChangeSet) set;
					if (contextChangeSet.getTask().equals(task) && contextChangeSet instanceof ActiveChangeSet) {
						return ((ActiveChangeSet) contextChangeSet).getResources();
					}
				}
			}
		}
		return null;
	}

	@Override
	public void contextActivated(IInteractionContext context) {
		try {
			ITask task = getTask(context);
			if (task != null && !activeChangeSets.containsKey(task.getHandleIdentifier())) {
				for (ActiveChangeSetManager manager : changeSetManagers) {

					AbstractActiveChangeSetProvider changeSetProvider = FocusedTeamUiPlugin.getDefault()
							.getActiveChangeSetProvider(manager);
					IContextChangeSet contextChangeSet = changeSetProvider.createChangeSet(task);

//					ContextChangeSet contextChangeSet = new ContextChangeSet(task, manager);
					if (contextChangeSet instanceof ActiveChangeSet) {
						ActiveChangeSet activeChangeSet = (ActiveChangeSet) contextChangeSet;
						List<IResource> interestingResources = ResourcesUiBridgePlugin.getDefault()
								.getInterestingResources(context);
						activeChangeSet.add(interestingResources.toArray(new IResource[interestingResources.size()]));

						activeChangeSets.put(task.getHandleIdentifier(), contextChangeSet);

						if (!manager.contains(activeChangeSet)) {
							manager.add(activeChangeSet);
						}
						manager.makeDefault(activeChangeSet);
					}
				}
			}
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, FocusedTeamUiPlugin.ID_PLUGIN, "Could not update change set", e));
		}
	}

	@Override
	public void contextDeactivated(IInteractionContext context) {
		for (ActiveChangeSetManager collector : changeSetManagers) {
			ChangeSet[] sets = collector.getSets();
			for (ChangeSet set : sets) {
				if (set instanceof ActiveChangeSet) {
					IResource[] resources = set.getResources();
					if (resources == null || resources.length == 0) {
						collector.remove(set);
					}
				}
			}
			// First look for it in the collector, then in our cache
			ActiveChangeSet noTaskSet = collector.getSet(LABEL_NO_TASK);
			if (noTaskSet == null) {
				noTaskSet = noTaskSetMap.get(collector);
			}

			if (noTaskSet == null) {
				AbstractActiveChangeSetProvider changeSetProvider = FocusedTeamUiPlugin.getDefault()
						.getActiveChangeSetProvider(collector);
				noTaskSet = (ActiveChangeSet) changeSetProvider.createChangeSet(noTaskActiveProxy);
				collector.add(noTaskSet);
				noTaskSetMap.put(collector, noTaskSet);
			}
			// TODO: not great to do the lookup based on a String value in case the user created this set
			collector.makeDefault(noTaskSet);
			collector.remove(noTaskSet);
		}
		activeChangeSets.clear();
	}

	@Override
	public void interestChanged(List<IInteractionElement> elements) {
		for (IInteractionElement element : elements) {
			AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(element.getContentType());
			try {
				if (bridge.isDocument(element.getHandleIdentifier())) {
					IResource resource = ResourcesUiBridgePlugin.getDefault().getResourceForElement(element, false);
					if (resource != null && resource.exists()) {
						for (IContextChangeSet activeContextChangeSet : getActiveChangeSets()) {
							if (activeContextChangeSet instanceof ActiveChangeSet) {
								if (!((ActiveChangeSet) activeContextChangeSet).contains(resource)) {
									if (element.getInterest().isInteresting()) {
										((ActiveChangeSet) activeContextChangeSet).add(new IResource[] { resource });
									}
								}
							}
						}
						if (shouldRemove(element)) {
							for (ActiveChangeSetManager collector : changeSetManagers) {
								ChangeSet[] sets = collector.getSets();
								for (ChangeSet set : sets) {
									if (set instanceof ActiveChangeSet) {
										set.remove(resource);
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.ERROR, FocusedTeamUiPlugin.ID_PLUGIN,
						"Could not manipulate change set resources", e));
			}
		}
	}

	public List<IContextChangeSet> getActiveChangeSets() {
		return new ArrayList<IContextChangeSet>(activeChangeSets.values());
	}

	private ITask getTask(IInteractionContext context) {
		return TasksUi.getTaskActivityManager().getActiveTask();
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
