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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.core.operations.OperationUtil;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.internal.commons.core.operations.NullOperationMonitor;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.RepositoryInfo;
import org.eclipse.mylyn.tasks.core.RepositoryVersion;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.UncheckedExecutionException;

public class BugzillaRestConnector extends AbstractRepositoryConnector {

	public static final Duration CLIENT_CACHE_DURATION = new Duration(24, TimeUnit.HOURS);

	public static final Duration CONFIGURATION_CACHE_EXPIRE_DURATION = new Duration(7, TimeUnit.DAYS);

	public static final Duration CONFIGURATION_CACHE_REFRESH_AFTER_WRITE_DURATION = new Duration(1, TimeUnit.DAYS);

	private final ExecutorService executor = Executors.newFixedThreadPool(3);

	private static final ThreadLocal<IOperationMonitor> context = new ThreadLocal<IOperationMonitor>();

	private boolean ignoredProperty(String propertyName) {
		if (propertyName.equals(RepositoryLocation.PROPERTY_LABEL) || propertyName.equals(TaskRepository.OFFLINE)
				|| propertyName.equals(IRepositoryConstants.PROPERTY_ENCODING)
				|| propertyName.equals(TaskRepository.PROXY_HOSTNAME) || propertyName.equals(TaskRepository.PROXY_PORT)
				|| propertyName.equals("org.eclipse.mylyn.tasklist.repositories.savePassword")
				|| propertyName.equals("org.eclipse.mylyn.tasklist.repositories.proxy.usedefault")
				|| propertyName.equals("org.eclipse.mylyn.tasklist.repositories.proxy.savePassword")
				|| propertyName.equals("org.eclipse.mylyn.tasklist.repositories.proxy.username")
				|| propertyName.equals("org.eclipse.mylyn.tasklist.repositories.proxy.password")
				|| propertyName.equals("org.eclipse.mylyn.tasklist.repositories.proxy.enabled")) {
			return true;
		}
		return false;
	}

	private final PropertyChangeListener repositoryChangeListener4ClientCache = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (ignoredProperty(evt.getPropertyName())) {
				return;
			}
			TaskRepository taskRepository = (TaskRepository) evt.getSource();
			clientCache.invalidate(new RepositoryKey(taskRepository));
		}
	};

	private final PropertyChangeListener repositoryChangeListener4ConfigurationCache = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (ignoredProperty(evt.getPropertyName())
					|| evt.getPropertyName().equals("org.eclipse.mylyn.tasklist.repositories.password")) {
				return;
			}
			TaskRepository taskRepository = (TaskRepository) evt.getSource();
			configurationCache.invalidate(new RepositoryKey(taskRepository));
		}
	};

	private final LoadingCache<RepositoryKey, BugzillaRestClient> clientCache = CacheBuilder.newBuilder()
			.expireAfterAccess(CLIENT_CACHE_DURATION.getValue(), CLIENT_CACHE_DURATION.getUnit())
			.build(new CacheLoader<RepositoryKey, BugzillaRestClient>() {

				@Override
				public BugzillaRestClient load(RepositoryKey key) throws Exception {
					TaskRepository repository = key.getRepository();
					repository.addChangeListener(repositoryChangeListener4ClientCache);
					return createClient(repository);
				}
			});

	private final LoadingCache<RepositoryKey, Optional<BugzillaRestConfiguration>> configurationCache;

	public BugzillaRestConnector() {
		this(CONFIGURATION_CACHE_REFRESH_AFTER_WRITE_DURATION);
	}

	public BugzillaRestConnector(Duration refreshAfterWriteDuration) {
		super();
		configurationCache = createCacheBuilder(CONFIGURATION_CACHE_EXPIRE_DURATION, refreshAfterWriteDuration)
				.build(new CacheLoader<RepositoryKey, Optional<BugzillaRestConfiguration>>() {

					@Override
					public Optional<BugzillaRestConfiguration> load(RepositoryKey key) throws Exception {
						BugzillaRestClient client = clientCache.get(key);
						TaskRepository repository = key.getRepository();
						repository.addChangeListener(repositoryChangeListener4ConfigurationCache);
						return Optional.fromNullable(client.getConfiguration(key.getRepository(), context.get()));
					}

					@Override
					public ListenableFuture<Optional<BugzillaRestConfiguration>> reload(final RepositoryKey key,
							Optional<BugzillaRestConfiguration> oldValue) throws Exception {
						// asynchronous!
						ListenableFutureJob<Optional<BugzillaRestConfiguration>> job = new ListenableFutureJob("") {

							@Override
							protected IStatus run(IProgressMonitor monitor) {
								BugzillaRestClient client;
								try {
									client = clientCache.get(key);
									set(Optional
											.fromNullable(client.getConfiguration(key.getRepository(), context.get())));
								} catch (ExecutionException e) {
									e.printStackTrace();
									return new Status(IStatus.ERROR, BugzillaRestCore.ID_PLUGIN,
											"BugzillaRestConnector reload Configuration", e);
								}
								return Status.OK_STATUS;
							}
						};
						job.schedule();
						return job;
					}
				});
	}

	protected CacheBuilder<Object, Object> createCacheBuilder(Duration expireAfterWriteDuration,
			Duration refreshAfterWriteDuration) {
		return CacheBuilder.newBuilder()
				.expireAfterWrite(expireAfterWriteDuration.getValue(), expireAfterWriteDuration.getUnit())
				.refreshAfterWrite(refreshAfterWriteDuration.getValue(), refreshAfterWriteDuration.getUnit());
	}

	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		return true;
	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		// ignore
		return false;
	}

	@Override
	public String getConnectorKind() {
		return BugzillaRestCore.CONNECTOR_KIND;
	}

	@Override
	public String getLabel() {
		return "Bugzilla 5.0 or later with REST";
	}

	@Override
	public String getRepositoryUrlFromTaskUrl(String taskUrl) {
		// ignore
		return null;
	}

	@Override
	public TaskData getTaskData(TaskRepository repository, String taskIdOrKey, IProgressMonitor monitor)
			throws CoreException {
		// ignore
		return null;
	}

	@Override
	public String getTaskIdFromTaskUrl(String taskUrl) {
		// ignore
		return null;
	}

	@Override
	public String getTaskUrl(String repositoryUrl, String taskIdOrKey) {
		// ignore
		return null;
	}

	@Override
	public boolean hasTaskChanged(TaskRepository taskRepository, ITask task, TaskData taskData) {
		// ignore
		return false;
	}

	@Override
	public IStatus performQuery(TaskRepository repository, IRepositoryQuery query, TaskDataCollector collector,
			ISynchronizationSession session, IProgressMonitor monitor) {
		// ignore
		return null;
	}

	@Override
	public void updateRepositoryConfiguration(TaskRepository taskRepository, IProgressMonitor monitor)
			throws CoreException {
		context.set(monitor != null ? OperationUtil.convert(monitor) : new NullOperationMonitor());
		configurationCache.refresh(new RepositoryKey(taskRepository));
		context.remove();
	}

	@Override
	public void updateTaskFromTaskData(TaskRepository taskRepository, ITask task, TaskData taskData) {
		// ignore

	}

	@Override
	public AbstractTaskDataHandler getTaskDataHandler() {
		return new BugzillaRestTaskDataHandler(this);
	}

	private BugzillaRestClient createClient(TaskRepository repository) {
		RepositoryLocation location = new RepositoryLocation(repository.getProperties());
		AuthenticationCredentials credentials1 = repository
				.getCredentials(org.eclipse.mylyn.commons.net.AuthenticationType.REPOSITORY);
		UserCredentials credentials = new UserCredentials(credentials1.getUserName(), credentials1.getPassword(), null,
				true);
		location.setCredentials(AuthenticationType.REPOSITORY, credentials);
		BugzillaRestClient client = new BugzillaRestClient(location);

		return client;
	}

	/**
	 * Returns the Client for the {@link TaskRepository}.
	 *
	 * @param repository
	 *            the {@link TaskRepository} object
	 * @return the client Object
	 * @throws CoreException
	 */
	public BugzillaRestClient getClient(TaskRepository repository) throws CoreException {
		try {
			return clientCache.get(new RepositoryKey(repository));
		} catch (ExecutionException e) {
			throw new CoreException(
					new Status(IStatus.ERROR, BugzillaRestCore.ID_PLUGIN, "TaskRepositoryManager is null"));
		}
	}

	@Override
	public RepositoryInfo validateRepository(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		try {
			BugzillaRestClient client = createClient(repository);
			if (!client.validate(OperationUtil.convert(monitor))) {
				throw new CoreException(
						new Status(IStatus.ERROR, BugzillaRestCore.ID_PLUGIN, "repository is invalide"));
			}
			BugzillaRestVersion version = client.getVersion(OperationUtil.convert(monitor));
			return new RepositoryInfo(new RepositoryVersion(version.toString()));
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaRestCore.ID_PLUGIN, e.getMessage(), e));
		}
	}

	public BugzillaRestConfiguration getRepositoryConfiguration(TaskRepository repository) throws CoreException {
		if (clientCache.getIfPresent(new RepositoryKey(repository)) == null) {
			getClient(repository);
		}
		try {
			Optional<BugzillaRestConfiguration> configurationOptional = configurationCache
					.get(new RepositoryKey(repository));
			return configurationOptional.isPresent() ? configurationOptional.get() : null;
		} catch (UncheckedExecutionException e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaRestCore.ID_PLUGIN, e.getMessage(), e));
		} catch (ExecutionException e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaRestCore.ID_PLUGIN, e.getMessage(), e));
		}
	}

	public void clearClientCache() {
		clientCache.invalidateAll();
	}

	public void clearConfigurationCache() {
		configurationCache.invalidateAll();
	}

	public void clearAllCaches() {
		clearClientCache();
		clearConfigurationCache();
	}

	@Override
	public boolean isRepositoryConfigurationStale(TaskRepository repository, IProgressMonitor monitor)
			throws CoreException {
		return false;
	}

}
