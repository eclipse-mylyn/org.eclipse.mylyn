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
package org.eclipse.mylar.xml;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.util.MylarStatusHandler;
import org.eclipse.mylar.xml.ant.AntEditingMonitor;
import org.eclipse.mylar.xml.pde.PdeEditingMonitor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 */
public class MylarXmlPlugin extends AbstractUIPlugin { 

	public static ImageDescriptor EDGE_REF_XML = getImageDescriptor("icons/elcl16/edge-ref-xml.gif");
	
	private PdeEditingMonitor pdeEditingMonitor;
	private AntEditingMonitor antEditingMonitor;
		
	private static MylarXmlPlugin plugin;
	private ResourceBundle resourceBundle;
	
	/**
	 * The constructor.
	 */
	public MylarXmlPlugin() {
		super();
		plugin = this;
	}
        
	/**
	 * This method is called upon plug-in activation
	 */
    @Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		        
  		final IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.getDisplay().asyncExec(new Runnable() {
            public void run() {
            	try {
	            	pdeEditingMonitor = new PdeEditingMonitor();
	            	MylarPlugin.getDefault().getSelectionMonitors().add(pdeEditingMonitor);
	            	
	            	antEditingMonitor = new AntEditingMonitor();
	            	MylarPlugin.getDefault().getSelectionMonitors().add(antEditingMonitor);
	    		} catch (Exception e) {
	    			MylarStatusHandler.fail(e, "Mylar IDE stop failed", true);
	    		}
            }
        });
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
    @Override
	public void stop(BundleContext context) throws Exception {
    	try {
			super.stop(context);
			plugin = null;
			resourceBundle = null;
	        MylarPlugin.getDefault().getSelectionMonitors().remove(pdeEditingMonitor);
	        MylarPlugin.getDefault().getSelectionMonitors().remove(antEditingMonitor);
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Mylar XML stop failed", false);
		}
    }

	/**
	 * Returns the shared instance.
	 */
	public static MylarXmlPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = MylarXmlPlugin.getDefault().getResourceBundle();
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
				resourceBundle = ResourceBundle.getBundle("org.eclipse.mylar.xml.XmlPluginResources");
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
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.mylar.xml", path);
	}
}


