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

import com.google.gerrit.reviewdb.AccountGeneralPreferences.DownloadScheme;

public enum DownloadSchemeX {
	ANON_HTTP(DownloadScheme.ANON_HTTP), HTTP(DownloadScheme.HTTP), GIT(DownloadScheme.ANON_GIT), SSH(
			DownloadScheme.SSH);

	private final DownloadScheme downloadScheme;

	private DownloadSchemeX(DownloadScheme scheme) {
		this.downloadScheme = scheme;
	}

	public DownloadScheme toDownloadScheme() {
		return downloadScheme;
	}

	public static DownloadSchemeX fromString(String scheme) {
		for (DownloadSchemeX value : values()) {
			if (value.toString().equalsIgnoreCase(scheme)) {
				return value;
			}
		}
		return null;
	}
}