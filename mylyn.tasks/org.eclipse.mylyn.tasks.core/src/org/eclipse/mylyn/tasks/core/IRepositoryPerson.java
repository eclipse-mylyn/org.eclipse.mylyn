/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
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

import java.util.Map;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Steffen Pingel
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IRepositoryPerson {

	/**
	 * @since 3.0
	 */
	public abstract String getConnectorKind();

	/**
	 * @since 3.0
	 */
	public abstract String getName();

	/**
	 * @since 3.0
	 */
	public abstract String getPersonId();

	/**
	 * @since 3.0
	 */
	public abstract String getRepositoryUrl();

	/**
	 * @since 3.0
	 */
	public abstract TaskRepository getTaskRepository();

	/**
	 * @since 3.0
	 */
	public abstract void setName(String name);

	/**
	 * Compares persons using the {@link TaskAttribute#PERSON_USERNAME} if defined, or the {@link #getPersonId() person
	 * ID} otherwise.
	 * 
	 * @since 3.18
	 */
	public abstract boolean matchesUsername(String username);

	/**
	 * @since 3.18
	 */
	public abstract String getAttribute(String key);

	/**
	 * @since 3.18
	 */
	public abstract void setAttribute(String key, String value);

	/**
	 * @since 3.18
	 */
	public abstract Map<String, String> getAttributes();

}