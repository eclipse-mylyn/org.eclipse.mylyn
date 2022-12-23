/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.bugs;

/**
 * Represents a provider that supports product.
 * 
 * @author Steffen Pingel
 * @since 3.4
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IProvider {

	/**
	 * Returns the name of the provider.
	 * 
	 * @return null, if a name is not available; the name, otherwise
	 */
	public abstract String getName();

	/**
	 * Returns a description for the provider.
	 * 
	 * @return null, if a description is not available; the description, otherwise
	 */
	public abstract String getDescription();

	/**
	 * Returns an id for the provider that is unique in respect to other provider ids.
	 */
	public abstract String getId();

}
