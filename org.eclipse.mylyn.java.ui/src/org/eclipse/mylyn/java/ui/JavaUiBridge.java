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
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.javaeditor.JavaOutlinePage;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.IMylarUiBridge;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author Mik Kersten
 */
public class JavaUiBridge implements IMylarUiBridge {
 
    public void open(IMylarElement node) {
        //get the element and open it in an editor
        IJavaElement javaElement = JavaCore.create(node.getHandleIdentifier());
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
    public void close(IMylarElement node) {
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

	public List<TreeViewer> getContentOutlineViewers(IEditorPart editorPart) {
        if (editorPart == null) return null;
        List<TreeViewer> viewers = new ArrayList<TreeViewer>();
        Object out = editorPart.getAdapter(IContentOutlinePage.class);
        if (out instanceof JavaOutlinePage) {
            JavaOutlinePage page = (JavaOutlinePage)out;
            if (page != null && page.getControl() != null) {
                try {
                    Class clazz = page.getClass();
                    Field field = clazz.getDeclaredField("fOutlineViewer"); 
                    field.setAccessible(true); 
                    viewers.add((TreeViewer)field.get(page));
                } catch (Exception e) { 
                	MylarPlugin.log(e, "could not get outline viewer");
                } 
            }
        }
        return viewers;
    }

	public Object getObjectForTextSelection(TextSelection selection, IEditorPart editor) {
		if (editor instanceof JavaEditor) {
            TextSelection textSelection = (TextSelection)selection;
            try {
				return SelectionConverter.resolveEnclosingElement((JavaEditor)editor, textSelection);
			} catch (JavaModelException e) {
				// ignore
			}
        }
		return null;
	}

//    public void refreshOutline(final Object element, final boolean updateLabels, final boolean setSelection) {
//        Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
//            public void run() { 
//                if (PlatformUI.getWorkbench() == null || PlatformUI.getWorkbench().getActiveWorkbenchWindow() == null) return;
//                IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
//                final TreeViewer treeViewer = getOutlineTreeViewer(editorPart);
//                if (treeViewer != null) {
//                    if (element == null) {
//                    	treeViewer.getControl().setRedraw(false);
//    	                treeViewer.refresh(true);
//    	                treeViewer.getControl().setRedraw(true);
//                    } else if (element instanceof IJavaElement) {
//                        IJavaElement toRefresh = (IJavaElement)element;
//                        if (element instanceof IMember) {
//                            toRefresh = toRefresh.getParent();
//                        } 
//                        treeViewer.getControl().setRedraw(false);
//                        treeViewer.refresh(toRefresh, updateLabels); 
//    	                treeViewer.getControl().setRedraw(true);
//                    }
//                    if (setSelection) {
//    	                if(((StructuredSelection)treeViewer.getSelection()).getFirstElement() != element)
//    	                    treeViewer.setSelection(new StructuredSelection(element));
//                    }
//                }
//            }
//        });  
//    }
}