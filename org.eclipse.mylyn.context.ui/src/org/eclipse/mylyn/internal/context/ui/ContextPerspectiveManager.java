/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class ContextPerspectiveManager {

	private final Set<String> actionSetsToSuppress;

	private final AbstractContextListener contextListener = new AbstractContextListener() {
		@Override
		public void contextChanged(org.eclipse.mylyn.context.core.ContextChangeEvent event) {
			switch (event.getEventKind()) {
			case ACTIVATED:
				ContextPerspectiveManager.this.contextActivated(event.getContext());
			case DEACTIVATED:
				ContextPerspectiveManager.this.contextDeactivated(event.getContext());
			}
		};
	};

	private final Set<String> managedPerspectiveIds;

	private final IPerspectiveListener perspectiveListener = new PerspectiveAdapter() {

		@Override
		public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
			cleanActionSets(page, perspective);
		}

		@Override
		public void perspectiveOpened(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
			cleanActionSets(page, perspective);
		}

	};

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

	public void contextActivated(IInteractionContext context) {
		try {
			IWorkbenchWindow launchingWindow = getWorkbenchWindow();
			if (launchingWindow != null) {
				IPerspectiveDescriptor descriptor = launchingWindow.getActivePage().getPerspective();
				setPerspectiveIdFor(null, descriptor.getId());

				String perspectiveId = getPerspectiveIdFor(context);
				showPerspective(perspectiveId);
			}
		} catch (Exception e) {
			// ignore, perspective may not have been saved, e.g. due to crash
		}
	}

	public void contextDeactivated(IInteractionContext context) {
		try {
			if (PlatformUI.isWorkbenchRunning()
					&& ContextUiPlugin.getDefault()
							.getPreferenceStore()
							.getBoolean(IContextUiPreferenceContstants.AUTO_MANAGE_PERSPECTIVES)) {
				IWorkbenchWindow launchingWindow = getWorkbenchWindow();
				if (launchingWindow != null) {
					IPerspectiveDescriptor descriptor = launchingWindow.getActivePage().getPerspective();
					setPerspectiveIdFor(context, descriptor.getId());

					String previousPerspectiveId = getPerspectiveIdFor(null);
					showPerspective(previousPerspectiveId);
				}
			}
		} catch (Exception e) {
			// ignore, perspective may not have been saved, e.g. due to crash
		}
	}

	public AbstractContextListener getContextListener() {
		return contextListener;
	}

	public IPerspectiveListener getPerspectiveListener() {
		return perspectiveListener;
	}

	public IWorkbenchWindow getWorkbenchWindow() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			if (windows.length > 0) {
				window = windows[0];
			}
		}
		return window;
	}

	public void removeManagedPerspective(String id) {
		managedPerspectiveIds.remove(id);
	}

	private void cleanActionSets(IWorkbenchPage page, IPerspectiveDescriptor perspectiveDescriptor) {
		if (managedPerspectiveIds.contains(perspectiveDescriptor.getId())) {
			for (String actionSetId : actionSetsToSuppress) {
				page.hideActionSet(actionSetId);
			}
		}
	}

	/**
	 * @param context
	 *            can be null to indicate no task
	 */
	private String getPerspectiveIdFor(IInteractionContext context) {
		if (context != null) {
			return preferenceStore.getString(IContextUiPreferenceContstants.PREFIX_TASK_TO_PERSPECTIVE
					+ context.getHandleIdentifier());
		} else {
			return preferenceStore.getString(IContextUiPreferenceContstants.PERSPECTIVE_NO_ACTIVE_TASK);
		}
	}

	/**
	 * @param context
	 *            can be null to indicate no task
	 */
	private void setPerspectiveIdFor(IInteractionContext context, String perspectiveId) {
		if (context != null) {
			preferenceStore.setValue(
					IContextUiPreferenceContstants.PREFIX_TASK_TO_PERSPECTIVE + context.getHandleIdentifier(),
					perspectiveId);
		} else {
			preferenceStore.setValue(IContextUiPreferenceContstants.PERSPECTIVE_NO_ACTIVE_TASK, perspectiveId);
		}
	}

	private void showPerspective(String perspectiveId) {
		if (perspectiveId != null
				&& perspectiveId.length() > 0
				&& ContextUiPlugin.getDefault()
						.getPreferenceStore()
						.getBoolean(IContextUiPreferenceContstants.AUTO_MANAGE_PERSPECTIVES)) {
			IWorkbenchWindow launchingWindow = getWorkbenchWindow();
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

}
