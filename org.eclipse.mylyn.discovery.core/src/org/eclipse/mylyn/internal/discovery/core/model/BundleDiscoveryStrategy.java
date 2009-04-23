/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.discovery.core.model;

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.discovery.core.DiscoveryCore;
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
		IExtensionPoint extensionPoint = getExtensionRegistry().getExtensionPoint(
				ConnectorDiscoveryExtensionReader.EXTENSION_POINT_ID);
		IExtension[] extensions = extensionPoint.getExtensions();
		if (extensions.length > 0) {
			monitor.beginTask("Loading local extensions", extensions.length == 0 ? 1 : extensions.length);

			processExtensions(new SubProgressMonitor(monitor, extensions.length), extensions);
		}
		monitor.done();
	}

	protected void processExtensions(IProgressMonitor monitor, IExtension[] extensions) {
		monitor.beginTask("Processing extensions", extensions.length == 0 ? 1 : extensions.length);

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
						categories.add(category);
					} else {
						throw new ValidationException(MessageFormat.format("unexpected element ''{0}''",
								element.getName()));
					}
				} catch (ValidationException e) {
					StatusHandler.log(new Status(IStatus.ERROR, DiscoveryCore.BUNDLE_ID, MessageFormat.format(
							"{0}: {1}", element.getContributor().getName(), e.getMessage()), e));
				}
			}
			monitor.worked(1);
		}

		monitor.done();
	}

	protected AbstractDiscoverySource computeDiscoverySource(IContributor contributor) {
		return new BundleDiscoverySource(Platform.getBundle(contributor.getName()));
	}

	protected IExtensionRegistry getExtensionRegistry() {
		return Platform.getExtensionRegistry();
	}

}
