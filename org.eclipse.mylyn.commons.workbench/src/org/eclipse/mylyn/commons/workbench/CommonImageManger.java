/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Perforce - fixes for bug 343892
 *     GitHub - fixes for bug 350333 
 *******************************************************************************/

package org.eclipse.mylyn.commons.workbench;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchImages;

/**
 * @author Steffen Pingel
 * @author Kevin Sawicki
 * @since 3.7
 */
public class CommonImageManger {

	private static final String[] IMAGE_EXTENSIONS = { "jpg", "gif", "png", "tiff", "tif", "bmp" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

	private final LocalResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());

	public CommonImageManger() {
	}

	public void dispose() {
		resourceManager.dispose();
	}

	public Image getFolderImage() {
		return WorkbenchImages.getImage(ISharedImages.IMG_OBJ_FOLDER);
	}

	public Image getFileImage(String filename) {
		if (filename != null) {
			int dotIndex = filename.lastIndexOf('.');
			if (dotIndex != -1) {
				String fileType = filename.substring(dotIndex + 1);
				for (String element2 : IMAGE_EXTENSIONS) {
					if (element2.equalsIgnoreCase(fileType)) {
						return CommonImages.getImage(CommonImages.IMAGE_FILE);
					}
				}
			}
			String file = new Path(filename).lastSegment();
			if (file != null) {
				return getImage(PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor(filename));
			}
		}
		return WorkbenchImages.getImage(ISharedImages.IMG_OBJ_FILE);
	}

	public Image getImage(ImageDescriptor imageDescriptor) {
		return (Image) resourceManager.get(imageDescriptor);
	}

	public LocalResourceManager getResourceManager() {
		return resourceManager;
	}

}
