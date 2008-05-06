/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.net.proxy.IProxyChangeEvent;
import org.eclipse.core.net.proxy.IProxyChangeListener;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.WebClientLog;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.context.core.ContextPreferenceContstants;
import org.eclipse.mylyn.internal.provisional.commons.ui.AbstractNotification;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonColors;
import org.eclipse.mylyn.internal.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTemplateManager;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskDataStorageManager;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask.PriorityLevel;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataStore;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractAttributeFactory;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractDuplicateDetector;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractLegacyRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractTaskDataHandler;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.core.deprecated.TaskComment;
import org.eclipse.mylyn.internal.tasks.core.externalization.ExternalizationManager;
import org.eclipse.mylyn.internal.tasks.core.externalization.TaskListExternalizer;
import org.eclipse.mylyn.internal.tasks.ui.notifications.TaskListNotification;
import org.eclipse.mylyn.internal.tasks.ui.notifications.TaskListNotificationQueryIncoming;
import org.eclipse.mylyn.internal.tasks.ui.notifications.TaskListNotificationReminder;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiExtensionReader;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoriesView;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.ITaskElement;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.core.TaskActivityAdapter;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.AbstractTaskRepositoryLinkProvider;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorFactory;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * Main entry point for the Tasks UI.
 * 
 * @author Mik Kersten
 * @since 3.0
 */
public class TasksUiPlugin extends AbstractUIPlugin {

	private static final int DELAY_QUERY_REFRESH_ON_STARTUP = 20 * 1000;

	private static final int MAX_CHANGED_ATTRIBUTES = 2;

	private static final int LINK_PROVIDER_TIMEOUT_SECONDS = 5;

	public static final String LABEL_VIEW_REPOSITORIES = "Task Repositories";

	public static final String ID_PLUGIN = "org.eclipse.mylyn.tasks.ui";

	private static final String FOLDER_OFFLINE = "offline";

	private static final String DIRECTORY_METADATA = ".metadata";

	private static final String NAME_DATA_DIR = ".mylyn";

	private static final char DEFAULT_PATH_SEPARATOR = '/';

	private static final int NOTIFICATION_DELAY = 5000;

	private static TasksUiPlugin INSTANCE;

	private static ExternalizationManager externalizationManager;

	private static TaskListManager taskListManager;

	private static TaskActivityManager taskActivityManager;

	private static TaskRepositoryManager repositoryManager;

	private static TaskListSynchronizationScheduler synchronizationScheduler;

	private static TaskDataManager taskDataManager;

	private static Map<String, AbstractRepositoryConnectorUi> repositoryConnectorUiMap = new HashMap<String, AbstractRepositoryConnectorUi>();

	//private TaskListSaveManager taskListSaveManager;

	private TaskListNotificationManager taskListNotificationManager;

	private TaskListBackupManager taskListBackupManager;

	private TaskDataStorageManager taskDataStorageManager;

	private RepositoryTemplateManager repositoryTemplateManager;

	private final Set<AbstractTaskEditorFactory> taskEditorFactories = new HashSet<AbstractTaskEditorFactory>();

	private final Set<AbstractTaskEditorPageFactory> taskEditorPageFactories = new HashSet<AbstractTaskEditorPageFactory>();

	private final Set<IHyperlinkDetector> hyperlinkDetectors = new HashSet<IHyperlinkDetector>();

	private final TreeSet<AbstractTaskRepositoryLinkProvider> repositoryLinkProviders = new TreeSet<AbstractTaskRepositoryLinkProvider>(
			new OrderComparator());

	private TaskListExternalizer taskListWriter;

	private ITaskHighlighter highlighter;

	private boolean initialized = false;

	private final Map<String, Image> brandingIcons = new HashMap<String, Image>();

	private final Map<String, ImageDescriptor> overlayIcons = new HashMap<String, ImageDescriptor>();

	private final Set<AbstractDuplicateDetector> duplicateDetectors = new HashSet<AbstractDuplicateDetector>();

	private ISaveParticipant saveParticipant;

	private TaskEditorBloatMonitor taskEditorBloatManager;

	private TaskJobFactory tasksJobFactory;

	private static final boolean DEBUG_HTTPCLIENT = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.mylyn.tasks.ui/debug/httpclient"));

	// XXX reconsider if this is necessary
	public static class TasksUiStartup implements IStartup {

		public void earlyStartup() {
			// ignore
		}
	}

	private static final class OrderComparator implements Comparator<AbstractTaskRepositoryLinkProvider> {
		public int compare(AbstractTaskRepositoryLinkProvider p1, AbstractTaskRepositoryLinkProvider p2) {
			return p1.getOrder() - p2.getOrder();
		}
	}

	public enum TaskListSaveMode {
		ONE_HOUR, THREE_HOURS, DAY;
		@Override
		public String toString() {
			switch (this) {
			case ONE_HOUR:
				return "1 hour";
			case THREE_HOURS:
				return "3 hours";
			case DAY:
				return "1 day";
			default:
				return "3 hours";
			}
		}

		public static TaskListSaveMode fromString(String string) {
			if (string == null) {
				return null;
			}
			if (string.equals("1 hour")) {
				return ONE_HOUR;
			}
			if (string.equals("3 hours")) {
				return THREE_HOURS;
			}
			if (string.equals("1 day")) {
				return DAY;
			}
			return null;
		}

		public static long fromStringToLong(String string) {
			long hour = 3600 * 1000;
			switch (fromString(string)) {
			case ONE_HOUR:
				return hour;
			case THREE_HOURS:
				return hour * 3;
			case DAY:
				return hour * 24;
			default:
				return hour * 3;
			}
		}
	}

	public enum ReportOpenMode {
		EDITOR, INTERNAL_BROWSER, EXTERNAL_BROWSER;
	}

	private static ITaskActivityListener CONTEXT_TASK_ACTIVITY_LISTENER = new TaskActivityAdapter() {

		@Override
		public void taskActivated(final ITask task) {
			ContextCore.getContextManager().activateContext(task.getHandleIdentifier());
		}

		@Override
		public void taskDeactivated(final ITask task) {
			ContextCore.getContextManager().deactivateContext(task.getHandleIdentifier());
		}

	};

	private static ITaskListNotificationProvider REMINDER_NOTIFICATION_PROVIDER = new ITaskListNotificationProvider() {

		public Set<AbstractNotification> getNotifications() {
			Collection<AbstractTask> allTasks = TasksUiPlugin.getTaskListManager().getTaskList().getAllTasks();
			Set<AbstractNotification> reminders = new HashSet<AbstractNotification>();
			for (AbstractTask task : allTasks) {
				if (task.isPastReminder() && !task.isReminded()) {
					reminders.add(new TaskListNotificationReminder(task));
					task.setReminded(true);
				}
			}
			return reminders;
		}
	};

	private static ITaskListNotificationProvider INCOMING_NOTIFICATION_PROVIDER = new ITaskListNotificationProvider() {

		public Set<AbstractNotification> getNotifications() {
			Set<AbstractNotification> notifications = new HashSet<AbstractNotification>();
			// Incoming Changes
			for (TaskRepository repository : getRepositoryManager().getAllRepositories()) {
				AbstractRepositoryConnector connector = getRepositoryManager().getRepositoryConnector(
						repository.getConnectorKind());
				AbstractRepositoryConnectorUi connectorUi = getConnectorUi(repository.getConnectorKind());
				if (connectorUi != null && !connectorUi.isCustomNotificationHandling()) {
					for (ITask repositoryTask : TasksUiPlugin.getTaskListManager().getTaskList().getTasks(
							repository.getRepositoryUrl())) {
						if ((repositoryTask.getLastReadTimeStamp() == null || repositoryTask.getSynchronizationState() == SynchronizationState.INCOMING)
								&& repositoryTask.isNotified() == false) {
							TaskListNotification notification = INSTANCE.getIncommingNotification(connector,
									repositoryTask);
							notifications.add(notification);
							repositoryTask.setNotified(true);
						}
					}
				}
			}
			// New query hits
			for (AbstractRepositoryQuery query : TasksUiPlugin.getTaskListManager().getTaskList().getQueries()) {
				AbstractRepositoryConnectorUi connectorUi = getConnectorUi(query.getConnectorKind());
				if (!connectorUi.isCustomNotificationHandling()) {
					for (ITask hit : query.getChildren()) {
						if (hit.isNotified() == false) {
							notifications.add(new TaskListNotificationQueryIncoming(hit));
							hit.setNotified(true);
						}
					}
				}
			}
			return notifications;
		}
	};

//	private final IPropertyChangeListener PREFERENCE_LISTENER = new IPropertyChangeListener() {
//
//		public void propertyChange(PropertyChangeEvent event) {
//			// TODO: do we ever get here?
////			if (event.getProperty().equals(ContextPreferenceContstants.PREF_DATA_DIR)) {
////				if (event.getOldValue() instanceof String) {
////					reloadDataDirectory();
////				}
////			}
//		}
//	};

	private final org.eclipse.jface.util.IPropertyChangeListener PROPERTY_LISTENER = new org.eclipse.jface.util.IPropertyChangeListener() {

		public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
//			if (event.getProperty().equals(ContextPreferenceContstants.PREF_DATA_DIR)) {
//				if (event.getOldValue() instanceof String) {
//					reloadDataDirectory();
//				}
//			}

			if (event.getProperty().equals(TasksUiPreferenceConstants.PLANNING_ENDHOUR)
					|| event.getProperty().equals(TasksUiPreferenceConstants.WEEK_START_DAY)) {
				updateTaskActivityManager();
			}

			if (event.getProperty().equals(TasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED)
					|| event.getProperty().equals(TasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS)) {
				updateSynchronizationScheduler(false);
			}
		}
	};

	private TaskActivityMonitor taskActivityMonitor;

	private ServiceReference proxyServiceReference;

	private IProxyChangeListener proxyChangeListener;

	private class TasksUiInitializationJob extends UIJob {

		public TasksUiInitializationJob() {
			super("Initializing Task List");
			setSystem(true);
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			// NOTE: failure in one part of the initialization should
			// not prevent others
			monitor.beginTask("Initializing Task List", 5);
			try {
				// Needs to run after workbench is loaded because it
				// relies on images.
				TasksUiExtensionReader.initWorkbenchUiExtensions();

				if (externalizationManager.getLoadStatus() != null) {
					// XXX: recovery from task list load failure  (Rendered in task list)
				}

				// Needs to happen asynchronously to avoid bug 159706
				for (AbstractTask task : taskListManager.getTaskList().getAllTasks()) {
					if (task.isActive()) {
						taskListManager.activateTask(task);
						break;
					}
				}
				taskActivityMonitor.reloadActivityTime();
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Could not initialize task activity", t));
			}
			monitor.worked(1);

			try {
				taskListNotificationManager = new TaskListNotificationManager();
				taskListNotificationManager.addNotificationProvider(REMINDER_NOTIFICATION_PROVIDER);
				taskListNotificationManager.addNotificationProvider(INCOMING_NOTIFICATION_PROVIDER);
				taskListNotificationManager.startNotification(NOTIFICATION_DELAY);
				getPreferenceStore().addPropertyChangeListener(taskListNotificationManager);
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Could not initialize notifications", t));
			}
			monitor.worked(1);

			try {
				taskListBackupManager = new TaskListBackupManager(getBackupFolderPath());
				getPreferenceStore().addPropertyChangeListener(taskListBackupManager);

				synchronizationScheduler = new TaskListSynchronizationScheduler(tasksJobFactory);
				updateSynchronizationScheduler(true);
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Could not initialize task list backup and synchronization", t));
			}
			monitor.worked(1);

			try {
				//taskListSaveManager = new TaskListSaveManager();
				//taskListManager.setTaskListSaveManager(taskListSaveManager);

				//ContextCorePlugin.getDefault().getPluginPreferences().addPropertyChangeListener(PREFERENCE_LISTENER);

				getPreferenceStore().addPropertyChangeListener(PROPERTY_LISTENER);

				// TODO: get rid of this, hack to make decorators show
				// up on startup
				TaskRepositoriesView repositoriesView = TaskRepositoriesView.getFromActivePerspective();
				if (repositoriesView != null) {
					repositoriesView.getViewer().refresh();
				}

				taskEditorBloatManager = new TaskEditorBloatMonitor();
				taskEditorBloatManager.install(PlatformUI.getWorkbench());
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Could not finish Tasks UI initialization", t));
			} finally {
				monitor.done();
			}
			return new Status(IStatus.OK, TasksUiPlugin.ID_PLUGIN, IStatus.OK, "", null);
		}
	}

	public TasksUiPlugin() {
		super();
		INSTANCE = this;
	}

	private void updateSynchronizationScheduler(boolean initial) {
		boolean enabled = TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(
				TasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED);
		if (enabled) {
			long interval = TasksUiPlugin.getDefault().getPreferenceStore().getLong(
					TasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS);
			if (initial) {
				synchronizationScheduler.setInterval(DELAY_QUERY_REFRESH_ON_STARTUP, interval);
			} else {
				synchronizationScheduler.setInterval(interval);
			}
		} else {
			synchronizationScheduler.setInterval(0);
		}
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		// NOTE: startup order is very sensitive
		try {
			StatusHandler.setDefaultStatusHandler(new RepositoryAwareStatusHandler());
			WebUtil.init();
			WebClientLog.setLoggingEnabled(DEBUG_HTTPCLIENT);
			initializeDefaultPreferences(getPreferenceStore());
			taskListWriter = new TaskListExternalizer();

			externalizationManager = new ExternalizationManager(getDataDirectory());

			File dataDir = new File(getDataDirectory());
			dataDir.mkdirs();
			String path = getDataDirectory() + File.separator + ITasksCoreConstants.DEFAULT_TASK_LIST_FILE;
			File taskListFile = new File(path);
			taskListManager = new TaskListManager(taskListWriter, taskListFile);
			repositoryManager = new TaskRepositoryManager(taskListManager.getTaskList());
			taskActivityManager = new TaskActivityManager(repositoryManager, taskListManager.getTaskList());
			updateTaskActivityManager();

			proxyServiceReference = context.getServiceReference(IProxyService.class.getName());
			if (proxyServiceReference != null) {
				IProxyService proxyService = (IProxyService) context.getService(proxyServiceReference);
				if (proxyService != null) {
					proxyChangeListener = new IProxyChangeListener() {
						public void proxyInfoChanged(IProxyChangeEvent event) {
							List<TaskRepository> repos = repositoryManager.getAllRepositories();
							for (TaskRepository repo : repos) {
								if (repo.isDefaultProxyEnabled()) {
									repositoryManager.notifyRepositorySettingsChanged(repo);
								}
							}
						}
					};
					proxyService.addProxyChangeListener(proxyChangeListener);
				}
			}

			repositoryTemplateManager = new RepositoryTemplateManager();

			// NOTE: initializing extensions in start(..) has caused race
			// conditions previously
			TasksUiExtensionReader.initStartupExtensions(taskListWriter);

			repositoryManager.readRepositories(getRepositoriesFilePath());

			// instantiates taskDataManager
			File root = new File(this.getDataDirectory() + '/' + FOLDER_OFFLINE);
			OfflineFileStorage storage = new OfflineFileStorage(root);
			OfflineCachingStorage cachedStorage = new OfflineCachingStorage(storage);
			taskDataStorageManager = new TaskDataStorageManager(repositoryManager, cachedStorage);
			taskDataStorageManager.start();

			TaskDataStore taskDataStore = new TaskDataStore(repositoryManager);

			taskDataManager = new TaskDataManager(taskDataStorageManager, taskDataStore, repositoryManager,
					taskListManager.getTaskList());
			taskDataManager.setDataPath(getDataDirectory());

			for (AbstractRepositoryConnector connector : repositoryManager.getRepositoryConnectors()) {
				connector.init(taskDataManager);
			}

			loadTemplateRepositories();

			tasksJobFactory = new TaskJobFactory(taskListManager.getTaskList(), taskDataManager, repositoryManager);

			// NOTE: task list must be read before Task List view can be initialized
			taskActivityManager.addActivityListener(CONTEXT_TASK_ACTIVITY_LISTENER);

			// readExistingOrCreateNewList() must be called after repositories have been read in
			taskListManager.readExistingOrCreateNewList();

			initialized = true;

			taskActivityMonitor = new TaskActivityMonitor(taskActivityManager, ContextCore.getContextManager());
			taskActivityMonitor.start();

			saveParticipant = new ISaveParticipant() {

				public void doneSaving(ISaveContext context) {
				}

				public void prepareToSave(ISaveContext context) throws CoreException {
				}

				public void rollback(ISaveContext context) {
				}

				public void saving(ISaveContext context) throws CoreException {
					if (context.getKind() == ISaveContext.FULL_SAVE) {
						//externalizationManager.requestSave();
						taskDataStorageManager.stop();
					}
				}
			};
			ResourcesPlugin.getWorkspace().addSaveParticipant(this, saveParticipant);

			ActivityExternalizationParticipant ACTIVITY_EXTERNALIZTAION_PARTICIPANT = new ActivityExternalizationParticipant();
			externalizationManager.addParticipant(ACTIVITY_EXTERNALIZTAION_PARTICIPANT);
			taskActivityManager.addActivityListener(ACTIVITY_EXTERNALIZTAION_PARTICIPANT);

			new TasksUiInitializationJob().schedule();
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Task list initialization failed", e));
		}
	}

	private void updateTaskActivityManager() {
		int endHour = getPreferenceStore().getInt(TasksUiPreferenceConstants.PLANNING_ENDHOUR);
		if (taskActivityManager.getEndHour() != endHour) {
			taskActivityManager.setEndHour(endHour);
			TaskActivityUtil.setEndHour(endHour);
		}

		int newWeekStartDay = getPreferenceStore().getInt(TasksUiPreferenceConstants.WEEK_START_DAY);
		int oldWeekStartDay = taskActivityManager.getWeekStartDay();
		if (oldWeekStartDay != newWeekStartDay) {
			taskActivityManager.setWeekStartDay(newWeekStartDay);
			TaskActivityUtil.setStartDay(newWeekStartDay);
			taskActivityManager.setStartTime(new Date());
		}

		// event.getProperty().equals(TaskListPreferenceConstants.PLANNING_STARTDAY)
		// scheduledStartHour =
		// TasksUiPlugin.getDefault().getPreferenceStore().getInt(
		// TaskListPreferenceConstants.PLANNING_STARTHOUR);
	}

	private void loadTemplateRepositories() {
		// Add standard local task repository
		getLocalTaskRepository();

		// Add the automatically created templates
		for (AbstractRepositoryConnector connector : repositoryManager.getRepositoryConnectors()) {
			for (RepositoryTemplate template : repositoryTemplateManager.getTemplates(connector.getConnectorKind())) {

				if (template.addAutomatically && !TaskRepositoryUtil.isAddAutomaticallyDisabled(template.repositoryUrl)) {
					try {
						TaskRepository taskRepository = repositoryManager.getRepository(connector.getConnectorKind(),
								template.repositoryUrl);
						if (taskRepository == null) {
							taskRepository = new TaskRepository(connector.getConnectorKind(), template.repositoryUrl);
							taskRepository.setVersion(template.version);
							taskRepository.setRepositoryLabel(template.label);
							taskRepository.setCharacterEncoding(template.characterEncoding);
							taskRepository.setAnonymous(template.anonymous);
							repositoryManager.addRepository(taskRepository, getRepositoriesFilePath());
						}
					} catch (Throwable t) {
						StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
								"Could not load repository template", t));
					}
				}
			}
		}
	}

	/**
	 * Returns the local task repository. If the repository does not exist it is created and added to the task
	 * repository manager.
	 * 
	 * @return the local task repository; never <code>null</code>
	 * @since 3.0
	 */
	public TaskRepository getLocalTaskRepository() {
		TaskRepository localRepository = repositoryManager.getRepository(LocalRepositoryConnector.CONNECTOR_KIND,
				LocalRepositoryConnector.REPOSITORY_URL);
		if (localRepository == null) {
			localRepository = new TaskRepository(LocalRepositoryConnector.CONNECTOR_KIND,
					LocalRepositoryConnector.REPOSITORY_URL);
			localRepository.setVersion(LocalRepositoryConnector.REPOSITORY_VERSION);
			localRepository.setRepositoryLabel(LocalRepositoryConnector.REPOSITORY_LABEL);
			localRepository.setAnonymous(true);
			repositoryManager.addRepository(localRepository, getRepositoriesFilePath());
		}
		return localRepository;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			if (taskActivityMonitor != null) {
				taskActivityMonitor.stop();
			}

			if (ResourcesPlugin.getWorkspace() != null) {
				ResourcesPlugin.getWorkspace().removeSaveParticipant(this);
			}

			if (proxyServiceReference != null) {
				IProxyService proxyService = (IProxyService) context.getService(proxyServiceReference);
				if (proxyService != null) {
					proxyService.removeProxyChangeListener(proxyChangeListener);
				}
				context.ungetService(proxyServiceReference);
			}

			if (PlatformUI.isWorkbenchRunning()) {
				getPreferenceStore().removePropertyChangeListener(taskListNotificationManager);
				getPreferenceStore().removePropertyChangeListener(taskListBackupManager);
				getPreferenceStore().removePropertyChangeListener(PROPERTY_LISTENER);
				//taskListManager.getTaskList().removeChangeListener(taskListSaveManager);
				CommonColors.dispose();
//				if (ContextCorePlugin.getDefault() != null) {
//					ContextCorePlugin.getDefault().getPluginPreferences().removePropertyChangeListener(
//							PREFERENCE_LISTENER);
//				}
				taskEditorBloatManager.dispose(PlatformUI.getWorkbench());
				INSTANCE = null;
			}
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Task list stop terminated abnormally", e));
		} finally {
			super.stop(context);
		}
	}

	public String getDefaultDataDirectory() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + '/' + DIRECTORY_METADATA + '/'
				+ NAME_DATA_DIR;
	}

	public String getDataDirectory() {
		return getPreferenceStore().getString(ContextPreferenceContstants.PREF_DATA_DIR);
	}

	/**
	 * Save first, then load from <code>newPath</code>. Sets the new data directory, which upon setting results in
	 * reload of task list information from the <code>newPath</code> supplied.
	 * 
	 * @throws CoreException
	 */
	public void setDataDirectory(final String newPath, IProgressMonitor monitor) throws CoreException {

		externalizationManager.saveNow(monitor);
		//getBackupManager().backupNow(true);
		// TODO: save repositories, activity
		//TasksUiPlugin.getBackupManager().backupNow(true);
		loadDataDirectory(newPath);
		getPreferenceStore().setValue(ContextPreferenceContstants.PREF_DATA_DIR, newPath);
		ContextCore.getContextStore().contextStoreMoved();

	}

	public void reloadDataDirectory() throws CoreException {
		// no save just load what is there
		loadDataDirectory(getDataDirectory());
	}

	/**
	 * Only support task data versions post 0.7
	 * 
	 * @throws CoreException
	 * @throws InterruptedException
	 */
	private synchronized void loadDataDirectory(final String newPath) throws CoreException {

		IRunnableWithProgress setDirectoryRunnable = new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

				try {
					monitor.beginTask("Set Data Directory", IProgressMonitor.UNKNOWN);
					Job.getJobManager().beginRule(ITasksCoreConstants.ROOT_SCHEDULING_RULE,
							new SubProgressMonitor(monitor, 1));
					if (monitor.isCanceled()) {
						throw new InterruptedException();
					}
					externalizationManager.reset();
					externalizationManager.setRootFolderPath(newPath);
					taskActivityManager.clear();

					getRepositoryManager().readRepositories(
							newPath + File.separator + TaskRepositoryManager.DEFAULT_REPOSITORIES_FILE);
					loadTemplateRepositories();

					getTaskListManager().resetTaskList();
//					getTaskListManager().setTaskListFile(
//							new File(newPath + File.separator + ITasksCoreConstants.DEFAULT_TASK_LIST_FILE));

					ContextCore.getContextManager().loadActivityMetaContext();

					getTaskListManager().readExistingOrCreateNewList();
					taskActivityMonitor.reloadActivityTime();
				} finally {
					Job.getJobManager().endRule(ITasksCoreConstants.ROOT_SCHEDULING_RULE);
					monitor.done();
				}
			}

		};

		IProgressService service = PlatformUI.getWorkbench().getProgressService();
		try {
			if (!CoreUtil.TEST_MODE) {
				service.run(false, false, setDirectoryRunnable);
			} else {
				setDirectoryRunnable.run(new NullProgressMonitor());
			}
		} catch (InvocationTargetException e) {
			throw new CoreException(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Failed to set data directory",
					e.getCause()));
		} catch (InterruptedException e) {
			throw new OperationCanceledException();
		}

	}

	@Override
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		store.setDefault(ContextPreferenceContstants.PREF_DATA_DIR, getDefaultDataDirectory());
		store.setDefault(TasksUiPreferenceConstants.GROUP_SUBTASKS, true);
		store.setDefault(TasksUiPreferenceConstants.NOTIFICATIONS_ENABLED, true);
		store.setDefault(TasksUiPreferenceConstants.FILTER_PRIORITY, PriorityLevel.P5.toString());
		store.setDefault(TasksUiPreferenceConstants.EDITOR_TASKS_RICH, true);
		store.setDefault(TasksUiPreferenceConstants.ACTIVATE_WHEN_OPENED, false);
		store.setDefault(TasksUiPreferenceConstants.SHOW_TRIM, false);
		store.setDefault(TasksUiPreferenceConstants.LOCAL_SUB_TASKS_ENABLED, false);

		store.setDefault(TasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED, true);
		store.setDefault(TasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS, "" + (20 * 60 * 1000));

		//store.setDefault(TasksUiPreferenceConstants.BACKUP_SCHEDULE, 1);
		store.setDefault(TasksUiPreferenceConstants.BACKUP_MAXFILES, 20);
		store.setDefault(TasksUiPreferenceConstants.BACKUP_LAST, 0f);

		store.setDefault(TasksUiPreferenceConstants.FILTER_ARCHIVE_MODE, true);
		store.setDefault(TasksUiPreferenceConstants.ACTIVATE_MULTIPLE, false);
		store.setValue(TasksUiPreferenceConstants.ACTIVATE_MULTIPLE, false);

		store.setDefault(TasksUiPreferenceConstants.WEEK_START_DAY, Calendar.getInstance().getFirstDayOfWeek());
		//store.setDefault(TasksUiPreferenceConstants.PLANNING_STARTHOUR, 9);
		store.setDefault(TasksUiPreferenceConstants.PLANNING_ENDHOUR, 18);
	}

	public static TaskListManager getTaskListManager() {
		return taskListManager;
	}

	public static TaskActivityManager getTaskActivityManager() {
		return taskActivityManager;
	}

	public static TaskListNotificationManager getTaskListNotificationManager() {
		return INSTANCE.taskListNotificationManager;
	}

	/**
	 * Returns the shared instance.
	 */
	public static TasksUiPlugin getDefault() {
		return INSTANCE;
	}

	public boolean groupSubtasks(ITaskElement container) {
		boolean groupSubtasks = TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(
				TasksUiPreferenceConstants.GROUP_SUBTASKS);

		if (container instanceof ITask) {
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(((ITask) container).getConnectorKind());
			if (connectorUi != null) {
				if (connectorUi.forceSubtaskHierarchy()) {
					groupSubtasks = true;
				}
			}
		}

		if (container instanceof AbstractRepositoryQuery) {
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(((AbstractRepositoryQuery) container).getConnectorKind());
			if (connectorUi != null) {
				if (connectorUi.forceSubtaskHierarchy()) {
					groupSubtasks = true;
				}
			}
		}

		return groupSubtasks;
	}

	private final Map<String, List<IDynamicSubMenuContributor>> menuContributors = new HashMap<String, List<IDynamicSubMenuContributor>>();

	public Map<String, List<IDynamicSubMenuContributor>> getDynamicMenuMap() {
		return menuContributors;
	}

	// API-3.0: move to a standard dynamic menu mechanism?
	public void addDynamicPopupContributor(String menuPath, IDynamicSubMenuContributor contributor) {
		List<IDynamicSubMenuContributor> contributors = menuContributors.get(menuPath);
		if (contributors == null) {
			contributors = new ArrayList<IDynamicSubMenuContributor>();
			menuContributors.put(menuPath, contributors);
		}
		contributors.add(contributor);
	}

	public String[] getSaveOptions() {
		String[] options = { TaskListSaveMode.ONE_HOUR.toString(), TaskListSaveMode.THREE_HOURS.toString(),
				TaskListSaveMode.DAY.toString() };
		return options;
	}

	public String getBackupFolderPath() {
		return getDataDirectory() + DEFAULT_PATH_SEPARATOR + ITasksCoreConstants.DEFAULT_BACKUP_FOLDER_NAME;
	}

	public ITaskHighlighter getHighlighter() {
		return highlighter;
	}

	public void setHighlighter(ITaskHighlighter highlighter) {
		this.highlighter = highlighter;
	}

	/**
	 * @since 3.0
	 */
	public AbstractTaskEditorPageFactory[] getTaskEditorPageFactories() {
		return taskEditorPageFactories.toArray(new AbstractTaskEditorPageFactory[0]);
	}

	public Set<AbstractTaskEditorFactory> getTaskEditorFactories() {
		return taskEditorFactories;
	}

	public void addContextEditor(AbstractTaskEditorFactory contextEditor) {
		if (contextEditor != null) {
			this.taskEditorFactories.add(contextEditor);
		}
	}

	/**
	 * @since 3.0
	 */
	public void addTaskEditorPageFactory(AbstractTaskEditorPageFactory factory) {
		Assert.isNotNull(factory);
		taskEditorPageFactories.add(factory);
	}

	/**
	 * @since 3.0
	 */
	public void removeTaskEditorPageFactory(AbstractTaskEditorPageFactory factory) {
		Assert.isNotNull(factory);
		taskEditorPageFactories.remove(factory);
	}

	public static TaskRepositoryManager getRepositoryManager() {
		return repositoryManager;
	}

	/**
	 * @since 3.0
	 */
	public static RepositoryTemplateManager getRepositoryTemplateManager() {
		return INSTANCE.repositoryTemplateManager;
	}

	public void addBrandingIcon(String repositoryType, Image icon) {
		brandingIcons.put(repositoryType, icon);
	}

	public Image getBrandingIcon(String repositoryType) {
		return brandingIcons.get(repositoryType);
	}

	public void addOverlayIcon(String repositoryType, ImageDescriptor icon) {
		overlayIcons.put(repositoryType, icon);
	}

	public ImageDescriptor getOverlayIcon(String repositoryType) {
		return overlayIcons.get(repositoryType);
	}

	public boolean isInitialized() {
		return initialized;
	}

	public IHyperlinkDetector[] getTaskHyperlinkDetectors() {
		return hyperlinkDetectors.toArray(new IHyperlinkDetector[1]);
	}

	public void addTaskHyperlinkDetector(IHyperlinkDetector listener) {
		if (listener != null) {
			this.hyperlinkDetectors.add(listener);
		}
	}

	public void addRepositoryLinkProvider(AbstractTaskRepositoryLinkProvider repositoryLinkProvider) {
		if (repositoryLinkProvider != null) {
			this.repositoryLinkProviders.add(repositoryLinkProvider);
		}
	}

	public static TaskListBackupManager getBackupManager() {
		return INSTANCE.taskListBackupManager;
	}

	public static TaskDataStorageManager getTaskDataStorageManager() {
		return INSTANCE.taskDataStorageManager;
	}

	public void addRepositoryConnectorUi(AbstractRepositoryConnectorUi repositoryConnectorUi) {
		if (!repositoryConnectorUiMap.values().contains(repositoryConnectorUi)) {
			repositoryConnectorUiMap.put(repositoryConnectorUi.getConnectorKind(), repositoryConnectorUi);
		}
	}

	/**
	 * @since 3.0
	 */
	public static AbstractRepositoryConnector getConnector(String kind) {
		return getRepositoryManager().getRepositoryConnector(kind);
	}

	public static AbstractRepositoryConnectorUi getConnectorUi(String kind) {
		return repositoryConnectorUiMap.get(kind);
	}

	public static TaskListSynchronizationScheduler getSynchronizationScheduler() {
		return synchronizationScheduler;
	}

	/**
	 * @since 3.0
	 */
	public static TaskDataManager getTaskDataManager() {
		return taskDataManager;
	}

	/**
	 * @since 3.0
	 */
	public static TaskJobFactory getTasksJobFactory() {
		return INSTANCE.tasksJobFactory;
	}

	public void addDuplicateDetector(AbstractDuplicateDetector duplicateDetector) {
		if (duplicateDetector != null) {
			duplicateDetectors.add(duplicateDetector);
		}
	}

	public Set<AbstractDuplicateDetector> getDuplicateSearchCollectorsList() {
		return duplicateDetectors;
	}

	public String getRepositoriesFilePath() {
		return getDataDirectory() + File.separator + TaskRepositoryManager.DEFAULT_REPOSITORIES_FILE;
	}

	public boolean canSetRepositoryForResource(IResource resource) {
		if (resource == null) {
			return false;
		}

		// find first provider that can link repository
		for (AbstractTaskRepositoryLinkProvider linkProvider : repositoryLinkProviders) {
			TaskRepository repository = linkProvider.getTaskRepository(resource, getRepositoryManager());
			if (repository != null) {
				return linkProvider.canSetTaskRepository(resource);
			}
		}
		// find first provider that can set new repository
		for (AbstractTaskRepositoryLinkProvider linkProvider : repositoryLinkProviders) {
			if (linkProvider.canSetTaskRepository(resource)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Associate a Task Repository with a workbench project
	 * 
	 * @param resource
	 * 		project or resource belonging to a project
	 * @param repository
	 * 		task repository to associate with given project
	 * @throws CoreException
	 */
	public void setRepositoryForResource(IResource resource, TaskRepository repository) throws CoreException {
		if (resource == null || repository == null) {
			return;
		}

		for (AbstractTaskRepositoryLinkProvider linkProvider : repositoryLinkProviders) {
			TaskRepository r = linkProvider.getTaskRepository(resource, getRepositoryManager());
			boolean canSetRepository = linkProvider.canSetTaskRepository(resource);
			if (r != null && !canSetRepository) {
				return;
			}
			if (canSetRepository) {
				linkProvider.setTaskRepository(resource, repository);
				return;
			}
		}
	}

	/**
	 * Retrieve the task repository that has been associated with the given project (or resource belonging to a project)
	 * 
	 * NOTE: if call does not return in LINK_PROVIDER_TIMEOUT_SECONDS, the provide will be disabled until the next time
	 * that the Workbench starts.
	 */
	public TaskRepository getRepositoryForResource(final IResource resource) {
		Assert.isNotNull(resource);
		Set<AbstractTaskRepositoryLinkProvider> defectiveLinkProviders = new HashSet<AbstractTaskRepositoryLinkProvider>();
		for (final AbstractTaskRepositoryLinkProvider linkProvider : repositoryLinkProviders) {
			long startTime = System.nanoTime();
			final TaskRepository[] repository = new TaskRepository[1];
			SafeRunnable.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.ERROR, ID_PLUGIN, "Repository link provider failed: \""
							+ linkProvider.getId() + "\"", e));
				}

				public void run() throws Exception {
					repository[0] = linkProvider.getTaskRepository(resource, getRepositoryManager());
				}
			});
			long elapsed = System.nanoTime() - startTime;
			if (elapsed > LINK_PROVIDER_TIMEOUT_SECONDS * 1000 * 1000 * 1000) {
				defectiveLinkProviders.add(linkProvider);
			}
			if (repository[0] != null) {
				return repository[0];
			}
		}
		if (!defectiveLinkProviders.isEmpty()) {
			repositoryLinkProviders.removeAll(defectiveLinkProviders);
			StatusHandler.log(new Status(IStatus.WARNING, ID_PLUGIN,
					"Repository link provider took over 5s to execute and was timed out: \"" + defectiveLinkProviders
							+ "\""));
		}
		return null;
	}

	@Deprecated
	public TaskRepository getRepositoryForResource(final IResource resource, boolean silent) {
		TaskRepository repository = getRepositoryForResource(resource);
		if (repository == null && !silent) {
			MessageDialog.openInformation(null, "No Repository Found",
					"No repository was found. Associate a Task Repository with this project via the project's property page.");
		}
		return repository;
	}

//	/**
//	 * Public for testing.
//	 */
//	public static TaskListSaveManager getTaskListSaveManager() {
//		return INSTANCE.taskListSaveManager;
//	}

	public String getNextNewRepositoryTaskId() {
		return getTaskDataStorageManager().getNewRepositoryTaskId();
	}

	/**
	 * TODO 3.0: move, uses and exposes internal class.
	 */
	@SuppressWarnings("restriction")
	@Deprecated
	public TaskListNotification getIncommingNotification(AbstractRepositoryConnector connector, ITask task) {

		TaskListNotification notification = new TaskListNotification(task);
		RepositoryTaskData newTaskData = getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(),
				task.getTaskId());
		RepositoryTaskData oldTaskData = getTaskDataStorageManager().getOldTaskData(task.getRepositoryUrl(),
				task.getTaskId());

		try {
			if (task.getSynchronizationState().equals(SynchronizationState.INCOMING)
					&& task.getLastReadTimeStamp() == null) {
				notification.setDescription("New unread task ");
			} else if (newTaskData != null && oldTaskData != null) {
				StringBuilder description = new StringBuilder();
				String changedDescription = getChangedDescription(newTaskData, oldTaskData);
				String changedAttributes = getChangedAttributes(newTaskData, oldTaskData);
				if (!"".equals(changedDescription.trim())) {
					description.append(changedDescription);
					if (!"".equals(changedAttributes)) {
						description.append('\n');
					}
				}
				if (!"".equals(changedAttributes)) {
					description.append(changedAttributes);
				}

				notification.setDescription(description.toString());

				if (connector instanceof AbstractLegacyRepositoryConnector) {
					AbstractTaskDataHandler offlineHandler = ((LocalRepositoryConnector) connector).getLegacyTaskDataHandler();
					if (offlineHandler != null && newTaskData.getLastModified() != null) {
						Date modified = newTaskData.getAttributeFactory().getDateForAttributeType(
								RepositoryTaskAttribute.DATE_MODIFIED, newTaskData.getLastModified());
						notification.setDate(modified);
					}
				}
			} else {
				notification.setDescription("Unread task");
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not format notification for: "
					+ task, t));
		}
		return notification;
	}

	public static boolean isAnimationsEnabled() {
		IPreferenceStore store = PlatformUI.getPreferenceStore();
		return store.getBoolean(IWorkbenchPreferenceConstants.ENABLE_ANIMATIONS);
	}

	private static String getChangedDescription(RepositoryTaskData newTaskData, RepositoryTaskData oldTaskData) {
		String descriptionText = "";

		if (newTaskData.getComments().size() > oldTaskData.getComments().size()) {
			List<TaskComment> taskComments = newTaskData.getComments();
			if (taskComments != null && taskComments.size() > 0) {
				TaskComment lastComment = taskComments.get(taskComments.size() - 1);
				if (lastComment != null) {
//					descriptionText += "Comment by " + lastComment.getAuthor() + ":\n  ";
					descriptionText += lastComment.getAuthor() + ":  ";
					descriptionText += cleanValue(lastComment.getText());
				}
			}
		}

		return descriptionText;
	}

	private static String getChangedAttributes(RepositoryTaskData newTaskData, RepositoryTaskData oldTaskData) {
		List<Change> changes = new ArrayList<Change>();
		for (RepositoryTaskAttribute newAttribute : newTaskData.getAttributes()) {
			if (ignoreAttribute(newTaskData, newAttribute)) {
				continue;
			}

			List<String> newValues = newAttribute.getValues();
			if (newValues != null) {
				RepositoryTaskAttribute oldAttribute = oldTaskData.getAttribute(newAttribute.getId());
				if (oldAttribute == null) {
					changes.add(getDiff(newTaskData, newAttribute, null, newValues));
				}
				if (oldAttribute != null) {
					List<String> oldValues = oldAttribute.getValues();
					if (!oldValues.equals(newValues)) {
						changes.add(getDiff(newTaskData, newAttribute, oldValues, newValues));
					}
				}
			}
		}

		for (RepositoryTaskAttribute oldAttribute : oldTaskData.getAttributes()) {
			if (ignoreAttribute(oldTaskData, oldAttribute)) {
				continue;
			}

			RepositoryTaskAttribute attribute = newTaskData.getAttribute(oldAttribute.getId());
			List<String> values = oldAttribute.getValues();
			if (attribute == null && values != null && !values.isEmpty()) {
				changes.add(getDiff(oldTaskData, oldAttribute, values, null));
			}
		}

		if (changes.isEmpty()) {
			return "";
		}

		String details = "";
		String sep = "";
		int n = 0;
		for (Change change : changes) {
			String removed = cleanValues(change.removed);
			String added = cleanValues(change.added);
			details += sep + "  " + change.field + " " + removed;
			if (removed.length() > 30) {
//				details += "\n  ";
				details += "\n  ";
			}
			details += " -> " + added;
			sep = "\n";

			if (++n == MAX_CHANGED_ATTRIBUTES) {
				break;
			}
		}
//		if (!details.equals("")) {
//			return details;
//			return "Attributes Changed:\n" + details;
//		}
		return details;
	}

	private static String cleanValues(List<String> values) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String value : values) {
			if (!first) {
				sb.append(", ");
			}
			sb.append(cleanValue(value));
			first = false;
		}
		return sb.toString();
	}

	private static String cleanValue(String value) {
		if (value == null) {
			return "";
		}
		String commentText = value.replaceAll("\\s", " ").trim();
		if (commentText.length() > 60) {
			commentText = commentText.substring(0, 55) + "...";
		}
		return commentText;
	}

	private static boolean ignoreAttribute(RepositoryTaskData taskData, RepositoryTaskAttribute attribute) {
		AbstractAttributeFactory factory = taskData.getAttributeFactory();
		return (attribute.getId().equals(factory.mapCommonAttributeKey(RepositoryTaskAttribute.DATE_MODIFIED))
				|| attribute.getId().equals(factory.mapCommonAttributeKey(RepositoryTaskAttribute.DATE_CREATION))
				|| "delta_ts".equals(attribute.getId()) || "longdesclength".equals(attribute.getId()));
	}

	private static Change getDiff(RepositoryTaskData taskData, RepositoryTaskAttribute attribute,
			List<String> oldValues, List<String> newValues) {
//		AbstractAttributeFactory factory = taskData.getAttributeFactory();
//		if (attribute.getId().equals(factory.mapCommonAttributeKey(RepositoryTaskAttribute.DATE_MODIFIED)) 
//			|| attribute.getId().equals(factory.mapCommonAttributeKey(RepositoryTaskAttribute.DATE_CREATION))) {
//			if (newValues != null && newValues.size() > 0) {
//				for (int i = 0; i < newValues.size(); i++) {
//					newValues.set(i, factory.getDateForAttributeType(attribute.getId(), newValues.get(i)).toString());
//				}
//			}
//			
//			Change change = new Change(attribute.getName(), newValues);
//			if (oldValues != null) {
//				for (String value : oldValues) {
//					value = factory.getDateForAttributeType(attribute.getId(), value).toString();
//					if (change.added.contains(value)) {
//						change.added.remove(value);
//					} else {
//						change.removed.add(value);
//					}
//				}
//			}
//			return change;		
//		}

		Change change = new Change(attribute.getName(), newValues);
		if (oldValues != null) {
			for (String value : oldValues) {
				if (change.added.contains(value)) {
					change.added.remove(value);
				} else {
					change.removed.add(value);
				}
			}
		}
		return change;
	}

	private static class Change {

		final String field;

		final List<String> added;

		final List<String> removed = new ArrayList<String>();

		public Change(String field, List<String> newValues) {
			this.field = field;
			if (newValues != null) {
				this.added = new ArrayList<String>(newValues);
			} else {
				this.added = new ArrayList<String>();
			}
		}
	}

	public static ExternalizationManager getExternalizationManager() {
		return externalizationManager;
	}

	public static TaskActivityMonitor getTaskActivityMonitor() {
		return INSTANCE.taskActivityMonitor;
	}
}
