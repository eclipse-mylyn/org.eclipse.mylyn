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
package org.eclipse.mylar.core;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.mylar.core.internal.MylarContextManager;
import org.eclipse.mylar.core.resources.ResourceStructureBridge;
import org.eclipse.mylar.core.util.DateUtil;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.views.markers.internal.ProblemMarker;
import org.osgi.framework.BundleContext;

 
/**
 * @author Mik Kersten
 */
public class MylarPlugin extends AbstractUIPlugin {
    
	private boolean predictedInterestEnabled = false;
	
    private Map<String, IMylarStructureBridge> bridges = new HashMap<String, IMylarStructureBridge>();
    
    private ResourceStructureBridge genericResourceBridge;
        
    private List<AbstractSelectionMonitor> selectionMonitors = new ArrayList<AbstractSelectionMonitor>();
    private List<AbstractCommandMonitor> commandMonitors = new ArrayList<AbstractCommandMonitor>();
    
    public static final String USER_ID = "org.eclipse.mylar.user.id";
    public static final String CLOSE_EDITORS = "org.eclipse.mylar.close.editors";
    
    public static boolean started = false;
    
	private static MylarPlugin INSTANCE;
    private static MylarContextManager contextManager;
    private ResourceBundle resourceBundle;
    public static final String IDENTIFIER = "org.eclipse.mylar.core";
//    public static boolean DEBUG_MODE = true;
    public static final String LOG_FILE_NAME = "mylar-log.txt";
    private PrintStream logStream = null;
    
    public static final String MYLAR_DIR = "org.eclipse.mylar.model.dir";
    public static final String MYLAR_DIR_NAME = ".mylar";
    private static final IMylarStructureBridge DEFAULT_BRIDGE = new IMylarStructureBridge() {
    	

        /**
         * Used to check for the null adapter
         */
        public String getResourceExtension() {
            return null;
        }
        
        public String getHandleIdentifier(Object object) {
//            MylarPlugin.log(this, "null bridge for object: " + object.getClass());
//            return null; 
            throw new RuntimeException("null bridge for object: " + object.getClass());
        }

        public Object getObjectForHandle(String handle) {
            MylarPlugin.log("null bridge for handle: " + handle, this);
            return null;
//            throw new RuntimeException("null adapter for handle: " + handle);
        }

        public String getParentHandle(String handle) {
            MylarPlugin.log("null bridge for handle: " + handle, this);
            return null;
        }

        public String getName(Object object) {
            MylarPlugin.log("null bridge for object: " + object.getClass(), this);
            return "";
//            throw new RuntimeException("null adapter");
        }

        /**
         * TODO: this behavir is depended on, move?
         */
        public boolean canBeLandmark(Object element) {
        	return false;
//            return false;
//            throw new RuntimeException("null bridge: " + element);
        }

        public boolean acceptsObject(Object object) {
//            return false;
            throw new RuntimeException("null bridge for object: " + object.getClass());
        }

        public boolean canFilter(Object element) {
//            MylarPlugin.log(this, "null bridge for element: " + element.getClass());
            return false;
        } 

        public boolean isDocument(String handle) {
//            return false;
            throw new RuntimeException("null adapter for handle: " + handle);
        }

        public String getHandleForMarker(ProblemMarker marker) {
            MylarPlugin.log("null bridge for marker: " + marker.getClass(), this);
            return null;
//            throw new RuntimeException("null adapter");
        }

		public IProject getProjectForObject(Object object) {
//			return null;
            throw new RuntimeException("null brige for object: " + object);
		}

        public String getResourceExtension(String elementHandle) {
            return getResourceExtension();
        }
    };
    
    public MylarPlugin() {  
		INSTANCE = this;  
	}
    
    /**
     * This method is called upon plug-in activation
     */
	@Override
    public void start(BundleContext context) throws Exception {
        super.start(context);

        getPreferenceStore().setDefault(
                MYLAR_DIR, ResourcesPlugin.getWorkspace().getRoot().getLocation().toString()
                + File.separator
                + MYLAR_DIR_NAME);
        contextManager = new MylarContextManager(); 
        genericResourceBridge = new ResourceStructureBridge(predictedInterestEnabled);
    }

    /**
     * This method is called when the plug-in is stopped
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        INSTANCE = null;
        resourceBundle = null;
    }
    
    public String getUserDataDirectory() {
        return getPreferenceStore().getString(MylarPlugin.MYLAR_DIR);
    }
    
    public static MylarPlugin getDefault() {
		return INSTANCE;
	}
	
    public static MylarContextManager getContextManager() {
        return contextManager;
    }

    public List<AbstractSelectionMonitor> getSelectionMonitors() {
        return selectionMonitors;
    }

    /**
     * Logs the specified status with this plug-in's log.
     * 
     * @param status status to log
     */
    public static void log(IStatus status) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[");
        buffer.append(DateUtil.getFormattedDate());
        buffer.append(", ");
        buffer.append(DateUtil.getFormattedTime());
        buffer.append("] ");
        buffer.append(status.toString());
        if (status.getException() != null) {
        	buffer.append(", exception: ");
        	buffer.append(status.getException().getStackTrace()[0]);
        }
     
        MylarPlugin.getDefault().getLog().log(status);
        if (getDefault().logStream != null) getDefault().logStream.println(buffer.toString());
    }

    public static void log(String message, Object source) {
        if (source != null) message += ", source: " + source.getClass().getName();
    	
        log(new Status(IStatus.INFO, MylarPlugin.IDENTIFIER, IStatus.OK, message, null));    	
    }
    
    public static void log(Throwable throwable, String message) {
    	fail(throwable, message, false);
    }

    /**
         * Log a failure
         * @param throwable  can be null
         * @param message The message to include
         * @param informUser if true dialog box will be popped up
         */
    public static void fail(Throwable throwable, String message, boolean informUser) {
        if (message == null) message = "no message";
 
        final Status status= new Status(
                Status.ERROR,
                MylarPlugin.IDENTIFIER, 
                IStatus.OK,
                message,
                throwable);

        log(status);
        
        if (informUser) {
          Workbench.getInstance().getDisplay().syncExec(new Runnable() {
              public void run() {
                  ErrorDialog.openError(
                          Workbench.getInstance().getActiveWorkbenchWindow().getShell(),
                          "Mylar error",
                          "Please report the following error",
                          status);
              }
          });
        }
    }

    /**
     * TODO: performance issue?
     */
    public IMylarStructureBridge getStructureBridge(String extension) {
        IMylarStructureBridge adapter = bridges.get(extension);
        if (adapter != null) {
            return adapter;
        } else {
            return genericResourceBridge;
//            if (genericResourceBridge.acceptsObject(getObjectForHandle(handle))) {
//                return genericResourceBridge;
//            } else {
//                return NULL_BRIDGE;
//            }
        }
    }

    /**
     * TODO: cache this to improve performance?
     * 
     * @return null if there are no bridges loaded, null bridge otherwise
     */
    public IMylarStructureBridge getStructureBridge(Object object) {
        IMylarStructureBridge bridge = null;
        if (bridges.size() == 0) return null;
        for (IMylarStructureBridge structureBridge : bridges.values()) {
            if (structureBridge.acceptsObject(object)) {
                bridge = structureBridge; 
                break;
            }
        }
        if (bridge != null) {
            return bridge;
        } else {
            if (genericResourceBridge.acceptsObject(object)) {
                return genericResourceBridge;
            } else {
                return DEFAULT_BRIDGE;
            }
        }
    }

    public void addBridge(IMylarStructureBridge bridge) {
        this.bridges.put(bridge.getResourceExtension(), bridge);
    }
    
    public List<AbstractCommandMonitor> getCommandMonitors() {
        return commandMonitors;
    }
    
    /**
     * Returns the string from the plugin's resource bundle,
     * or 'key' if not found.
     */
    public static String getResourceString(String key) {
        ResourceBundle bundle = MylarPlugin.getDefault().getResourceBundle();
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
                resourceBundle = ResourceBundle.getBundle("org.eclipse.mylar.core.MylarPluginResources");
        } catch (MissingResourceException x) {
            resourceBundle = null;
        }
        return resourceBundle;
    }   
    
    public ResourceStructureBridge getGenericResourceBridge() {
        return genericResourceBridge;
    }

	public void setLogStream(PrintStream logStream) {
		this.logStream = logStream;
	}

	public PrintStream getLogStream() {
		return logStream;
	}

	public boolean isPredictedInterestEnabled() {
		return predictedInterestEnabled;
	}	
	
	public boolean suppressWizardsOnStartup() {
    	List<String> commandLineArgs = Arrays.asList(Platform.getCommandLineArgs());
    	if (commandLineArgs.contains("-showmylarwizards")) {
    		return false;
    	} else {
    		return commandLineArgs.contains("-pdelaunch");
    	}
	}
}
