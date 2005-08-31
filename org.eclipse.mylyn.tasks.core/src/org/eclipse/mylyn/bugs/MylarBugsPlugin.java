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
package org.eclipse.mylar.bugs;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.bugs.search.BugzillaReferencesProvider;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class MylarBugsPlugin extends AbstractUIPlugin {

    private static BugzillaMylarBridge bridge = null;
//    private BugzillaStructureBridge structureBridge;
    private static BugzillaReferencesProvider referencesProvider = new BugzillaReferencesProvider();
	private static MylarBugsPlugin plugin;
	private BugzillaReportCache cache;
	
	
	public MylarBugsPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		cache = new BugzillaReportCache();
		cache.readCacheFile();

//        MylarUiPlugin.getDefault().addAdapter(BugzillaStructureBridge.EXTENSION, new BugzillaUiBridge());
        MylarPlugin.getDefault().getSelectionMonitors().add(new BugzillaEditingMonitor());             
        
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            // create a new bridge and initialize it
            bridge = new BugzillaMylarBridge();
        }
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;

//        List<AbstractRelationshipProvider> providers = structureBridge.getProviders();
//        if(providers != null){
//	        for(AbstractRelationshipProvider provider: providers){
//	        	provider.stopAllRunningJobs();
//	        }
//        }
	}

	/**
	 * Returns the shared instance.
	 */
	public static MylarBugsPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.mylar.bugs.bridge", path);
	}
	
    public static BugzillaMylarBridge getBridge() {
        // make sure that the bridge initialized, if not, make a new one
        if (bridge == null) {
            bridge = new BugzillaMylarBridge();
        }
        return bridge;
    }
    
    
//    public BugzillaStructureBridge getStructureBridge() {
//        return structureBridge;
//    }

	public static BugzillaReferencesProvider getReferenceProvider() {
		return referencesProvider;
		
	}

	public BugzillaReportCache getCache() {
		return cache;
	}
}
