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

import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.BugzillaRestErrorResponse;
import org.eclipse.mylyn.tasks.core.TaskRepository;

public class BugzillaRestClient {

	private final BugzillaRestHttpClient client;

	public BugzillaRestClient(RepositoryLocation location) {
		client = new BugzillaRestHttpClient(location);
	}

	public BugzillaRestHttpClient getClient() {
		return client;
	}

	public BugzillaRestVersion getVersion(IOperationMonitor monitor) throws BugzillaRestException {
		return new BugzillaRestRequestGetVersion(client).run(monitor);
	}

	public boolean validate(IOperationMonitor monitor) throws BugzillaRestException {
		BugzillaRestErrorResponse validateResponse = new BugzillaRestValidateRequest(client).run(monitor);
		return validateResponse.isError() && validateResponse.getCode() == 32614;
	}

	public BugzillaRestConfiguration getConfiguration(TaskRepository repository, IOperationMonitor monitor) {
		return null;
	}

}
