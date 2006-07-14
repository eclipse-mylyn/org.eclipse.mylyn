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

package org.eclipse.mylar.internal.context.ui;

import java.util.List;

import org.eclipse.mylar.context.ui.ContextUiPlugin;
import org.eclipse.mylar.tasks.core.DateRangeContainer;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskActivityListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/**
 * @author Mik Kersten
 */
public class MylarPerspectiveManager implements ITaskActivityListener {

	public void taskActivated(ITask task) {
		String perspectiveId = ContextUiPlugin.getDefault().getPerspectiveIdFor(task);
		
		if (perspectiveId != null && !"".equals(perspectiveId) 
				&& ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(MylarUiPrefContstants.AUTO_MANAGE_PERSPECTIVES)) {
			try {
				PlatformUI.getWorkbench().showPerspective(perspectiveId, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
			} catch (WorkbenchException e) {
				// ignore
			}
		}
	}

	public void taskDeactivated(ITask task) {
		if (PlatformUI.isWorkbenchRunning() 
				&& ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(MylarUiPrefContstants.AUTO_MANAGE_PERSPECTIVES)) {
			IPerspectiveDescriptor descriptor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getPerspective();
			ContextUiPlugin.getDefault().setPerspectiveIdFor(task, descriptor.getId());
		}
	}

	public void activityChanged(DateRangeContainer week) {
		// ignore
	}

	public void taskListRead() {
		// ignore
	}

	public void tasksActivated(List<ITask> tasks) {
		// ignore
	}

	public void calendarChanged() {
		// ignore
	}
}
