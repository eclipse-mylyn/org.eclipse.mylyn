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
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.mylar.xml.ant.AntEditingMonitor;
import org.eclipse.mylar.xml.ant.AntStructureBridge;
import org.eclipse.mylar.xml.ant.ui.AntUiBridge;
import org.eclipse.mylar.xml.pde.PdeEditingMonitor;
import org.eclipse.mylar.xml.pde.PdeStructureBridge;
import org.eclipse.mylar.xml.pde.ui.PdeUiBridge;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class MylarXmlPlugin extends AbstractUIPlugin implements IStartup { 

	private static MylarXmlPlugin plugin;
	private ResourceBundle resourceBundle;
	
    private static PdeStructureBridge pdeStructureBridge;
    private static AntStructureBridge antStructureBridge;
    
    private static PdeUiBridge pdeUiBridge;
    private static AntUiBridge antUiBridge;
    
	/**
	 * The constructor.
	 */
	public MylarXmlPlugin() {
		super();
		plugin = this;
	}

    /**
     * Used to start plugin on startup -> entry in plugin.xml to invoke this
     * 
     * @see org.eclipse.ui.IStartup#earlyStartup()
     */
    public void earlyStartup() {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            public void run() {
             
              pdeStructureBridge = new PdeStructureBridge(MylarPlugin.getDefault().getGenericResourceBridge());
              antStructureBridge = new AntStructureBridge(MylarPlugin.getDefault().getGenericResourceBridge());
              
              antUiBridge = new AntUiBridge(); 
              pdeUiBridge = new PdeUiBridge(); 
              
                //PDE
              MylarPlugin.getDefault().addBridge(pdeStructureBridge);
              MylarPlugin.getDefault().getSelectionMonitors().add(new PdeEditingMonitor());
              MylarUiPlugin.getDefault().addAdapter(PdeStructureBridge.EXTENSION, pdeUiBridge);
              
              //ANT
              MylarPlugin.getDefault().addBridge(antStructureBridge);
              MylarPlugin.getDefault().getSelectionMonitors().add(new AntEditingMonitor());
              MylarUiPlugin.getDefault().addAdapter(AntStructureBridge.EXTENSION,antUiBridge);
              
// code for testing whether selections are being sent or not
//              Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService().addPostSelectionListener(new ISelectionListener() {
//                public void selectionChanged(IWorkbenchPart part, ISelection selection) {
//                }  
//              });
//              Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService().addSelectionListener(new ISelectionListener() {
//                  public void selectionChanged(IWorkbenchPart part, ISelection selection) {
//                  }  
//              });
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

    public static AntStructureBridge getAntStructureBridge() {
        return antStructureBridge;
    }

    public static PdeStructureBridge getPdeStructureBridge() {
        return pdeStructureBridge;
    }
    
    public static AntUiBridge getAntUiBridge() {
        return antUiBridge;
    }

    public static PdeUiBridge getPdeUiBridge() {
        return pdeUiBridge;
    }
}


