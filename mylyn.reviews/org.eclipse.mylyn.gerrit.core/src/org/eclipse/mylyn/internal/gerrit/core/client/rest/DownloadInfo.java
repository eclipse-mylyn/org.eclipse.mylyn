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
 * <a href="https://gerrit-review.googlesource.com/Documentation/rest-api-config.html#download-info">DownloadInfo</a>.
 *
 * @since 2.12
 */
public class DownloadInfo {
	Map<String, DownloadSchemeInfo> schemes;

	public Map<String, DownloadSchemeInfo> getSchemes() {
		return schemes;
	}
}
