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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
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
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;

/**
 * 
 * @author David Green
 */
public class DiscoveryRegistryStrategy extends RegistryStrategy {

	private Set<File> bundleFiles = new HashSet<File>();

	private final List<JarFile> jars = new ArrayList<JarFile>();

	private final Map<IContributor, File> contributorToJarFile = new HashMap<IContributor, File>();

	private final Object token;

	public DiscoveryRegistryStrategy(File[] storageDirs, boolean[] cacheReadOnly, Object token) {
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
			IContributor contributor = new RegistryContributor(bundle.getSymbolicName(), bundle.getSymbolicName(),
					null, null);

			InputStream inputStream = bundle.getEntry("plugin.xml").openStream(); //$NON-NLS-1$
			try {
				registry.addContribution(inputStream, contributor, false, bundle.getSymbolicName(), null, token);
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
				StatusHandler.log(new Status(IStatus.ERROR, DiscoveryCore.BUNDLE_ID, NLS.bind(
						Messages.DiscoveryRegistryStrategy_cannot_load_bundle, bundleFile.getName(), e.getMessage()), e));
			}
		}
	}

	private void processBundle(IExtensionRegistry registry, File bundleFile) throws IOException {
		JarFile jarFile = new JarFile(bundleFile);
		jars.add(jarFile);

		ZipEntry pluginXmlEntry = jarFile.getEntry("plugin.xml"); //$NON-NLS-1$
		if (pluginXmlEntry == null) {
			throw new IOException(Messages.DiscoveryRegistryStrategy_missing_pluginxml);
		}
		IContributor contributor = new RegistryContributor(bundleFile.getName(), bundleFile.getName(), null, null);
		if (((IDynamicExtensionRegistry) registry).hasContributor(contributor)) {
			jarFile.close();
			return;
		}
		contributorToJarFile.put(contributor, bundleFile);

		ResourceBundle translationBundle = loadTranslationBundle(jarFile);

		InputStream inputStream = jarFile.getInputStream(pluginXmlEntry);
		try {
			registry.addContribution(inputStream, contributor, false, bundleFile.getPath(), translationBundle, token);
		} finally {
			inputStream.close();
		}
	}

	private ResourceBundle loadTranslationBundle(JarFile jarFile) throws IOException {
		List<String> bundleNames = computeBundleNames("plugin"); //$NON-NLS-1$
		for (String bundleName : bundleNames) {
			ZipEntry entry = jarFile.getEntry(bundleName);
			if (entry != null) {
				InputStream inputStream = jarFile.getInputStream(entry);
				try {
					PropertyResourceBundle resourceBundle = new PropertyResourceBundle(inputStream);
					return resourceBundle;
				} finally {
					inputStream.close();
				}
			}
		}
		return null;
	}

	private List<String> computeBundleNames(String baseName) {
		String suffix = ".properties"; //$NON-NLS-1$
		String name = baseName;
		List<String> bundleNames = new ArrayList<String>();
		Locale locale = Locale.getDefault();
		bundleNames.add(name + suffix);
		if (locale.getLanguage() != null && locale.getLanguage().length() > 0) {
			name = name + '_' + locale.getLanguage();
			bundleNames.add(0, name + suffix);
		}
		if (locale.getCountry() != null && locale.getCountry().length() > 0) {
			name = name + '_' + locale.getCountry();
			bundleNames.add(0, name + suffix);
		}
		if (locale.getVariant() != null && locale.getVariant().length() > 0) {
			name = name + '_' + locale.getVariant();
			bundleNames.add(0, name + suffix);
		}
		return bundleNames;
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
