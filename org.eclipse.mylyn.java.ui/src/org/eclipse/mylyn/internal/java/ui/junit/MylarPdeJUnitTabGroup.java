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

package org.eclipse.mylar.internal.java.ui.junit;

import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.pde.internal.ui.launcher.AbstractPDELaunchConfigurationTabGroup;
import org.eclipse.pde.ui.launcher.ConfigurationTab;
import org.eclipse.pde.ui.launcher.PluginJUnitMainTab;
import org.eclipse.pde.ui.launcher.PluginsTab;
import org.eclipse.pde.ui.launcher.TracingTab;

/**
 * Copied from: JUnitTabGroup
 * 
 * @author Mik Kersten
 */
public class MylarPdeJUnitTabGroup extends AbstractPDELaunchConfigurationTabGroup {

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = null;
		tabs = new ILaunchConfigurationTab[]{
				new MylarJUnitMainTab(true),
//				new JUnitMainTab(),
				new PluginJUnitMainTab(), 
				new JavaArgumentsTab(),
				new PluginsTab(false),	
				new ConfigurationTab(true), 
				new TracingTab(),
				new EnvironmentTab(), 
				new CommonTab()};
		setTabs(tabs);
//		ILaunchConfigurationTab[] tabs = null;
//		if (PDECore.getDefault().getModelManager().isOSGiRuntime()) {
//			tabs = new ILaunchConfigurationTab[] {
//					// new JUnitMainTab(),
//					new MylarJUnitMainTab(true), new PluginJUnitMainTab(), new JavaArgumentsTab(),
//					new PluginsTab(false), new ConfigurationTab(true), new TracingTab(), new EnvironmentTab(),
//					new SourceLookupTab(), new CommonTab() };
//		} else {
//			tabs = new ILaunchConfigurationTab[] {
//					// new JUnitMainTab(),
//					new MylarJUnitMainTab(true), new PluginJUnitMainTab(), new JavaArgumentsTab(),
//					new PluginsTab(false), new TracingTab(), new EnvironmentTab(), new SourceLookupTab(),
//					new CommonTab() };
//		}
//		setTabs(tabs);
	}

}
