/*******************************************************************************
 * Copyright (c) 2007, 2013 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguageProvider;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.Closeables;

/**
 * A service locator for use both inside and outside of an Eclipse environment. Provides access to markup languages by
 * name.
 * <p>
 * Markup languages may be dynamically discovered by adding a Java service file in one of the following locations:
 * <ul>
 * <li><tt>META-INF/services/org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage</tt></li>
 * <li><tt>services/org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage</tt></li>
 * <li><tt>META-INF/services/org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguageProvider</tt></li>
 * <li><tt>services/org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguageProvider</tt></li>
 * </ul>
 * </p>
 * 
 * @author David Green
 * @since 1.0
 * @see MarkupLanguage
 * @see MarkupLanguageProvider
 */
public class ServiceLocator {

	protected final ClassLoader classLoader;

	private static Object implementationClassLock = new Object();

	private static Class<? extends ServiceLocator> implementationClass;

	private static Pattern CLASS_NAME_PATTERN = Pattern.compile("\\s*([^\\s#]+)?#?.*"); //$NON-NLS-1$

	protected ServiceLocator(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * Get an instance of the service locator
	 * 
	 * @param classLoader
	 *            the class loader to use when looking up services
	 * @see #getInstance()
	 */
	public static ServiceLocator getInstance(ClassLoader classLoader) {
		synchronized (implementationClassLock) {
			if (implementationClass != null) {
				try {
					return implementationClass.getConstructor(ClassLoader.class).newInstance(classLoader);
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}
		}
		return new ServiceLocator(classLoader);
	}

	/**
	 * Get an instance of the service locator
	 * 
	 * @see #getInstance(ClassLoader)
	 */
	public static ServiceLocator getInstance() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			ServiceLocator.class.getClassLoader();
		}
		return getInstance(loader);
	}

	/**
	 * get a markup language by name
	 * 
	 * @param languageName
	 *            the {@link MarkupLanguage#getName() name} of the markup language, or the fully qualified name of the
	 *            class that implements the language
	 * @return the language implementation
	 * @throws IllegalArgumentException
	 *             if the provided language name is null or if no implementation is available for the given language
	 */
	public MarkupLanguage getMarkupLanguage(final String languageName) throws IllegalArgumentException {
		checkArgument(!Strings.isNullOrEmpty(languageName), "Must provide a languageName"); //$NON-NLS-1$
		Pattern classNamePattern = Pattern.compile("\\s*([^\\s#]+)?#?.*"); //$NON-NLS-1$
		// first try Java services (jar-based)
		final List<String> names = Lists.newArrayList();
		final List<MarkupLanguage> languages = Lists.newArrayList();

		final MarkupLanguage[] result = new MarkupLanguage[1];

		loadMarkupLanguages(new MarkupLanguageVisitor() {

			public boolean accept(MarkupLanguage language) {
				if (languageName.equals(language.getName())) {
					result[0] = language;
					return false;
				}
				languages.add(language);
				names.add(language.getName());
				return true;
			}
		});
		if (result[0] != null) {
			return result[0];
		}

		// next attempt to load the markup language as if the language name is a fully qualified name
		Matcher matcher = classNamePattern.matcher(languageName);
		if (matcher.matches()) {
			String className = matcher.group(1);
			if (className != null) {
				// first try to load from a discovered markup language since this will circumvent
				//  classloader issues
				for (MarkupLanguage language : languages) {
					if (className.equals(language.getClass().getName())) {
						return language;
					}
				}
				try {
					Class<?> clazz = Class.forName(className, true, classLoader);
					if (MarkupLanguage.class.isAssignableFrom(clazz)) {
						MarkupLanguage instance = (MarkupLanguage) clazz.newInstance();
						return instance;
					}
				} catch (Exception e) {
					// ignore
				}
			}
		}

		Collections.sort(names);

		// specified language not found.
		// create a useful error message
		StringBuilder buf = new StringBuilder();
		for (String name : names) {
			if (buf.length() != 0) {
				buf.append(", "); //$NON-NLS-1$
			}
			buf.append('\'');
			buf.append(name);
			buf.append('\'');
		}
		throw new IllegalArgumentException(MessageFormat.format(Messages.getString("ServiceLocator.4"), //$NON-NLS-1$
				languageName, buf.length() == 0 ? Messages.getString("ServiceLocator.5") //$NON-NLS-1$
						: Messages.getString("ServiceLocator.6") + buf)); //$NON-NLS-1$
	}

	/**
	 * Get all known markup languages
	 * 
	 * @since 1.6
	 */
	public Set<MarkupLanguage> getAllMarkupLanguages() {
		final Set<MarkupLanguage> markupLanguages = new HashSet<MarkupLanguage>();
		loadMarkupLanguages(new MarkupLanguageVisitor() {

			public boolean accept(MarkupLanguage language) {
				markupLanguages.add(language);
				return true;
			}
		});
		return markupLanguages;
	}

	public static void setImplementation(Class<? extends ServiceLocator> implementationClass) {
		synchronized (implementationClassLock) {
			ServiceLocator.implementationClass = implementationClass;
		}
	}

	private interface MarkupLanguageVisitor {
		public boolean accept(MarkupLanguage language);
	}

	private void loadMarkupLanguages(MarkupLanguageVisitor visitor) {
		for (ResourceDescriptor descriptor : discoverServiceResources()) {
			List<String> classNames = readServiceClassNames(descriptor.getUrl());
			for (String className : classNames) {
				try {
					Class<?> clazz = loadClass(descriptor, className);
					if (MarkupLanguage.class.isAssignableFrom(clazz)) {
						MarkupLanguage instance = (MarkupLanguage) clazz.newInstance();
						if (!visitor.accept(instance)) {
							return;
						}
					} else if (MarkupLanguageProvider.class.isAssignableFrom(clazz)) {
						MarkupLanguageProvider provider = (MarkupLanguageProvider) clazz.newInstance();
						for (MarkupLanguage language : provider.getMarkupLanguages()) {
							if (!visitor.accept(language)) {
								return;
							}
						}
					}
				} catch (Exception e) {
					// very unusual, but inform the user in a stand-alone way
					Logger.getLogger(ServiceLocator.class.getName()).log(Level.WARNING,
							MessageFormat.format(Messages.getString("ServiceLocator.0"), className), e); //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * @since 2.0
	 */
	protected static class ResourceDescriptor {
		private final URL url;

		public ResourceDescriptor(URL url) {
			this.url = checkNotNull(url);
		}

		public URL getUrl() {
			return url;
		}
	}

	/**
	 * Loads the specified class.
	 * 
	 * @param resourceUrl
	 *            the service resource from which the class name was discovered
	 * @param className
	 *            the class name to load
	 * @return the class
	 * @throws ClassNotFoundException
	 *             if the class could not be loaded
	 * @since 2.0
	 */
	protected Class<?> loadClass(ResourceDescriptor resource, String className) throws ClassNotFoundException {
		return Class.forName(className, true, classLoader);
	}

	/**
	 * @return the service resources
	 * @since 2.0
	 * @see #getClasspathServiceResourceNames()
	 */
	protected List<ResourceDescriptor> discoverServiceResources() {
		List<ResourceDescriptor> serviceResources = Lists.newArrayList();
		for (String serviceResourceName : getClasspathServiceResourceNames()) {
			try {
				Enumeration<URL> resources = classLoader.getResources(serviceResourceName);
				while (resources.hasMoreElements()) {
					serviceResources.add(new ResourceDescriptor(resources.nextElement()));
				}
			} catch (IOException e) {
				logReadServiceClassNamesFailure(e);
			}
		}
		return serviceResources;
	}

	/**
	 * Provides the list of service resource names from which Java services should be loaded.
	 * 
	 * @return the list of service resource names
	 * @since 2.0
	 */
	protected List<String> getClasspathServiceResourceNames() {
		List<String> paths = Lists.newArrayList();
		for (String suffix : new String[] { "services/" + MarkupLanguage.class.getName(), //$NON-NLS-1$
				"services/" + MarkupLanguageProvider.class.getName() }) { //$NON-NLS-1$
			for (String prefix : new String[] { "", "META-INF/" }) { //$NON-NLS-1$//$NON-NLS-2$
				paths.add(prefix + suffix);
			}
		}
		return ImmutableList.copyOf(paths);
	}

	/**
	 * Reads the services provided in the file with the given URL. The URL must provide a file in the format expected by
	 * {@link ServiceLoader}.
	 * 
	 * @since 2.0
	 * @see ServiceLoader
	 */
	protected List<String> readServiceClassNames(URL url) {
		InputStream stream = null;
		try {
			stream = url.openStream();
			return readServiceClassNames(stream);
		} catch (IOException e) {
			logReadServiceClassNamesFailure(e);
		} finally {
			Closeables.closeQuietly(stream);
		}
		return Collections.emptyList();
	}

	List<String> readServiceClassNames(InputStream stream) {
		List<String> serviceClassNames = Lists.newArrayList();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(stream, Charsets.UTF_8));
			String line;
			while ((line = reader.readLine()) != null) {
				Matcher matcher = CLASS_NAME_PATTERN.matcher(line);
				if (matcher.matches()) {
					String className = matcher.group(1);
					if (className != null) {
						serviceClassNames.add(className);
					}
				}
			}
		} catch (IOException e) {
			logReadServiceClassNamesFailure(e);
		} finally {
			Closeables.closeQuietly(reader);
		}
		return serviceClassNames;
	}

	void logReadServiceClassNamesFailure(IOException e) {
		// very unusual, but inform in a stand-alone way
		Logger.getLogger(ServiceLocator.class.getName()).log(Level.SEVERE, Messages.getString("ServiceLocator.1"), e); //$NON-NLS-1$
	}

}
