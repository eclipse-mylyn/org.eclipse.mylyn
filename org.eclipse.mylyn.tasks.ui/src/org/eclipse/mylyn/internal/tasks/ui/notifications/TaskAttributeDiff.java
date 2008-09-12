/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.notifications;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Steffen Pingel
 */
public class TaskAttributeDiff {

	private String attributeId;

	private final TaskAttribute newAttribute;

	private final List<String> newValues;

	private final TaskAttribute oldAttribute;

	private final List<String> oldValues;

	public TaskAttributeDiff(TaskAttribute oldAttribute, TaskAttribute newAttribute) {
		Assert.isTrue(oldAttribute != null || newAttribute != null);
		this.oldAttribute = oldAttribute;
		this.newAttribute = newAttribute;
		if (oldAttribute != null) {
			this.oldValues = oldAttribute.getTaskData().getAttributeMapper().getValueLabels(oldAttribute);
			this.attributeId = oldAttribute.getId();
		} else {
			this.oldValues = Collections.emptyList();
		}
		if (newAttribute != null) {
			this.newValues = newAttribute.getTaskData().getAttributeMapper().getValueLabels(newAttribute);
			this.attributeId = newAttribute.getId();
		} else {
			this.newValues = Collections.emptyList();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TaskAttributeDiff other = (TaskAttributeDiff) obj;
		if (attributeId == null) {
			if (other.attributeId != null) {
				return false;
			}
		} else if (!attributeId.equals(other.attributeId)) {
			return false;
		}
		return true;
	}

	public List<String> getAddedValues() {
		List<String> result = new ArrayList<String>(getNewValues());
		if (getOldValues() != null) {
			result.removeAll(getOldValues());
		}
		return result;
	}

	public TaskAttribute getNewAttribute() {
		return newAttribute;
	}

	public List<String> getNewValues() {
		return newValues;
	}

	public TaskAttribute getOldAttribute() {
		return oldAttribute;
	}

	public List<String> getOldValues() {
		return oldValues;
	}

	public List<String> getRemovedValues() {
		List<String> result = new ArrayList<String>(getOldValues());
		if (getNewValues() != null) {
			result.removeAll(getNewValues());
		}
		return result;
	}

	public boolean hasChanges() {
		return !oldValues.equals(newValues);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributeId == null) ? 0 : attributeId.hashCode());
		return result;
	}

	public String getLabel() {
		if (newAttribute != null) {
			return newAttribute.getTaskData().getAttributeMapper().getLabel(newAttribute);
		} else {
			return oldAttribute.getTaskData().getAttributeMapper().getLabel(oldAttribute);
		}
	}
}