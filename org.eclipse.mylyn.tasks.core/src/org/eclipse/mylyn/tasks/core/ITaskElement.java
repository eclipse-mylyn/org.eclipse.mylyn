/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.Collection;

import org.eclipse.core.runtime.IAdaptable;

/**
 * @author Mik Kersten
 * @since 3.0
 */
public interface ITaskElement extends Comparable<ITaskElement>, IAdaptable {

	/**
	 * @since 3.0
	 */
	public abstract Collection<ITask> getChildren();

	public abstract String getSummary();

	public abstract String getHandleIdentifier();

	/**
	 * @return can be null
	 */
	public abstract String getUrl();

	public abstract String getPriority();

}