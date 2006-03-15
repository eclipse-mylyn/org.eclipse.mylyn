/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasklist.ui;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

/**
 * @author Mik Kersten
 */
public class TaskListColorsAndFonts {

	public static final Color BACKGROUND_ARCHIVE = new Color(Display.getDefault(), 225, 226, 246);

	public static final Color COLOR_GRAY_LIGHT = new Color(Display.getDefault(), 170, 170, 170);

	public static final Color COLOR_TASK_COMPLETED = new Color(Display.getDefault(), 170, 170, 170);

	public static final Color COLOR_TASK_ACTIVE = new Color(Display.getDefault(), 36, 22, 50);

	public static final Color COLOR_TASK_OVERDUE = new Color(Display.getDefault(), 200, 10, 30);

	public static final Color COLOR_HYPERLINK = new Color(Display.getDefault(), 0, 0, 255);

	public static final Font BOLD = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);

	public static final Font ITALIC = JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT);

	/**
	 * NOTE: disposal of JFaceResources fonts handled by registry.
	 */
	public static void dispose() {
		BACKGROUND_ARCHIVE.dispose();
		COLOR_GRAY_LIGHT.dispose();
		COLOR_TASK_COMPLETED.dispose();
		COLOR_TASK_ACTIVE.dispose();
		COLOR_TASK_OVERDUE.dispose();
		COLOR_HYPERLINK.dispose();
	}

	public static final String THEME_COLOR_ID_TASKLIST_CATEGORY = "org.eclipse.mylar.tasklist.ui.colors.background.category"; 

}
