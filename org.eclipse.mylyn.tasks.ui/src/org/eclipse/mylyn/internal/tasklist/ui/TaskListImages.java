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
/*
 * Created on Apr 20, 2004
 */
package org.eclipse.mylar.internal.tasklist.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

/**
 * @author Mik Kersten
 */
public class TaskListImages {

	private static ImageRegistry imageRegistry;

	public static final Color BACKGROUND_WHITE = new Color(Display.getDefault(), 255, 255, 255);

	public static final Color BACKGROUND_ARCHIVE = new Color(Display.getDefault(), 199, 199, 237);

	public static final Color GRAY_LIGHT = new Color(Display.getDefault(), 170, 170, 170);

	public static final Color COLOR_TASK_COMPLETED = new Color(Display.getDefault(), 170, 170, 170); 

	public static final Color COLOR_TASK_ACTIVE = new Color(Display.getDefault(), 36, 22, 50);

	public static final Color COLOR_TASK_OVERDUE = new Color(Display.getDefault(), 200, 10, 30);

	public static final Font BOLD = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);

	public static final Font ITALIC = JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT);

	private static final String T_ELCL = "elcl16";

	private static final String T_EVIEW = "eview16";
		
	private static final String T_TOOL = "etool16";
	
//	public static final Point SIZE_SMALL = new Point(16, 16);
	
	public static final Point SIZE_WIDE = new Point(20, 16);
	
	private static final URL baseURL = MylarTaskListPlugin.getDefault().getBundle().getEntry("/icons/");

	public static final ImageDescriptor TASKLIST = create("eview16", "task-list.gif");

	public static final ImageDescriptor REPOSITORY = create("eview16", "repository.gif");

	public static final ImageDescriptor REPOSITORY_NEW = create("etool16", "repository-new.gif");

	public static final ImageDescriptor REPOSITORIES = create("eview16", "repositories.gif");

	public static final ImageDescriptor REMOVE = create(T_ELCL, "remove.gif");

	public static final ImageDescriptor FILTER_COMPLETE = create(T_ELCL, "filter-complete.gif");

	public static final ImageDescriptor FILTER_PRIORITY = create(T_ELCL, "filter-priority.gif");

	public static final ImageDescriptor COLOR_PALETTE = create(T_ELCL, "color-palette.gif");

	public static final ImageDescriptor TASK = createSize(create(T_TOOL, "task.gif"), SIZE_WIDE);

	public static final ImageDescriptor TASK_NOTES = createSize(create(T_TOOL, "task-notes.gif"), SIZE_WIDE);
	
	public static final ImageDescriptor TASK_NEW = create(T_TOOL, "task-new.gif");

	public static final ImageDescriptor OVERLAY_WEB = create(T_TOOL, "overlay-web.gif");

	public static final ImageDescriptor TASK_WEB = createWithOverlay(TASK, OVERLAY_WEB, false, true);

	public static final ImageDescriptor TASK_WEB_REMOTE = create(T_TOOL, "overlay-web.gif");
		
	public static final ImageDescriptor CATEGORY = createSize(create(T_TOOL, "category.gif"), SIZE_WIDE);

	public static final ImageDescriptor CATEGORY_NEW = create(T_TOOL, "category-new.gif");

	public static final ImageDescriptor CATEGORY_ARCHIVE = createSize(create(T_TOOL, "category-archive.gif"), SIZE_WIDE);


	public static final ImageDescriptor TASK_REMOTE = createSize(create(T_TOOL, "task-remote.gif"), SIZE_WIDE);
	public static final ImageDescriptor TASK_REPOSITORY = create(T_TOOL, "task-repository.gif");

//	public static final ImageDescriptor TASK_REPOSITORY_NOTES = create(T_TOOL, "task-repository-notes.gif");
	
	public static final ImageDescriptor TASK_REPOSITORY_NEW = create(T_TOOL, "task-repository-new.gif");

	public static final ImageDescriptor OVERLAY_INCOMMING = create(T_EVIEW, "overlay-incoming.gif");

	public static final ImageDescriptor OVERLAY_OUTGOING = create(T_EVIEW, "overlay-outgoing.gif");

	public static final ImageDescriptor OVERLAY_CONFLICT = create(T_EVIEW, "overlay-conflicting.gif");

	public static final ImageDescriptor OVERLAY_REPOSITORY = create(T_EVIEW, "overlay-repository.gif");
	
	public static final ImageDescriptor TASK_REPOSITORY_INCOMMING = createWithOverlay(TASK_REPOSITORY,
			OVERLAY_INCOMMING, true, false);

	public static final ImageDescriptor TASK_REPOSITORY_CONFLICT = createWithOverlay(TASK_REPOSITORY, OVERLAY_CONFLICT,
			true, false);

	public static final ImageDescriptor TASK_REPOSITORY_OUTGOING = createWithOverlay(TASK_REPOSITORY, OVERLAY_OUTGOING,
			true, false);

	public static final ImageDescriptor QUERY = createSize(create(T_TOOL, "query.gif"), SIZE_WIDE);

	public static final ImageDescriptor QUERY_NEW = create(T_TOOL, "query-new.gif");

	public static final ImageDescriptor REPOSITORY_SYNCHRONIZE = create(T_TOOL, "repository-synchronize.gif");

	public static final ImageDescriptor NAVIGATE_PREVIOUS = create(T_TOOL, "navigate-previous.gif");

	public static final ImageDescriptor NAVIGATE_NEXT = create(T_TOOL, "navigate-next.gif");

	public static final ImageDescriptor COPY = create(T_TOOL, "copy.png");

	public static final ImageDescriptor GO_UP = create(T_TOOL, "go-up.gif");

	public static final ImageDescriptor GO_INTO = create(T_TOOL, "go-into.gif");

	public static final ImageDescriptor TASK_ACTIVE = createSize(create(T_TOOL, "task-active.gif"), SIZE_WIDE);

	public static final ImageDescriptor TASK_INACTIVE = createSize(create(T_TOOL, "task-inactive.gif"), SIZE_WIDE);

	public static final ImageDescriptor TASK_INACTIVE_CONTEXT = createSize(create(T_TOOL, "task-context.gif"), SIZE_WIDE);

	public static final ImageDescriptor TASK_COMPLETE = create(T_TOOL, "task-complete.gif");

	public static final ImageDescriptor TASK_INCOMPLETE = create(T_TOOL, "task-incomplete.gif");

	public static final ImageDescriptor COLLAPSE_ALL = create(T_ELCL, "collapseall.png");

	public static final ImageDescriptor PRIORITY_1 = createSize(create(T_EVIEW, "priority-1.gif"), SIZE_WIDE);
	
	public static final ImageDescriptor PRIORITY_2 = createSize(create(T_EVIEW, "priority-2.gif"), SIZE_WIDE);
	
	public static final ImageDescriptor PRIORITY_3 = createSize(create(T_EVIEW, "priority-3.gif"), SIZE_WIDE);
	
	public static final ImageDescriptor PRIORITY_4 = createSize(create(T_EVIEW, "priority-4.gif"), SIZE_WIDE);
	
	public static final ImageDescriptor PRIORITY_5 = createSize(create(T_EVIEW, "priority-5.gif"), SIZE_WIDE);
	
	public static final ImageDescriptor CALENDAR = create(T_EVIEW, "task-activity.gif");
		
	private static ImageDescriptor create(String prefix, String name) {
		try {
			return ImageDescriptor.createFromURL(makeIconFileURL(prefix, name));
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	private static ImageDescriptor createSize(ImageDescriptor base, Point size) {
		return new TaskListOverlayDescriptor(base, size);
	}
	
	private static ImageDescriptor createWithOverlay(ImageDescriptor base, ImageDescriptor overlay, boolean top,
			boolean left) {
		return new TaskListOverlayDescriptor(base, overlay, top, left);
	}

	private static URL makeIconFileURL(String prefix, String name) throws MalformedURLException {
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
		ImageRegistry imageRegistry = getImageRegistry();

		Image image = imageRegistry.get("" + imageDescriptor.hashCode());
		if (image == null) {
			image = imageDescriptor.createImage();
			imageRegistry.put("" + imageDescriptor.hashCode(), image);
		}
		return image;
	}
}

//class TaskImageDescriptor extends CompositeImageDescriptor {
//	  
//	private Point size;
//	
//	private ImageDescriptor contents;
//	
//	public TaskImageDescriptor(ImageDescriptor contents, Point size) {
//		this.contents = contents;
//	}
//
//	@Override
//	protected void drawCompositeImage(int width, int height) {
//		super.drawImage(contents.getImageData(), width, height);
//	}
//
//	@Override
//	protected Point getSize() {
//		return size;
//	}
//}
