/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Sascha Scholz (SAP) - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.compat;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.gerrit.common.data.GerritConfig;

/**
 * @author Steffen Pingel
 * @author Sascha Scholz
 */
public class GerritConfigX extends GerritConfig {

	private List<CommentLink> commentLinks;

	private String gitHttpUrl;

	private Map<DownloadSchemeX, SchemeInfo> schemes;

	public Map<DownloadSchemeX, SchemeInfo> getSchemes() {
		return schemes;
	}

	public void setSchemes(Map<DownloadSchemeX, SchemeInfo> schemes) {
		this.schemes = Collections.unmodifiableMap(schemes);
	}

	public List<CommentLink> getCommentLinks2() {
		return commentLinks;
	}

	public String getGitHttpUrl() {
		return gitHttpUrl;
	}

}