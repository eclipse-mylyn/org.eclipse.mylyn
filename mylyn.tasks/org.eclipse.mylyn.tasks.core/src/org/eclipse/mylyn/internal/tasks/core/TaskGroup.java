/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eugen Kuleshov - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

/**
 * NOTE: this class is likely to change or become API for 3.0
 * 
 * @author Eugene Kuleshov
 * @since 2.1
 */
public class TaskGroup extends AbstractTaskContainer {

	private final String summary;

	private final String groupBy;

	public TaskGroup(String parentHandle, String summary, String groupBy) {
		super(parentHandle + summary);
		this.summary = summary;
		this.groupBy = groupBy;
	}

	@Override
	public String getSummary() {
		return summary;
	}

	@Override
	public boolean isUserManaged() {
		return false;
	}

	public String getGroupBy() {
		return groupBy;
	}

}
