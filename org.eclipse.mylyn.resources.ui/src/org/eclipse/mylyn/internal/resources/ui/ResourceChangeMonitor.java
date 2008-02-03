/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.resources.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.resources.ResourcesUiBridgePlugin;

/**
 * @author Mik Kersten
 */
public class ResourceChangeMonitor implements IResourceChangeListener {

	private boolean enabled = true;

	public void resourceChanged(IResourceChangeEvent event) {
		if (!enabled || !ContextCorePlugin.getContextManager().isContextActive()) {
			return;
		}
		if (event.getType() != IResourceChangeEvent.POST_CHANGE) {
			return;
		}
		final Set<IResource> addedResources = new HashSet<IResource>();
		final Set<IResource> changedResources = new HashSet<IResource>();
		final Set<String> excludedPatterns = ResourcesUiPreferenceInitializer.getExcludedResourcePatterns();
		excludedPatterns.addAll(ResourcesUiPreferenceInitializer.getForcedExcludedResourcePatterns());
		IResourceDelta rootDelta = event.getDelta();
		IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {
			public boolean visit(IResourceDelta delta) {
				IResourceDelta[] added = delta.getAffectedChildren(IResourceDelta.ADDED);
				for (int i = 0; i < added.length; i++) {
					IResource resource = added[i].getResource();
					if ((resource instanceof IFile || resource instanceof IFolder)
							&& !isExcluded(resource.getProjectRelativePath(), resource, excludedPatterns)) {
						addedResources.add(resource);
					}
				}
//				int changeMask = IResourceDelta.CONTENT | IResourceDelta.REMOVED | IResourceDelta.MOVED_TO | IResourceDelta.MOVED_FROM;
				IResourceDelta[] changed = delta.getAffectedChildren(IResourceDelta.CHANGED | IResourceDelta.REMOVED);
				for (int i = 0; i < changed.length; i++) {
					IResource resource = changed[i].getResource();
					if (resource instanceof IFile) {
						changedResources.add(resource);
					}
				}
				return true;
			}
		};
		try {
			rootDelta.accept(visitor);
			ResourcesUiBridgePlugin.getInterestUpdater().addResourceToContext(changedResources,
					InteractionEvent.Kind.PREDICTION);
			ResourcesUiBridgePlugin.getInterestUpdater().addResourceToContext(addedResources,
					InteractionEvent.Kind.SELECTION);
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ResourcesUiBridgePlugin.PLUGIN_ID,
					"Could not accept marker visitor", e));
		}
	}

	/**
	 * Public for testing.
	 * 
	 * @param resource can be null
	 */
	public boolean isExcluded(IPath path, IResource resource, Set<String> excludedPatterns) {
		boolean excluded = false;
		// NOTE: n^2 time complexity, but should not be a bottleneck
		for (String pattern : excludedPatterns) {
			if (resource != null && pattern.startsWith("file:/")) {
				excluded |= isUriExcluded(resource.getLocationURI().toString(), pattern);
			} else {
				for (String segment : path.segments()) {
					excluded |= segment.matches(pattern.replaceAll("\\.", "\\\\.").replaceAll("\\*", ".*"));
				}
			}
			
			if (excluded) {
				break;
			}
		}
		return excluded;
	}

	/**
	 * Public for testing.
	 */
	public boolean isUriExcluded(String uri, String pattern) {
		if (uri != null && uri.startsWith(pattern)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
