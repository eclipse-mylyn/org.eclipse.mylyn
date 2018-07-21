/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Steffen Pingel
 */
public class BugHistory {

	public static class Change {

		private final String added;

		private final int attachmentId;

		private final String fieldName;

		private final String removed;

		private Change(String fieldName, String added, String removed, int attachmentId) {
			this.fieldName = fieldName;
			this.added = added;
			this.removed = removed;
			this.attachmentId = attachmentId;
		}

		public String getAdded() {
			return added;
		}

		public int getAttachmentId() {
			return attachmentId;
		}

		public String getFieldName() {
			return fieldName;
		}

		public String getRemoved() {
			return removed;
		}

	}

	public static class Revision {

		private final List<Change> changes;

		private final Date when;

		private final String who;

		private Revision(Date when, String who) {
			this.when = when;
			this.who = who;
			this.changes = new ArrayList<Change>();
		}

		public void addChange(String fieldName, String added, String removed, int attachmentId) {
			changes.add(new Change(fieldName, added, removed, attachmentId));
		}

		public List<Change> getChanges() {
			return changes;
		}

		public Date getWhen() {
			return when;
		}

		public String getWho() {
			return who;
		}

	}

	private final int bugId;

	private final List<Revision> revisions;

	public BugHistory(int bugId) {
		this.bugId = bugId;
		this.revisions = new ArrayList<BugHistory.Revision>();
	}

	public Revision createRevision(Date when, String who) {
		Revision revision = new Revision(when, who);
		revisions.add(revision);
		return revision;
	}

	public int getBugId() {
		return bugId;
	}

	public List<Revision> getRevisions() {
		return revisions;
	}

}
