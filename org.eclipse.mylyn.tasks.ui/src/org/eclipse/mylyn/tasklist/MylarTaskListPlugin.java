/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.tasklist;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.MylarPrefContstants;
import org.eclipse.mylar.core.util.MylarStatusHandler;
import org.eclipse.mylar.tasklist.internal.TaskListExtensionReader;
import org.eclipse.mylar.tasklist.internal.TaskListManager;
import org.eclipse.mylar.tasklist.internal.TaskListSaveManager;
import org.eclipse.mylar.tasklist.internal.TaskListWriter;
import org.eclipse.mylar.tasklist.internal.planner.ReminderRequiredCollector;
import org.eclipse.mylar.tasklist.internal.planner.TaskReportGenerator;
import org.eclipse.mylar.tasklist.repositories.TaskRepositoryManager;
import org.eclipse.mylar.tasklist.ui.IContextEditorFactory;
import org.eclipse.mylar.tasklist.ui.IDynamicSubMenuContributor;
import org.eclipse.mylar.tasklist.ui.ITaskHighlighter;
import org.eclipse.mylar.tasklist.ui.ITaskListElement;
import org.eclipse.mylar.tasklist.ui.TasksReminderDialog;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 * 
 * TODO: this class is in serious need of refactoring
 */
public class MylarTaskListPlugin extends AbstractUIPlugin implements IStartup {

	public static final String PLUGIN_ID = "org.eclipse.mylar.tasklist";
	
	private static MylarTaskListPlugin INSTANCE;

	private static TaskListManager taskListManager;
	
	private static TaskRepositoryManager taskRepositoryManager;

	private TaskListSaveManager taskListSaveManager = new TaskListSaveManager();
		
	private List<ITaskHandler> taskHandlers = new ArrayList<ITaskHandler>(); // TODO: use extension points

	private List<IContextEditorFactory> contextEditors = new ArrayList<IContextEditorFactory>();

	private TaskListWriter taskListWriter;
	
	public static final String FILE_EXTENSION = ".xml";

	public static final String DEFAULT_TASK_LIST_FILE = "tasklist" + FILE_EXTENSION;

	private ResourceBundle resourceBundle;

	private long AUTOMATIC_BACKUP_SAVE_INTERVAL = 1 * 3600 * 1000; // every hour

	private static Date lastBackup = new Date();

	private ITaskHighlighter highlighter;
	
	private static boolean shellActive = true;

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

	public enum PriorityLevel {
		P1, P2, P3, P4, P5;

		@Override
		public String toString() {
			switch (this) {
			case P1:
				return "P1";
			case P2:
				return "P2";
			case P3:
				return "P3";
			case P4:
				return "P4";
			case P5:
				return "P5";
			default:
				return "P5";
			}
		}

		public static PriorityLevel fromString(String string) {
			if (string == null)
				return null;
			if (string.equals("P1"))
				return P1;
			if (string.equals("P2"))
				return P2;
			if (string.equals("P3"))
				return P3;
			if (string.equals("P4"))
				return P4;
			if (string.equals("P5"))
				return P5;
			return null;
		}
	}

	private static ITaskActivityListener CONTEXT_TASK_ACTIVITY_LISTENER = new ITaskActivityListener() {

		public void taskActivated(ITask task) {
			MylarPlugin.getContextManager().contextActivated(task.getHandleIdentifier());
		}

		public void tasksActivated(List<ITask> tasks) {
			for (ITask task : tasks) {
				MylarPlugin.getContextManager().contextActivated(task.getHandleIdentifier());
			}
		}

		public void taskDeactivated(ITask task) {
			MylarPlugin.getContextManager().contextDeactivated(task.getHandleIdentifier());
		}

		public void tasklistRead() {
			// ignore
		}

		public void taskChanged(ITask task) {
			// TODO Auto-generated method stub
			
		}

		public void tasklistModified() {
			// TODO Auto-generated method stub
			
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
			getDefault().checkReminders();
			shellActive = true;
		}

		public void shellDeiconified(ShellEvent arg0) {
			// ingore
		}

		public void shellIconified(ShellEvent arg0) {
			// ignore
		}
	};

	private final IPropertyChangeListener PREFERENCE_LISTENER = new IPropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals(MylarTaskListPrefConstants.MULTIPLE_ACTIVE_TASKS)) {
				TaskListView.getDefault().togglePreviousAction(!getPrefs().getBoolean(MylarTaskListPrefConstants.MULTIPLE_ACTIVE_TASKS));
				TaskListView.getDefault().toggleNextAction(!getPrefs().getBoolean(MylarTaskListPrefConstants.MULTIPLE_ACTIVE_TASKS));
				TaskListView.getDefault().clearTaskHistory();
			}
            if (event.getProperty().equals(MylarPrefContstants.PREF_DATA_DIR)) {                
                if (event.getOldValue() instanceof String) {
                	String newDirPath = MylarPlugin.getDefault().getDataDirectory();
            		String taskListFilePath = newDirPath + File.separator + DEFAULT_TASK_LIST_FILE;
            		
            		getTaskListSaveManager().saveTaskListAndContexts();
            		getTaskListManager().setTaskListFile(new File(taskListFilePath));
            		getTaskListManager().createNewTaskList();
            		getTaskListManager().readOrCreateTaskList();

            		if (TaskListView.getDefault() != null) TaskListView.getDefault().clearTaskHistory();
                }
            } 
		}
	};

	public MylarTaskListPlugin() {
		super();
		INSTANCE = this;
//		List<ITaskListExternalizer> externalizers = new ArrayList<ITaskListExternalizer>();
		
		try {
			initializeDefaultPreferences(getPrefs());
			 
			taskListWriter = new TaskListWriter();
			
			String path = MylarPlugin.getDefault().getDataDirectory() + File.separator + DEFAULT_TASK_LIST_FILE;
			File taskListFile = new File(path);
			
			// TODO: decouple from core
			int nextTaskId = 1;
			if (MylarPlugin.getDefault() != null && MylarPlugin.getDefault().getPreferenceStore().contains(MylarTaskListPrefConstants.TASK_ID)) { // TODO: fix to MylarTaskListPlugin
				nextTaskId = MylarPlugin.getDefault().getPreferenceStore().getInt(MylarTaskListPrefConstants.TASK_ID);
			} 

			taskListManager = new TaskListManager(taskListWriter, taskListFile, nextTaskId);	
			taskRepositoryManager = new TaskRepositoryManager();
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Mylar Task List initialization failed", false);
		}
	}

	public void earlyStartup() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					TaskListExtensionReader.initExtensions(taskListWriter);
					
					taskListManager.addListener(CONTEXT_TASK_ACTIVITY_LISTENER);
					taskListManager.addListener(taskListSaveManager);
					
					Workbench.getInstance().getActiveWorkbenchWindow().getShell().addShellListener(SHELL_LISTENER);
					MylarPlugin.getDefault().getPluginPreferences().addPropertyChangeListener(PREFERENCE_LISTENER);
					Workbench.getInstance().getActiveWorkbenchWindow().getShell().addDisposeListener(taskListSaveManager);
										
					restoreTaskHandlerState();
					taskListManager.readOrCreateTaskList();
					restoreTaskHandlerState();
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
			if (Workbench.getInstance() != null && Workbench.getInstance().getActiveWorkbenchWindow() != null) {
				Workbench.getInstance().getActiveWorkbenchWindow().getShell().removeShellListener(SHELL_LISTENER);
				Workbench.getInstance().getActiveWorkbenchWindow().getShell().removeDisposeListener(taskListSaveManager);
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Mylar Java stop failed", false);
		}
	}

	@Override
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		store.setDefault(MylarTaskListPrefConstants.AUTO_MANAGE_EDITORS, true);
		store.setDefault(MylarTaskListPrefConstants.SELECTED_PRIORITY, "P5");
		store.setDefault(MylarTaskListPrefConstants.REPORT_OPEN_EDITOR, true);
		store.setDefault(MylarTaskListPrefConstants.REPORT_OPEN_INTERNAL, false);
		store.setDefault(MylarTaskListPrefConstants.REPORT_OPEN_EXTERNAL, false);
		store.setDefault(MylarTaskListPrefConstants.MULTIPLE_ACTIVE_TASKS, false);

		store.setDefault(MylarTaskListPrefConstants.SAVE_TASKLIST_MODE, TaskListSaveMode.THREE_HOURS.toString());
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
	 * Returns the string from the INSTANCE's resource bundle,
	 * or 'key' if not found.
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
		return MylarPlugin.getDefault().getPreferenceStore();
	}

//	/**
//	 * Sets the directory containing the task list file to use.
//	 * Switches immediately to use the data at that location.
//	 */
//	public void setDataDirectory(String newDirPath) {
//		String taskListFilePath = newDirPath + File.separator + DEFAULT_TASK_LIST_FILE;
//		getTaskListManager().setTaskListFile(new File(taskListFilePath));
//		getTaskListManager().createNewTaskList();
//		getTaskListManager().readTaskList();
//
//		if (TaskListView.getDefault() != null) TaskListView.getDefault().clearTaskHistory();
//	}

	private void checkTaskListBackup() {
		//    	if (getPrefs().contains(PREVIOUS_SAVE_DATE)) {
		//			lastSave = new Date(getPrefs().getLong(PREVIOUS_SAVE_DATE));
		//		} else {
		//			lastSave = new Date();
		//			getPrefs().setValue(PREVIOUS_SAVE_DATE, lastSave.getTime());
		//		}
		Date currentTime = new Date();
		if (currentTime.getTime() > lastBackup.getTime() + AUTOMATIC_BACKUP_SAVE_INTERVAL) {//TaskListSaveMode.fromStringToLong(getPrefs().getString(SAVE_TASKLIST_MODE))) {
			MylarTaskListPlugin.getDefault().getTaskListSaveManager().createTaskListBackupFile();
			lastBackup = new Date();
			//			INSTANCE.getPreferenceStore().setValue(PREVIOUS_SAVE_DATE, lastSave.getTime());
		}
	}

	private void checkReminders() {
		final TaskReportGenerator parser = new TaskReportGenerator(MylarTaskListPlugin.getTaskListManager().getTaskList());
		parser.addCollector(new ReminderRequiredCollector());
		parser.collectTasks();
		if (!parser.getAllCollectedTasks().isEmpty()) {
			Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
				public void run() {
					TasksReminderDialog dialog = new TasksReminderDialog(Workbench.getInstance().getDisplay().getActiveShell(), parser.getAllCollectedTasks());
					dialog.setBlockOnOpen(false);
					dialog.open();
				}
			});
		}
	}

	public static void setPriorityLevel(PriorityLevel pl) {
		getPrefs().setValue(MylarTaskListPrefConstants.SELECTED_PRIORITY, pl.toString());
	}

	public static String getPriorityLevel() {
		if (getPrefs().contains(MylarTaskListPrefConstants.SELECTED_PRIORITY)) {
			return getPrefs().getString(MylarTaskListPrefConstants.SELECTED_PRIORITY);
		} else {
			return PriorityLevel.P5.toString();
		}
	}

	public void setFilterCompleteMode(boolean isFilterOn) {
		getPrefs().setValue(MylarTaskListPrefConstants.FILTER_COMPLETE_MODE, isFilterOn);
	}

	public boolean isFilterCompleteMode() {
		if (getPrefs().contains(MylarTaskListPrefConstants.FILTER_COMPLETE_MODE)) {
			return getPrefs().getBoolean(MylarTaskListPrefConstants.FILTER_COMPLETE_MODE);
		} else {
			return false;
		}
	}

	public void setFilterInCompleteMode(boolean isFilterOn) {
		getPrefs().setValue(MylarTaskListPrefConstants.FILTER_INCOMPLETE_MODE, isFilterOn);
	}

	public boolean isFilterInCompleteMode() {
		if (getPrefs().contains(MylarTaskListPrefConstants.FILTER_INCOMPLETE_MODE)) {
			return getPrefs().getBoolean(MylarTaskListPrefConstants.FILTER_INCOMPLETE_MODE);
		} else {
			return false;
		}
	}

	/**
	 * TODO: remove
	 */
	public ReportOpenMode getReportMode() {
		return ReportOpenMode.EDITOR;
		//		if (getPrefs().getBoolean(REPORT_OPEN_EDITOR)) {
		//			return ReportOpenMode.EDITOR;
		//		} else if (getPrefs().getBoolean(REPORT_OPEN_INTERNAL)) {
		//			return ReportOpenMode.INTERNAL_BROWSER;
		//		} else {
		//			return ReportOpenMode.EXTERNAL_BROWSER;
		//		} 
	}

//	public TaskListWriter getTaskListExternalizer() {
//		return externalizer;
//	}

	public List<ITaskHandler> getTaskHandlers() {
		return taskHandlers;
	}

	public ITaskHandler getHandlerForElement(ITaskListElement element) {
		for (ITaskHandler taskHandler : taskHandlers) {
			if (taskHandler.acceptsItem(element))
				return taskHandler;
		}
		return null;
	}

	public void addTaskHandler(ITaskHandler taskHandler) {
		taskHandlers.add(taskHandler);
	}

	private void restoreTaskHandlerState() {
		for (ITaskHandler handler : taskHandlers) {
			handler.restoreState(TaskListView.getDefault());
		}
	}

	private List<IDynamicSubMenuContributor> menuContributors = new ArrayList<IDynamicSubMenuContributor>();

	public List<IDynamicSubMenuContributor> getDynamicMenuContributers() {
		return menuContributors;
	}

	public void addDynamicPopupContributor(IDynamicSubMenuContributor contributor) {
		menuContributors.add(contributor);
	}

	public boolean isMultipleActiveTasksMode() {
		return getPrefs().getBoolean(MylarTaskListPrefConstants.MULTIPLE_ACTIVE_TASKS);
	}

	public String[] getSaveOptions() {
		String[] options = { TaskListSaveMode.ONE_HOUR.toString(), TaskListSaveMode.THREE_HOURS.toString(), TaskListSaveMode.DAY.toString() };
		return options;
	}

	public ITaskHighlighter getHighlighter() {
		return highlighter;
	}

	public void setHighlighter(ITaskHighlighter highlighter) {
		this.highlighter = highlighter;
	}

	public List<IContextEditorFactory> getContextEditors() {
		return contextEditors;
	}

	public void addContextEditor(IContextEditorFactory contextEditor) {
		if (contextEditor != null)
			this.contextEditors.add(contextEditor);
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
}

//private List<ITaskActivationListener> taskListListeners = new ArrayList<ITaskActivationListener>();
//
//	public List<ITaskActivationListener> getTaskListListeners() {
//		return taskListListeners;
//	}
//
//	public void addTaskListListener(ITaskActivationListener taskListListner) {
//		taskListListeners.add(taskListListner);
//	}