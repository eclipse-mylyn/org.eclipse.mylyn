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
package org.eclipse.mylyn.internal.bugzilla.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
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

	public static final String T_VIEW = "eview16";
	
	public static final ImageDescriptor OVERLAY_BUGZILLA = create(T_VIEW, "overlay-bugzilla.gif");
	
	public static final ImageDescriptor BUG = create(T_ELCL, "bug.gif");

	public static final ImageDescriptor BUG_COMMENT = create(T_ELCL, "bug-comment.gif");
	
	public static final ImageDescriptor REMOVE_ALL = create("", "remove-all.gif");

	public static final ImageDescriptor REMOVE = create("", "remove.gif");

	public static final ImageDescriptor SELECT_ALL = create("", "selectAll.gif");

	public static final ImageDescriptor OPEN = create("", "openresult.gif");

	public static final ImageDescriptor OVERLAY_CRITICAL = create(T_VIEW, "overlay-critical.gif");
	
	public static final ImageDescriptor OVERLAY_MAJOR = create(T_VIEW, "overlay-major.gif");

	public static final ImageDescriptor OVERLAY_ENHANCEMENT = create(T_VIEW, "overlay-enhancement.gif");

	public static final ImageDescriptor OVERLAY_MINOR = create(T_VIEW, "overlay-minor.gif");
	
	private static ImageDescriptor create(String prefix, String name) {
		try {
			return ImageDescriptor.createFromURL(makeIconFileURL(prefix, name));
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
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
