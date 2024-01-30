/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.monitor.ui;

import org.eclipse.mylyn.monitor.ui.AbstractUserActivityMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class WorkbenchUserActivityMonitor extends AbstractUserActivityMonitor {

	private Listener interactionActivityListener;

	private Display display;

	public WorkbenchUserActivityMonitor() {
	}

	@Override
	public void start() {
		display = MonitorUiPlugin.getDefault().getWorkbench().getDisplay();
		interactionActivityListener = event -> setLastEventTime(System.currentTimeMillis());

		display.addFilter(SWT.KeyUp, interactionActivityListener);
		display.addFilter(SWT.MouseUp, interactionActivityListener);
	}

	@Override
	public void stop() {
		if (display != null && !display.isDisposed() && interactionActivityListener != null) {
			display.removeFilter(SWT.KeyUp, interactionActivityListener);
			display.removeFilter(SWT.MouseUp, interactionActivityListener);
		}
	}
}
