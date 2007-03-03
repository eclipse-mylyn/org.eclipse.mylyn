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

/**
 * @author Mik Kersten
 */
public class ContextPerspectiveManager implements ITaskActivityListener {

	public void taskActivated(ITask task) {
		try {
			IPerspectiveDescriptor descriptor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getPerspective();
			ContextUiPlugin.getDefault().setPerspectiveIdFor(null, descriptor.getId());

			String perspectiveId = ContextUiPlugin.getDefault().getPerspectiveIdFor(task);
			showPerspective(perspectiveId);
		} catch (Exception e) {
			// ignore, perspective may not have been saved, e.g. due to crash
		}
	}

	public void taskDeactivated(ITask task) {
		try {
			if (PlatformUI.isWorkbenchRunning()
					&& ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
							ContextUiPrefContstants.AUTO_MANAGE_PERSPECTIVES)) {
				IPerspectiveDescriptor descriptor = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().getPerspective();
				ContextUiPlugin.getDefault().setPerspectiveIdFor(task, descriptor.getId());

				String previousPerspectiveId = ContextUiPlugin.getDefault().getPerspectiveIdFor(null);
				showPerspective(previousPerspectiveId);
			}
		} catch (Exception e) {
			// ignore, perspective may not have been saved, e.g. due to crash
		}
	}

	private void showPerspective(String perspectiveId) {
		if (perspectiveId != null
				&& !"".equals(perspectiveId)
				&& ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
						ContextUiPrefContstants.AUTO_MANAGE_PERSPECTIVES)) {
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setRedraw(false);
				PlatformUI.getWorkbench().showPerspective(perspectiveId,
						PlatformUI.getWorkbench().getActiveWorkbenchWindow()); 
			} catch (Exception e) {
				// ignore, perspective i spreserved if ID not found
			} finally {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setRedraw(true);
			}
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
