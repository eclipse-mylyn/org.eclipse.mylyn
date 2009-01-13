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
