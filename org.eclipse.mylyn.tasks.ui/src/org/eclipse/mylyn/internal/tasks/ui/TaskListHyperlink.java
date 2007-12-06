/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

/**
 * Override Hyperlink to truncate its text at the end, rather than the middle.
 * 
 * @author Leo Dos Santos
 * @author Mik Kersten
 */
public class TaskListHyperlink extends ImageHyperlink {

	private final MouseTrackListener MOUSE_TRACK_LISTENER  = new MouseTrackListener() {
		
		public void mouseEnter(MouseEvent e) {
			setUnderlined(true);
		}

		public void mouseExit(MouseEvent e) {
			setUnderlined(false);
		}

		public void mouseHover(MouseEvent e) {
		}
	};

	private AbstractTask task;

	public TaskListHyperlink(Composite parent, int style) {
		super(parent, style);
		setForeground(TaskListColorsAndFonts.COLOR_HYPERLINK_WIDGET);
		addMouseTrackListener(MOUSE_TRACK_LISTENER);
	}
	
	@Override
	public void dispose() {
		removeMouseTrackListener(MOUSE_TRACK_LISTENER);
		super.dispose();
	}

	@Override
	protected String shortenText(GC gc, String t, int width) {
		if (t == null) {
			return null;
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

	public AbstractTask getTask() {
		return task;
	}

	public void setTask(AbstractTask task) {
		this.task = task;
	}

}
