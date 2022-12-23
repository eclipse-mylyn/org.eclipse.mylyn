/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.core.auth;

/**
 * @author Steffen Pingel
 */
public abstract class AuthenticationCredentials {

	public abstract void clear(ICredentialsStore store, String prefix);

	public abstract void save(ICredentialsStore store, String prefix);

}
