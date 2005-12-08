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
 * Created on Apr 6, 2005
  */
package org.eclipse.mylar.java.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.ui.JavaElementImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.util.ErrorLogger;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.views.markers.internal.ConcreteMarker;


public class JavaUiUtil {

    public static Font getFontForElement(IJavaElement element) { 
//      if (MylarPlugin.getTaskscapeManager().getActiveNode() == null) return null;
//      IJavaElement activeElement = JavaCore.create(MylarPlugin.getTaskscapeManager().getActiveNode().getElementHandle());
//      if (activeElement == null) return null;
//    
      if (element == null) return null;
      IMylarElement info = MylarPlugin.getContextManager().getElement(element.getHandleIdentifier());
      if (info.getInterest().isLandmark() && !info.getInterest().isPropagated()) {
          return MylarUiPlugin.BOLD;
      }
//      if (info.getDegreeOfInterest().getDegreeOfInterest().isPredicted()) return UiUtil.ITALIC;
      return null;
  }
  
    private static final Point SMALL_SIZE= new Point(16, 16); 
    public static ImageDescriptor decorate( ImageDescriptor base, int decorations ) {
        ImageDescriptor imageDescriptor = new JavaElementImageDescriptor( base, decorations, SMALL_SIZE);   
        return imageDescriptor;
    }
    
    public static IJavaElement getJavaElement(ConcreteMarker marker) {
    	if (marker == null)	return null;
    	try {
    		IResource res= marker.getResource();
    		ICompilationUnit cu = null;
    		if (res instanceof IFile) {
    		    IFile file = (IFile)res; 
    		    if (file.getFileExtension().equals("java")) { // TODO: instanceof instead?
    		        cu = JavaCore.createCompilationUnitFrom(file);
    		    } else {
    		        return null;
    		    }
    		}
            if (cu != null) {  
                IJavaElement je= cu.getElementAt(marker.getMarker().getAttribute(IMarker.CHAR_START, 0));
                return je;
            } else {
                return null;
            }
    	} catch (JavaModelException ex) {
    		if (!ex.isDoesNotExist())
    			ExceptionHandler.handle(ex, "error", "could not find java element"); //$NON-NLS-2$ //$NON-NLS-1$
    		return null;  
    	} catch (Throwable t) {
    	    ErrorLogger.fail(t, "Could not find element for: " + marker, false);
    	    return null;
    	}
    }
    
    /**
     * Get the fully qualified name of a IMember
     * 
     * @param m
     *            The IMember to get the fully qualified name for
     * @return String representing the fully qualified name
     */
    public static String getFullyQualifiedName(IJavaElement je) {
        if(!(je instanceof IMember)) return null;
        
        IMember m = (IMember)je;
        if (m.getDeclaringType() == null)
            return ((IType) m).getFullyQualifiedName();
        else
            return m.getDeclaringType().getFullyQualifiedName() + "."
                    + m.getElementName();
    }
    
    //	public static void openFile(IPath path) {
    ////		PathfinderView.userActionListener.incrementNumPathfinderselection();
    //		final IFile ir = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
    //		if (ir != null && ir.exists()) {
    //			try {
    //				IEditorPart editorPart= IDE.openEditor(
    //					Workbench.getInstance().getActiveWorkbenchWindow().getActivePage(), 
    //					ir, true); 
    //				editorPart.
    //			} catch (CoreException ce) {
    //				ce.printStackTrace();
    //			}
    //		} else {
    //		    MylarPlugin.fail(null, "Couldn't open pathfile: " + path.toString(), true);
    //		}
    //	}
    public static void closeActiveEditors(boolean javaOnly) {
        IWorkbenchPage page = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
        if (page != null) {
            IEditorReference[] references = page.getEditorReferences();
    	    for (int i = 0; i < references.length; i++) {
    	        IEditorPart part = references[i].getEditor(false);
    	        if (part != null) {
                        if (javaOnly
    			            && part.getEditorInput() instanceof IFileEditorInput
    			            && part instanceof JavaEditor) {
    		            JavaEditor editor = (JavaEditor)part;
    		            editor.close(true);
    		        } else if (part instanceof JavaEditor) {
                        ((AbstractTextEditor)part).close(true);
                    }
                }
    	    }
        }
    }
}
