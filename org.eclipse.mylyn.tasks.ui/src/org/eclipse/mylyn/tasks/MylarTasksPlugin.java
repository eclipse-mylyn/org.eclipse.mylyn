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
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
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
	private ResourceBundle resourceBundle;

    /** The bridge between Bugzilla and mylar */
    private static BugzillaMylarBridge bridge = null;

    private static BugzillaStructureBridge structureBridge = new BugzillaStructureBridge();
    
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
        
    };
    
    private static ShellListener SHELL_LISTENER = new ShellListener() {
        private void saveState() {
            taskListManager.saveTaskList();
            for(ITask task : taskListManager.getTaskList().getActiveTasks()) {
                MylarPlugin.getTaskscapeManager().saveTaskscape(task.getHandle(), task.getPath());
            } 
        }
        public void shellClosed(ShellEvent arg0) {
            saveState();
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
                            	
                MylarPlugin.getDefault().addBridge(structureBridge);
                MylarPlugin.getTaskscapeManager().addListener(new BugzillaReferencesProvider());
                MylarUiPlugin.getDefault().addAdapter(BugzillaStructureBridge.EXTENSION, new BugzillaUiBridge());
                MylarPlugin.getDefault().getSelectionMonitors().add(new BugzillaEditingMonitor());             
                
                IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
                
                Workbench.getInstance().getActiveWorkbenchWindow().getShell().addShellListener(SHELL_LISTENER);
                MylarPlugin.getDefault().getPluginPreferences().addPropertyChangeListener(PREFERENCE_LISTENER);
                
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
    
    public static BugzillaStructureBridge getStructureBridge() {
        return structureBridge;
    }
}
