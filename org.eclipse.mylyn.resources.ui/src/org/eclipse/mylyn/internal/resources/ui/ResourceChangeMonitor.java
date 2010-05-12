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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.resources.ui.ResourcesUi;

/**
 * @author Mik Kersten
 */
public class ResourceChangeMonitor implements IResourceChangeListener {

	private class ResourceDeltaVisitor implements IResourceDeltaVisitor {

		private final Set<IResource> addedResources;

		private final Set<IResource> changedResources;

		private boolean haveTeamPrivateMember;

		private final List<IResourceExclusionStrategy> resourceExclusions;

		public ResourceDeltaVisitor(List<IResourceExclusionStrategy> resourceExclusions) {
			this.resourceExclusions = resourceExclusions;
			this.addedResources = new HashSet<IResource>();
			this.changedResources = new HashSet<IResource>();

			// make sure that the exclusions are updated 
			for (IResourceExclusionStrategy exclusion : exclusions) {
				exclusion.update();
			}
		}

		public boolean hasValidResult() {
			return !haveTeamPrivateMember;
		}

		public boolean visit(IResourceDelta delta) {

			IResource deltaResource = delta.getResource();
			if (deltaResource instanceof IProject
					&& (delta.getKind() == IResourceDelta.REMOVED || (delta.getFlags() & IResourceDelta.OPEN) != 0)) {
				// the project was either opened, closed or deleted, so lets ignore this so that we don't add every file to the context
				return false;
			}

			if (hasTeamPrivate(deltaResource)) {
				return false;
			}

			if (isExcluded(deltaResource)) {
				return false;
			}

			IResourceDelta[] added = delta.getAffectedChildren(IResourceDelta.ADDED);
			for (IResourceDelta element : added) {
				IResource resource = element.getResource();
				if ((resource instanceof IFile || resource instanceof IFolder) && !isExcluded(resource)) {

					if (hasTeamPrivate(resource)) {
						return false;
					}

					addedResources.add(resource);
				}
			}

			IResourceDelta[] changed = delta.getAffectedChildren(IResourceDelta.CHANGED | IResourceDelta.REMOVED);
			for (IResourceDelta element : changed) {
				IResource resource = element.getResource();
				// special rule for feature.xml files: bug 249856 
				if (resource instanceof IFile && !isExcluded(resource) && !"feature.xml".equals(resource.getName())) { //$NON-NLS-1$
					if (element.getKind() == IResourceDelta.CHANGED
							&& (element.getFlags() & IResourceDelta.CONTENT) == 0) {
						// make sure that there was a content change and not just a markers change
						continue;
					}

					if (hasTeamPrivate(resource)) {
						return false;
					}

					changedResources.add(resource);
				}
			}
			return true;
		}

		private boolean hasTeamPrivate(IResource resource) {
			if (haveTeamPrivateMember) {
				return true;
			}
			if (resource.isTeamPrivateMember()) {
				haveTeamPrivateMember = true;
				return true;
			}
			return false;
		}

		private boolean isExcluded(IResource resource) {
			for (IResourceExclusionStrategy exclusion : resourceExclusions) {
				if (exclusion.isExcluded(resource)) {
					return true;
				}
			}
			return false;
		}

		public Set<IResource> getChangedResources() {
			return changedResources;
		}

		public Set<IResource> getAddedResources() {
			return addedResources;
		}

	};

	private boolean enabled;

	private final List<IResourceExclusionStrategy> exclusions = new ArrayList<IResourceExclusionStrategy>();

	public ResourceChangeMonitor() {
		this.enabled = true;
		// ant based pattern exclusion
		exclusions.add(new ResourcePatternExclusionStrategy());
		// exclude resources not modified while task active
		exclusions.add(new ResourceModifiedDateExclusionStrategy());

		for (IResourceExclusionStrategy exclusion : exclusions) {
			exclusion.init();
		}
	}

	public void dispose() {
		for (IResourceExclusionStrategy exclusion : exclusions) {
			exclusion.dispose();
		}
		exclusions.clear();
	}

	// TODO investigate moving computation to the background?
	public void resourceChanged(IResourceChangeEvent event) {
		if (!enabled || !ContextCore.getContextManager().isContextActive()) {
			return;
		}
		if (event.getType() != IResourceChangeEvent.POST_CHANGE) {
			return;
		}
		IResourceDelta rootDelta = event.getDelta();
		if (rootDelta != null) {
			ResourceDeltaVisitor visitor = new ResourceDeltaVisitor(exclusions);
			try {
				rootDelta.accept(visitor, IContainer.INCLUDE_TEAM_PRIVATE_MEMBERS | IContainer.INCLUDE_HIDDEN);
				if (visitor.hasValidResult()) {
					ResourcesUi.addResourceToContext(visitor.getChangedResources(), InteractionEvent.Kind.PREDICTION);
					ResourcesUi.addResourceToContext(visitor.getAddedResources(), InteractionEvent.Kind.PROPAGATION);
				}
			} catch (CoreException e) {
				StatusHandler.log(new Status(IStatus.ERROR, ResourcesUiBridgePlugin.ID_PLUGIN,
						"Could not accept marker visitor", e)); //$NON-NLS-1$
			}
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
