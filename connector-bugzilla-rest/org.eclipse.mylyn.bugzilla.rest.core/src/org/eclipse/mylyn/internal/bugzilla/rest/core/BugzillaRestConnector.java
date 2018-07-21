/*******************************************************************************
 * Copyright (c) 2013 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.core.operations.OperationUtil;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.Field;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.FieldValues;
import org.eclipse.mylyn.internal.commons.core.operations.NullOperationMonitor;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.RepositoryInfo;
import org.eclipse.mylyn.tasks.core.RepositoryVersion;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;

import com.google.common.base.Objects;
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

	private static final ThreadLocal<IOperationMonitor> context = new ThreadLocal<IOperationMonitor>();

	private BugzillaRestTaskAttachmentHandler attachmentHandler;

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
					|| evt.getPropertyName().equals("org.eclipse.mylyn.tasklist.repositories.password")) { //$NON-NLS-1$
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
		this.attachmentHandler = new BugzillaRestTaskAttachmentHandler(this);
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
						ListenableFutureJob<Optional<BugzillaRestConfiguration>> job = new ListenableFutureJob<Optional<BugzillaRestConfiguration>>(
								"") {

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
		if (taskUrl == null) {
			return null;
		}
		int index = taskUrl.indexOf("/rest.cgi/"); //$NON-NLS-1$
		return index == -1 ? null : taskUrl.substring(0, index);
	}

	@Override
	public TaskData getTaskData(TaskRepository repository, String taskIdOrKey, IProgressMonitor monitor)
			throws CoreException {
		return ((BugzillaRestTaskDataHandler) getTaskDataHandler()).getTaskData(repository, taskIdOrKey, monitor);
	}

	@Override
	public String getTaskIdFromTaskUrl(String taskUrl) {
		// ignore
		return null;
	}

	@Override
	public String getTaskUrl(String repositoryUrl, String taskIdOrKey) {
		return repositoryUrl + "/rest.cgi/bug/" + taskIdOrKey; //$NON-NLS-1$
	}

	@Override
	public boolean hasTaskChanged(TaskRepository taskRepository, ITask task, TaskData taskData) {
		String lastKnownLocalModValue = task
				.getAttribute(BugzillaRestTaskSchema.getDefault().DATE_MODIFICATION.getKey());
		TaskAttribute latestRemoteModAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.DATE_MODIFICATION);
		String latestRemoteModValue = latestRemoteModAttribute != null ? latestRemoteModAttribute.getValue() : null;
		return !Objects.equal(latestRemoteModValue, lastKnownLocalModValue);
	}

	@Override
	public IStatus performQuery(TaskRepository repository, IRepositoryQuery query, TaskDataCollector collector,
			ISynchronizationSession session, IProgressMonitor monitor) {
		monitor = Policy.monitorFor(monitor);
		try {
			monitor.beginTask("performQuery", IProgressMonitor.UNKNOWN);
			BugzillaRestClient client = getClient(repository);
			IOperationMonitor progress = OperationUtil.convert(monitor, "performQuery", 3); //$NON-NLS-1$
			return client.performQuery(repository, query, collector, progress);
		} catch (CoreException e) {
			return new Status(IStatus.ERROR, BugzillaRestCore.ID_PLUGIN, IStatus.INFO,
					"CoreException from performQuery", e);
		} catch (BugzillaRestException e) {
			return new Status(IStatus.ERROR, BugzillaRestCore.ID_PLUGIN, IStatus.INFO,
					"BugzillaRestException from performQuery", e);
		} finally {
			monitor.done();
		}
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
		TaskMapper scheme = getTaskMapping(taskData);
		scheme.applyTo(task);
		task.setUrl(taskData.getRepositoryUrl() + "/rest.cgi/bug/" + taskData.getTaskId()); //$NON-NLS-1$

		boolean isComplete = false;
		TaskAttribute attributeStatus = taskData.getRoot().getMappedAttribute(TaskAttribute.STATUS);
		if (attributeStatus != null) {
			try {
				BugzillaRestConfiguration configuration;
				configuration = getRepositoryConfiguration(taskRepository);
				if (configuration != null) {
					Field stat = configuration.getFieldWithName(IBugzillaRestConstants.BUG_STATUS);
					for (FieldValues fieldValue : stat.getValues()) {
						if (attributeStatus.getValue().equals(fieldValue.getName())) {
							isComplete = !fieldValue.isOpen();
						}
					}
				}
			} catch (CoreException e) {
				StatusHandler.log(new Status(IStatus.ERROR, BugzillaRestCore.ID_PLUGIN,
						"Error during get BugzillaRestConfiguration", e));
			}
		}
		if (taskData.isPartial()) {
			if (isComplete) {
				if (task.getCompletionDate() == null) {
					task.setCompletionDate(new Date(0));
				}
			} else {
				task.setCompletionDate(null);
			}
		} else {
			inferCompletionDate(task, taskData, scheme, isComplete);
		}

	}

	private void inferCompletionDate(ITask task, TaskData taskData, TaskMapper scheme, boolean isComplete) {
		if (isComplete) {
			Date completionDate = null;
			List<TaskAttribute> taskComments = taskData.getAttributeMapper().getAttributesByType(taskData,
					TaskAttribute.TYPE_COMMENT);
			if (taskComments != null && taskComments.size() > 0) {
				TaskAttribute lastComment = taskComments.get(taskComments.size() - 1);
				if (lastComment != null) {
					TaskAttribute attributeCommentDate = lastComment.getMappedAttribute(TaskAttribute.COMMENT_DATE);
					if (attributeCommentDate != null) {
						completionDate = new Date(Long.parseLong(attributeCommentDate.getValue()));
					}
				}
			}
			if (completionDate == null) {
				// Use last modified date
				TaskAttribute attributeLastModified = taskData.getRoot()
						.getMappedAttribute(TaskAttribute.DATE_MODIFICATION);
				if (attributeLastModified != null && attributeLastModified.getValue().length() > 0) {
					completionDate = taskData.getAttributeMapper().getDateValue(attributeLastModified);
				}
			}
			task.setCompletionDate(completionDate);
		} else {
			task.setCompletionDate(null);
		}
		// Bugzilla Specific Attributes

		// Product
		if (scheme.getProduct() != null) {
			task.setAttribute(BugzillaRestTaskSchema.getDefault().PRODUCT.getKey(), scheme.getProduct());
		}

		// Severity
		TaskAttribute attrSeverity = taskData.getRoot()
				.getMappedAttribute(BugzillaRestTaskSchema.getDefault().SEVERITY.getKey());
		if (attrSeverity != null && !attrSeverity.getValue().equals("")) { //$NON-NLS-1$
			task.setAttribute(BugzillaRestTaskSchema.getDefault().SEVERITY.getKey(), attrSeverity.getValue());
		}

		// Severity
		TaskAttribute attrDelta = taskData.getRoot()
				.getAttribute(BugzillaRestTaskSchema.getDefault().DATE_MODIFICATION.getKey());
		if (attrDelta != null && !attrDelta.getValue().equals("")) { //$NON-NLS-1$
			task.setAttribute(BugzillaRestTaskSchema.getDefault().DATE_MODIFICATION.getKey(), attrDelta.getValue());
		}
	}

	@Override
	public AbstractTaskDataHandler getTaskDataHandler() {
		return new BugzillaRestTaskDataHandler(this);
	}

	private BugzillaRestClient createClient(TaskRepository repository) {
		RepositoryLocation location = new RepositoryLocation(convertProperties(repository));
		AuthenticationCredentials credentials1 = repository
				.getCredentials(org.eclipse.mylyn.commons.net.AuthenticationType.REPOSITORY);
		UserCredentials credentials = new UserCredentials(credentials1.getUserName(), credentials1.getPassword(), null,
				true);
		location.setCredentials(AuthenticationType.REPOSITORY, credentials);
		BugzillaRestClient client = new BugzillaRestClient(location, this);

		return client;
	}

	private Map<String, String> convertProperties(TaskRepository repository) {
		return repository.getProperties().entrySet().stream().collect(
				Collectors.toMap(e -> convertProperty(e.getKey()), Map.Entry::getValue));
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

	@Override
	public TaskMapper getTaskMapping(final TaskData taskData) {

		return new TaskMapper(taskData) {
			@Override
			public String getTaskKey() {
				TaskAttribute attribute = getTaskData().getRoot()
						.getAttribute(BugzillaRestTaskSchema.getDefault().BUG_ID.getKey());
				if (attribute != null) {
					return attribute.getValue();
				}
				return super.getTaskKey();
			}

			@Override
			public String getTaskKind() {
				return taskData.getConnectorKind();
			}

			@Override
			public String getTaskUrl() {
				return taskData.getRepositoryUrl();
			}
		};
	}

	@Override
	@Nullable
	public AbstractTaskAttachmentHandler getTaskAttachmentHandler() {
		return attachmentHandler;
	}

	@Override
	@Nullable
	public URL getAuthenticatedUrl(@NonNull TaskRepository repository, @NonNull IRepositoryElement element) {
		if (element instanceof ITask) {
			try {
				String url = element.getUrl();
				String urlString = url.replace("/rest.cgi/bug/", "/show_bug.cgi?id="); //$NON-NLS-1$ //$NON-NLS-2$
				return new URL(urlString);
			} catch (MalformedURLException e) {
				StatusHandler.log(
						new Status(IStatus.ERROR, BugzillaRestCore.ID_PLUGIN, "could not create url from string", e)); //$NON-NLS-1$
			}
		}
		return super.getAuthenticatedUrl(repository, element);
	}

}
