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
package org.eclipse.mylar.ide;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ide.ui.NavigatorRefreshListener;
import org.eclipse.mylar.ide.ui.ProblemsListInterestFilter;
import org.eclipse.mylar.ide.ui.actions.ApplyMylarToNavigatorAction;
import org.eclipse.mylar.ide.ui.actions.ApplyMylarToProblemsListAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class MylarIdePlugin extends AbstractUIPlugin {

//    private ResourceStructureBridge genericResourceBridge;
    private NavigatorRefreshListener navigatorRefreshListener = new NavigatorRefreshListener();
    protected ProblemsListInterestFilter interestFilter = new ProblemsListInterestFilter();    
    
	private static MylarIdePlugin plugin;
	
	public MylarIdePlugin() {
		plugin = this;
	}

//    public void earlyStartup() {
//        final IWorkbench workbench = PlatformUI.getWorkbench();
//        workbench.getDisplay().asyncExec(new Runnable() {
//            public void run() {
//              
//            }
//        });
//    }
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		  MylarPlugin.getContextManager().addListener(navigatorRefreshListener);
          MylarPlugin.getDefault().getSelectionMonitors().add(new ResourceSelectionMonitor());
          
          if (ApplyMylarToNavigatorAction.getDefault() != null) ApplyMylarToNavigatorAction.getDefault().update();
          if (ApplyMylarToProblemsListAction.getDefault() != null) ApplyMylarToProblemsListAction.getDefault().update();
//        genericResourceBridge = new ResourceStructureBridge();//MylarPlugin.getDefault().isPredictedInterestEnabled());
//        MylarPlugin.getDefault().setDefaultBridge(genericResourceBridge);
	}

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	public static MylarIdePlugin getDefault() {
		return plugin;
	}

//    public ResourceStructureBridge getGenericResourceBridge() {
//        return genericResourceBridge;
//    }
	
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.mylar.ide", path);
	}
}
