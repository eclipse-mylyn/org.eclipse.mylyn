/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.resources.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.ui.IContextUiStartup;
import org.eclipse.mylyn.monitor.ui.MonitorUi;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Main entry point for the Resource Structure Bridge. Initialization order is very important.
 * 
 * @author Mik Kersten
 * @author Steffen Pingel
 * @since 3.0
 */
public class ResourcesUiBridgePlugin extends AbstractUIPlugin {

	/**
	 * @since 3.0
	 */
	public static class ResourcesUiBridgeStartup implements IContextUiStartup {

		public void lazyStartup() {
			ResourcesUiBridgePlugin.getDefault().lazyStart();
		}

	}

	public static final String ID_PLUGIN = "org.eclipse.mylyn.resources.ui"; //$NON-NLS-1$

	private static ResourcesUiBridgePlugin INSTANCE;

	private ResourceChangeMonitor resourceChangeMonitor;

	private ResourceInteractionMonitor resourceInteractionMonitor;

	private EditorInteractionMonitor interestEditorTracker;

	private ResourceInterestUpdater interestUpdater;

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
		interestUpdater = new ResourceInterestUpdater();
	}

	/**
	 * @since 3.0
	 */
	protected void lazyStart() {
		resourceChangeMonitor = new ResourceChangeMonitor();
		resourceInteractionMonitor = new ResourceInteractionMonitor();
		interestEditorTracker = new EditorInteractionMonitor();

		MonitorUi.getSelectionMonitors().add(resourceInteractionMonitor);

		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeMonitor,
				IResourceChangeEvent.POST_CHANGE);

		interestEditorTracker.install(PlatformUI.getWorkbench());
	}

	protected void lazyStop() {
		if (resourceChangeMonitor != null) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceChangeMonitor);
		}
		if (resourceInteractionMonitor != null) {
			MonitorUi.getSelectionMonitors().remove(resourceInteractionMonitor);
		}
		if (interestEditorTracker != null) {
			interestEditorTracker.dispose(PlatformUI.getWorkbench());
		}
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		lazyStop();

		super.stop(context);
		INSTANCE = null;
	}

	public List<IResource> getInterestingResources(IInteractionContext context) {
		List<IResource> interestingResources = new ArrayList<IResource>();
		Collection<IInteractionElement> resourceElements = ContextCore.getContextManager().getActiveDocuments(context);
		for (IInteractionElement element : resourceElements) {
			IResource resource = getResourceForElement(element, false);
			if (resource != null) {
				interestingResources.add(resource);
			}
		}
		return interestingResources;
	}

	public static ResourceInterestUpdater getInterestUpdater() {
		return INSTANCE.interestUpdater;
	}

	public IResource getResourceForElement(IInteractionElement element, boolean findContainingResource) {
		if (element == null) {
			return null;
		}
		AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(element.getContentType());
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
				return getResourceForElement(ContextCore.getContextManager().getElement(parentHandle), true);
			}
		} else {
			return null;
		}
	}

	public void setResourceMonitoringEnabled(boolean enabled) {
		resourceChangeMonitor.setEnabled(enabled);
	}

	public static ResourcesUiBridgePlugin getDefault() {
		return INSTANCE;
	}

	@Deprecated
	public ResourceBundle getResourceBundle() {
		return null;
	}

}
