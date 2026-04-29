/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.jenkins.core;

import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.jenkins.core.client.JenkinsServerInfo;

/**
 * @author Steffen Pingel
 */
public class JenkinsStatus extends Status {

	private JenkinsServerInfo info;

	public JenkinsStatus(int severity, String pluginId, String message) {
		super(severity, pluginId, message);
	}

	public JenkinsStatus(int severity, String pluginId, String message, Throwable exception) {
		super(severity, pluginId, message, exception);
	}

	public JenkinsStatus(int severity, String pluginId, int code, String message, Throwable exception) {
		super(severity, pluginId, code, message, exception);
	}

	void setInfo(JenkinsServerInfo info) {
		this.info = info;
	}

	public JenkinsServerInfo getInfo() {
		return info;
	}

}
