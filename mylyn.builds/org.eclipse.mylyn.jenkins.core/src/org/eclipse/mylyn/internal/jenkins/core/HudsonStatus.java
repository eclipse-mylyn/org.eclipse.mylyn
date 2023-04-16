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
import org.eclipse.mylyn.internal.jenkins.core.client.HudsonServerInfo;

/**
 * @author Steffen Pingel
 */
public class HudsonStatus extends Status {

	private HudsonServerInfo info;

	public HudsonStatus(int severity, String pluginId, String message) {
		super(severity, pluginId, message);
	}

	public HudsonStatus(int severity, String pluginId, String message, Throwable exception) {
		super(severity, pluginId, message, exception);
	}

	public HudsonStatus(int severity, String pluginId, int code, String message, Throwable exception) {
		super(severity, pluginId, code, message, exception);
	}

	void setInfo(HudsonServerInfo info) {
		this.info = info;
	}

	public HudsonServerInfo getInfo() {
		return info;
	}

}
