/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author Steffen Pingel
 */
public class BuildImages {

	private static final URL baseURL = BuildsUiPlugin.getDefault().getBundle().getEntry("/icons/"); //$NON-NLS-1$

	public static final String T_OBJ = "obj16"; //$NON-NLS-1$

	public static final String T_OVR = "ovr16"; //$NON-NLS-1$

	public static final ImageDescriptor STATUS_PASSED = create(T_OBJ, "passed-status.png"); //$NON-NLS-1$

	public static final ImageDescriptor STATUS_FAILED = create(T_OBJ, "failed-status.gif"); //$NON-NLS-1$

	public static final ImageDescriptor DECORATION_RUNNING = create(T_OVR, "run_co.gif"); //$NON-NLS-1$;

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
		if (prefix != "") { //$NON-NLS-1$
			buffer.append('/');
		}
		buffer.append(name);
		return new URL(baseURL, buffer.toString());
	}

}
