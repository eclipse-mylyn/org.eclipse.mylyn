/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui.compatibility;

/**
 * Provides common theme constants.
 * <p>
 * NOTE: Use of this class is discouraged. The specified theme settings depend on components in Mylyn Tasks and may not be available.
 * </p>
 * 
 * @author Mik Kersten
 * @since 3.7
 */
public class CommonThemes {

	public static final String COLOR_INCOMING_BACKGROUND = "org.eclipse.mylyn.tasks.ui.colors.incoming.background"; //$NON-NLS-1$

	public static final String COLOR_SCHEDULED_PAST = "org.eclipse.mylyn.tasks.ui.colors.foreground.past.scheduled"; //$NON-NLS-1$

	public static final String COLOR_OVERDUE = "org.eclipse.mylyn.tasks.ui.colors.foreground.past.due"; //$NON-NLS-1$

	public static final String COLOR_SCHEDULED_THIS_WEEK = "org.eclipse.mylyn.tasks.ui.colors.foreground.thisweek.scheduled"; //$NON-NLS-1$

	public static final String COLOR_COMPLETED = "org.eclipse.mylyn.tasks.ui.colors.foreground.completed"; //$NON-NLS-1$

	public static final String COLOR_SCHEDULED_TODAY = "org.eclipse.mylyn.tasks.ui.colors.foreground.today.scheduled"; //$NON-NLS-1$

	public static final String COLOR_COMPLETED_TODAY = "org.eclipse.mylyn.tasks.ui.colors.foreground.today.completed"; //$NON-NLS-1$

	public static final String COLOR_OVERDUE_FOR_OTHERS = "org.eclipse.mylyn.tasks.ui.colors.foreground.overdue.for.others"; //$NON-NLS-1$

	public static final String COLOR_CATEGORY_GRADIENT_START = "org.eclipse.mylyn.tasks.ui.colors.category.gradient.start"; //$NON-NLS-1$

	public static final String COLOR_CATEGORY_GRADIENT_END = "org.eclipse.mylyn.tasks.ui.colors.category.gradient.end"; //$NON-NLS-1$

	public static final String COLOR_CATEGORY = COLOR_CATEGORY_GRADIENT_END;

	public static final String FONT_EDITOR_COMMENT = "org.eclipse.mylyn.tasks.ui.fonts.task.editor.comment"; //$NON-NLS-1$

	public static final String COLOR_TASK_ACTIVE = "org.eclipse.mylyn.tasks.ui.colors.foreground.task.active"; //$NON-NLS-1$

	public static boolean isCommonTheme(String property) {
		if (property == null) {
			return false;
		} else {
			return property.equals(COLOR_CATEGORY) || property.equals(COLOR_OVERDUE)
					|| property.equals(COLOR_SCHEDULED_PAST) || property.equals(COLOR_COMPLETED_TODAY)
					|| property.equals(COLOR_SCHEDULED_TODAY) || property.equals(COLOR_SCHEDULED_THIS_WEEK)
					|| property.equals(COLOR_INCOMING_BACKGROUND) || property.equals(COLOR_CATEGORY_GRADIENT_START)
					|| property.equals(COLOR_CATEGORY_GRADIENT_END) || property.equals(COLOR_TASK_ACTIVE);
		}
	}

}
