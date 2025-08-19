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
	String getConnectorKind();

	/**
	 * @since 3.0
	 */
	String getName();

	/**
	 * @since 3.0
	 */
	String getPersonId();

	/**
	 * @since 3.0
	 */
	String getRepositoryUrl();

	/**
	 * @since 3.0
	 */
	TaskRepository getTaskRepository();

	/**
	 * @since 3.0
	 */
	void setName(String name);

	/**
	 * Compares persons using the {@link TaskAttribute#PERSON_USERNAME} if defined, or the {@link #getPersonId() person ID} otherwise.
	 *
	 * @since 3.18
	 */
	boolean matchesUsername(String username);

	/**
	 * @since 3.18
	 */
	String getAttribute(String key);

	/**
	 * @since 3.18
	 */
	void setAttribute(String key, String value);

	/**
	 * @since 3.18
	 */
	Map<String, String> getAttributes();

}