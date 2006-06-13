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
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.provisional.core.InteractionEvent;
import org.eclipse.mylar.provisional.core.MylarPlugin;

/**
 * @author Mik Kersten
 */
public class ResourceChangeMonitor implements IResourceChangeListener {

	private boolean enabled = true;

	public void resourceChanged(IResourceChangeEvent event) { 
		if (!enabled || !MylarPlugin.getContextManager().isContextActive()) {
			return;
		}
		if (event.getType() != IResourceChangeEvent.POST_CHANGE) {
			return;
		}
		final Set<IResource> addedResources = new HashSet<IResource>();
		final Set<IResource> changedResources = new HashSet<IResource>();
		IResourceDelta rootDelta = event.getDelta();
		IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {
			public boolean visit(IResourceDelta delta) {
				IResourceDelta[] added = delta.getAffectedChildren(IResourceDelta.ADDED);
				for (int i = 0; i < added.length; i++) {
					IResource resource = added[i].getResource();
					if ((resource instanceof IFile || resource instanceof IFolder) && !isExcluded(resource.getProjectRelativePath())) {
						addedResources.add(resource);
					}
				}
//				int changeMask = IResourceDelta.CONTENT | IResourceDelta.REMOVED | IResourceDelta.MOVED_TO | IResourceDelta.MOVED_FROM;
				IResourceDelta[] changed = delta.getAffectedChildren(IResourceDelta.CHANGED);
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
			MylarIdePlugin.getDefault().getInterestUpdater().addResourceToContext(changedResources, InteractionEvent.Kind.PREDICTION);
			MylarIdePlugin.getDefault().getInterestUpdater().addResourceToContext(addedResources, InteractionEvent.Kind.SELECTION);	
		} catch (CoreException e) {
			MylarStatusHandler.log(e, "could not accept marker visitor");
		}
	}

	private boolean isExcluded(IPath path) {
		if (path == null) {
			return false;
		}
		// NOTE: n^2 time complexity, but should not be a bottleneck
		for (String pattern : MylarIdePlugin.getDefault().getExcludedResourcePatterns()) {
			for (String segment : path.segments()) {
				return segment.matches(pattern.replaceAll("\\.", "\\\\.").replaceAll("\\*", ".*"));		
			}
		}
		return false;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
