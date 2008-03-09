/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Apr 20, 2004
 */
package org.eclipse.mylyn.internal.tasks.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.mylyn.tasks.core.AbstractTask.PriorityLevel;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

/**
 * NOTE: if you create your own image descriptors you are responsible for disposing them.
 * 
 * @author Mik Kersten
 */
public class TasksUiImages {

	private static ImageRegistry imageRegistry;

	private static final String T_ELCL = "elcl16";

	private static final String T_EVIEW = "eview16";

	private static final String T_TOOL = "etool16";

	private static final String T_OBJ = "obj16";

	private static final String T_WIZBAN = "wizban";

	private static final String T_OVR = "ovr16";

	private static final URL baseURL = TasksUiPlugin.getDefault().getBundle().getEntry("/icons/");

	public static final ImageDescriptor TASK_ACTIVE = create(T_TOOL, "task-active.gif");

	public static final ImageDescriptor TASK_ACTIVE_CENTERED = create(T_TOOL, "task-active-centered.gif");

	public static final ImageDescriptor TASK_INACTIVE = create(T_TOOL, "task-inactive.gif");

	public static final ImageDescriptor TASK_INACTIVE_CONTEXT = create(T_TOOL, "task-context.gif");

	public static final ImageDescriptor TASK_COMPLETE = create(T_TOOL, "task-complete.gif");

	public static final ImageDescriptor TASK_INCOMPLETE = create(T_TOOL, "task-incomplete.gif");

	public static final ImageDescriptor TASK = create(T_TOOL, "task.gif");

	public static final ImageDescriptor TASK_COMPLETED = create(T_TOOL, "task-completed.gif");

	public static final ImageDescriptor TASK_NOTES = create(T_TOOL, "task-notes.gif");

	public static final ImageDescriptor TASK_NEW = create(T_TOOL, "task-new.gif");

	public static final ImageDescriptor TASK_REPOSITORY_HISTORY = create(T_TOOL, "task-repository-history.gif");

	public static final ImageDescriptor TASK_REPOSITORY_NOTES = create(T_TOOL, "task-repository-notes.gif");

	public static final ImageDescriptor TASK_REPOSITORY_COMPLETED = create(T_TOOL, "task-repository-completed.gif");

	public static final ImageDescriptor TASK_REMOTE = create(T_TOOL, "task-remote.gif");

	public static final ImageDescriptor TASK_WORKING_SET = create(T_TOOL, "open-task.gif");

	public static final ImageDescriptor SORT_COMMENT_DOWN = create(T_TOOL, "sort-down.gif");

	public static final ImageDescriptor SORT_COMMENT_UP = create(T_TOOL, "sort-up.gif");

	public static final ImageDescriptor SORT_COMMENT_DOWN_GRAY = create(T_TOOL, "sort-down-gray.gif");

	public static final ImageDescriptor SORT_COMMENT_UP_GRAY = create(T_TOOL, "sort-up-gray.gif");

	public static final ImageDescriptor TASKLIST = create("eview16", "task-list.gif");

	public static final ImageDescriptor REPOSITORY = create("eview16", "repository.gif");

	public static final ImageDescriptor REPOSITORY_OFFLINE = ImageDescriptor.createWithFlags(TasksUiImages.REPOSITORY,
			SWT.IMAGE_GRAY);

	public static final ImageDescriptor REPOSITORY_SUBMIT = create(T_TOOL, "repository-submit.gif");

	public static final ImageDescriptor REPOSITORY_SMALL = create(T_OBJ, "repository-small.gif");

	public static final ImageDescriptor REPOSITORY_NEW = create("etool16", "repository-new.gif");

	public static final ImageDescriptor REPOSITORIES = create("eview16", "repositories.gif");

	public static final ImageDescriptor REMOVE = create(T_ELCL, "remove.gif");

	public static final ImageDescriptor DELETE = create(T_ELCL, "delete.gif");

	public static final ImageDescriptor FILTER_COMPLETE = create(T_ELCL, "filter-complete.gif");

	public static final ImageDescriptor FILTER_ARCHIVE = create(T_ELCL, "filter-archive.gif");

	public static final ImageDescriptor FILTER_PRIORITY = create(T_ELCL, "filter-priority.gif");

	public static final ImageDescriptor COLOR_PALETTE = create(T_ELCL, "color-palette.gif");

	public static final ImageDescriptor FILTER = create(T_TOOL, "view-filter.gif");

	public static final ImageDescriptor FIND_CLEAR = create(T_TOOL, "find-clear.gif");

	public static final ImageDescriptor FIND_CLEAR_DISABLED = create(T_TOOL, "find-clear-disabled.gif");

	public static final ImageDescriptor SCHEDULE_DAY = create(T_TOOL, "schedule-day.png");

	public static final ImageDescriptor SCHEDULE_WEEK = create(T_TOOL, "schedule-week.png");

	public static final ImageDescriptor WARNING = create(T_ELCL, "warning.gif");

	public static final ImageDescriptor OVERLAY_WEB = create(T_TOOL, "overlay-web.gif");

	public static final ImageDescriptor OVERLAY_OFFLINE = create(T_EVIEW, "overlay-offline.gif");

	public static final ImageDescriptor BROWSER_SMALL = create(T_OBJ, "browser-small.gif");

	public static final ImageDescriptor BROWSER_OPEN_TASK = create(T_TOOL, "open-browser.gif");

	public static final ImageDescriptor OVERLAY_SYNCHRONIZING = create(T_EVIEW, "overlay-synchronizing.gif");

	public static final ImageDescriptor OVERLAY_HAS_DUE = create(T_EVIEW, "overlay-has-due.gif");

	public static final ImageDescriptor OVERLAY_OVER_DUE = create(T_EVIEW, "overlay-overdue.gif");

	public static final ImageDescriptor OVERLAY_SOLID_WHITE = create(T_OVR, "solid-white.gif");

	public static final ImageDescriptor TASK_WEB_REMOTE = create(T_TOOL, "overlay-web.gif");

	public static final ImageDescriptor CATEGORY = create(T_TOOL, "category.gif");

	public static final ImageDescriptor CATEGORY_NEW = create(T_TOOL, "category-new.gif");

	public static final ImageDescriptor CATEGORY_ARCHIVE = create(T_TOOL, "category-archive.gif");

	public static final ImageDescriptor QUERY_UNMATCHED = create(T_TOOL, "query-unmatched.png");

	public static final ImageDescriptor TASK_REPOSITORY = create(T_TOOL, "task-repository.gif");

	public static final ImageDescriptor TASK_REPOSITORY_NEW = create(T_TOOL, "task-repository-new.gif");

	public static final ImageDescriptor TOOLBAR_ARROW_RIGHT = create(T_TOOL, "toolbar-arrow-right.gif");

	public static final ImageDescriptor TOOLBAR_ARROW_DOWN = create(T_TOOL, "toolbar-arrow-down.gif");

	public static final ImageDescriptor COMMENT = create(T_TOOL, "comment.gif");

	public static final ImageDescriptor LINK_EDITOR = create(T_TOOL, "link-editor.gif");

	public static final ImageDescriptor PERSON = create(T_TOOL, "person.gif");

	public static final ImageDescriptor PERSON_NARROW = create(T_TOOL, "person-narrow.gif");

	public static final ImageDescriptor PERSON_ME = create(T_TOOL, "person-me.gif");

	public static final ImageDescriptor PERSON_ME_NARROW = create(T_TOOL, "person-me-narrow.gif");

	public static final ImageDescriptor CONTEXT_FOCUS = create(T_EVIEW, "focus-view.gif");

	public static final ImageDescriptor CONTEXT_ATTACH = create(T_TOOL, "context-attach.gif");

	public static final ImageDescriptor CONTEXT_RETRIEVE = create(T_TOOL, "context-retrieve.gif");

	public static final ImageDescriptor CONTEXT_TRANSFER = create(T_TOOL, "context-transfer.gif");

	public static final ImageDescriptor CONTEXT_CLEAR = create(T_TOOL, "context-clear.gif");

	public static final ImageDescriptor CLEAR = create(T_TOOL, "clear.gif");

	public static final ImageDescriptor EDIT = create(T_TOOL, "edit.gif");

	public static final ImageDescriptor CUT = create(T_TOOL, "cut.gif");

	public static final ImageDescriptor ATTACHMENT_PATCH = create(T_OBJ, "attachment-patch.gif");

	public static final ImageDescriptor TASK_RETRIEVE = create(T_TOOL, "task-retrieve.gif");

	public static final ImageDescriptor OVERLAY_LOCAL_TASK = create(T_OVR, "overlay-local-task.gif");

	public static final ImageDescriptor OVERLAY_INCOMMING = create(T_EVIEW, "overlay-incoming.gif");

	public static final ImageDescriptor OVERLAY_INCOMMING_NEW = create(T_EVIEW, "overlay-incoming-new.gif");

	public static final ImageDescriptor OVERLAY_OUTGOING = create(T_EVIEW, "overlay-outgoing.gif");

	public static final ImageDescriptor OVERLAY_SYNCH_INCOMMING = create(T_EVIEW, "overlay-synch-incoming.gif");

	public static final ImageDescriptor OVERLAY_SYNCH_INCOMMING_NEW = create(T_EVIEW, "overlay-synch-incoming-new.gif");

	public static final ImageDescriptor OVERLAY_SYNCH_OUTGOING = create(T_EVIEW, "overlay-synch-outgoing.gif");

	public static final ImageDescriptor OVERLAY_CONFLICT = create(T_EVIEW, "overlay-conflicting.gif");

	public static final ImageDescriptor OVERLAY_REPOSITORY = create(T_EVIEW, "overlay-repository.gif");

	public static final ImageDescriptor OVERLAY_REPOSITORY_CONTEXT = create(T_EVIEW, "overlay-repository-context.gif");

	public static final ImageDescriptor OVERLAY_WARNING = create(T_OVR, "overlay-warning.gif");

	public static final ImageDescriptor OVERLAY_BLANK = create(T_OVR, "overlay-blank.gif");

	public static final ImageDescriptor STATUS_NORMAL = create(T_EVIEW, "status-normal.gif");

	public static final ImageDescriptor STATUS_CONTEXT = create(T_EVIEW, "status-server-context.gif");

	public static final ImageDescriptor QUERY = create(T_TOOL, "query.gif");

	public static final ImageDescriptor QUERY_NEW = create(T_TOOL, "query-new.gif");

	public static final ImageDescriptor REPOSITORY_SYNCHRONIZE = create(T_TOOL, "repository-synchronize.gif");

	public static final ImageDescriptor NAVIGATE_PREVIOUS = create(T_TOOL, "navigate-previous.gif");

	public static final ImageDescriptor NAVIGATE_PREVIOUS_PAUSE = create(T_TOOL, "navigate-previous-pause.gif");

	public static final ImageDescriptor NAVIGATE_PREVIOUS_ACTIVE = create(T_TOOL, "navigate-previous-active.gif");

	public static final ImageDescriptor CAPTURE_PAUSE = create(T_TOOL, "capture-pause.gif");

	public static final ImageDescriptor NAVIGATE_NEXT = create(T_TOOL, "navigate-next.gif");

	public static final ImageDescriptor TASKLIST_MODE = create(T_TOOL, "presentation.gif");

	public static final ImageDescriptor TASK_GROUPING = create(T_TOOL, "grouping.gif");

	public static final ImageDescriptor COPY = create(T_TOOL, "copy.png");

	public static final ImageDescriptor GO_UP = create(T_TOOL, "go-up.gif");

	public static final ImageDescriptor GO_INTO = create(T_TOOL, "go-into.gif");

	public static final ImageDescriptor REFRESH = create(T_ELCL, "refresh.gif");

	public static final ImageDescriptor REFRESH_SMALL = create(T_ELCL, "refresh-small.gif");

	public static final ImageDescriptor COLLAPSE_ALL = create(T_ELCL, "collapseall.png");

	public static final ImageDescriptor NOTIFICATION_CLOSE = create(T_EVIEW, "notification-close.gif");

	public static final ImageDescriptor NOTIFICATION_CLOSE_ACTIVE = create(T_EVIEW, "notification-close-active.gif");

	public static final ImageDescriptor EXPAND_ALL = create(T_ELCL, "expandall.gif");

	public static final ImageDescriptor REPLY = create(T_ELCL, "reply.gif");

	public static final ImageDescriptor PRIORITY_1 = create(T_EVIEW, "priority-1.gif");

	public static final ImageDescriptor PRIORITY_2 = create(T_EVIEW, "priority-2.gif");

	public static final ImageDescriptor PRIORITY_3 = create(T_EVIEW, "priority-3.gif");

	public static final ImageDescriptor PRIORITY_4 = create(T_EVIEW, "priority-4.gif");

	public static final ImageDescriptor PRIORITY_5 = create(T_EVIEW, "priority-5.gif");

	public static final ImageDescriptor CALENDAR = create(T_TOOL, "calendar.gif");

	public static final ImageDescriptor CALENDAR_SMALL = create(T_OBJ, "calendar-small.gif");

	public static final ImageDescriptor BANNER_REPOSITORY = create(T_WIZBAN, "banner-repository.gif");

	public static final ImageDescriptor BANNER_SCREENSHOT = create(T_WIZBAN, "banner-screenshot.png");

	public static final ImageDescriptor BANNER_WORKING_SET = create(T_WIZBAN, "workset_wiz.png");

	public static final ImageDescriptor BANNER_REPOSITORY_SETTINGS = create(T_WIZBAN, "banner-repository-settings.gif");

	public static final ImageDescriptor BANNER_REPOSITORY_CONTEXT = create(T_WIZBAN, "banner-repository-context.gif");

	public static final ImageDescriptor BANNER_IMPORT = create(T_WIZBAN, "banner-import.gif");

	public static final ImageDescriptor BLANK_TINY = create(T_ELCL, "clearDot.gif");

	public static final ImageDescriptor BLANK = create(T_ELCL, "blank.gif");

	public static final ImageDescriptor IMAGE_CAPTURE = create(T_TOOL, "capture-screen.png");

	public static final ImageDescriptor IMAGE_FIT = create(T_TOOL, "capture-fit.png");

	public static final ImageDescriptor IMAGE_FILE = create(T_OBJ, "file-image.gif");

	public static final ImageDescriptor NEW_SUB_TASK = create(T_TOOL, "sub-task-new.gif");

	public static final ImageDescriptor QUESTION = create(T_OBJ, "question.gif");

	public static final ImageDescriptor CONTENT_ASSIST_SEPARATOR = create(T_TOOL, "content-assist-separator.gif");

	private static ImageDescriptor create(String prefix, String name) {
		try {
			return ImageDescriptor.createFromURL(makeIconFileURL(prefix, name));
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	/**
	 * Lazily initializes image map.
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
			CompositeTaskImageDescriptor imageDescriptor = new CompositeTaskImageDescriptor(icon, overlayKind, wide);
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
					OVERLAY_BLANK, wide);
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
			CompositeSynchImageDescriptor imageDescriptor = new CompositeSynchImageDescriptor(icon, background);
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

	public static Image getImageForPriority(PriorityLevel priorityLevel) {
		if (priorityLevel == null) {
			return null;
		} else {
			ImageDescriptor imageDescriptor = getImageDescriptorForPriority(priorityLevel);
			if (imageDescriptor != null) {
				return TasksUiImages.getImage(imageDescriptor);
			}
		}
		return null;
	}

	public static ImageDescriptor getImageDescriptorForPriority(PriorityLevel priorityLevel) {
		if (priorityLevel == null) {
			return null;
		}
		switch (priorityLevel) {
		case P1:
			return TasksUiImages.PRIORITY_1;
		case P2:
			return TasksUiImages.PRIORITY_2;
		case P3:
			return TasksUiImages.PRIORITY_3;
		case P4:
			return TasksUiImages.PRIORITY_4;
		case P5:
			return TasksUiImages.PRIORITY_5;
		default:
			return null;
		}
	}

}
