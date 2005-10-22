/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.monitor.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.monitor.MylarMonitorPlugin;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */ 
public class MonitorImages {

    private static Map<ImageDescriptor, Image> imageMap = new HashMap<ImageDescriptor, Image>();
     
	private static final String T_ELCL = "elcl16";
	private static final String T_EVIEW = "eview16";
	private static final URL baseURL = MylarMonitorPlugin.getDefault().getBundle().getEntry("/icons/");
	
	public static final ImageDescriptor REFRESH = create(T_ELCL, "refresh.gif");
	public static final ImageDescriptor SYNCHED = create(T_ELCL, "synched.gif");
	public static final ImageDescriptor REMOVE = create(T_ELCL, "remove.gif");
	public static final ImageDescriptor MONITOR = create(T_EVIEW, "monitor.gif");
	
	public static final ImageDescriptor ZIP_FILE = create(T_ELCL, "import-zip.gif");
	
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
			
		StringBuffer buffer= new StringBuffer(prefix);
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
