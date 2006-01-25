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

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.Assert;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

/**
 * @author Mik Kersten
 */
public class TaskListImages {

	private static ImageRegistry imageRegistry;

	public static final Color BACKGROUND_WHITE = new Color(Display.getDefault(), 255, 255, 255);

	public static final Color BACKGROUND_ARCHIVE = new Color(Display.getDefault(), 190, 210, 238);

	public static final Color GRAY_LIGHT = new Color(Display.getDefault(), 170, 170, 170); // TODO:

	// use
	// theme?

	public static final Color COLOR_TASK_COMPLETED = new Color(Display.getDefault(), 170, 170, 170); // TODO:

	// use
	// theme?

	public static final Color COLOR_TASK_ACTIVE = new Color(Display.getDefault(), 36, 22, 50);

	public static final Color COLOR_TASK_OVERDUE = new Color(Display.getDefault(), 200, 10, 30);

	public static final Font BOLD = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);

	public static final Font ITALIC = JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT);

	private static final String T_ELCL = "elcl16";

	private static final String T_TOOL = "etool16";

	private static final URL baseURL = MylarTaskListPlugin.getDefault().getBundle().getEntry("/icons/");

	public static final ImageDescriptor TASKLIST = create("eview16", "task-list.gif");

	public static final ImageDescriptor REPOSITORY = create("eview16", "task-repository.gif");

	public static final ImageDescriptor REPOSITORY_NEW = create("etool16", "task-repository-new.gif");

	public static final ImageDescriptor REPOSITORIES = create("eview16", "task-repositories.gif");

	public static final ImageDescriptor REMOVE = create(T_ELCL, "remove.gif");

	public static final ImageDescriptor FILTER_COMPLETE = create(T_ELCL, "filter-complete.gif");

	public static final ImageDescriptor FILTER_PRIORITY = create(T_ELCL, "filter-priority.gif");

	public static final ImageDescriptor COLOR_PALETTE = create(T_ELCL, "color-palette.gif");

	public static final ImageDescriptor TASK2 = create(T_TOOL, "task.gif");

	public static final ImageDescriptor TASK = createWithOverlay(TASK2, null, true, false);

	public static final ImageDescriptor WEB_OVERLAY = create(T_TOOL, "overlay-web.gif");

	public static final ImageDescriptor TASK_WEB = createWithOverlay(TASK2, WEB_OVERLAY, false, true);

	public static final ImageDescriptor TASK_NEW = create(T_TOOL, "task-new.gif");

	public static final ImageDescriptor CATEGORY = create(T_TOOL, "category.gif");

	public static final ImageDescriptor CATEGORY_NEW = create(T_TOOL, "category-new.gif");

	public static final ImageDescriptor CATEGORY_ARCHIVE = create(T_TOOL, "category-archive.gif");

	public static final ImageDescriptor NAVIGATE_PREVIOUS = create(T_TOOL, "navigate-previous.gif");

	public static final ImageDescriptor NAVIGATE_NEXT = create(T_TOOL, "navigate-next.gif");

	public static final ImageDescriptor COPY = create(T_TOOL, "copy.png");

	public static final ImageDescriptor GO_UP = create(T_TOOL, "go-up.gif");

	public static final ImageDescriptor GO_INTO = create(T_TOOL, "go-into.gif");

	public static final ImageDescriptor TASK_ACTIVE = create(T_TOOL, "task-active.gif");

	public static final ImageDescriptor TASK_INACTIVE = create(T_TOOL, "task-inactive.gif");

	public static final ImageDescriptor TASK_INACTIVE_CONTEXT = create(T_TOOL, "task-context.gif");

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

	private static ImageDescriptor createWithOverlay(ImageDescriptor base, ImageDescriptor overlay, boolean top,
			boolean left) {
		return new MylarTasklistOverlayDescriptor(base, overlay, top, left);
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

	// /**
	// * TODO: get rid of this
	// */
	// public static ImageDescriptor getImageDescriptor(Image image) {
	// for (ImageDescriptor imageDescriptor : imageMap.keySet()) {
	// if (image.equals(imageMap.get(imageDescriptor))) return imageDescriptor;
	// }
	// return null;
	// }

	public static class MylarTasklistOverlayDescriptor extends CompositeImageDescriptor {

		private ImageData base;

		private ImageData overlay;

		private Point fSize;

		private boolean top;

		private boolean left;

		public MylarTasklistOverlayDescriptor(ImageDescriptor baseDesc, ImageDescriptor overlayDesc, boolean top,
				boolean left) {
			this.base = getImageData(baseDesc);
			this.top = top;
			this.left = left;
			if (overlayDesc != null)
				this.overlay = getImageData(overlayDesc);
			Point size = new Point(base.width, base.height);
			setImageSize(size);
		}

		@Override
		protected void drawCompositeImage(int width, int height) {
			drawImage(base, 0, 0);
			int x = 0;
			int y = 0;
			if (!left)
				x = 8;// base.width - overlay.width;
			if (!top)
				y = 8;// base.height - overlay.height;
			if (overlay != null) {
				drawImage(overlay, x, y);
			}
		}

		private ImageData getImageData(ImageDescriptor descriptor) {
			ImageData data = descriptor.getImageData(); // see bug 51965:
			// getImageData can
			// return null
			if (data == null) {
				data = DEFAULT_IMAGE_DATA;
			}
			return data;
		}

		/**
		 * Sets the size of the image created by calling
		 * <code>createImage()</code>.
		 * 
		 * @param size
		 *            the size of the image returned from calling
		 *            <code>createImage()</code>
		 * @see ImageDescriptor#createImage()
		 */
		public void setImageSize(Point size) {
			Assert.isNotNull(size);
			Assert.isTrue(size.x >= 0 && size.y >= 0);
			fSize = size;
		}

		@Override
		protected Point getSize() {
			return new Point(fSize.x, fSize.y);
		}

	}

}
