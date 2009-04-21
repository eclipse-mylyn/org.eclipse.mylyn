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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.internal.registry.ExtensionRegistry;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.internal.discovery.core.DiscoveryCore;
import org.eclipse.mylyn.internal.discovery.core.util.WebUtil;
import org.eclipse.mylyn.internal.discovery.core.util.WebUtil.TextContentProcessor;

/**
 * A discovery strategy that downloads a simple directory of remote jars. The
 * directory is first downloaded, then each remote jar is downloaded.
 * 
 * @author David Green
 */
public class RemoteBundleDiscoveryStrategy extends BundleDiscoveryStrategy {

	private String directoryUrl;
	private DiscoveryRegistryStrategy registryStrategy;

	@Override
	public void performDiscovery(IProgressMonitor monitor) throws CoreException {
		if (connectors == null || categories == null || directoryUrl == null) {
			throw new IllegalStateException();
		}
		if (registryStrategy != null) {
			throw new IllegalStateException();
		}

		final int totalTicks = 10000;
		final int ticksTenPercent = totalTicks / 10;
		monitor.beginTask("remote discovery", totalTicks);

		File tempFolder;
		File registryCacheFolder;
		try {
			tempFolder = File
					.createTempFile(RemoteBundleDiscoveryStrategy.class
							.getSimpleName(), ".tmp"); //$NON-NLS-1$
			tempFolder.delete();
			if (!tempFolder.mkdirs()) {
				throw new IOException();
			}
			registryCacheFolder = new File(tempFolder, ".registry-cache"); //$NON-NLS-1$
			if (!registryCacheFolder.mkdirs()) {
				throw new IOException();
			}
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					DiscoveryCore.BUNDLE_ID,
					"IO failure: cannot create temporary storage area", e));
		}
		if (monitor.isCanceled()) {
			return;
		}

		final Set<String> bundleUrls = new HashSet<String>();

		// FIXME: Eclipse network/proxy settings
		WebLocation webLocation = new WebLocation(directoryUrl);
		try {
			WebUtil.readResource(webLocation, new TextContentProcessor() {
				public void process(Reader reader) throws IOException {
					BufferedReader lineReader = new BufferedReader(reader);
					String line;
					while ((line = lineReader.readLine()) != null) {
						line = line.trim();
						if (line.length() > 0 && line.charAt(0) != '#') {
							bundleUrls.add(line);
						}
					}
				}
			}, new SubProgressMonitor(monitor, ticksTenPercent));
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					DiscoveryCore.BUNDLE_ID,
					"IO failure: cannot load discovery directory", e));
		}
		if (monitor.isCanceled()) {
			return;
		}
		if (bundleUrls.isEmpty()) {
			throw new CoreException(new Status(IStatus.ERROR,
					DiscoveryCore.BUNDLE_ID, "Discovery directory is empty"));
		}

		Set<File> bundles = new HashSet<File>();

		// TODO: multithreaded downloading
		for (String bundleUrl : bundleUrls) {
			try {
				if (!bundleUrl.startsWith("http://") && !bundleUrl.startsWith("https://")) { //$NON-NLS-1$ //$NON-NLS-2$
					StatusHandler.log(new Status(IStatus.WARNING,
							DiscoveryCore.BUNDLE_ID, MessageFormat.format(
									"Unrecognized discovery bundle URL: {0}",
									bundleUrl)));
				}
				String lastPathElement = bundleUrl.lastIndexOf('/') == -1 ? bundleUrl
						: bundleUrl.substring(bundleUrl.lastIndexOf('/'));
				File target = File.createTempFile(lastPathElement.replaceAll(
						"^[a-zA-Z0-9_.]", "_") + "_", ".jar", tempFolder); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$

				if (monitor.isCanceled()) {
					return;
				}

				// FIXME: Eclipse network/proxy settings
				WebUtil.downloadResource(target, new WebLocation(bundleUrl),
						monitor);
				bundles.add(target);
			} catch (IOException e) {
				StatusHandler.log(new Status(IStatus.ERROR,
						DiscoveryCore.BUNDLE_ID, MessageFormat.format(
								"Cannot download bundle at {0}: {1}",
								bundleUrl, e.getMessage()), e));
			}
		}
		try {
			registryStrategy = new DiscoveryRegistryStrategy(
					new File[] { registryCacheFolder },
					new boolean[] { false }, this);
			registryStrategy.setBundleFiles(bundles);
			IExtensionRegistry extensionRegistry = new ExtensionRegistry(
					registryStrategy, this, this);
			try {
				IExtensionPoint extensionPoint = extensionRegistry
						.getExtensionPoint(ConnectorDiscoveryExtensionReader.EXTENSION_POINT_ID);
				if (extensionPoint != null) {
					IExtension[] extensions = extensionPoint.getExtensions();
					if (extensions.length > 0) {
						monitor.beginTask("Loading remote extensions",
								extensions.length == 0 ? 1 : extensions.length);

						processExtensions(new SubProgressMonitor(monitor,
								ticksTenPercent * 3), extensions);
					}
				}
			} finally {
				extensionRegistry.stop(this);
			}
		} finally {
			registryStrategy = null;
		}
		monitor.done();
	}

	public String getDirectoryUrl() {
		return directoryUrl;
	}

	public void setDirectoryUrl(String directoryUrl) {
		this.directoryUrl = directoryUrl;
	}

	@Override
	protected AbstractDiscoverySource computeDiscoverySource(
			IContributor contributor) {
		return new JarDiscoverySource(contributor.getName(), registryStrategy
				.getJarFile(contributor));
	}
}
