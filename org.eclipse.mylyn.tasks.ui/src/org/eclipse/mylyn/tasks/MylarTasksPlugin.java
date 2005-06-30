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
package org.eclipse.mylar.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasks.bugzilla.BugzillaContentProvider;
import org.eclipse.mylar.tasks.bugzilla.BugzillaEditingMonitor;
import org.eclipse.mylar.tasks.bugzilla.BugzillaMylarBridge;
import org.eclipse.mylar.tasks.bugzilla.BugzillaReferencesProvider;
import org.eclipse.mylar.tasks.bugzilla.BugzillaStructureBridge;
import org.eclipse.mylar.tasks.bugzilla.ui.BugzillaUiBridge;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 */
public class MylarTasksPlugin extends AbstractUIPlugin implements IStartup {
    
    private static MylarTasksPlugin plugin;
    private static TaskListManager taskListManager;
    private BugzillaContentProvider bugzillaProvider;
    
    public static final String FILE_EXTENSION = ".xml";
    public static final String TASK_ID = "org.eclipse.mylar.tasks.userid";
    public static final String DEFAULT_TASK_LIST_FILE = "tasklist" + FILE_EXTENSION;
    public static final String TASK_EDITOR_ID = "org.eclipse.mylar.tasks.ui.taskEditor";
    public static final String SHOW_P1_MODE = "org.eclipse.mylar.tasks.show.p1";
    public static final String SHOW_P2_MODE = "org.eclipse.mylar.tasks.show.p2";
    public static final String SHOW_P3_MODE = "org.eclipse.mylar.tasks.show.p3";
    public static final String SHOW_P4_MODE = "org.eclipse.mylar.tasks.show.p4";
    public static final String SHOW_P5_MODE = "org.eclipse.mylar.tasks.show.p5";
    public static final String FILTER_COMPLETE_MODE = "org.eclipse.mylar.tasks.filter.complete";
    public static final String FILTER_INCOMPLETE_MODE = "org.eclipse.mylar.tasks.filter.incomplete";
    
	private ResourceBundle resourceBundle;

	public enum Priority_Level {
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
                default: return "null";
            }
        }
        public static Priority_Level fromString(String string) {
            if (string == null) return null;
            if (string.equals("P1")) return P1;
            if (string.equals("P2")) return P2;
            if (string.equals("P3")) return P3;
            if (string.equals("P4")) return P4;
            if (string.equals("P5")) return P5;
            return null;
        }
    }
	
    /** The bridge between Bugzilla and mylar */
    private static BugzillaMylarBridge bridge = null;

    private BugzillaStructureBridge structureBridge;
    
    private static BugzillaReferencesProvider referencesProvider = new BugzillaReferencesProvider();
    
    private static ITaskActivityListener TASK_LIST_LISTENER = new ITaskActivityListener() {

        public void taskActivated(ITask task) {
            MylarPlugin.getTaskscapeManager().taskActivated(task.getHandle(), task.getPath());
        }

        public void tasksActivated(List<ITask> tasks) {
            for (ITask task : tasks) {
                MylarPlugin.getTaskscapeManager().taskActivated(task.getHandle(), task.getPath());
            }
        }

        public void taskDeactivated(ITask task) {
            MylarPlugin.getTaskscapeManager().taskDeactivated(task.getHandle(), task.getPath());
        }

		public void taskPropertyChanged(ITask updatedTask, String property) {
			// don't care about property change	
		}
        
    };
    
    private static ShellListener SHELL_LISTENER = new ShellListener() {
        private void saveState() {
            taskListManager.saveTaskList();
        }
        public void shellClosed(ShellEvent arg0) {
            saveState();
            for(ITask task : taskListManager.getTaskList().getActiveTasks()) {
                MylarPlugin.getTaskscapeManager().saveTaskscape(task.getHandle(), task.getPath());
            }
        }  
        public void shellDeactivated(ShellEvent arg0) { 
            saveState();
        }
        public void shellActivated(ShellEvent arg0) { }
        public void shellDeiconified(ShellEvent arg0) { }
        public void shellIconified(ShellEvent arg0) { }
    };
    
    private static IPropertyChangeListener PREFERENCE_LISTENER = new IPropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent event) {
			// TODO Auto-generated method stub
			if (event.getProperty().equals(MylarPlugin.MYLAR_DIR)) {				
				if (event.getOldValue() instanceof String) {
					String prevDir = (String) event.getOldValue();				
					MylarPlugin.getTaskscapeManager().updateMylarDirContents(prevDir);
					getTaskListManager().updateTaskscapeReference(prevDir);
					
					String path = MylarPlugin.getDefault().getUserDataDirectory() + File.separator + DEFAULT_TASK_LIST_FILE;        
					getTaskListManager().setFile(new File(path));
				}
			} else {
			}
		}        
    };
    
	public MylarTasksPlugin() {
		super();
		plugin = this;
	}

    public void earlyStartup() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.getDisplay().asyncExec(new Runnable() {
            public void run() {
                
            	structureBridge = new BugzillaStructureBridge();
            	
                MylarPlugin.getDefault().addBridge(structureBridge);
                MylarPlugin.getTaskscapeManager().addListener(referencesProvider);
                MylarUiPlugin.getDefault().addAdapter(BugzillaStructureBridge.EXTENSION, new BugzillaUiBridge());
                MylarPlugin.getDefault().getSelectionMonitors().add(new BugzillaEditingMonitor());             
                
                IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
                
                Workbench.getInstance().getActiveWorkbenchWindow().getShell().addShellListener(SHELL_LISTENER);
                MylarPlugin.getDefault().getPluginPreferences().addPropertyChangeListener(PREFERENCE_LISTENER);
                
                getPrefs().setDefault(SHOW_P1_MODE, true);
                getPrefs().setDefault(SHOW_P2_MODE, true);
                getPrefs().setDefault(SHOW_P3_MODE, true);
                getPrefs().setDefault(SHOW_P4_MODE, true);
                getPrefs().setDefault(SHOW_P5_MODE, true);                
                if (window != null) {
                    // create a new bridge and initialize it
                    bridge = new BugzillaMylarBridge();
                }
            }
        });
    }
    
    @Override
	public void start(BundleContext context) throws Exception {
        bugzillaProvider = new BugzillaContentProvider();
        String path = MylarPlugin.getDefault().getUserDataDirectory() + File.separator + DEFAULT_TASK_LIST_FILE;        
        File taskListFile = new File(path);
        taskListManager = new TaskListManager(taskListFile);
        taskListManager.addListener(TASK_LIST_LISTENER);
        taskListManager.readTaskList();
        if (taskListManager.getTaskList() == null) taskListManager.createNewTaskList();
        
		super.start(context);
	}

    @Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		resourceBundle = null;
	}

    /**
     * Get the bridge for this plugin
     * 
     * @return The bugzilla mylar bridge
     */
    public static BugzillaMylarBridge getBridge() {
        // make sure that the bridge initialized, if not, make a new one
        if (bridge == null) {
            bridge = new BugzillaMylarBridge();
//            MylarPlugin.getTaskscapeManager().addRelationshipProvider(new BugzillaRelationshipProvider());
//            MylarUiPlugin.getDefault().getAdapters().put(ITaskscapeNode.Kind.Bugzilla, new BugzillaUiAdapter());
        }
        return bridge;
    }
    
    
    public static TaskListManager getTaskListManager() {
        return taskListManager;
    }
    
	/**
	 * Returns the shared instance.
	 */
	public static MylarTasksPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = MylarTasksPlugin.getDefault().getResourceBundle();
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
    
    public BugzillaContentProvider getBugzillaProvider() {
        return bugzillaProvider;
    }
    
    public void setBugzillaProvider(BugzillaContentProvider bugzillaProvider) {
        this.bugzillaProvider = bugzillaProvider;
    }
    
    public BugzillaStructureBridge getStructureBridge() {
        return structureBridge;
    }

	public static BugzillaReferencesProvider getReferenceProvider() {
		return referencesProvider;
		
	}
	
	public static IPreferenceStore getPrefs() {
		return MylarPlugin.getDefault().getPreferenceStore();
	}
	
	public static void setPriorityLevel(Priority_Level pl, boolean showPriority) {
		String key = "";
		switch(pl) {
		case P1: key = SHOW_P1_MODE; break;
		case P2: key = SHOW_P2_MODE; break;
		case P3: key = SHOW_P3_MODE; break;
		case P4: key = SHOW_P4_MODE; break;
		case P5: key = SHOW_P5_MODE; break;
		default: key = SHOW_P1_MODE; break;
		}		
		getPrefs().setValue(key, showPriority);
	}
	public static boolean getPriorityLevel(Priority_Level pl) {
		String key = "";
		switch(pl) {
		case P1: key = SHOW_P1_MODE; break;
		case P2: key = SHOW_P2_MODE; break;
		case P3: key = SHOW_P3_MODE; break;
		case P4: key = SHOW_P4_MODE; break;
		case P5: key = SHOW_P5_MODE; break;
		default: key = SHOW_P1_MODE; break;
		}		
		if(getPrefs().contains(key)) {
			return getPrefs().getBoolean(key);
		} else {			
			return true;
		}		
	}
	public static List<Priority_Level> getPriorityLevels() {
		List<Priority_Level>  levels = new ArrayList<Priority_Level>();
		if (getPriorityLevel(Priority_Level.P1)) {
			levels.add(Priority_Level.P1);
		}
		if (getPriorityLevel(Priority_Level.P2)) {
			levels.add(Priority_Level.P2);
		}
		if (getPriorityLevel(Priority_Level.P3)) {
			levels.add(Priority_Level.P3);
		}
		if (getPriorityLevel(Priority_Level.P4)) {
			levels.add(Priority_Level.P4);
		}
		if (getPriorityLevel(Priority_Level.P5)) {
			levels.add(Priority_Level.P5);
		}
		return levels;
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
}
