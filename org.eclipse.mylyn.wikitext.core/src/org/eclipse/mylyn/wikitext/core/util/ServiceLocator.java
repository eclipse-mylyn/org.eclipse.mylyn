/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
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
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;

/**
 * A service locator for use both inside and outside of an Eclipse environment.
 * 
 * @author David Green
 */
public class ServiceLocator {

	private static final String UTF_8 = "utf-8";

	protected final ClassLoader classLoader;

	private static Class<? extends ServiceLocator> implementationClass;

	protected ServiceLocator(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * Get an instance of the service locator
	 * 
	 * @param classLoader
	 *            the class loader to use when looking up services
	 */
	public static ServiceLocator getInstance(ClassLoader classLoader) {
		if (implementationClass != null) {
			try {
				return implementationClass.getConstructor(ClassLoader.class).newInstance(classLoader);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		return new ServiceLocator(classLoader);
	}

	/**
	 * get a markup language by name
	 * 
	 * @param languageName
	 *            the {@link MarkupLanguage#getName() name} of the markup language, or the fully qualified name of the
	 *            class that implements the language
	 * 
	 * @return the language implementation
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided language name is null or if no implementation is available for the given language
	 */
	public MarkupLanguage getMarkupLanguage(String languageName) throws IllegalArgumentException {
		if (languageName == null) {
			throw new IllegalArgumentException();
		}
		Pattern classNamePattern = Pattern.compile("\\s*([^\\s#]+)?#?.*");
		// first try Java services (jar-based)
		Set<String> names = new TreeSet<String>();
		try {
			// note that we can't use the standard Java services API to load services here since the service may be declared on 
			// a specific class loader (not the system class loader).
			String servicesFilename = "META-INF/services/" + MarkupLanguage.class.getName();
			Enumeration<URL> resources = classLoader.getResources(servicesFilename);
			while (resources.hasMoreElements()) {
				URL url = resources.nextElement();
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), UTF_8));
					try {
						String line;
						while ((line = reader.readLine()) != null) {
							Matcher matcher = classNamePattern.matcher(line);
							if (matcher.matches()) {
								String className = matcher.group(1);
								if (className != null) {
									try {
										Class<?> clazz = Class.forName(className, true, classLoader);
										if (MarkupLanguage.class.isAssignableFrom(clazz)) {
											MarkupLanguage instance = (MarkupLanguage) clazz.newInstance();
											if (languageName.equals(instance.getName())) {
												return instance;
											}
											names.add(instance.getName());
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					} finally {
						reader.close();
					}
				} catch (IOException e) {
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
					e.printStackTrace();
				}
			}
		}

		// specified language not found.
		// create a useful error message
		StringBuilder buf = new StringBuilder();
		for (String name : names) {
			if (buf.length() != 0) {
				buf.append(", ");
			}
			buf.append('\'');
			buf.append(name);
			buf.append('\'');
		}
		throw new IllegalArgumentException(
				String.format(
						"No parser available for markup language '%s'. %s",
						languageName,
						buf.length() == 0 ? "There are no markup language parsers available on the current classpath.  Did you forget to add a jar file?"
								: "Known markup languages are " + buf));
	}

	public static void setImplementation(Class<? extends ServiceLocator> implementationClass) {
		ServiceLocator.implementationClass = implementationClass;
	}
}
