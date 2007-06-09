/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.ide.ant;

import org.eclipse.mylyn.monitor.ui.MonitorUiPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 */
public class AntUiBridgePlugin extends AbstractUIPlugin {

	private AntEditingMonitor antEditingMonitor;

	public AntUiBridgePlugin() {
		// ignore
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		antEditingMonitor = new AntEditingMonitor();
		MonitorUiPlugin.getDefault().getSelectionMonitors().add(antEditingMonitor);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		MonitorUiPlugin.getDefault().getSelectionMonitors().remove(antEditingMonitor);
	}

}
