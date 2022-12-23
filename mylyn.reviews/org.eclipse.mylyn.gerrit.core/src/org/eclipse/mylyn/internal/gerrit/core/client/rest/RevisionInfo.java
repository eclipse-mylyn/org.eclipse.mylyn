/*******************************************************************************
 * Copyright (c) 2013, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Ericsson AB - added actions support
 *******************************************************************************/
package org.eclipse.mylyn.internal.gerrit.core.client.rest;

import java.util.Map;

/**
 * Data model object for
 * <a href="https://gerrit-review.googlesource.com/Documentation/rest-api-changes.html#revision-info">RevisionInfo</a>.
 */
public class RevisionInfo {

	private boolean draft;

	private int _number;

	public Map<String, ActionInfo> actions;

	private CommitInfo commit;

	private AccountInfo uploader;

	public boolean isDraft() {
		return draft;
	}

	public int getNumber() {
		return _number;
	}

	public Map<String, ActionInfo> getActions() {
		return actions;
	}

	public CommitInfo getCommit() {
		return commit;
	}

	public AccountInfo getUploader() {
		return uploader;
	}
}
