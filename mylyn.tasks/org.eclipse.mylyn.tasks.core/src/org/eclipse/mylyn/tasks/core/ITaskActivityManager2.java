/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.core;

import java.util.Date;

/**
 * Adds additional behaviour to ITaskActivityManager
 * 
 * @since 3.10
 * @author Lily Guo
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ITaskActivityManager2 extends ITaskActivityManager {

	/**
	 * @return the date of the first activity of the given task
	 * @since 3.10
	 */
	public abstract Date getFirstActivity(ITask task);

	/**
	 * @return the date of the last activity of the given task
	 * @since 3.10
	 */
	public abstract Date getLastActivity(ITask task);

}
