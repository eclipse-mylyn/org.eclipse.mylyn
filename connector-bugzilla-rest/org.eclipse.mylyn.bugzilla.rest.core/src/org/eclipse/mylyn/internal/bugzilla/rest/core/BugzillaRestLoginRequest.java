/*******************************************************************************
 * Copyright (c) 2013 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core;

import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.LoginToken;

import com.google.gson.reflect.TypeToken;

public class BugzillaRestLoginRequest extends BugzillaRestGetRequest<LoginToken> {

	public BugzillaRestLoginRequest(CommonHttpClient client) {
		super(client, "/login?", new TypeToken<LoginToken>() { //$NON-NLS-1$
		});
	}
}
