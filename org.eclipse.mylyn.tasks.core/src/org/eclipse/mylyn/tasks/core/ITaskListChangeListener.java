/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

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
