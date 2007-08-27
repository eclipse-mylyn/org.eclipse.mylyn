/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.core;

import java.util.Set;

import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskCategory;

/**
 * @author Eugene Kuleshov
 * @since 2.1
 */
public class TaskGroup extends AbstractTaskCategory {

	private final String summary;

	private final String groupBy;

	public TaskGroup(String parentHandle, String summary, String groupBy) {
		super(parentHandle + summary);
		this.summary = summary;
		this.groupBy = groupBy;
	}

	@Override
	public Set<AbstractTask> getChildren() {
		return super.getChildren();
	}

	@Override
	public String getSummary() {
		return summary + " / " + super.getChildren().size();
	}

	public boolean isUserDefined() {
		return false;
	}

	public String getGroupBy() {
		return groupBy;
	}

}
