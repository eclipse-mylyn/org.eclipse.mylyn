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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.BugzillaRestIdResult;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.ErrorResponse;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.Field;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.FieldResponse;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.Named;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.ParameterResponse;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.Product;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.ProductResponse;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.PutUpdateResult;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.RestResponse;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.VersionResponse;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;

public class BugzillaRestClient {

	private final BugzillaRestHttpClient client;

	private final BugzillaRestConnector connector;

	public static final int MAX_RETRIEVED_PER_QUERY = 50;

	public BugzillaRestClient(RepositoryLocation location, BugzillaRestConnector connector) {
		client = new BugzillaRestHttpClient(location);
		this.connector = connector;
	}

	public BugzillaRestHttpClient getClient() {
		return client;
	}

	public BugzillaRestVersion getVersion(IOperationMonitor monitor) throws BugzillaRestException {

		VersionResponse versionResponse = new BugzillaRestUnauthenticatedGetRequest<VersionResponse>(client, "/version",
				new TypeToken<VersionResponse>() {
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
			config.setProducts(getProducts(monitor));
			ParameterResponse parameterResponse = getParameters(monitor);
			config.setParameters(parameterResponse != null ? parameterResponse.getParameters() : null);
			return config;
		} catch (Exception e) {
			StatusHandler
					.log(new Status(IStatus.ERROR, BugzillaRestCore.ID_PLUGIN, "Could not get the Configuration", e)); //$NON-NLS-1$
			return null;
		}
	}

	public <R extends RestResponse<E>, E extends Named> Map<String, E> retrieveItems(IOperationMonitor monitor,
			String path, TypeToken typeToken) throws BugzillaRestException {
		R response = new BugzillaRestAuthenticatedGetRequest<R>(client, path, typeToken).run(monitor);
		E[] members = response.getArray();
		return Maps.uniqueIndex(Lists.newArrayList(members), new Function<E, String>() {
			public String apply(E input) {
				return input.getName();
			};
		});
	}

	private Map<String, Field> getFields(IOperationMonitor monitor) throws BugzillaRestException {
		return retrieveItems(monitor, "/field/bug?", new TypeToken<FieldResponse>() {
		});
	}

	private Map<String, Product> getProducts(IOperationMonitor monitor) throws BugzillaRestException {
		return retrieveItems(monitor, "/product?type=accessible", new TypeToken<ProductResponse>() { //$NON-NLS-1$
		});
	}

	public ParameterResponse getParameters(IOperationMonitor monitor) throws BugzillaRestException {
		return new BugzillaRestAuthenticatedGetRequest<ParameterResponse>(client, "/parameters?",
				new TypeToken<ParameterResponse>() {
				}).run(monitor);
	}

	public RepositoryResponse postTaskData(TaskData taskData, Set<TaskAttribute> oldAttributes,
			IOperationMonitor monitor) throws BugzillaRestException {
		if (taskData.isNew()) {
			BugzillaRestIdResult result = new BugzillaRestPostNewTask(client, taskData).run(monitor);
			return new RepositoryResponse(ResponseKind.TASK_CREATED, result.getId());
		} else {
			PutUpdateResult result = new BugzillaRestPutUpdateTask(client, taskData, oldAttributes).run(monitor);
			return new RepositoryResponse(ResponseKind.TASK_UPDATED, result.getBugs()[0].getId());
		}
	}

	private final Function<String, String> function = new Function<String, String>() {

		@Override
		public String apply(String input) {
			while (input.startsWith("0")) { //$NON-NLS-1$
				input = input.substring(1);
			}
			return input;
		}
	};

	public void getTaskData(Set<String> taskIds, TaskRepository taskRepository, TaskDataCollector collector,
			IOperationMonitor monitor) throws BugzillaRestException {
		BugzillaRestConfiguration config;
		try {
			config = connector.getRepositoryConfiguration(taskRepository);
		} catch (CoreException e1) {
			throw new BugzillaRestException(e1);
		}

		Iterable<String> taskIdsTemp = Iterables.transform(taskIds, function);
		Iterable<List<String>> partitions = Iterables.partition(taskIdsTemp, MAX_RETRIEVED_PER_QUERY);
		for (List<String> list : partitions) {
			Joiner joiner = Joiner.on(",id=").skipNulls(); //$NON-NLS-1$
			String urlIDList = "id=" + joiner.join(list); //$NON-NLS-1$
			try {

				List<TaskData> taskDataArray = new BugzillaRestGetTaskData(client, connector, urlIDList, taskRepository)
						.run(monitor);
				for (TaskData taskData : taskDataArray) {
					new BugzillaRestGetTaskComments(getClient(), taskData).run(monitor);
					config.updateProductOptions(taskData);
					config.addValidOperations(taskData);
					collector.accept(taskData);
				}
			} catch (BugzillaRestException e) {
				throw e;
			} catch (RuntimeException e) {
				// if the Throwable was warped in a RuntimeException in
				// BugzillaRestGetTaskData.JSonTaskDataDeserializer.deserialize()
				// we now remove the warper and throw a  BugzillaRestException
				Throwable cause = e.getCause();
				if (cause instanceof CoreException) {
					throw new BugzillaRestException(cause);
				}
				throw e;
			}
		}

	}

}
