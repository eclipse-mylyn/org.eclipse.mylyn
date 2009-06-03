/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.discovery.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author David Green
 */
public class DiscoveryImages {
	private static final String T_OVR_32 = "ovr32"; //$NON-NLS-1$

	private static final String T_OBJ_16 = "obj16"; //$NON-NLS-1$

	private static final URL baseURL = Platform.getBundle(DiscoveryUi.ID_PLUGIN).getEntry("/icons/"); //$NON-NLS-1$

	/**
	 * image descriptor for a warning overlay suitable for use with 32x32 images.
	 */
	public static final ImageDescriptor OVERLAY_WARNING_32 = create(T_OVR_32, "message_warning.gif"); //$NON-NLS-1$

	public static final ImageDescriptor MESSAGE_INFO = create(T_OBJ_16, "message_info.gif"); //$NON-NLS-1$

	private static ImageDescriptor create(String prefix, String name) {
		try {
			return ImageDescriptor.createFromURL(makeIconFileURL(prefix, name));
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	private static URL makeIconFileURL(String prefix, String name) throws MalformedURLException {
		if (baseURL == null) {
			throw new MalformedURLException();
		}

		StringBuilder buffer = new StringBuilder(prefix);
		buffer.append('/');
		buffer.append(name);
		return new URL(baseURL, buffer.toString());
	}

}
