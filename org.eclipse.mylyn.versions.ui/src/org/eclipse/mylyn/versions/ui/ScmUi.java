/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Alvaro Sanchez-Leon - Extended to include resolution of ui connectors
 *******************************************************************************/

package org.eclipse.mylyn.versions.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.compare.CompareUI;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.versions.ui.spi.ScmUiConnector;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.internal.core.history.LocalFileRevision;
import org.eclipse.team.internal.ui.history.CompareFileRevisionEditorInput;
import org.eclipse.team.internal.ui.history.FileRevisionTypedElement;
import org.eclipse.team.ui.synchronize.SaveableCompareEditorInput;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Steffen Pingel
 */
public class ScmUi {
	private static HashMap<String, ScmUiConnector> connectorById = new HashMap<String, ScmUiConnector>();

	private static final String ID_PLUGIN = "org.eclipse.mylyn.versions.ui"; //$NON-NLS-1$

	private static class EmptyTypedElement implements ITypedElement {

		private final String name;

		public EmptyTypedElement(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public Image getImage() {
			return null;
		}

		public String getType() {
			return ITypedElement.UNKNOWN_TYPE;
		}

	}

	protected static ITypedElement getElementFor(IResource resource) {
		return SaveableCompareEditorInput.createFileElement((IFile) resource);
	}

	private static IResource getResource(IFileRevision revision) {
		if (revision instanceof LocalFileRevision) {
			LocalFileRevision local = (LocalFileRevision) revision;
			return local.getFile();
		}
		return null;
	}

	/**
	 * @param page
	 * @param file1
	 * @param file2
	 */
	public static void openCompareEditor(IWorkbenchPage page, IFileRevision file1, IFileRevision file2) {
		Assert.isNotNull(file2);

		IResource resource = getResource(file2);
		if (resource != null) {
			IFileRevision temp = file1;
			file1 = file2;
			file2 = temp;
		}
		ITypedElement left;
		if (file1 != null) {
			resource = getResource(file1);
			if (resource != null) {
				left = getElementFor(resource);
			} else {
				left = new FileRevisionTypedElement(file1, null);
			}
		} else {
			left = new EmptyTypedElement(file2.getName());
		}
		ITypedElement right = new FileRevisionTypedElement(file2, null);

		CompareFileRevisionEditorInput input = new CompareFileRevisionEditorInput(left, right, page);
		CompareUI.openCompareEditor(input, OpenStrategy.activateOnOpen());
	}

	/**
	 * @return
	 */
	public static List<ScmUiConnector> getAllRegisteredUiConnectors() {
		List<ScmUiConnector> scmUiConnectors = new ArrayList<ScmUiConnector>();
		String[] teamProviderIds = RepositoryProvider.getAllProviderTypeIds();
		for (String providerId : teamProviderIds) {
			ScmUiConnector connector = getScmUiConnectorById(providerId);
			if (connector != null) {
				scmUiConnectors.add(connector);
			}
		}
		return scmUiConnectors;
	}

	/**
	 * @param resource
	 * @return
	 */
	public static ScmUiConnector getUiConnector(IResource resource) {
		if (!RepositoryProvider.isShared(resource.getProject())) {
			return null;
		}

		RepositoryProvider provider = RepositoryProvider.getProvider(resource.getProject());
		return getScmUiConnectorById(provider.getID());
	}

	private synchronized static ScmUiConnector getScmUiConnectorById(String id) {
		ScmUiConnector connector = connectorById.get(id);
		if (connector == null) {
			connector = loadConnector(id);
			connectorById.put(id, connector);
		}
		return connector;
	}

	private static ScmUiConnector loadConnector(String id) {
		Assert.isNotNull(id);
		MultiStatus result = new MultiStatus(ScmUi.ID_PLUGIN, 0, "Scm ui connectors failed to load.", null); //$NON-NLS-1$

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint connectorsExtensionPoint = registry.getExtensionPoint(ScmUi.ID_PLUGIN + ".connectors"); //$NON-NLS-1$
		IExtension[] extensions = connectorsExtensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (id.equals(element.getAttribute("id"))) { //$NON-NLS-1$
					try {
						Object object = element.createExecutableExtension("ui"); //$NON-NLS-1$
						if (object instanceof ScmUiConnector) {
							return (ScmUiConnector) object;
						} else {
							result.add(new Status(
									IStatus.ERROR,
									ScmUi.ID_PLUGIN,
									NLS.bind(
											"Connector ui ''{0}'' does not extend expected class for extension contributed by {1}", //$NON-NLS-1$
											object.getClass().getCanonicalName(), element.getContributor().getName())));
						}
					} catch (Throwable e) {
						result.add(new Status(
								IStatus.ERROR,
								ScmUi.ID_PLUGIN,
								NLS.bind(
										"Connector core failed to load for extension contributed by {0}", element.getContributor().getName()), e)); //$NON-NLS-1$
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
