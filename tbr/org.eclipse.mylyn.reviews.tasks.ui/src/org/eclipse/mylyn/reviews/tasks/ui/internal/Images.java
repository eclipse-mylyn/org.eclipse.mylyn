/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Christoph Mayerhofer (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.tasks.ui.internal;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * Image helper class which contains all application images.
 *
 * @author Christoph Mayerhofer
 */
public class Images {

	protected final static ImageRegistry PLUGIN_REGISTRY = ReviewsUiPlugin
			.getDefault().getImageRegistry();

	// icons path
	public final static String ICONS_PATH = "icons/"; //$NON-NLS-1$

	public static final ImageDescriptor SMALL_ICON = create(ICONS_PATH,
			"reviews16.gif"); //$NON-NLS-1$

	public static final ImageDescriptor ICON = create(ICONS_PATH,
			"reviews24.gif"); //$NON-NLS-1$
	public static final ImageDescriptor BIG_ICON = create(ICONS_PATH,
			"reviews32.gif"); //$NON-NLS-1$

	public static final ImageDescriptor REVIEW_RESULT_FAILED = create(
			ICONS_PATH, "review_failed.png"); //$NON-NLS-1$
	public static final ImageDescriptor REVIEW_RESULT_WARNING = create(
			ICONS_PATH, "review_warning.png"); //$NON-NLS-1$
	public static final ImageDescriptor REVIEW_RESULT_PASSED = create(
			ICONS_PATH, "review_passed.png"); //$NON-NLS-1$
	public static final ImageDescriptor REVIEW_RESULT_NONE = create(ICONS_PATH,
			"review_none.png"); //$NON-NLS-1$
	public static final ImageDescriptor OVERLAY_ADDITION = create(ICONS_PATH,
			"addition.gif"); //$NON-NLS-1$
	public static final ImageDescriptor OVERLAY_OBSTRUCTED = create(ICONS_PATH,
			"obstructed.gif"); //$NON-NLS-1$
	public static final ImageDescriptor MAXIMIZE = create(ICONS_PATH,
	"maximize.png"); //$NON-NLS-1$
	public static final ImageDescriptor FILTER = create(ICONS_PATH,
	"filter_ps.gif"); //$NON-NLS-1$

	protected static ImageDescriptor create(String prefix, String name) {
		return ImageDescriptor.createFromURL(makeIconURL(prefix, name));
	}

	/**
	 * Get the image for the specified key from the registry.
	 *
	 * @param key
	 *            The key for the image.
	 * @return The image, or <code>null</code> if none.
	 */
	public static Image get(String key) {
		return PLUGIN_REGISTRY.get(key);
	}

	private static URL makeIconURL(String prefix, String name) {
		String path = "$nl$/" + prefix + name; //$NON-NLS-1$
		return FileLocator.find(ReviewsUiPlugin.getDefault().getBundle(),
				new Path(path), null);
	}

	/**
	 * Puts the image with the specified key to the image registry.
	 *
	 * @param key
	 *            The key for the image.
	 * @param desc
	 *            The ImageDescriptor for which the image is created.
	 * @return The image which has been added to the registry.
	 */
	public static Image manage(String key, ImageDescriptor desc) {
		Image image = desc.createImage();
		PLUGIN_REGISTRY.put(key, image);
		return image;
	}
}
