/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies.
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

package org.eclipse.mylyn.internal.jenkins.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Steffen Pingel
 */
public class JenkinsUiPlugin extends AbstractUIPlugin {

	public static String ID_PLUGIN = "org.eclipse.mylyn.jenkins.ui"; //$NON-NLS-1$

	public JenkinsUiPlugin() {
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (JenkinsStartup.getInstance() != null) {
			JenkinsStartup.getInstance().stop();
		}
		super.stop(context);
	}

}
