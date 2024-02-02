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

package org.eclipse.mylyn.internal.pde.ui;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.mylyn.context.ui.IContextUiStartup;
import org.eclipse.mylyn.monitor.ui.MonitorUi;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class PdeUiBridgePlugin extends Plugin {

	public static class PdeUiBrideStartup implements IContextUiStartup {

		@Override
		public void lazyStartup() {
			PdeUiBridgePlugin.getDefault().lazyStart();
		}

	}

	public static final String ID_PLUGIN = "org.eclipse.mylyn.pde.ui"; //$NON-NLS-1$

	private static PdeUiBridgePlugin INSTANCE;

	public static PdeUiBridgePlugin getDefault() {
		return INSTANCE;
	}

	private PdeEditingMonitor pdeEditingMonitor;

	public PdeUiBridgePlugin() {
	}

	private void lazyStart() {
		pdeEditingMonitor = new PdeEditingMonitor();
		MonitorUi.getSelectionMonitors().add(pdeEditingMonitor);
	}

	private void lazyStop() {
		if (pdeEditingMonitor != null) {
			MonitorUi.getSelectionMonitors().remove(pdeEditingMonitor);
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
