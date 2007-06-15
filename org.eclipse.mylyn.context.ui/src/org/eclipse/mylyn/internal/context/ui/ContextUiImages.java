/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.mylyn.context.ui.ContextUiPlugin;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class ContextUiImages {

	private static ImageRegistry imageRegistry;

	public static final String T_ELCL = "elcl16";

	public static final String T_TOOL = "etool16";

	public static final String T_VIEW = "eview16";
	
	private static final URL baseURL = ContextUiPlugin.getDefault().getBundle().getEntry("/icons/");

	public static final ImageDescriptor CONTEXT_ADD = create(T_TOOL, "context-add.gif");
	
	public static final ImageDescriptor CONTEXT_ATTACH = create(T_TOOL, "context-attach.gif");

	public static final ImageDescriptor CONTEXT_RETRIEVE = create(T_TOOL, "context-retrieve.gif");
	
	public static final ImageDescriptor CONTEXT_COPY = create(T_ELCL, "context-transfer.gif");
	
	public static final ImageDescriptor MYLYN = create(T_ELCL, "mylar.gif");

	public static final ImageDescriptor MYLYN_OVERLAY = create(T_ELCL, "mylar-overlay.gif");

	public static final ImageDescriptor DECORATE_INTEREST = create(T_ELCL, "refresh.gif");

	public static final ImageDescriptor SYNCHED = create(T_ELCL, "synched.gif");

	public static final ImageDescriptor REMOVE = create(T_ELCL, "remove.gif");

	public static final ImageDescriptor ERASE_TASKSCAPE = create(T_ELCL, "context-clear.gif");

	public static final ImageDescriptor INTEREST_LANDMARK = create(T_ELCL, "interest-landmark.gif");

	public static final ImageDescriptor INTEREST_FILTERING = create(T_ELCL, "interest-filtering.gif");

	public static final ImageDescriptor INTEREST_FOLDING = create(T_ELCL, "interest-filtering.gif");

	public static final ImageDescriptor CAPTURE_PAUSE = create(T_ELCL, "capture-pause.gif");

	public static final ImageDescriptor CAPTURE_RESUME = create(T_ELCL, "capture-resume.gif");

	public static final ImageDescriptor FILTER_COMPLETE = create(T_ELCL, "filter-complete.gif");

	public static final ImageDescriptor FILTER_PRIORITY = create(T_ELCL, "filter-priority.gif");

	public static final ImageDescriptor COPY = create(T_ELCL, "copy.png");

	public static final ImageDescriptor COLOR_PALETTE = create(T_ELCL, "color-palette.gif");

	public static final ImageDescriptor STOP_SEARCH = create(T_ELCL, "stop_all.gif");

	public static final ImageDescriptor CONTENT_ASSIST_SEPARATOR = create(T_TOOL, "content-assist-separator.gif");

	public static final ImageDescriptor QUALIFY_NAMES = create(T_TOOL, "qualify-names.gif");

	public static final ImageDescriptor EDGE_INHERITANCE = create(T_ELCL, "edge-inheritance.gif");

	public static final ImageDescriptor EDGE_REFERENCE = create(T_ELCL, "edge-reference.gif");

	public static final ImageDescriptor EDGE_ACCESS_READ = create(T_ELCL, "edge-read.gif");

	public static final ImageDescriptor EDGE_ACCESS_WRITE = create(T_ELCL, "edge-write.gif");

	public static final ImageDescriptor IMPORT_ZIP = create(T_ELCL, "import-zip.gif");

	public static final ImageDescriptor FILE_XML = create(T_ELCL, "file-xml.gif");

	public static final ImageDescriptor FILE_GENERIC = create(T_ELCL, "file_obj.gif");

	public static final ImageDescriptor FOLDER_GENERIC = create(T_ELCL, "fldr_obj.gif");

	public static final ImageDescriptor LINK_WITH_EDITOR = create(T_ELCL, "link-with-editor.gif");

//	public static final ImageDescriptor OVERLAY_MYLAR = create(T_VIEW, "overlay-mylar.gif");
	
	public static final ImageDescriptor BLANK = create(T_ELCL, "blank.gif");

	public static ImageDescriptor create(String prefix, String name) {
		return create(prefix, name, baseURL);
	}

	public static ImageDescriptor create(String prefix, String name, URL baseURL) {
		try {
			return ImageDescriptor.createFromURL(makeIconFileURL(prefix, name, baseURL));
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	private static URL makeIconFileURL(String prefix, String name, URL baseURL) throws MalformedURLException {
		if (baseURL == null)
			throw new MalformedURLException();

		StringBuffer buffer = new StringBuffer(prefix);
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
		if (imageDescriptor == null) {
			return null;
		} 
		
		ImageRegistry imageRegistry = getImageRegistry();
		Image image = imageRegistry.get("" + imageDescriptor.hashCode());
		if (image == null) {
			image = imageDescriptor.createImage();
			imageRegistry.put("" + imageDescriptor.hashCode(), image);
		}
		return image;
	}
}
