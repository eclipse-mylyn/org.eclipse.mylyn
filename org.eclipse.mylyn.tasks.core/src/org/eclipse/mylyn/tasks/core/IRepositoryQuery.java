/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

/**
 * @since 3.0
 */
public interface IRepositoryQuery extends IAttributeContainer {

	/**
	 * @since 3.0
	 */
	public abstract String getConnectorKind();

	public abstract String getRepositoryUrl();

	public abstract String getUrl();

	public abstract void setUrl(String url);

	public abstract String getSummary();

	public abstract void setSummary(String summary);

}