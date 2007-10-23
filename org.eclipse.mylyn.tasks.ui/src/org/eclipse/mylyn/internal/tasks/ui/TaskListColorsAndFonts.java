/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.lang.reflect.Field;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

/**
 * @author Mik Kersten
 */
public class TaskListColorsAndFonts {

	public static final String THEME_COLOR_TASKS_INCOMING_BACKGROUND = "org.eclipse.mylyn.tasks.ui.colors.incoming.background";

	public static final String THEME_COLOR_TASK_OVERDUE = "org.eclipse.mylyn.tasks.ui.colors.foreground.overdue";

	public static final String THEME_COLOR_TASK_THISWEEK_SCHEDULED = "org.eclipse.mylyn.tasks.ui.colors.foreground.thisweek.scheduled";

	public static final String THEME_COLOR_COMPLETED = "org.eclipse.mylyn.tasks.ui.colors.foreground.completed";

	public static final String THEME_COLOR_TASK_TODAY_SCHEDULED = "org.eclipse.mylyn.tasks.ui.colors.foreground.today.scheduled";

	public static final String THEME_COLOR_TASK_TODAY_COMPLETED = "org.eclipse.mylyn.tasks.ui.colors.foreground.today.completed";

	public static final String THEME_COLOR_CATEGORY_GRADIENT_START = "org.eclipse.mylyn.tasks.ui.colors.category.gradient.start";

	public static final String THEME_COLOR_CATEGORY_GRADIENT_END = "org.eclipse.mylyn.tasks.ui.colors.category.gradient.end";

	public static final String THEME_COLOR_TASKLIST_CATEGORY = THEME_COLOR_CATEGORY_GRADIENT_END;

	public static final Color BACKGROUND_ARCHIVE = new Color(Display.getDefault(), 225, 226, 246);

	public static final Color COLOR_TASK_ACTIVE = new Color(Display.getDefault(), 36, 22, 50);

	public static final Color COLOR_LABEL_CAUTION = new Color(Display.getDefault(), 200, 10, 30);

	public static final Color COLOR_HYPERLINK_WIDGET = new Color(Display.getDefault(), 12, 81, 172);

	public static final Color COLOR_HYPERLINK_TEXT = new Color(Display.getDefault(), 0, 0, 255);

	public static final Color COLOR_QUOTED_TEXT = new Color(Display.getDefault(), 38, 86, 145);
	
	public static final Color COLOR_SPELLING_ERROR = new Color(Display.getDefault(), 255, 0, 0);

	public static final Color GRAY = new Color(Display.getDefault(), 100, 100, 100);
	
	public static final Font BOLD = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);

	public static final Font ITALIC = JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT);

	public static Font STRIKETHROUGH;

	static {
		Font defaultFont = JFaceResources.getFontRegistry().get(JFaceResources.DEFAULT_FONT);
		FontData[] defaultData = defaultFont.getFontData();
		if (defaultData != null && defaultData.length == 1) {
			FontData data = new FontData(defaultData[0].getName(), defaultData[0].getHeight(),
					defaultData[0].getStyle());

			// NOTE: Windows XP only, for: data.data.lfStrikeOut = 1;
			try {
				Field dataField = data.getClass().getDeclaredField("data");
				Object dataObject = dataField.get(data);
				Class<?> clazz = dataObject.getClass().getSuperclass();
				Field strikeOutFiled = clazz.getDeclaredField("lfStrikeOut");
				strikeOutFiled.set(dataObject, (byte) 1);
				STRIKETHROUGH = new Font(Display.getCurrent(), data);
			} catch (Throwable t) {
				// Linux or other platform
				STRIKETHROUGH = defaultFont;
			}
		} else {
			STRIKETHROUGH = defaultFont;
		}
	}

	/**
	 * NOTE: disposal of JFaceResources fonts handled by registry.
	 */
	public static void dispose() {
		if (STRIKETHROUGH != null && !STRIKETHROUGH.isDisposed()) {
			STRIKETHROUGH.dispose();
		}
		BACKGROUND_ARCHIVE.dispose();
		COLOR_LABEL_CAUTION.dispose();
		COLOR_TASK_ACTIVE.dispose();
		COLOR_HYPERLINK_WIDGET.dispose();
		COLOR_QUOTED_TEXT.dispose();
		COLOR_SPELLING_ERROR.dispose();
	}

	public static boolean isTaskListTheme(String property) {
		if (property == null) {
			return false;
		} else {
			return property.equals(TaskListColorsAndFonts.THEME_COLOR_TASKLIST_CATEGORY)
					|| property.equals(TaskListColorsAndFonts.THEME_COLOR_TASK_OVERDUE)
					|| property.equals(TaskListColorsAndFonts.THEME_COLOR_TASK_TODAY_COMPLETED)
					|| property.equals(TaskListColorsAndFonts.THEME_COLOR_TASK_TODAY_SCHEDULED)
					|| property.equals(TaskListColorsAndFonts.THEME_COLOR_TASK_TODAY_SCHEDULED)
					|| property.equals(TaskListColorsAndFonts.THEME_COLOR_TASK_THISWEEK_SCHEDULED)
					|| property.equals(TaskListColorsAndFonts.THEME_COLOR_TASKS_INCOMING_BACKGROUND)
					|| property.equals(TaskListColorsAndFonts.THEME_COLOR_CATEGORY_GRADIENT_START)
					|| property.equals(TaskListColorsAndFonts.THEME_COLOR_CATEGORY_GRADIENT_END);
		}
	}

	public static final String TASK_EDITOR_FONT = "org.eclipse.mylyn.tasks.ui.fonts.task.editor.comment";

}
