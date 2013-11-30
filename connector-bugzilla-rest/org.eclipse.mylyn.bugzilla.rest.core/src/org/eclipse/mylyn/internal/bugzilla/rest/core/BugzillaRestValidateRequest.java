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

import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpStatus;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpResponse;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.BugzillaRestErrorResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BugzillaRestValidateRequest extends BugzillaRestAuthenticatedGetRequest<BugzillaRestErrorResponse> {

	public BugzillaRestValidateRequest(BugzillaRestHttpClient client) {
		super(client);
	}

	@Override
	protected void doValidate(CommonHttpResponse response, IOperationMonitor monitor) throws IOException,
			BugzillaRestException {
		validate(response, HttpStatus.SC_BAD_REQUEST, monitor);
	}

	@Override
	protected BugzillaRestErrorResponse parseFromJson(InputStreamReader in) {
		TypeToken<BugzillaRestErrorResponse> type = new TypeToken<BugzillaRestErrorResponse>() {
		};
		return new Gson().fromJson(in, type.getType());
	}

}
