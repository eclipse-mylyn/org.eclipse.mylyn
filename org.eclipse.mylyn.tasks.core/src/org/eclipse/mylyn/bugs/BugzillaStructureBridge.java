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

package org.eclipse.mylar.bugs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.bugzilla.core.BugzillaRepository;
import org.eclipse.mylar.bugzilla.core.BugzillaTools;
import org.eclipse.mylar.bugzilla.core.search.BugzillaSearchHit;
import org.eclipse.mylar.bugzilla.ui.editor.AbstractBugEditor;
import org.eclipse.mylar.bugzilla.ui.outline.BugzillaOutlineNode;
import org.eclipse.mylar.bugzilla.ui.outline.BugzillaReportSelection;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaReportNode;
import org.eclipse.mylar.core.AbstractRelationshipProvider;
import org.eclipse.mylar.core.IDegreeOfSeparation;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.internal.DegreeOfSeparation;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.views.markers.internal.ProblemMarker;


public class BugzillaStructureBridge implements IMylarStructureBridge {

    public final static String EXTENSION = "bugzilla";
    
    public List<AbstractRelationshipProvider> providers;
    
    public String getResourceExtension() {
        return EXTENSION;
    }
    
    public BugzillaStructureBridge() {
        super();
		providers = new ArrayList<AbstractRelationshipProvider>();
    	providers.add(MylarBugsPlugin.getReferenceProvider());
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

    private BugReport result;
  
    public Object getObjectForHandle(final String handle) {
    	result = null;

        String [] parts = handle.split(";");
        if (parts.length >= 2){
        	String server = parts[0];
            final int id = Integer.parseInt(parts[1]);

            String bugHandle = server + ";" + id;
            
            int commentNumber = -1;
            if(parts.length == 3){
            	 commentNumber = Integer.parseInt(parts[2]);	
            }
            
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

            BugzillaReportNode reportNode = MylarBugsPlugin.getReferenceProvider().getCached(handle);
            
            // try to get from the cache, if it doesn't exist, startup an operation to get it
            result = MylarBugsPlugin.getDefault().getCache().getFromCache(bugHandle);
            if(result == null && reportNode != null){
            	return reportNode;
            } else if(result == null && reportNode == null){
            	WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
                	protected void execute(IProgressMonitor monitor) throws CoreException {
                		monitor.beginTask("Downloading Bug# " + id, IProgressMonitor.UNKNOWN);
                        try {
                        	result = BugzillaRepository.getInstance().getCurrentBug(id);
                        }catch(Exception e){
                        	result = null;
                        }
                	}};
                	
                	 // Use the progess service to execute the runnable
                    IProgressService service = PlatformUI.getWorkbench().getProgressService();
                    try {
                    	service.run(false, false, op);
                    } catch (InvocationTargetException e) {
                    	// Operation was canceled
                    } catch (InterruptedException e) {
                    	// Handle the wrapped exception
                    }
                	
            	if(result != null)
            		MylarBugsPlugin.getDefault().getCache().cache(bugHandle, result);
            }

            BugzillaOutlineNode node = BugzillaOutlineNode.parseBugReport(result);
            return findNode(node, commentNumber);
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

    	//check so that we don't need to try to get the parent if we are already at the bug report
    	if(!handle.matches(".*;.*;.*"))
    		return null;
    	
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
        } else if (object instanceof BugzillaReportNode){
        	BugzillaSearchHit hit = ((BugzillaReportNode)object).getHit();
            return  hit.getServer() + ": Bug#: " + hit.getId() + ": " + hit.getDescription();
        }
        return "";
    }

    public boolean canBeLandmark(String handle) {
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

	public List<AbstractRelationshipProvider> getProviders() {
		return providers;
	}
	
	public List<IDegreeOfSeparation> getDegreesOfSeparation() {
		List <IDegreeOfSeparation> separations = new ArrayList<IDegreeOfSeparation>();
		separations.add(new DegreeOfSeparation("disabled", 0));
		separations.add(new DegreeOfSeparation("local, fully qualified matches", 1));
		separations.add(new DegreeOfSeparation("local, unqualified matches", 2));
		separations.add(new DegreeOfSeparation("server, fully quaified matches", 3));
		separations.add(new DegreeOfSeparation("server, unqualified matches", 4));

		return separations;
	}

	public String getHandleForOffsetInObject(Object resource, int offset) {
		return null;
	}

	public void setParentBridge(IMylarStructureBridge bridge) {
		// ignore
	}
}
