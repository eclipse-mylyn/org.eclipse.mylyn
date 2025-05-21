/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

/**
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @author Mik Kersten
 * @since 3.0
 */
public class TasksUiImages {

	private static final URL baseURL = TasksUiPlugin.getDefault().getBundle().getEntry("/icons/"); //$NON-NLS-1$

	private static final String VIEW = "eview16"; //$NON-NLS-1$

	private static final String TOOL = "etool16"; //$NON-NLS-1$

	private static final String TOOL_SMALL = "etool12"; //$NON-NLS-1$

	private static final String OBJ = "obj16"; //$NON-NLS-1$

	private static final String OVERLAY = "ovr16"; //$NON-NLS-1$

	private static final String WIZBAN = "wizban"; //$NON-NLS-1$

	// Tasks and Task List elements

	public static final ImageDescriptor TASK = create(TOOL, "task.svg"); //$NON-NLS-1$

	public static final ImageDescriptor TASK_COMPLETE = create(TOOL, "task-complete.svg"); //$NON-NLS-1$

	public static final ImageDescriptor TASK_INCOMPLETE = create(TOOL, "task-incomplete.svg"); //$NON-NLS-1$

	public static final ImageDescriptor TASK_COMPLETED = create(TOOL, "task-completed.svg"); //$NON-NLS-1$

	public static final ImageDescriptor TASK_NOTES = create(TOOL, "task-notes.svg"); //$NON-NLS-1$

	public static final ImageDescriptor TASK_NEW = create(TOOL, "task-new.svg"); //$NON-NLS-1$

	public static final ImageDescriptor TASK_NEW_SUB = create(TOOL, "sub-task-new.svg"); //$NON-NLS-1$

	public static final ImageDescriptor TASK_REPOSITORY_HISTORY = create(TOOL, "task-repository-history.svg"); //$NON-NLS-1$

	public static final ImageDescriptor TASK_REMOTE = create(TOOL, "task-remote.svg"); //$NON-NLS-1$

	public static final ImageDescriptor TASK_WORKING_SET = create(TOOL, "open-task.svg"); //$NON-NLS-1$

	public static final ImageDescriptor TASKS_VIEW = create(VIEW, "task-list.svg"); //$NON-NLS-1$

	public static final ImageDescriptor TASK_ATTACHMENT_PATCH = create(OBJ, "attachment-patch.svg"); //$NON-NLS-1$

	/**
	 * @since 3.21
	 */
	public static final ImageDescriptor TASK_OWNED = create(TOOL, "task-owned.svg"); //$NON-NLS-1$

	/**
	 * @since 3.7
	 */
	public static final ImageDescriptor FILTER_OBSOLETE_SMALL = create(TOOL_SMALL, "file-delete-line-12x12.svg"); //$NON-NLS-1$

	/**
	 * @since 3.7
	 */
	public static final ImageDescriptor FILE_NEW_SMALL = create(TOOL_SMALL, "file-new-12x12.svg"); //$NON-NLS-1$

	public static final ImageDescriptor TASK_RETRIEVE = create(TOOL, "task-retrieve.svg"); //$NON-NLS-1$

	public static final ImageDescriptor TASK_REPOSITORY = create(TOOL, "task-repository.svg"); //$NON-NLS-1$

	public static final ImageDescriptor TASK_REPOSITORY_NEW = create(TOOL, "task-repository-new.svg"); //$NON-NLS-1$

	public static final ImageDescriptor CATEGORY = create(TOOL, "category.svg"); //$NON-NLS-1$

	public static final ImageDescriptor CATEGORY_NEW = create(TOOL, "category-new.svg"); //$NON-NLS-1$

	public static final ImageDescriptor CATEGORY_UNCATEGORIZED = create(TOOL, "category-archive.svg"); //$NON-NLS-1$

	public static final ImageDescriptor QUERY = create(TOOL, "query.svg"); //$NON-NLS-1$

	public static final ImageDescriptor QUERY_NEW = create(TOOL, "query-new.svg"); //$NON-NLS-1$

	/**
	 * @since 3.2
	 */
	public static final ImageDescriptor QUERY_OFFLINE = ImageDescriptor.createWithFlags(TasksUiImages.QUERY,
			SWT.IMAGE_GRAY);

	public static final ImageDescriptor QUERY_UNMATCHED = create(TOOL, "query-unmatched.svg"); //$NON-NLS-1$

	public static final ImageDescriptor REPOSITORY = create(VIEW, "repositories.svg"); //$NON-NLS-1$

	public static final ImageDescriptor REPOSITORY_OFFLINE = ImageDescriptor.createWithFlags(TasksUiImages.REPOSITORY,
			SWT.IMAGE_GRAY);

	public static final ImageDescriptor REPOSITORY_SYNCHRONIZE_SMALL = create(TOOL_SMALL,
			"repository-synchronize-small.svg"); //$NON-NLS-1$

	/**
	 * @since 3.2
	 */
	public static final ImageDescriptor REPOSITORY_UPDATE_CONFIGURATION = create(TOOL,
			"repository-synchronize-attributes.svg"); //$NON-NLS-1$

	public static final ImageDescriptor REPOSITORY_SYNCHRONIZE = create(TOOL, "repository-synchronize.svg"); //$NON-NLS-1$

	public static final ImageDescriptor REPOSITORY_SUBMIT = create(TOOL, "repository-submit.svg"); //$NON-NLS-1$

	public static final ImageDescriptor REPOSITORY_SMALL = create(OBJ, "repository-small.svg"); //$NON-NLS-1$

	public static final ImageDescriptor REPOSITORY_NEW = create(TOOL, "repository-new.svg"); //$NON-NLS-1$

	/**
	 * @since 3.1
	 */
	public static final ImageDescriptor REPOSITORY_VALIDATE = create(OBJ, "resource_obj.svg"); //$NON-NLS-1$

	public static final ImageDescriptor REPOSITORIES_VIEW = create(VIEW, "repositories.svg"); //$NON-NLS-1$

	// Context and activation

	public static final ImageDescriptor CONTEXT_ACTIVE = create(TOOL, "task-active.svg"); //$NON-NLS-1$

	public static final ImageDescriptor CONTEXT_ACTIVE_CENTERED = create(TOOL, "task-active-centered.svg"); //$NON-NLS-1$

	public static final ImageDescriptor CONTEXT_INACTIVE_EMPTY = create(TOOL, "task-inactive.svg"); //$NON-NLS-1$

	public static final ImageDescriptor CONTEXT_INACTIVE = create(TOOL, "task-context.svg"); //$NON-NLS-1$

	public static final ImageDescriptor CONTEXT_FOCUS = create(VIEW, "focus.svg"); //$NON-NLS-1$

	public static final ImageDescriptor CONTEXT_ATTACH = create(TOOL, "context-attach.svg"); //$NON-NLS-1$

	public static final ImageDescriptor CONTEXT_RETRIEVE = create(TOOL, "context-retrieve.svg"); //$NON-NLS-1$

	public static final ImageDescriptor CONTEXT_TRANSFER = create(TOOL, "context-transfer.svg"); //$NON-NLS-1$

	public static final ImageDescriptor CONTEXT_CLEAR = create(TOOL, "context-clear.svg"); //$NON-NLS-1$

	public static final ImageDescriptor CONTEXT_HISTORY_PREVIOUS = create(TOOL, "navigate-previous.svg"); //$NON-NLS-1$

	@Deprecated
	public static final ImageDescriptor CONTEXT_HISTORY_PREVIOUS_PAUSE = create(TOOL, "navigate-previous-pause.svg"); //$NON-NLS-1$

	public static final ImageDescriptor CONTEXT_HISTORY_PREVIOUS_ACTIVE = create(TOOL, "navigate-previous-active.svg"); //$NON-NLS-1$

	public static final ImageDescriptor CONTEXT_HISTORY_NEXT = create(TOOL, "navigate-next.svg"); //$NON-NLS-1$

	public static final ImageDescriptor CONTEXT_CAPTURE_PAUSE = create(TOOL, "capture-pause.svg"); //$NON-NLS-1$

	public static final ImageDescriptor CONTEXT_ADD = create(TOOL, "context-add.svg"); //$NON-NLS-1$

	public static final ImageDescriptor CONTEXT_COPY = create(TOOL, "context-transfer.svg"); //$NON-NLS-1$

	// Comments and collaboration

	public static final ImageDescriptor COMMENT = create(TOOL, "comment.svg"); //$NON-NLS-1$

	public static final ImageDescriptor COMMENT_SORT_DOWN = create(TOOL, "sort-down.gif"); //$NON-NLS-1$

	public static final ImageDescriptor COMMENT_SORT_UP = create(TOOL, "sort-up.gif"); //$NON-NLS-1$

	public static final ImageDescriptor COMMENT_SORT_DOWN_GRAY = create(TOOL, "sort-down-gray.gif"); //$NON-NLS-1$

	public static final ImageDescriptor COMMENT_SORT_UP_GRAY = create(TOOL, "sort-up-gray.gif"); //$NON-NLS-1$

	public static final ImageDescriptor COMMENT_REPLY = create(TOOL, "reply.svg"); //$NON-NLS-1$

	/**
	 * @since 3.1
	 */
	public static final ImageDescriptor COMMENT_REPLY_SMALL = create(TOOL_SMALL, "reply.svg"); //$NON-NLS-1$

	// Wizard banners

	public static final ImageDescriptor BANNER_REPOSITORY = create(WIZBAN, "banner-repository.gif"); //$NON-NLS-1$

	public static final ImageDescriptor BANNER_REPOSITORY_SETTINGS = create(WIZBAN, "banner-repository-settings.gif"); //$NON-NLS-1$

	public static final ImageDescriptor BANNER_REPOSITORY_CONTEXT = create(WIZBAN, "banner-repository-context.gif"); //$NON-NLS-1$

	public static final ImageDescriptor BANNER_WORKING_SET = create(WIZBAN, "workset_wiz.svg"); //$NON-NLS-1$

	/**
	 * @since 3.2
	 */
	public static final ImageDescriptor PRESENTATION_CATEGORIZED = create(TOOL, "presentation-categorized.svg"); //$NON-NLS-1$

	/**
	 * @since 3.2
	 */
	public static final ImageDescriptor PRESENTATION_SCHEDULED = create(TOOL, "presentation-scheduled.svg"); //$NON-NLS-1$

	/**
	 * @since 3.2
	 */
	public static final ImageDescriptor BANNER_REPORT_BUG = create(WIZBAN, "bug-wizard.gif"); //$NON-NLS-1$

	/**
	 * @since 3.7
	 */

	public static final ImageDescriptor LOCK_CLOSE = create(TOOL_SMALL, "lock.svg"); //$NON-NLS-1$

	/**
	 * @since 3.7
	 */
	public static final ImageDescriptor LOCK_OPEN = create(TOOL_SMALL, "unlock.svg"); //$NON-NLS-1$

	/**
	 * @since 3.8
	 */
	public static final ImageDescriptor IMAGE_CAPTURE_SMALL = create(TOOL_SMALL, "image_capture.svg"); //$NON-NLS-1$

	/**
	 * @since 3.10
	 */
	public static final ImageDescriptor NOTES = create(OVERLAY, "overlay-notes.svg"); //$NON-NLS-1$

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
		return switch (priorityLevel) {
			case P1 -> CommonImages.PRIORITY_1;
			case P2 -> CommonImages.PRIORITY_2;
			case P3 -> CommonImages.PRIORITY_3;
			case P4 -> CommonImages.PRIORITY_4;
			case P5 -> CommonImages.PRIORITY_5;
			default -> null;
		};
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

		StringBuilder buffer = new StringBuilder(prefix);
		buffer.append('/');
		buffer.append(name);
		return new URL(baseURL, buffer.toString());
	}

}
