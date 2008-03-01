/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

import org.eclipse.mylyn.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.ITaskHighlighter;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.graphics.Color;

/**
 * @author Steffen Pingel
 */
public class ContextHighlighterInitializer {

	public static void init() {
		// NOTE: task list must have finished initializing	
		TasksUiPlugin.getDefault().setHighlighter(new ITaskHighlighter() {
			@SuppressWarnings("deprecation")
			public Color getHighlightColor(AbstractTask task) {
				Highlighter highlighter = ContextUiPlugin.getDefault().getHighlighterForContextId(
						"" + task.getHandleIdentifier());
				if (highlighter != null) {
					return highlighter.getHighlightColor();
				} else {
					return null;
				}
			}
		});
	}

}