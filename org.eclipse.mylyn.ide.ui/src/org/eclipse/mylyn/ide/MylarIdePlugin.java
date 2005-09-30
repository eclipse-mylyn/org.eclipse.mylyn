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
import org.eclipse.mylar.ide.internal.ActiveSearchViewTracker;
import org.eclipse.mylar.ide.ui.NavigatorRefreshListener;
import org.eclipse.mylar.ide.ui.ProblemsListInterestFilter;
import org.eclipse.mylar.ide.ui.actions.ApplyMylarToNavigatorAction;
import org.eclipse.mylar.ide.ui.actions.ApplyMylarToProblemsListAction;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 */
public class MylarIdePlugin extends AbstractUIPlugin {

    private NavigatorRefreshListener navigatorRefreshListener = new NavigatorRefreshListener();
    protected ProblemsListInterestFilter interestFilter = new ProblemsListInterestFilter();    
    
    private ResourceSelectionMonitor resourceSelectionMonitor;
	private static MylarIdePlugin plugin;
	
	private ActiveSearchViewTracker activeSearchViewTracker = new ActiveSearchViewTracker();
	
	public MylarIdePlugin() {
		plugin = this;
	}
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		MylarPlugin.getContextManager().addListener(navigatorRefreshListener);
          
  		final IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.getDisplay().asyncExec(new Runnable() {
            public void run() {
            	resourceSelectionMonitor = new ResourceSelectionMonitor();
                MylarPlugin.getDefault().getSelectionMonitors().add(resourceSelectionMonitor);
            	
            	if (ApplyMylarToNavigatorAction.getDefault() != null) ApplyMylarToNavigatorAction.getDefault().update();
                if (ApplyMylarToProblemsListAction.getDefault() != null) ApplyMylarToProblemsListAction.getDefault().update();
                
                workbench.addWindowListener(activeSearchViewTracker);
        		IWorkbenchWindow[] windows= workbench.getWorkbenchWindows();
        		for (int i= 0; i < windows.length; i++) {
        			windows[i].addPageListener(activeSearchViewTracker);
        			IWorkbenchPage[] pages= windows[i].getPages();
        			for (int j= 0; j < pages.length; j++) {
        				pages[j].addPartListener(activeSearchViewTracker);
        			}
        		}
            }
        });
	}

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		MylarPlugin.getDefault().getSelectionMonitors().remove(resourceSelectionMonitor);
		MylarPlugin.getContextManager().removeListener(navigatorRefreshListener);
		
		IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.removeWindowListener(activeSearchViewTracker);
		IWorkbenchWindow[] windows= workbench.getWorkbenchWindows();
		for (int i= 0; i < windows.length; i++) {
			IWorkbenchPage[] pages= windows[i].getPages();
			windows[i].removePageListener(activeSearchViewTracker);
			for (int j= 0; j < pages.length; j++) {
				pages[j].removePartListener(activeSearchViewTracker);
			}
		}
	}

	public static MylarIdePlugin getDefault() {
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
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.mylar.ide", path);
	}
}
