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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.internal.TaskListExternalizer;
import org.eclipse.mylar.tasklist.report.internal.ReminderRequiredCollector;
import org.eclipse.mylar.tasklist.report.internal.TaskReportGenerator;
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
 */
public class MylarTasklistPlugin extends AbstractUIPlugin implements IStartup {
    
    private static MylarTasklistPlugin plugin;
    private static TaskListManager taskListManager;
    private TaskListExternalizer externalizer;
    private List<ITaskHandler> taskHandlers = new ArrayList<ITaskHandler>(); // TODO: use extension points
    
    public static final String TASK_CONTRIBUTOR_EXTENSION_POINT_ID = "org.eclipse.mylar.tasklist.taskListContributor";

    public static final String PLANNING_GAME_WIZARD_ID = "org.eclipse.mylar.tasklist.report.ui.planningGameWizard";
    public static final String PLANNING_GAME_EDITOR_ID = "org.eclipse.mylar.tasklist.report.ui.planning";
	public static final String TASK_HANDLER_ELEMENT = "taskHandler";
	public static final String EXTERNALIZER_CLASS_ID = "externalizerClass";
	public static final String ACTION_CONTRIBUTOR_CLASS_ID = "taskHandlerClass";
	public static final String TASK_LISTENER_ELEMENT = "taskListener";
	public static final String TASK_LISTENER_CLASS_ID = "class";
	public static final String DYNAMIC_POPUP_ELEMENT = "dynamicPopupMenu";
	public static final String DYNAMIC_POPUP_CLASS_ID = "class";
    		
    public static final String REPORT_OPEN_EDITOR = "org.eclipse.mylar.tasklist.report.open.editor";
    public static final String REPORT_OPEN_INTERNAL = "org.eclipse.mylar.tasklist.report.open.internal";
    public static final String REPORT_OPEN_EXTERNAL = "org.eclipse.mylar.tasklist.report.open.external";
    public static final String MULTIPLE_ACTIVE_TASKS = "org.eclipse.mylar.tasklist.active.multipe";
    
    public static final String FILE_EXTENSION = ".xml";
    public static final String TASK_ID = "org.eclipse.mylar.tasklist.userid";
    public static final String DEFAULT_TASK_LIST_FILE = "tasklist" + FILE_EXTENSION;
    public static final String TASK_EDITOR_ID = "org.eclipse.mylar.tasklist.ui.taskEditor";
    public static final String SELECTED_PRIORITY = "org.eclipse.mylar.tasklist.filter.priority";
    public static final String FILTER_COMPLETE_MODE = "org.eclipse.mylar.tasklist.filter.complete";
    public static final String FILTER_INCOMPLETE_MODE = "org.eclipse.mylar.tasklist.filter.incomplete";
    public static final String SAVE_TASKLIST_MODE = "org.eclipse.mylar.tasklist.save.mode";
    public static final String PREVIOUS_SAVE_DATE = "org.eclipse.mylar.tasklist.save.last";
//    public static final String REMINDER_CHECK = "org.eclipse.mylar.tasklist.reminder.check";
    
	private ResourceBundle resourceBundle;
	private static Date lastSave = null;
//	private ITaskListActionContributor primaryContributor;
	public enum TaskListSaveMode {
		ONE_HOUR,
		THREE_HOURS,
		DAY;
		 @Override
        public String toString() {
            switch(this) {
                case ONE_HOUR: return "1 hour";
                case THREE_HOURS: return "3 hours";
                case DAY: return "1 day";
                default: return "3 hours";
            }
        }
        public static TaskListSaveMode fromString(String string) {
            if (string == null) return null;
            if (string.equals("1 hour")) return ONE_HOUR;
            if (string.equals("3 hours")) return THREE_HOURS;
            if (string.equals("1 day")) return DAY;
            return null;
        }
        public static long fromStringToLong(String string) {
        	long hour = 3600*1000;
        	switch(fromString(string)) {
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
		EDITOR,
		INTERNAL_BROWSER,
		EXTERNAL_BROWSER;
	}
	
	public enum PriorityLevel {
        P1,
        P2,
        P3,
        P4,
        P5;               
        
        @Override
        public String toString() {
            switch(this) {
                case P1: return "P1";
                case P2: return "P2";
                case P3: return "P3";
                case P4: return "P4";
                case P5: return "P5";
                default: return "P5";
            }
        }
        public static PriorityLevel fromString(String string) {
            if (string == null) return null;
            if (string.equals("P1")) return P1;
            if (string.equals("P2")) return P2;
            if (string.equals("P3")) return P3;
            if (string.equals("P4")) return P4;
            if (string.equals("P5")) return P5;
            return null;
        }
    }
	
    private static ITaskActivityListener CONTEXT_MANAGER_TASK_LISTENER = new ITaskActivityListener() {

        public void taskActivated(ITask task) {
            MylarPlugin.getContextManager().contextActivated(task.getHandle(), task.getPath());
        }

        public void tasksActivated(List<ITask> tasks) {
            for (ITask task : tasks) {
                MylarPlugin.getContextManager().contextActivated(task.getHandle(), task.getPath());
            }
        }

        public void taskDeactivated(ITask task) {
            MylarPlugin.getContextManager().contextDeactivated(task.getHandle(), task.getPath());
        }

		public void taskPropertyChanged(ITask updatedTask, String property) {
			// don't care about property change	
		}
        
    };
    
    private static ShellListener SHELL_LISTENER = new ShellListener() {
        private void saveState() {
            taskListManager.saveTaskList();
            for(ITask task : taskListManager.getTaskList().getActiveTasks()) {
                MylarPlugin.getContextManager().saveContext(task.getHandle(), task.getPath());
            }
            lastSave = new Date();
			plugin.getPreferenceStore().setValue(PREVIOUS_SAVE_DATE, lastSave.getTime());
        }
        
        private void checkTaskListSave() {
        	if (getPrefs().contains(PREVIOUS_SAVE_DATE)) {
    			lastSave = new Date(getPrefs().getLong(PREVIOUS_SAVE_DATE));
    		} else {
    			lastSave = new Date();
    			getPrefs().setValue(PREVIOUS_SAVE_DATE, lastSave.getTime());
    		}
        	Date currentTime = new Date();        	
        	if (currentTime.getTime() > lastSave.getTime() + TaskListSaveMode.fromStringToLong(getPrefs().getString(SAVE_TASKLIST_MODE))) {
        		taskListManager.saveTaskList();
        		lastSave = new Date();
    			plugin.getPreferenceStore().setValue(PREVIOUS_SAVE_DATE, lastSave.getTime());
        	} else {        		
//        		System.out.println("Tasklist not saved auto yet");
//        		System.out.println("Save time: " + new Date(
//        				lastSave.getTime() + 
//        				TaskListSaveMode.fromStringToLong(getPrefs().getString(SAVE_TASKLIST_MODE))).toString());
        	}
        }
        
        private void checkReminders() {
//        	if (getPrefs().getBoolean(REMINDER_CHECK)) {
//        		getPrefs().setValue(REMINDER_CHECK, false);
        		final TaskReportGenerator parser = new TaskReportGenerator(MylarTasklistPlugin.getTaskListManager().getTaskList());
        		parser.addCollector(new ReminderRequiredCollector());
        		parser.checkTasks();
        		if (!parser.getTasks().isEmpty()) {
        			Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
        				public void run() {
        					TasksReminderDialog dialog = new TasksReminderDialog(Workbench.getInstance().getDisplay().getActiveShell(), parser.getTasks());
        					dialog.setBlockOnOpen(false);
        					dialog.open();
        				}
        			});
        		}
//        	}
        }
        
        public void shellClosed(ShellEvent arg0) {
            saveState();
        }  
        
        public void shellDeactivated(ShellEvent arg0) { 
        	// bug 1002249: too slow to save state here
        }
        public void shellActivated(ShellEvent arg0) { 
        	checkTaskListSave();
        	checkReminders();
        }
        
        public void shellDeiconified(ShellEvent arg0) { }
        
        public void shellIconified(ShellEvent arg0) { 
        	saveState();
        }
    };
    
    private static IPropertyChangeListener PREFERENCE_LISTENER = new IPropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent event) {
			// TODO Auto-generated method stub
			if (event.getProperty().equals(MylarPlugin.MYLAR_DIR)) {				
				if (event.getOldValue() instanceof String) {
					String prevDir = (String) event.getOldValue();				
					MylarPlugin.getContextManager().updateMylarDirContents(prevDir);
					getTaskListManager().updateTaskscapeReference(prevDir);
					
					String path = MylarPlugin.getDefault().getUserDataDirectory() + File.separator + DEFAULT_TASK_LIST_FILE;        
					getTaskListManager().setTaskListFile(new File(path));
				}
			} else if (event.getProperty().equals(MULTIPLE_ACTIVE_TASKS)) {
				TaskListView.getDefault().togglePreviousAction(!getPrefs().getBoolean(MULTIPLE_ACTIVE_TASKS));
				TaskListView.getDefault().toggleNextAction(!getPrefs().getBoolean(MULTIPLE_ACTIVE_TASKS));
				TaskListView.getDefault().clearTaskHistory();
			} else {
			}
		}        
    };
    
	public MylarTasklistPlugin() {
		super();
		plugin = this;
		initializeDefaultPreferences(getPrefs());
        externalizer = new TaskListExternalizer();  
    	
        String path = MylarPlugin.getDefault().getUserDataDirectory() + File.separator + DEFAULT_TASK_LIST_FILE;        
        File taskListFile = new File(path);
        taskListManager = new TaskListManager(taskListFile);
        taskListManager.addListener(CONTEXT_MANAGER_TASK_LISTENER);
	}

	public void earlyStartup() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
            	
        workbench.getDisplay().syncExec(new Runnable() {
            public void run() {
            	Workbench.getInstance().getActiveWorkbenchWindow().getShell().addShellListener(SHELL_LISTENER);
                MylarPlugin.getDefault().getPluginPreferences().addPropertyChangeListener(PREFERENCE_LISTENER);
            	
            	taskListManager.readTaskList();
                if (taskListManager.getTaskList() == null) taskListManager.createNewTaskList(); 
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
		plugin = null;
		resourceBundle = null;
		createFileBackup();
//		getPrefs().setValue(REMINDER_CHECK, true);
	}

    @Override
    protected void initializeDefaultPreferences(IPreferenceStore store) {
       	store.setDefault(MylarPlugin.TASKLIST_EDITORS_CLOSE, true);    	
    	store.setDefault(SELECTED_PRIORITY, "P5");
    	store.setDefault(REPORT_OPEN_EDITOR, true);
    	store.setDefault(REPORT_OPEN_INTERNAL, false);
    	store.setDefault(REPORT_OPEN_EXTERNAL, false);
    	store.setDefault(MULTIPLE_ACTIVE_TASKS, false);
    	store.setDefault(SAVE_TASKLIST_MODE, TaskListSaveMode.THREE_HOURS.toString());
//    	store.setDefault(REMINDER_CHECK, true);
    	
    }    
    
    public static TaskListManager getTaskListManager() {
        return taskListManager;
    }
    
	/**
	 * Returns the shared instance.
	 */
	public static MylarTasklistPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = MylarTasklistPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
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
	
	public static void setPriorityLevel(PriorityLevel pl) {
		getPrefs().setValue(SELECTED_PRIORITY, pl.toString());
	}
	public static String getPriorityLevel() {
		if(getPrefs().contains(SELECTED_PRIORITY)) {
			return getPrefs().getString(SELECTED_PRIORITY);
		} else {			
			return PriorityLevel.P5.toString();
		}		
	}
	
	public void setFilterCompleteMode(boolean isFilterOn) {
		getPrefs().setValue(FILTER_COMPLETE_MODE, isFilterOn);
	}
	
	public boolean isFilterCompleteMode() {
		if (getPrefs().contains(FILTER_COMPLETE_MODE)) {
			return getPrefs().getBoolean(FILTER_COMPLETE_MODE);
		} else {
			return false;
		}
	}
	
	public void setFilterInCompleteMode(boolean isFilterOn) {
		getPrefs().setValue(FILTER_INCOMPLETE_MODE, isFilterOn);
	}
	
	public boolean isFilterInCompleteMode() {
		if (getPrefs().contains(FILTER_INCOMPLETE_MODE)) {
			return getPrefs().getBoolean(FILTER_INCOMPLETE_MODE);
		} else {
			return false;
		}
	}
	
	public ReportOpenMode getReportMode() {
		if (getPrefs().getBoolean(REPORT_OPEN_EDITOR)) {
			return ReportOpenMode.EDITOR;
		} else if (getPrefs().getBoolean(REPORT_OPEN_INTERNAL)) {
			return ReportOpenMode.INTERNAL_BROWSER;
		} else {
			return ReportOpenMode.EXTERNAL_BROWSER;
		} 
	}

	public TaskListExternalizer getTaskListExternalizer() {
		return externalizer;
	}

	public List<ITaskHandler> getTaskHandlers() {
		return taskHandlers;
	}

	public ITaskHandler getTaskHandlerForElement(ITaskListElement element){
		for(ITaskHandler taskHandler: taskHandlers){
			if(taskHandler.acceptsItem(element)) return taskHandler;
		}
		return null;
	}
	
	public void addTaskHandler(ITaskHandler taskHandler) {
		taskHandlers.add(taskHandler);
	}
	
	public void restoreTaskHandlerState(){
		for(ITaskHandler handler: taskHandlers){
			handler.restoreState(TaskListView.getDefault());
		}
	}

	private List<ITaskListDynamicSubMenuContributor> menuContributors = new ArrayList<ITaskListDynamicSubMenuContributor>();
	
	public List<ITaskListDynamicSubMenuContributor> getDynamicMenuContributers() {
		return menuContributors;
	}
	
	public void addDynamicPopupContributor(ITaskListDynamicSubMenuContributor contributor) {
		menuContributors.add(contributor);
	}
	
	private List<ITaskListener> taskListListeners = new ArrayList<ITaskListener>();
	
	public List<ITaskListener> getTaskListListeners() {
		return taskListListeners;
	}
	
	public void addTaskListListener(ITaskListener taskListListner) {
		taskListListeners.add(taskListListner);
	}
		
	private void createFileBackup() {
		String path = MylarPlugin.getDefault().getUserDataDirectory() + File.separator + DEFAULT_TASK_LIST_FILE;	
		File taskListFile = new File(path);
		String backup = path.substring(0, path.lastIndexOf('.')) + "-backup.xml";
		copy(taskListFile, new File(backup));
	}
    
    private boolean copy(File src, File dst) {
		try {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dst);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			return true;
		} catch (IOException ioe) {
			return false;
		}
    }
    
    public boolean isMultipleMode() {
    	return getPrefs().getBoolean(MULTIPLE_ACTIVE_TASKS);
    }
    
    public String[] getSaveOptions() {
    	String[] options = {TaskListSaveMode.ONE_HOUR.toString(),
    			TaskListSaveMode.THREE_HOURS.toString(),
    			TaskListSaveMode.DAY.toString()};
    	return options;
    }
}
