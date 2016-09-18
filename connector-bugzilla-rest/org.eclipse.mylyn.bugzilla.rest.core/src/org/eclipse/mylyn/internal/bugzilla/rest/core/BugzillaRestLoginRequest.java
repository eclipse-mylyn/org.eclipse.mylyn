/*******************************************************************************
 * Copyright (c) 2013 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
