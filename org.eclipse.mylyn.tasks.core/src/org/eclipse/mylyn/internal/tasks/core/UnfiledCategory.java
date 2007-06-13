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
 * Category created for the user to hold uncategorized tasks.
 * 
 * @author Rob Elves
 */
public class UnfiledCategory extends AbstractTaskCategory {

	public static final String LABEL = "Misc";

	public static final String HANDLE = "uncategorized";

	public UnfiledCategory() {
		super(HANDLE);
	}
	
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
	public boolean isUserDefined() {
		// ignore
		return false;
	}
}
