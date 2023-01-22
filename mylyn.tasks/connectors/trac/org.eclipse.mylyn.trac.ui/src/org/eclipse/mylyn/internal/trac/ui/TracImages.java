/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author Shawn Minto
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class TracImages {

	private static final URL baseURL = TracUiPlugin.getDefault().getBundle().getEntry("/icons/"); //$NON-NLS-1$

	public static final String T_VIEW = "eview16"; //$NON-NLS-1$

	public static final ImageDescriptor OVERLAY_DEFECT = create(T_VIEW, "overlay-critical.gif"); //$NON-NLS-1$

	public static final ImageDescriptor OVERLAY_ENHANCEMENT = create(T_VIEW, "overlay-enhancement.gif"); //$NON-NLS-1$

	public static final ImageDescriptor OVERLAY_STORY = create(T_VIEW, "overlay-story.gif"); //$NON-NLS-1$

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
