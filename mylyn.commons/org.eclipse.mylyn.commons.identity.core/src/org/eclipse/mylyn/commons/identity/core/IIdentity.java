/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.identity.core;

import java.beans.PropertyChangeListener;
import java.util.UUID;
import java.util.concurrent.Future;

/**
 * @author Steffen Pingel
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @since 0.8
 */
public interface IIdentity {

	//	public static final String KIND_DEFAULT = "org.eclipse.mylyn.commons.identity.default"; //$NON-NLS-1$
	//
	//	public static final String KIND_EMAIL = "org.eclipse.mylyn.commons.identity.email"; //$NON-NLS-1$

	void addAccount(Account account);

	void addPropertyChangeListener(PropertyChangeListener listener);

	Account getAccountById(String id);

	Account getAccountByKind(String kind);

	Account[] getAccounts();

	String[] getAliases();

	UUID getId();

	void removeAccount(Account account);

	void removePropertyChangeListener(PropertyChangeListener listener);

	Future<IProfileImage> requestImage(int preferredWidth, int preferredHeight);

	Future<IProfile> requestProfile();

}
