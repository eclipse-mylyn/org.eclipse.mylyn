/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasks.core;

/**
 * @author Rob Elves
 */
public class UncategorizedCategory extends AbstractTaskContainer {

	public static final String LABEL = "<Uncategorized>";// "Root

	public static final String HANDLE = "uncategorized";

	public UncategorizedCategory(TaskList taskList) {
		super(HANDLE, taskList);
	}

	public String getPriority() {
		return Task.PriorityLevel.P1.toString();
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
	public boolean isLocal() {
		return true;
	}

	@Override
	public boolean canRename() {
		return false;
	}
}
