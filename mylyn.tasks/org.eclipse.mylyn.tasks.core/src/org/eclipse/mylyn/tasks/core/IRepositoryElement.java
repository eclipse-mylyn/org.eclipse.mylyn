/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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

import org.eclipse.core.runtime.IAdaptable;

/**
 * @author Mik Kersten
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IRepositoryElement extends Comparable<IRepositoryElement>, IAdaptable {

	/**
	 * Returns a readable description of the element.
	 */
	String getSummary();

	/**
	 * Returns an identifier for unique to where it resides. For tasks this is an identifier unique to the repository in which the tasks
	 * resides, such as the local machine or a web service. For elements in the Task List such as queries or categories, this identifier may
	 * only be unique to that Task List.
	 */
	String getHandleIdentifier();

	/**
	 * Used for elements that reside in web services and can be used for URL-based access to resources on the local machine. Optional, can
	 * be null.
	 */
	String getUrl();

}