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

package org.eclipse.mylyn.internal.gerrit.core.client.rest;

import java.util.Map;

/**
 * Data model object for
 * <a href= "https://gerrit-review.googlesource.com/Documentation/rest-api-config.html#download-scheme-info">DownloadSchemeInfo</a>.
 *
 * @since 2.12
 */
public class DownloadSchemeInfo {
	private String url;

	private boolean is_auth_required;

	private boolean is_auth_supported;

	private Map<String, String> commands;

	private Map<String, String> clone_commands;

	public String getUrl() {
		return url;
	}

	public boolean isAuthRequired() {
		return is_auth_required;
	}

	public boolean isAuthSupported() {
		return is_auth_supported;
	}

	public Map<String, String> getCommands() {
		return commands;
	}

	public Map<String, String> getCloneCommands() {
		return clone_commands;
	}
}
