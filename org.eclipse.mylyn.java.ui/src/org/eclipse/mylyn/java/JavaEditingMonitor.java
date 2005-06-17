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
 * Created on Apr 5, 2005
  */
package org.eclipse.mylar.java;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.browsing.ProjectsView;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.viewsupport.IProblemChangedListener;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.core.AbstractSelectionMonitor;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.java.search.JavaImplementorsProvider;
import org.eclipse.mylar.java.search.JavaReferencesProvider;
import org.eclipse.mylar.java.ui.editor.ActiveFoldingListener;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Mik Kersten
 */
public class JavaEditingMonitor extends AbstractSelectionMonitor {
    
    protected IJavaElement lastSelectedElement = null;
    protected IJavaElement lastResolvedElement = null;
    protected JavaEditor currentEditor;
    protected StructuredSelection currentSelection = null;
    protected Map<JavaEditor, ActiveFoldingListener> editorListenerMap = new HashMap<JavaEditor, ActiveFoldingListener>();
    
    public JavaEditingMonitor() {
        super();
        JavaPlugin.getDefault().getProblemMarkerManager().addListener(PROBLEM_LISTENER);
        JavaCore.addElementChangedListener(new IElementChangedListener() {
            public void elementChanged(ElementChangedEvent event) {
            	// don't care when an element is changed right now
//                IJavaElementDelta delta = event.getDelta();
//                super.handleElementEdit(part, selectedElement);
            }
        });
    }
    
    private final IProblemChangedListener PROBLEM_LISTENER = new IProblemChangedListener() {
        public void problemsChanged(IResource[] changedResources,
                boolean isMarkerChange) {
            try {
                if (MylarPlugin.getTaskscapeManager().getActiveTaskscape() == null)
                    return;
                for (int i = 0; i < changedResources.length; i++) {
                    IResource resource = changedResources[i];
//                    if (resource instanceof IFile) {
                        try {
                            IMarker[] markers = resource.findMarkers(
                                    IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER,
                                    false, IResource.DEPTH_INFINITE);
                            IJavaElement element = (IJavaElement)resource.getAdapter(IJavaElement.class);
                            boolean hasError = false; 
                            for (int j = 0; j < markers.length; j++) {
                                if (markers[j].getAttribute(IMarker.SEVERITY).equals(IMarker.SEVERITY_ERROR)) {
                                    hasError = true;
                                } 
                            }
                            if (element != null) {
                                if (!hasError) {
                                    MylarPlugin.getTaskscapeManager().removeErrorPredictedInterest(element.getHandleIdentifier(), JavaStructureBridge.EXTENSION, true);
                                } else {
                                    MylarPlugin.getTaskscapeManager().addErrorPredictedInterest(element.getHandleIdentifier(), JavaStructureBridge.EXTENSION, true);
                                }
                            }
                        } catch (ResourceException e) {
                            // ignore missing resources
                        }
                }
                // XXX: notify, but using the correct node
//                MylarPlugin.getTaskscapeManager().notifyPostPresentationSettingsChange(ITaskscapeListener.UpdateKind.UPDATE); // HACK: wrong notification
            } catch (Exception e) {
            	MylarPlugin.log(this.getClass().toString(), e);
            }
        }
    };
    
    /**
     * Only public for testing
     */
    @Override
    public void handleWorkbenchPartSelection(IWorkbenchPart part, ISelection selection) {
        try {
            if (MylarUiPlugin.getDefault().isGlobalFilteringEnabled() && part instanceof ProjectsView) return;

            IJavaElement selectedElement = null;
            if (selection instanceof StructuredSelection) {
                StructuredSelection structuredSelection = (StructuredSelection)selection;
             
                if (structuredSelection.equals(currentSelection)) return;
                currentSelection = structuredSelection;
              
                Object selectedObject = structuredSelection.getFirstElement();
                if (selectedObject instanceof IJavaElement) {
                    IJavaElement checkedElement = checkIfAcceptedAndPromoteIfNecessary((IJavaElement)selectedObject);
                    if (checkedElement == null) {
                        return;
                    } else {
                        selectedElement = checkedElement; 
                    }
                } 
                if (selectedElement != null) super.handleElementSelection(part, selectedElement);
            } else {
                if (selection instanceof TextSelection && part instanceof JavaEditor) {
                    currentEditor = (JavaEditor)part;
                    registerEditor(currentEditor);
                    TextSelection textSelection = (TextSelection)selection;
                    selectedElement = SelectionConverter.resolveEnclosingElement(currentEditor, textSelection);
                    if (selectedElement instanceof IPackageDeclaration) return; // HACK: ignoring these selections
                    IJavaElement[] resolved = SelectionConverter.codeResolve(currentEditor);
                    if (resolved != null && resolved.length == 1 && !resolved[0].equals(selectedElement)) {
                        lastResolvedElement = resolved[0];
                    } 
                         
                    boolean selectionResolved = false;
                    if (selectedElement instanceof IMethod && lastSelectedElement instanceof IMethod) {
                        if (lastResolvedElement != null && lastSelectedElement != null
                            && lastResolvedElement.equals(selectedElement)
                            && !lastSelectedElement.equals(lastResolvedElement)) { 
                            super.handleNavigation(part, selectedElement, JavaReferencesProvider.ID);
                            selectionResolved = true;
                        } else if (lastSelectedElement != null 
                            && lastSelectedElement.equals(lastResolvedElement)
                            && !lastSelectedElement.equals(selectedElement)) {
                            super.handleNavigation(part, selectedElement, JavaReferencesProvider.ID);
                            selectionResolved = true;
                        }
                    } else if (selectedElement != null && lastSelectedElement != null && !lastSelectedElement.equals(selectedElement)) {
                        if (lastSelectedElement.getElementName().equals(selectedElement.getElementName())) {
                            if (selectedElement instanceof IMethod && lastSelectedElement instanceof IMethod) { 
                                super.handleNavigation(part, selectedElement, JavaImplementorsProvider.ID);
                                selectionResolved = true;
                            } else if (selectedElement instanceof IType && lastSelectedElement instanceof IType) { 
                                super.handleNavigation(part, selectedElement, JavaImplementorsProvider.ID);
                                selectionResolved = true;
                            }
                        }
                    } 
                    if (selectedElement != null) {
                        if (!selectionResolved && selectedElement.equals(lastSelectedElement))  {
                            super.handleElementEdit(part, selectedElement);
                        } else {
                            super.handleElementSelection(part, selectedElement);
                        }
                    }
                                        
                    IJavaElement checkedElement = checkIfAcceptedAndPromoteIfNecessary(selectedElement);
                    if (checkedElement == null) {
                        return;
                    } else {
                        selectedElement = checkedElement; 
                    }            
                }
            }   
            if (selectedElement != null) lastSelectedElement = selectedElement;
        } catch (Throwable t) {
            MylarPlugin.fail(t, "Failed to update model based on selection.", false); 
        }
    }
    
    private void registerEditor(JavaEditor editor) {
        if (editorListenerMap.containsKey(editor)) {
            return;
        } else {
            ActiveFoldingListener listener = new ActiveFoldingListener(this, editor);
            editorListenerMap.put(editor, listener);
            MylarPlugin.getTaskscapeManager().addListener(listener);
        }        
    }
    
    public void unregisterEditor(JavaEditor editor) {
        ActiveFoldingListener listener = editorListenerMap.get(editor);
        MylarPlugin.getTaskscapeManager().removeListener(listener);
        editorListenerMap.remove(editor);
    }

    /**
     * @return null for elements that aren't modeled
     */
    protected IJavaElement checkIfAcceptedAndPromoteIfNecessary(IJavaElement element) {
        if (element instanceof IPackageDeclaration) return null;
        if (element instanceof IImportContainer) {
            return element.getParent();
        } else if (element instanceof IImportDeclaration) {
            return element.getParent().getParent();
        } else {
            return element;
        }
    }
}
