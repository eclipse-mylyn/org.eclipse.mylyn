/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.pde.ui;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.mylyn.context.ui.IContextUiStartup;
import org.eclipse.mylyn.monitor.ui.MonitorUiPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class PdeUiBridgePlugin extends Plugin {

	public static class PdeUiBrideStartup implements IContextUiStartup {

		public void lazyStartup() {
			PdeUiBridgePlugin.getDefault().lazyStart();
		}

	}

	public static final String ID_PLUGIN = "org.eclipse.mylyn.pde.ui";

	private static PdeUiBridgePlugin INSTANCE;

	public static PdeUiBridgePlugin getDefault() {
		return INSTANCE;
	}

	private PdeEditingMonitor pdeEditingMonitor;

	public PdeUiBridgePlugin() {
	}

	private void lazyStart() {
		pdeEditingMonitor = new PdeEditingMonitor();
		MonitorUiPlugin.getDefault().getSelectionMonitors().add(pdeEditingMonitor);
	}

	private void lazyStop() {
		if (pdeEditingMonitor != null) {
			MonitorUiPlugin.getDefault().getSelectionMonitors().remove(pdeEditingMonitor);
		}
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		INSTANCE = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		lazyStop();

		super.stop(context);
		INSTANCE = null;
	}

}
