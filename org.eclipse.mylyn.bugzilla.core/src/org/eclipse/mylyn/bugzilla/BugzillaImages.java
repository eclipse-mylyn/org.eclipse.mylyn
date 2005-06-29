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
package org.eclipse.mylar.bugzilla;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
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
	public static final String IMG_TOOL_ADD_TO_FAVORITES = "IMG_TOOL_FAVORITE";
	
    public static final String BUG = "IMG_BUG";
    public static final String IMG_COMMENT = "IMG_COMMENT";
    
    
	private static HashMap<String, ImageDescriptor> descriptors = new HashMap<String, ImageDescriptor>();
	
	private static ImageRegistry imageRegistry;

	// Subdirectory (under the package containing this class) where 16 color images are
	private static final URL URL_BASIC = BugzillaPlugin.getDefault().getBundle().getEntry("/");

	public final static String ICONS_PATH = "icons/";//$NON-NLS-1$

	private final static void declareImages() {
		// toolbar icons for the result view
		declareImage(IMG_TOOL_ADD_TO_FAVORITES, ICONS_PATH+"elcl16/bug-favorite.gif");//$NON-NLS-1$
        declareImage(BUG, ICONS_PATH+"elcl16/bug.gif");//$NON-NLS-1$
        declareImage(IMG_COMMENT, ICONS_PATH+"elcl16/bug-comment.gif");//$NON-NLS-1$
	}

	/**
	 * Declare an ImageDescriptor in the descriptor table.
	 * @param key   The key to use when registering the image
	 * @param path  The path where the image can be found. This path is relative to where
	 *              this plugin class is found (i.e. typically the packages directory)
	 */
	private final static void declareImage(String key,String path) {
		URL url = null;
		try {
			url = new URL(URL_BASIC, path);
		} catch (MalformedURLException e) {
			BugzillaPlugin.log(new Status(IStatus.WARNING, IBugzillaConstants.PLUGIN_ID,IStatus.OK,"Unable to declare the image for: " + path, e));
		}
		ImageDescriptor desc = ImageDescriptor.createFromURL(url);
		descriptors.put(key, desc);
	}

	/**
	 * Returns the image stored in the workbench plugin's image registry 
	 * under the given symbolic name.  If there isn't any value associated 
	 * with the name then <code>null</code> is returned.  
	 *
	 * The returned Image is managed by the workbench plugin's image registry.  
	 * Callers of this method must not dispose the returned image.
	 *
	 * This method is essentially a convenient short form of
	 * HipikatImages.getImageRegistry.get(symbolicName).
	 */
	public static Image getImage(String symbolicName) {
		return getImageRegistry().get(symbolicName);
	}

	/**
	 * Returns the image descriptor stored under the given symbolic name.
	 * If there isn't any value associated with the name then <code>null
	 * </code> is returned.
	 *
	 * The class also "caches" commonly used images in the image registry.
	 * If you are looking for one of these common images it is recommended you use 
	 * the getImage() method instead.
	 */
	public static ImageDescriptor getImageDescriptor(String symbolicName) {
		if (imageRegistry == null) {
			initializeImageRegistry();
		}
		return descriptors.get(symbolicName);
	}

	/**
	 * Returns the ImageRegistry.
	 */
	public static ImageRegistry getImageRegistry() {
		if (imageRegistry == null) {
			initializeImageRegistry();
		}
		return imageRegistry;
	}

	private static ImageRegistry initializeImageRegistry() {
		imageRegistry = new ImageRegistry();
		declareImages();
		return imageRegistry;
	}
}
