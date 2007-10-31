/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.ide.ant;

import org.eclipse.mylyn.internal.context.ui.AbstractContextUiPlugin;
import org.eclipse.mylyn.monitor.ui.MonitorUiPlugin;
import org.eclipse.ui.IWorkbench;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 */
public class AntUiBridgePlugin extends AbstractContextUiPlugin {

	private AntEditingMonitor antEditingMonitor;

	public AntUiBridgePlugin() {
		// ignore
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	@Override
	protected void lazyStart(IWorkbench workbench) {
		antEditingMonitor = new AntEditingMonitor();
		MonitorUiPlugin.getDefault().getSelectionMonitors().add(antEditingMonitor);
	}

	@Override
	protected void lazyStop() {
		MonitorUiPlugin.getDefault().getSelectionMonitors().remove(antEditingMonitor);
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}
}
