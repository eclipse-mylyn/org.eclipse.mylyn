/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.bugzilla.core.history;

import java.io.Serializable;

/**
 * @author John Anvik
 */
public class TaskRevision implements Comparable<TaskRevision>, Serializable {

	private static final long serialVersionUID = 7739060501945994522L;

	protected final static String STATUS = "Status";

	protected final static String RESOLUTION = "Resolution";

	protected final static String ASSIGNMENT = "AssignedTo";

	protected final static String ATTACHMENT = "Attachment";

	protected String name;

	protected String date;

	protected String what;

	protected String removed;

	protected String added;

	protected TaskRevision() {
		this.added = "";
	}

	public static TaskRevision createEvent(String type, String change) {
		TaskRevision event = new TaskRevision();

		event.setWhat(type);

		if (STATUS.equals(type)) {
			event = new StatusEvent(StatusType.convert(change));
		}

		if (RESOLUTION.equals(type)) {
			event = new ResolutionEvent(ResolutionType.convert(change));
		}

		if (ASSIGNMENT.equals(type)) {
			event = new AssignmentEvent(change);
		}

		if (type.contains(ATTACHMENT) && type.contains("Flag")) {
			event = new AttachmentEvent(AttachmentEvent.parseId(type), AttachmentEvent.parseFlags(change));
		}

		event.setAdded(change);
		return event;
	}

	private void setAdded(String added) {
		this.added = added;
	}

	public void setRemoved(String removed) {
		this.removed = removed;
	}

	private void setWhat(String what) {
		this.what = what;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAdded() {
		return this.added;
	}

	public String getRemoved() {
		return this.removed;
	}

	public String getWhat() {
		return this.what;
	}

	public String getDate() {
		return this.date;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return this.name + " | " + this.date + " | " + this.what + " | " + this.removed + " | " + this.added;
	}

	public int compareTo(TaskRevision o) {
		return this.date.compareTo(o.getDate());
	}
}
