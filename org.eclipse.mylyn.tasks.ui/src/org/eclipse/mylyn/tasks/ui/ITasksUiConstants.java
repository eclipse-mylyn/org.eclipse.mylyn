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

package org.eclipse.mylyn.tasks.ui;

/**
 * @author Mik Kersten
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ITasksUiConstants {

	/**
	 * @since 3.0
	 */
	public static final String ID_COMMAND_MARK_TASK_UNREAD = "org.eclipse.mylyn.tasks.ui.command.markTaskUnread"; //$NON-NLS-1$

	/**
	 * @since 3.0
	 */
	public static final String ID_COMMAND_MARK_TASK_READ = "org.eclipse.mylyn.tasks.ui.command.markTaskRead"; //$NON-NLS-1$

	/**
	 * @since 3.0
	 */
	public static final String ID_PAGE_PLANNING = "org.eclipse.mylyn.tasks.ui.pageFactory.Planning"; //$NON-NLS-1$

	/**
	 * @since 3.0
	 */
	public static final String ID_PERSPECTIVE_PLANNING = "org.eclipse.mylyn.tasks.ui.perspectives.planning"; //$NON-NLS-1$

	/**
	 * @since 3.0
	 */
	public static final String ID_PREFERENCES_COLORS_AND_FONTS = "org.eclipse.ui.preferencePages.ColorsAndFonts"; //$NON-NLS-1$

	/**
	 * @since 3.2
	 */
	public static final String ID_VIEW_TASKS = "org.eclipse.mylyn.tasks.ui.views.tasks"; //$NON-NLS-1$

	/**
	 * @since 3.2
	 */
	public static final String ID_VIEW_REPOSITORIES = "org.eclipse.mylyn.tasks.ui.views.repositories"; //$NON-NLS-1$

}
