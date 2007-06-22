/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
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
import org.eclipse.mylyn.internal.monitor.core.util.StatusManager;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
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
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.WorkspaceAwareContextStore;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskListSaveManager;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskListWriter;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiExtensionReader;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoriesView;
import org.eclipse.mylyn.internal.tasks.ui.wizards.EditRepositoryWizard;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.AbstractTaskDataHandler;
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
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 * 
 * TODO: this class is in serious need of refactoring
 */
public class TasksUiPlugin extends AbstractUIPlugin implements IStartup {

	private static final String FOLDER_OFFLINE = "offline";

	public static final String LABEL_VIEW_REPOSITORIES = "Task Repositories view";

	public static final String PLUGIN_ID = "org.eclipse.mylyn.tasklist";

	private static final String DIRECTORY_METADATA = ".metadata";

	private static final String NAME_DATA_DIR = ".mylyn";

	private static final char DEFAULT_PATH_SEPARATOR = '/';

	private static final int NOTIFICATION_DELAY = 5000;

	private static TasksUiPlugin INSTANCE;

	private static TaskListManager taskListManager;

	private static TaskRepositoryManager taskRepositoryManager;

	private static TaskListSynchronizationScheduler synchronizationScheduler;

	private static RepositorySynchronizationManager synchronizationManager;

	private static Map<String, AbstractRepositoryConnectorUi> repositoryConnectorUis = new HashMap<String, AbstractRepositoryConnectorUi>();

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

	private static boolean shellActive = true;

	private boolean initialized = false;

	private Map<String, Image> brandingIcons = new HashMap<String, Image>();

	private Map<String, ImageDescriptor> overlayIcons = new HashMap<String, ImageDescriptor>();

	private Set<AbstractDuplicateDetector> duplicateDetectors = new HashSet<AbstractDuplicateDetector>();

	private boolean eclipse_3_3_workbench = false;

	private ISaveParticipant saveParticipant;

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

		public void calendarChanged() {
			// ignore
		}
	};

	/**
	 * TODO: move into reminder mechanisms
	 */
	private IWindowListener WINDOW_LISTENER = new IWindowListener() {
		/**
		 * bug 1002249: too slow to save state here
		 */
		public void windowDeactivated(IWorkbenchWindow window) {
			shellActive = false;
		}

		public void windowActivated(IWorkbenchWindow window) {
			// getDefault().checkTaskListBackup();
			shellActive = true;
		}

		public void windowOpened(IWorkbenchWindow window) {
			// ignore
		}

		public void windowClosed(IWorkbenchWindow window) {
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
						repository.getKind());
				AbstractRepositoryConnectorUi connectorUi = getRepositoryUi(repository.getKind());
				if (connectorUi != null && !connectorUi.hasCustomNotificationHandling()) {
					for (AbstractTask repositoryTask : TasksUiPlugin.getTaskListManager()
							.getTaskList()
							.getRepositoryTasks(repository.getUrl())) {
						if ((repositoryTask.getLastReadTimeStamp() == null || repositoryTask.getSynchronizationState() == RepositoryTaskSyncState.INCOMING)
								&& repositoryTask.isNotified() == false) {
							TaskListNotificationIncoming notification = getIncommingNotification(connector,
									repositoryTask);
							notifications.add(notification);
							repositoryTask.setNotified(true);
						}
					}
				}
			}
			// New query hits
			for (AbstractRepositoryQuery query : TasksUiPlugin.getTaskListManager().getTaskList().getQueries()) {
				AbstractRepositoryConnectorUi connectorUi = getRepositoryUi(query.getRepositoryKind());
				if (!connectorUi.hasCustomNotificationHandling()) {
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
			if (event.getProperty().equals(TasksUiPreferenceConstants.ACTIVATE_MULTIPLE)) {
				TaskListView.getFromActivePerspective().togglePreviousAction(
						!getPreferenceStore().getBoolean(TasksUiPreferenceConstants.ACTIVATE_MULTIPLE));
				getTaskListManager().getTaskActivationHistory().clear();

			}
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

	public TasksUiPlugin() {
		super();
		INSTANCE = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		// NOTE: startup order is very sensitive
		try {
			StatusManager.addStatusHandler(new RepositoryAwareStatusHandler());
			WebClientUtil.initCommonsLoggingSettings();
			initializeDefaultPreferences(getPreferenceStore());
			taskListWriter = new TaskListWriter();

			File dataDir = new File(getDataDirectory());
			dataDir.mkdirs();
			String path = getDataDirectory() + File.separator + ITasksUiConstants.DEFAULT_TASK_LIST_FILE;

			File taskListFile = new File(path);
			taskListManager = new TaskListManager(taskListWriter, taskListFile);
			taskRepositoryManager = new TaskRepositoryManager(taskListManager.getTaskList());
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

			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					// NOTE: failure in one part of the initialization should
					// not prevent others
					try {
						// Needs to run after workbench is loaded because it
						// relies on images.
						TasksUiExtensionReader.initWorkbenchUiExtensions();

						// TasksUiExtensionReader.initWorkbenchUiExtensions();
						PlatformUI.getWorkbench().addWindowListener(WINDOW_LISTENER);

						// Needs to happen asynchronously to avoid bug 159706
						if (taskListManager.getTaskList().getActiveTask() != null) {
							taskListManager.activateTask(taskListManager.getTaskList().getActiveTask());
						}
						taskListManager.initActivityHistory();
					} catch (Throwable t) {
						StatusManager.fail(t, "Could not initialize task activity", false);
					}

					try {
						taskListNotificationManager = new TaskListNotificationManager();
						taskListNotificationManager.addNotificationProvider(REMINDER_NOTIFICATION_PROVIDER);
						taskListNotificationManager.addNotificationProvider(INCOMING_NOTIFICATION_PROVIDER);
						taskListNotificationManager.startNotification(NOTIFICATION_DELAY);
						getPreferenceStore().addPropertyChangeListener(taskListNotificationManager);
					} catch (Throwable t) {
						StatusManager.fail(t, "Could not initialize notifications", false);
					}

					try {
						taskListBackupManager = new TaskListBackupManager();
						getPreferenceStore().addPropertyChangeListener(taskListBackupManager);

						synchronizationScheduler = new TaskListSynchronizationScheduler(true);
						synchronizationScheduler.startSynchJob();
					} catch (Throwable t) {
						StatusManager.fail(t, "Could not initialize task list backup and synchronization", false);
					}

					try {
						taskListSaveManager = new TaskListSaveManager();
						taskListManager.setTaskListSaveManager(taskListSaveManager);

						ContextCorePlugin.getDefault().getPluginPreferences().addPropertyChangeListener(
								PREFERENCE_LISTENER);

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
						StatusManager.fail(t, "Could not finish Tasks UI initialization", false);
					}
				}
			});

			Bundle bundle = Platform.getBundle("org.eclipse.ui.workbench");
			if (bundle.getLocation().contains("_3.3.")) {
				eclipse_3_3_workbench = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			StatusManager.fail(e, "Mylar Task List initialization failed", false);
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
				if (template.addAutomatically) {
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
						StatusManager.fail(t, "Could not load repository template", false);
					}
				}
			}
		}
	}

	private void checkForCredentials() {
		for (TaskRepository repository : taskRepositoryManager.getAllRepositories()) {
			if (!repository.isAnonymous()
					&& (repository.getUserName() == null || repository.getPassword() == null
							|| "".equals(repository.getUserName()) || "".equals(repository.getPassword()))) {
				try {
					EditRepositoryWizard wizard = new EditRepositoryWizard(repository);
					Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
					if (wizard != null && shell != null && !shell.isDisposed()) {
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
					StatusManager.fail(e, e.getMessage(), true);
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
				PlatformUI.getWorkbench().removeWindowListener(WINDOW_LISTENER);
				INSTANCE = null;
			}
		} catch (Exception e) {
			StatusManager.log(e, "Mylar Task List stop terminated abnormally");
		} finally {
			super.stop(context);
		}
	}

	public String getDefaultDataDirectory() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + '/' +DIRECTORY_METADATA+ '/'+ NAME_DATA_DIR;
	}

	public String getDataDirectory() {
		return getPreferenceStore().getString(ContextPreferenceContstants.PREF_DATA_DIR);
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
		store.setDefault(TasksUiPreferenceConstants.FILTER_SUBTASKS, true);
		store.setDefault(TasksUiPreferenceConstants.NOTIFICATIONS_ENABLED, true);
		store.setDefault(TasksUiPreferenceConstants.FILTER_PRIORITY, PriorityLevel.P5.toString());
		store.setDefault(TasksUiPreferenceConstants.EDITOR_TASKS_RICH, true);
		store.setDefault(TasksUiPreferenceConstants.ACTIVATE_WHEN_OPENED, false);

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

	public TaskListNotificationManager getTaskListNotificationManager() {
		return taskListNotificationManager;
	}

	/**
	 * Returns the shared instance.
	 */
	public static TasksUiPlugin getDefault() {
		return INSTANCE;
	}

	// private void checkTaskListBackup() {
	// Date currentTime = new Date();
	// if (currentTime.getTime() > lastBackup.getTime() +
	// AUTOMATIC_BACKUP_SAVE_INTERVAL) {//
	// TaskListSaveMode.fromStringToLong(getPrefs().getString(SAVE_TASKLIST_MODE)))
	// // {
	// TasksUiPlugin.getDefault().getTaskListSaveManager().createTaskListBackupFile();
	// lastBackup = new Date();
	// }
	// }

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

	// TODO: remove
	public boolean isMultipleActiveTasksMode() {
		return getPreferenceStore().getBoolean(TasksUiPreferenceConstants.ACTIVATE_MULTIPLE);
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

	public boolean isShellActive() {
		return TasksUiPlugin.shellActive;
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

	public TaskDataManager getTaskDataManager() {
		if (taskDataManager == null) {
			StatusManager.fail(null, "Offline reports file not created, try restarting.", true);
		}
		return taskDataManager;
	}

	public static void addRepositoryConnectorUi(AbstractRepositoryConnectorUi repositoryConnectorUi) {
		if (!repositoryConnectorUis.values().contains(repositoryConnectorUi)) {
			repositoryConnectorUis.put(repositoryConnectorUi.getRepositoryType(), repositoryConnectorUi);
		}
	}

	public static AbstractRepositoryConnectorUi getRepositoryUi(String kind) {
		return repositoryConnectorUis.get(kind);
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
	 */
	public TaskRepository getRepositoryForResource(IResource resource, boolean silent) {
		if (resource == null) {
			return null;
		}

		for (AbstractTaskRepositoryLinkProvider linkProvider : repositoryLinkProviders) {
			TaskRepository repository = linkProvider.getTaskRepository(resource, getRepositoryManager());
			if (repository != null) {
				return repository;
			}
		}

		if (!silent) {
			MessageDialog.openInformation(null, "No Repository Found",
					"No repository was found. Associate a Task Repository with this project via the project's property page.");
		}

		return null;
	}

	public boolean isEclipse_3_3_workbench() {
		return eclipse_3_3_workbench;
	}

	/**
	 * Public for testing.
	 */
	public TaskListSaveManager getTaskListSaveManager() {
		return taskListSaveManager;
	}

	public String getNextNewRepositoryTaskId() {
		return getTaskDataManager().getNewRepositoryTaskId();
	}

	public static TaskListNotificationIncoming getIncommingNotification(AbstractRepositoryConnector connector,
			AbstractTask repositoryTask) {

		TaskListNotificationIncoming notification = new TaskListNotificationIncoming(repositoryTask);
		RepositoryTaskData newTaskData = getDefault().getTaskDataManager().getNewTaskData(
				repositoryTask.getRepositoryUrl(), repositoryTask.getTaskId());
		RepositoryTaskData oldTaskData = getDefault().getTaskDataManager().getOldTaskData(
				repositoryTask.getRepositoryUrl(), repositoryTask.getTaskId());

		if (newTaskData != null && oldTaskData != null) {

			String descriptionText = getChangedDescription(newTaskData, oldTaskData);
			if (descriptionText != null) {
				notification.setDescription(descriptionText);
			}

			if (connector != null) {
				AbstractTaskDataHandler offlineHandler = connector.getTaskDataHandler();
				if (offlineHandler != null && newTaskData.getLastModified() != null) {
					Date modified = newTaskData.getAttributeFactory().getDateForAttributeType(
							RepositoryTaskAttribute.DATE_MODIFIED, newTaskData.getLastModified());
					notification.setDate(modified);
				}
			}

		} else {
			notification.setDescription("Open to view changes");
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
					String commentText = lastComment.getText();
					if (commentText.length() > 60) {
						commentText = commentText.substring(0, 55) + "...";
					}
					descriptionText += commentText;
				}
			}
		}

		boolean attributeChanged = false;

		for (RepositoryTaskAttribute newAttribute : newTaskData.getAttributes()) {
			RepositoryTaskAttribute oldAttribute = oldTaskData.getAttribute(newAttribute.getId());
			if (oldAttribute == null) {
				attributeChanged = true;
				break;
			}
			if (oldAttribute.getValue() != null && !oldAttribute.getValue().equals(newAttribute.getValue())) {
				attributeChanged = true;
				break;
			} else if (oldAttribute.getValues() != null && !oldAttribute.getValues().equals(newAttribute.getValues())) {
				attributeChanged = true;
				break;
			}
		}

		if (attributeChanged) {
			if (descriptionText.equals("")) {
				descriptionText += "Attributes changed";
			}
		}
// else {
// String description = taskData.getDescription();
// if (description != null) {
// notification.setDescription(description);
// }
// }

		return descriptionText;
	}

}
