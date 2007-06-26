/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Dec 28, 2004
 */
package org.eclipse.mylyn.internal.context.ui;

import org.eclipse.mylyn.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @author Mik Kersten
 */
public class ColorMap {

	// TODO: use themes?
	public static final Color LANDMARK = TaskListColorsAndFonts.COLOR_TASK_ACTIVE;

	public static final Color BACKGROUND_COLOR = new Color(Display.getDefault(), 255, 255, 255);

	public static final Color DEFAULT = null;//Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);//new Color(Display.getDefault(), 255, 255, 255); 

	public static final Color GRAY_DARK = new Color(Display.getDefault(), 70, 70, 70);

	public static final Color GRAY_MEDIUM = new Color(Display.getDefault(), 105, 105, 105);

	public static final Color GRAY_LIGHT = new Color(Display.getDefault(), 145, 145, 145);

	public static final Color GRAY_VERY_LIGHT = new Color(Display.getDefault(), 200, 200, 200);

	public static final Color RELATIONSHIP = new Color(Display.getDefault(), 32, 104, 157);

	public static final Color HIGLIGHTER_RED_INTERSECTION = new Color(Display.getDefault(), 200, 0, 0);

	public static final Color HIGHLIGHTER_ORANGE_GRADIENT = new Color(Display.getDefault(), 222, 137, 71);

	public static final Color HIGLIGHTER_BLUE_GRADIENT = new Color(Display.getDefault(), 81, 158, 235);

	public static final Color HIGHLIGHTER_YELLOW = new Color(Display.getDefault(), 255, 238, 99);

	public static final Color PANTONE_PASTEL_YELLOW = new Color(Display.getDefault(), 244, 238, 175);

	public static final Color PANTONE_PASTEL_ROSE = new Color(Display.getDefault(), 254, 179, 190);

	public static final Color PANTONE_PASTEL_MAUVE = new Color(Display.getDefault(), 241, 183, 216);

	public static final Color PANTONE_PASTEL_PURPLE = new Color(Display.getDefault(), 202, 169, 222);

	public static final Color PANTONE_PASTEL_BLUE = new Color(Display.getDefault(), 120, 160, 250);

	public static final Color PANTONE_PASTERL_GREEN = new Color(Display.getDefault(), 162, 231, 215);

	public static final Color COLOR_WHITE = new Color(Display.getCurrent(), 255, 255, 255);

	public static final Color COLOR_BLACK = new Color(Display.getCurrent(), 0, 0, 0);

	public void dispose() {
		LANDMARK.dispose();
		BACKGROUND_COLOR.dispose();
		GRAY_DARK.dispose();
		GRAY_MEDIUM.dispose();
		GRAY_LIGHT.dispose();
		GRAY_VERY_LIGHT.dispose();
		RELATIONSHIP.dispose();
		HIGLIGHTER_RED_INTERSECTION.dispose();
		HIGHLIGHTER_ORANGE_GRADIENT.dispose();
		HIGHLIGHTER_YELLOW.dispose();
		PANTONE_PASTERL_GREEN.dispose();
		PANTONE_PASTEL_BLUE.dispose();
		PANTONE_PASTEL_MAUVE.dispose();
		PANTONE_PASTEL_PURPLE.dispose();
		PANTONE_PASTEL_ROSE.dispose();
		PANTONE_PASTEL_YELLOW.dispose();
		COLOR_WHITE.dispose();
		COLOR_BLACK.dispose();

		// below disposed by registry
		// DEFAULT.dispose();
	}

	private GammaSetting gammaSetting = GammaSetting.STANDARD;

	public enum GammaSetting {
		LIGHTEN, DARKEN, STANDARD;

		public static int compare(GammaSetting gs1, GammaSetting gs2) {
			if (gs1 == LIGHTEN) {
				if (gs2 == STANDARD) {
					return 1;
				} else if (gs2 == DARKEN) {
					return 2;
				} else {
					return 0;
				}
			} else if (gs1 == STANDARD) {
				if (gs2 == LIGHTEN) {
					return -1;
				} else if (gs2 == DARKEN) {
					return 1;
				} else {
					return 0;
				}
			} else {
				if (gs2 == LIGHTEN) {
					return -2;
				} else if (gs2 == STANDARD) {
					return -1;
				} else {
					return 0;
				}
			}
		}
	}

	public GammaSetting getGammaSetting() {
		return gammaSetting;
	}

	public void setGammaSetting(GammaSetting gammaSetting) {
		this.gammaSetting = gammaSetting;
	}

}
