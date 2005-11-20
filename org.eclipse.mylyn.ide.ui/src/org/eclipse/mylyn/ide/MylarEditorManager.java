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

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class MylarEditorManager implements IMylarContextListener {

	public void contextActivated(IMylarContext context) {
    	if (MylarUiPlugin.getPrefs().getBoolean(MylarPlugin.TASKLIST_EDITORS_CLOSE)) {
			List <IResource> resources = MylarIdePlugin.getDefault().getInterestingResources();
			IWorkbenchPage activePage = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
			for (IResource resource : resources) {
				if (resource instanceof IFile) {
					try {
						IDE.openEditor(activePage, (IFile)resource, false);
					} catch (PartInitException e) {
						MylarPlugin.fail(e, "failed to open editor", false);
					}
				}
			}
	        IMylarElement activeNode = context.getActiveNode();
	        if (activeNode != null) {
	            MylarUiPlugin.getDefault().getUiBridge(activeNode.getContentType()).open(activeNode);
	        }
    	}
	}

	public void contextDeactivated(IMylarContext context) {
    	if (MylarUiPlugin.getPrefs().getBoolean(MylarPlugin.TASKLIST_EDITORS_CLOSE)) {
        	closeAllEditors();
      	} 
	}

	public void closeAllEditors() {
        try {
            IWorkbenchPage page = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
            if (page != null) page.closeAllEditors(true);
        } catch (Throwable t) {
            MylarPlugin.fail(t, "Could not auto close editor.", false);
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
