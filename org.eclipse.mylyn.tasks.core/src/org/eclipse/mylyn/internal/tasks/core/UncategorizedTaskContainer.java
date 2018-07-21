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
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;

/**
 * Category created for the user to hold uncategorized tasks.
 * 
 * @author Rob Elves
 */
public class UncategorizedTaskContainer extends AbstractTaskCategory {

	public static final String LABEL = Messages.UncategorizedTaskContainer_Uncategorized;

	public static final String HANDLE = LABEL;

	public UncategorizedTaskContainer() {
		super(HANDLE);
	}

	@Override
	public String getPriority() {
		return PriorityLevel.P1.toString();
	}

	@Override
	public String getHandleIdentifier() {
		return HANDLE;
	}

	@Override
	public String getSummary() {
		return LABEL;
	}

	@Override
	public boolean isUserManaged() {
		return false;
	}

}
