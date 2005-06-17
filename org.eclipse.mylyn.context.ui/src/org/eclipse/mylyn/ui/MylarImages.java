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
package org.eclipse.mylar.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */ 
public class MylarImages {

    private static Map<ImageDescriptor, Image> imageMap = new HashMap<ImageDescriptor, Image>();
     
	private static final String T_ELCL = "elcl16";
	private static final String T_EVIEW = "eview16";
    private static final String STRUCTURE = "structure";
    private static final String RELATIONSHIPS = "relationships";
	private static final URL baseURL = MylarUiPlugin.getDefault().getBundle().getEntry("/icons/");
	  
	public static final ImageDescriptor REFRESH = create(T_ELCL, "refresh.gif");
	public static final ImageDescriptor SYNCHED = create(T_ELCL, "synched.gif");
	public static final ImageDescriptor REMOVE = create(T_ELCL, "remove.gif");
	public static final ImageDescriptor TIME = create(T_ELCL, "time.gif");
	public static final ImageDescriptor ERASE_TASKSCAPE = create(T_ELCL, "erase-model.gif");
	public static final ImageDescriptor FILTER_UNINTERESTING = create(T_ELCL, "auto-fold.gif");
	public static final ImageDescriptor AUTO_EXPAND = create(T_ELCL, "auto-expand.gif");
	public static final ImageDescriptor AUTO_FOLD = create(T_ELCL, "auto-fold.gif");
	public static final ImageDescriptor FILTER_DECLARATIONS = create(T_ELCL, "filter-declarations.gif");
    public static final ImageDescriptor INTERSECTION = create(T_ELCL, "filter-declarations.gif");

    public static final ImageDescriptor IMPORT_ZIP = create(T_ELCL, "import-zip.gif");
    
    public static final ImageDescriptor BUG = create(STRUCTURE, "bug-report.gif");
    public static final ImageDescriptor TASK_BUGZILLA = create(STRUCTURE, "bug-task.gif");
    public static final ImageDescriptor TASK_BUGZILLA_NEW = create(STRUCTURE, "bug-task-new.gif");
    public static final ImageDescriptor TASK_BUGZILLA_OUTGOING = create(STRUCTURE, "bug-outgoing.gif");
    public static final ImageDescriptor TASK_BUGZILLA_LOCAL = create(STRUCTURE, "bug-local.gif");
    public static final ImageDescriptor TASK = create(STRUCTURE, "task.gif"); 
    public static final ImageDescriptor TASK_NEW = create(STRUCTURE, "task-new.gif"); 
    public static final ImageDescriptor TASK_WITH_TASKSCAPE = create(STRUCTURE, "task-with-taskscape.gif"); 
    public static final ImageDescriptor BUG_WITH_TASKSCAPE = create(STRUCTURE, "bug-with-taskscape.gif"); 
    public static final ImageDescriptor CATEGORY = create(T_ELCL, "task-category.gif"); 
    public static final ImageDescriptor CATEGORY_NEW = create(T_ELCL, "task-category-new.gif");
    public static final ImageDescriptor TASK_ACTIVE = create(T_ELCL, "complete_tsk.gif");
    public static final ImageDescriptor TASK_INACTIVE = create(T_ELCL, "incomplete_tsk.gif");
    
    public static final ImageDescriptor FILE_GENERIC = create(STRUCTURE, "file_obj.gif");
    public static final ImageDescriptor FOLDER_GENERIC = create(STRUCTURE, "fldr_obj.gif");
    public static final ImageDescriptor FILE_XML = create(STRUCTURE, "file-xml.gif");
    public static final ImageDescriptor LANDMARK = create(STRUCTURE, "landmark.gif"); 
//    public static final ImageDescriptor LANDMARK_SMALL = create("structure", "landmark-small.gif");
//    public static final ImageDescriptor LANDMARK_LARGE = create("structure", "landmark-large.gif");
    
	public static ImageDescriptor MYLAR = create(T_EVIEW, "mylar.gif");
	public static final ImageDescriptor PATHFINDER = create(T_EVIEW, "pathfinder.gif");
	public static ImageDescriptor OUTLINE = create(T_EVIEW, "outline.gif");
	public static ImageDescriptor OUTLINE_MYLAR = create(T_EVIEW, "outline-mylar.gif");
	public static ImageDescriptor PACKAGE_EXPLORER = create(T_EVIEW, "package-explorer.gif");
	public static ImageDescriptor PACKAGE_EXPLORER_MYLAR = create(T_EVIEW, "package-explorer-mylar.gif");
	public static ImageDescriptor SEARCH = create(T_EVIEW, "search.gif");
	public static ImageDescriptor SEARCH_MYLAR = create(T_EVIEW, "search-mylar.gif");
	public static ImageDescriptor PROBLEMS = create(T_EVIEW, "problems.gif");
	public static ImageDescriptor PROBLEMS_MYLAR = create(T_EVIEW, "problems-mylar.gif");

    public static ImageDescriptor RELATIONSHIPS_INHERITANCE_JAVA = create(RELATIONSHIPS, "inheritance-java.gif"); 
    public static ImageDescriptor RELATIONSHIPS_REFS_JAVA = create(RELATIONSHIPS, "refs-java.gif"); 
    public static ImageDescriptor RELATIONSHIPS_READ_JAVA = create(RELATIONSHIPS, "read-java.gif");
    public static ImageDescriptor RELATIONSHIPS_WRITE_JAVA = create(RELATIONSHIPS, "write-java.gif");
    public static ImageDescriptor RELATIONSHIPS_REFS_BUGZILLA = create(RELATIONSHIPS, "refs-bugzilla.gif"); 
    public static ImageDescriptor RELATIONSHIPS_REFS_XML = create(RELATIONSHIPS, "refs-xml.gif"); 
    public static ImageDescriptor RELATIONSHIPS_REFS_JUNIT = create(RELATIONSHIPS, "refs-junit.gif"); 
    
//	public static ImageDescriptor USAGE_KEYSTROKES = JavaPluginImages.DESC_OBJS_TEXT_EDIT;
//	public static ImageDescriptor USAGE_SELECTIONS = JavaPluginImages.DESC_OBJS_CUNIT;
//	public static ImageDescriptor USAGE_SELECTIONS_EDITOR = JavaPluginImages.DESC_OBJS_CUNIT;
//	public static ImageDescriptor USAGE_UNKNOWN = JavaPluginImages.DESC_OBJS_UNKNOWN;
    
    public static ImageDescriptor TEST_OVERLAY = create("", "warning_co.gif");
	
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
