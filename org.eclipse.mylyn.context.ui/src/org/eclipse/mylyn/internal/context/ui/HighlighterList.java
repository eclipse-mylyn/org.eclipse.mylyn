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

import org.eclipse.mylyn.context.ui.ContextUiPlugin;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * List class that wraps List of highlighters
 * 
 */
public class HighlighterList {

	public static final Highlighter DEFAULT_HIGHLIGHTER = new Highlighter("<none>", ColorMap.DEFAULT, false);

	private List<Highlighter> highlighters;

	public HighlighterList() {
		highlighters = new ArrayList<Highlighter>();
	}

	public HighlighterList(List<Highlighter> hlist) {
		highlighters = new ArrayList<Highlighter>();
		for (Iterator<Highlighter> it = hlist.iterator(); it.hasNext();) {
			final Highlighter highlighter = it.next();
			highlighters.add(highlighter);
		}
	}

	public HighlighterList(String attributes) {
		highlighters = new ArrayList<Highlighter>();
		this.internalizeFromString(attributes);
	}

	public void dispose() {
		for (Highlighter highlighter : highlighters) {
			highlighter.dispose();
		}
	}
	
	public void setToDefaultList() {
		this.highlighters.clear();
		highlighters.add(DEFAULT_HIGHLIGHTER);
		highlighters.add(new Highlighter("yellow", ColorMap.HIGHLIGHTER_YELLOW, false));
		highlighters.add(new Highlighter("rose", ColorMap.PANTONE_PASTEL_ROSE, false));
		highlighters.add(new Highlighter("purple", ColorMap.PANTONE_PASTEL_PURPLE,
				false));
		highlighters.add(new Highlighter("blue", ColorMap.PANTONE_PASTEL_BLUE, false));
		highlighters
				.add(new Highlighter("green", ColorMap.PANTONE_PASTERL_GREEN, false));
		highlighters.add(new Highlighter("blue gradient",
				ColorMap.HIGLIGHTER_BLUE_GRADIENT, true));
		highlighters.add(new Highlighter("orange gradient",
				ColorMap.HIGHLIGHTER_ORANGE_GRADIENT, true));

		Highlighter intersectionHighlighter = new Highlighter("intersection",
				ColorMap.HIGLIGHTER_RED_INTERSECTION, false);
		intersectionHighlighter.setIntersection(true);
		ContextUiPlugin.getDefault().setIntersectionHighlighter(intersectionHighlighter);
	}

	public void add(Highlighter hl) {
		this.highlighters.add(hl);
	}

	/**
	 * @return Returns the list.
	 */
	public List<Highlighter> getHighlighters() {
		return highlighters;
	}

	public Highlighter addHighlighter() {
//		ColorMap colorMap = new ColorMap();
		Highlighter hl = new Highlighter("new", ColorMap.GRAY_DARK, false);
		this.highlighters.add(hl);
		return hl;
	}

	public Highlighter getHighlighter(String name) {
		for (Iterator<Highlighter> it = highlighters.iterator(); it.hasNext();) {
			Highlighter highlighter = it.next();
			if (highlighter.getName().equals(name))
				return highlighter;
		}
		return null;
	}

	public void removeHighlighter(Highlighter hl) {
		this.highlighters.remove(hl);
		// MylarUiPlugin.getDefault().setDefaultHighlighter(
		// this.highlighters.get(0));
	}

	public String externalizeToString() {
		// Add an initial flag so that we know if the highlighterlist has been
		// save before.
		// This is only used when mylar is first launched with eclipse.
		// if the preference store returns a null string, then we would
		// initialize
		// the default highlighter.
		// but if this flag is in place, we will know if highlighterlist has
		// ever been saved
		String result = "flag:";
		for (Iterator<Highlighter> it = highlighters.iterator(); it.hasNext();) {
			Highlighter highlighter = it.next();
			result += highlighter.externalizeToString() + ":";
		}
		return result;
	}

	public void internalizeFromString(String attributes) {
		if (attributes != null) {
			this.highlighters.clear();
			String[] data = attributes.split(":");
			// skip the flag
			//
			for (int i = 1; i < data.length; i++) {
				Highlighter hl = new Highlighter(data[i]);
				this.highlighters.add(hl);
			}
		}
	}

	public void updateHighlighterWithGamma(ColorMap.GammaSetting prev, ColorMap.GammaSetting curr) {
		int res = ColorMap.GammaSetting.compare(prev, curr);
		if (res < 0) {
			lightenAllColors(Math.abs(res));

		} else if (res > 0) {
			darkenAllColors(Math.abs(res));
		}
	}

	private void darkenAllColors(int degree) {
		for (Highlighter hl : highlighters) {
			Color c = hl.getCore();
			double[] HSV = ColorCoordinatesChange.RGBToHSV(c.getRed(), c.getGreen(), c.getBlue());
			if (degree != 2) {
				HSV[1] *= 2;
			} else {
				HSV[1] *= 3;
			}
			if (HSV[1] > 1)
				HSV[1] = 1;

			int[] newRGB = ColorCoordinatesChange.HSVtoRGB(HSV[0], HSV[1], HSV[2]);
			Color rgb = new Color(Display.getDefault(), newRGB[0], newRGB[1], newRGB[2]);
			hl.setCore(rgb);
		}
	}

	private void lightenAllColors(int degree) {
		for (Highlighter hl : highlighters) {
			Color c = hl.getCore();
			double[] HSV = ColorCoordinatesChange.RGBToHSV(c.getRed(), c.getGreen(), c.getBlue());
			if (degree != 2) {
				HSV[1] *= 0.5;
			} else {
				HSV[1] *= 0.333;
			}
			int[] newRGB = ColorCoordinatesChange.HSVtoRGB(HSV[0], HSV[1], HSV[2]);
			Color rgb = new Color(Display.getDefault(), newRGB[0], newRGB[1], newRGB[2]);
			hl.setCore(rgb);
		}
	}
}
