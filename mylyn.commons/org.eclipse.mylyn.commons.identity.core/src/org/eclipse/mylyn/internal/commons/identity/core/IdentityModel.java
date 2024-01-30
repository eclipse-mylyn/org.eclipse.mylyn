/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.identity.core;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.identity.core.Account;
import org.eclipse.mylyn.commons.identity.core.IIdentity;
import org.eclipse.mylyn.commons.identity.core.spi.IdentityConnector;
import org.eclipse.mylyn.commons.identity.core.spi.Profile;
import org.eclipse.mylyn.commons.identity.core.spi.ProfileImage;
import org.eclipse.mylyn.internal.commons.identity.core.gravatar.GravatarConnector;

/**
 * @author Steffen Pingel
 * @since 0.8
 */
public final class IdentityModel implements Serializable {

	private static final long serialVersionUID = -8812399358357509612L;

	private transient final List<IdentityConnector> connectors;

	private final Map<UUID, Identity> identityById;

	public IdentityModel() {
		connectors = new CopyOnWriteArrayList<>();
		identityById = new WeakHashMap<>();
	}

	public void addConnector(IdentityConnector connector) {
		connectors.add(connector);
	}

	public synchronized IIdentity getIdentity(Account account) {
		for (Identity identity : identityById.values()) {
			if (identity.is(account)) {
				return identity;
			}
		}

		Identity identity = new Identity(this);
		identity.addAccount(account);

		// cache identity
		identityById.put(identity.getId(), identity);

		return identity;
	}

	public void removeConnector(IdentityConnector connector) {
		connectors.remove(new GravatarConnector());
	}

	public IIdentity[] getIdentities() {
		return identityById.values().toArray(new IIdentity[identityById.size()]);
	}

	public ProfileImage getImage(Identity identity, int preferredWidth, int preferredHeight, IProgressMonitor monitor)
			throws CoreException {
		for (IdentityConnector connector : connectors) {
			ProfileImage image = connector.getImage(identity, preferredHeight, preferredHeight, monitor);
			if (image != null) {
				return image;
			}
		}
		return null;
	}

	public void updateProfile(Profile profile, IProgressMonitor monitor) throws CoreException {
		Account[] accounts = profile.getIdentity().getAccounts();
		for (Account account : accounts) {
			if (profile.getEmail() == null && account.getId().contains("@")) { //$NON-NLS-1$
				profile.setEmail(account.getId());
			}
			if (profile.getName() == null && account.getName() != null) {
				profile.setName(account.getName());
			}
		}
		for (IdentityConnector connector : connectors) {
			connector.updateProfile(profile, monitor);
		}
	}

}
