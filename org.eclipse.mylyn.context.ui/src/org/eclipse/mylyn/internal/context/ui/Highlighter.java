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

package org.eclipse.mylyn.internal.context.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.monitor.core.util.StatusManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @author Mik Kersten
 */
public class Highlighter {

	private static final String LABEL_SOLID = "Solid";

	private static final String LABEL_INTERSECTION = "Intersection";

	private static final String LABEL_GRADIENT = "Gradient";

	private static final int NUM_LEVELS = 17;

	private static final String VAL_DEFAULT = "";

	private final List<Color> gradients = new ArrayList<Color>();

	private String name;

	private Color core;

	private Color highlightColor;

	private boolean isGradient;

	private boolean isIntersection;

	public boolean isIntersection() {
		return isIntersection;
	}

	public void setIntersection(boolean isIntersection) {
		this.isIntersection = isIntersection;
	}

	public Highlighter(String name, Color coreColor, boolean isGradient) {
		this.name = name;
		this.core = coreColor;
		this.isGradient = isGradient;
		if (coreColor != null) {
			initializeHighlight();
			initializeGradients();
		}
	}

	public Highlighter(String attributes) {
		if (initializeFromString(attributes)) {
			initializeHighlight();
			initializeGradients();
		}
	}

	public void dispose() {
		for (Color color : gradients) {
			color.dispose();
		}
	}

	public Color getHighlightColor() {
		return highlightColor;
	}

	public Color getHighlight(IInteractionElement info, boolean isLandmark) {
		if (info.getInterest().getValue() > 0) {
			if (isGradient) {
				return mapDoiToElevation(info);
			} else {
				return highlightColor;
			}
		} else {
			return ColorMap.COLOR_WHITE;
		}
	}

	public Color mapDoiToElevation(IInteractionElement element) {
		if (element == null)
			return ColorMap.COLOR_WHITE;
		if (element.getInterest().getValue() < 0)
			return highlightColor;

		int step = 2;
		Color color = ColorMap.COLOR_WHITE;
		for (Iterator<Color> it = gradients.iterator(); it.hasNext();) {
			color = it.next();
			if (element.getInterest().getValue() < step)
				return color;
			step += 2;
		}
		return color;
	}

	private void initializeHighlight() {
		try {
			int redStep = (int) Math.ceil((core.getRed() + 2 * ColorMap.COLOR_WHITE.getRed()) / 3);
			int greenStep = (int) Math.ceil((core.getGreen() + 2 * ColorMap.COLOR_WHITE.getGreen()) / 3);
			int blueStep = (int) Math.ceil((core.getBlue() + 2 * ColorMap.COLOR_WHITE.getBlue()) / 3);

			highlightColor = new Color(Display.getDefault(), redStep, greenStep, blueStep);
		} catch (Throwable t) {
			StatusManager.log(t, "highlighter init failed");
		}
	}

	private void initializeGradients() {
		try {
			int redStep = (int) Math.ceil((highlightColor.getRed() - ColorMap.COLOR_WHITE.getRed()) / NUM_LEVELS);
			int greenStep = (int) Math.ceil((highlightColor.getGreen() - ColorMap.COLOR_WHITE.getGreen()) / NUM_LEVELS);
			int blueStep = (int) Math.ceil((highlightColor.getBlue() - ColorMap.COLOR_WHITE.getBlue()) / NUM_LEVELS);

			int OFFSET = 1;
			int red = ColorMap.COLOR_WHITE.getRed() + redStep * OFFSET;
			int green = ColorMap.COLOR_WHITE.getGreen() + greenStep * OFFSET;
			int blue = ColorMap.COLOR_WHITE.getBlue() + blueStep * OFFSET;
			for (int i = 0; i < NUM_LEVELS - OFFSET; i++) {
				if (red > 255)
					red = 255; // TODO: fix this mess
				if (green > 255)
					green = 255;
				if (blue > 255)
					blue = 255;
				if (red < 0)
					red = 0;
				if (green < 0)
					green = 0;
				if (blue < 0)
					blue = 0;
				gradients.add(new Color(Display.getDefault(), red, green, blue));
				red += redStep;
				blue += blueStep;
				green += greenStep;
			}
		} catch (Throwable t) {
			StatusManager.log(t, "gradients failed");
		}
	}

	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Color getBase() {
		return ColorMap.COLOR_WHITE;
	}

//	public void setBase(Color base) {
//		ColorMap.COLOR_WHITE = base;
//	}

	public static Color blend(List<Highlighter> highlighters, IInteractionElement info, boolean isLandmark) {
		int red = 0;
		int green = 0;
		int blue = 0;
		int num = highlighters.size();
		for (Iterator<Highlighter> it = highlighters.iterator(); it.hasNext();) {
			Highlighter highlighter = it.next();
			Color color = highlighter.getHighlight(info, isLandmark);
			red += color.getRed();
			green += color.getGreen();
			blue += color.getBlue();
		}
		return new Color(Display.getDefault(), red / num, green / num, blue / num);
	}

	public boolean isGradient() {
		return isGradient;
	}

	public void setGradient(boolean isGradient) {
		this.isGradient = isGradient;
	}

	public Color getCore() {
		return core;
	}

	public void setCore(Color core) {
		this.core = core;
		this.initializeGradients();
		this.initializeHighlight();
	}

	public String getHighlightKind() {
		String res = VAL_DEFAULT;
		if (this.isGradient) {
			res = LABEL_GRADIENT;
		} else if (this.isIntersection) {
			res = LABEL_INTERSECTION;
		} else {
			res = LABEL_SOLID;
		}
		return res;
	}

	public String externalizeToString() {
		if (core == null) {
			return VAL_DEFAULT;
		} else {
			Integer r = new Integer(this.core.getRed());
			Integer g = new Integer(this.core.getGreen());
			Integer b = new Integer(this.core.getBlue());
			return r.toString() + ";" + g.toString() + ";" + b.toString() + ";" + this.name + ";"
					+ this.getHighlightKind();
		}
	}

	private boolean initializeFromString(String attributes) {
		if (!VAL_DEFAULT.equals(attributes)) {
			String[] data = attributes.split(";");
			Integer r = new Integer(data[0]);
			Integer g = new Integer(data[1]);
			Integer b = new Integer(data[2]);
			this.core = new Color(Display.getCurrent(), r.intValue(), g.intValue(), b.intValue());
			this.name = data[3];
			if (data[4].compareTo(LABEL_GRADIENT) == 0) {
				this.isGradient = true;
				this.isIntersection = false;
			} else if (data[4].compareTo(LABEL_INTERSECTION) == 0) {
				this.isGradient = false;
				this.isIntersection = true;
			} else {
				this.isGradient = false;
				this.isIntersection = false;
			}
			return true;
		} else {
			this.name = HighlighterList.DEFAULT_HIGHLIGHTER.getName();
			this.core = HighlighterList.DEFAULT_HIGHLIGHTER.getCore();
			return false;
		}
	}
}
