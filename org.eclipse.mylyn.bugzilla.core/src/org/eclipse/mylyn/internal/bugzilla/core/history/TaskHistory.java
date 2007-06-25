/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.bugzilla.core.history;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author John Anvik
 */
public class TaskHistory implements Iterable<TaskRevision>, Serializable {

	private static final long serialVersionUID = 1724420130243724426L;

	private final List<StatusEvent> statusEvents;

	private final List<ResolutionEvent> resolutionEvents;

	private final List<AssignmentEvent> assignmentEvents;

	private final List<TaskRevision> otherEvents;

	private final List<AttachmentEvent> attachmentEvents;

	public TaskHistory() {
		this.statusEvents = new ArrayList<StatusEvent>();
		this.resolutionEvents = new ArrayList<ResolutionEvent>();
		this.assignmentEvents = new ArrayList<AssignmentEvent>();
		this.attachmentEvents = new ArrayList<AttachmentEvent>();
		this.otherEvents = new ArrayList<TaskRevision>();
	}

	public void addEvent(TaskRevision event) {
		if (event instanceof StatusEvent) {
			this.statusEvents.add((StatusEvent) event);
			return;
		}

		if (event instanceof ResolutionEvent) {
			this.resolutionEvents.add((ResolutionEvent) event);
			return;
		}

		if (event instanceof AssignmentEvent) {
			this.assignmentEvents.add((AssignmentEvent) event);
			return;
		}

		if (event instanceof AttachmentEvent) {
			this.attachmentEvents.add((AttachmentEvent) event);
			return;
		}
		this.otherEvents.add(event);
	}

	private List<TaskRevision> getEvents() {
		List<TaskRevision> events = new ArrayList<TaskRevision>();
		events.addAll(this.statusEvents);
		events.addAll(this.resolutionEvents);
		events.addAll(this.assignmentEvents);
		events.addAll(this.attachmentEvents);
		events.addAll(this.otherEvents);
		Collections.sort(events);
		return events;
	}

	public Iterator<TaskRevision> iterator() {
		return getEvents().iterator();
	}

	public int size() {
		return this.otherEvents.size() + this.statusEvents.size() + this.resolutionEvents.size()
				+ this.assignmentEvents.size();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Object event : this) {
			sb.append(event);
			sb.append("\n");
		}
		return sb.toString();
	}

	public List<StatusEvent> getStatusEvents() {
		return statusEvents;
	}

	public List<ResolutionEvent> getResolutionEvents() {
		return resolutionEvents;
	}

	public List<TaskRevision> getOtherEvents() {
		return otherEvents;
	}

	public List<AttachmentEvent> getAttachmentEvents() {
		return attachmentEvents;
	}

	public List<AssignmentEvent> getAssignmentEvents() {
		return assignmentEvents;
	}

}
