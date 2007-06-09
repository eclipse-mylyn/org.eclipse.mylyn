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
package org.eclipse.mylyn.internal.ide.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.core.MylarStatusHandler;
import org.eclipse.mylyn.internal.ide.xml.ant.AntEditingMonitor;
import org.eclipse.mylyn.internal.ide.xml.pde.PdeEditingMonitor;
import org.eclipse.mylyn.monitor.ui.MonitorUiPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 */
public class IdeUiBridgePlugin extends AbstractUIPlugin {
	
	public static final String PLUGIN_ID = "org.eclipse.mylyn.ide";
	
	private static IdeUiBridgePlugin INSTANCE;

	public static final ImageDescriptor EDGE_REF_XML = getImageDescriptor("icons/elcl16/edge-ref-xml.gif");

	private PdeEditingMonitor pdeEditingMonitor;

	private AntEditingMonitor antEditingMonitor;
		
	public IdeUiBridgePlugin() {
		INSTANCE = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		pdeEditingMonitor = new PdeEditingMonitor();
		MonitorUiPlugin.getDefault().getSelectionMonitors().add(pdeEditingMonitor);

		antEditingMonitor = new AntEditingMonitor();
		MonitorUiPlugin.getDefault().getSelectionMonitors().add(antEditingMonitor);
		
//		final IWorkbench workbench = PlatformUI.getWorkbench();
//		workbench.getDisplay().asyncExec(new Runnable() {
//			public void run() {
//				try {
//					pdeEditingMonitor = new PdeEditingMonitor();
//					MylarMonitorPlugin.getDefault().getSelectionMonitors().add(pdeEditingMonitor);
//
//					antEditingMonitor = new AntEditingMonitor();
//					MylarMonitorPlugin.getDefault().getSelectionMonitors().add(antEditingMonitor);
//				} catch (Exception e) {
//					MylarStatusHandler.fail(e, "Mylar IDE initialization failed", false);
//				}
//			}
//		});
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			super.stop(context);
			INSTANCE = null;
			MonitorUiPlugin.getDefault().getSelectionMonitors().remove(pdeEditingMonitor);
			MonitorUiPlugin.getDefault().getSelectionMonitors().remove(antEditingMonitor);
		} catch (Exception e) {
			MylarStatusHandler.fail(e,
					"Mylar IDE stop failed, Mylar may not have started properly (ensure correct Eclipse version)",
					false);
		}
	}

	public static IdeUiBridgePlugin getDefault() {
		return INSTANCE;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.mylyn.ide", path);
	}
}
