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

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.ErrorResponse;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.Field;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.FieldResponse;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.Named;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.VersionResponse;
import org.eclipse.mylyn.tasks.core.TaskRepository;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;

public class BugzillaRestClient {

	private final BugzillaRestHttpClient client;

	public BugzillaRestClient(RepositoryLocation location) {
		client = new BugzillaRestHttpClient(location);
	}

	public BugzillaRestHttpClient getClient() {
		return client;
	}

	public BugzillaRestVersion getVersion(IOperationMonitor monitor) throws BugzillaRestException {

		VersionResponse versionResponse = new BugzillaRestUnauthenticatedGetRequest<VersionResponse>(client,
				"/version", new TypeToken<VersionResponse>() {
		}).run(monitor);
		return new BugzillaRestVersion(versionResponse.getVersion());
	}

	public boolean validate(IOperationMonitor monitor) throws BugzillaRestException {
		ErrorResponse validateResponse = new BugzillaRestValidateRequest(client).run(monitor);
		return validateResponse.isError() && validateResponse.getCode() == 32614;
	}

	public BugzillaRestConfiguration getConfiguration(TaskRepository repository, IOperationMonitor monitor) {
		try {
			BugzillaRestConfiguration config = new BugzillaRestConfiguration(repository.getUrl());
			config.setFields(getFields(monitor));
			return config;
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, BugzillaRestCore.ID_PLUGIN,
					"Could not get the Configuration", e)); //$NON-NLS-1$
			return null;
		}
	}

	public <R, E extends Named> Map<String, E> retrieveItems(IOperationMonitor monitor, String path,
			Function<R, E[]> attributeProvider, TypeToken typeToken) throws BugzillaRestException {
		R response = new BugzillaRestAuthenticatedGetRequest<R>(client, path, typeToken).run(monitor);
		E[] members = attributeProvider.apply(response);
		return Maps.uniqueIndex(Lists.newArrayList(members), new Function<E, String>() {
			public String apply(E input) {
				return input.getName();
			};
		});
	}

	public Map<String, Field> getFields(IOperationMonitor monitor) throws BugzillaRestException {
		return retrieveItems(monitor, "/field/bug?", new Function<FieldResponse, Field[]>() {
			public Field[] apply(FieldResponse input) {
				return input.getFields();
			}
		}, new TypeToken<FieldResponse>() {
		});
	}

}
