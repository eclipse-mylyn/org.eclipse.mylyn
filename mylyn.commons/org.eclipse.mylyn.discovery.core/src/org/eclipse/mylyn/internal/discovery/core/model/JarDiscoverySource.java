/*******************************************************************************
 * Copyright (c) 2009, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.discovery.core.model;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author David Green
 */
public class JarDiscoverySource extends AbstractDiscoverySource {

	private final String id;

	private final File jarFile;

	public JarDiscoverySource(String id, File jarFile) {
		this.id = id;
		this.jarFile = jarFile;
	}

	@Override
	public Object getId() {
		return id;
	}

	@Override
	public URL getResource(String resourceName) {
		try {
			String prefix = jarFile.toURI().toURL().toExternalForm();

			return new URL("jar:" + prefix + "!/" + URLEncoder.encode(resourceName, "utf-8")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}
}
