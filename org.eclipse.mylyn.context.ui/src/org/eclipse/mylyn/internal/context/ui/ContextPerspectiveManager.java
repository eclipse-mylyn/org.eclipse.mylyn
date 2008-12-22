/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.mylyn.monitor.ui.MonitorUi;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivationListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener4;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Perspective;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.internal.registry.IActionSetDescriptor;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class ContextPerspectiveManager implements ITaskActivationListener, IPerspectiveListener4 {

	private final Set<String> managedPerspectiveIds;

	private final Set<String> actionSetsToSuppress;

	private final IPreferenceStore preferenceStore;

	public ContextPerspectiveManager(IPreferenceStore preferenceStore) {
		this.preferenceStore = preferenceStore;
		this.managedPerspectiveIds = new HashSet<String>();
		this.actionSetsToSuppress = new HashSet<String>();
		actionSetsToSuppress.add("org.eclipse.ui.edit.text.actionSet.annotationNavigation"); //$NON-NLS-1$
		actionSetsToSuppress.add("org.eclipse.ui.edit.text.actionSet.convertLineDelimitersTo"); //$NON-NLS-1$
		actionSetsToSuppress.add("org.eclipse.ui.externaltools.ExternalToolsSet"); //$NON-NLS-1$
	}

	public void addManagedPerspective(String id) {
		managedPerspectiveIds.add(id);
	}

	public void removeManagedPerspective(String id) {
		managedPerspectiveIds.remove(id);
	}

	public void taskActivated(ITask task) {
		try {
			IWorkbenchWindow launchingWindow = MonitorUi.getLaunchingWorkbenchWindow();
			if (launchingWindow != null) {
				IPerspectiveDescriptor descriptor = launchingWindow.getActivePage().getPerspective();
				setPerspectiveIdFor(null, descriptor.getId());

				String perspectiveId = getPerspectiveIdFor(task);
				showPerspective(perspectiveId);
			}
		} catch (Exception e) {
			// ignore, perspective may not have been saved, e.g. due to crash
		}
	}

	public void taskDeactivated(ITask task) {
		try {
			if (PlatformUI.isWorkbenchRunning()
					&& ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
							IContextUiPreferenceContstants.AUTO_MANAGE_PERSPECTIVES)) {
				IWorkbenchWindow launchingWindow = MonitorUi.getLaunchingWorkbenchWindow();
				if (launchingWindow != null) {
					IPerspectiveDescriptor descriptor = launchingWindow.getActivePage().getPerspective();
					setPerspectiveIdFor(task, descriptor.getId());

					String previousPerspectiveId = getPerspectiveIdFor(null);
					showPerspective(previousPerspectiveId);
				}
			}
		} catch (Exception e) {
			// ignore, perspective may not have been saved, e.g. due to crash
		}
	}

	private void showPerspective(String perspectiveId) {
		if (perspectiveId != null
				&& perspectiveId.length() > 0
				&& ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
						IContextUiPreferenceContstants.AUTO_MANAGE_PERSPECTIVES)) {
			IWorkbenchWindow launchingWindow = MonitorUi.getLaunchingWorkbenchWindow();
			try {
				if (launchingWindow != null) {
					launchingWindow.getShell().setRedraw(false);
					PlatformUI.getWorkbench().showPerspective(perspectiveId, launchingWindow);
				}
			} catch (Exception e) {
				// perspective's preserved id not found, ignore
			} finally {
				if (launchingWindow != null) {
					launchingWindow.getShell().setRedraw(true);
				}
			}
		}
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
				perspective.turnOffActionSets(toRemove.toArray(new IActionSetDescriptor[toRemove.size()]));
			}
		}
	}

	public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
		// ignore
	}

	public void preTaskActivated(ITask task) {
		// ignore	
	}

	public void preTaskDeactivated(ITask task) {
		// ignore		
	}

	/**
	 * @param task
	 *            can be null to indicate no task
	 */
	private String getPerspectiveIdFor(ITask task) {
		if (task != null) {
			return preferenceStore.getString(IContextUiPreferenceContstants.PREFIX_TASK_TO_PERSPECTIVE
					+ task.getHandleIdentifier());
		} else {
			return preferenceStore.getString(IContextUiPreferenceContstants.PERSPECTIVE_NO_ACTIVE_TASK);
		}
	}

	/**
	 * @param task
	 *            can be null to indicate no task
	 */
	private void setPerspectiveIdFor(ITask task, String perspectiveId) {
		if (task != null) {
			preferenceStore.setValue(IContextUiPreferenceContstants.PREFIX_TASK_TO_PERSPECTIVE
					+ task.getHandleIdentifier(), perspectiveId);
		} else {
			preferenceStore.setValue(IContextUiPreferenceContstants.PERSPECTIVE_NO_ACTIVE_TASK, perspectiveId);
		}
	}

}
