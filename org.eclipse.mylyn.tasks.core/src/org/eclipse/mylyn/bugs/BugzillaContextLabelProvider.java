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
/*
 * Created on Apr 18, 2005
  */
package org.eclipse.mylar.bugs;

import org.eclipse.mylar.bugs.search.BugzillaReferencesProvider;
import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaReportNode;
import org.eclipse.mylar.core.IMylarContextEdge;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.AbstractContextLabelProvider;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class BugzillaContextLabelProvider extends AbstractContextLabelProvider {
	
	@Override
	protected Image getImage(IMylarContextNode node) {
		return MylarImages.getImage(MylarImages.BUG); 
	}

	@Override
	protected Image getImage(IMylarContextEdge edge) {
		return MylarImages.getImage(MylarImages.EDGE_REF_BUGZILLA); 
	}

    /**
     * TODO: slow?
     */
	@Override
	protected String getText(IMylarContextNode node) {        
        // try to get from the cache before downloading
        Object report;
    	BugzillaReportNode reportNode = MylarBugsPlugin.getReferenceProvider().getCached(node.getElementHandle());
    	BugReport cachedReport = MylarBugsPlugin.getDefault().getCache().getCached(node.getElementHandle());
    	IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(BugzillaStructureBridge.EXTENSION);
		
    	if(reportNode != null && cachedReport == null){
    		report = reportNode;
    	} else{
     		report = bridge.getObjectForHandle(node.getElementHandle());
    	}
        return bridge.getName(report);
	}

	@Override
	protected String getText(IMylarContextEdge edge) {
		return BugzillaReferencesProvider.NAME;  
	}
} 
