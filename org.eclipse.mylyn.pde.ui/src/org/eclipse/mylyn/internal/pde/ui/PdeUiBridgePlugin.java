/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.pde.ui;

import org.eclipse.mylyn.monitor.ui.MonitorUiPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 */
public class PdeUiBridgePlugin extends AbstractUIPlugin {

	private PdeEditingMonitor pdeEditingMonitor;

	public PdeUiBridgePlugin() {
		// ignore
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		pdeEditingMonitor = new PdeEditingMonitor();
		MonitorUiPlugin.getDefault().getSelectionMonitors().add(pdeEditingMonitor);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		MonitorUiPlugin.getDefault().getSelectionMonitors().remove(pdeEditingMonitor);
	}

}
