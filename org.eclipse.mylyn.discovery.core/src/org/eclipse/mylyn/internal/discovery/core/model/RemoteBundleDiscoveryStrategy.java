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
 * A discovery strategy that downloads a simple directory of remote jars. The directory is first downloaded, then each
 * remote jar is downloaded.
 * 
 * @author David Green
 */
public class RemoteBundleDiscoveryStrategy extends BundleDiscoveryStrategy {

	private String directoryUrl;

	private DiscoveryRegistryStrategy registryStrategy;

	private File temporaryStorage;

	@Override
	public void performDiscovery(IProgressMonitor monitor) throws CoreException {
		if (connectors == null || categories == null || directoryUrl == null) {
			throw new IllegalStateException();
		}
		if (registryStrategy != null) {
			throw new IllegalStateException();
		}

		final int totalTicks = 100000;
		final int ticksTenPercent = totalTicks / 10;
		monitor.beginTask("remote discovery", totalTicks);

		File registryCacheFolder;
		try {
			if (temporaryStorage != null && temporaryStorage.exists()) {
				delete(temporaryStorage);
			}
			temporaryStorage = File.createTempFile(RemoteBundleDiscoveryStrategy.class.getSimpleName(), ".tmp"); //$NON-NLS-1$
			temporaryStorage.delete();
			if (!temporaryStorage.mkdirs()) {
				throw new IOException();
			}
			registryCacheFolder = new File(temporaryStorage, ".rcache"); //$NON-NLS-1$
			if (!registryCacheFolder.mkdirs()) {
				throw new IOException();
			}
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, DiscoveryCore.BUNDLE_ID,
					"IO failure: cannot create temporary storage area", e));
		}
		if (monitor.isCanceled()) {
			return;
		}

		Directory directory;

		// FIXME: Eclipse network/proxy settings
		WebLocation webLocation = new WebLocation(directoryUrl);
		try {
			final Directory[] temp = new Directory[1];
			WebUtil.readResource(webLocation, new TextContentProcessor() {
				public void process(Reader reader) throws IOException {
					DirectoryParser parser = new DirectoryParser();
					temp[0] = parser.parse(reader);
				}
			}, new SubProgressMonitor(monitor, ticksTenPercent));
			directory = temp[0];
			if (directory == null) {
				throw new IllegalStateException();
			}
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, DiscoveryCore.BUNDLE_ID,
					"IO failure: cannot load discovery directory", e));
		}
		if (monitor.isCanceled()) {
			return;
		}
		if (directory.getEntries().isEmpty()) {
			throw new CoreException(new Status(IStatus.ERROR, DiscoveryCore.BUNDLE_ID, "Discovery directory is empty"));
		}

		Set<File> bundles = new HashSet<File>();

		// TODO: multithreaded downloading
		for (Directory.Entry entry : directory.getEntries()) {
			String bundleUrl = entry.getLocation();
			try {
				if (!bundleUrl.startsWith("http://") && !bundleUrl.startsWith("https://")) { //$NON-NLS-1$ //$NON-NLS-2$
					StatusHandler.log(new Status(IStatus.WARNING, DiscoveryCore.BUNDLE_ID, MessageFormat.format(
							"Unrecognized discovery bundle URL: {0}", bundleUrl)));
					continue;
				}
				String lastPathElement = bundleUrl.lastIndexOf('/') == -1 ? bundleUrl
						: bundleUrl.substring(bundleUrl.lastIndexOf('/'));
				File target = File.createTempFile(
						lastPathElement.replaceAll("^[a-zA-Z0-9_.]", "_") + "_", ".jar", temporaryStorage); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$

				if (monitor.isCanceled()) {
					return;
				}

				// FIXME: Eclipse network/proxy settings
				WebUtil.downloadResource(target, new WebLocation(bundleUrl), new SubProgressMonitor(monitor,
						ticksTenPercent * 4 / directory.getEntries().size()));
				bundles.add(target);
			} catch (IOException e) {
				StatusHandler.log(new Status(IStatus.ERROR, DiscoveryCore.BUNDLE_ID, MessageFormat.format(
						"Cannot download bundle at {0}: {1}", bundleUrl, e.getMessage()), e));
			}
		}
		try {
			registryStrategy = new DiscoveryRegistryStrategy(new File[] { registryCacheFolder },
					new boolean[] { false }, this);
			registryStrategy.setBundleFiles(bundles);
			IExtensionRegistry extensionRegistry = new ExtensionRegistry(registryStrategy, this, this);
			try {
				IExtensionPoint extensionPoint = extensionRegistry.getExtensionPoint(ConnectorDiscoveryExtensionReader.EXTENSION_POINT_ID);
				if (extensionPoint != null) {
					IExtension[] extensions = extensionPoint.getExtensions();
					if (extensions.length > 0) {
						monitor.beginTask("Loading remote extensions", extensions.length == 0 ? 1 : extensions.length);

						processExtensions(new SubProgressMonitor(monitor, ticksTenPercent * 3), extensions);
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

	private void delete(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] children = file.listFiles();
				if (children != null) {
					for (File child : children) {
						delete(child);
					}
				}
			}
			if (!file.delete()) {
				// fail quietly
			}
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		if (temporaryStorage != null) {
			delete(temporaryStorage);
		}
	}

	public String getDirectoryUrl() {
		return directoryUrl;
	}

	public void setDirectoryUrl(String directoryUrl) {
		this.directoryUrl = directoryUrl;
	}

	@Override
	protected AbstractDiscoverySource computeDiscoverySource(IContributor contributor) {
		return new JarDiscoverySource(contributor.getName(), registryStrategy.getJarFile(contributor));
	}
}
