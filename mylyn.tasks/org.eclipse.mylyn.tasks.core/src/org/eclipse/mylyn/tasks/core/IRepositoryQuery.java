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

/**
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IRepositoryQuery extends IAttributeContainer {

	/**
	 * @since 3.0
	 */
	String getConnectorKind();

	/**
	 * @since 3.0
	 */
	String getRepositoryUrl();

	/**
	 * @since 3.0
	 */
	String getUrl();

	/**
	 * @since 3.0
	 */
	void setUrl(String url);

	/**
	 * @since 3.0
	 */
	String getSummary();

	/**
	 * @since 3.0
	 */
	void setSummary(String summary);

}