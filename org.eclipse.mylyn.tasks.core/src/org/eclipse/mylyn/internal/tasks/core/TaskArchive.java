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

package org.eclipse.mylyn.internal.tasks.core;

import org.eclipse.mylyn.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.tasks.core.AbstractTask.PriorityLevel;



/**
 * @author Mik Kersten
 */
public class TaskArchive extends AbstractTaskCategory {

	public static final String HANDLE = "archive";
	
	public static final String LABEL_ARCHIVE = "Archive (all tasks)";
	
	public TaskArchive() {
		super(HANDLE);
	}

	public String getPriority() {
		return PriorityLevel.P5.toString();
	}

	@Override
	public String getHandleIdentifier() {
		return HANDLE;
	}
	
	@Override
	public String getSummary() {
		return LABEL_ARCHIVE;
	}

	@Override
	public boolean canRename() {
		return false;
	}
}
