/*******************************************************************************
 * Copyright (c) 2010 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylyn.internal.provisional.commons.ui.DelayedRefreshJob;

public abstract class TaskListDelayedRefreshJob extends DelayedRefreshJob {

	private boolean focusedMode;

	public TaskListDelayedRefreshJob(StructuredViewer viewer, String name, boolean focusedMode) {
		super(viewer, name);
		this.focusedMode = focusedMode;
	}

	public boolean isFocusedMode() {
		return focusedMode;
	}

	public void setFocusedMode(boolean focusedMode) {
		this.focusedMode = focusedMode;
	}

}
