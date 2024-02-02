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

/**
 * Data model object for <a href="https://gerrit-review.googlesource.com/Documentation/rest-api-config.html#server-info">ServerInfo</a>.
 *
 * @since 2.12
 */
public class ServerInfo {
	private GerritInfo gerrit;

	private DownloadInfo download;

	public GerritInfo getGerrit() {
		return gerrit;
	}

	public DownloadInfo getDownload() {
		return download;
	}
}
