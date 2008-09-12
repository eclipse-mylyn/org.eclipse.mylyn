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

/**
 * @author Mik Kersten
 * @since 3.0
 */
public class CommonThemes {

	public static final String COLOR_INCOMING_BACKGROUND = "org.eclipse.mylyn.tasks.ui.colors.incoming.background";

	public static final String COLOR_SCHEDULED_PAST = "org.eclipse.mylyn.tasks.ui.colors.foreground.past.scheduled";

	public static final String COLOR_OVERDUE = "org.eclipse.mylyn.tasks.ui.colors.foreground.past.due";

	public static final String COLOR_SCHEDULED_THIS_WEEK = "org.eclipse.mylyn.tasks.ui.colors.foreground.thisweek.scheduled";

	public static final String COLOR_COMPLETED = "org.eclipse.mylyn.tasks.ui.colors.foreground.completed";

	public static final String COLOR_SCHEDULED_TODAY = "org.eclipse.mylyn.tasks.ui.colors.foreground.today.scheduled";

	public static final String COLOR_COMPLETED_TODAY = "org.eclipse.mylyn.tasks.ui.colors.foreground.today.completed";

	public static final String COLOR_CATEGORY_GRADIENT_START = "org.eclipse.mylyn.tasks.ui.colors.category.gradient.start";

	public static final String COLOR_CATEGORY_GRADIENT_END = "org.eclipse.mylyn.tasks.ui.colors.category.gradient.end";

	public static final String COLOR_CATEGORY = COLOR_CATEGORY_GRADIENT_END;

	public static final String FONT_EDITOR_COMMENT = "org.eclipse.mylyn.tasks.ui.fonts.task.editor.comment";

	public static boolean isCommonTheme(String property) {
		if (property == null) {
			return false;
		} else {
			return property.equals(COLOR_CATEGORY) || property.equals(COLOR_OVERDUE)
					|| property.equals(COLOR_SCHEDULED_PAST) || property.equals(COLOR_COMPLETED_TODAY)
					|| property.equals(COLOR_SCHEDULED_TODAY) || property.equals(COLOR_SCHEDULED_THIS_WEEK)
					|| property.equals(COLOR_INCOMING_BACKGROUND) || property.equals(COLOR_CATEGORY_GRADIENT_START)
					|| property.equals(COLOR_CATEGORY_GRADIENT_END);
		}
	}

}
