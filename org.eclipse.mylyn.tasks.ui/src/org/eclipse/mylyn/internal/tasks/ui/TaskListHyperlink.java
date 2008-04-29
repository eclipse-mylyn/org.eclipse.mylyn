/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.mylyn.provisional.workbench.ui.CommonColorsAndFonts;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

/**
 * Enhanced <code>Hyperlink</code> for rendering tasks that truncates its text at the end rather than the middle if it
 * is wider than the available space.
 * 
 * @author Leo Dos Santos
 * @author Mik Kersten
 * @author Steffen Pingel
 * @author Frank Becker
 */
public class TaskListHyperlink extends ImageHyperlink {

	private final MouseTrackListener MOUSE_TRACK_LISTENER = new MouseTrackListener() {

		public void mouseEnter(MouseEvent e) {
			setUnderlined(true);
		}

		public void mouseExit(MouseEvent e) {
			setUnderlined(false);
		}

		public void mouseHover(MouseEvent e) {
		}
	};

	private boolean strikeThrough;

	private AbstractTask task;

	public TaskListHyperlink(Composite parent, int style) {
		super(parent, style);
		setForeground(CommonColorsAndFonts.COLOR_HYPERLINK_WIDGET);
		addMouseTrackListener(MOUSE_TRACK_LISTENER);
	}

	@Override
	public void dispose() {
		removeMouseTrackListener(MOUSE_TRACK_LISTENER);
		super.dispose();
	}

	public AbstractTask getTask() {
		return task;
	}

	public boolean isStrikeThrough() {
		return strikeThrough;
	}

	@Override
	protected void paintText(GC gc, Rectangle bounds) {
		super.paintText(gc, bounds);
		if (strikeThrough) {
			Point totalSize = computeTextSize(SWT.DEFAULT, SWT.DEFAULT);
			int textWidth = Math.min(bounds.width, totalSize.x);
			int textHeight = totalSize.y;

			//			int descent = gc.getFontMetrics().getDescent();
			int lineY = bounds.y + (textHeight / 2); // - descent + 1;
			gc.drawLine(bounds.x, lineY, bounds.x + textWidth, lineY);
		}
	}

	public void setStrikeThrough(boolean strikethrough) {
		this.strikeThrough = strikethrough;
	}

	public void setTask(AbstractTask task) {
		this.task = task;
		if (task != null) {
			if ((getStyle() & SWT.SHORT) != 0) {
				setText(task.getTaskKey());
				setToolTipText(task.getTaskKey() + ": " + task.getSummary());
				setStrikeThrough(task.isCompleted());
			} else {
				setText(task.getSummary());
				setToolTipText("");
				setStrikeThrough(false);
			}
		} else {
			setText("");
			setToolTipText("");
			setStrikeThrough(false);
		}
		setUnderlined(false);
	}

	@Override
	protected String shortenText(GC gc, String t, int width) {
		if (t == null) {
			return null;
		}

		if ((getStyle() & SWT.SHORT) != 0) {
			return t;
		}

		String returnText = t;
		if (gc.textExtent(t).x > width) {
			for (int i = t.length(); i > 0; i--) {
				String test = t.substring(0, i);
				test = test + "...";
				if (gc.textExtent(test).x < width) {
					returnText = test;
					break;
				}
			}
		}
		return returnText;
	}

}
