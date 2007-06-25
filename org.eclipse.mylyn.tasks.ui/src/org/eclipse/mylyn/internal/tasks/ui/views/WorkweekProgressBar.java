/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.mylyn.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.IThemeManager;

/**
 * Derived from JUnitProgressBar
 * 
 * @author Mik Kersten
 */
public class WorkweekProgressBar extends Canvas {

	private static final int DEFAULT_HEIGHT = 5;

	private int currentTickCount = 0;

	private int maxTickCount = 0;

	private int colorBarWidth = 0;

	private Color completedColor;

	private Composite parent;

	public WorkweekProgressBar(Composite parent) {
		super(parent, SWT.NONE);
		this.parent = parent;

		parent.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				colorBarWidth = scale(currentTickCount);
				redraw();
			}
		});
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				paint(e);
			}
		});
		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		completedColor = themeManager.getCurrentTheme().getColorRegistry().get(
				TaskListColorsAndFonts.THEME_COLOR_TASK_TODAY_COMPLETED);
	}

	public void setMaximum(int max) {
		maxTickCount = max;
	}

	public void reset() {
		currentTickCount = 0;
		maxTickCount = 0;
		colorBarWidth = 0;
		redraw();
	}

	public void reset(int ticksDone, int maximum) {
//		boolean noChange = fError == hasErrors && fStopped == stopped && currentTickCount == ticksDone
//				&& maxTickCount == maximum;
//		fError = hasErrors;
//		fStopped = stopped;
		currentTickCount = ticksDone;
		maxTickCount = maximum;
		colorBarWidth = scale(ticksDone);
		computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		redraw();
	}

	private void paintStep(int startX, int endX) {
		GC gc = new GC(this);
		setStatusColor(gc);
		Rectangle rect = getClientArea();
		startX = Math.max(1, startX);
		gc.fillRectangle(startX, 1, endX - startX, rect.height - 2);
		gc.dispose();
	}

	private void setStatusColor(GC gc) {
		gc.setBackground(completedColor);
	}

	private int scale(int value) {
		if (maxTickCount > 0) {
			// TODO: should probably get own client area, not parent's
			Rectangle r = parent.getClientArea();
			if (r.width != 0)
				return Math.max(0, value * (r.width - 2) / maxTickCount);
		}
		return value;
	}

	private void drawBevelRect(GC gc, int x, int y, int w, int h, Color topleft, Color bottomright) {
		gc.setForeground(topleft);
		gc.drawLine(x, y, x + w - 1, y);
		gc.drawLine(x, y, x, y + h - 1);

		gc.setForeground(bottomright);
		gc.drawLine(x + w, y, x + w, y + h);
		gc.drawLine(x, y + h, x + w, y + h);
	}

	private void paint(PaintEvent event) {
		GC gc = event.gc;
		Display disp = getDisplay();

		Rectangle rect = getClientArea();
		gc.fillRectangle(rect);
		drawBevelRect(gc, rect.x, rect.y, rect.width - 1, rect.height - 1,
				disp.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW),
				disp.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));

		setStatusColor(gc);
		colorBarWidth = Math.min(rect.width - 2, colorBarWidth);
		gc.fillRectangle(1, 1, colorBarWidth, rect.height - 2);
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		checkWidget();
		Point size = new Point(parent.getSize().x, DEFAULT_HEIGHT);//parent.getSize().y);
		if (wHint != SWT.DEFAULT)
			size.x = wHint;
		if (hHint != SWT.DEFAULT)
			size.y = hHint;
		return size;
	}

	public void setCount(int count) {
		currentTickCount++;
		int x = colorBarWidth;

		colorBarWidth = scale(currentTickCount);

		if (currentTickCount == maxTickCount)
			colorBarWidth = getClientArea().width - 1;
		paintStep(x, colorBarWidth);
	}

	public void step(int failures) {
		currentTickCount++;
		int x = colorBarWidth;

		colorBarWidth = scale(currentTickCount);
		if (currentTickCount == maxTickCount)
			colorBarWidth = getClientArea().width - 1;
		paintStep(x, colorBarWidth);
	}

}
