/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

/**
 * @author Mik Kersten
 * @since 3.0
 */
public interface ITasksUiConstants {

	@Deprecated
	public static final String TITLE_DIALOG = "Mylyn Information";

	@Deprecated
	public static final String MESSAGE_RESTORE = "Could not read task list.  Consider restoring via File -> Import -> Mylyn Task Data";

	public static final String URL_HOMEPAGE = "http://eclipse.org/mylyn";

	public static final String ID_COMMAND_MARK_TASK_UNREAD = "org.eclipse.mylyn.tasks.ui.command.markTaskUnread";

	public static final String ID_COMMAND_MARK_TASK_READ = "org.eclipse.mylyn.tasks.ui.command.markTaskRead";

	public static final String ID_PAGE_PLANNING = "org.eclipse.mylyn.tasks.ui.pageFactory.Planning";

	public static final String ID_PERSPECTIVE_PLANNING = "org.eclipse.mylyn.tasks.ui.perspectives.planning";

}
