/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.state;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.IContextUiPreferenceContstants;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Saves the active perspective on context deactivation and restores it on activation.
 * 
 * @author Mik Kersten
 * @author Shawn Minto
 * @author Steffen Pingel
 */
public class PerspectiveStateParticipant extends ContextStateParticipant {

	public static final String KEY_ACTIVE_ID = "activeId"; //$NON-NLS-1$

	public static final String MEMENTO_PERSPECTIVE = "org.eclipse.mylyn.context.ui.perspectives"; //$NON-NLS-1$

	private final IPreferenceStore preferenceStore;

	public PerspectiveStateParticipant(IPreferenceStore preferenceStore) {
		this.preferenceStore = preferenceStore;
	}

	@Override
	public void clearState(String contextHandle, boolean isActiveContext) {
		// ignore		
	}

	public String getActivePerspectiveId() {
		if (PlatformUI.isWorkbenchRunning()) {
			IWorkbenchWindow launchingWindow = getWorkbenchWindow();
			if (launchingWindow != null) {
				return getActivePerspectiveId(launchingWindow);
			}
		}
		return null;
	}

	public IWorkbenchWindow getWorkbenchWindow() {
		if (PlatformUI.isWorkbenchRunning()) {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window == null) {
				IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
				if (windows.length > 0) {
					window = windows[0];
				}
			}
			return window;
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		return ContextUiPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(IContextUiPreferenceContstants.AUTO_MANAGE_PERSPECTIVES);
	}

	@Override
	public void restoreDefaultState(ContextState memento) {
		String previousPerspectiveId = getDefaultPerspectiveId();
		showPerspective(previousPerspectiveId);
	}

	@Override
	public void restoreState(ContextState state) {
		IWorkbenchWindow launchingWindow = getWorkbenchWindow();
		if (launchingWindow != null) {
			// restore perspective
			IMemento memento = state.getMemento(MEMENTO_PERSPECTIVE);
			if (memento != null) {
				String perspectiveId = memento.getString(KEY_ACTIVE_ID);
				showPerspective(perspectiveId);
			}
		}
	}

	@Override
	public void saveDefaultState(ContextState memento) {
		String id = getActivePerspectiveId();
		if (id != null) {
			setDefaultPerspectiveId(id);
		}
	}

	@Override
	public void saveState(ContextState state, boolean allowModifications) {
		String id = getActivePerspectiveId();
		if (id != null) {
			IMemento memento = state.createMemento(MEMENTO_PERSPECTIVE);
			memento.putString(KEY_ACTIVE_ID, id);
		}
	}

	private String getActivePerspectiveId(IWorkbenchWindow window) {
		Assert.isNotNull(window);
		IPerspectiveDescriptor descriptor = window.getActivePage().getPerspective();
		return descriptor.getId();
	}

	private String getDefaultPerspectiveId() {
		return preferenceStore.getString(IContextUiPreferenceContstants.PERSPECTIVE_NO_ACTIVE_TASK);
	}

	private void setDefaultPerspectiveId(String perspectiveId) {
		preferenceStore.setValue(IContextUiPreferenceContstants.PERSPECTIVE_NO_ACTIVE_TASK, perspectiveId);
	}

	private void showPerspective(String perspectiveId) {
		if (perspectiveId != null && perspectiveId.length() > 0) {
			IWorkbenchWindow launchingWindow = getWorkbenchWindow();
			if (launchingWindow != null) {
				try {
					launchingWindow.getShell().setRedraw(false);
					PlatformUI.getWorkbench().showPerspective(perspectiveId, launchingWindow);
				} catch (Exception e) {
					// perspective's preserved id not found, ignore
				} finally {
					launchingWindow.getShell().setRedraw(true);
				}
			}
		}
	}

}
