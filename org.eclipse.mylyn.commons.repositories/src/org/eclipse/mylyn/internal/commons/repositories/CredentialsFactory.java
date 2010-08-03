/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.repositories;

import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.mylyn.commons.repositories.auth.AuthenticationCredentials;
import org.eclipse.mylyn.commons.repositories.auth.ICredentialsStore;
import org.eclipse.mylyn.commons.repositories.auth.UsernamePasswordCredentials;

/**
 * @author Steffen Pingel
 */
public class CredentialsFactory {

	public static <T extends AuthenticationCredentials> T create(Class<T> credentialsKind,
			ICredentialsStore credentialsStore, String key) throws StorageException {
		if (credentialsKind == UsernamePasswordCredentials.class) {
			return (T) UsernamePasswordCredentials.create(credentialsStore, key);
		}
		throw new IllegalArgumentException("Unknown credentials type: " + credentialsKind); //$NON-NLS-1$
	}

}
