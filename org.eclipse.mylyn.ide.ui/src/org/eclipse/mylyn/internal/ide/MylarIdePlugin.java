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
package org.eclipse.mylar.internal.ide;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.ide.team.MylarChangeSetManager;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.core.IMylarStructureBridge;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 */
public class MylarIdePlugin extends AbstractUIPlugin {

	private MylarEditorManager editorManager = new MylarEditorManager();

	private ResourceInteractionMonitor resourceInteractionMonitor;

	private static MylarIdePlugin plugin;

	private ActiveSearchViewTracker activeSearchViewTracker = new ActiveSearchViewTracker();

	private EditorInteractionMonitor interestEditorTracker = new EditorInteractionMonitor();

	private ResourceChangeMonitor resourceChangeMonitor = new ResourceChangeMonitor();

	private MylarChangeSetManager changeSetManager;

	private ResourceInterestUpdater interestUpdater = new ResourceInterestUpdater();

	public static final String CHANGE_SET_MANAGE = "org.eclipse.mylar.team.changesets.manage";

	public static final String COMMIT_PREFIX_COMPLETED = "org.eclipse.mylar.team.commit.prefix.completed";

	public static final String COMMIT_PREFIX_PROGRESS = "org.eclipse.mylar.team.commit.prefix.progress";

	public static final String DEFAULT_PREFIX_PROGRESS = "Progress on:";

	public static final String DEFAULT_PREFIX_COMPLETED = "Completed:";

	private static final String PREF_STORE_DELIM = ", ";

//	public static final String PREF_RESOURCE_MONITORING_ENABLED = "org.eclipse.mylar.ide.resources.monitoring.enabled";
	
	public static final String PREF_RESOURCES_IGNORED = "org.eclipse.mylar.ide.resources.ignored.pattern";

	public static final String PREF_VAL_DEFAULT_RESOURCES_IGNORED = ".*" + PREF_STORE_DELIM;
	
	public MylarIdePlugin() {
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		initPreferenceDefaults();

		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					changeSetManager = new MylarChangeSetManager();
					if (getPreferenceStore().getBoolean(CHANGE_SET_MANAGE)) {
						changeSetManager.enable();
					}
					// MylarPlugin.getContextManager().addListener(navigatorRefreshListener);

					resourceInteractionMonitor = new ResourceInteractionMonitor();
					// Display.getDefault().addFilter(SWT.Selection,
					// resourceInteractionMonitor);

					MylarPlugin.getDefault().getSelectionMonitors().add(resourceInteractionMonitor);
					MylarPlugin.getContextManager().addListener(editorManager);

					ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeMonitor,
							IResourceChangeEvent.POST_CHANGE);

					// if (ApplyMylarToNavigatorAction.getDefault() != null)
					// ApplyMylarToNavigatorAction.getDefault().update();
					// if (ApplyMylarToProblemsListAction.getDefault() != null)
					// ApplyMylarToProblemsListAction.getDefault().update();

					workbench.addWindowListener(activeSearchViewTracker);
					IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
					for (int i = 0; i < windows.length; i++) {
						windows[i].addPageListener(activeSearchViewTracker);
						IWorkbenchPage[] pages = windows[i].getPages();
						for (int j = 0; j < pages.length; j++) {
							pages[j].addPartListener(activeSearchViewTracker);
						}
					}

					interestEditorTracker.install(workbench);
					// workbench.addWindowListener(interestEditorTracker);
					// for (int i = 0; i < windows.length; i++) {
					// windows[i].addPageListener(interestEditorTracker);
					// IWorkbenchPage[] pages = windows[i].getPages();
					// for (int j = 0; j < pages.length; j++) {
					// pages[j].addPartListener(interestEditorTracker);
					// }
					// }
				} catch (Exception e) {
					MylarStatusHandler.fail(e, "Mylar IDE initialization failed", false);
				}
			}
		});
	}

	private void initPreferenceDefaults() {
		getPreferenceStore().setDefault(CHANGE_SET_MANAGE, true);
		getPreferenceStore().setDefault(COMMIT_PREFIX_COMPLETED, DEFAULT_PREFIX_COMPLETED);
		getPreferenceStore().setDefault(COMMIT_PREFIX_PROGRESS, DEFAULT_PREFIX_PROGRESS);
		getPreferenceStore().setDefault(PREF_RESOURCES_IGNORED, PREF_VAL_DEFAULT_RESOURCES_IGNORED);

		// restore old preference values if set
		if (MylarTaskListPlugin.getDefault() != null) {
			if (MylarTaskListPlugin.getMylarCorePrefs().contains(COMMIT_PREFIX_COMPLETED)) {
				getPreferenceStore().setValue(COMMIT_PREFIX_COMPLETED,
						MylarTaskListPlugin.getMylarCorePrefs().getString(COMMIT_PREFIX_COMPLETED));
			}
			if (MylarTaskListPlugin.getMylarCorePrefs().contains(COMMIT_PREFIX_PROGRESS)) {
				getPreferenceStore().setValue(COMMIT_PREFIX_PROGRESS,
						MylarTaskListPlugin.getMylarCorePrefs().getString(COMMIT_PREFIX_PROGRESS));
			}
		}
	}

	public void stop(BundleContext context) throws Exception {
		try {
			super.stop(context);
			plugin = null;
			MylarPlugin.getContextManager().removeListener(editorManager);
			MylarPlugin.getDefault().getSelectionMonitors().remove(resourceInteractionMonitor);
			// MylarPlugin.getContextManager().removeListener(navigatorRefreshListener);
			changeSetManager.disable();

			ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceChangeMonitor);
			IWorkbench workbench = PlatformUI.getWorkbench();
			if (workbench != null) {
				workbench.removeWindowListener(activeSearchViewTracker);
				IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
				for (int i = 0; i < windows.length; i++) {
					IWorkbenchPage[] pages = windows[i].getPages();
					windows[i].removePageListener(activeSearchViewTracker);
					for (int j = 0; j < pages.length; j++) {
						pages[j].removePartListener(activeSearchViewTracker);
					}
				}
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e,
					"Mylar IDE stop failed, Mylar may not have started properly (ensure correct Eclipse version)",
					false);
		}
	}

	public void setResourceMonitoringEnabled(boolean enabled) {
		resourceChangeMonitor.setEnabled(enabled);
	}

	public static MylarIdePlugin getDefault() {
		return plugin;
	}

	public MylarChangeSetManager getChangeSetManager() {
		return changeSetManager;
	}

	public List<IResource> getInterestingResources() {
		List<IResource> interestingResources = new ArrayList<IResource>();
		List<IMylarElement> resourceElements = MylarPlugin.getContextManager().getInterestingDocuments();
		for (IMylarElement element : resourceElements) {
			IResource resource = getResourceForElement(element, false);
			if (resource != null)
				interestingResources.add(resource);
		}
		return interestingResources;
	}

	public void setExcludedResourcePatterns(Set<String> patterns) {
		StringBuilder store = new StringBuilder();
		for (String string : patterns) {
			store.append(string);
			store.append(PREF_STORE_DELIM);
		}
		getPreferenceStore().setValue(PREF_RESOURCES_IGNORED, store.toString());
	}

	public Set<String> getExcludedResourcePatterns() {
		Set<String> ignored = new HashSet<String>();
		String read = getPreferenceStore().getString(PREF_RESOURCES_IGNORED);
		if (read != null) {
			StringTokenizer st = new StringTokenizer(read, PREF_STORE_DELIM);
			while (st.hasMoreTokens()) {
				ignored.add(st.nextToken());
			}
		}
		return ignored;
	}

	public IResource getResourceForElement(IMylarElement element, boolean findContainingResource) {
		if (element == null)
			return null;
		IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(element.getContentType());
		Object object = bridge.getObjectForHandle(element.getHandleIdentifier());
		if (object instanceof IResource) {
			return (IResource) object;
		} else if (object instanceof IAdaptable) {
			Object adapted = ((IAdaptable) object).getAdapter(IResource.class);
			if (adapted instanceof IResource) {
				return (IResource) adapted;
			}
		}
		if (findContainingResource) { // recurse if not found
			String parentHandle = bridge.getParentHandle(element.getHandleIdentifier());
			return getResourceForElement(MylarPlugin.getContextManager().getElement(parentHandle), true);
		} else {
			return null;
		}
	}

	public MylarEditorManager getEditorManager() {
		return editorManager;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.mylar.internal.ide", path);
	}

	public ResourceInterestUpdater getInterestUpdater() {
		return interestUpdater;
	}
}
