/*******************************************************************************
 * Copyright (c) 2009, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.discovery.core.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.discovery.core.DiscoveryCore;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;

/**
 * a strategy for discovering via installed platform {@link Bundle bundles}.
 * 
 * @author David Green
 */
public class BundleDiscoveryStrategy extends AbstractDiscoveryStrategy {

	@Override
	public void performDiscovery(IProgressMonitor monitor) throws CoreException {
		if (connectors == null || categories == null) {
			throw new IllegalStateException();
		}
		IExtensionPoint extensionPoint = getExtensionRegistry()
				.getExtensionPoint(ConnectorDiscoveryExtensionReader.EXTENSION_POINT_ID);
		IExtension[] extensions = extensionPoint.getExtensions();
		monitor.beginTask(Messages.BundleDiscoveryStrategy_task_loading_local_extensions,
				extensions.length == 0 ? 1 : extensions.length);
		try {
			if (extensions.length > 0) {
				processExtensions(new SubProgressMonitor(monitor, extensions.length), extensions);
			}
		} finally {
			monitor.done();
		}
	}

	protected void processExtensions(IProgressMonitor monitor, IExtension[] extensions) {
		monitor.beginTask(Messages.BundleDiscoveryStrategy_task_processing_extensions,
				extensions.length == 0 ? 1 : extensions.length);
		try {
			ConnectorDiscoveryExtensionReader extensionReader = new ConnectorDiscoveryExtensionReader();

			for (IExtension extension : extensions) {
				AbstractDiscoverySource discoverySource = computeDiscoverySource(extension.getContributor());
				IConfigurationElement[] elements = extension.getConfigurationElements();
				for (IConfigurationElement element : elements) {
					if (monitor.isCanceled()) {
						return;
					}
					try {
						if (ConnectorDiscoveryExtensionReader.CONNECTOR_DESCRIPTOR.equals(element.getName())) {
							DiscoveryConnector descriptor = extensionReader.readConnectorDescriptor(element,
									DiscoveryConnector.class);
							descriptor.setSource(discoverySource);
							connectors.add(descriptor);
						} else if (ConnectorDiscoveryExtensionReader.CONNECTOR_CATEGORY.equals(element.getName())) {
							DiscoveryCategory category = extensionReader.readConnectorCategory(element,
									DiscoveryCategory.class);
							category.setSource(discoverySource);
							if (!discoverySource.getPolicy().isPermitCategories()) {
								StatusHandler.log(new Status(IStatus.ERROR, DiscoveryCore.ID_PLUGIN,
										NLS.bind(Messages.BundleDiscoveryStrategy_categoryDisallowed,
												new Object[] { category.getName(), category.getId(),
														element.getContributor().getName() }),
										null));
							} else {
								categories.add(category);
							}
						} else if (ConnectorDiscoveryExtensionReader.CERTIFICATION.equals(element.getName())) {
							DiscoveryCertification certification = extensionReader.readCertification(element,
									DiscoveryCertification.class);
							certification.setSource(discoverySource);
							certifications.add(certification);
						} else {
							throw new ValidationException(
									NLS.bind(Messages.BundleDiscoveryStrategy_unexpected_element, element.getName()));
						}
					} catch (ValidationException e) {
						StatusHandler.log(new Status(IStatus.ERROR, DiscoveryCore.ID_PLUGIN,
								NLS.bind(Messages.BundleDiscoveryStrategy_3, element.getContributor().getName(),
										e.getMessage()),
								e));
					}
				}
				monitor.worked(1);
			}
		} finally {
			monitor.done();
		}
	}

	protected AbstractDiscoverySource computeDiscoverySource(IContributor contributor) {
		Policy policy = new Policy(true);
		BundleDiscoverySource bundleDiscoverySource = new BundleDiscoverySource(
				Platform.getBundle(contributor.getName()));
		bundleDiscoverySource.setPolicy(policy);
		return bundleDiscoverySource;
	}

	protected IExtensionRegistry getExtensionRegistry() {
		return Platform.getExtensionRegistry();
	}

}
