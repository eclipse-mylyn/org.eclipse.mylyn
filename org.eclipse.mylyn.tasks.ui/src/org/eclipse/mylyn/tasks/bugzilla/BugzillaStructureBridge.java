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
 * Created on May 2, 2005
  */
package org.eclipse.mylar.tasks.bugzilla;

import org.eclipse.core.resources.IProject;
import org.eclipse.mylar.bugzilla.core.BugzillaRepository;
import org.eclipse.mylar.bugzilla.ui.editor.AbstractBugEditor;
import org.eclipse.mylar.bugzilla.ui.outline.BugzillaOutlineNode;
import org.eclipse.mylar.bugzilla.ui.outline.BugzillaReportSelection;
import org.eclipse.mylar.bugzilla.ui.outline.BugzillaTools;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.markers.internal.ProblemMarker;


public class BugzillaStructureBridge implements IMylarStructureBridge {

    public final static String EXTENSION = "bugzilla";
    
    public String getResourceExtension() {
        return EXTENSION;
    }
    
    public BugzillaStructureBridge() {
        super();
    }

    /**
     * Handle format: <server-name:port>;<bug-id>;<comment#>
     * 
     * Use: BugzillaTools ???
     */
    public String getHandleIdentifier(Object object) {
    	if(object instanceof BugzillaOutlineNode){
    		BugzillaOutlineNode n = (BugzillaOutlineNode)object;
    		return BugzillaTools.getHandle(n);
    	}
    	else if(object instanceof BugzillaReportSelection){
    		BugzillaReportSelection n = (BugzillaReportSelection)object;
    		return BugzillaTools.getHandle(n);
    	}
        return null;
    }

    public Object getObjectForHandle(String handle) {
        String [] parts = handle.split(";");
        if (parts.length >= 2){
//            String server = parts[0]; TODO add back in when we deal with multiple servers
            int id = Integer.parseInt(parts[1]);
            int commentNumber = -1;
            if(parts.length == 3){
            	 commentNumber = Integer.parseInt(parts[2]);	
            }
            try{
            	// get the bugzillaOutlineNode for the element
            	IEditorPart editorPart = null;
            	try{
                    editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
                }catch(NullPointerException e){
                    // do nothing, this just means that there is no active page
                }
                if(editorPart != null && editorPart instanceof AbstractBugEditor){
                	AbstractBugEditor abe = ((AbstractBugEditor)editorPart);
                	BugzillaOutlineNode node = abe.getModel();
                	return findNode(node, commentNumber);
                }

                // TODO There is a huge slowdown here always getting the object - maybe make bugzilla store the bug for a while in memory or as temp ofline?
                BugzillaOutlineNode node = BugzillaOutlineNode.parseBugReport(BugzillaRepository.getInstance().getCurrentBug(id));
                return findNode(node, commentNumber);
            }catch(Exception e){
                return null;
            }
        }
        else{
            return null;
        }
    }
    
    private BugzillaOutlineNode findNode(BugzillaOutlineNode startNode, int commentNumber){
    	
    	if(commentNumber == -1){
    		return startNode;
    	}else if(startNode.getComment() != null && startNode.getComment().getNumber() == commentNumber -1){
    		return startNode;
    	} else if(startNode.isCommentHeader() && commentNumber == 1){
    		return startNode;
    	}else if(startNode.isDescription() && commentNumber == 0){
    		return startNode;
    	}
    	
    	BugzillaOutlineNode[] children = startNode.getChildren();
    	for(int i = 0; i < children.length; i++){
    		BugzillaOutlineNode n = findNode(children[i], commentNumber);
    		if(n != null)
    			return n;
    	}
    	return null;
    }

    public String getParentHandle(String handle) {
    	BugzillaOutlineNode bon = (BugzillaOutlineNode)getObjectForHandle(handle);
    	if(bon != null && bon.getParent() != null)
    		return BugzillaTools.getHandle(bon.getParent());
    	else
    		return null;
//        String [] parts = handle.split(";");
//        if (parts.length == 1){
//            return null;
//        }else if (parts.length > 2) {
//            String newHandle = "";
//            for(int i = 0; i < parts.length - 1; i++)
//                newHandle += parts[i] + ";";
//            return newHandle.substring(0, newHandle.length() - 1);
////            return handle.substring(0, handle.lastIndexOf(";"));
//        }
//        return null;
    }

    public String getName(Object object) {
        if(object instanceof BugzillaOutlineNode){
        	BugzillaOutlineNode b = (BugzillaOutlineNode)object;
            return BugzillaTools.getName(b);
        }
        return "";
    }

    public boolean acceptAsLandmark(String handle) {
        return false;
    }

    public boolean acceptsObject(Object object) {
        return object instanceof BugzillaOutlineNode || object instanceof BugzillaReportSelection;
    }

    public boolean canFilter(Object element) {
        return true;
    }

    public boolean isDocument(String handle) {
        return (handle.indexOf(';') == handle.lastIndexOf(';') && handle.indexOf(";") != -1);
    }

    public String getHandleForMarker(ProblemMarker marker) {
        return null;
    }

	public IProject getProjectForObject(Object object) {
		// bugzilla objects do not yet sit in a project
		return null;
	}

    public String getResourceExtension(String elementHandle) {
        return getResourceExtension();
    }
}
