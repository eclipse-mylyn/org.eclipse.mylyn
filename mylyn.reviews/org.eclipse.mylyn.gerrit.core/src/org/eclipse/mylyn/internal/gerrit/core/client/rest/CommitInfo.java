/*******************************************************************************
 * Copyright (c) 2014, 2015 Ericsson AB and others.
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
 * Data model object for
 * <a href="https://gerrit-review.googlesource.com/Documentation/rest-api-changes.html#commit-info">CommitInfo</a>.
 */
public class CommitInfo {

	private String commit;

	private CommitInfo[] parents;

	private GitPersonalInfo author;

	private GitPersonalInfo committer;

	private String subject;

	private String message;

	public String getCommit() {
		return commit;
	}

	public GitPersonalInfo getAuthor() {
		return author;
	}

	public GitPersonalInfo getCommitter() {
		return committer;
	}

	public String getSubject() {
		return subject;
	}

	public String getMessage() {
		return message;
	}

	public CommitInfo[] getParents() {
		return parents;
	}
}
