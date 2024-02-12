/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
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
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
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

	private final List<ActiveChangeSetManager> changeSetManagers = new ArrayList<>();

	private final List<IContextChangeSet> activeChangeSets = new ArrayList<>();

	private final Map<ActiveChangeSetManager, ChangeSetChangeListener> listenerByManager = new HashMap<>();

	/**
	 * Used to restore change sets managed with task context when platform deletes them, bug 168129
	 */
	private class ChangeSetChangeListener implements IChangeSetChangeListener {

		private final ActiveChangeSetManager manager;

		public ChangeSetChangeListener(ActiveChangeSetManager manager) {
			this.manager = manager;
		}

		@Override
		public void setRemoved(ChangeSet set) {
			if (set instanceof IContextChangeSet contextChangeSet) {
				// never matches the noTask change set: its task is never active
				if (contextChangeSet.getTask() != null && contextChangeSet.getTask().isActive()) {
					// put it back
					manager.add((ActiveChangeSet) contextChangeSet);
				}
			}
		}

		@Override
		public void setAdded(ChangeSet set) {
			// ignore
		}

		@Override
		public void defaultSetChanged(ChangeSet previousDefault, ChangeSet set) {
			// ignore
		}

		@Override
		public void nameChanged(ChangeSet set) {
			// ignore
		}

		@Override
		public void resourcesChanged(ChangeSet set, IPath[] paths) {
			// ignore
		}

	}

	public ContextActiveChangeSetManager() {
		Collection<AbstractActiveChangeSetProvider> providerList = FocusedTeamUiPlugin.getDefault()
				.getActiveChangeSetProviders();
		for (AbstractActiveChangeSetProvider provider : providerList) {
			ActiveChangeSetManager changeSetManager = provider.getActiveChangeSetManager();
			if (changeSetManager != null) {
				changeSetManagers.add(changeSetManager);
			}
		}
	}

	@Override
	protected void updateChangeSetLabel(ITask task) {
		for (ActiveChangeSetManager collector : changeSetManagers) {
			ChangeSet[] sets = collector.getSets();
			for (ChangeSet set : sets) {
				if (set instanceof IContextChangeSet contextChangeSet) {
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
		for (ActiveChangeSetManager collector : changeSetManagers) {
			ChangeSetChangeListener listener = listenerByManager.get(collector);
			if (listener == null) {
				listener = new ChangeSetChangeListener(collector);
				listenerByManager.put(collector, listener);
				collector.addListener(listener);
			}
		}
	}

	@Override
	public void disable() {
		super.disable();
		for (ActiveChangeSetManager collector : changeSetManagers) {
			ChangeSetChangeListener listener = listenerByManager.get(collector);
			if (listener != null) {
				collector.removeListener(listener);
				listenerByManager.remove(collector);
			}
		}
	}

	@Override
	protected void initContextChangeSets() {
		// replace existing change sets with IContextChangeSet
		for (ActiveChangeSetManager manager : changeSetManagers) {
			ChangeSet[] sets = manager.getSets();
			for (ChangeSet restoredSet : sets) {
				if (!(restoredSet instanceof IContextChangeSet)) {
					String encodedTitle = restoredSet.getName();
					String taskHandle = ContextChangeSet.getHandleFromPersistedTitle(encodedTitle);
					ITask task = TasksUi.getRepositoryModel().getTask(taskHandle);
					if (task != null) {
						try {
							IContextChangeSet contextChangeSet = getOrCreateSet(manager, task);
							if (contextChangeSet instanceof ActiveChangeSet) {
								contextChangeSet.restoreResources(restoredSet.getResources());
								manager.remove(restoredSet);
								manager.add((ActiveChangeSet) contextChangeSet);
							}
						} catch (Exception e) {
							StatusHandler.log(new Status(IStatus.ERROR, FocusedTeamUiPlugin.ID_PLUGIN,
									"Could not restore change set", e)); //$NON-NLS-1$
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
				if (set instanceof IContextChangeSet contextChangeSet) {
					if (contextChangeSet.getTask().equals(task) && contextChangeSet instanceof ActiveChangeSet) {
						return ((ActiveChangeSet) contextChangeSet).getResources();
					}
				}
			}
		}
		return null;
	}

	// TODO m4.0 remove deprecated method that is needed maintain backwards compatibility
	@SuppressWarnings({ "deprecation" })
	@Override
	public void contextActivated(IInteractionContext context) {
		try {
			ITask task = getTask(context);
			if (task != null) {
				for (ActiveChangeSetManager manager : changeSetManagers) {
					IContextChangeSet contextChangeSet = getOrCreateSet(manager, task);
					if (contextChangeSet instanceof ActiveChangeSet activeChangeSet) {
						//						List<IResource> interestingResources = ResourcesUiBridgePlugin.getDefault()
//								.getInterestingResources(context);
//						activeChangeSet.add(interestingResources.toArray(new IResource[interestingResources.size()]));
						activeChangeSets.add(contextChangeSet);

						// makeDefault() will add the change set
//						if (!manager.contains(activeChangeSet)) {
//							manager.add(activeChangeSet);
//						}
						manager.makeDefault(activeChangeSet);
					}
				}
			}
		} catch (Exception e) {
			StatusHandler
			.log(new Status(IStatus.ERROR, FocusedTeamUiPlugin.ID_PLUGIN, "Could not update change set", e)); //$NON-NLS-1$
		}
	}

	private IContextChangeSet getOrCreateSet(ActiveChangeSetManager manager, ITask task) {
		ChangeSet[] sets = manager.getSets();
		for (ChangeSet set : sets) {
			if (set instanceof IContextChangeSet && task.equals(((IContextChangeSet) set).getTask())) {
				return (IContextChangeSet) set;
			}
		}
		// change set does not exist, create a new one
		AbstractActiveChangeSetProvider provider = FocusedTeamUiPlugin.getDefault().getActiveChangeSetProvider(manager);
		return provider.createChangeSet(task);
	}

	// TODO m4.0 remove deprecated method that is needed maintain backwards compatibility
	@SuppressWarnings("deprecation")
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

			AbstractActiveChangeSetProvider changeSetProvider = FocusedTeamUiPlugin.getDefault()
					.getActiveChangeSetProvider(collector);

			changeSetProvider.activateDefaultChangeSet();
		}
		activeChangeSets.clear();
	}

	// TODO m4.0 remove deprecated method that is needed maintain backwards compatibility
	@SuppressWarnings("deprecation")
	@Override
	public void interestChanged(List<IInteractionElement> elements) {
		// disabled, see bug 162007
		//		for (IInteractionElement element : elements) {
//			AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(element.getContentType());
//			try {
//				if (bridge.isDocument(element.getHandleIdentifier())) {
//					IResource resource = ResourcesUiBridgePlugin.getDefault().getResourceForElement(element, false);
//					if (resource != null && resource.exists()) {
//						for (IContextChangeSet activeContextChangeSet : getActiveChangeSets()) {
//							if (activeContextChangeSet instanceof ActiveChangeSet) {
//								if (!((ActiveChangeSet) activeContextChangeSet).contains(resource)) {
//									if (element.getInterest().isInteresting()) {
//										((ActiveChangeSet) activeContextChangeSet).add(new IResource[] { resource });
//									}
//								}
//							}
//						}
//						if (shouldRemove(element)) {
//							for (ActiveChangeSetManager collector : changeSetManagers) {
//								ChangeSet[] sets = collector.getSets();
//								for (ChangeSet set : sets) {
//									if (set instanceof ActiveChangeSet) {
//										set.remove(resource);
//									}
//								}
//							}
//						}
//					}
//				}
//			} catch (Exception e) {
//				StatusHandler.log(new Status(IStatus.ERROR, FocusedTeamUiPlugin.ID_PLUGIN,
//						"Could not manipulate change set resources", e)); //$NON-NLS-1$
//			}
//		}
	}

	public List<IContextChangeSet> getActiveChangeSets() {
		return new ArrayList<>(activeChangeSets);
	}

	private ITask getTask(IInteractionContext context) {
		return TasksUi.getTaskActivityManager().getActiveTask();
	}

	/**
	 * Ignores decay.
	 */
//	private boolean shouldRemove(IInteractionElement element) {
//		// TODO: generalize this logic?
//		return (element.getInterest().getValue() + element.getInterest().getDecayValue()) < ContextCore.getCommonContextScaling()
//				.getInteresting();
//	}

}
