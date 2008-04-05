/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.ide.ant;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.mylyn.context.ui.IContextUiStartup;
import org.eclipse.mylyn.monitor.ui.MonitorUiPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class AntUiBridgePlugin extends Plugin {

	public static class AntUiBridgePluginStartup implements IContextUiStartup {

		public void lazyStartup() {
			AntUiBridgePlugin.getDefault().lazyStart();
		}

	}

	public static final String ID_PLUGIN = "org.eclipse.mylyn.ide.ant";

	private static AntUiBridgePlugin INSTANCE;

	public static AntUiBridgePlugin getDefault() {
		return INSTANCE;
	}

	private AntEditingMonitor antEditingMonitor;

	public AntUiBridgePlugin() {
		INSTANCE = this;
	}

	private void lazyStart() {
		antEditingMonitor = new AntEditingMonitor();
		MonitorUiPlugin.getDefault().getSelectionMonitors().add(antEditingMonitor);
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (antEditingMonitor != null) {
			MonitorUiPlugin.getDefault().getSelectionMonitors().remove(antEditingMonitor);
		}

		super.stop(context);
	}

}
