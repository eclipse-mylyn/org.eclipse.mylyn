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

package org.eclipse.mylyn.tasks.core;

import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;


/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public interface ITaskActivityListener {
 
	public abstract void taskActivated(AbstractTask task);

	public abstract void taskDeactivated(AbstractTask task);

	public abstract void taskListRead();

	@Deprecated
	public abstract void activityChanged(ScheduledTaskContainer week);

	@Deprecated
	public abstract void calendarChanged();

}
