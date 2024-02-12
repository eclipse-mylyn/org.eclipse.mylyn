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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.pde.ui.junit;

import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.mylyn.internal.java.ui.junit.TaskContextJUnitMainTab;
import org.eclipse.pde.ui.launcher.ConfigurationTab;
import org.eclipse.pde.ui.launcher.JUnitTabGroup;
import org.eclipse.pde.ui.launcher.PluginJUnitMainTab;
import org.eclipse.pde.ui.launcher.PluginsTab;
import org.eclipse.pde.ui.launcher.TracingTab;

/**
 * Copied from: JUnitTabGroup
 * 
 * @author Mik Kersten
 */
@SuppressWarnings("restriction")
public class TaskContextPdeJUnitTabGroup extends JUnitTabGroup {

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {

		ILaunchConfigurationTab[] tabs = null;
		tabs = new ILaunchConfigurationTab[] { new TaskContextJUnitMainTab(true),
//				new JUnitLaunchConfigurationTab(),
				new PluginJUnitMainTab(), new JavaArgumentsTab(), new PluginsTab(), new ConfigurationTab(true),
				new TracingTab(), new EnvironmentTab(), new CommonTab() };
		setTabs(tabs);
	}

}
