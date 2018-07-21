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

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.BooleanResult;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.BugzillaRestIdResult;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.Field;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.FieldResponse;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.LoginToken;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.Named;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.ParameterResponse;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.Product;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.ProductResponse;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.PutUpdateResult;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.RestResponse;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.VersionResponse;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.osgi.util.NLS;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;

public class BugzillaRestClient {

	private final CommonHttpClient client;

	private final BugzillaRestConnector connector;

	public static final int MAX_RETRIEVED_PER_QUERY = 50;

	public BugzillaRestClient(RepositoryLocation location, BugzillaRestConnector connector) {
		client = new CommonHttpClient(location);
		this.connector = connector;
	}

	public CommonHttpClient getClient() {
		return client;
	}

	public BugzillaRestVersion getVersion(IOperationMonitor monitor) throws BugzillaRestException {

		VersionResponse versionResponse = new BugzillaRestUnauthenticatedGetRequest<VersionResponse>(client, "/version", //$NON-NLS-1$
				new TypeToken<VersionResponse>() {
				}).run(monitor);
		return new BugzillaRestVersion(versionResponse.getVersion());
	}

	public boolean validate(IOperationMonitor monitor) throws BugzillaRestException {
		RepositoryLocation location = getClient().getLocation();
		if (location.getBooleanPropery(IBugzillaRestConstants.REPOSITORY_USE_API_KEY)) {
			UserCredentials credentials = location.getCredentials(AuthenticationType.REPOSITORY);
			Preconditions.checkState(credentials != null, "Authentication requested without valid credentials");
			String url = MessageFormat.format("/valid_login?login={0}&api_key={1}", //$NON-NLS-1$
					credentials.getUserName(), location.getProperty(IBugzillaRestConstants.REPOSITORY_API_KEY));

			BooleanResult response = new BugzillaRestUnauthenticatedGetRequest<BooleanResult>(client, url,
					new TypeToken<BooleanResult>() {
					}).run(monitor);
			return response.getResult();
		} else {
			LoginToken validateResponse = new BugzillaRestLoginRequest(client).run(monitor);
			if (validateResponse != null && !Strings.isNullOrEmpty(validateResponse.getId())) {
				// invalide the token
				String url = MessageFormat.format("/logout?token={0}", //$NON-NLS-1$
						validateResponse.getToken());
				new BugzillaRestUnauthenticatedGetRequest<BooleanResult>(client, url, new TypeToken<BooleanResult>() {
				}).run(monitor);
				return true;
			}
		}
		return false;
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
			String path, TypeToken<?> typeToken) throws BugzillaRestException {
		R response = new BugzillaRestGetRequest<R>(client, path, typeToken).run(monitor);
		E[] members = response.getArray();
		return Maps.uniqueIndex(Lists.newArrayList(members), new Function<E, String>() {
			public String apply(E input) {
				return input.getName();
			};
		});
	}

	private Map<String, Field> getFields(IOperationMonitor monitor) throws BugzillaRestException {
		return retrieveItems(monitor, "/field/bug?", new TypeToken<FieldResponse>() { //$NON-NLS-1$
		});
	}

	private Map<String, Product> getProducts(IOperationMonitor monitor) throws BugzillaRestException {
		return retrieveItems(monitor, "/product?type=accessible", new TypeToken<ProductResponse>() { //$NON-NLS-1$
		});
	}

	public ParameterResponse getParameters(IOperationMonitor monitor) throws BugzillaRestException {
		return new BugzillaRestGetRequest<ParameterResponse>(client, "/parameters?", //$NON-NLS-1$
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

	private final Function<String, String> removeLeadingZero = new Function<String, String>() {

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

		Iterable<String> taskIdsTemp = Iterables.transform(taskIds, removeLeadingZero);
		Iterable<List<String>> partitions = Iterables.partition(taskIdsTemp, MAX_RETRIEVED_PER_QUERY);
		for (List<String> list : partitions) {
			Joiner joiner = Joiner.on(",id=").skipNulls(); //$NON-NLS-1$
			String urlIDList = "id=" + joiner.join(list); //$NON-NLS-1$
			try {

				List<TaskData> taskDataArray = new BugzillaRestGetTaskData(client, connector, urlIDList, taskRepository)
						.run(monitor);
				for (TaskData taskData : taskDataArray) {
					new BugzillaRestGetTaskComments(getClient(), taskData).run(monitor);
					new BugzillaRestGetTaskAttachments(getClient(), taskData).run(monitor);
					config.updateProductOptions(taskData);
					config.addValidOperations(taskData);
					config.updateFlags(taskData);
					config.updateKeyword(taskData);
					collector.accept(taskData);
				}
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

	public IStatus performQuery(TaskRepository taskRepository, final IRepositoryQuery query,
			final TaskDataCollector resultCollector, IOperationMonitor monitor) throws BugzillaRestException {
		String urlIDList = query.getUrl();
		urlIDList = urlIDList.substring(urlIDList.indexOf("?") + 1); //$NON-NLS-1$
		List<TaskData> taskDataArray = new BugzillaRestGetTaskData(client, connector, urlIDList, taskRepository)
				.run(monitor);
		for (final TaskData taskData : taskDataArray) {
			taskData.setPartial(true);
			SafeRunner.run(new ISafeRunnable() {

				@Override
				public void run() throws Exception {
					resultCollector.accept(taskData);
				}

				@Override
				public void handleException(Throwable exception) {
					StatusHandler.log(new Status(IStatus.ERROR, BugzillaRestCore.ID_PLUGIN,
							NLS.bind("Unexpected error during result collection. TaskID {0} in repository {1}", //$NON-NLS-1$
									taskData.getTaskId(), taskData.getRepositoryUrl()),
							exception));
				}
			});
		}
		return Status.OK_STATUS;
	}

	public InputStream getAttachmentData(@NonNull TaskAttribute attachmentAttribute,
			@Nullable IOperationMonitor monitor) throws BugzillaRestException {
		return new BugzillaRestGetTaskAttachmentData(client, attachmentAttribute).run(monitor);
	}

	public void addAttachment(String bugReportID, String comment, AbstractTaskAttachmentSource source,
			TaskAttribute attachmentAttribute, IOperationMonitor monitor) throws BugzillaRestException {
		new BugzillaRestPostNewAttachment(client, bugReportID, comment, source, attachmentAttribute, monitor)
				.run(monitor);
	}

}
