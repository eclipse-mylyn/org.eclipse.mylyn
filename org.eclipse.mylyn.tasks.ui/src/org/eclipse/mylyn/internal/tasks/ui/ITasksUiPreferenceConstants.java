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

package org.eclipse.mylyn.internal.tasks.ui;

/**
 * @author Mik Kersten
 */
public interface ITasksUiPreferenceConstants {

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

	public static final String OVERLAYS_INCOMING_TIGHT = "org.eclipse.mylyn.tasks.ui.overlays.incoming.tight";

	public static final String NOTIFICATIONS_ENABLED = "org.eclipse.mylyn.tasks.ui.notifications.enabled";

	public static final String WEEK_START_DAY = "org.eclipse.mylyn.tasks.ui.planning.week.start.day";

	public static final String PLANNING_ENDHOUR = "org.eclipse.mylyn.tasks.ui.planning.end.hour";

	public static final String EDITOR_TASKS_RICH = "org.eclipse.mylyn.tasks.ui.reporting.open.editor";

	public static final String REPOSITORY_SYNCH_SCHEDULE_ENABLED = "org.eclipse.mylyn.tasks.ui.repositories.synch.schedule";

	public static final String REPOSITORY_SYNCH_SCHEDULE_MILISECONDS = "org.eclipse.mylyn.tasks.ui.repositories.synch.schedule.miliseconds";

	public static final String TEMPLATES_DELETED = "org.eclipse.mylyn.tasks.ui.templates.deleted";

	public static final String TEMPLATES_DELETED_DELIM = "|";

	public static final String LOCAL_SUB_TASKS_ENABLED = "org.eclipse.mylyn.tasks.ui.subtasks.local";

	// NOTE: legacy name, do not change
	public static final String PREF_DATA_DIR = "org.eclipse.mylyn.data.dir";

}
