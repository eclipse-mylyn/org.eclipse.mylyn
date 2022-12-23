/*******************************************************************************
 * Copyright (c) 2018 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.compat;

import java.util.Collections;
import java.util.Map;

public class SchemeInfo {
	private final String url;

	private final boolean authRequired;

	private final boolean authSupported;

	private final Map<String, String> commands;

	private final Map<String, String> cloneCommands;

	public SchemeInfo(String url, boolean authRequired, boolean authSupported, Map<String, String> commands,
			Map<String, String> cloneCommands) {
		this.url = url;
		this.authRequired = authRequired;
		this.authSupported = authSupported;
		this.commands = Collections.unmodifiableMap(commands);
		this.cloneCommands = Collections.unmodifiableMap(cloneCommands);
	}

	public String getUrl() {
		return url;
	}

	public boolean isAuthRequired() {
		return authRequired;
	}

	public boolean isAuthSupported() {
		return authSupported;
	}

	public Map<String, String> getCommands() {
		return commands;
	}

	public Map<String, String> getCloneCommands() {
		return cloneCommands;
	}
}
