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

import java.io.InputStreamReader;

import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.BugzillaRestVersionResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BugzillaRestRequestGetVersion extends BugzillaRestUnauthenticatedGetRequest<BugzillaRestVersion> {

	public BugzillaRestRequestGetVersion(BugzillaRestHttpClient client) {
		super(client);
	}

	@Override
	protected String getUrlSuffix() {
		return "/version"; //$NON-NLS-1$
	}

	@Override
	protected BugzillaRestVersion parseFromJson(InputStreamReader in) {
		TypeToken<BugzillaRestVersionResponse> type = new TypeToken<BugzillaRestVersionResponse>() {
		};
		BugzillaRestVersionResponse versionResponse = new Gson().fromJson(in, type.getType());
		return new BugzillaRestVersion(versionResponse.getVersion());
	}

}
