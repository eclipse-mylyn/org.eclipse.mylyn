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
 * Created on Apr 21, 2005
  */
package org.eclipse.mylar.core.resources;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.ui.views.markers.internal.ProblemMarker;


/**
 * @author Mik Kersten
 */
public class ResourceStructureBridge implements IMylarStructureBridge {

    public final static String EXTENSION = "*";

    public ResourceStructureBridge(){
    	IWorkspace workspace = ResourcesPlugin.getWorkspace();
   	   	workspace.addResourceChangeListener(new ResourceMarkerListener());

    }
    
    public String getResourceExtension() {
        return EXTENSION;
    }
    
    public String getParentHandle(String handle) {
        IResource resource = (IResource)getObjectForHandle(handle);
        if (resource != null) {
            return getHandleIdentifier(resource.getParent());
        } else {
            return null;
        }
    }

    public String getHandleIdentifier(Object object) {
        if (object instanceof IProject) {
            String path = ((IResource)object).getFullPath().toPortableString();
            String javaCoreStylePath = "=" + path.substring(1);
            return javaCoreStylePath;
        }
        if (object instanceof IResource) {
            return ((IResource)object).getFullPath().toPortableString();
        } else {
            return null;
        }
    }
    
    public Object getObjectForHandle(String handle) {
        IPath path = new Path(handle);
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        if (path.segmentCount() == 1) {
            String projectName = handle.substring(1);
            return workspace.getRoot().getProject(projectName);
        } else if (path.segmentCount() > 1){
            return workspace.getRoot().getFile(path);
        } else {
            return null;
        }
    }

    public String getName(Object object) {
        if (object instanceof IResource) {
            return ((IResource)object).getName();
        } else {
            return "";
        }
    }

    public boolean canBeLandmark(Object element) {
        return true;
    }

    public boolean acceptsObject(Object object) {
        return object instanceof IResource;
    }

    public boolean canFilter(Object element) {
        return true;
    }

    public boolean isDocument(String handle) {
        return getObjectForHandle(handle) instanceof IFile;
    }

    public String getHandleForMarker(ProblemMarker marker) {
        // we can only get a handle for a marker with the resource plugin.xml
        if (marker == null) return null;
        try {
            IResource res= marker.getResource();

            if (res instanceof IFile) {
                IFile file = (IFile)res; 
                return getHandleIdentifier(file);
            }
            return null;
        }
        catch (Throwable t) {
            MylarPlugin.log(t, "Could not find element for: " + marker);
            return null;
        }
    }

	public IProject getProjectForObject(Object object) {
		if(object instanceof IResource){
			return ((IResource)object).getProject();
		}
		return null;
	}

    public String getResourceExtension(String elementHandle) {
        return getResourceExtension();
    }
}
