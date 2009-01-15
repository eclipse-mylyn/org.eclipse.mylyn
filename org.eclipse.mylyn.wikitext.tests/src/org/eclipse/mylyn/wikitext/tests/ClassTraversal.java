/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.tests;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;

import org.eclipse.mylyn.internal.wikitext.core.WikiTextPlugin;
import org.osgi.framework.Bundle;

/**
 * A utility for visiting Mylyn classes available on the classpath.
 * 
 * @author David Green
 */
public class ClassTraversal {
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
				try {
					file = URLDecoder.decode(file, "utf-8").substring(0, file.indexOf(resourceOfClass));
				} catch (UnsupportedEncodingException e) {
					throw new IllegalStateException(e);
				}
				visitClasses(loader, new File(file), new File(file), visitor);

			} else if (protocol.equals("bundleresource")) {
				long bundleId = Long.parseLong(url.getHost());
				Bundle bundle = WikiTextPlugin.getDefault().getBundle().getBundleContext().getBundle(bundleId);
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
						} catch (LinkageError e) {
							// see bug 255568 comment 11
							// can't load the class, so skip it.
							continue;
						} catch (Exception e) {
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
		public void visit(Class<?> clazz);
	}
}
