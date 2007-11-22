/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.tasks.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.internal.net.ProxyManager;
import org.eclipse.core.net.proxy.IProxyChangeListener;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.ContextPreferenceContstants;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.core.TaskDataManager;
import org.eclipse.mylyn.internal.tasks.ui.IDynamicSubMenuContributor;
import org.eclipse.mylyn.internal.tasks.ui.ITaskHighlighter;
import org.eclipse.mylyn.internal.tasks.ui.ITaskListNotification;
import org.eclipse.mylyn.internal.tasks.ui.ITaskListNotificationProvider;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.internal.tasks.ui.OfflineCachingStorage;
import org.eclipse.mylyn.internal.tasks.ui.OfflineFileStorage;
import org.eclipse.mylyn.internal.tasks.ui.RepositoryAwareStatusHandler;
import org.eclipse.mylyn.internal.tasks.ui.TaskListBackupManager;
import org.eclipse.mylyn.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylyn.internal.tasks.ui.TaskListNotificationIncoming;
import org.eclipse.mylyn.internal.tasks.ui.TaskListNotificationManager;
import org.eclipse.mylyn.internal.tasks.ui.TaskListNotificationQueryIncoming;
import org.eclipse.mylyn.internal.tasks.ui.TaskListNotificationReminder;
import org.eclipse.mylyn.internal.tasks.ui.TaskListSynchronizationScheduler;
import org.eclipse.mylyn.internal.tasks.ui.TaskRepositoryUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskListSaveManager;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskListWriter;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiExtensionReader;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoriesView;
import org.eclipse.mylyn.internal.tasks.ui.wizards.EditRepositoryWizard;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.core.TaskComment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.AbstractTask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorFactory;
import org.eclipse.mylyn.web.core.WebClientUtil;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;

/**
 * Main entry point for the Tasks UI.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public class TasksUiPlugin extends AbstractUIPlugin implements IStartup {

	private static final int LINK_PROVIDER_TIMEOUT_SECONDS = 5;

	public static final String LABEL_VIEW_REPOSITORIES = "Task Repositories";

	public static final String ID_PLUGIN = "org.eclipse.mylyn.tasks.ui";

	private static final String FOLDER_OFFLINE = "offline";

	private static final String DIRECTORY_METADATA = ".metadata";

	private static final String NAME_DATA_DIR = ".mylyn";

	private static final char DEFAULT_PATH_SEPARATOR = '/';

	private static final int NOTIFICATION_DELAY = 5000;

	private static TasksUiPlugin INSTANCE;

	private static TaskListManager taskListManager;

	private static TaskActivityManager taskActivityManager;

	private static TaskRepositoryManager taskRepositoryManager;

	private static TaskListSynchronizationScheduler synchronizationScheduler;

	private static RepositorySynchronizationManager synchronizationManager;

	private static Map<String, AbstractRepositoryConnectorUi> repositoryConnectorUiMap = new HashMap<String, AbstractRepositoryConnectorUi>();

	private TaskListSaveManager taskListSaveManager;

	private TaskListNotificationManager taskListNotificationManager;

	private TaskListBackupManager taskListBackupManager;

	private TaskDataManager taskDataManager;

	private Set<AbstractTaskEditorFactory> taskEditorFactories = new HashSet<AbstractTaskEditorFactory>();

	private Set<IHyperlinkDetector> hyperlinkDetectors = new HashSet<IHyperlinkDetector>();

	private TreeSet<AbstractTaskRepositoryLinkProvider> repositoryLinkProviders = new TreeSet<AbstractTaskRepositoryLinkProvider>(
			new OrderComparator());

	private TaskListWriter taskListWriter;

	private ITaskHighlighter highlighter;

	private boolean initialized = false;

	private Map<String, Image> brandingIcons = new HashMap<String, Image>();

	private Map<String, ImageDescriptor> overlayIcons = new HashMap<String, ImageDescriptor>();

	private Set<AbstractDuplicateDetector> duplicateDetectors = new HashSet<AbstractDuplicateDetector>();

	private ISaveParticipant saveParticipant;

	private static final boolean DEBUG_HTTPCLIENT = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.mylyn.tasks.ui/debug/httpclient"));

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
			if (string == null)
				return null;
			if (string.equals("1 hour"))
				return ONE_HOUR;
			if (string.equals("3 hours"))
				return THREE_HOURS;
			if (string.equals("1 day"))
				return DAY;
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

	private static ITaskActivityListener CONTEXT_TASK_ACTIVITY_LISTENER = new ITaskActivityListener() {

		public void taskActivated(final AbstractTask task) {
			ContextCorePlugin.getContextManager().activateContext(task.getHandleIdentifier());
		}

		public void taskDeactivated(final AbstractTask task) {
			ContextCorePlugin.getContextManager().deactivateContext(task.getHandleIdentifier());
		}

		public void activityChanged(ScheduledTaskContainer week) {
			// ignore
		}

		public void taskListRead() {
			// ignore
		}
	};

	private static ITaskListNotificationProvider REMINDER_NOTIFICATION_PROVIDER = new ITaskListNotificationProvider() {

		public Set<ITaskListNotification> getNotifications() {
			Date currentDate = new Date();
			Collection<AbstractTask> allTasks = TasksUiPlugin.getTaskListManager().getTaskList().getAllTasks();
			Set<ITaskListNotification> reminders = new HashSet<ITaskListNotification>();
			for (AbstractTask task : allTasks) {
				if (!task.isCompleted() && task.getScheduledForDate() != null && !task.isReminded()
						&& task.getScheduledForDate().compareTo(currentDate) < 0) {
					reminders.add(new TaskListNotificationReminder(task));
					task.setReminded(true);
				}
			}
			return reminders;
		}
	};

	private static ITaskListNotificationProvider INCOMING_NOTIFICATION_PROVIDER = new ITaskListNotificationProvider() {

		public Set<ITaskListNotification> getNotifications() {
			Set<ITaskListNotification> notifications = new HashSet<ITaskListNotification>();
			// Incoming Changes
			for (TaskRepository repository : getRepositoryManager().getAllRepositories()) {
				AbstractRepositoryConnector connector = getRepositoryManager().getRepositoryConnector(
						repository.getConnectorKind());
				AbstractRepositoryConnectorUi connectorUi = getConnectorUi(repository.getConnectorKind());
				if (connectorUi != null && !connectorUi.isCustomNotificationHandling()) {
					for (AbstractTask repositoryTask : TasksUiPlugin.getTaskListManager()
							.getTaskList()
							.getRepositoryTasks(repository.getUrl())) {
						if ((repositoryTask.getLastReadTimeStamp() == null || repositoryTask.getSynchronizationState() == RepositoryTaskSyncState.INCOMING)
								&& repositoryTask.isNotified() == false) {
							TaskListNotificationIncoming notification = INSTANCE.getIncommingNotification(connector,
									repositoryTask);
							notifications.add(notification);
							repositoryTask.setNotified(true);
						}
					}
				}
			}
			// New query hits
			for (AbstractRepositoryQuery query : TasksUiPlugin.getTaskListManager().getTaskList().getQueries()) {
				AbstractRepositoryConnectorUi connectorUi = getConnectorUi(query.getRepositoryKind());
				if (!connectorUi.isCustomNotificationHandling()) {
					for (AbstractTask hit : query.getChildren()) {
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

	private final IPropertyChangeListener PREFERENCE_LISTENER = new IPropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent event) {
			// TODO: do we ever get here?
			if (event.getProperty().equals(ContextPreferenceContstants.PREF_DATA_DIR)) {
				if (event.getOldValue() instanceof String) {
					reloadDataDirectory(true);
				}
			}
		}

	};

	private final org.eclipse.jface.util.IPropertyChangeListener PROPERTY_LISTENER = new org.eclipse.jface.util.IPropertyChangeListener() {

		public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
			if (event.getProperty().equals(ContextPreferenceContstants.PREF_DATA_DIR)) {
				if (event.getOldValue() instanceof String) {
					reloadDataDirectory(true);
				}
			}
		}

	};

	private class TasksUiInitializationJob extends UIJob {

		public TasksUiInitializationJob() {
			super("Initializing Task List");
			setSystem(true);
		}

//		public IStatus runInUIThread(IProgressMonitor monitor) {
//			Job job = new RealJob(JavaUIMessages.JavaPlugin_initializing_ui);
//			job.setPriority(Job.SHORT);
//			job.schedule();
//			return new Status(IStatus.OK, JavaPlugin.getPluginId(), IStatus.OK, "", null); //$NON-NLS-1$
//		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			// NOTE: failure in one part of the initialization should
			// not prevent others
			monitor.beginTask("Initializing Task List", 5);
			try {
				// Needs to run after workbench is loaded because it
				// relies on images.
				TasksUiExtensionReader.initWorkbenchUiExtensions();

				// Needs to happen asynchronously to avoid bug 159706
				if (taskListManager.getTaskList().getActiveTask() != null) {
					taskListManager.activateTask(taskListManager.getTaskList().getActiveTask());
				}
				taskListManager.initActivityHistory();
			} catch (Throwable t) {
				StatusHandler.fail(t, "Could not initialize task activity", false);
			}
			monitor.worked(1);

			try {
				taskListNotificationManager = new TaskListNotificationManager();
				taskListNotificationManager.addNotificationProvider(REMINDER_NOTIFICATION_PROVIDER);
				taskListNotificationManager.addNotificationProvider(INCOMING_NOTIFICATION_PROVIDER);
				taskListNotificationManager.startNotification(NOTIFICATION_DELAY);
				getPreferenceStore().addPropertyChangeListener(taskListNotificationManager);
			} catch (Throwable t) {
				StatusHandler.fail(t, "Could not initialize notifications", false);
			}
			monitor.worked(1);

			try {
				taskListBackupManager = new TaskListBackupManager();
				getPreferenceStore().addPropertyChangeListener(taskListBackupManager);

				synchronizationScheduler = new TaskListSynchronizationScheduler(true);
				synchronizationScheduler.startSynchJob();
			} catch (Throwable t) {
				StatusHandler.fail(t, "Could not initialize task list backup and synchronization", false);
			}
			monitor.worked(1);

			try {
				taskListSaveManager = new TaskListSaveManager();
				taskListManager.setTaskListSaveManager(taskListSaveManager);

				ContextCorePlugin.getDefault().getPluginPreferences().addPropertyChangeListener(PREFERENCE_LISTENER);

				getPreferenceStore().addPropertyChangeListener(PROPERTY_LISTENER);
				getPreferenceStore().addPropertyChangeListener(synchronizationScheduler);
				getPreferenceStore().addPropertyChangeListener(taskListManager);

				// TODO: get rid of this, hack to make decorators show
				// up on startup
				TaskRepositoriesView repositoriesView = TaskRepositoriesView.getFromActivePerspective();
				if (repositoriesView != null) {
					repositoriesView.getViewer().refresh();
				}
				checkForCredentials();
			} catch (Throwable t) {
				StatusHandler.fail(t, "Could not finish Tasks UI initialization", false);
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

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		// NOTE: startup order is very sensitive
		try {
			StatusHandler.addStatusHandler(new RepositoryAwareStatusHandler());
			WebClientUtil.setLoggingEnabled(DEBUG_HTTPCLIENT);
			initializeDefaultPreferences(getPreferenceStore());
			taskListWriter = new TaskListWriter();

			File dataDir = new File(getDataDirectory());
			dataDir.mkdirs();
			String path = getDataDirectory() + File.separator + ITasksUiConstants.DEFAULT_TASK_LIST_FILE;

			File taskListFile = new File(path);
			taskListManager = new TaskListManager(taskListWriter, taskListFile);
			taskActivityManager = TaskActivityManager.getInstance();
			taskRepositoryManager = new TaskRepositoryManager(taskListManager.getTaskList());

			IProxyService proxyService = ProxyManager.getProxyManager();
			IProxyChangeListener proxyChangeListener = new TasksUiProxyChangeListener(taskRepositoryManager);
			proxyService.addProxyChangeListener(proxyChangeListener);

			synchronizationManager = new RepositorySynchronizationManager();

			// NOTE: initializing extensions in start(..) has caused race
			// conditions previously
			TasksUiExtensionReader.initStartupExtensions(taskListWriter);

			taskRepositoryManager.readRepositories(getRepositoriesFilePath());

			// instantiates taskDataManager
			startOfflineStorageManager();

			loadTemplateRepositories();

			// NOTE: task list must be read before Task List view can be
			// initialized
			taskListManager.init();
			taskListManager.addActivityListener(CONTEXT_TASK_ACTIVITY_LISTENER);
			taskListManager.readExistingOrCreateNewList();
			initialized = true;

			// if the taskListManager didn't initialize do it here
			if (!taskActivityManager.isInitialized()) {
				taskActivityManager.init(taskRepositoryManager, taskListManager.getTaskList());
				taskActivityManager.setEndHour(getPreferenceStore().getInt(TasksUiPreferenceConstants.PLANNING_ENDHOUR));
			}

			saveParticipant = new ISaveParticipant() {

				public void doneSaving(ISaveContext context) {
				}

				public void prepareToSave(ISaveContext context) throws CoreException {
				}

				public void rollback(ISaveContext context) {
				}

				public void saving(ISaveContext context) throws CoreException {
					if (context.getKind() == ISaveContext.FULL_SAVE) {
						taskListManager.saveTaskList();
						taskDataManager.stop();
					}
				}
			};
			ResourcesPlugin.getWorkspace().addSaveParticipant(this, saveParticipant);

			new TasksUiInitializationJob().schedule();
		} catch (Exception e) {
			e.printStackTrace();
			StatusHandler.fail(e, "Mylyn Task List initialization failed", false);
		}
	}

	private void loadTemplateRepositories() {

		// Add standard local task repository
		if (taskRepositoryManager.getRepository(LocalRepositoryConnector.REPOSITORY_URL) == null) {
			TaskRepository localRepository = new TaskRepository(LocalRepositoryConnector.REPOSITORY_KIND,
					LocalRepositoryConnector.REPOSITORY_URL, LocalRepositoryConnector.REPOSITORY_VERSION);
			localRepository.setRepositoryLabel(LocalRepositoryConnector.REPOSITORY_LABEL);
			localRepository.setAnonymous(true);
			taskRepositoryManager.addRepository(localRepository, getRepositoriesFilePath());
		}

		// Add the automatically created templates
		for (AbstractRepositoryConnector connector : taskRepositoryManager.getRepositoryConnectors()) {
			connector.setTaskDataManager(taskDataManager);

			for (RepositoryTemplate template : connector.getTemplates()) {

				if (template.addAutomatically && !TaskRepositoryUtil.isAddAutomaticallyDisabled(template.repositoryUrl)) {
					try {
						TaskRepository taskRepository = taskRepositoryManager.getRepository(
								connector.getConnectorKind(), template.repositoryUrl);
						if (taskRepository == null) {
							taskRepository = new TaskRepository(connector.getConnectorKind(), template.repositoryUrl,
									template.version);
							taskRepository.setRepositoryLabel(template.label);
							taskRepository.setAnonymous(true);
							taskRepositoryManager.addRepository(taskRepository, getRepositoriesFilePath());
						}
					} catch (Throwable t) {
						StatusHandler.fail(t, "Could not load repository template", false);
					}
				}
			}
		}
	}

	private void checkForCredentials() {
		for (TaskRepository repository : taskRepositoryManager.getAllRepositories()) {
			AbstractRepositoryConnector connector = getRepositoryManager().getRepositoryConnector(
					repository.getConnectorKind());
			boolean userManaged = true;
			if (connector != null) {
				userManaged = connector.isUserManaged();
			}
			if (!repository.isAnonymous()
					&& !userManaged
					&& (repository.getUserName() == null || repository.getPassword() == null
							|| "".equals(repository.getUserName()) || "".equals(repository.getPassword()))) {
				try {
					EditRepositoryWizard wizard = new EditRepositoryWizard(repository);
					Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
					if (shell != null && !shell.isDisposed()) {
						WizardDialog dialog = new WizardDialog(shell, wizard);
						dialog.create();
						// dialog.setTitle("Repository Credentials Missing");
						dialog.setErrorMessage("Authentication credentials missing.");
						dialog.setBlockOnOpen(true);
						if (dialog.open() == Dialog.CANCEL) {
							dialog.close();
							return;
						}
					}
				} catch (Exception e) {
					StatusHandler.fail(e, e.getMessage(), true);
				}
			}
		}
	}

	public void earlyStartup() {
		// ignore
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		try {

			if (ResourcesPlugin.getWorkspace() != null) {
				ResourcesPlugin.getWorkspace().removeSaveParticipant(this);
			}

			if (PlatformUI.isWorkbenchRunning()) {
				getPreferenceStore().removePropertyChangeListener(taskListNotificationManager);
				getPreferenceStore().removePropertyChangeListener(taskListBackupManager);
				getPreferenceStore().removePropertyChangeListener(taskListManager);
				getPreferenceStore().removePropertyChangeListener(synchronizationScheduler);
				getPreferenceStore().removePropertyChangeListener(PROPERTY_LISTENER);
				taskListManager.getTaskList().removeChangeListener(taskListSaveManager);
				taskListManager.dispose();
				TaskListColorsAndFonts.dispose();
				if (ContextCorePlugin.getDefault() != null) {
					ContextCorePlugin.getDefault().getPluginPreferences().removePropertyChangeListener(
							PREFERENCE_LISTENER);
				}
				INSTANCE = null;
			}
		} catch (Exception e) {
			StatusHandler.log(e, "Mylyn Task List stop terminated abnormally");
		} finally {
			super.stop(context);
		}
	}

	public String getDefaultDataDirectory() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + '/' + DIRECTORY_METADATA + '/'
				+ NAME_DATA_DIR;
	}

	/**
	 * Only attempt once per startup.
	 */
	private boolean attemptMigration = true;

	public synchronized String getDataDirectory() {
		if (attemptMigration) {
			migrateFromLegacyDirectory();
			attemptMigration = false;
		}
//		return ContextCorePlugin.getDefault().getContextStore().getRootDirectory().getAbsolutePath();
		return getPreferenceStore().getString(ContextPreferenceContstants.PREF_DATA_DIR);
	}

	@Deprecated
	private void migrateFromLegacyDirectory() {
		// Migrate .mylar data folder to .metadata/.mylyn
		String oldDefaultDataPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + '/' + ".mylar";
		File oldDefaultDataDir = new File(oldDefaultDataPath);
		if (oldDefaultDataDir.exists()) { // && !newDefaultDataDir.exists()) {
			File metadata = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + '/'
					+ DIRECTORY_METADATA);
			if (!metadata.exists()) {
				if (!metadata.mkdirs()) {
					StatusHandler.log("Unable to create metadata folder: " + metadata.getAbsolutePath(), this);
				}
			}
			File newDefaultDataDir = new File(getPreferenceStore().getString(ContextPreferenceContstants.PREF_DATA_DIR));
			if (metadata.exists()) {
				if (!oldDefaultDataDir.renameTo(newDefaultDataDir)) {
					StatusHandler.log("Could not migrate legacy data from " + oldDefaultDataDir.getAbsolutePath()
							+ " to " + TasksUiPlugin.getDefault().getDefaultDataDirectory(), this);
				} else {
					StatusHandler.log("Migrated legacy task data from " + oldDefaultDataDir.getAbsolutePath() + " to "
							+ TasksUiPlugin.getDefault().getDefaultDataDirectory(), this);
				}
			}
		}
	}

	public void setDataDirectory(String newPath) {
		getTaskListManager().saveTaskList();
		ContextCorePlugin.getContextManager().saveActivityContext();
		getPreferenceStore().setValue(ContextPreferenceContstants.PREF_DATA_DIR, newPath);
		ContextCorePlugin.getDefault().getContextStore().contextStoreMoved();
	}

	/**
	 * Only support task data versions post 0.7
	 * 
	 * @param withProgress
	 */
	public void reloadDataDirectory(boolean withProgress) {
		getTaskListManager().resetTaskList();
		getTaskListManager().getTaskActivationHistory().clear();
		getRepositoryManager().readRepositories(getRepositoriesFilePath());
		loadTemplateRepositories();
		ContextCorePlugin.getContextManager().loadActivityMetaContext();
		getTaskListManager().setTaskListFile(
				new File(getDataDirectory() + File.separator + ITasksUiConstants.DEFAULT_TASK_LIST_FILE));
		getTaskListManager().readExistingOrCreateNewList();
		getTaskListManager().initActivityHistory();
		checkForCredentials();
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

		store.setDefault(TasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED, true);
		store.setDefault(TasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS, "" + (20 * 60 * 1000));

		store.setDefault(TasksUiPreferenceConstants.BACKUP_SCHEDULE, 1);
		store.setDefault(TasksUiPreferenceConstants.BACKUP_MAXFILES, 20);
		store.setDefault(TasksUiPreferenceConstants.BACKUP_LAST, 0f);

		store.setDefault(TasksUiPreferenceConstants.FILTER_ARCHIVE_MODE, true);
		store.setDefault(TasksUiPreferenceConstants.ACTIVATE_MULTIPLE, false);
		store.setValue(TasksUiPreferenceConstants.ACTIVATE_MULTIPLE, false);

		store.setDefault(TasksUiPreferenceConstants.PLANNING_STARTHOUR, 9);
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

	public boolean groupSubtasks(AbstractTaskContainer container) {
		boolean groupSubtasks = TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(
				TasksUiPreferenceConstants.GROUP_SUBTASKS);

		if (container instanceof AbstractTask) {
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(((AbstractTask) container).getConnectorKind());
			if (connectorUi != null) {
				if (connectorUi.forceSubtaskHierarchy()) {
					groupSubtasks = true;
				}
			}
		}

		if (container instanceof AbstractRepositoryQuery) {
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(((AbstractRepositoryQuery) container).getRepositoryKind());
			if (connectorUi != null) {
				if (connectorUi.forceSubtaskHierarchy()) {
					groupSubtasks = true;
				}
			}
		}

		return groupSubtasks;
	}

	private Map<String, List<IDynamicSubMenuContributor>> menuContributors = new HashMap<String, List<IDynamicSubMenuContributor>>();

	public Map<String, List<IDynamicSubMenuContributor>> getDynamicMenuMap() {
		return menuContributors;
	}

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
		return getDataDirectory() + DEFAULT_PATH_SEPARATOR + ITasksUiConstants.DEFAULT_BACKUP_FOLDER_NAME;
	}

	public ITaskHighlighter getHighlighter() {
		return highlighter;
	}

	public void setHighlighter(ITaskHighlighter highlighter) {
		this.highlighter = highlighter;
	}

	public Set<AbstractTaskEditorFactory> getTaskEditorFactories() {
		return taskEditorFactories;
	}

	public void addContextEditor(AbstractTaskEditorFactory contextEditor) {
		if (contextEditor != null)
			this.taskEditorFactories.add(contextEditor);
	}

	public static TaskRepositoryManager getRepositoryManager() {
		return taskRepositoryManager;
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
		if (listener != null)
			this.hyperlinkDetectors.add(listener);
	}

	public void addRepositoryLinkProvider(AbstractTaskRepositoryLinkProvider repositoryLinkProvider) {
		if (repositoryLinkProvider != null)
			this.repositoryLinkProviders.add(repositoryLinkProvider);
	}

	public TaskListBackupManager getBackupManager() {
		return taskListBackupManager;
	}

	private void startOfflineStorageManager() {
		//IPath offlineReportsPath = Platform.getStateLocation(TasksUiPlugin.getDefault().getBundle());
		File root = new File(this.getDataDirectory() + '/' + FOLDER_OFFLINE);
		OfflineFileStorage storage = new OfflineFileStorage(root);
		OfflineCachingStorage cachedStorage = new OfflineCachingStorage(storage);
		taskDataManager = new TaskDataManager(taskRepositoryManager, cachedStorage);
		taskDataManager.start();
	}

	public static TaskDataManager getTaskDataManager() {
		if (INSTANCE == null || INSTANCE.taskDataManager == null) {
			StatusHandler.fail(null, "Offline reports file not created, try restarting.", true);
			return null;
		} else {
			return INSTANCE.taskDataManager;
		}
	}

	public void addRepositoryConnectorUi(AbstractRepositoryConnectorUi repositoryConnectorUi) {
		if (!repositoryConnectorUiMap.values().contains(repositoryConnectorUi)) {
			repositoryConnectorUiMap.put(repositoryConnectorUi.getConnectorKind(), repositoryConnectorUi);
		}
	}

	public static AbstractRepositoryConnectorUi getConnectorUi(String kind) {
		return repositoryConnectorUiMap.get(kind);
	}

	public static TaskListSynchronizationScheduler getSynchronizationScheduler() {
		return synchronizationScheduler;
	}

	public static RepositorySynchronizationManager getSynchronizationManager() {
		return synchronizationManager;
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
	 *            project or resource belonging to a project
	 * @param repository
	 *            task repository to associate with given project
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
	 * NOTE: if call does not return in LINK_PROVIDER_TIMEOUT_SECONDS, the provide will be disabled
	 * until the next time that the Workbench starts.
	 * 
	 * API-3.0: remove "silent" parameter
	 */
	public TaskRepository getRepositoryForResource(IResource resource, boolean silent) {
		if (resource == null) {
			return null;
		}
		Set<AbstractTaskRepositoryLinkProvider> defectiveLinkProviders = new HashSet<AbstractTaskRepositoryLinkProvider>();
		for (AbstractTaskRepositoryLinkProvider linkProvider : repositoryLinkProviders) {
			long startTime = System.nanoTime();
			TaskRepository repository = linkProvider.getTaskRepository(resource, getRepositoryManager());
			long elapsed = System.nanoTime() - startTime;
			if (elapsed > LINK_PROVIDER_TIMEOUT_SECONDS * 1000 * 1000 * 1000) {
				defectiveLinkProviders.add(linkProvider);
			}
			if (repository != null) {
				return repository;
			}
		}
		if (!defectiveLinkProviders.isEmpty()) {
			repositoryLinkProviders.removeAll(defectiveLinkProviders);
			StatusHandler.log(new Status(IStatus.WARNING, ID_PLUGIN, "Repository link provider took over 5s to execute and was timed out: " + defectiveLinkProviders));
		}
		
		if (!silent) {
			MessageDialog.openInformation(null, "No Repository Found",
					"No repository was found. Associate a Task Repository with this project via the project's property page.");
		}

		return null;
	}

	/**
	 * Public for testing.
	 */
	public static TaskListSaveManager getTaskListSaveManager() {
		return INSTANCE.taskListSaveManager;
	}

	public String getNextNewRepositoryTaskId() {
		return getTaskDataManager().getNewRepositoryTaskId();
	}

	/**
	 * TODO: move, uses internal class.
	 */
	public TaskListNotificationIncoming getIncommingNotification(AbstractRepositoryConnector connector,
			AbstractTask task) {

		TaskListNotificationIncoming notification = new TaskListNotificationIncoming(task);
		RepositoryTaskData newTaskData = getTaskDataManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
		RepositoryTaskData oldTaskData = getTaskDataManager().getOldTaskData(task.getRepositoryUrl(), task.getTaskId());

		try {
			if (task.getSynchronizationState().equals(RepositoryTaskSyncState.INCOMING)
					&& task.getLastReadTimeStamp() == null) {
				notification.setDescription("New unread task");
			} else if (newTaskData != null && oldTaskData != null) {
				notification.setDescription(getChangedDescription(newTaskData, oldTaskData));
				notification.setDetails(getChangedAttributes(newTaskData, oldTaskData));

				if (connector != null) {
					AbstractTaskDataHandler offlineHandler = connector.getTaskDataHandler();
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
			StatusHandler.fail(t, "Could not format notification for: " + task, false);
		}
		return notification;
	}

	private static String getChangedDescription(RepositoryTaskData newTaskData, RepositoryTaskData oldTaskData) {
		String descriptionText = "";

		if (newTaskData.getComments().size() > oldTaskData.getComments().size()) {
			List<TaskComment> taskComments = newTaskData.getComments();
			if (taskComments != null && taskComments.size() > 0) {
				TaskComment lastComment = taskComments.get(taskComments.size() - 1);
				if (lastComment != null) {
					descriptionText += "Comment by " + lastComment.getAuthor() + ":\n  ";
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
				details += "\n  ";
			}
			details += " -> " + added;
			sep = "\n";

			n++;
			if (n > 5) {
				details += "\nOpen to view more changes";
				break;
			}
		}
		if (!details.equals("")) {
			return "Attributes Changed:\n" + details;
		}
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
		String commentText = value.replaceAll("\\s", " ").trim();
		if (commentText.length() > 60) {
			commentText = commentText.substring(0, 55) + "...";
		}
		return commentText;
	}

	private static boolean ignoreAttribute(RepositoryTaskData taskData, RepositoryTaskAttribute attribute) {
		AbstractAttributeFactory factory = taskData.getAttributeFactory();
		if (attribute.getId().equals(factory.mapCommonAttributeKey(RepositoryTaskAttribute.USER_CC))) {
			return false;
		}
		return (attribute.isHidden()
				|| attribute.getId().equals(factory.mapCommonAttributeKey(RepositoryTaskAttribute.DATE_MODIFIED))
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

}
