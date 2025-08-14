/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.monitor.ui;

import org.eclipse.mylyn.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Monitors interaction with workbench parts.
 *
 * @author Mik Kersten
 * @since 2.0
 */
public abstract class AbstractPartTracker implements IPartListener {

	public void install(IWorkbench workbench) {
		MonitorUiPlugin.getDefault().addWindowPartListener(this);
	}

	public void dispose(IWorkbench workbench) {
		MonitorUiPlugin.getDefault().removeWindowPartListener(this);
	}

	@Override
	public abstract void partActivated(IWorkbenchPart part);

	@Override
	public abstract void partBroughtToTop(IWorkbenchPart part);

	@Override
	public abstract void partClosed(IWorkbenchPart part);

	@Override
	public abstract void partDeactivated(IWorkbenchPart part);

	@Override
	public abstract void partOpened(IWorkbenchPart part);

}
