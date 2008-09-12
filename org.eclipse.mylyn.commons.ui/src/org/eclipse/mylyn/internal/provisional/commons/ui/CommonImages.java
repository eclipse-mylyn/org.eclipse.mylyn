/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.mylyn.internal.commons.ui.CommonsUiPlugin;
import org.eclipse.mylyn.internal.commons.ui.CompositeContainerImageDescriptor;
import org.eclipse.mylyn.internal.commons.ui.CompositeElementImageDescriptor;
import org.eclipse.mylyn.internal.commons.ui.CompositeSyncImageDescriptor;
import org.eclipse.mylyn.internal.commons.ui.TaskListImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 * @since 3.0
 */
public class CommonImages {

	private static final URL baseURL = CommonsUiPlugin.getDefault().getBundle().getEntry("/icons/");

	private static ImageRegistry imageRegistry;

	private static final String T_ELCL = "elcl16";

	private static final String T_EVIEW = "eview16";

	private static final String T_TOOL = "etool16";

	private static final String T_OBJ = "obj16";

	private static final String T_WIZBAN = "wizban";

	private static final String T_OVR = "ovr16";

	// Priorities

	public static final ImageDescriptor PRIORITY_1 = create(T_OBJ, "priority-1.gif");

	public static final ImageDescriptor PRIORITY_2 = create(T_OBJ, "priority-2.gif");

	public static final ImageDescriptor PRIORITY_3 = create(T_OBJ, "priority-3.gif");

	public static final ImageDescriptor PRIORITY_4 = create(T_OBJ, "priority-4.gif");

	public static final ImageDescriptor PRIORITY_5 = create(T_OBJ, "priority-5.gif");

	// 	Calendars, people and notifications

	public static final ImageDescriptor CALENDAR = create(T_TOOL, "calendar.gif");

	public static final ImageDescriptor CALENDAR_SMALL = create(T_OBJ, "calendar-small.gif");

	public static final ImageDescriptor SCHEDULE_DAY = create(T_TOOL, "schedule-day.png");

	public static final ImageDescriptor SCHEDULE_WEEK = create(T_TOOL, "schedule-week.png");

	public static final ImageDescriptor PERSON = create(T_TOOL, "person.gif");

	public static final ImageDescriptor PERSON_NARROW = create(T_TOOL, "person-narrow.gif");

	public static final ImageDescriptor PERSON_ME = create(T_TOOL, "person-me.gif");

	public static final ImageDescriptor PERSON_ME_NARROW = create(T_TOOL, "person-me-narrow.gif");

	public static final ImageDescriptor NOTIFICATION_CLOSE = create(T_EVIEW, "notification-close.gif");

	public static final ImageDescriptor NOTIFICATION_CLOSE_HOVER = create(T_EVIEW, "notification-close-active.gif");

	// Date and synchronization overlays

	public static final ImageDescriptor OVERLAY_DATE_DUE = create(T_EVIEW, "overlay-has-due.gif");

	public static final ImageDescriptor OVERLAY_DATE_OVERDUE = create(T_EVIEW, "overlay-overdue.gif");

	public static final ImageDescriptor OVERLAY_SYNC_IN_PROGRESS = create(T_EVIEW, "overlay-synchronizing.gif");

	public static final ImageDescriptor OVERLAY_SYNC_INCOMMING = create(T_EVIEW, "overlay-incoming.gif");

	public static final ImageDescriptor OVERLAY_SYNC_INCOMMING_NEW = create(T_EVIEW, "overlay-incoming-new.gif");

	public static final ImageDescriptor OVERLAY_SYNC_OUTGOING = create(T_EVIEW, "overlay-outgoing.gif");

	public static final ImageDescriptor OVERLAY_SYNC_OUTGOING_NEW = create(T_EVIEW, "overlay-outgoing-new.gif");

	public static final ImageDescriptor OVERLAY_SYNC_CONFLICT = create(T_EVIEW, "overlay-conflict.gif");

	public static final ImageDescriptor OVERLAY_SYNC_WARNING = create(T_OVR, "overlay-warning.gif");

	public static final ImageDescriptor OVERLAY_SYNC_OLD_INCOMMING = create(T_EVIEW, "overlay-synch-incoming.gif");

	public static final ImageDescriptor OVERLAY_SYNC_OLD_INCOMMING_NEW = create(T_EVIEW,
			"overlay-synch-incoming-new.gif");

	public static final ImageDescriptor OVERLAY_SYNC_OLD_OUTGOING = create(T_EVIEW, "overlay-synch-outgoing.gif");

	public static final ImageDescriptor OVERLAY_CLEAR = create(T_OVR, "overlay-blank.gif");

	public static final ImageDescriptor OVERLAY_WHITE = create(T_OVR, "solid-white.gif");

	// Wizard banners

	public static final ImageDescriptor BANNER_SCREENSHOT = create(T_WIZBAN, "banner-screenshot.png");

	public static final ImageDescriptor BANNER_IMPORT = create(T_WIZBAN, "banner-import.gif");

	// Miscellaneous
	// TODO: some of the common images below come from the workbench

	public static final ImageDescriptor COMPLETE = create(T_OBJ, "complete.gif");

	public static final ImageDescriptor REMOVE = create(T_ELCL, "remove.gif");

	public static final ImageDescriptor DELETE = create(T_ELCL, "delete.gif");

	public static final ImageDescriptor WARNING = create(T_ELCL, "warning.gif");

	public static final ImageDescriptor FILTER_COMPLETE = create(T_ELCL, "filter-complete.gif");

	public static final ImageDescriptor FILTER_ARCHIVE = create(T_ELCL, "filter-archive.gif");

	public static final ImageDescriptor FILTER_PRIORITY = create(T_ELCL, "filter-priority.gif");

	public static final ImageDescriptor COLOR_PALETTE = create(T_ELCL, "color-palette.gif");

	public static final ImageDescriptor FILTER = create(T_TOOL, "view-filter.gif");

	public static final ImageDescriptor FIND_CLEAR = create(T_TOOL, "find-clear.gif");

	public static final ImageDescriptor FIND_CLEAR_DISABLED = create(T_TOOL, "find-clear-disabled.gif");

	public static final ImageDescriptor BROWSER_SMALL = create(T_OBJ, "browser-small.gif");

	public static final ImageDescriptor BROWSER_OPEN_TASK = create(T_TOOL, "open-browser.gif");

	public static final ImageDescriptor TOOLBAR_ARROW_RIGHT = create(T_TOOL, "toolbar-arrow-right.gif");

	public static final ImageDescriptor TOOLBAR_ARROW_DOWN = create(T_TOOL, "toolbar-arrow-down.gif");

	public static final ImageDescriptor LINK_EDITOR = create(T_TOOL, "link-editor.gif");

	public static final ImageDescriptor CLEAR = create(T_TOOL, "clear.gif");

	public static final ImageDescriptor EDIT = create(T_TOOL, "edit.gif");

	public static final ImageDescriptor CUT = create(T_TOOL, "cut.gif");

	public static final ImageDescriptor STATUS_NORMAL = create(T_EVIEW, "status-normal.gif");

	public static final ImageDescriptor STATUS_CONTEXT = create(T_EVIEW, "status-server-context.gif");

	public static final ImageDescriptor PRESENTATION = create(T_TOOL, "presentation.gif");

	public static final ImageDescriptor GROUPING = create(T_TOOL, "grouping.gif");

	public static final ImageDescriptor COPY = create(T_TOOL, "copy.png");

	public static final ImageDescriptor GO_UP = create(T_TOOL, "go-up.gif");

	public static final ImageDescriptor GO_INTO = create(T_TOOL, "go-into.gif");

	public static final ImageDescriptor REFRESH = create(T_ELCL, "refresh.gif");

	public static final ImageDescriptor REFRESH_SMALL = create(T_ELCL, "refresh-small.gif");

	public static final ImageDescriptor COLLAPSE_ALL = create(T_ELCL, "collapseall.png");

	public static final ImageDescriptor COLLAPSE_ALL_SMALL = create(T_ELCL, "collapseall-small.png");

	public static final ImageDescriptor EXPAND_ALL = create(T_ELCL, "expandall.gif");

	public static final ImageDescriptor EXPAND_ALL_SMALL = create(T_ELCL, "expandall-small.png");

	public static final ImageDescriptor BLANK = create(T_ELCL, "blank.gif");

	public static final ImageDescriptor IMAGE_CAPTURE = create(T_TOOL, "capture-screen.png");

	public static final ImageDescriptor IMAGE_FIT = create(T_TOOL, "capture-fit.png");

	public static final ImageDescriptor IMAGE_FILE = create(T_OBJ, "file-image.gif");

	public static final ImageDescriptor QUESTION = create(T_OBJ, "question.gif");

	public static final ImageDescriptor SEPARATOR_LIST = create(T_TOOL, "content-assist-separator.gif");

	public static final ImageDescriptor PART_MAXIMIZE = create(T_TOOL, "maximize.png");

	public static final ImageDescriptor PREVIEW_WEB = create(T_TOOL, "preview-web.png");

	public static final ImageDescriptor FIND = create(T_TOOL, "find.gif");

	private static ImageDescriptor create(String prefix, String name) {
		try {
			return ImageDescriptor.createFromURL(makeIconFileURL(prefix, name));
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	/**
	 * Lazily initializes image map.
	 * 
	 * @param imageDescriptor
	 * @return Image
	 */
	public static Image getImage(ImageDescriptor imageDescriptor) {
		ImageRegistry imageRegistry = getImageRegistry();
		Image image = imageRegistry.get("" + imageDescriptor.hashCode());
		if (image == null) {
			image = imageDescriptor.createImage(true);
			imageRegistry.put("" + imageDescriptor.hashCode(), image);
		}
		return image;
	}

	public static Image getImageWithOverlay(ImageDescriptor icon, ImageDescriptor overlay, boolean top, boolean left) {
		if (icon == null) {
			return null;
		}
		String key = "" + icon.hashCode();
		if (overlay != null) {
			key += overlay.hashCode();
		}
		key += new Boolean(top).hashCode();
		key += new Boolean(left).hashCode();

		Image image = getImageRegistry().get(key);

		if (image == null) {
			TaskListImageDescriptor imageDescriptor = new TaskListImageDescriptor(icon, overlay, top, left);
			image = imageDescriptor.createImage(true);
			getImageRegistry().put(key, image);
		}
		return image;
	}

	/**
	 * Lazily initializes image map.
	 * 
	 * @param icon
	 *            cannot be null
	 * @param overlayKind
	 * @param wide
	 * @return Image
	 */
	public static Image getCompositeTaskImage(ImageDescriptor icon, ImageDescriptor overlayKind, boolean wide) {
		if (icon == null) {
			return null;
		}
		String key = "" + icon.hashCode();
		if (overlayKind != null) {
			key += overlayKind.hashCode();
		}
		if (wide) {
			key += ".wide";
		}
		Image image = getImageRegistry().get(key);

		if (image == null) {
			CompositeElementImageDescriptor imageDescriptor = new CompositeElementImageDescriptor(icon, overlayKind,
					wide);
			image = imageDescriptor.createImage(true);
			getImageRegistry().put(key, image);
		}
		return image;
	}

	public static Image getCompositeContainerImage(ImageDescriptor icon, boolean wide) {
		if (icon == null) {
			return null;
		}
		String key = "" + icon.hashCode();
		if (wide) {
			key += ".wide";
		}
		Image image = getImageRegistry().get(key);
		if (image == null) {
			CompositeContainerImageDescriptor imageDescriptor = new CompositeContainerImageDescriptor(icon,
					OVERLAY_CLEAR, wide);
			image = imageDescriptor.createImage(true);
			getImageRegistry().put(key, image);
		}
		return image;
	}

	public static Image getCompositeSynchImage(ImageDescriptor icon, boolean background) {
		String key = "" + icon.hashCode();
		if (background) {
			key += ".background";
		}

		Image image = getImageRegistry().get(key);
		if (image == null) {
			CompositeSyncImageDescriptor imageDescriptor = new CompositeSyncImageDescriptor(icon, background);
			image = imageDescriptor.createImage(true);
			getImageRegistry().put(key, image);
		}
		return image;
	}

	private static ImageRegistry getImageRegistry() {
		if (imageRegistry == null) {
			imageRegistry = new ImageRegistry();
		}

		return imageRegistry;
	}

	private static URL makeIconFileURL(String prefix, String name) throws MalformedURLException {
		if (baseURL == null) {
			throw new MalformedURLException();
		}

		StringBuffer buffer = new StringBuffer(prefix);
		buffer.append('/');
		buffer.append(name);
		return new URL(baseURL, buffer.toString());
	}

	private static Image[] progressImages;

	public static Image[] getProgressImages() {

		if (progressImages != null) {
			return progressImages;
		}

		progressImages = new Image[8];

		for (int i = 1; i <= 8; i++) {
			ImageDescriptor imageDescriptor = create(T_EVIEW + "/progress", i + ".png");
			progressImages[i - 1] = getImage(imageDescriptor);
		}

		return progressImages;

	}
}
