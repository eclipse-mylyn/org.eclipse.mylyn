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

import java.util.Iterator;
import java.util.List;

import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.util.MylarStatusHandler;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.MylarTaskListPrefConstants;
import org.eclipse.mylar.ui.IMylarUiBridge;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.mylar.ui.MylarUiPrefContstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class MylarEditorManager implements IMylarContextListener {

//	public static final int ACTIVATION_THRESHOLD = 8; 
	
	public void contextActivated(IMylarContext context) {
		if (MylarTaskListPlugin.getPrefs().getBoolean(MylarTaskListPrefConstants.AUTO_MANAGE_EDITORS)) {
	        Workbench workbench = (Workbench)PlatformUI.getWorkbench();
			try {				
				MylarPlugin.getContextManager().setContextCapturePaused(true);
				for (IMylarUiBridge bridge : MylarUiPlugin.getDefault().getUiBridges()) {
					bridge.setContextCapturePaused(true);
				}
		        workbench.largeUpdateStart();
		        
				List <IMylarElement> documents = MylarPlugin.getContextManager().getInterestingDocuments();
				int opened = 0;
				int threshold = MylarUiPlugin.getPrefs().getInt(MylarUiPrefContstants.MANAGE_EDITORS_AUTO_OPEN_NUM);
				for (Iterator iter = documents.iterator(); iter.hasNext() && opened < threshold-1; opened++) {
					IMylarElement document = (IMylarElement) iter.next();
					IMylarUiBridge bridge = MylarUiPlugin.getDefault().getUiBridge(document.getContentType());
					bridge.restoreEditor(document);
					opened++;
				}
				IMylarElement activeNode = context.getActiveNode();
				if (activeNode != null) {
		            MylarUiPlugin.getDefault().getUiBridge(activeNode.getContentType()).open(activeNode);
		        }
			} catch (Exception e) {
				MylarStatusHandler.fail(e, "failed to open editors on activation", false);
			} finally {				
				MylarPlugin.getContextManager().setContextCapturePaused(false);
				for (IMylarUiBridge bridge : MylarUiPlugin.getDefault().getUiBridges()) {
					bridge.setContextCapturePaused(false);
				}
				workbench.largeUpdateEnd();
			}
    	}
	}

	public void contextDeactivated(IMylarContext context) {
    	if (MylarTaskListPlugin.getPrefs().getBoolean(MylarTaskListPrefConstants.AUTO_MANAGE_EDITORS)) {
        	closeAllEditors();
      	} 
	}

	public void closeAllEditors() {
        try {
            IWorkbenchPage page = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
            if (page != null) page.closeAllEditors(true);
        } catch (Throwable t) {
            MylarStatusHandler.fail(t, "Could not auto close editor.", false);
        } 
	}
	
	public void presentationSettingsChanging(UpdateKind kind) {
		// ignore
	}

	public void presentationSettingsChanged(UpdateKind kind) {
		// ignore
	}

	public void interestChanged(IMylarElement node) {
		// ignore
	}

	public void interestChanged(List<IMylarElement> nodes) {
		// ignore
	}

	public void nodeDeleted(IMylarElement node) {
		// ignore
	}

	public void landmarkAdded(IMylarElement node) {
		// ignore
	}

	public void landmarkRemoved(IMylarElement node) {
		// ignore
	}

	public void edgesChanged(IMylarElement node) {
		// ignore
	}

}
