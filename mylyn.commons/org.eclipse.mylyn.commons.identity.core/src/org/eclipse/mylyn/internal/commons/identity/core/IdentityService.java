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

	@Override
	public IIdentity getIdentity(Account account) {
		return model.getIdentity(account);
	}

	@Override
	public IIdentity[] getIdentities() {
		return model.getIdentities();
	}

}
