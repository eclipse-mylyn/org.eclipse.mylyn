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
 * Created on Jun 10, 2005
  */
package org.eclipse.mylar.monitor.monitors;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.eclipse.core.internal.preferences.Base64;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.core.AbstractUserInteractionMonitor;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.MylarContextManager;
import org.eclipse.mylar.core.util.ErrorLogger;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.EditorPart;

/**
 * Limited to Java selections.
 * 
 * @author Mik Kersten
 */
public class SelectionMonitor extends AbstractUserInteractionMonitor {

    public static final String SELECTION_DEFAULT = "selected";
    public static final String SELECTION_NEW = "new";
    public static final String SELECTION_DECAYED = "decayed";
    public static final String SELECTION_PREDICTED = "predicted";
	private IJavaElement lastSelectedElement = null;
        
    @Override
    protected void handleWorkbenchPartSelection(IWorkbenchPart part, ISelection selection) {
        // ignored, since not using taskscape monitoring facilities
    }
    
    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        String structureKind = "?";
        String obfuscatedElementHandle = "?";
        String elementHandle = "?";
        InteractionEvent.Kind interactionKind = InteractionEvent.Kind.SELECTION;
        if (selection instanceof StructuredSelection) {
            StructuredSelection structuredSelection = (StructuredSelection)selection;       
            Object selectedObject = structuredSelection.getFirstElement();
            if (selectedObject == null) return;
            if (selectedObject instanceof IJavaElement) {
                IJavaElement javaElement = (IJavaElement)selectedObject;
                structureKind = "java:" + javaElement.getClass();
                elementHandle = javaElement.getHandleIdentifier();
                obfuscatedElementHandle = obfuscateJavaElementHandle(javaElement);
                lastSelectedElement = javaElement;
            } else {
                structureKind = "?: " + selectedObject.getClass();
                if (selectedObject instanceof IAdaptable) {
                    IResource resource = (IResource)((IAdaptable)selectedObject).getAdapter(IResource.class);
                    if (resource != null) {
                        obfuscatedElementHandle = obfuscateResourcePath(resource.getProjectRelativePath());
                    }
                }
            }
        } else {
            if (selection instanceof TextSelection && part instanceof JavaEditor) {
                TextSelection textSelection = (TextSelection)selection;
                IJavaElement javaElement;
                try {
                    javaElement = SelectionConverter.resolveEnclosingElement((JavaEditor)part, textSelection);
                    if (javaElement != null) {
	                    structureKind = "java:" + javaElement.getClass();
	                    obfuscatedElementHandle = obfuscateJavaElementHandle(javaElement);
	                    elementHandle = javaElement.getHandleIdentifier();
	                    if (javaElement != null && javaElement.equals(lastSelectedElement))  {
	                        interactionKind = InteractionEvent.Kind.EDIT;
	                    }
	                    lastSelectedElement = javaElement;
                    }
                } catch (JavaModelException e) {
                	// ignore unresolved elements
//                    MylarPlugin.log("Could not resolve java element from text selection.", this);
                }
            } else if (part instanceof EditorPart) {
                EditorPart editorPart = (EditorPart)part;
                IEditorInput input = editorPart.getEditorInput();
                if (input instanceof IPathEditorInput) {
                    structureKind = "file";
                    obfuscatedElementHandle = obfuscateResourcePath(((IPathEditorInput)input).getPath());
                }
            }
        }
        IMylarElement node = MylarPlugin.getContextManager().getElement(elementHandle);
        String delta = "";
        float selectionFactor = MylarContextManager.getScalingFactors().get(InteractionEvent.Kind.SELECTION).getValue();
        
        // XXX: broken in 0.4?
        if (node != null) {
        	if (node.getInterest().getEncodedValue() <= selectionFactor
        		&& node.getInterest().getValue() >  selectionFactor) { 
        		delta = SELECTION_PREDICTED;
    		} else if (node.getInterest().getEncodedValue() < selectionFactor
    				&& node.getInterest().getDecayValue() > selectionFactor) {
    			delta = SELECTION_DECAYED;
    		} else if (node.getInterest().getValue() == selectionFactor
    				&& node.getInterest().getDecayValue() < selectionFactor) {
    			delta = SELECTION_NEW;
    		} else {
    			delta = SELECTION_DEFAULT;
    		} 
        }
        
        InteractionEvent event = new InteractionEvent(
        		interactionKind, 
        		structureKind, 
        		obfuscatedElementHandle, 
        		part.getSite().getId(), 
        		"null", 
        		delta, 
        		0); 
        MylarPlugin.getDefault().notifyInteractionObserved(event);
    }

    private String obfuscateResourcePath(IPath path) {
        StringBuffer obfuscatedPath = new StringBuffer();
        for (int i = 0; i < path.segmentCount(); i++) {
            obfuscatedPath.append(obfuscateString(path.segments()[i]));
            if (i < path.segmentCount()-1) obfuscatedPath.append('/');
        }
        return obfuscatedPath.toString();
    }

    /**
     * Encrypts the string using SHA, then makes it reasonable to print.
     */
    private String obfuscateString(String string) {	
    	String obfuscatedString = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
	    	md.update(string.getBytes());
	    	byte[] digest = md.digest();
	    	obfuscatedString = new String(Base64.encode(digest)).replace('/', '=');
//			obfuscatedString = "" + new String(digest).hashCode();
		} catch (NoSuchAlgorithmException e) {
			ErrorLogger.log("SHA not available", this);
			obfuscatedString = "<failed to obfuscate>"; 
		}
		return obfuscatedString;
    }

    private String obfuscateJavaElementHandle(IJavaElement javaElement) {
        try {
            StringBuffer obfuscatedPath = new StringBuffer();
            IResource resource;
            resource = (IResource)javaElement.getUnderlyingResource();
            if (resource != null &&(resource instanceof IFile)) {
                IFile file = (IFile)resource;
                obfuscatedPath.append(obfuscateResourcePath(file.getProjectRelativePath()));
                obfuscatedPath.append(':');
                obfuscatedPath.append(obfuscateString(javaElement.getElementName()));
                return obfuscatedPath.toString();
            }
        } catch (JavaModelException e) { 
        	// ignore non-existing element
//            MylarPlugin.log(this, "failed to resolve java element for element: " + javaElement.getHandleIdentifier());
        }
        return "(non-source element)";
    }
}
