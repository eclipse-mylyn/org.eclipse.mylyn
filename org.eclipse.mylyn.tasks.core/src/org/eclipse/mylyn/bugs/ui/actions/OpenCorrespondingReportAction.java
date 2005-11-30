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

package org.eclipse.mylar.bugs.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.bugs.java.OpenBugzillaReportJob;
import org.eclipse.mylar.bugzilla.ui.BugzillaUITools;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ide.MylarContextChangeSet;
import org.eclipse.swt.widgets.Display;
import org.eclipse.team.internal.ccvs.core.client.listeners.LogEntry;
import org.eclipse.team.internal.ui.synchronize.ChangeSetDiffNode;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ObjectPluginAction;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Mik Kersten
 */
public class OpenCorrespondingReportAction implements IViewActionDelegate {

	public void init(IViewPart view) {
		// ignore
	}

	public void run(IAction action) {
		if (action instanceof ObjectPluginAction) {
    		ObjectPluginAction objectAction = (ObjectPluginAction)action;
    		if (objectAction.getSelection() instanceof StructuredSelection) {
    			StructuredSelection selection = (StructuredSelection)objectAction.getSelection();
    			Object firstElement = selection.getFirstElement();
    			String comment = null;
    			boolean resolved = false;
    			if (firstElement instanceof ChangeSetDiffNode) {
    				comment = ((ChangeSetDiffNode)firstElement).getName();
    			} else if (firstElement instanceof LogEntry){
    				comment = ((LogEntry)firstElement).getComment();
    			}
    			if (comment != null) {
					String idString = MylarContextChangeSet.getIssueIdFromComment(comment);
					String url = MylarContextChangeSet.getUrlFromComment(comment);
					
					int id = -1;
					try {
						id = Integer.parseInt(idString);
					} catch (NumberFormatException e) { 
						// ignore
					}
					if (id != -1) {
						OpenBugzillaReportJob job = new OpenBugzillaReportJob(id);
						IProgressService service = PlatformUI.getWorkbench().getProgressService();
						try {
							service.run(true, false, job);
						} catch (Exception e) {
							MylarPlugin.fail(e, "Could not open report", true);
						}
						resolved = true;
					} else if (url != null) {
	    				BugzillaUITools.openUrl("Web Browser", "Web Browser", url);
	    				resolved = true;
					} 
    			}
    			
    			if (!resolved) {
					MessageDialog.openInformation(
							Display.getCurrent().getActiveShell(), 
							"Mylar Information", 
							"Could not resolve report corresponding to change set comment.");
				}
    		}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
	}

}
