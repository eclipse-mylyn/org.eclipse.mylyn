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

package org.eclipse.mylyn.tasks.core.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;

/**
 * Describes a revision of a task in its history.
 * 
 * @author Steffen Pingel
 * @since 3.6
 */
public class TaskRevision {

	/**
	 * @author Steffen Pingel
	 */
	public static class Change {

		private final String added;

		private final String attributeId;

		private final String field;

		private final String removed;

		/**
		 * Constructs a field change.
		 * 
		 * @param attributeId
		 *            the id of the attribute that has changed, must not be null
		 * @param field
		 *            the label of the attribute that has changed, must not be null
		 * @param removed
		 *            the values that were removed
		 * @param added
		 *            the values that were added
		 */
		public Change(String attributeId, String field, String removed, String added) {
			Assert.isNotNull(attributeId);
			Assert.isNotNull(field);
			this.attributeId = attributeId;
			this.field = field;
			this.removed = removed;
			this.added = added;
		}

		public String getAdded() {
			return added;
		}

		public String getAttributeId() {
			return attributeId;
		}

		public String getField() {
			return field;
		}

		public String getRemoved() {
			return removed;
		}

	}

	private final IRepositoryPerson author;

	private final List<TaskRevision.Change> changes;

	private final Date date;

	private final String id;

	/**
	 * @param id
	 *            id that identifies the revisions, it must be unique on a per task basis
	 * @param date
	 *            the time the revision was created
	 * @param author
	 *            the person that made changes for this revision
	 */
	public TaskRevision(String id, Date date, IRepositoryPerson author) {
		Assert.isNotNull(id);
		this.id = id;
		this.date = date;
		this.author = author;
		this.changes = new ArrayList<TaskRevision.Change>();
	}

	public void add(TaskRevision.Change change) {
		changes.add(change);
	}

	public IRepositoryPerson getAuthor() {
		return author;
	}

	public List<TaskRevision.Change> getChanges() {
		return new ArrayList<TaskRevision.Change>(changes);
	}

	public Date getDate() {
		return date;
	}

	public String getId() {
		return id;
	}

	public void remove(TaskRevision.Change change) {
		changes.remove(change);
	}

}