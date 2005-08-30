/*******************************************************************************
 * Copyright (c) 2003 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.tasklist.TaskListImages.MylarTasklistOverlayDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * This class provides convenience access to many of the resources required
 * by the workbench. The class stores some images as descriptors, and
 * some are stored as real Images in the registry.  This is a pure
 * speed-space tradeoff.  The trick for users of this class is that
 * images obtained from the registry (using getImage()), don't require
 * disposal since they are shared, while images obtained using
 * getImageDescriptor() will require disposal.  Consult the declareImages
 * method to see if a given image is declared as a registry image or
 * just as a descriptor.  If you change an image from being stored
 * as a descriptor to a registry image, or vice-versa, make sure to
 * check all users of the image to ensure they are calling
 * the correct getImage... method and handling disposal correctly.
 *
 *  Images:
 *      - use getImage(key) to access cached images from the registry.
 *      - Less common images are found by calling getImageDescriptor(key)
 *          where key can be found in IWorkbenchGraphicConstants
 *
 *      This class initializes the image registry by declaring all of the required
 *      graphics. This involves creating image descriptors describing
 *      how to create/find the image should it be needed.
 *      The image is not actually allocated until requested.
 *
 *      Some Images are also made available to other plugins by being
 *      placed in the descriptor table of the SharedImages class.
 *
 *      Where are the images?
 *          The images (typically gifs) are found the plugins install directory
 *
 *      How to add a new image
 *          Place the gif file into the appropriate directories.
 *          Add a constant to IWorkbenchGraphicConstants following the conventions
 *          Add the declaration to this file
 */
public class BugzillaImages {

	private static Map<ImageDescriptor, Image> imageMap = new HashMap<ImageDescriptor, Image>();
	
	private static final URL baseURL = BugzillaUiPlugin.getDefault().getBundle().getEntry("/icons/");
	public static final String T_ELCL = "elcl16";
	public static final String T_TOOL = "etool16";
	
	public static final ImageDescriptor IMG_TOOL_ADD_TO_FAVORITES = create(T_ELCL, "bug-favorite.gif");
    public static final ImageDescriptor BUG = create(T_ELCL, "bug.gif");
    public static final ImageDescriptor IMG_COMMENT = create(T_ELCL, "bug-comment.gif");
    public static final ImageDescriptor TASK_BUG = create(T_TOOL, "task-bug.gif");
    
    public static final ImageDescriptor OVERLAY_INCOMMING = create(T_ELCL, "overlay-incoming.gif");
	public static final ImageDescriptor OVERLAY_OUTGOING = create(T_ELCL, "overlay-outgoing.gif");
	public static final ImageDescriptor OVERLAY_CONFLICT = create(T_ELCL, "overlay-conflicting.gif");
	
	public static final ImageDescriptor TASK_BUGZILLA = createWithOverlay(TASK_BUG, null);
    public static final ImageDescriptor TASK_BUGZILLA_INCOMMING = createWithOverlay(TASK_BUGZILLA, OVERLAY_INCOMMING);
	public static final ImageDescriptor TASK_BUGZILLA_CONFLICT = createWithOverlay(TASK_BUGZILLA, OVERLAY_CONFLICT);
	public static final ImageDescriptor TASK_BUGZILLA_OUTGOING = createWithOverlay(TASK_BUGZILLA, OVERLAY_OUTGOING);
	public static final ImageDescriptor BUGZILLA_HIT = createWithOverlay(BUG, null);
	
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
	
	private static ImageDescriptor createWithOverlay(ImageDescriptor base, ImageDescriptor overlay) { 
		return new MylarTasklistOverlayDescriptor(base, overlay);
	}

	private static URL makeIconFileURL(String prefix, String name) throws MalformedURLException {
		if (baseURL == null)
			throw new MalformedURLException();
			
		StringBuffer buffer= new StringBuffer(prefix);
		if(prefix != "")
			buffer.append('/');
		buffer.append(name);
		return new URL(baseURL, buffer.toString());
	}	
	
	/**
	 * Lazily initializes image map.
	 */
	public static Image getImage(ImageDescriptor imageDescriptor) {
	    Image image = imageMap.get(imageDescriptor);
	    if (image == null) {
	        image = imageDescriptor.createImage();
	        imageMap.put(imageDescriptor, image);
	    }
	    return image;
	}
}
