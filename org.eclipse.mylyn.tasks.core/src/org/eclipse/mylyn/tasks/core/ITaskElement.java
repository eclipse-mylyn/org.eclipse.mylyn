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
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ITaskElement extends Comparable<ITaskElement>, IAdaptable {

	/**
	 * Returns the children of this task, as defined by a containment hierarchy such as the Task List's categories,
	 * queries and substasks. Never returns null.
	 * 
	 * @since 3.0
	 */
	public abstract Collection<ITask> getChildren();

	/**
	 * Returns a readable description of the element.
	 */
	public abstract String getSummary();

	/**
	 * Returns an identifier for unique to where it resides. For tasks this is an identifier unique to the repository in
	 * which the tasks resides, such as the local machine or a web service. For elements in the Task List such as
	 * queries or categories, this identifier may only be unique to that Task List.
	 */
	public abstract String getHandleIdentifier();

	/**
	 * Used for elements that reside in web services and can be used for URL-based access to resources on the local
	 * machine. Optional, can be null.
	 */
	public abstract String getUrl();

}