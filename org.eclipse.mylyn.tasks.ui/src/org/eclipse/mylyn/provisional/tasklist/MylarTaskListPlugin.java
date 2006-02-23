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
package org.eclipse.mylar.provisional.tasklist;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.mylar.internal.core.MylarPreferenceContstants;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.TaskListPreferenceConstants;
import org.eclipse.mylar.internal.tasklist.TaskListRefreshManager;
import org.eclipse.mylar.internal.tasklist.ui.IDynamicSubMenuContributor;
import org.eclipse.mylar.internal.tasklist.ui.ITaskEditorFactory;
import org.eclipse.mylar.internal.tasklist.ui.ITaskHighlighter;
import org.eclipse.mylar.internal.tasklist.ui.ITaskListNotification;
import org.eclipse.mylar.internal.tasklist.ui.ITaskListNotificationProvider;
import org.eclipse.mylar.internal.tasklist.ui.TaskListNotificationManager;
import org.eclipse.mylar.internal.tasklist.ui.TaskListNotificationReminder;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.internal.tasklist.util.TaskListExtensionReader;
import org.eclipse.mylar.internal.tasklist.util.TaskListSaveManager;
import org.eclipse.mylar.internal.tasklist.util.TaskListWriter;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 * 
 * TODO: this class is in serious need of refactoring
 */
public class MylarTaskListPlugin extends AbstractUIPlugin implements IStartup {

	public static final String PLUGIN_ID = "org.eclipse.mylar.tasklist";

	public static final String URL_HOMEPAGE = "http://eclipse.org/mylar";
	
	private static MylarTaskListPlugin INSTANCE;

	private static TaskListManager taskListManager;

	private static TaskRepositoryManager taskRepositoryManager;

	private TaskListSaveManager taskListSaveManager;
	
	private TaskListRefreshManager taskListRefreshManager;
	
	private TaskListNotificationManager taskListNotificationManager;
	
	private List<ITaskEditorFactory> taskEditors = new ArrayList<ITaskEditorFactory>();

	private TaskListWriter taskListWriter;

	public static final String FILE_EXTENSION = ".xml";

	public static final String DEFAULT_TASK_LIST_FILE = "tasklist" + FILE_EXTENSION;

	public static final String TITLE_DIALOG = "Mylar Information";

	private ResourceBundle resourceBundle;

	private long AUTOMATIC_BACKUP_SAVE_INTERVAL = 1 * 3600 * 1000; // every

	private static Date lastBackup = new Date();

	private ITaskHighlighter highlighter;

	private static boolean shellActive = true;
	
	private boolean initialized = false;
	
	private Map<AbstractRepositoryConnector, Image> brandingIcons = new HashMap<AbstractRepositoryConnector, Image>();
	
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

		public void taskActivated(ITask task) {
			MylarPlugin.getContextManager().contextActivated(task.getHandleIdentifier());
		}

		public void tasksActivated(List<ITask> tasks) {
			for (ITask task : tasks) {
				taskActivated(task);
			}
		}

		public void taskDeactivated(ITask task) {
			MylarPlugin.getContextManager().contextDeactivated(task.getHandleIdentifier());
		}

		public void tasklistRead() {
			// ignore
		}

		public void localInfoChanged(ITask task) {
			// ignore
		}

		public void repositoryInfoChanged(ITask task) {
			// ignore
		}
		
		public void taskListModified() {
			// ignore
		}
	};

	/**
	 * TODO: move into reminder mechanims
	 */
	private static ShellListener SHELL_LISTENER = new ShellListener() {

		public void shellClosed(ShellEvent arg0) {
			// ignore
		}

		/**
		 * bug 1002249: too slow to save state here
		 */
		public void shellDeactivated(ShellEvent arg0) {
			shellActive = false;
		}

		public void shellActivated(ShellEvent arg0) {
			getDefault().checkTaskListBackup();
			shellActive = true;
		}

		public void shellDeiconified(ShellEvent arg0) {
			// ingore
		}

		public void shellIconified(ShellEvent arg0) {
			// ignore
		}
	};
	
	private static ITaskListNotificationProvider NOTIFICATION_PROVIDER = new ITaskListNotificationProvider() {

		public Set<ITaskListNotification> getNotifications() {
			Date currentDate = new Date();
			Set<ITask> allTasks = MylarTaskListPlugin.getTaskListManager().getTaskList().getAllTasks();
			Set<ITaskListNotification> reminders = new HashSet<ITaskListNotification>();
			for (ITask task : allTasks) {
				if (task.getReminderDate() != null && !task.hasBeenReminded() && task.getReminderDate().compareTo(currentDate) < 0) {			
					reminders.add(new TaskListNotificationReminder(task));
				}	
			}
			return reminders;
		} };

	private final IPropertyChangeListener PREFERENCE_LISTENER = new IPropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals(TaskListPreferenceConstants.MULTIPLE_ACTIVE_TASKS)) {
				TaskListView.getDefault().togglePreviousAction(
						!getPrefs().getBoolean(TaskListPreferenceConstants.MULTIPLE_ACTIVE_TASKS));
				TaskListView.getDefault().toggleNextAction(
						!getPrefs().getBoolean(TaskListPreferenceConstants.MULTIPLE_ACTIVE_TASKS));
				TaskListView.getDefault().clearTaskHistory();
			}
			if (event.getProperty().equals(MylarPreferenceContstants.PREF_DATA_DIR)) {
				if (event.getOldValue() instanceof String) {
					String newDirPath = MylarPlugin.getDefault().getDataDirectory();
					String taskListFilePath = newDirPath + File.separator + DEFAULT_TASK_LIST_FILE;

					getTaskListSaveManager().saveTaskListAndContexts();
					getTaskListManager().setTaskListFile(new File(taskListFilePath));
					getTaskListManager().createNewTaskList();
					getTaskListManager().readExistingOrCreateNewList();

					if (TaskListView.getDefault() != null)
						TaskListView.getDefault().clearTaskHistory();
				}
			}
		}
	};

	public MylarTaskListPlugin() {
		super();
		INSTANCE = this;

		try {
			initializeDefaultPreferences(getPrefs());
			taskListWriter = new TaskListWriter();

			String path = MylarPlugin.getDefault().getDataDirectory() + File.separator + DEFAULT_TASK_LIST_FILE;
			File taskListFile = new File(path);

			// TODO: decouple from core
			int nextTaskId = 1;
			if (MylarPlugin.getDefault() != null
					&& MylarPlugin.getDefault().getPreferenceStore().contains(TaskListPreferenceConstants.TASK_ID)) {
				nextTaskId = MylarPlugin.getDefault().getPreferenceStore().getInt(TaskListPreferenceConstants.TASK_ID);
			}

			taskListManager = new TaskListManager(taskListWriter, taskListFile, nextTaskId);
			taskRepositoryManager = new TaskRepositoryManager();
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Mylar Task List initialization failed", false);
		} 
	}

	/**
	 * Startup order is critical
	 */
	public void earlyStartup() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					TaskListExtensionReader.initExtensions(taskListWriter);
					taskRepositoryManager.readRepositories();

					taskListManager.addListener(CONTEXT_TASK_ACTIVITY_LISTENER);
					taskListManager.readExistingOrCreateNewList();
					initialized = true;
					migrateHandlesToRepositorySupport();

					if (getPrefs().getBoolean(TaskListPreferenceConstants.REPOSITORY_SYNCH_ON_STARTUP)) {
						for (AbstractRepositoryConnector repositoryClient : taskRepositoryManager.getRepositoryClients()) {
							repositoryClient.synchronize();
						}
					}

					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().addShellListener(SHELL_LISTENER);
					
					taskListNotificationManager = new TaskListNotificationManager();
					taskListNotificationManager.addNotificationProvider(NOTIFICATION_PROVIDER);
					taskListNotificationManager.startNotification(5000);	
					
					taskListRefreshManager = new TaskListRefreshManager();
					taskListRefreshManager.startRefreshJob();	
					
					taskListSaveManager = new TaskListSaveManager();
					taskListManager.addListener(taskListSaveManager); 
					
					MylarPlugin.getDefault().getPluginPreferences().addPropertyChangeListener(PREFERENCE_LISTENER);					
					getPrefs().addPropertyChangeListener(taskListRefreshManager);
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().addDisposeListener(
							taskListSaveManager);
				} catch (Exception e) {
					MylarStatusHandler.fail(e, "Task List initialization failed", true);
				}
			}
		});
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		INSTANCE = null;
		resourceBundle = null;
		try {
			taskListManager.removeListener(taskListSaveManager);
			if (MylarPlugin.getDefault() != null) {
				MylarPlugin.getDefault().getPluginPreferences().removePropertyChangeListener(PREFERENCE_LISTENER);
			}
			if (PlatformUI.getWorkbench() != null && PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null && PlatformUI.getWorkbench().isClosing()) {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().removeShellListener(SHELL_LISTENER);
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().removeDisposeListener(
						taskListSaveManager);
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Mylar Task List stop failed", false);
		}
	}

	private void migrateHandlesToRepositorySupport() {
		boolean migrated = false;
		getPreferenceStore().setDefault(TaskListPreferenceConstants.CONTEXTS_MIGRATED, false);
		if (!getPreferenceStore().getBoolean(TaskListPreferenceConstants.CONTEXTS_MIGRATED)) {
			File dataDir = new File(MylarPlugin.getDefault().getDataDirectory());
			TaskRepository defaultRepository = MylarTaskListPlugin.getRepositoryManager().getDefaultRepository(
					TaskRepositoryManager.PREFIX_REPOSITORY_OLD.toLowerCase());
			if (defaultRepository != null) {
				String repositoryUrl = defaultRepository.getUrl().toExternalForm();
				migrated = true;
				if (dataDir.exists() && dataDir.isDirectory()) {
					for (File file : dataDir.listFiles()) {
						String oldHandle = file.getName().substring(0, file.getName().lastIndexOf('.'));
						if (oldHandle.startsWith(TaskRepositoryManager.PREFIX_REPOSITORY_OLD)) {
							String id = AbstractRepositoryTask.getTaskId(oldHandle);
							String newHandle = AbstractRepositoryTask.getHandle(repositoryUrl, id);
							File newFile = MylarPlugin.getContextManager().getFileForContext(newHandle);
							file.renameTo(newFile);
						}
					}
				}
				for (ITask task : taskListManager.getTaskList().getAllTasks()) {
					if (!task.isLocal()) {
						String id = AbstractRepositoryTask.getTaskId(task.getHandleIdentifier());
						String newHandle = AbstractRepositoryTask.getHandle(repositoryUrl, id);
						task.setHandleIdentifier(newHandle);
					}
				}

				for (AbstractRepositoryQuery query : taskListManager.getTaskList().getQueries()) {
					query.setRepositoryUrl(repositoryUrl);
					for (AbstractQueryHit hit : query.getHits()) {
						hit.setRepositoryUrl(repositoryUrl);
					}
				}
				taskListManager.saveTaskList();
				taskListManager.readExistingOrCreateNewList();
			}
		}
		if (migrated) {
			MylarStatusHandler.log("Migrated context files to repository-aware paths", this);
			getPreferenceStore().setValue(TaskListPreferenceConstants.CONTEXTS_MIGRATED, true);
		}
	}
	
	@Override
	protected void initializeDefaultPreferences(IPreferenceStore store) {
//		store.setDefault(TaskListPreferenceConstants.AUTO_MANAGE_EDITORS, true);
		store.setDefault(TaskListPreferenceConstants.SELECTED_PRIORITY, Task.PriorityLevel.P5.toString());
		store.setDefault(TaskListPreferenceConstants.REPORT_OPEN_EDITOR, true);
		store.setDefault(TaskListPreferenceConstants.REPORT_OPEN_INTERNAL, false);
		store.setDefault(TaskListPreferenceConstants.REPORT_OPEN_EXTERNAL, false);
		store.setDefault(TaskListPreferenceConstants.REPOSITORY_SYNCH_ON_STARTUP, false);
		
		store.setDefault(TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED, false);
		store.setDefault(TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS, ""+(30 * 60 * 1000));

		store.setDefault(TaskListPreferenceConstants.MULTIPLE_ACTIVE_TASKS, false);
		store.setValue(TaskListPreferenceConstants.MULTIPLE_ACTIVE_TASKS, false);

		store.setDefault(TaskListPreferenceConstants.SAVE_TASKLIST_MODE, TaskListSaveMode.THREE_HOURS.toString());
	}

	public static TaskListManager getTaskListManager() {
		return taskListManager;
	}

	/**
	 * Returns the shared instance.
	 */
	public static MylarTaskListPlugin getDefault() {
		return INSTANCE;
	}

	/**
	 * Returns the string from the INSTANCE's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = MylarTaskListPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the INSTANCE's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle("taskListPlugin.TaskListPluginPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}

	public static IPreferenceStore getPrefs() {
		// TODO: should be using the task list's prefernece store, but can't
		// change without migrating because this will cause people to lose
		// repositories
		return MylarPlugin.getDefault().getPreferenceStore();
	}

	private void checkTaskListBackup() {
		// if (getPrefs().contains(PREVIOUS_SAVE_DATE)) {
		// lastSave = new Date(getPrefs().getLong(PREVIOUS_SAVE_DATE));
		// } else {
		// lastSave = new Date();
		// getPrefs().setValue(PREVIOUS_SAVE_DATE, lastSave.getTime());
		// }
		Date currentTime = new Date();
		if (currentTime.getTime() > lastBackup.getTime() + AUTOMATIC_BACKUP_SAVE_INTERVAL) {// TaskListSaveMode.fromStringToLong(getPrefs().getString(SAVE_TASKLIST_MODE)))
			// {
			MylarTaskListPlugin.getDefault().getTaskListSaveManager().createTaskListBackupFile();
			lastBackup = new Date();
			// INSTANCE.getPreferenceStore().setValue(PREVIOUS_SAVE_DATE,
			// lastSave.getTime());
		}
	}

	/**
	 * TODO: move
	 */
	public static void setCurrentPriorityLevel(Task.PriorityLevel pl) {
		getPrefs().setValue(TaskListPreferenceConstants.SELECTED_PRIORITY, pl.toString());
	}

	public static String getCurrentPriorityLevel() {
		if (getPrefs().contains(TaskListPreferenceConstants.SELECTED_PRIORITY)) {
			return getPrefs().getString(TaskListPreferenceConstants.SELECTED_PRIORITY);
		} else {
			return Task.PriorityLevel.P5.toString();
		}
	}

	public void setFilterCompleteMode(boolean isFilterOn) {
		getPrefs().setValue(TaskListPreferenceConstants.FILTER_COMPLETE_MODE, isFilterOn);
	}

	public boolean isFilterCompleteMode() {
		if (getPrefs().contains(TaskListPreferenceConstants.FILTER_COMPLETE_MODE)) {
			return getPrefs().getBoolean(TaskListPreferenceConstants.FILTER_COMPLETE_MODE);
		} else {
			return false;
		}
	}

	public void setFilterInCompleteMode(boolean isFilterOn) {
		getPrefs().setValue(TaskListPreferenceConstants.FILTER_INCOMPLETE_MODE, isFilterOn);
	}

	public boolean isFilterInCompleteMode() {
		if (getPrefs().contains(TaskListPreferenceConstants.FILTER_INCOMPLETE_MODE)) {
			return getPrefs().getBoolean(TaskListPreferenceConstants.FILTER_INCOMPLETE_MODE);
		} else {
			return false;
		}
	}

	// public List<ITaskHandler> getTaskHandlers() {
	// return taskHandlers;
	// }

	// public ITaskHandler getHandlerForElement(ITaskListElement element) {
	// for (ITaskHandler taskHandler : taskHandlers) {
	// if (taskHandler.acceptsItem(element))
	// return taskHandler;
	// }
	// return null;
	// }

	// public void addTaskHandler(ITaskHandler taskHandler) {
	// taskHandlers.add(taskHandler);
	// }

	// private void restoreTaskHandlerState() {
	// for (ITaskHandler handler : taskHandlers) {
	// handler.restoreState(TaskListView.getDefault());
	// }
	// }

	private List<IDynamicSubMenuContributor> menuContributors = new ArrayList<IDynamicSubMenuContributor>();

	public List<IDynamicSubMenuContributor> getDynamicMenuContributers() {
		return menuContributors;
	}

	public void addDynamicPopupContributor(IDynamicSubMenuContributor contributor) {
		menuContributors.add(contributor);
	}

	public boolean isMultipleActiveTasksMode() {
		return getPrefs().getBoolean(TaskListPreferenceConstants.MULTIPLE_ACTIVE_TASKS);
	}

	public String[] getSaveOptions() {
		String[] options = { TaskListSaveMode.ONE_HOUR.toString(), TaskListSaveMode.THREE_HOURS.toString(),
				TaskListSaveMode.DAY.toString() };
		return options;
	}

	public ITaskHighlighter getHighlighter() {
		return highlighter;
	}

	public void setHighlighter(ITaskHighlighter highlighter) {
		this.highlighter = highlighter;
	}

	public List<ITaskEditorFactory> getTaskEditors() {
		return taskEditors;
	}

	public void addContextEditor(ITaskEditorFactory contextEditor) {
		if (contextEditor != null)
			this.taskEditors.add(contextEditor);
	}

	public TaskListSaveManager getTaskListSaveManager() {
		return taskListSaveManager;
	}

	public boolean isShellActive() {
		return MylarTaskListPlugin.shellActive;
	}

	public static TaskRepositoryManager getRepositoryManager() {
		return taskRepositoryManager;
	}

	public Map<AbstractRepositoryConnector, Image> getBrandingIcons() {
		return brandingIcons;
	}

	public boolean isInitialized() {
		return initialized;
	}
} 

// /**
// * Sets the directory containing the task list file to use.
// * Switches immediately to use the data at that location.
// */
// public void setDataDirectory(String newDirPath) {
// String taskListFilePath = newDirPath + File.separator +
// DEFAULT_TASK_LIST_FILE;
// getTaskListManager().setTaskListFile(new File(taskListFilePath));
// getTaskListManager().createNewTaskList();
// getTaskListManager().readTaskList();
//
// if (TaskListView.getDefault() != null)
// TaskListView.getDefault().clearTaskHistory();
// }

// private List<ITaskActivationListener> taskListListeners = new
// ArrayList<ITaskActivationListener>();
//
// public List<ITaskActivationListener> getTaskListListeners() {
// return taskListListeners;
// }
//
// public void addTaskListListener(ITaskActivationListener taskListListner) {
// taskListListeners.add(taskListListner);
// }
