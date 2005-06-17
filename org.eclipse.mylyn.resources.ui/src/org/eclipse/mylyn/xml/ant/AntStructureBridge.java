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
 * Created on Apr 20, 2005
  */
package org.eclipse.mylar.xml.ant;

import org.eclipse.ant.internal.ui.editor.text.AntAnnotationModel;
import org.eclipse.ant.internal.ui.model.AntElementNode;
import org.eclipse.ant.internal.ui.model.AntModel;
import org.eclipse.ant.internal.ui.model.IProblemRequestor;
import org.eclipse.ant.internal.ui.model.LocationProvider;
import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.xml.XmlNodeHelper;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.markers.internal.ProblemMarker;


/**
 * @author Mik Kersten
 * 
 */
public class AntStructureBridge implements IMylarStructureBridge {

    public final static String EXTENSION = "build.xml";
    
    private final IMylarStructureBridge parentBridge;
    
    public AntStructureBridge(IMylarStructureBridge parentBridge) {
        this.parentBridge = parentBridge;
    }
    
    /**
     * @see org.eclipse.mylar.core.IMylarStructureBridge#getResourceExtension()
     */
    public String getResourceExtension() {
        return EXTENSION;
    }
    
    public String getResourceExtension(String elementHandle) {
        if (elementHandle.endsWith(".xml")) {
            return parentBridge.getResourceExtension();
        } else {
            return EXTENSION;
        }
    }
    
    /**
     * @see org.eclipse.mylar.core.IMylarStructureBridge#getParentHandle(java.lang.String)
     */
    public String getParentHandle(String handle) {
        Object o = getObjectForHandle(handle);
        
        // we can only get the parent if the element is an AntElementNode
        if(o instanceof AntElementNode){
            
            // try to get the parent node
            AntElementNode parent = ((AntElementNode)o).getParentNode();
            
            if(parent != null){
                // get the handle for the parent node
                return getHandleIdentifier(parent);
            }
            else{
                // if the parent is null, we just need to return the handle for the file
                int delimeterIndex = handle.indexOf(";");
                if (delimeterIndex != -1) {
                    String parentHandle = handle.substring(0, delimeterIndex);
                    return parentHandle;
                }else{
                    return null;
                }
            }
        }else if(o instanceof IFile){
            return parentBridge.getParentHandle(handle);
        } else{
            // return null if we can't get a parents
            return null;
        }
    }

    /**
     * @see org.eclipse.mylar.core.IMylarStructureBridge#getObjectForHandle(java.lang.String)
     */
    public Object getObjectForHandle(String handle) {
        int first = handle.indexOf(";");
        String filename = "";
        if(first == -1){
            // we have just the filename, so return the IFile for this filename
            filename = handle;
            IPath path = new Path(filename);
            IFile f = (IFile)((Workspace)ResourcesPlugin.getWorkspace()).newResource(path, IResource.FILE);
            return f;
        }
        else{
            // we have an element since there is a line number
            // get the filename from the handle
            filename = handle.substring(0, first);
        }
        
        try{
            // get the file and create a new FileEditorInput
            IPath path = new Path(filename);
            IFile f = (IFile)((Workspace)ResourcesPlugin.getWorkspace()).newResource(path, IResource.FILE);
            FileEditorInput fei = new FileEditorInput(f);
            
            // get the line number that the element is on
            int start = Integer.parseInt(handle.substring(first + 1));

            // get the contents of the file and create a document so that we can get the offset
            String content = XmlNodeHelper.getContents(f.getContents());
            IDocument d = new Document(content);
            
            // get the offsets for the element and make sure that we are on something other than whitespace
            int startOffset = d.getLineOffset(start);
            while(d.getChar(startOffset) == ' ')
                startOffset++;
            
//XXX needed if the editor is the only way to get the model
//            get the active editor, which should be the ant editor so we can get the AntModel
//            IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
//            if(editorPart instanceof AntEditor){
//                AntModel am = ((AntEditor)editorPart).getAntModel(); 
//                if(am != null)
//                    
//                    // from the AntModel, get the node that we want
//                    return am.getNode(startOffset + 1, false);    
//            }else{
            
            // create the ant model and get the element from it 
            IProblemRequestor p = new AntAnnotationModel(f);
            AntModel am = new AntModel(d, p, new LocationProvider(fei));
            am.reconcile();
            return am.getNode(startOffset + 1, false);
//            }
//            return null;
            
        }catch(Exception e){
        	MylarPlugin.log(this.getClass().toString(), e);
        }
        return null;
    }
    
    /**
     * @see org.eclipse.mylar.core.IMylarStructureBridge#getHandleIdentifier(java.lang.Object)
     */
    public String getHandleIdentifier(Object object) {
        // we can only create handles for AntElementNodes and build.xml Files
        
        if (object instanceof XmlNodeHelper) {
            return ((XmlNodeHelper)object).getHandle();
        } else if (object instanceof AntElementNode) {
            AntElementNode node = (AntElementNode)object;
            try{
                // get the handle for the AntElementNode from the helper
                String handle = new XmlNodeHelper(new FileEditorInput(node.getIFile()), node.getOffset()).getHandle();
                return handle;
            }catch(Exception e){
            	MylarPlugin.log(this.getClass().toString(), e);
            }
            
        }else if (object instanceof File) {
            File file = (File)object;
            
            // get the handle for the build.xml file
            if (file.getFullPath().toString().endsWith("build.xml")) return file.getFullPath().toString();
        }
        return null;
    }

    /**
     * @see org.eclipse.mylar.core.IMylarStructureBridge#getName(java.lang.Object)
     */
    public String getName(Object object) {
        if(object instanceof AntElementNode){
            AntElementNode n = (AntElementNode)object;
            return n.getIFile().getName() + " " + n.getName();
        }else if (object instanceof File) {
            File file = (File)object;
            if (file.getFullPath().toString().endsWith("build.xml")) return "build.xml";
        }
        return "";
    }

    /**
     * @see org.eclipse.mylar.core.IMylarStructureBridge#acceptAsLandmark(java.lang.String)
     */
    public boolean acceptAsLandmark(String handle) {
        return true;
    }

    /**
     * @see org.eclipse.mylar.core.IMylarStructureBridge#acceptsObject(java.lang.Object)
     */
    public boolean acceptsObject(Object object) {
        // we accept AntElementNode and build.xml File objects
        if (object instanceof XmlNodeHelper || object instanceof AntElementNode) {
            return true;
        } else if (object instanceof File) {
            File file = (File)object;
            if (file.getFullPath().toString().endsWith("build.xml")) return true;
        }
        return false;
    }

    /**
     * @see org.eclipse.mylar.core.IMylarStructureBridge#canFilter(java.lang.Object)
     */
    public boolean canFilter(Object element) {
        return true;
    }

    /**
     * @see org.eclipse.mylar.core.IMylarStructureBridge#isDocument(java.lang.String)
     */
    public boolean isDocument(String handle) {
        return handle.indexOf(';') == -1;
    }

    /**
     * @see org.eclipse.mylar.core.IMylarStructureBridge#getHandleForMarker(org.eclipse.ui.views.markers.internal.ProblemMarker)
     */
    public String getHandleForMarker(ProblemMarker marker) {
        // we can only return a handle if the resource is build.xml
        if (marker == null) return null;
        try {
            IResource res= marker.getResource();
            
            if (res instanceof IFile) {
                IFile file = (IFile)res; 
                if (file.getFullPath().toString().endsWith("build.xml")) { 
                    return file.getFullPath().toString();
                } else {
                    return null;
                }
            }
            return null;
        }
        catch (Throwable t) {
            MylarPlugin.fail(t, "Could not find element for: " + marker, false);
            return null;
        }
    }

	public IProject getProjectForObject(Object object) {
		while(!(object instanceof IFile)){
    		String handle = getParentHandle(getHandleIdentifier(object));
    		if(handle == null)
    			break;
    		object = getObjectForHandle(handle);
    	}
    	if(object instanceof IFile && acceptsObject(object)){
    		return((IFile)object).getProject();
    	}
		return null;
	}
}
