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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.mylyn.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener4;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Perspective;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.internal.registry.IActionSetDescriptor;

/**
 * @author Mik Kersten
 */
public class ContextPerspectiveManager implements ITaskActivityListener, IPerspectiveListener4 {

	private Set<String> managedPerspectiveIds = new HashSet<String>();

	private Set<String> actionSetsToSuppress = new HashSet<String>();

	public ContextPerspectiveManager() {
		actionSetsToSuppress.add("org.eclipse.ui.edit.text.actionSet.annotationNavigation");
		actionSetsToSuppress.add("org.eclipse.ui.edit.text.actionSet.convertLineDelimitersTo");
		actionSetsToSuppress.add("org.eclipse.ui.externaltools.ExternalToolsSet");
	}

	public void addManagedPerspective(String id) {
		managedPerspectiveIds.add(id);
	}

	public void removeManagedPerspective(String id) {
		managedPerspectiveIds.remove(id);
	}

	public void taskActivated(AbstractTask task) {
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

	public void taskDeactivated(AbstractTask task) {
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

	public void activityChanged(ScheduledTaskContainer week) {
		// ignore
	}

	public void taskListRead() {
		// ignore
	}

	public void tasksActivated(List<AbstractTask> tasks) {
		// ignore
	}

	public void calendarChanged() {
		// ignore
	}

	public void perspectivePreDeactivate(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		// ignore
	}

	public void perspectiveClosed(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		// ignore
	}

	public void perspectiveDeactivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		// ignore
	}

	public void perspectiveOpened(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		cleanActionSets(page, perspective);
	}

	public void perspectiveSavedAs(IWorkbenchPage page, IPerspectiveDescriptor oldPerspective,
			IPerspectiveDescriptor newPerspective) {
		// ignore
	}

	public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective,
			IWorkbenchPartReference partRef, String changeId) {
		// ignore
	}

	public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspectiveDescriptor) {
		cleanActionSets(page, perspectiveDescriptor);
	}

	private void cleanActionSets(IWorkbenchPage page, IPerspectiveDescriptor perspectiveDescriptor) {
		if (managedPerspectiveIds.contains(perspectiveDescriptor.getId())) {
			if (page instanceof WorkbenchPage) {
				Perspective perspective = ((WorkbenchPage) page).getActivePerspective();
				
				Set<IActionSetDescriptor> toRemove = new HashSet<IActionSetDescriptor>();
				IActionSetDescriptor[] actionSetDescriptors = ((WorkbenchPage) page).getActionSets();
				for (IActionSetDescriptor actionSetDescriptor : actionSetDescriptors) {
					if (actionSetsToSuppress.contains(actionSetDescriptor.getId())) {
						toRemove.add(actionSetDescriptor);
					}
				}
				perspective.turnOffActionSets((IActionSetDescriptor[]) toRemove
						.toArray(new IActionSetDescriptor[toRemove.size()]));
			}
		}
	}

	public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
		// ignore
	}
}
