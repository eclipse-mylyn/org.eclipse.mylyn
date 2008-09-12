/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

/**
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IRepositoryQuery extends IAttributeContainer {

	/**
	 * @since 3.0
	 */
	public abstract String getConnectorKind();

	/**
	 * @since 3.0
	 */
	public abstract String getRepositoryUrl();

	/**
	 * @since 3.0
	 */
	public abstract String getUrl();

	/**
	 * @since 3.0
	 */
	public abstract void setUrl(String url);

	/**
	 * @since 3.0
	 */
	public abstract String getSummary();

	/**
	 * @since 3.0
	 */
	public abstract void setSummary(String summary);

}