/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.activity.core;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.commons.core.CoreUtil;

/**
 * @author Steffen Pingel
 * @author Timur Achmetow
 */
public class ActivityEvent implements Comparable<ActivityEvent> {

	private final String handle;

	private final String kind;

	private final String summary;

	private final Date date;

	private final Map<String, String> attributes;

	public ActivityEvent(String handle, String kind, String summary, Date date, Map<String, String> attributes) {
		Assert.isNotNull("Handle must not be null", handle); //$NON-NLS-1$
		Assert.isNotNull("Task kind must not be null", kind); //$NON-NLS-1$
		Assert.isNotNull("Task summary must not be null", summary); //$NON-NLS-1$
		this.handle = handle;
		this.kind = kind;
		this.summary = summary;
		this.date = date;
		this.attributes = attributes;
	}

	public String getKind() {
		return kind;
	}

	public String getSummary() {
		return summary;
	}

	public String getHandle() {
		return handle;
	}

	public Date getDate() {
		return date;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	@Override
	public int hashCode() {
		return Objects.hash(handle);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		ActivityEvent other = (ActivityEvent) obj;
		if (!Objects.equals(handle, other.handle)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(ActivityEvent object) {
		return CoreUtil.compare(date, object.getDate());
	}
}