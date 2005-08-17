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
/*
 * Created on Apr 20, 2004
 */
package org.eclipse.mylar.tasklist;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */ 
public class TaskListImages {

    private static Map<ImageDescriptor, Image> imageMap = new HashMap<ImageDescriptor, Image>();
     
	private static final String T_ELCL = "elcl16";
	private static final String T_TOOL = "etool16";
	private static final URL baseURL = MylarTasklistPlugin.getDefault().getBundle().getEntry("/icons/");
	
	public static final ImageDescriptor REMOVE = create(T_ELCL, "remove.gif");
	public static final ImageDescriptor ERASE_TASKSCAPE = create(T_ELCL, "context-clear.gif");

    public static final ImageDescriptor FILTER_COMPLETE = create(T_ELCL, "filter-complete.gif");
    public static final ImageDescriptor FILTER_PRIORITY = create(T_ELCL, "filter-priority.gif");
    
    public static final ImageDescriptor COLOR_PALETTE = create(T_ELCL, "color-palette.gif");
    
    public static final ImageDescriptor TASK = create(T_TOOL, "task.gif"); 
    public static final ImageDescriptor TASK_NEW = create(T_TOOL, "task-new.gif"); 
    public static final ImageDescriptor CATEGORY = create(T_TOOL, "category.gif"); 
    public static final ImageDescriptor CATEGORY_NEW = create(T_TOOL, "category-new.gif");
    
    public static final ImageDescriptor NAVIGATE_PREVIOUS = create(T_TOOL, "navigate-previous.gif");
    public static final ImageDescriptor NAVIGATE_NEXT = create(T_TOOL, "navigate-next.gif"); 
        
    public static final ImageDescriptor TASK_ACTIVE = create(T_TOOL, "task-active.gif");
    public static final ImageDescriptor TASK_INACTIVE = create(T_TOOL, "task-inactive.gif");
    public static final ImageDescriptor TASK_COMPLETE = create(T_TOOL, "task-complete.gif");
    public static final ImageDescriptor TASK_INCOMPLETE = create(T_TOOL, "task-incomplete.gif");

	public static final ImageDescriptor COLLAPSE_ALL = create(T_ELCL, "collapseall.png");
   
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
