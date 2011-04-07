/*******************************************************************************
 * Copyright (c) 2011 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Chris Aniszczyk <caniszczyk@gmail.com> - initial contribution
 *******************************************************************************/
package org.eclipse.mylyn.github.ui.internal;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

public class GitHubImages {

	private static final String NAME_PREFIX = "org.eclipse.mylyn.github.ui"; //$NON-NLS-1$
	private static final String ICONS_PATH = "icons/"; //$NON-NLS-1$
	private static final String PATH_OBJ = ICONS_PATH + "obj16/"; //$NON-NLS-1$

	private static ImageRegistry manager;

	public static final String GITHUB_LOGO_OBJ = NAME_PREFIX + "OBJ_GITHUB_LOGO"; //$NON-NLS-1$

	public static final ImageDescriptor DESC_GITHUB_LOGO = create(PATH_OBJ, "github.png"); //$NON-NLS-1$

	private static ImageDescriptor create(String prefix, String name) {
		return ImageDescriptor.createFromURL(makeImageURL(prefix, name));
	}

	public static Image get(String key) {
		if (manager == null)
			initialize();
		return manager.get(key);
	}

	private static final void initialize() {
		manager = new ImageRegistry();
		manage(GITHUB_LOGO_OBJ, DESC_GITHUB_LOGO);
	}

	private static URL makeImageURL(String prefix, String name) {
		String path = "$nl$/" + prefix + name; //$NON-NLS-1$
		Bundle bundle = Platform.getBundle(GitHubUi.BUNDLE_ID);
		return FileLocator.find(bundle, new Path(path), null);
	}

	private static Image manage(String key, ImageDescriptor desc) {
		Image image = desc.createImage();
		manager.put(key, image);
		return image;
	}

}
