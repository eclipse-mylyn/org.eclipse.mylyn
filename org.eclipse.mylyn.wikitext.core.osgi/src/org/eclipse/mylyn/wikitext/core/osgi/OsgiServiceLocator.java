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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.text.MessageFormat.format;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.util.ServiceLocator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
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

	/**
	 * Creates a new {@link OsgiServiceLocator}.
	 */
	public OsgiServiceLocator() {
		this(OsgiServiceLocator.class.getClassLoader());
	}

	public OsgiServiceLocator(ClassLoader classLoader) {
		super(classLoader);
	}

	/**
	 * Indicates whether the OSGi service locator can be used.
	 */
	public static boolean isApplicable() {
		return FrameworkUtil.getBundle(OsgiServiceLocator.class) != null;
	}

	@Override
	public MarkupLanguage getMarkupLanguage(String languageName) throws IllegalArgumentException {
		checkArgument(!Strings.isNullOrEmpty(languageName), "Must provide a languageName"); //$NON-NLS-1$

		List<String> names = Lists.newArrayList();
		Set<MarkupLanguage> allMarkupLanguages = getAllMarkupLanguages();
		for (MarkupLanguage language : allMarkupLanguages) {
			if (language.getName().equals(languageName) || language.getClass().getName().equals(languageName)) {
				return language;
			}
			names.add(language.getName());
		}
		Collections.sort(names);
		String languages = Joiner.on(Messages.getString("OsgiServiceLocator.3")).join(names); //$NON-NLS-1$
		String message = format(Messages.getString("OsgiServiceLocator.4"), languageName, //$NON-NLS-1$
				languages);
		throw new IllegalArgumentException(message);
	}

	@Override
	public Set<MarkupLanguage> getAllMarkupLanguages() {
		Set<MarkupLanguage> allLanguages = Sets.newHashSet();

		final String serviceResourceName = "META-INF/services/" + MarkupLanguage.class.getName(); //$NON-NLS-1$

		for (Bundle bundle : bundles()) {
			Enumeration<URL> resources = null;
			try {
				resources = bundle.getResources(serviceResourceName);
			} catch (IOException e) {
				log(format(Messages.getString("OsgiServiceLocator.0"), bundle.getSymbolicName(), e.getMessage()), e); //$NON-NLS-1$
			}
			if (resources == null) {
				continue;
			}
			while (resources.hasMoreElements()) {
				URL resourceUrl = resources.nextElement();
				for (String serviceClass : readServiceClassNames(resourceUrl)) {
					Class<?> clazz;
					try {
						clazz = bundle.loadClass(serviceClass);
					} catch (ClassNotFoundException e1) {
						log(format(Messages.getString("OsgiServiceLocator.2"), serviceClass), e1); //$NON-NLS-1$
						continue;
					}
					if (MarkupLanguage.class.isAssignableFrom(clazz)) {
						try {
							allLanguages.add((MarkupLanguage) clazz.newInstance());
						} catch (Exception e) {
							log(format(Messages.getString("OsgiServiceLocator.1"), e.getMessage()), e); //$NON-NLS-1$
						}
					}
				}
			}
		}
		return ImmutableSet.copyOf(allLanguages);
	}

	private void log(String message, Throwable t) {
		Logger.getLogger(OsgiServiceLocator.class.getName()).log(Level.SEVERE, message, t);
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
