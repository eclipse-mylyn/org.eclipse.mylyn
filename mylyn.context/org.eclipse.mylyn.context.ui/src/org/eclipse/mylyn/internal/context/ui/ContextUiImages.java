/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author Mik Kersten
 */
public class ContextUiImages {

	private static final String T_ELCL = "elcl16"; //$NON-NLS-1$

	private static final URI baseURI = toURI(ContextUiPlugin.getDefault().getBundle().getEntry("/icons/")); //$NON-NLS-1$

	public static final ImageDescriptor FILE_XML = create(T_ELCL, "file-xml.gif"); //$NON-NLS-1$

	public static final ImageDescriptor FILE_GENERIC = create(T_ELCL, "file_obj.gif"); //$NON-NLS-1$

	public static final ImageDescriptor FOLDER_GENERIC = create(T_ELCL, "fldr_obj.gif"); //$NON-NLS-1$

	public static final ImageDescriptor CONTEXT_FOCUS = create(T_ELCL, "focus.gif"); //$NON-NLS-1$

	private static URI toURI(URL url) {
		if (url == null) {
			return null;
		}
		try {
			return url.toURI();
		} catch (URISyntaxException e) {
			return null;
		}
	}

	private static ImageDescriptor create(String prefix, String name) {
		return create(prefix, name, baseURI);
	}

	private static ImageDescriptor create(String prefix, String name, URI baseURI) {
		try {
			return ImageDescriptor.createFromURL(makeIconFileURL(prefix, name, baseURI));
		} catch (URISyntaxException | MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	private static URL makeIconFileURL(String prefix, String name, URI baseURI)
			throws URISyntaxException, MalformedURLException {
		if (baseURI == null) {
			throw new URISyntaxException("", "baseURI is null"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		URI resolved = baseURI.resolve(prefix + '/' + name);
		return resolved.toURL();
	}

}