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
package org.eclipse.mylar.xml.ant.ui;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.ant.internal.ui.editor.AntEditor;
import org.eclipse.ant.internal.ui.editor.outline.AntEditorContentOutlinePage;
import org.eclipse.ant.internal.ui.model.AntElementNode;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.model.ITaskscapeNode;
import org.eclipse.mylar.ui.IMylarUiBridge;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.mylar.xml.MylarXmlPlugin;
import org.eclipse.mylar.xml.XmlNodeHelper;
import org.eclipse.mylar.xml.XmlReferencesProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class AntUiBridge implements IMylarUiBridge {

    protected AntNodeLabelProvider labelProvider = new AntNodeLabelProvider();
    
    /**
     * @see org.eclipse.mylar.ui.IMylarUiBridge#open(org.eclipse.mylar.core.model.ITaskscapeNode)
     */
    public void open(ITaskscapeNode node) {
        // get the handle of the node
        String handle = node.getElementHandle();
        
        int first = handle.indexOf(";");
        String filename = "";
        if(first == -1)
            filename = handle;
        else
            filename = handle.substring(0, first);
        
        try{
            // get the file
            IPath path = new Path(filename);
            IFile f = (IFile)((Workspace)ResourcesPlugin.getWorkspace()).newResource(path, IResource.FILE);
            
            // open the xml file looking at the proper line
            IEditorPart editor = openInEditor(f, true);
         
            // if the editor is null, we had a problem and should return
            if(editor == null){
                MylarPlugin.log("Unable to open editor for file: " + filename, this);
                return;
            }
            
            // get the contents and create a new document so that we can get
            // the offsets to highlight
            String content = XmlNodeHelper.getContents(f.getContents());
            
            IDocument d = new Document(content);

            if(first != -1){
                int start = Integer.parseInt(handle.substring(first + 1));

                // get the offsets for the element
                int startOffset = d.getLineOffset(start);
                int length = 0;
                
                // set the selection if the selection provider is not null
                ISelectionProvider selectionProvider = editor.getEditorSite().getSelectionProvider();
                if(selectionProvider != null)
                    selectionProvider.setSelection(new TextSelection(startOffset, length));
            }
            

        } catch(Exception e){
            MylarPlugin.fail(e, "ERROR OPENING XML EDITOR\n" + e.getMessage(), false);
        }
    }
    
    /**
     * Open a file in the appropriate editor
     * @param file The IFile to open
     * @param activate Whether to activate the editor or not
     * @return The IEditorPart that the file opened in
     * @throws PartInitException
     */
    private IEditorPart openInEditor(IFile file, boolean activate) throws PartInitException {
        if (file != null) {
            IWorkbenchPage p= MylarXmlPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
            if (p != null) {
                IEditorPart editorPart= IDE.openEditor(p, file, activate);
//                initializeHighlightRange(editorPart);
                return editorPart;
            }
        }
        return null;
    }

    public ILabelProvider getLabelProvider() {
        return labelProvider;
    }

    public void close(ITaskscapeNode node) {
        IWorkbenchPage page = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
        if (page != null) {
            IEditorReference[] references = page.getEditorReferences();
            for (int i = 0; i < references.length; i++) {
                IEditorPart part = references[i].getEditor(false);
                if (part != null) {
                        if (part.getEditorInput() instanceof IFileEditorInput) {
                        IFileEditorInput input = (IFileEditorInput)part.getEditorInput();
                        if ((input.getFile().getFullPath().toString()).equals(node.getElementHandle())) {
                            if (part instanceof FormEditor) {
                                ((FormEditor)part).close(true);
                            } else if (part instanceof AbstractTextEditor) {
                                ((AbstractTextEditor)part).close(true);
                            }   
                        }
                    } 
                }
            }
        }
    }

    public boolean acceptsEditor(IEditorPart editorPart) {
        return editorPart instanceof AntEditor;
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
    
    public TreeViewer getOutlineTreeViewer(IEditorPart editor) {
        // HACK use reflection to get the TreeViewer
        if(editor instanceof AntEditor){
            try{
                AntEditor ae = (AntEditor)editor;
                AntEditorContentOutlinePage outline = (AntEditorContentOutlinePage)ae.getAdapter(IContentOutlinePage.class);
                Class clazz = ContentOutlinePage.class;
                Method method= clazz.getDeclaredMethod("getTreeViewer", new Class[] { });
                method.setAccessible(true);
                return (TreeViewer)method.invoke(outline, new Object[] { });
            } catch (Exception e) {
            	MylarPlugin.log(e, "couldn't get outline");
                return null;
            }
        }
        return null;
    }

    public void refreshOutline(Object element, boolean updateLabels) {

        IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        TreeViewer treeViewer = getOutlineTreeViewer(editorPart);
        if (treeViewer != null) {
            if (element == null) {
                treeViewer.refresh(true);
            } else if (element instanceof AntElementNode) {
                treeViewer.refresh(true); 
                if(((StructuredSelection)treeViewer.getSelection()).getFirstElement() != element)
                    treeViewer.setSelection(new StructuredSelection(element));
            }
        }
    }
    
    public ImageDescriptor getIconForRelationship(String relationshipHandle) {
        return MylarImages.RELATIONSHIPS_REFS_XML;
    }

    public String getNameForRelationship(String relationshipHandle) {
        return XmlReferencesProvider.NAME;        
    }
}