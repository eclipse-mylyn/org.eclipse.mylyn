/*********************************************************************
 * Copyright (c) 2010, 2015 Sony Ericsson/ST Ericsson and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *      Tasktop Technologies - improvements
 *      GitHub, Inc. - fixes for bug 354753
 *      Sascha Scholz (SAP) - improvements
 *      Francois Chouinard (Ericsson) - Bug 414253 Add support for Gerrit Dashboard
 *      Jacques Bouthillier (Ericsson) - Bug 414253 Add support for Gerrit Dashboard
 *      Jacques Bouthillier (Ericsson) - Bug 426505 Add Starred functionality
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.core;

import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritAuthenticationState;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritCapabilities;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClientStateListener;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritConfiguration;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpException;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritLoginException;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritRestClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritSystemInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.JSonSupport;
import org.eclipse.mylyn.internal.gerrit.core.client.data.GerritQueryResult;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.reviews.core.model.ReviewStatus;
import org.eclipse.mylyn.reviews.core.spi.ReviewsConnector;
import org.eclipse.mylyn.reviews.internal.core.ReviewsCoreConstants;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryLocationFactory;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;
import org.eclipse.osgi.util.NLS;

import com.google.gwtorm.client.KeyUtil;
import com.google.gwtorm.server.StandardKeyEncoder;

/**
 * The Gerrit connector core.
 *
 * @author Mikael Kober
 * @author Thomas Westling
 * @author Sascha Scholz
 * @author Miles Parker
 * @author Francois Chouinard
 * @author Jacques Bouthillier
 */
public class GerritConnector extends ReviewsConnector {

	static Logger logger = Logger.getLogger("com.google.gson.ParameterizedTypeHandlerMap"); //$NON-NLS-1$

	static {
		KeyUtil.setEncoderImpl(new StandardKeyEncoder());
		// disable logging of Overriding the existing type handler for class java.sql.Timestamp message
		logger.setLevel(Level.OFF);
	}

	private static final Pattern CHANGE_ID_PATTERN = Pattern.compile("(/#change,|/#/c/)(\\d+)"); //$NON-NLS-1$

	/**
	 * Prefix for task id in a task-url: http://[gerrit-repository]/#change,[task.id] for Gerrit 2.1.
	 */
	public static final String CHANGE_PREFIX_OLD = "/#change,"; //$NON-NLS-1$

	/**
	 * Prefix for task id in a task-url: http://[gerrit-repository]/#/c/[task.id] for Gerrit 2.2 and later.
	 */
	public static final String CHANGE_PREFIX_NEW = "/#/c/"; //$NON-NLS-1$

	/**
	 * Connector kind
	 */
	public static final String CONNECTOR_KIND = "org.eclipse.mylyn.gerrit"; //$NON-NLS-1$

	public static final String KEY_REPOSITORY_CONFIG = CONNECTOR_KIND + ".config"; //$NON-NLS-1$

	public static final String KEY_REPOSITORY_AUTH = CONNECTOR_KIND + ".auth"; //$NON-NLS-1$

	public static final String KEY_REPOSITORY_ACCOUNT_ID = CONNECTOR_KIND + ".accountId"; //$NON-NLS-1$

	public static final String KEY_REPOSITORY_OPEN_ID_ENABLED = CONNECTOR_KIND + ".openId.enabled"; //$NON-NLS-1$

	public static final String KEY_REPOSITORY_OPEN_ID_PROVIDER = CONNECTOR_KIND + ".openId.provider"; //$NON-NLS-1$

	public static final String GERRIT_RPC_URI = "/gerrit/rpc/"; //$NON-NLS-1$

	public static final String GERRIT_260_RPC_URI = "/gerrit_ui/rpc/"; //$NON-NLS-1$

	private final GerritTaskDataHandler taskDataHandler = new GerritTaskDataHandler(this);

	private TaskRepositoryLocationFactory taskRepositoryLocationFactory = new TaskRepositoryLocationFactory();

	private final ConcurrentMap<TaskRepository, GerritConfiguration> configurationCache = new ConcurrentHashMap<>();

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
		return (GerritClient) getReviewClient(repository);
	}

	@Override
	public String getConnectorKind() {
		return CONNECTOR_KIND;
	}

	@Override
	public String getLabel() {
		return NLS.bind(Messages.GerritConnector_Label, GerritCapabilities.MINIMUM_SUPPORTED_VERSION,
				GerritCapabilities.MAXIMUM_SUPPORTED_VERSION);
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

		int i = url.indexOf(CHANGE_PREFIX_OLD);
		if (i != -1) {
			return url.substring(0, i);
		}

		i = url.indexOf(CHANGE_PREFIX_NEW);
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
		// example: https://review.sonyericsson.net/#/c/14175
		Matcher matcher = CHANGE_ID_PATTERN.matcher(url);
		if (matcher.find()) {
			return matcher.group(2);
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
		repositoryUrl = StringUtils.removeEnd(repositoryUrl, "/"); //$NON-NLS-1$
		return repositoryUrl + CHANGE_PREFIX_NEW + taskId + "/"; //$NON-NLS-1$
	}

	@Override
	public boolean hasTaskChanged(TaskRepository repository, ITask task, TaskData taskData) {
		ITaskMapping taskMapping = getTaskMapping(taskData);
		Date repositoryDate = taskMapping.getModificationDate();
		Date localDate = task.getModificationDate();
		if (areMillisecondsMissingFromLocalDate(localDate, repositoryDate)) {
			return false;
		}
		return repositoryDate == null || !repositoryDate.equals(localDate);
	}

	protected boolean areMillisecondsMissingFromLocalDate(Date localDate, Date repositoryDate) {
		if (localDate == null || repositoryDate == null) {
			return false;
		}
		Calendar repositoryCalendar = Calendar.getInstance();
		repositoryCalendar.setTime(repositoryDate);
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.setTime(localDate);
		return localCalendar.get(Calendar.MILLISECOND) == 0 && repositoryCalendar.get(Calendar.MILLISECOND) != 0
				&& Math.abs(repositoryCalendar.getTimeInMillis() - localCalendar.getTimeInMillis()) < 1000;
	}

	@Override
	public IStatus performQuery(TaskRepository repository, IRepositoryQuery query, TaskDataCollector resultCollector,
			ISynchronizationSession session, IProgressMonitor monitor) {
		try {
			monitor.beginTask(Messages.GerritConnector_Executing_query, IProgressMonitor.UNKNOWN);
			GerritClient client = getClient(repository);
			client.refreshConfigOnce(monitor);
			GerritRestClient restClient = client.getRestClient();

			List<GerritQueryResult> result = null;
			if (GerritQuery.ALL_OPEN_CHANGES.equals(query.getAttribute(GerritQuery.TYPE))) {
				result = restClient.queryAllReviews(monitor);
			} else if (GerritQuery.MY_CHANGES.equals(query.getAttribute(GerritQuery.TYPE))) {
				result = restClient.queryMyReviews(monitor);
			} else if (GerritQuery.MY_WATCHED_CHANGES.equals(query.getAttribute(GerritQuery.TYPE))) {
				result = restClient.queryWatchedReviews(monitor);
			} else if (GerritQuery.CUSTOM.equals(query.getAttribute(GerritQuery.TYPE))) {
				String queryString = query.getAttribute(GerritQuery.QUERY_STRING);
				result = restClient.executeQuery(monitor, queryString);
			} else if (GerritQuery.OPEN_CHANGES_BY_PROJECT.equals(query.getAttribute(GerritQuery.TYPE))) {
				String project = query.getAttribute(GerritQuery.PROJECT);
				result = restClient.queryByProject(monitor, project);
			} else {
				String queryString = query.getAttribute(GerritQuery.QUERY_STRING);
				if (StringUtils.isNotBlank(queryString)) {
					result = restClient.executeQuery(monitor, queryString);
				}
			}

			if (result != null) {
				for (GerritQueryResult changeInfo : result) {
					TaskData taskData = taskDataHandler.createPartialTaskData(repository,
							Integer.toString(changeInfo.getNumber()), monitor);
					taskDataHandler.updatePartialTaskData(repository, taskData, changeInfo);
					if (monitor.isCanceled()) {
						break;
					}

					resultCollector.accept(taskData);
				}
				return Status.OK_STATUS;
			}

			return new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID, NLS.bind("Unknown query type: {0}", //$NON-NLS-1$
					query.getAttribute(GerritQuery.PROJECT)));
		} catch (UnsupportedClassVersionError e) {
			return toStatus(repository, e);
		} catch (GerritException e) {
			return toStatus(repository, "Problem performing query", e); //$NON-NLS-1$
		} finally {
			monitor.done();
		}
	}

	public synchronized void setTaskRepositoryLocationFactory(
			TaskRepositoryLocationFactory taskRepositoryLocationFactory) {
		this.taskRepositoryLocationFactory = taskRepositoryLocationFactory;
	}

	@Override
	public void updateRepositoryConfiguration(TaskRepository repository, IProgressMonitor monitor)
			throws CoreException {
		try {
			getClient(repository).refreshConfig(monitor);
		} catch (GerritException e) {
			throw toCoreException(repository, "Problem updating repository", e); //$NON-NLS-1$
		}
	}

	@Override
	public void updateTaskFromTaskData(TaskRepository taskRepository, ITask task, TaskData taskData) {
		Date oldModificationDate = task.getModificationDate();

		TaskMapper mapper = (TaskMapper) getTaskMapping(taskData);
		mapper.applyTo(task);
		String key = task.getTaskKey();
		if (key != null) {
			task.setSummary(NLS.bind("{0} [{1}]", mapper.getSummary(), key)); //$NON-NLS-1$
			task.setTaskKey(task.getTaskId());
		}

		// retain modification date to force an update when full task data is received
		if (taskData.isPartial()) {
			task.setModificationDate(oldModificationDate);
		}

		GerritTaskSchema schema = GerritTaskSchema.getDefault();
		TaskAttribute status = taskData.getRoot().getMappedAttribute(TaskAttribute.STATUS);
		TaskAttribute codeReview = taskData.getRoot().getAttribute(schema.REVIEW_STATE.getKey());
		TaskAttribute verified = taskData.getRoot().getAttribute(schema.VERIFY_STATE.getKey());
		TaskAttribute project = taskData.getRoot().getAttribute(schema.PROJECT.getKey());
		TaskAttribute branch = taskData.getRoot().getAttribute(schema.BRANCH.getKey());

		setAttribute(task, TaskAttribute.STATUS, status);
		setAttribute(task, ReviewsCoreConstants.CODE_REVIEW, codeReview);
		setAttribute(task, ReviewsCoreConstants.VERIFIED, verified);
		setAttribute(task, ReviewsCoreConstants.BRANCH, branch);

		addExtendedTooltip(task, project);

		super.updateTaskFromTaskData(taskRepository, task, taskData);
	}

	@SuppressWarnings("restriction")
	private void addExtendedTooltip(ITask task, TaskAttribute projectAttribute) {
		String projectValue = projectAttribute == null ? null : projectAttribute.getValue();
		String branchValue = task.getAttribute(ReviewsCoreConstants.BRANCH);
		String codeReviewValue = task.getAttribute(ReviewsCoreConstants.CODE_REVIEW);
		String verifiedValue = task.getAttribute(ReviewsCoreConstants.VERIFIED);

		String projectTooltip = null;
		if (StringUtils.isNotEmpty(projectValue)) {
			projectTooltip = NLS.bind(Messages.GerritConnector_ProjectTooltip, projectValue);
		}
		String branchTooltip = null;
		if (StringUtils.isNotEmpty(branchValue)) {
			branchTooltip = NLS.bind(Messages.GerritConnector_BranchTooltip, branchValue);
		}
		String reviewTooltip = createVoteTooltipText(Messages.GerritConnector_CodeReviewTooltip, codeReviewValue);
		String verifiedTooltip = createVoteTooltipText(Messages.GerritConnector_VerifiedTooltip, verifiedValue);

		String tooltip = Stream.of(projectTooltip, branchTooltip, reviewTooltip, verifiedTooltip)
				.filter(Objects::nonNull)
				.collect(Collectors.joining("\n")); //$NON-NLS-1$
		if (!tooltip.isEmpty()) {
			task.setAttribute(ITasksCoreConstants.ATTRIBUTE_TASK_EXTENDED_TOOLTIP, tooltip);
		}
	}

	private String createVoteTooltipText(String format, String integerString) {
		int value = tryParseInt(integerString);
		if (value != 0) {
			String sign;
			if (value > 0) {
				sign = "+"; //$NON-NLS-1$
			} else {
				sign = ""; //$NON-NLS-1$
			}
			return NLS.bind(format, sign, value);
		}
		return null;
	}

	private int tryParseInt(String integerString) {
		try {
			return Integer.parseInt(integerString);
		} catch (NumberFormatException e) {
			// ignore
		}
		return 0;
	}

	private void setAttribute(ITask task, String key, TaskAttribute attribute) {
		if (attribute != null) {
			task.setAttribute(key, attribute.getValue());
		}
	}

	public GerritSystemInfo validate(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		// only allow user prompting in case of Open ID authentication
		if (!Boolean.parseBoolean(repository.getProperty(GerritConnector.KEY_REPOSITORY_OPEN_ID_ENABLED))) {
			monitor = Policy.backgroundMonitorFor(monitor);
		}
		try {
			return createTransientReviewClient(repository).getInfo(monitor);
		} catch (UnsupportedClassVersionError e) {
			throw toCoreException(repository, e);
		} catch (GerritException e) {
			throw toCoreException(repository, "Invalid repository", e); //$NON-NLS-1$
		}
	}

	@Override
	protected GerritClient createReviewClient(final TaskRepository repository, boolean b) {
		GerritConfiguration config = loadConfiguration(repository);
		GerritAuthenticationState authState = loadAuthState(repository);
		return GerritClient.create(repository, taskRepositoryLocationFactory.createWebLocation(repository), config,
				authState, null, new GerritClientStateListener() {
					@Override
					protected void configurationChanged(GerritConfiguration config) {
						saveConfiguration(repository, config);
					}

					@Override
					protected void authStateChanged(GerritAuthenticationState authState) {
						repository.setProperty(KEY_REPOSITORY_AUTH, authStateToString(authState));
					}
				});
	}

	private static String authStateToString(GerritAuthenticationState authState) {
		if (authState == null) {
			return null;
		}
		try {
			JSonSupport support = new JSonSupport();
			return support.toJson(authState);
		} catch (Exception e) {
			// ignore
			return null;
		}
	}

	protected GerritClient createTransientReviewClient(final TaskRepository repository) {
		return GerritClient.create(repository, taskRepositoryLocationFactory.createWebLocation(repository));
	}

	private GerritAuthenticationState loadAuthState(final TaskRepository repository) {
		String authState = repository.getProperty(KEY_REPOSITORY_AUTH);
		if (authState != null) {
			return authStateFromString(authState);
		}
		return null;
	}

	private static GerritAuthenticationState authStateFromString(String token) {
		try {
			JSonSupport support = new JSonSupport();
			return support.parseResponse(token, GerritAuthenticationState.class);
		} catch (Exception e) {
			// ignore
			return null;
		}
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
		if (token == null) {
			return null;
		}
		try {
			JSonSupport support = new JSonSupport();
			return support.parseResponse(token, GerritConfiguration.class);
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID,
					"Failed to deserialize configuration: '" + token + "'", e)); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
	}

	private static String configurationToString(GerritConfiguration config) {
		try {
			JSonSupport support = new JSonSupport();
			return support.toJson(config);
		} catch (Exception e) {
			StatusHandler
					.log(new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID, "Failed to serialize configuration", e)); //$NON-NLS-1$
			return null;
		}
	}

	public CoreException toCoreException(TaskRepository repository, String qualifier, GerritException e) {
		return new CoreException(toStatus(repository, qualifier, e));
	}

	public static CoreException toCoreException(TaskRepository repository, UnsupportedClassVersionError e) {
		return new CoreException(toStatus(repository, e));
	}

	Status toStatus(TaskRepository repository, String qualifier, Exception e) {
		if (StringUtils.isEmpty(qualifier)) {
			qualifier = ""; //$NON-NLS-1$
		} else if (!StringUtils.endsWith(qualifier, ": ")) { //$NON-NLS-1$
			qualifier += ": "; //$NON-NLS-1$
		}
		if (e instanceof GerritHttpException) {
			int code = ((GerritHttpException) e).getResponseCode();
			return createErrorStatus(repository, qualifier + HttpStatus.getStatusText(code));
		} else if (e instanceof GerritLoginException) {
			if (repository != null) {
				return RepositoryStatus.createLoginError(repository.getUrl(), GerritCorePlugin.PLUGIN_ID);
			} else {
				return createErrorStatus(null, qualifier + "Unknown Host"); //$NON-NLS-1$
			}
		} else if (e instanceof UnknownHostException) {
			return createErrorStatus(repository, qualifier + "Unknown Host"); //$NON-NLS-1$
		} else if (e instanceof GerritException && e.getCause() != null) {
			Throwable cause = e.getCause();
			if (cause instanceof Exception) {
				return toStatus(repository, qualifier, (Exception) cause);
			}
		} else if (e instanceof GerritException && e.getMessage() != null) {
			return createErrorStatus(repository,
					NLS.bind("{0}Gerrit connection issue: {1}", qualifier, e.getMessage())); //$NON-NLS-1$
		}
		String message = NLS.bind("{0}Unexpected error while connecting to Gerrit: {1}", qualifier, e.getMessage()); //$NON-NLS-1$
		if (repository != null) {
			return RepositoryStatus.createStatus(repository, IStatus.ERROR, GerritCorePlugin.PLUGIN_ID, message);
		} else {
			return createErrorStatus(repository, message);
		}
	}

	protected Status createErrorStatus(TaskRepository repository, String message) {
		if (repository != null) {
			return RepositoryStatus.createStatus(repository, IStatus.ERROR, GerritCorePlugin.PLUGIN_ID, message);
		} else {
			return new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID, message + " (Repository Unknown)"); //$NON-NLS-1$
		}
	}

	public static Status toStatus(TaskRepository repository, UnsupportedClassVersionError e) {
		String message = NLS.bind("The Gerrit Connector requires at Java 1.6 or higer (installed version: {0})", //$NON-NLS-1$
				System.getProperty("java.version")); //$NON-NLS-1$
		return new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID, message, e);
	}

	public static boolean isClosed(String status) {
		return EnumSet.of(ReviewStatus.MERGED, ReviewStatus.ABANDONED).contains(ReviewStatus.get(status));
	}

	public void setStarred(TaskRepository taskRepository, String taskID, boolean starred,
			IProgressMonitor progressMonitor) throws CoreException {
		GerritClient client = getClient(taskRepository);
		try {
			client.setStarred(taskID, starred, progressMonitor);
		} catch (GerritException e) {
			throw toCoreException(e);
		}
	}

	private CoreException toCoreException(GerritException e) {
		return new CoreException(new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID,
				"Unable to set the starred flag, the following Status is received", e)); //$NON-NLS-1$
	}
}
