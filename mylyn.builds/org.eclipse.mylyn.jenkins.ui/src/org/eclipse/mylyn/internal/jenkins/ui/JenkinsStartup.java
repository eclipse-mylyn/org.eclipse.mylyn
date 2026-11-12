/*******************************************************************************
 * Copyright (c) 2012, 2013 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.jenkins.ui;

import org.eclipse.mylyn.builds.ui.BuildsUiStartup;

/**
 * @author Steffen Pingel
 */
public class JenkinsStartup extends BuildsUiStartup {

	private static JenkinsStartup instance;

	public static JenkinsStartup getInstance() {
		return instance;
	}

	public JenkinsStartup() {
		instance = this;
	}

	@Override
	public void lazyStartup() {
	}

	public void stop() {
	}

}
