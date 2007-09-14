/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

/**
 * @author Mik Kersten
 */
public final class TasksUiPreferenceConstants {

	public static final String ACTIVATE_MULTIPLE = "org.eclipse.mylyn.tasks.ui.activation.multipe";

	public static final String ACTIVATE_WHEN_OPENED = "org.eclipse.mylyn.tasks.ui.activate.when.opened";

	public static final String SHOW_TRIM = "org.eclipse.mylyn.tasks.ui.show.trim";

	public static final String BACKUP_LAST = "org.eclipse.mylyn.tasks.ui.backup.last";

	public static final String BACKUP_MAXFILES = "org.eclipse.mylyn.tasks.ui.backup.maxfiles";

	public static final String BACKUP_SCHEDULE = "org.eclipse.mylyn.tasks.ui.backup.schedule";

	public static final String FILTER_ARCHIVE_MODE = "org.eclipse.mylyn.tasks.ui.filters.archive";

	public static final String FILTER_COMPLETE_MODE = "org.eclipse.mylyn.tasks.ui.filters.complete";

	public static final String FILTER_PRIORITY = "org.eclipse.mylyn.tasks.ui.filters.priority";

	public static final String GROUP_SUBTASKS = "org.eclipse.mylyn.tasks.ui.group.subtasks";

	/**
	 * Use GROUP_SUBTASKS instead, this option is no longer set or references.
	 */
	@Deprecated
	public static final String FILTER_SUBTASKS = "org.eclipse.mylyn.tasks.ui.filters.subtasks";
	
	public static final String OVERLAYS_INCOMING_TIGHT = "org.eclipse.mylyn.tasks.ui.overlays.incoming.tight";

	public static final String NOTIFICATIONS_ENABLED = "org.eclipse.mylyn.tasks.ui.notifications.enabled";

	public static final String PLANNING_ENDHOUR = "org.eclipse.mylyn.tasks.ui.planning.end.hour";

	public static final String PLANNING_STARTHOUR = "org.eclipse.mylyn.tasks.ui.planning.start.hour";

	public static final String EDITOR_TASKS_RICH = "org.eclipse.mylyn.tasks.ui.reporting.open.editor";

//	public static final String REPORTING_OPEN_EXTERNAL = "org.eclipse.mylyn.tasks.ui.reporting.open.external";
//
//	public static final String REPORTING_OPEN_INTERNAL = "org.eclipse.mylyn.tasks.ui.reporting.open.internal";

//	public static final String REPORTING_DISABLE_INTERNAL = "org.eclipse.mylyn.tasks.ui.reporting.disable.internal";

	public static final String REPOSITORY_SYNCH_SCHEDULE_ENABLED = "org.eclipse.mylyn.tasks.ui.repositories.synch.schedule";

	public static final String REPOSITORY_SYNCH_SCHEDULE_MILISECONDS = "org.eclipse.mylyn.tasks.ui.repositories.synch.schedule.miliseconds";

}
