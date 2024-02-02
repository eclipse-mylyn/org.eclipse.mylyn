/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.versions.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.versions.core.ScmInternal;
import org.eclipse.mylyn.versions.core.spi.ScmConnector;
import org.eclipse.osgi.util.NLS;
import org.eclipse.team.core.RepositoryProvider;

/**
 * @author Steffen Pingel
 */
public class ScmCore {

	private static HashMap<String, ScmConnector> connectorById = new HashMap<>();

	public static IResource findResource(String file) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IPath path = new Path(file);
		path.makeRelative();
		IResource resource = findResource(root, path);
		if (resource == null) {
			for (IProject project : root.getProjects()) {
				resource = project.findMember(path);
				if (resource != null) {
					break;
				}
			}
		}
		return resource;
	}

	private static IResource findResource(IWorkspaceRoot root, IPath path) {
		while (path.segmentCount() > 1) {
			IResource resource = root.findMember(path);
			if (resource != null) {
				return resource;
			}
			path = path.removeFirstSegments(1);
		}
		return null;
	}

	public static List<ScmConnector> getAllRegisteredConnectors() {
		List<ScmConnector> scmConnectors = new ArrayList<>();
		String[] teamProviderIds = RepositoryProvider.getAllProviderTypeIds();
		for (String providerId : teamProviderIds) {
			ScmConnector connector = getScmConnectorById(providerId);
			if (connector != null) {
				scmConnectors.add(connector);
			}
		}
		return scmConnectors;
	}

	public static ScmConnector getConnector(IResource resource) {
		if (!RepositoryProvider.isShared(resource.getProject())) {
			return null;
		}

		RepositoryProvider provider = RepositoryProvider.getProvider(resource.getProject());
		return getScmConnectorById(provider.getID());
	}

	private synchronized static ScmConnector getScmConnectorById(String id) {
		ScmConnector connector = connectorById.get(id);
		if (connector == null) {
			connector = loadConnector(id);
			connectorById.put(id, connector);
		}
		return connector;
	}

	private static ScmConnector loadConnector(String id) {
		Assert.isNotNull(id);
		MultiStatus result = new MultiStatus(ScmInternal.ID_PLUGIN, 0, "Scm connectors failed to load.", null); //$NON-NLS-1$

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint connectorsExtensionPoint = registry.getExtensionPoint(ScmInternal.ID_PLUGIN + ".connectors"); //$NON-NLS-1$
		IExtension[] extensions = connectorsExtensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (id.equals(element.getAttribute("id"))) { //$NON-NLS-1$
					try {
						Object object = element.createExecutableExtension("core"); //$NON-NLS-1$
						if (object instanceof ScmConnector) {
							return (ScmConnector) object;
						} else {
							result.add(new Status(IStatus.ERROR, ScmInternal.ID_PLUGIN, NLS.bind(
									"Connector core ''{0}'' does not extend expected class for extension contributed by {1}", //$NON-NLS-1$
									object.getClass().getCanonicalName(), element.getContributor().getName())));
						}
					} catch (Throwable e) {
						result.add(new Status(IStatus.ERROR, ScmInternal.ID_PLUGIN,
								NLS.bind("Connector core failed to load for extension contributed by {0}", //$NON-NLS-1$
										element.getContributor().getName()),
								e));
					}
				}
			}
		}

		if (!result.isOK()) {
			StatusHandler.log(result);
		}

		return null;
	}

}
