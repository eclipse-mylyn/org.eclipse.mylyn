/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Manuel Doninger - improvements for bug 364155
 *******************************************************************************/

package org.eclipse.mylyn.resources.ui;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiBridgePlugin;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 * @author Manuel Doninger
 * @author Steffen Pingel
 * @since 3.0
 */
public final class ResourcesUi {

	public static void addResourceToContext(final Set<IResource> resources,
			final InteractionEvent.Kind interactionKind) {
		ResourcesUiBridgePlugin.getInterestUpdater().addResourceToContext(resources, interactionKind);
	}

	/**
	 * Returns all projects that are referenced in <code>context</code>.
	 * 
	 * @since 3.8
	 */
	public static Set<IProject> getProjects(IInteractionContext context) {
		List<IInteractionElement> allElements = context.getAllElements();
		Set<IProject> projectsInContext = new HashSet<>();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		List<IProject> projectsInWorkspace = new LinkedList<>();

		for (IProject p : workspace.getRoot().getProjects()) {
			if (p.exists()) {
				projectsInWorkspace.add(p);
			}
		}

		for (IInteractionElement element : allElements) {
			String handle = element.getHandleIdentifier();
			IPath path = new Path(handle);

			if (path.segmentCount() == 1 && path.isValidPath(handle)) {
				String projectName = handle.substring(1);
				IProject project = workspace.getRoot().getProject(projectName);
				if (projectsInWorkspace.contains(project)) {
					projectsInContext.add(project);
				}
			}
		}
		return projectsInContext;
	}

}
