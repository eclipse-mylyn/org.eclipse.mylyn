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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.eclipse.core.resources.IContainer;
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
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.resources.ui.ResourcesUi;

/**
 * @author Mik Kersten
 */
public class ResourceChangeMonitor implements IResourceChangeListener {

	private static final String TRAILING_PATH_WILDCARD = "/**"; //$NON-NLS-1$

	private static final String LEADING_PATH_WILDCARD = "**/"; //$NON-NLS-1$

	private class ResourceDeltaVisitor implements IResourceDeltaVisitor {

		private final Set<IResource> addedResources;

		private final Set<IResource> changedResources;

		private final Set<String> excludedPatterns;

		private boolean haveTeamPrivateMember;

		public ResourceDeltaVisitor() {
			Set<String> excludedResourcePatterns = ResourcesUiPreferenceInitializer.getExcludedResourcePatterns();
			excludedResourcePatterns.addAll(ResourcesUiPreferenceInitializer.getForcedExcludedResourcePatterns());
			this.excludedPatterns = new HashSet<String>();
			for (String pattern : excludedResourcePatterns) {
				if (pattern != null && pattern.length() > 0) {
					this.excludedPatterns.add(pattern);
				}
			}
			this.addedResources = new HashSet<IResource>();
			this.changedResources = new HashSet<IResource>();
		}

		public boolean hasValidResult() {
			return !haveTeamPrivateMember;
		}

		public boolean visit(IResourceDelta delta) {
			if (haveTeamPrivateMember) {
				return false;
			}
			if (delta.getResource().isTeamPrivateMember()) {
				haveTeamPrivateMember = true;
				return false;
			}
			if (isExcluded(delta.getResource().getProjectRelativePath(), delta.getResource(), excludedPatterns)) {
				return false;
			}

			IResourceDelta[] added = delta.getAffectedChildren(IResourceDelta.ADDED);
			for (IResourceDelta element : added) {
				IResource resource = element.getResource();
				if ((resource instanceof IFile || resource instanceof IFolder)
						&& !isExcluded(resource.getProjectRelativePath(), resource, excludedPatterns)) {
					addedResources.add(resource);
				}
			}

			IResourceDelta[] changed = delta.getAffectedChildren(IResourceDelta.CHANGED | IResourceDelta.REMOVED);
			for (IResourceDelta element : changed) {
				IResource resource = element.getResource();
				// special rule for feature.xml files: bug 249856 
				if (resource instanceof IFile
						&& !isExcluded(resource.getProjectRelativePath(), resource, excludedPatterns)
						&& !"feature.xml".equals(resource.getName())) { //$NON-NLS-1$
					if (element.getKind() == IResourceDelta.CHANGED
							&& (element.getFlags() & IResourceDelta.CONTENT) == 0) {
						// make sure that there was a content change and not just a markers change
						continue;
					}
					changedResources.add(resource);
				}
			}
			return true;
		}

		public Set<IResource> getChangedResources() {
			return changedResources;
		}

		public Set<IResource> getAddedResources() {
			return addedResources;
		}

	};

	private boolean enabled;

	public ResourceChangeMonitor() {
		this.enabled = true;
	}

	public void resourceChanged(IResourceChangeEvent event) {
		if (!enabled || !ContextCore.getContextManager().isContextActive()) {
			return;
		}
		if (event.getType() != IResourceChangeEvent.POST_CHANGE) {
			return;
		}
		IResourceDelta rootDelta = event.getDelta();
		if (rootDelta != null) {
			ResourceDeltaVisitor visitor = new ResourceDeltaVisitor();
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

	/**
	 * Public for testing.
	 * 
	 * @param resource
	 *            can be null
	 */
	public static boolean isExcluded(IPath path, IResource resource, Set<String> excludedPatterns) {
		if (resource != null && resource.isDerived()) {
			return true;
		}
		String pathString = path.toPortableString();

		for (String pattern : excludedPatterns) {

			if (resource != null
					&& pattern.startsWith("file:/") && isUriExcluded(resource.getLocationURI().toString(), pattern)) { //$NON-NLS-1$
				return true;
			} else if (SelectorUtils.matchPath(pattern, pathString, false)
					|| SelectorUtils.match(pattern, pathString, false)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Public for testing.
	 */
	public static boolean isUriExcluded(String uri, String pattern) {
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

	public static Collection<String> convertToAntPattern(String basicPattern) {
		Set<String> patterns = new HashSet<String>();
		patterns.add(basicPattern);
		if (!basicPattern.contains(LEADING_PATH_WILDCARD) && !basicPattern.contains(TRAILING_PATH_WILDCARD)) {
			// we don't want to migrate patterns that are already ant-style paterns 
			patterns.add(LEADING_PATH_WILDCARD + basicPattern + TRAILING_PATH_WILDCARD);
			patterns.add(LEADING_PATH_WILDCARD + basicPattern);
			patterns.add(basicPattern + TRAILING_PATH_WILDCARD);
		}
		return patterns;
	}

}
