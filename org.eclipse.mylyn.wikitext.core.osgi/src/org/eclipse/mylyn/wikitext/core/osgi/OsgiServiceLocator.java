/*******************************************************************************
 * Copyright (c) 2013 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core.osgi;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import org.eclipse.mylyn.wikitext.core.util.ServiceLocator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * A {@link ServiceLocator} for use in an OSGi runtime environment. Uses OSGI {@link Bundle bundles} to load markup
 * languages using the {@link ServiceLoader Java service} defined by bundle resources defined by service files at the
 * path: {@code "META-INF/services/org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage"} .
 * 
 * @author david.green
 * @since 2.0
 */
public class OsgiServiceLocator extends ServiceLocator {

	private static final String SERVICES_SLASH = "services/"; //$NON-NLS-1$

	static class BundleResourceDescriptor extends ResourceDescriptor {

		private final Bundle bundle;

		public BundleResourceDescriptor(Bundle bundle, URL resourceUrl) {
			super(resourceUrl);
			this.bundle = bundle;
		}

	}

	/**
	 * Creates a new {@link OsgiServiceLocator}.
	 */
	public OsgiServiceLocator() {
		this(OsgiServiceLocator.class.getClassLoader());
	}

	/**
	 * Creates a new {@link OsgiServiceLocator}.
	 */
	public OsgiServiceLocator(ClassLoader classLoader) {
		super(classLoader);
	}

	/**
	 * Provides the {@link #isApplicable() applicable} service locator instance. Selects an {@link OsgiServiceLocator}
	 * if {{@link #isApplicable()} returns {@code true}, otherwise delegates to {@link ServiceLocator#getInstance()}.
	 * 
	 * @return the service locator.
	 */
	public static ServiceLocator getApplicableInstance() {
		if (isApplicable()) {
			return new OsgiServiceLocator();
		}
		return ServiceLocator.getInstance();
	}

	/**
	 * Indicates whether the OSGi service locator can be used.
	 */
	public static boolean isApplicable() {
		return FrameworkUtil.getBundle(OsgiServiceLocator.class) != null;
	}

	@Override
	protected List<ResourceDescriptor> discoverServiceResources() {
		Set<URL> resourceUrls = Sets.newHashSet();
		List<ResourceDescriptor> descriptors = Lists.newArrayList();
		for (Bundle bundle : bundles()) {
			for (String resourceName : getClasspathServiceResourceNames()) {
				int indexOf = resourceName.indexOf(SERVICES_SLASH);
				checkState(indexOf >= 0, resourceName);

				String path = resourceName.substring(0, indexOf + SERVICES_SLASH.length() - 1);
				String file = resourceName.substring(indexOf + SERVICES_SLASH.length());
				Enumeration<URL> resources = bundle.findEntries(path, file, false);
				if (resources == null) {
					// for running within Eclipse as a JUnit plug-in test or via self-hosted Eclipse instance
					resources = bundle.findEntries("bin/" + path, file, false); //$NON-NLS-1$
					if (resources == null) {
						continue;
					}
				}
				while (resources.hasMoreElements()) {
					URL resourceUrl = resources.nextElement();
					if (resourceUrls.add(resourceUrl)) {
						descriptors.add(new BundleResourceDescriptor(bundle, resourceUrl));
					}
				}
			}
		}
		return descriptors;
	}

	@Override
	protected Class<?> loadClass(ResourceDescriptor resource, String className) throws ClassNotFoundException {
		return ((BundleResourceDescriptor) resource).bundle.loadClass(className);
	}

	private static class SystemBundleFilter implements Predicate<Bundle> {
		public boolean apply(Bundle input) {
			return input.getBundleId() != 0L;
		}
	}

	private Iterable<Bundle> bundles() {
		Bundle[] bundles = getContext().getBundles();
		return FluentIterable.from(Arrays.asList(bundles)).filter(new SystemBundleFilter());
	}

	BundleContext getContext() {
		Bundle bundle = getBundle();
		ensureContext(bundle);
		return checkNotNull(bundle.getBundleContext(), "Bundle has no context"); //$NON-NLS-1$
	}

	protected void ensureContext(Bundle bundle) {
		try {
			bundle.start();
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	private Bundle getBundle() {
		return checkNotNull(FrameworkUtil.getBundle(OsgiServiceLocator.class), "Bundle is null."); //$NON-NLS-1$
	}
}
