/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.bugzilla.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages.MylarTasklistOverlayDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * @author Shawn Minto
 * @author Mik Kersten
 */
public class BugzillaImages {

	private static ImageRegistry imageRegistry;

	private static final URL baseURL = BugzillaUiPlugin.getDefault().getBundle().getEntry("/icons/");

	public static final String T_ELCL = "elcl16";

	public static final String T_TOOL = "etool16";

	public static final ImageDescriptor IMG_TOOL_ADD_TO_FAVORITES = create(T_ELCL, "bug-favorite.gif");

	public static final ImageDescriptor BUG = create(T_ELCL, "bug.gif");

	public static final ImageDescriptor BUG_HIT = create(T_ELCL, "bug-small.gif");

	public static final ImageDescriptor IMG_COMMENT = create(T_ELCL, "bug-comment.gif");

	public static final ImageDescriptor TASK_BUG = create(T_TOOL, "task-repository.gif");

	public static final ImageDescriptor OVERLAY_INCOMMING = create(T_ELCL, "overlay-incoming.gif");

	public static final ImageDescriptor OVERLAY_OUTGOING = create(T_ELCL, "overlay-outgoing.gif");

	public static final ImageDescriptor OVERLAY_CONFLICT = create(T_ELCL, "overlay-conflicting.gif");

	public static final ImageDescriptor TASK_BUGZILLA = createWithOverlay(TASK_BUG, null, true, false);

	public static final ImageDescriptor TASK_BUGZILLA_INCOMMING = createWithOverlay(TASK_BUGZILLA, OVERLAY_INCOMMING,
			true, false);

	public static final ImageDescriptor TASK_BUGZILLA_CONFLICT = createWithOverlay(TASK_BUGZILLA, OVERLAY_CONFLICT,
			true, false);

	public static final ImageDescriptor TASK_BUGZILLA_OUTGOING = createWithOverlay(TASK_BUGZILLA, OVERLAY_OUTGOING,
			true, false);

	public static final ImageDescriptor BUGZILLA_HIT = createWithOverlay(BUG_HIT, null, true, false);

	public static final ImageDescriptor BUGZILLA_HIT_INCOMMING = BUGZILLA_HIT;// createWithOverlay(BUGZILLA_HIT,
																				// OVERLAY_INCOMMING);

	public static final ImageDescriptor TASK_BUGZILLA_NEW = create(T_TOOL, "task-bug-new.gif");

	public static final ImageDescriptor CATEGORY_QUERY = create(T_TOOL, "category-query.gif");

	public static final ImageDescriptor CATEGORY_QUERY_NEW = create(T_TOOL, "category-query-new.gif");

	public static final ImageDescriptor TASK_BUG_REFRESH = create(T_TOOL, "task-bug-refresh.gif");

	public static final ImageDescriptor REMOVE_ALL = create("", "remove-all.gif");

	public static final ImageDescriptor REMOVE = create("", "remove.gif");

	public static final ImageDescriptor SELECT_ALL = create("", "selectAll.gif");

	public static final ImageDescriptor OPEN = create("", "openresult.gif");

	private static ImageDescriptor create(String prefix, String name) {
		try {
			return ImageDescriptor.createFromURL(makeIconFileURL(prefix, name));
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	private static ImageDescriptor createWithOverlay(ImageDescriptor base, ImageDescriptor overlay, boolean top,
			boolean left) {
		return new MylarTasklistOverlayDescriptor(base, overlay, top, left);
	}

	private static URL makeIconFileURL(String prefix, String name) throws MalformedURLException {
		if (baseURL == null)
			throw new MalformedURLException();

		StringBuffer buffer = new StringBuffer(prefix);
		if (prefix != "")
			buffer.append('/');
		buffer.append(name);
		return new URL(baseURL, buffer.toString());
	}

	private static ImageRegistry getImageRegistry() {
		if (imageRegistry == null) {
			imageRegistry = new ImageRegistry();
		}

		return imageRegistry;
	}

	/**
	 * Lazily initializes image map.
	 */
	public static Image getImage(ImageDescriptor imageDescriptor) {
		ImageRegistry imageRegistry = getImageRegistry();
		Image image = imageRegistry.get("" + imageDescriptor.hashCode());
		if (image == null) {
			image = imageDescriptor.createImage();
			imageRegistry.put("" + imageDescriptor.hashCode(), image);
		}
		return image;
	}
}
