/*******************************************************************************
 * Copyright (c) 2007, 2021 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.tests;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * A utility for visiting Mylyn classes available on the classpath.
 *
 * @author David Green
 */
public class ClassTraversal {
	private static final Pattern BUNDLE_RESOURCE_35 = Pattern.compile("(\\d+)\\..*");

	public void visitClasses(Visitor visitor) {
		visitClasses(ClassTraversal.class, visitor);
	}

	private void visitClasses(Class<ClassTraversal> classOnClasspath, Visitor visitor) {
		ClassLoader loader = classOnClasspath.getClassLoader();
		String resourceOfClass = classOnClasspath.getCanonicalName().replace('.', '/') + ".class";
		Enumeration<URL> resources;
		try {
			resources = loader.getResources(resourceOfClass);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		while (resources.hasMoreElements()) {
			URL url = resources.nextElement();
			String protocol = url.getProtocol();
			if (protocol.equals("file")) {
				String file = url.getFile();
				file = URLDecoder.decode(file.substring(0, file.indexOf(resourceOfClass)), StandardCharsets.UTF_8);
				visitClasses(loader, new File(file), new File(file), visitor);

			} else if (protocol.equals("bundleresource")) {
				String host = url.getHost();
				// bug 266767 see http://dev.eclipse.org/mhonarc/lists/equinox-dev/msg05209.html
				Matcher bundle35Matcher = BUNDLE_RESOURCE_35.matcher(host);
				if (bundle35Matcher.matches()) {
					host = bundle35Matcher.group(1);
				}
				long bundleId = Long.parseLong(host);
				Bundle bundle = getBundle(bundleId);
				if (bundle == null) {
					throw new IllegalStateException("Cannot get bundle " + bundleId);
				}

				String path = url.getFile();
				path = path.substring(0, path.indexOf(resourceOfClass));

				visitClasses(bundle, path, visitor);
			} else {
				throw new IllegalStateException("Unimplemented protocol: " + protocol);
			}
		}
	}

	private Bundle getBundle(long bundleId) {
		return requireNonNull(FrameworkUtil.getBundle(WikiTextUiPlugin.class), "Cannot determine bundle")
				.getBundleContext()
				.getBundle(bundleId);
	}

	private void visitClasses(ClassLoader loader, File root, File file, Visitor visitor) {
		File[] files = file.listFiles();
		if (files != null) {
			for (File child : files) {
				if (child.isDirectory()) {
					visitClasses(loader, root, child, visitor);
				} else {
					String path = child.getPath();
					if (path.endsWith(".class")) {
						String fqn = path.substring(root.getPath().length() + 1, path.length() - ".class".length());
						fqn = fqn.replace('/', '.').replace('\\', '.');
						Class<?> clazz;
						try {
							clazz = Class.forName(fqn, true, loader);
						} catch (LinkageError | Exception e) {
							// can't load the class, so skip it.
							continue;
						}
						visitor.visit(clazz);
					}
				}
			}
		}
	}

	private void visitClasses(Bundle bundle, String path, Visitor visitor) {
		Enumeration<URL> entries = bundle.findEntries(path, "*.class", true);
		while (entries.hasMoreElements()) {
			URL element = entries.nextElement();
			String filePath = element.getFile();
			if (filePath.indexOf("org/eclipse/mylyn") != -1) {
				filePath = filePath.substring(filePath.indexOf("org/eclipse/mylyn"));
			} else {
				continue;
			}
			String fqn = filePath.substring(0, filePath.length() - ".class".length()).replace('/', '.');
			Class<?> clazz;
			try {
				clazz = bundle.loadClass(fqn);
			} catch (Exception e) {
				// can't laod the class, so skip it.
				continue;
			}
			visitor.visit(clazz);
		}
	}

	public interface Visitor {
		void visit(Class<?> clazz);
	}
}
