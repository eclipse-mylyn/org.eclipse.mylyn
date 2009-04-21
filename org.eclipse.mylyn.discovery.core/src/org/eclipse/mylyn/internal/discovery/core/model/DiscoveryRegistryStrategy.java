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
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.spi.IDynamicExtensionRegistry;
import org.eclipse.core.runtime.spi.RegistryContributor;
import org.eclipse.core.runtime.spi.RegistryStrategy;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.discovery.core.DiscoveryCore;
import org.osgi.framework.Bundle;

/**
 * 
 * @author David Green
 */
public class DiscoveryRegistryStrategy extends RegistryStrategy {

	private Set<File> bundleFiles = new HashSet<File>();

	private List<JarFile> jars = new ArrayList<JarFile>();

	private Map<IContributor, File> contributorToJarFile = new HashMap<IContributor, File>();

	private final Object token;

	public DiscoveryRegistryStrategy(File[] storageDirs,
			boolean[] cacheReadOnly, Object token) {
		super(storageDirs, cacheReadOnly);
		this.token = token;
	}

	@Override
	public void onStart(IExtensionRegistry registry, boolean loadedFromCache) {
		super.onStart(registry, loadedFromCache);
		if (!loadedFromCache) {
			processDiscoveryCoreBundle(registry);
			processBundles(registry);
		}
	}

	private void processDiscoveryCoreBundle(IExtensionRegistry registry) {
		// we must add a contribution from the core bundle so that we get the
		// extension point itself
		try {
			Bundle bundle = Platform.getBundle(DiscoveryCore.BUNDLE_ID);
			IContributor contributor = new RegistryContributor(bundle
					.getSymbolicName(), bundle.getSymbolicName(), null, null);

			InputStream inputStream = bundle
					.getEntry("plugin.xml").openStream(); //$NON-NLS-1$
			try {
				registry.addContribution(inputStream, contributor, false,
						bundle.getSymbolicName(), null, token);
			} finally {
				inputStream.close();
			}
		} catch (IOException e) {
			throw new IllegalStateException();
		}
	}

	public Set<File> getBundleFiles() {
		return bundleFiles;
	}

	public void setBundleFiles(Set<File> bundleFiles) {
		this.bundleFiles = bundleFiles;
	}

	private void processBundles(IExtensionRegistry registry) {
		if (bundleFiles == null) {
			throw new IllegalStateException();
		}
		for (File bundleFile : bundleFiles) {
			try {
				processBundle(registry, bundleFile);
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.ERROR,
						DiscoveryCore.BUNDLE_ID, MessageFormat.format(
								"Cannot load bundle {0}: {1}", bundleFile
										.getName(), e.getMessage()), e));
			}
		}
	}

	private void processBundle(IExtensionRegistry registry, File bundleFile)
			throws IOException {
		JarFile jarFile = new JarFile(bundleFile);
		jars.add(jarFile);

		ZipEntry pluginXmlEntry = jarFile.getEntry("plugin.xml"); //$NON-NLS-1$
		if (pluginXmlEntry == null) {
			throw new IOException("no plugin.xml in bundle");
		}
		IContributor contributor = new RegistryContributor(
				bundleFile.getName(), bundleFile.getName(), null, null);
		if (((IDynamicExtensionRegistry) registry).hasContributor(contributor)) {
			jarFile.close();
			return;
		}
		contributorToJarFile.put(contributor, bundleFile);

		InputStream inputStream = jarFile.getInputStream(pluginXmlEntry);
		try {
			registry.addContribution(inputStream, contributor, false,
					bundleFile.getPath(), null, token);
		} finally {
			inputStream.close();
		}
	}

	@Override
	public void onStop(IExtensionRegistry registry) {
		try {
			super.onStop(registry);
		} finally {
			for (JarFile jar : jars) {
				try {
					jar.close();
				} catch (Exception e) {
				}
			}
			jars.clear();
		}
	}

	/**
	 * get the jar file that corresponds to the given contributor.
	 * 
	 * @throws IllegalArgumentException
	 *             if the given contributor is unknown
	 */
	public File getJarFile(IContributor contributor) {
		File file = contributorToJarFile.get(contributor);
		if (file == null) {
			throw new IllegalArgumentException(contributor.getName());
		}
		return file;
	}
}
