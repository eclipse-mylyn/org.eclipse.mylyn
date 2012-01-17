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

package org.eclipse.mylyn.internal.commons.repositories.core;

import java.lang.reflect.Constructor;

import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationCredentials;
import org.eclipse.mylyn.commons.repositories.core.auth.ICredentialsStore;

/**
 * Simple factory that creates {@link AuthenticationCredentials} objects.
 * 
 * @author Steffen Pingel
 */
public class CredentialsFactory {

	public static <T extends AuthenticationCredentials> T create(Class<T> credentialsType,
			ICredentialsStore credentialsStore, String key, boolean loadSecrets) {
		try {
			Constructor<T> constructor = credentialsType.getDeclaredConstructor(ICredentialsStore.class, String.class,
					boolean.class);
			constructor.setAccessible(true);
			return constructor.newInstance(credentialsStore, key, loadSecrets);
		} catch (Exception e) {
			throw new RuntimeException("Unexpected error while creating credentials", e); //$NON-NLS-1$
		}
	}

}
