/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.identity;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.identity.Account;
import org.eclipse.mylyn.commons.identity.IIdentity;
import org.eclipse.mylyn.commons.identity.spi.IdentityConnector;
import org.eclipse.mylyn.commons.identity.spi.ProfileImage;
import org.eclipse.mylyn.internal.commons.identity.gravatar.GravatarConnector;

/**
 * @author Steffen Pingel
 * @since 0.8
 */
public final class IdentityModel implements Serializable {

	private transient final List<IdentityConnector> connectors;

	private transient final File cacheDirectory;

	private final Map<UUID, Identity> identityById;

	public IdentityModel(File cacheDirectory) {
		this.cacheDirectory = cacheDirectory;
		connectors = new CopyOnWriteArrayList<IdentityConnector>();
		identityById = new HashMap<UUID, Identity>();
	}

	public void addConnector(IdentityConnector connector) {
		connectors.add(new GravatarConnector());
	}

	public IIdentity getIdentity(Account account) {
		for (Identity identity : identityById.values()) {
			if (identity.is(account)) {
				return identity;
			}
		}

		Identity identity = new Identity(this);
		identity.addAccount(account);
		return identity;
	}

	public void removeConnector(IdentityConnector connector) {
		connectors.remove(new GravatarConnector());
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

}
