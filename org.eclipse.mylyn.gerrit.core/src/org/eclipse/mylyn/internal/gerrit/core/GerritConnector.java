/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *      Tasktop Technologies - improvements
 *      GitHub, Inc. - fixes for bug 354753
 *      Sascha Scholz (SAP) - improvements
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.core;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpStatus;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritAuthenticationState;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritConfiguration;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpException;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritLoginException;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritSystemInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.JSonSupport;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryLocationFactory;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;
import org.eclipse.osgi.util.NLS;

import com.google.gerrit.common.data.ChangeInfo;
import com.google.gwtorm.client.KeyUtil;
import com.google.gwtorm.server.StandardKeyEncoder;

/**
 * The Gerrit connector core.
 * 
 * @author Mikael Kober
 * @author Thomas Westling
 * @author Sascha Scholz
 */
public class GerritConnector extends AbstractRepositoryConnector {

	static Logger logger = Logger.getLogger("com.google.gson.ParameterizedTypeHandlerMap"); //$NON-NLS-1$

	static {
		KeyUtil.setEncoderImpl(new StandardKeyEncoder());
		// disable logging of Overriding the existing type handler for class java.sql.Timestamp message
		logger.setLevel(Level.OFF);
	}

	/**
	 * Prefix for task id in a task-url: http://[gerrit-repository]/#change,[task.id].
	 */
	public static final String CHANGE_PREFIX = "/#change,"; //$NON-NLS-1$

	/**
	 * Connector kind
	 */
	public static final String CONNECTOR_KIND = "org.eclipse.mylyn.gerrit"; //$NON-NLS-1$

	/**
	 * Label for the connector.
	 */
	public static final String CONNECTOR_LABEL = "Gerrit Code Review"; //$NON-NLS-1$

	public static final String KEY_REPOSITORY_CONFIG = CONNECTOR_KIND + ".config"; //$NON-NLS-1$

	public static final String KEY_REPOSITORY_AUTH = CONNECTOR_KIND + ".auth"; //$NON-NLS-1$

	public static final String KEY_REPOSITORY_ACCOUNT_ID = CONNECTOR_KIND + ".accountId"; //$NON-NLS-1$

	public static final String KEY_REPOSITORY_OPEN_ID_ENABLED = CONNECTOR_KIND + ".openId.enabled"; //$NON-NLS-1$

	public static final String KEY_REPOSITORY_OPEN_ID_PROVIDER = CONNECTOR_KIND + ".openId.provider"; //$NON-NLS-1$

	private final GerritTaskDataHandler taskDataHandler = new GerritTaskDataHandler(this);

	private TaskRepositoryLocationFactory taskRepositoryLocationFactory = new TaskRepositoryLocationFactory();

	private final ConcurrentMap<TaskRepository, GerritConfiguration> configurationCache = new ConcurrentHashMap<TaskRepository, GerritConfiguration>();

	public GerritConnector() {
		if (GerritCorePlugin.getDefault() != null) {
			GerritCorePlugin.getDefault().setConnector(this);
		}
	}

	/**
	 * Not supported, yet.
	 */
	@Override
	public boolean canCreateNewTask(TaskRepository arg0) {
		return false;
	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository arg0) {
		return true;
	}

	public GerritClient getClient(TaskRepository repository) {
		return createClient(repository, true);
	}

	@Override
	public String getConnectorKind() {
		return CONNECTOR_KIND;
	}

	@Override
	public String getLabel() {
		return "Gerrit Code Review (supports 2.3 and later)";
	}

	@Override
	public TaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		return taskDataHandler.getTaskData(repository, taskId, monitor);
	}

	@Override
	public AbstractTaskDataHandler getTaskDataHandler() {
		return taskDataHandler;
	}

	@Override
	public String getRepositoryUrlFromTaskUrl(String url) {
		if (url == null) {
			return null;
		}

		int i = url.indexOf(CHANGE_PREFIX);
		if (i != -1) {
			return url.substring(0, i);
		}
		return null;
	}

	@Override
	public String getTaskIdFromTaskUrl(String url) {
		if (url == null) {
			return null;
		}

		// example: https://review.sonyericsson.net/#change,14175
		int index = url.indexOf(CHANGE_PREFIX);
		if (index > 0) {
			return url.substring(index + CHANGE_PREFIX.length());
		}
		return null;
	}

	@Override
	public ITaskMapping getTaskMapping(TaskData taskData) {
		return new TaskMapper(taskData);
	}

	public synchronized TaskRepositoryLocationFactory getTaskRepositoryLocationFactory() {
		return taskRepositoryLocationFactory;
	}

	@Override
	public String getTaskUrl(String repositoryUrl, String taskId) {
		return repositoryUrl + CHANGE_PREFIX + taskId;
	}

	@Override
	public boolean hasTaskChanged(TaskRepository repository, ITask task, TaskData taskData) {
		ITaskMapping taskMapping = getTaskMapping(taskData);
		Date repositoryDate = taskMapping.getModificationDate();
		Date localDate = task.getModificationDate();
		if (repositoryDate != null && repositoryDate.equals(localDate)) {
			return false;
		}
		return true;
	}

	@Override
	public IStatus performQuery(TaskRepository repository, IRepositoryQuery query, TaskDataCollector resultCollector,
			ISynchronizationSession session, IProgressMonitor monitor) {
		try {
			monitor.beginTask("Executing query", IProgressMonitor.UNKNOWN);
			GerritClient client = getClient(repository);
			client.refreshConfigOnce(monitor);
			List<ChangeInfo> result = null;
			if (GerritQuery.ALL_OPEN_CHANGES.equals(query.getAttribute(GerritQuery.TYPE))) {
				result = client.queryAllReviews(monitor);
			} else if (GerritQuery.CUSTOM.equals(query.getAttribute(GerritQuery.TYPE))) {
				String queryString = query.getAttribute(GerritQuery.QUERY_STRING);
				result = client.executeQuery(monitor, queryString);
			} else if (GerritQuery.MY_CHANGES.equals(query.getAttribute(GerritQuery.TYPE))) {
				result = client.queryMyReviews(monitor);
			} else if (GerritQuery.MY_WATCHED_CHANGES.equals(query.getAttribute(GerritQuery.TYPE))) {
				result = client.queryWatchedReviews(monitor);
			} else if (GerritQuery.OPEN_CHANGES_BY_PROJECT.equals(query.getAttribute(GerritQuery.TYPE))) {
				String project = query.getAttribute(GerritQuery.PROJECT);
				result = client.queryByProject(monitor, project);
			}

			if (result != null) {
				for (ChangeInfo changeInfo : result) {
					TaskData taskData = taskDataHandler.createPartialTaskData(repository,
							changeInfo.getId() + "", monitor); //$NON-NLS-1$
					taskData.setPartial(true);
					taskDataHandler.updateTaskData(repository, taskData, changeInfo);
					resultCollector.accept(taskData);
				}
				return Status.OK_STATUS;
			} else {
				return new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID, NLS.bind("Unknows query type: {0}",
						query.getAttribute(GerritQuery.PROJECT)));
			}
		} catch (UnsupportedClassVersionError e) {
			return toStatus(repository, e);
		} catch (GerritException e) {
			return toStatus(repository, e);
		} finally {
			monitor.done();
		}
	}

	public synchronized void setTaskRepositoryLocationFactory(
			TaskRepositoryLocationFactory taskRepositoryLocationFactory) {
		this.taskRepositoryLocationFactory = taskRepositoryLocationFactory;
	}

	@Override
	public void updateRepositoryConfiguration(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		try {
			getClient(repository).refreshConfig(monitor);
		} catch (GerritException e) {
			throw toCoreException(repository, e);
		}
	}

	@Override
	public void updateTaskFromTaskData(TaskRepository taskRepository, ITask task, TaskData taskData) {
		Date oldModificationDate = task.getModificationDate();

		TaskMapper mapper = (TaskMapper) getTaskMapping(taskData);
		mapper.applyTo(task);
		String key = task.getTaskKey();
		if (key != null) {
			task.setSummary(NLS.bind("{0} [{1}]", mapper.getSummary(), key));
			task.setTaskKey(task.getTaskId());
		}

		// retain modification date to force an update when full task data is received
		if (taskData.isPartial()) {
			task.setModificationDate(oldModificationDate);
		}
	}

	public GerritSystemInfo validate(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		try {
			// only allow user prompting in case of Open ID authentication 
			if (!Boolean.parseBoolean(repository.getProperty(GerritConnector.KEY_REPOSITORY_OPEN_ID_ENABLED))) {
				monitor = Policy.backgroundMonitorFor(monitor);
			}
			return createClient(repository, false).getInfo(monitor);
		} catch (UnsupportedClassVersionError e) {
			throw toCoreException(repository, e);
		} catch (GerritException e) {
			throw toCoreException(repository, e);
		}
	}

	private GerritClient createClient(final TaskRepository repository, boolean cachedConfig) {
		GerritConfiguration config = (cachedConfig) ? loadConfiguration(repository) : null;
		GerritAuthenticationState authState = (cachedConfig)
				? GerritClient.authStateFromString(repository.getProperty(KEY_REPOSITORY_AUTH))
				: null;
		return new GerritClient(taskRepositoryLocationFactory.createWebLocation(repository), config, authState) {
			@Override
			protected void configurationChanged(GerritConfiguration config) {
				saveConfiguration(repository, config);
			}

			@Override
			protected void authStateChanged(GerritAuthenticationState authState) {
				repository.setProperty(KEY_REPOSITORY_AUTH, GerritClient.authStateToString(authState));
			}
		};
	}

	protected GerritConfiguration loadConfiguration(TaskRepository repository) {
		GerritConfiguration configuration = configurationCache.get(repository);
		if (configuration == null) {
			configuration = configurationFromString(repository.getProperty(KEY_REPOSITORY_CONFIG));
			if (configuration != null) {
				configurationCache.put(repository, configuration);
			}
		}
		return configuration;
	}

	protected void saveConfiguration(TaskRepository repository, GerritConfiguration configuration) {
		configurationCache.put(repository, configuration);
		repository.setProperty(KEY_REPOSITORY_CONFIG, configurationToString(configuration));
	}

	public GerritConfiguration getConfiguration(TaskRepository repository) {
		GerritConfiguration configuration = configurationCache.get(repository);
		if (configuration == null) {
			configuration = loadConfiguration(repository);
		}
		return configuration;
	}

	private static GerritConfiguration configurationFromString(String token) {
		try {
			JSonSupport support = new JSonSupport();
			return support.getGson().fromJson(token, GerritConfiguration.class);
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID,
					"Failed to deserialize configuration: '" + token + "'", e));
			return null;
		}
	}

	private static String configurationToString(GerritConfiguration config) {
		try {
			JSonSupport support = new JSonSupport();
			return support.getGson().toJson(config);
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID,
					"Failed to serialize configuration", e));
			return null;
		}
	}

	CoreException toCoreException(TaskRepository repository, GerritException e) {
		return new CoreException(toStatus(repository, e));
	}

	CoreException toCoreException(TaskRepository repository, UnsupportedClassVersionError e) {
		return new CoreException(toStatus(repository, e));
	}

	Status toStatus(TaskRepository repository, GerritException e) {
		String message;
		if (e instanceof GerritHttpException) {
			int code = ((GerritHttpException) e).getResponseCode();
			message = NLS.bind("Unexpected error: {1} ({0})", code, HttpStatus.getStatusText(code));
		} else if (e instanceof GerritLoginException) {
			message = "Login failed";
		} else if (e.getMessage() != null) {
			message = NLS.bind("Unexpected error: {0}", e.getMessage());
		} else {
			message = "Unexpected error while communicating with Gerrit";
		}
		return new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID, message, e);
	}

	Status toStatus(TaskRepository repository, UnsupportedClassVersionError e) {
		String message = NLS.bind("The Gerrit Connector requires at Java 1.6 or higer (installed version: {0})",
				System.getProperty("java.version"));
		return new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID, message, e);
	}

}
