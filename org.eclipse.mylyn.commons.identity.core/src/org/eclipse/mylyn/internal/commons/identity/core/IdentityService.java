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

package org.eclipse.mylyn.internal.commons.identity.core;

import org.eclipse.mylyn.commons.identity.core.Account;
import org.eclipse.mylyn.commons.identity.core.IIdentity;
import org.eclipse.mylyn.commons.identity.core.spi.AbstractIdentityService;
import org.eclipse.mylyn.internal.commons.identity.core.gravatar.GravatarConnector;

/**
 * @author Steffen Pingel
 */
public class IdentityService extends AbstractIdentityService {

	private final IdentityModel model;

	public IdentityService() {
		model = new IdentityModel();
		model.addConnector(new GravatarConnector());
	}

	public IIdentity getIdentity(Account account) {
		return model.getIdentity(account);
	}

	public IIdentity[] getIdentities() {
		return model.getIdentities();
	}

}
