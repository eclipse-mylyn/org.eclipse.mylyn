/*******************************************************************************
 * Copyright (c) 2023 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.gitlab.core;

import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.core.operations.OperationUtil;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.gitlab.core.Duration;
import org.eclipse.mylyn.gitlab.core.GitlabConfiguration;
import org.eclipse.mylyn.gitlab.core.GitlabCoreActivator;
import org.eclipse.mylyn.internal.commons.core.operations.NullOperationMonitor;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.RepositoryInfo;
import org.eclipse.mylyn.tasks.core.RepositoryVersion;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

public class GitlabRepositoryConnector extends AbstractRepositoryConnector {

	public class RepositoryKey {
		private final TaskRepository repository;

		public RepositoryKey(@NonNull TaskRepository repository) {
			this.repository = repository;
		}

		public TaskRepository getRepository() {
			return repository;
		}

		@Override
		public int hashCode() {
			return repository.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}
			return repository.equals(((RepositoryKey) obj).getRepository());
		}
	}

	private static final ThreadLocal<IOperationMonitor> context = new ThreadLocal<>();

	private final LoadingCache<RepositoryKey, Optional<GitlabConfiguration>> configurationCache;

	private SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); //$NON-NLS-1$

	public static Duration CONFIGURATION_CACHE_EXPIRE_DURATION = new Duration(7, TimeUnit.DAYS);

	public static Duration CONFIGURATION_CACHE_REFRESH_AFTER_WRITE_DURATION = new Duration(1, TimeUnit.DAYS);

	public static Duration CLIENT_CACHE_DURATION = new Duration(24, TimeUnit.HOURS);

	private boolean ignoredProperty(String propertyName) {
		if (propertyName.equals(RepositoryLocation.PROPERTY_LABEL) || propertyName.equals(TaskRepository.OFFLINE)
				|| propertyName.equals(IRepositoryConstants.PROPERTY_ENCODING)
				|| propertyName.equals(TaskRepository.PROXY_HOSTNAME) || propertyName.equals(TaskRepository.PROXY_PORT)
				|| propertyName.equals("org.eclipse.mylyn.tasklist.repositories.savePassword") //$NON-NLS-1$
				|| propertyName.equals("org.eclipse.mylyn.tasklist.repositories.proxy.usedefault") //$NON-NLS-1$
				|| propertyName.equals("org.eclipse.mylyn.tasklist.repositories.proxy.savePassword") //$NON-NLS-1$
				|| propertyName.equals("org.eclipse.mylyn.tasklist.repositories.proxy.username") //$NON-NLS-1$
				|| propertyName.equals("org.eclipse.mylyn.tasklist.repositories.proxy.password") //$NON-NLS-1$
				|| propertyName.equals("org.eclipse.mylyn.tasklist.repositories.proxy.enabled")) { //$NON-NLS-1$
			return true;
		}
		return false;
	}

	private PropertyChangeListener repositoryChangeListener4ConfigurationCache = null;

	private PropertyChangeListener repositoryChangeListener4ClientCache;

	protected Caffeine<Object, Object> createCacheBuilder(Duration expireAfterWriteDuration,
			Duration refreshAfterWriteDuration) {
		return Caffeine.newBuilder()
				.expireAfterWrite(expireAfterWriteDuration.getValue(), expireAfterWriteDuration.getUnit())
				.refreshAfterWrite(refreshAfterWriteDuration.getValue(), refreshAfterWriteDuration.getUnit());
	}

	public GitlabRepositoryConnector() {
		this(CONFIGURATION_CACHE_REFRESH_AFTER_WRITE_DURATION);
	}

	public GitlabRepositoryConnector(Duration refreshAfterWriteDuration) {
		configurationCache = createCacheBuilder(CONFIGURATION_CACHE_EXPIRE_DURATION, refreshAfterWriteDuration)
				.build(key -> {
					GitlabRestClient client = clientCache.get(key);
					TaskRepository repository = key.getRepository();
					repository.addChangeListener(repositoryChangeListener4ConfigurationCache);
					return Optional.ofNullable(client.getConfiguration(key.getRepository(), context.get()));
				});

		repositoryChangeListener4ClientCache = evt -> {
			TaskRepository taskRepository = (TaskRepository) evt.getSource();
			clientCache.invalidate(new RepositoryKey(taskRepository));
		};

		repositoryChangeListener4ConfigurationCache = evt -> {
			if (ignoredProperty(evt.getPropertyName())
					|| evt.getPropertyName().equals("org.eclipse.mylyn.tasklist.repositories.password")) { //$NON-NLS-1$
				return;
			}
			TaskRepository taskRepository = (TaskRepository) evt.getSource();
			configurationCache.invalidate(new RepositoryKey(taskRepository));
		};
	}

	public GitlabConfiguration getRepositoryConfiguration(TaskRepository repository) throws CoreException {
		long startTime = 0, endTime = 0;
		String traceExitResult = ""; //$NON-NLS-1$
		if (GitlabCoreActivator.DEBUG_REPOSITORY_CONNECTOR) {
			GitlabCoreActivator.DEBUG_TRACE.traceEntry(null, repository.getUrl());
		}
		if (clientCache.getIfPresent(new RepositoryKey(repository)) == null) {
			getClient(repository);
		}
		try {
			if (GitlabCoreActivator.DEBUG_REPOSITORY_CONNECTOR) {
				startTime = System.currentTimeMillis();
			}

			Optional<GitlabConfiguration> configurationOptional = configurationCache.get(new RepositoryKey(repository));
			GitlabConfiguration result = configurationOptional.isPresent() ? configurationOptional.get() : null;
			if (GitlabCoreActivator.DEBUG_REPOSITORY_CONNECTOR) {
				endTime = System.currentTimeMillis();
				traceExitResult = result.toString() + " " + (endTime - startTime) + " ms"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			return result;
		} finally {
			if (GitlabCoreActivator.DEBUG_REPOSITORY_CONNECTOR) {
				GitlabCoreActivator.DEBUG_TRACE.traceExit(null, traceExitResult);
			}
		}
	}

	@Override
	public boolean canCreateNewTask(@NonNull TaskRepository repository) {
		return true;
	}

	@Override
	public boolean canCreateTaskFromKey(@NonNull TaskRepository repository) {
		return false;
	}

	@Override
	public @Nullable String getRepositoryUrlFromTaskUrl(@NonNull String taskUrl) {
		throw new UnsupportedOperationException();
	}

	@Override
	public @Nullable String getTaskIdFromTaskUrl(@NonNull String taskUrl) {
		throw new UnsupportedOperationException();
	}

	@Override
	public @Nullable String getTaskUrl(@NonNull String repositoryUrl, @NonNull String taskIdOrKey) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasTaskChanged(@NonNull TaskRepository taskRepository, @NonNull ITask task,
			@NonNull TaskData taskData) {
		String lastKnownLocalModValue = task.getModificationDate() != null
				? simpleFormatter.format(task.getModificationDate())
				: ""; //$NON-NLS-1$
		TaskAttribute latestRemoteModAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.DATE_MODIFICATION);
		String latestRemoteModValue = latestRemoteModAttribute != null ? latestRemoteModAttribute.getValue() : null;
		return !Objects.equals(latestRemoteModValue, lastKnownLocalModValue);
	}

	@Override
	public void updateRepositoryConfiguration(@NonNull TaskRepository taskRepository, @NonNull IProgressMonitor monitor)
			throws CoreException {
		context.set(monitor != null ? OperationUtil.convert(monitor) : new NullOperationMonitor());
		configurationCache.invalidate(new RepositoryKey(taskRepository));
		getRepositoryConfiguration(taskRepository);
		context.remove();
	}

	@Override
	public String getConnectorKind() {
		return GitlabCoreActivator.CONNECTOR_KIND;
	}

	@Override
	public String getLabel() {
		return "Gitlab"; //$NON-NLS-1$
	}

	public class SingleTaskDataCollector extends TaskDataCollector {
		final TaskData[] retrievedData = new TaskData[1];

		@Override
		public void accept(TaskData taskData) {
			retrievedData[0] = taskData;
		}

		public TaskData getTaskData() {
			return retrievedData[0];
		}

	}

	@Override
	public TaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		Set<String> taskIds = new HashSet<>();
		taskIds.add(taskId);
		SingleTaskDataCollector singleTaskDataCollector = new SingleTaskDataCollector();
		getTaskDataHandler().getMultiTaskData(repository, taskIds, singleTaskDataCollector, monitor);

		if (singleTaskDataCollector.getTaskData() == null) {
			throw new CoreException(new Status(IStatus.ERROR, GitlabCoreActivator.PLUGIN_ID,
					"Task data could not be retrieved. Please re-synchronize task")); //$NON-NLS-1$
		}
		return singleTaskDataCollector.getTaskData();
	}

	@Override
	public IStatus performQuery(TaskRepository repository, IRepositoryQuery query, TaskDataCollector collector,
			ISynchronizationSession session, IProgressMonitor monitor) {
		monitor.beginTask("performQuery", IProgressMonitor.UNKNOWN); //$NON-NLS-1$
		GitlabRestClient client;
		try {
			client = getClient(repository);
			IOperationMonitor progress = OperationUtil.convert(monitor, "performQuery", 3); //$NON-NLS-1$
			client.getIssues(query, collector, new NullOperationMonitor());
		} catch (CoreException e) {
			return new Status(IStatus.ERROR, GitlabCoreActivator.PLUGIN_ID, IStatus.INFO,
					"CoreException from performQuery", e); //$NON-NLS-1$
		}
		return Status.OK_STATUS;
	}

	@Override
	public void updateTaskFromTaskData(TaskRepository taskRepository, ITask task, TaskData taskData) {
		TaskMapper scheme = getTaskMapping(taskData);
		scheme.applyTo(task);
	}

	@Override
	public AbstractTaskDataHandler getTaskDataHandler() {
		return new GitlabTaskDataHandler(this);
	}

	@Override
	public RepositoryInfo validateRepository(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		try {
			GitlabRestClient client = createClient(repository);
			if (!client.validate(OperationUtil.convert(monitor))) {
				throw new CoreException(
						new Status(IStatus.ERROR, GitlabCoreActivator.PLUGIN_ID, "repository is invalide")); //$NON-NLS-1$
			}
			return new RepositoryInfo(new RepositoryVersion(client.getVersion(OperationUtil.convert(monitor))));
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, GitlabCoreActivator.PLUGIN_ID, e.getMessage(), e));
		}
	}

	public GitlabRestClient getClient(TaskRepository repository) throws CoreException {
		return clientCache.get(new RepositoryKey(repository));
	}

	private final LoadingCache<RepositoryKey, GitlabRestClient> clientCache = Caffeine.newBuilder()
			.expireAfterAccess(CLIENT_CACHE_DURATION.getValue(), CLIENT_CACHE_DURATION.getUnit())
			.build(key -> {
				TaskRepository repository = key.getRepository();
				repository.addChangeListener(repositoryChangeListener4ClientCache);
				return createClient(repository);
			});

	private final LoadingCache<String, byte[]> avatarCache = Caffeine.newBuilder()
			.expireAfterAccess(CLIENT_CACHE_DURATION.getValue(), CLIENT_CACHE_DURATION.getUnit())
			.build(key -> {
				byte[] avatarBytes = null;
				HttpURLConnection connection;

				connection = (HttpURLConnection) new URL(key).openConnection();
				connection.setConnectTimeout(30000);
				connection.setUseCaches(false);
				connection.connect();

				if (connection.getResponseCode() == 200) {

					try (ByteArrayOutputStream output = new ByteArrayOutputStream();
							InputStream input = connection.getInputStream()) {
						byte[] buffer = new byte[8192];
						int read = -1;
						while ((read = input.read(buffer)) != -1) {
							output.write(buffer, 0, read);
						}

						avatarBytes = output.toByteArray();
					}
				}
				return avatarBytes;
			});

	public GitlabRestClient createClient(TaskRepository repository) {
		RepositoryLocation location = new RepositoryLocation(convertProperties(repository));
		AuthenticationCredentials credentials1 = repository
				.getCredentials(org.eclipse.mylyn.commons.net.AuthenticationType.REPOSITORY);
		UserCredentials credentials = new UserCredentials(credentials1.getUserName(), credentials1.getPassword(), null,
				true);
		location.setCredentials(AuthenticationType.REPOSITORY, credentials);
		GitlabRestClient client = new GitlabRestClient(location, this, repository);

		return client;
	}

	private Map<String, String> convertProperties(TaskRepository repository) {
		return repository.getProperties()
				.entrySet()
				.stream()
				.collect(Collectors.toMap(e -> convertProperty(e.getKey()), Map.Entry::getValue));
	}

	@SuppressWarnings("restriction")
	private String convertProperty(String key) {
		if (TaskRepository.PROXY_USEDEFAULT.equals(key)) {
			return RepositoryLocation.PROPERTY_PROXY_USEDEFAULT;
		} else if (TaskRepository.PROXY_HOSTNAME.equals(key)) {
			return RepositoryLocation.PROPERTY_PROXY_HOST;
		} else if (TaskRepository.PROXY_PORT.equals(key)) {
			return RepositoryLocation.PROPERTY_PROXY_PORT;
		}
		return key;
	}

	@Override
	public TaskMapper getTaskMapping(final TaskData taskData) {

		return new TaskMapper(taskData) {
			@Override
			public String getTaskKey() {
				TaskAttribute attribute = getTaskData().getRoot()
						.getAttribute(GitlabTaskSchema.getDefault().TASK_KEY.getKey());
				if (attribute != null) {
					return attribute.getValue();
				}
				return super.getTaskKey();
			}

			@Override
			public String getTaskKind() {
				return taskData.getConnectorKind();
			}
		};
	}

	public byte[] getAvatarData(String url) {
		return avatarCache.get(url);
	}

}
