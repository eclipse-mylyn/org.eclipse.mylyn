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
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.javaeditor.JavaOutlinePage;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.IMylarUiBridge;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author Mik Kersten
 */
public class JavaUiBridge implements IMylarUiBridge {
 
    public void open(IMylarContextNode node) {
        //get the element and open it in an editor
        IJavaElement javaElement = JavaCore.create(node.getElementHandle());
        if (javaElement == null || !javaElement.exists()) return;
        try {
            IEditorPart part = JavaUI.openInEditor(javaElement);
            JavaUI.revealInEditor(part, javaElement);
        } catch (Throwable t) {
        	MylarPlugin.fail(t, "Could not open editor for: " + node, true);
        }
    }

    /**
     * TODO: implement if needed
     */
    public void close(IMylarContextNode node) {
        try {
            IWorkbenchPage page = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
            if (page != null) {
                IEditorReference[] references = page.getEditorReferences();
                for (int i = 0; i < references.length; i++) {
                    IEditorPart part = references[i].getEditor(false);
                    if (part != null  && part instanceof JavaEditor) {
                    	MylarPlugin.log("editor closing not implemented", this);
//                        JavaEditor editor = (JavaEditor)part;
//                        String name = MylarJavaPlugin.getStructureBridge().getName(
//                                MylarJavaPlugin.getStructureBridge().getObjectForHandle(node.getElementHandle()));
//                        if (editor.getTitle().equals(name)) editor.close(true);
                    } 
                }
            }
        } catch (Throwable t) {
            MylarPlugin.fail(t, "Could not auto close editor.", false);
        } 
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
                	MylarPlugin.log(e, "could not get outline viewer");
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
}