/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.ide.team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.MylarContextManager;
import org.eclipse.mylar.core.util.ErrorLogger;
import org.eclipse.mylar.ide.MylarIdePlugin;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskActivityListener;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.team.internal.ccvs.ui.CVSUIPlugin;
import org.eclipse.team.internal.core.subscribers.ChangeSet;
import org.eclipse.team.internal.core.subscribers.IChangeSetChangeListener;
import org.eclipse.team.internal.core.subscribers.SubscriberChangeSetCollector;

/**
 * @author Mik Kersten
 */
public class MylarChangeSetManager implements IMylarContextListener {

	private final IChangeSetChangeListener CHANGE_SET_LISTENER = new IChangeSetChangeListener() {
		public void setRemoved(ChangeSet set) {
			if (set instanceof MylarContextChangeSet) {
				MylarContextChangeSet contextChangeSet = (MylarContextChangeSet)set;
				if (contextChangeSet.getTask().isActive()) {
					collector.add(contextChangeSet); // put it back
				}
			}
		}

		public void setAdded(ChangeSet set) {
			// TODO Auto-generated method stub
			
		}

		public void defaultSetChanged(ChangeSet previousDefault, ChangeSet set) {
			// TODO Auto-generated method stub
			
		}

		public void nameChanged(ChangeSet set) {
			// TODO Auto-generated method stub
			
		}

		public void resourcesChanged(ChangeSet set, IResource[] resources) {
			// TODO Auto-generated method stub
			
		}
	};

	private SubscriberChangeSetCollector collector;
	
	private Map<String, MylarContextChangeSet> activeChangeSets = new HashMap<String, MylarContextChangeSet>();
		
	private ITaskActivityListener TASK_ACTIVITY_LISTENER = new ITaskActivityListener() {

		public void tasklistRead() {
			initContextChangeSets();
		}
		
		public void taskChanged(ITask task) {
			ChangeSet[] sets = collector.getSets();
			for (int i = 0; i < sets.length; i++) {
				ChangeSet set = sets[i];
				if (set instanceof MylarContextChangeSet) {
					MylarContextChangeSet contextChangeSet = (MylarContextChangeSet)set;
					if (contextChangeSet.getTask().equals(task)) {
						contextChangeSet.initTitle();
					}
				}
			}  
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

		public void tasklistModified() {
			// TODO Auto-generated method stub
			
		}
	};
	
	private boolean isEnabled = false;
	
	public MylarChangeSetManager() {
		collector = CVSUIPlugin.getPlugin().getChangeSetManager();
	}

	public void enable() {
		if (!isEnabled) {
			MylarPlugin.getContextManager().addListener(this);
			MylarTaskListPlugin.getTaskListManager().addListener(TASK_ACTIVITY_LISTENER); 
			if (MylarTaskListPlugin.getTaskListManager().isTaskListRead()) {
				initContextChangeSets(); // otherwise listener will do it
			}  
			collector.addListener(CHANGE_SET_LISTENER);
			isEnabled = true;
		}
	}
	
	public void disable() {
		MylarPlugin.getContextManager().removeListener(this);
		MylarTaskListPlugin.getTaskListManager().removeListener(TASK_ACTIVITY_LISTENER); 
		collector.removeListener(CHANGE_SET_LISTENER);
		isEnabled = false;
	}
	
	private void initContextChangeSets() {
		ChangeSet[] sets = collector.getSets();
		for (int i = 0; i < sets.length; i++) {
			ChangeSet restoredSet = sets[i];
			if (!(restoredSet instanceof MylarContextChangeSet)) {
				String encodedTitle = restoredSet.getName();
				String taskHandle = MylarContextChangeSet.getHandleFromPersistedTitle(encodedTitle);
				ITask task = MylarTaskListPlugin.getTaskListManager().getTaskForHandle(taskHandle, true);
				if (task != null) {				
					try {
						MylarContextChangeSet contextChangeSet = new MylarContextChangeSet(task, collector);
						contextChangeSet.restoreResources(restoredSet.getResources());
						collector.remove(restoredSet);
						
//						activeChangeSets.put(task.getHandleIdentifier(), contextChangeSet);
						collector.add(contextChangeSet);
					} catch (Exception e) {
						ErrorLogger.fail(e, "could not restore change set", false);
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
		ChangeSet[] sets = collector.getSets();
		for (int i = 0; i < sets.length; i++) {
			ChangeSet set = sets[i];
			if (set instanceof MylarContextChangeSet) {
				MylarContextChangeSet contextChangeSet = (MylarContextChangeSet)set;
				if (contextChangeSet.getTask().equals(task)) {
					return contextChangeSet.getResources();
				}
			}
		}  
		return null;
	}
	
	public void contextActivated(IMylarContext context) {
		try {
			ITask task = getTask(context); 
			if (task == null) {
				// ignore
//				ErrorLogger.log("could not resolve task for context", this);
			} else if (!activeChangeSets.containsKey(task.getHandleIdentifier())) { 
				MylarContextChangeSet contextChangeSet = new MylarContextChangeSet(task, collector);
				List<IResource> interestingResources = MylarIdePlugin.getDefault().getInterestingResources();
				contextChangeSet.add(interestingResources.toArray(new IResource[interestingResources.size()]));
				
				activeChangeSets.put(task.getHandleIdentifier(), contextChangeSet);
				if (!collector.contains(contextChangeSet)) collector.add(contextChangeSet);
			}
		} catch (Exception e) {
			ErrorLogger.fail(e, "could not update change set", false);
		}
	}

	public void contextDeactivated(IMylarContext context) {
		ChangeSet[] sets = collector.getSets();
		for (int i = 0; i < sets.length; i++) {
			ChangeSet set = sets[i];
			if (set instanceof MylarContextChangeSet) {
				IResource[] resources = set.getResources();
				if (resources == null || resources.length == 0) {
					collector.remove(set);			
				}				
			}
		}  
		activeChangeSets.clear();
	}

	public List<MylarContextChangeSet> getActiveChangeSets() {
		return new ArrayList<MylarContextChangeSet>(activeChangeSets.values());
	}
	
	private ITask getTask(IMylarContext context) {
		List<ITask> activeTasks = MylarTaskListPlugin.getTaskListManager().getTaskList().getActiveTasks();
		
		// TODO: support multiple tasks
		if (activeTasks.size() > 0) {
			return activeTasks.get(0);
		} else {
			return null;
		}
	}
	
	public void interestChanged(IMylarElement element) {
		IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(element.getContentType());
		try { 
			if (bridge.isDocument(element.getHandleIdentifier())) {
				IResource resource = MylarIdePlugin.getDefault().getResourceForElement(element);
				if (resource != null && resource.exists()) {
					for (MylarContextChangeSet activeContextChangeSet: getActiveChangeSets()) {
						if (!activeContextChangeSet.contains(resource)) {
							if (element.getInterest().isInteresting()) {
								activeContextChangeSet.add(new IResource[] { resource });
							} 
						} 
					}
					if (shouldRemove(element)) {
						ChangeSet[] sets = collector.getSets();
						for (int i = 0; i < sets.length; i++) {
							if (sets[i] instanceof MylarContextChangeSet) {
								sets[i].remove(resource);				
							}
						}
					}
				}
			}
		} catch (Exception e) {
			ErrorLogger.fail(e, "could not manipulate change set resources", false);
		}
	}

//	private void touch(final IResource resource) throws CoreException {
//		final IWorkbench workbench = PlatformUI.getWorkbench();
//		workbench.getDisplay().asyncExec(new Runnable() {
//			public void run() {
//				if (resource instanceof IFile) {
//					try {
//						((IFile)resource).touch(new NullProgressMonitor());
//					} catch (CoreException e) {
//						MylarPlugin.fail(e, "failed to touch resource: " + resource, false);
//					}
//				}
//			}
//		});
//	}

	/**
	 * Ignores decay.
	 */
	private boolean shouldRemove(IMylarElement element) {
		// TODO: generalize this logic?
		return (element.getInterest().getValue() + element.getInterest().getDecayValue()) 
			< MylarContextManager.getScalingFactors().getInteresting();
	}
	
	public void interestChanged(List<IMylarElement> elements) {
		for (IMylarElement element : elements) {
			interestChanged(element);
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
