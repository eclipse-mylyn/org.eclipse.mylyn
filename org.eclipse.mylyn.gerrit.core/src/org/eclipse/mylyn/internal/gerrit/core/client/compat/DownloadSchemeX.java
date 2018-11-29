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

public enum DownloadSchemeX {
	ANON_HTTP("anonymous http"), HTTP("http"), GIT("git"), SSH("ssh"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	private String scheme;

	private DownloadSchemeX(String scheme) {
		this.scheme = scheme;
	}

	@Override
	public String toString() {
		return scheme;
	}

	public static DownloadSchemeX fromString(String scheme) {
		for (DownloadSchemeX value : values()) {
			if (value.toString().equals(scheme)) {
				return value;
			}
		}
		return null;
	}
}
