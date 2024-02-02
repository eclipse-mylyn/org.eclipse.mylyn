/*******************************************************************************
 * Copyright (c) 2014 Ericsson AB and others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Ericsson AB - initial API and implementation
 ******************************************************************************/
package org.eclipse.mylyn.internal.gerrit.core.client.rest;

/**
 * Data model object for <a href="https://gerrit-review.googlesource.com/Documentation/rest-api-changes.html#related-change-and-commit-info"
 * >RelatedChangeAndCommitInfo</a>.
 */
public class RelatedChangeAndCommitInfo {

	private String change_id;

	private CommitInfo commit;

	private int _change_number;

	private int _revision_number;

	private int _current_revision_number;

	public String getChangeId() {
		return change_id;
	}

	public CommitInfo getCommitInfo() {
		return commit;
	}

	public int getChangeNumber() {
		return _change_number;
	}

	public int getRevisionNumber() {
		return _revision_number;
	}

	public int getCurrentRevisionNumber() {
		return _current_revision_number;
	}

}
