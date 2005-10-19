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

package org.eclipse.mylar.ui;

import java.util.List;

import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class MylarEditorManager implements IMylarContextListener {

	/**
	 * Set false for testing.
	 */
	private boolean asyncExecMode = true;
	
	public void contextActivated(IMylarContext context) {
		// TODO Auto-generated method stub
		
	}

	public void contextDeactivated(IMylarContext context) {
    	if (MylarUiPlugin.getPrefs().getBoolean(MylarPlugin.TASKLIST_EDITORS_CLOSE)) {
    		if (!asyncExecMode) {
    			closeAllEditors();
    		}
        	IWorkbench workbench = PlatformUI.getWorkbench();
            workbench.getDisplay().asyncExec(new Runnable() {
                public void run() {
                	closeAllEditors();
                }
            });
      	} else {
      		// TODO: enable closing of interesting editors
//		    	for (IMylarElement node : MylarPlugin.getContextManager().getInterestingResources(context)) {
//		            MylarUiPlugin.getDefault().getUiBridge(node.getStructureKind()).close(node);
//		        }       		
      	}
	}

	private void closeAllEditors() {
        try {
            IWorkbenchPage page = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
            if (page != null) page.closeAllEditors(true);
        } catch (Throwable t) {
            MylarPlugin.fail(t, "Could not auto close editor.", false);
        } 
	}
	
	public void presentationSettingsChanging(UpdateKind kind) {
		// TODO Auto-generated method stub
		
	}

	public void presentationSettingsChanged(UpdateKind kind) {
		// TODO Auto-generated method stub
		
	}

	public void interestChanged(IMylarElement node) {
		// TODO Auto-generated method stub
		
	}

	public void interestChanged(List<IMylarElement> nodes) {
		// TODO Auto-generated method stub
		
	}

	public void nodeDeleted(IMylarElement node) {
		// TODO Auto-generated method stub
		
	}

	public void landmarkAdded(IMylarElement node) {
		// TODO Auto-generated method stub
		
	}

	public void landmarkRemoved(IMylarElement node) {
		// TODO Auto-generated method stub
		
	}

	public void edgesChanged(IMylarElement node) {
		// TODO Auto-generated method stub
		
	}

	public void setAsyncExecMode(boolean asyncExecMode) {
		this.asyncExecMode = asyncExecMode;
	}

}
