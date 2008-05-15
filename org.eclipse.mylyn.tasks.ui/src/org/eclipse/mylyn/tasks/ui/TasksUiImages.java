/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 * @since 3.0
 */
public class TasksUiImages {

	private static final URL baseURL = TasksUiPlugin.getDefault().getBundle().getEntry("/icons/");

	private static ImageRegistry imageRegistry;

	private static final String VIEW = "eview16";

	private static final String TOOL = "etool16";

	public static final String OBJ = "obj16";

	private static final String OVR = "ovr16";

	private static final String WIZBAN = "wizban";

	// Tasks and Task List elements

	public static final ImageDescriptor TASK = create(TOOL, "task.gif");

	public static final ImageDescriptor TASK_COMPLETE = create(TOOL, "task-complete.gif");

	public static final ImageDescriptor TASK_INCOMPLETE = create(TOOL, "task-incomplete.gif");

	public static final ImageDescriptor TASK_COMPLETED = create(TOOL, "task-completed.gif");

	public static final ImageDescriptor TASK_NOTES = create(TOOL, "task-notes.gif");

	public static final ImageDescriptor TASK_NEW = create(TOOL, "task-new.gif");

	public static final ImageDescriptor TASK_NEW_SUB = create(TOOL, "sub-task-new.gif");

	public static final ImageDescriptor TASK_REPOSITORY_HISTORY = create(TOOL, "task-repository-history.gif");

	public static final ImageDescriptor TASK_REMOTE = create(TOOL, "task-remote.gif");

	public static final ImageDescriptor TASK_WORKING_SET = create(TOOL, "open-task.gif");

	public static final ImageDescriptor TASKS_VIEW = create("eview16", "task-list.gif");

	public static final ImageDescriptor TASK_ATTACHMENT_PATCH = create(OBJ, "attachment-patch.gif");

	public static final ImageDescriptor TASK_RETRIEVE = create(TOOL, "task-retrieve.gif");

	public static final ImageDescriptor TASK_REPOSITORY = create(TOOL, "task-repository.gif");

	public static final ImageDescriptor TASK_REPOSITORY_NEW = create(TOOL, "task-repository-new.gif");

	public static final ImageDescriptor CATEGORY = create(TOOL, "category.gif");

	public static final ImageDescriptor CATEGORY_NEW = create(TOOL, "category-new.gif");

	public static final ImageDescriptor CATEGORY_ARCHIVE = create(TOOL, "category-archive.gif");

	public static final ImageDescriptor QUERY = create(TOOL, "query.gif");

	public static final ImageDescriptor QUERY_NEW = create(TOOL, "query-new.gif");

	public static final ImageDescriptor QUERY_UNMATCHED = create(TOOL, "query-unmatched.png");

	public static final ImageDescriptor REPOSITORY = create("eview16", "repository.gif");

	public static final ImageDescriptor REPOSITORY_OFFLINE = ImageDescriptor.createWithFlags(TasksUiImages.REPOSITORY,
			SWT.IMAGE_GRAY);

	public static final ImageDescriptor REPOSITORY_SYNCHRONIZE = create(TOOL, "repository-synchronize.gif");

	public static final ImageDescriptor REPOSITORY_SUBMIT = create(TOOL, "repository-submit.gif");

	public static final ImageDescriptor REPOSITORY_SMALL = create(OBJ, "repository-small.gif");

	public static final ImageDescriptor REPOSITORY_NEW = create("etool16", "repository-new.gif");

	public static final ImageDescriptor REPOSITORIES_VIEW = create("eview16", "repositories.gif");

	// Context and activation

	public static final ImageDescriptor CONTEXT_ACTIVE = create(TOOL, "task-active.gif");

	public static final ImageDescriptor CONTEXT_ACTIVE_CENTERED = create(TOOL, "task-active-centered.gif");

	public static final ImageDescriptor CONTEXT_INACTIVE_EMPTY = create(TOOL, "task-inactive.gif");

	public static final ImageDescriptor CONTEXT_INACTIVE = create(TOOL, "task-context.gif");

	public static final ImageDescriptor CONTEXT_FOCUS = create(VIEW, "focus.gif");

	public static final ImageDescriptor CONTEXT_ATTACH = create(TOOL, "context-attach.gif");

	public static final ImageDescriptor CONTEXT_RETRIEVE = create(TOOL, "context-retrieve.gif");

	public static final ImageDescriptor CONTEXT_TRANSFER = create(TOOL, "context-transfer.gif");

	public static final ImageDescriptor CONTEXT_CLEAR = create(TOOL, "context-clear.gif");

	public static final ImageDescriptor CONTEXT_HISTORY_PREVIOUS = create(TOOL, "navigate-previous.gif");

	public static final ImageDescriptor CONTEXT_HISTORY_PREVIOUS_PAUSE = create(TOOL, "navigate-previous-pause.gif");

	public static final ImageDescriptor CONTEXT_HISTORY_PREVIOUS_ACTIVE = create(TOOL, "navigate-previous-active.gif");

	public static final ImageDescriptor CONTEXT_HISTORY_NEXT = create(TOOL, "navigate-next.gif");

	public static final ImageDescriptor CONTEXT_CAPTURE_PAUSE = create(TOOL, "capture-pause.gif");

	public static final ImageDescriptor CONTEXT_ADD = create(TOOL, "context-add.gif");

	public static final ImageDescriptor CONTEXT_COPY = create(TOOL, "context-transfer.gif");

	// Comments and collaboration

	public static final ImageDescriptor COMMENT = create(TOOL, "comment.gif");

	public static final ImageDescriptor COMMENT_SORT_DOWN = create(TOOL, "sort-down.gif");

	public static final ImageDescriptor COMMENT_SORT_UP = create(TOOL, "sort-up.gif");

	public static final ImageDescriptor COMMENT_SORT_DOWN_GRAY = create(TOOL, "sort-down-gray.gif");

	public static final ImageDescriptor COMMENT_SORT_UP_GRAY = create(TOOL, "sort-up-gray.gif");

	public static final ImageDescriptor COMMENT_REPLY = create(TOOL, "reply.gif");

	// Overlays

	public static final ImageDescriptor OVERLAY_TASK_LOCAL = create(OVR, "overlay-local-task.gif");

	public static final ImageDescriptor OVERLAY_TASK_WEB = create(TOOL, "overlay-web.gif");

	// Wizard banners

	public static final ImageDescriptor BANNER_REPOSITORY = create(WIZBAN, "banner-repository.gif");

	public static final ImageDescriptor BANNER_REPOSITORY_SETTINGS = create(WIZBAN, "banner-repository-settings.gif");

	public static final ImageDescriptor BANNER_REPOSITORY_CONTEXT = create(WIZBAN, "banner-repository-context.gif");

	public static final ImageDescriptor BANNER_WORKING_SET = create(WIZBAN, "workset_wiz.png");

	public static Image getImageForPriority(PriorityLevel priorityLevel) {
		if (priorityLevel == null) {
			return null;
		} else {
			ImageDescriptor imageDescriptor = getImageDescriptorForPriority(priorityLevel);
			if (imageDescriptor != null) {
				return CommonImages.getImage(imageDescriptor);
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
			return CommonImages.PRIORITY_1;
		case P2:
			return CommonImages.PRIORITY_2;
		case P3:
			return CommonImages.PRIORITY_3;
		case P4:
			return CommonImages.PRIORITY_4;
		case P5:
			return CommonImages.PRIORITY_5;
		default:
			return null;
		}
	}

	public static ImageDescriptor create(String prefix, String name) {
		try {
			return ImageDescriptor.createFromURL(makeIconFileURL(prefix, name));
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
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

}
