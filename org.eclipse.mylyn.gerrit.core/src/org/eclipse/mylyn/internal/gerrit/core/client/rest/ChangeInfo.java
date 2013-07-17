/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.rest;

import java.sql.Timestamp;

import com.google.gerrit.reviewdb.Change;

/**
 * Data model object for <a
 * href="https://gerrit-review.googlesource.com/Documentation/rest-api-changes.html#change-info">ChangeInfo</a>.
 */
public class ChangeInfo {
	// e.g. "gerritcodereview#change"
	private String kind;

	// e.g. "myProject~master~I8473b95934b5732ac55d26311a706c9c2bde9940"
	private String id;

	// e.g. "myProject"
	private String project;

	// e.g. "master"
	private String branch;

	// e.g. "I8473b95934b5732ac55d26311a706c9c2bde9940"
	private String change_id;

	// e.g. "Implementing Feature X"
	private String subject;

	// e.g. "ABANDONED"
	private Change.Status status;

	// e.g. "2013-02-01 09:59:32.126000000"
	private Timestamp created;

	// e.g. "2013-02-21 11:16:36.775000000",
	private Timestamp updated;

	private boolean reviewed;

	private boolean mergeable;

	private AccountInfo owner;

	// e.g. "0023412400000f7d"
	@SuppressWarnings("unused")
	private String _sortkey;

	// e.g. 3965
	@SuppressWarnings("unused")
	private int _number;

	public String getKind() {
		return kind;
	}

	public String getId() {
		return id;
	}

	public String getProject() {
		return project;
	}

	public String getBranch() {
		return branch;
	}

	public String getChangeId() {
		return change_id;
	}

	public String getSubject() {
		return subject;
	}

	public Change.Status getStatus() {
		return status;
	}

	public Timestamp getCreated() {
		return created;
	}

	public Timestamp getUpdated() {
		return updated;
	}

	public boolean isReviewed() {
		return reviewed;
	}

	public boolean isMergeable() {
		return mergeable;
	}

	public AccountInfo getOwner() {
		return owner;
	}
}
