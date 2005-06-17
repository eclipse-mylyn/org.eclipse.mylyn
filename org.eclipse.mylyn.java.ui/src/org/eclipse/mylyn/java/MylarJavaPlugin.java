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
package org.eclipse.mylar.java;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.filters.CustomFiltersDialog;
import org.eclipse.jdt.internal.ui.filters.FilterDescriptor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.java.search.JUnitReferencesProvider;
import org.eclipse.mylar.java.search.JavaImplementorsProvider;
import org.eclipse.mylar.java.search.JavaReadAccessProvider;
import org.eclipse.mylar.java.search.JavaReferencesProvider;
import org.eclipse.mylar.java.search.JavaWriteAccessProvider;
import org.eclipse.mylar.java.ui.JavaUiBridge;
import org.eclipse.mylar.java.ui.LandmarkMarkerManager;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 */
public class MylarJavaPlugin extends AbstractUIPlugin implements IStartup {
	private static MylarJavaPlugin plugin;
	private ResourceBundle resourceBundle;
    private static JavaStructureBridge structureBridge = new JavaStructureBridge();
    private static JavaModelUiUpdateBridge modelUpdateBridge = new JavaModelUiUpdateBridge();
    private static JavaUiBridge uiBridge = new JavaUiBridge();
    
	public MylarJavaPlugin() {
		super();
		plugin = this;
    }

    /**
     * Used to start plugin on startup -> entry in plugin.xml to invoke this
     * 
     * @see org.eclipse.ui.IStartup#earlyStartup()
     */
    public void earlyStartup() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.getDisplay().asyncExec(new Runnable() {
            public void run() {
//                MylarGenericPlugin.getDefault(); // depends on it for package explorer
                MylarPlugin.getDefault().addBridge(structureBridge); 
                MylarPlugin.getTaskscapeManager().addListener(modelUpdateBridge);

                MylarPlugin.getTaskscapeManager().addListener(new JavaReferencesProvider());
                MylarPlugin.getTaskscapeManager().addListener(new JavaImplementorsProvider());
                MylarPlugin.getTaskscapeManager().addListener(new JavaReadAccessProvider());
                MylarPlugin.getTaskscapeManager().addListener(new JavaWriteAccessProvider()); 
                MylarPlugin.getTaskscapeManager().addListener(new JUnitReferencesProvider());
                
                MylarPlugin.getDefault().getSelectionMonitors().add(new JavaEditingMonitor());
                MylarPlugin.getTaskscapeManager().addListener(new LandmarkMarkerManager());
                MylarUiPlugin.getDefault().addAdapter(structureBridge.getResourceExtension(), uiBridge);
                modelUpdateBridge.revealInteresting();
                
                
                // HACK: used to disable the filter from the quick outline by default
                initializeWithPluginContributions();
                
            }
        });
    }

	
	/**
	 * This method is called upon plug-in activation
	 */
    @Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
    @Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		resourceBundle = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static MylarJavaPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = MylarJavaPlugin.getDefault().getResourceBundle();
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
				resourceBundle = ResourceBundle.getBundle("org.eclipse.mylar.java.JavaPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.mylar.java", path);
	}

    public static JavaUiBridge getUiBridge() {
        return uiBridge;
    }

    public static JavaStructureBridge getStructureBridge() {
        return structureBridge;
    }
    
    
    
    
    /**
     * 
     * CODE FROM @see org.eclipse.jdt.ui.actions.CustomFiltersActionGroup
     * 
     * Slightly modified.
     * Needed to initialize the structure view to have no filter
     * 
     */
    
	private static final String TAG_USER_DEFINED_PATTERNS_ENABLED= "userDefinedPatternsEnabled"; //$NON-NLS-1$
	private static final String TAG_USER_DEFINED_PATTERNS= "userDefinedPatterns"; //$NON-NLS-1$
	private static final String TAG_LRU_FILTERS = "lastRecentlyUsedFilters"; //$NON-NLS-1$

	private static final String SEPARATOR= ",";  //$NON-NLS-1$
	
	private final String fTargetId = "org.eclipse.jdt.internal.ui.text.QuickOutline";
	
    private void initializeWithPluginContributions() {
    	IPreferenceStore store= JavaPlugin.getDefault().getPreferenceStore();
    	if (store.contains(getPreferenceKey("TAG_DUMMY_TO_TEST_EXISTENCE")))
    		return;
    	
		FilterDescriptor[] filterDescs= getCachedFilterDescriptors();
		Map<String, FilterDescriptor> fFilterDescriptorMap= new HashMap<String, FilterDescriptor>(filterDescs.length);
		Map<String, Boolean> fEnabledFilterIds= new HashMap<String, Boolean>(filterDescs.length);
		for (int i= 0; i < filterDescs.length; i++) {
			String id= filterDescs[i].getId();
			Boolean isEnabled= new Boolean(filterDescs[i].isEnabled());
			if (fEnabledFilterIds.containsKey(id))
				JavaPlugin.logErrorMessage("WARNING: Duplicate id for extension-point \"org.eclipse.jdt.ui.javaElementFilters\""); //$NON-NLS-1$
			fEnabledFilterIds.put(id, isEnabled);
			fFilterDescriptorMap.put(id, filterDescs[i]);
		}
		storeViewDefaults(fEnabledFilterIds, store);
	}
    
    private void storeViewDefaults(Map<String, Boolean> fEnabledFilterIds, IPreferenceStore store) {
		// see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=22533
		store.setValue(getPreferenceKey("TAG_DUMMY_TO_TEST_EXISTENCE"), "storedViewPreferences");//$NON-NLS-1$//$NON-NLS-2$
		
		store.setValue(getPreferenceKey(TAG_USER_DEFINED_PATTERNS_ENABLED), false);
		store.setValue(getPreferenceKey(TAG_USER_DEFINED_PATTERNS), CustomFiltersDialog.convertToString(new String[0],SEPARATOR));

		Iterator iter= fEnabledFilterIds.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry= (Map.Entry)iter.next();
			String id= (String)entry.getKey();
			boolean isEnabled= ((Boolean)entry.getValue()).booleanValue();
			if(id.equals("org.eclipse.mylar.ui.java.InterestFilter")){
				store.setValue(id, false);	
			} else {
				store.setValue(id, isEnabled);
			}
		}

		StringBuffer buf= new StringBuffer("");
		store.setValue(TAG_LRU_FILTERS, buf.toString());
	}
	
	private String getPreferenceKey(String tag) {
		return "CustomFiltersActionGroup." + fTargetId + '.' + tag; //$NON-NLS-1$
	}
    
	private FilterDescriptor[] getCachedFilterDescriptors() {
		FilterDescriptor[] fCachedFilterDescriptors= FilterDescriptor.getFilterDescriptors(fTargetId);
		return fCachedFilterDescriptors;
	}
}
