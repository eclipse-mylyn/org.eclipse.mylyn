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
package org.eclipse.mylar.xml.pde;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.IMylarUiBridge;
import org.eclipse.mylar.xml.MylarXmlPlugin;
import org.eclipse.pde.internal.ui.editor.FormOutlinePage;
import org.eclipse.pde.internal.ui.editor.ISortableContentOutlinePage;
import org.eclipse.pde.internal.ui.editor.PDEFormEditor;
import org.eclipse.pde.internal.ui.editor.PDESourcePage;
import org.eclipse.pde.internal.ui.editor.SourceOutlinePage;
import org.eclipse.pde.internal.ui.editor.plugin.ManifestEditor;
import org.eclipse.pde.internal.ui.editor.plugin.PluginInputContext;
import org.eclipse.pde.internal.ui.model.plugin.PluginObjectNode;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.texteditor.AbstractTextEditor;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class PdeUiBridge implements IMylarUiBridge {

	private TreeViewerListener treeSelectionChangedListener;
    
    public PdeUiBridge(){
    	treeSelectionChangedListener = new TreeViewerListener();
    }
    
    /**
     * @see org.eclipse.mylar.ui.IMylarUiBridge#open(org.eclipse.mylar.core.IMylarElement)
     */
    public void open(IMylarElement node) {
        // get the handle of the node
        String handle = node.getHandleIdentifier();
        
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
//            String content = XmlNodeHelper.getContents(f.getContents());
//            
//            IDocument d = new Document(content);

//            if(first != -1){
//                int start = Integer.parseInt(handle.substring(first + 1));
//                
//                // get the offsets for the element
//                int startOffset = d.getLineOffset(start);
//                int length = 0;
//                
//                // set the selection if the selection provider is not null
//                ISelectionProvider selectionProvider = editor.getEditorSite().getSelectionProvider();
//                if(selectionProvider != null)
//                    selectionProvider.setSelection(new TextSelection(startOffset, length));
//            }
            

        } catch(Exception e){
//            MylarPlugin.log(e, "ERROR OPENING XML EDITOR\n" + e.getMessage());
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
            if (p != null && file.exists()) {
                IEditorPart editorPart= IDE.openEditor(p, file, activate);
//                initializeHighlightRange(editorPart);
                return editorPart;
            }
        }
        return null;
    }

    public void close(IMylarElement node) {
        IWorkbenchPage page = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
        if (page != null) {
            IEditorReference[] references = page.getEditorReferences();
            for (int i = 0; i < references.length; i++) {
                IEditorPart part = references[i].getEditor(false);
                if (part != null) {
                    // HACK find better way to get the filename other than the tooltip
					if(("/"+part.getTitleToolTip()).equals(node.getHandleIdentifier())){
						if (part instanceof FormEditor) {
							((FormEditor)part).close(true);
						} else if (part instanceof AbstractTextEditor) {
							((AbstractTextEditor)part).close(true);
						}   
					}
//					if (part.getEditorInput() instanceof IFileEditorInput) {
//                        IFileEditorInput input = (IFileEditorInput)part.getEditorInput();
//                        if (input.getFile().getName().endsWith(".xml")) {
//                            if (part instanceof FormEditor) {
//                                ((FormEditor)part).close(true);
//                            } else if (part instanceof AbstractTextEditor) {
//                                ((AbstractTextEditor)part).close(true);
//                            }   
//                        }
//                    } 
                }
            }
        }
    }

    public boolean acceptsEditor(IEditorPart editorPart) {
        return editorPart instanceof ManifestEditor;
    }

    /**
     * HACK: use a lot of reflection to get the TreeViewer
     */
    public List<TreeViewer> getContentOutlineViewers(IEditorPart editor) {
        if (editor instanceof PDEFormEditor) {
        	PDESourcePage sp = null;
        	List<TreeViewer> viewers = new ArrayList<TreeViewer>(2);
        	if((sp = (PDESourcePage)((PDEFormEditor) editor).findPage(PluginInputContext.CONTEXT_ID)) != null){
        		ISortableContentOutlinePage p = sp.getContentOutline();
        		if (p != null && p.getControl() != null) {
                    try {
                        if (p != null && p instanceof SourceOutlinePage) {
                            // get the tree viewer for the outline
                            Class clazz2 = p.getClass();
                            Field field2 = clazz2.getDeclaredField("viewer");
                            field2.setAccessible(true);
                            Object f2 = field2.get(p);
                            if (f2 != null && f2 instanceof TreeViewer) {
                                viewers.add((TreeViewer)f2);
                            }
                        }
                    }catch (Exception e) {
                    	MylarPlugin.log(e, "failed to get tree viewers");
                        return null;
                    }
        		}
        	}
        	            
            try {
                // get the current page of the outline
                Class clazz = PDEFormEditor.class;
                Field field = clazz.getDeclaredField("formOutline");
                field.setAccessible(true);
                Object f = field.get(editor);
                if (f != null && f instanceof FormOutlinePage) {
                    // get the tree viewer for the outline
                    Class clazz2 = FormOutlinePage.class;
                    Field field2 = clazz2.getDeclaredField("treeViewer");
                    field2.setAccessible(true);
                    Object f2 = field2.get(f);
                    if (f2 != null && f2 instanceof TreeViewer) {
                        TreeViewer treeViewer = (TreeViewer) f2;
                        viewers.add(treeViewer);
                    }
                }
            } catch (Exception e) {
                MylarPlugin.log(e.getMessage(), this);
                return null;
            }
            
            // add a listener so that when the selection changes, the view is 
            // refreshed to attempt to keep the ui model the same as the taskscape one
            for(TreeViewer viewer: viewers){
            	viewer.addSelectionChangedListener(treeSelectionChangedListener);
            	viewer.addTreeListener(treeSelectionChangedListener);
            }
            
            return viewers;
        }
        return Collections.emptyList();     
    }

    public void refreshOutline(Object element, boolean updateLabels, boolean setSelection) {
        IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        List<TreeViewer> treeViewers = getContentOutlineViewers(editorPart);
        for(TreeViewer treeViewer: treeViewers){
	        if (treeViewer != null) {
	            if (element == null) {
	            	treeViewer.getControl().setRedraw(false);
	                treeViewer.refresh(true);
	                treeViewer.getControl().setRedraw(true);
	                treeViewer.expandAll();
	            } 
	            else if (element instanceof PluginObjectNode) {
	            	treeViewer.getControl().setRedraw(false);
	                treeViewer.refresh(element, true);
	                treeViewer.getControl().setRedraw(true);
	                treeViewer.expandAll();
	            }
	        }
        }
        
    }

    /**
     * Class to listen to the tree views to attempt to refresh them more
     * frequently to keep the ui model consistant with the user selections
     * 
     * @author Shawn Minto
     */
    private class TreeViewerListener implements ISelectionChangedListener, ITreeViewerListener{
    	public void selectionChanged(SelectionChangedEvent event) {
			Object o = event.getSource();
			if(o instanceof TreeViewer){
				((TreeViewer)o).refresh();
				((TreeViewer)o).expandAll();
			}
		}

		public void treeCollapsed(TreeExpansionEvent event) {
			Object o = event.getSource();
			if(o instanceof TreeViewer){
				((TreeViewer)o).refresh();
			}
			
		}

		public void treeExpanded(TreeExpansionEvent event) {
			Object o = event.getSource();
			if(o instanceof TreeViewer){
				((TreeViewer)o).refresh();
			}
		}
    }

	public Object getObjectForTextSelection(TextSelection selection, IEditorPart editor) {
		return null;
	}
}