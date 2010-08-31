/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.hudson.core.client;

/**
 * @author Steffen Pingel
 */
public class HudsonServerInfo {

	private final String version;

	public HudsonServerInfo(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

}
