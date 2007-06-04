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

import java.util.List;


/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public interface ITaskActivityListener {

	public abstract void taskActivated(ITask task);

	@Deprecated
	public abstract void tasksActivated(List<ITask> tasks);

	public abstract void taskDeactivated(ITask task);

	public abstract void activityChanged(DateRangeContainer week);
	
	public abstract void taskListRead();
	
	public abstract void calendarChanged();

}
