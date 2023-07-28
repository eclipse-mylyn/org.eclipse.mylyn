/*******************************************************************************
 * Copyright (c) 2023 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.gitlab.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

public class GitlabImages {

    // For the images
    private static ImageRegistry fImageRegistry = new ImageRegistry();

    public static String GITLAB_PICTURE_FILE = "icons/obj20/gitlab.png"; //$NON-NLS-1$

    static {
	fImageRegistry.put(GITLAB_PICTURE_FILE, GitlabUiActivator.getImageDescriptor(GITLAB_PICTURE_FILE));
    }

    public static ImageDescriptor getDescriptor(String key) {
	return fImageRegistry.getDescriptor(key);
    }

    public static Image getImage(String key) {
	return fImageRegistry.get(key);
    }

}
