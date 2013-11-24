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
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Closeables;

/**
 * A service locator for use both inside and outside of an Eclipse environment. Provides access to markup languages by
 * name.
 * 
 * @author David Green
 * @since 1.0
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
		if (languageName == null) {
			throw new IllegalArgumentException();
		}
		Pattern classNamePattern = Pattern.compile("\\s*([^\\s#]+)?#?.*"); //$NON-NLS-1$
		// first try Java services (jar-based)
		final Set<String> names = new TreeSet<String>();

		final MarkupLanguage[] result = new MarkupLanguage[1];

		loadMarkupLanguages(new MarkupLanguageVisitor() {

			public boolean accept(MarkupLanguage language) {
				if (languageName.equals(language.getName())) {
					result[0] = language;
					return false;
				}
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
		try {
			// note that we can't use the standard Java services API to load services here since the service may be declared on 
			// a specific class loader (not the system class loader).
			String servicesFilename = "META-INF/services/" + MarkupLanguage.class.getName(); //$NON-NLS-1$
			Enumeration<URL> resources = classLoader.getResources(servicesFilename);
			while (resources.hasMoreElements()) {
				List<String> classNames = readServiceClassNames(resources.nextElement());
				for (String className : classNames) {
					try {
						Class<?> clazz = Class.forName(className, true, classLoader);
						if (MarkupLanguage.class.isAssignableFrom(clazz)) {
							MarkupLanguage instance = (MarkupLanguage) clazz.newInstance();
							if (!visitor.accept(instance)) {
								return;
							}
						}
					} catch (Exception e) {
						// very unusual, but inform the user in a stand-alone way
						Logger.getLogger(ServiceLocator.class.getName()).log(Level.WARNING,
								MessageFormat.format(Messages.getString("ServiceLocator.0"), className), e); //$NON-NLS-1$
					}
				}
			}
		} catch (IOException e) {
			logReadServiceClassNamesFailure(e);
		}

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

	protected void logReadServiceClassNamesFailure(IOException e) {
		// very unusual, but inform in a stand-alone way
		Logger.getLogger(ServiceLocator.class.getName()).log(Level.SEVERE, Messages.getString("ServiceLocator.1"), e); //$NON-NLS-1$
	}
}
