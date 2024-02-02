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
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author Mik Kersten
 */
public class ContextUiImages {

	private static final String T_ELCL = "elcl16"; //$NON-NLS-1$

	private static final URL baseURL = ContextUiPlugin.getDefault().getBundle().getEntry("/icons/"); //$NON-NLS-1$

	public static final ImageDescriptor FILE_XML = create(T_ELCL, "file-xml.gif"); //$NON-NLS-1$

	public static final ImageDescriptor FILE_GENERIC = create(T_ELCL, "file_obj.gif"); //$NON-NLS-1$

	public static final ImageDescriptor FOLDER_GENERIC = create(T_ELCL, "fldr_obj.gif"); //$NON-NLS-1$

	public static final ImageDescriptor CONTEXT_FOCUS = create(T_ELCL, "focus.gif"); //$NON-NLS-1$

	private static ImageDescriptor create(String prefix, String name) {
		return create(prefix, name, baseURL);
	}

	private static ImageDescriptor create(String prefix, String name, URL baseURL) {
		try {
			return ImageDescriptor.createFromURL(makeIconFileURL(prefix, name, baseURL));
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	private static URL makeIconFileURL(String prefix, String name, URL baseURL) throws MalformedURLException {
		if (baseURL == null) {
			throw new MalformedURLException();
		}

		StringBuilder buffer = new StringBuilder(prefix);
		buffer.append('/');
		buffer.append(name);
		return new URL(baseURL, buffer.toString());
	}

}
