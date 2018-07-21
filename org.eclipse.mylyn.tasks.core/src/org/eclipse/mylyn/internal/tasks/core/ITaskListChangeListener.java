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

import java.util.Set;

/**
 * Listener for task list modifications and task content modifications.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public interface ITaskListChangeListener {

	public abstract void containersChanged(Set<TaskContainerDelta> containers);

}
