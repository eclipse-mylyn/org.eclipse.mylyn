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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.javaeditor.JavaOutlinePage;
import org.eclipse.jdt.internal.ui.viewsupport.DecoratingJavaLabelProvider;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.model.ITaskscapeNode;
import org.eclipse.mylar.core.model.TaskscapeManager;
import org.eclipse.mylar.java.MylarJavaPlugin;
import org.eclipse.mylar.java.search.JUnitReferencesProvider;
import org.eclipse.mylar.java.search.JavaImplementorsProvider;
import org.eclipse.mylar.java.search.JavaReadAccessProvider;
import org.eclipse.mylar.java.search.JavaReferencesProvider;
import org.eclipse.mylar.java.search.JavaWriteAccessProvider;
import org.eclipse.mylar.ui.IMylarUiBridge;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class JavaUiBridge implements IMylarUiBridge {
 
    protected DecoratingJavaLabelProvider labelProvider;
    
    public JavaUiBridge() {
        labelProvider = new DecoratingJavaLabelProvider(new MylarJavaLabelProvider(), true);
//        labelProvider.setLabelDecorator(new DegreeOfInterestDecorator());
    }
    
    public void open(ITaskscapeNode node) {
        //get the element and open it in an editor
        IJavaElement javaElement = JavaCore.create(node.getElementHandle());
        if (javaElement == null) return;
        try {
            IEditorPart part = JavaUI.openInEditor(javaElement);
            JavaUI.revealInEditor(part, javaElement);
        } catch (PartInitException e) {
        	MylarPlugin.log(this.getClass().toString(), e);
        } catch (JavaModelException e) {
        	MylarPlugin.log(this.getClass().toString(), e);
        }
    }

    /**
     * TODO: is there an easier way?  Is this slow?
     * XXX: could close the wrong editor
     */
    public void close(ITaskscapeNode node) {
        try {
            IWorkbenchPage page = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
            if (page != null) {
                IEditorReference[] references = page.getEditorReferences();
                for (int i = 0; i < references.length; i++) {
                    IEditorPart part = references[i].getEditor(false);
                    if (part != null  && part instanceof JavaEditor) {
                        JavaEditor editor = (JavaEditor)part;
                        String name = MylarJavaPlugin.getStructureBridge().getName(
                                MylarJavaPlugin.getStructureBridge().getObjectForHandle(node.getElementHandle()));
                        if (editor.getTitle().equals(name)) editor.close(true);
                    } 
                }
            }
        } catch (Throwable t) {
            MylarPlugin.fail(t, "Could not auto close editor.", false);
        } 
    }
    
    public ILabelProvider getLabelProvider() {
        return labelProvider;
    }

    public boolean acceptsEditor(IEditorPart editorPart) {
        return editorPart instanceof JavaEditor;
    }

    public List<TreeViewer> getTreeViewers(IEditorPart editor) {
        TreeViewer outline = getOutlineTreeViewer(editor);
        if (outline != null) {
            ArrayList<TreeViewer> outlines = new ArrayList<TreeViewer>(1);
            outlines.add(outline);
            return outlines;
        } else {
            return Collections.emptyList();
        }
    }
    
    public TreeViewer getOutlineTreeViewer(IEditorPart editorPart) {
        if (editorPart == null) return null;
        Object out = editorPart.getAdapter(IContentOutlinePage.class);
        if (out instanceof JavaOutlinePage) {
            JavaOutlinePage page = (JavaOutlinePage)out;
            if (page != null && page.getControl() != null && page.getControl().isVisible()) {
                try {
                    Class clazz = page.getClass();
                    Field field = clazz.getDeclaredField("fOutlineViewer"); 
                    field.setAccessible(true); 
                    return (TreeViewer)field.get(page);
                } catch (Exception e) { 
                	MylarPlugin.log(this.getClass().toString(), e);
                }
            }
        }
        return null;
    }

    /**
     * TODO: extract common code?
     */
    public void refreshOutline(final Object element, final boolean updateLabels) {
        Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
            public void run() { 
                if (PlatformUI.getWorkbench() == null || PlatformUI.getWorkbench().getActiveWorkbenchWindow() == null) return;
                IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
                final TreeViewer treeViewer = getOutlineTreeViewer(editorPart);
                if (treeViewer != null) {
                    if (element == null) {
                        treeViewer.refresh(true);
                    } else if (element instanceof IJavaElement) {
                        IJavaElement toRefresh = (IJavaElement)element;
                        if (element instanceof IMember) {
                            toRefresh = toRefresh.getParent();
                        } 
                        treeViewer.refresh(toRefresh, updateLabels); // TODO: use runnable?
                    }
                }
            }
        });  
    }

    public ImageDescriptor getIconForRelationship(String relationshipHandle) {
        if (relationshipHandle.equals(JavaReferencesProvider.ID)) {
            return MylarImages.RELATIONSHIPS_REFS_JAVA; 
        } else if (relationshipHandle.equals(JavaImplementorsProvider.ID)) {
            return MylarImages.RELATIONSHIPS_INHERITANCE_JAVA; 
        } else if (relationshipHandle.equals(JUnitReferencesProvider.ID)) {
            return MylarImages.RELATIONSHIPS_REFS_JUNIT; 
        } else if (relationshipHandle.equals(JavaWriteAccessProvider.ID)) {
            return MylarImages.RELATIONSHIPS_WRITE_JAVA; 
        } else if (relationshipHandle.equals(JavaReadAccessProvider.ID)) {
            return MylarImages.RELATIONSHIPS_READ_JAVA; 
        } else {
            return null;
        }
    }
    
    public String getNameForRelationship(String relationshipHandle) {
        if (relationshipHandle.equals(JavaReferencesProvider.ID)) {
            return JavaReferencesProvider.NAME; 
        } else if (relationshipHandle.equals(JavaImplementorsProvider.ID)) {
            return JavaImplementorsProvider.NAME; 
        } else if (relationshipHandle.equals(JUnitReferencesProvider.ID)) {
            return JUnitReferencesProvider.NAME; 
        } else if (relationshipHandle.equals(JavaWriteAccessProvider.ID)) {
            return JavaWriteAccessProvider.NAME; 
        } else if (relationshipHandle.equals(JavaReadAccessProvider.ID)) {
            return JavaReadAccessProvider.NAME; 
        } else if (relationshipHandle.equals(TaskscapeManager.CONTAINMENT_PROPAGATION_ID)) {
            return "Containment"; // TODO: make this generic? 
        } else {
            return null;
        }
    }
}