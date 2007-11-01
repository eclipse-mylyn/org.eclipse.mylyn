/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.ui.AbstractContextUiPlugin;
import org.eclipse.mylyn.internal.resources.ui.ContextEditorManager;
import org.eclipse.mylyn.internal.resources.ui.EditorInteractionMonitor;
import org.eclipse.mylyn.internal.resources.ui.ResourceChangeMonitor;
import org.eclipse.mylyn.internal.resources.ui.ResourceInteractionMonitor;
import org.eclipse.mylyn.internal.resources.ui.ResourceInterestUpdater;
import org.eclipse.mylyn.monitor.ui.MonitorUiPlugin;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;

/**
 * Main entry point for the Resource Structure Bridge. Initialization order is very important.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public class ResourcesUiBridgePlugin extends AbstractContextUiPlugin {

	public static final String PLUGIN_ID = "org.eclipse.mylyn.resources.ui";

	private static ResourcesUiBridgePlugin INSTANCE;

	private ResourceChangeMonitor resourceChangeMonitor;

	private ContextEditorManager editorManager;

	private ResourceInteractionMonitor resourceInteractionMonitor;

	private EditorInteractionMonitor interestEditorTracker;

	private ResourceInterestUpdater interestUpdater;

	private static final String PREF_STORE_DELIM = ", ";

	public static final String PREF_RESOURCES_IGNORED = "org.eclipse.mylyn.ide.resources.ignored.pattern";

	public static final String PREF_VAL_DEFAULT_RESOURCES_IGNORED = ".*" + PREF_STORE_DELIM;

	public ResourcesUiBridgePlugin() {
		super();
		INSTANCE = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		initPreferenceDefaults();
		interestUpdater = new ResourceInterestUpdater();
	}

	@Override
	protected void lazyStart(IWorkbench workbench) {
		resourceChangeMonitor = new ResourceChangeMonitor();
		editorManager = new ContextEditorManager();
		resourceInteractionMonitor = new ResourceInteractionMonitor();
		interestEditorTracker = new EditorInteractionMonitor();

		ContextCorePlugin.getContextManager().addListener(editorManager);
		MonitorUiPlugin.getDefault().getSelectionMonitors().add(resourceInteractionMonitor);

		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeMonitor,
				IResourceChangeEvent.POST_CHANGE);

		interestEditorTracker.install(PlatformUI.getWorkbench());
	}

	@Override
	protected void lazyStop() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceChangeMonitor);
		ContextCorePlugin.getContextManager().removeListener(editorManager);
		MonitorUiPlugin.getDefault().getSelectionMonitors().remove(resourceInteractionMonitor);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		INSTANCE = null;
	}

	private void initPreferenceDefaults() {
		getPreferenceStore().setDefault(PREF_RESOURCES_IGNORED, PREF_VAL_DEFAULT_RESOURCES_IGNORED);
	}

	public List<IResource> getInterestingResources(IInteractionContext context) {
		List<IResource> interestingResources = new ArrayList<IResource>();
		Collection<IInteractionElement> resourceElements = ContextCorePlugin.getContextManager()
				.getInterestingDocuments(context);
		for (IInteractionElement element : resourceElements) {
			IResource resource = getResourceForElement(element, false);
			if (resource != null) {
				interestingResources.add(resource);
			}
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

	public static ResourceInterestUpdater getInterestUpdater() {
		return INSTANCE.interestUpdater;
	}

	public IResource getResourceForElement(IInteractionElement element, boolean findContainingResource) {
		if (element == null)
			return null;
		AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(
				element.getContentType());
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
			if (element.getHandleIdentifier().equals(parentHandle)) {
				return null;
			} else {
				return getResourceForElement(ContextCorePlugin.getContextManager().getElement(parentHandle), true);
			}
		} else {
			return null;
		}
	}

	public void setResourceMonitoringEnabled(boolean enabled) {
		resourceChangeMonitor.setEnabled(enabled);
	}

	public static ContextEditorManager getEditorManager() {
		return INSTANCE.editorManager;
	}

	public static ResourcesUiBridgePlugin getDefault() {
		return INSTANCE;
	}

	@Deprecated
	public ResourceBundle getResourceBundle() {
		return null;
	}

}
