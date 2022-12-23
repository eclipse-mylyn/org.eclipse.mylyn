/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Sascha Scholz (SAP) - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author Steffen Pingel
 * @author Sascha Scholz
 */
public class GerritImages {

	private static final URL baseURL = GerritUiPlugin.getDefault().getBundle().getEntry("/icons/"); //$NON-NLS-1$

	public static final ImageDescriptor OVERLAY_REVIEW = create("eview16/overlay-review.png"); //$NON-NLS-1$

	public static final ImageDescriptor GERRIT_OVERLAY = create("gerrit-overlay.png"); //$NON-NLS-1$

	public static final ImageDescriptor GERRIT = create("gerrit.png"); //$NON-NLS-1$

	public static final ImageDescriptor GIT_REPOSITORY = create("repository_rep.gif"); //$NON-NLS-1$

	private static ImageDescriptor create(String path) {
		try {
			return ImageDescriptor.createFromURL(makeIconFileURL(path));
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	private static URL makeIconFileURL(String path) throws MalformedURLException {
		if (baseURL == null) {
			throw new MalformedURLException();
		}
		return new URL(baseURL, path);
	}
}
