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
package org.eclipse.mylyn.internal.github.ui;

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
	public static final String GITHUB_ISSUE_LABEL_OBJ = NAME_PREFIX + "OBJ_GITHUB_ISSUE_LABEL"; //$NON-NLS-1$
	public static final String GITHUB_ADD_OBJ = NAME_PREFIX + "GITHUB_ADD_OBJ"; //$NON-NLS-1$
	public static final String GITHUB_CHECKALL_OBJ = NAME_PREFIX + "GITHUB_CHECKALL_OBJ"; //$NON-NLS-1$
	public static final String GITHUB_UNCHECKALL_OBJ = NAME_PREFIX + "GITHUB_UNCHECKALL_OBJ"; //$NON-NLS-1$
	public static final String GITHUB_ORG = NAME_PREFIX + "GITHUB_ORG"; //$NON-NLS-1$

	public static final ImageDescriptor DESC_GITHUB_LOGO = create(PATH_OBJ, "github.png"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_GITHUB_ISSUE_LABEL = create(PATH_OBJ, "issue_label.png"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_GITHUB_ADD = create(PATH_OBJ, "add.png"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_GITHUB_CHECKALL = create(PATH_OBJ, "checkall.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_GITHUB_UNCHECKALL = create(PATH_OBJ, "uncheckall.gif"); //$NON-NLS-1$
	public static final ImageDescriptor DESC_GITHUB_ORG = create(PATH_OBJ, "org.png"); //$NON-NLS-1$

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
		manage(GITHUB_ISSUE_LABEL_OBJ, DESC_GITHUB_ISSUE_LABEL);
		manage(GITHUB_ADD_OBJ, DESC_GITHUB_ADD);
		manage(GITHUB_CHECKALL_OBJ, DESC_GITHUB_CHECKALL);
		manage(GITHUB_UNCHECKALL_OBJ, DESC_GITHUB_UNCHECKALL);
		manage(GITHUB_ORG, DESC_GITHUB_ORG);
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
