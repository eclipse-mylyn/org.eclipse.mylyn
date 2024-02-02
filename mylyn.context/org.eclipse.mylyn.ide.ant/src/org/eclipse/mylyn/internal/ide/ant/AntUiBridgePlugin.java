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

package org.eclipse.mylyn.internal.ide.ant;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.mylyn.context.ui.IContextUiStartup;
import org.eclipse.mylyn.monitor.ui.MonitorUi;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class AntUiBridgePlugin extends Plugin {

	public static class AntUiBridgePluginStartup implements IContextUiStartup {

		@Override
		public void lazyStartup() {
			AntUiBridgePlugin.getDefault().lazyStart();
		}

	}

	public static final String ID_PLUGIN = "org.eclipse.mylyn.ide.ant"; //$NON-NLS-1$

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
		MonitorUi.getSelectionMonitors().add(antEditingMonitor);
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (antEditingMonitor != null) {
			MonitorUi.getSelectionMonitors().remove(antEditingMonitor);
		}

		super.stop(context);
	}

}
