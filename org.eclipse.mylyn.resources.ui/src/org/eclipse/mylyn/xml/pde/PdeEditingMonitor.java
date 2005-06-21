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
 * Created on Apr 1, 2005
 */
package org.eclipse.mylar.xml.pde;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.core.AbstractSelectionMonitor;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.xml.XmlNodeHelper;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginObject;
import org.eclipse.pde.internal.core.plugin.ImportObject;
import org.eclipse.pde.internal.ui.editor.plugin.ManifestEditor;
import org.eclipse.pde.internal.ui.editor.plugin.ManifestSourcePage;
import org.eclipse.pde.internal.ui.model.IDocumentNode;
import org.eclipse.pde.internal.ui.model.plugin.PluginModel;
import org.eclipse.pde.internal.ui.model.plugin.PluginObjectNode;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.FileEditorInput;


public class PdeEditingMonitor extends AbstractSelectionMonitor {

    public PdeEditingMonitor() {
        super();
    }

    @Override
    protected void handleWorkbenchPartSelection(IWorkbenchPart part, ISelection selection) {
        if (part instanceof ManifestEditor) {
            TextSelection textSelection = null;
            IEditorInput in = null;

            // assume that we are editing an xml file due to the editor used
            // this is the plugin.xml editor

            ManifestEditor editor = (ManifestEditor) part;

            // make sure that the selection is a text selection
            if (!(editor.getSelection() instanceof TextSelection || editor.getSelection() instanceof StructuredSelection)) {
                return;
            } else if (editor.getSelection() instanceof StructuredSelection) {
                StructuredSelection s = (StructuredSelection) editor
                        .getSelection();
                if (s.getFirstElement() instanceof PluginObjectNode) {
                    PluginObjectNode n = (PluginObjectNode) s
                            .getFirstElement();
                    textSelection = new TextSelection(n.getOffset(), n
                            .getLength());
                } else if (s.getFirstElement() instanceof ImportObject) {
                    ImportObject io = (ImportObject) s.getFirstElement();
                    if (io.getImport() instanceof PluginObjectNode) {
                        PluginObjectNode n = (PluginObjectNode) io
                                .getImport();
                        textSelection = new TextSelection(n.getOffset(), n
                                .getLength());
                    }
                } else {
                    return;
                }
            } else {
                // get the selection and the editor input
                textSelection = (TextSelection) editor.getSelection();
            }
            in = editor.getEditorInput();
            
            // check if we have a text selection
            if (textSelection != null) {
                try {
                    
                    // get the node for the selection
                    int offset = textSelection.getOffset();
                    PluginObjectNode node = getNode(editor, offset);

                    if (node != null) {
                        FileEditorInput fei = (FileEditorInput) in;

                        // create the helper to get the handle for the node
                        XmlNodeHelper xnode = new XmlNodeHelper(fei, node.getOffset());

                        // get the name for the node
                        String name = node.getXMLAttributeValue("name");
                        if (name == null) name = node.getXMLTagName();
                        super.handleElementSelection(part, xnode);
                    }
                } catch (Exception e) {
                	MylarPlugin.log(e, "couldn't resolve selection");
                }
            }
        }
    }
    
    /**
     * COPIED FROM ManifestSourcePage - from a getRangeElement body
     */
    public static PluginObjectNode getNode(ManifestEditor editor, int offset){
        ManifestSourcePage page = (ManifestSourcePage)editor.findPage("plugin-context");
        if(page != null){
            IPluginModelBase model = (IPluginModelBase) page.getInputContext().getModel();
            PluginObjectNode node = (PluginObjectNode)PdeEditingMonitor.findNode(model.getPluginBase().getLibraries(),
                    offset);
            if (node == null)
                node = (PluginObjectNode)PdeEditingMonitor.findNode(model.getPluginBase().getImports(), offset);
            if (node == null)
                node = (PluginObjectNode)PdeEditingMonitor.findNode(model.getPluginBase().getExtensionPoints(), offset);
            if (node == null)
                node = (PluginObjectNode)PdeEditingMonitor.findNode(model.getPluginBase().getExtensions(), offset);
            if (node == null) {
                node = (PluginObjectNode)PdeEditingMonitor.findNode(new IPluginObject[] { model.getPluginBase() }, offset);
            }
            return node;
        }
        return null;
    }
    
    public static PluginObjectNode getNode(IDocument d, IFile f, int offset) throws CoreException{
        PluginModel model = new PluginModel(d, true);
        model.setUnderlyingResource(f);
        if(!model.isLoaded()){
            model.load();
            model.setEnabled(true);
        }
        
        PluginObjectNode node = (PluginObjectNode)PdeEditingMonitor.findNode(model.getPluginBase().getLibraries(),
                offset);
        if (node == null)
            node = (PluginObjectNode)PdeEditingMonitor.findNode(model.getPluginBase().getImports(), offset);
        if (node == null)
            node = (PluginObjectNode)PdeEditingMonitor.findNode(model.getPluginBase().getExtensionPoints(), offset);
        if (node == null)
            node = (PluginObjectNode)PdeEditingMonitor.findNode(model.getPluginBase().getExtensions(), offset);
        if (node == null) {
            node = (PluginObjectNode)PdeEditingMonitor.findNode(new IPluginObject[] { model.getPluginBase() }, offset);
        }
        return node;
    }
    
    /**
     * COPIED FROM ManifestSourcePage
     */
    private static IDocumentNode findNode(IPluginObject[] nodes, int offset) {
        for (int i = 0; i < nodes.length; i++) {
            IDocumentNode node = (IDocumentNode) nodes[i];
            IDocumentNode[] children = node.getChildNodes();
            
            // changed region - added to check the children to make it work properly
            IDocumentNode node2 = null;
            if(children.length > 0)
                node2 = PdeEditingMonitor.findNode(children, offset);
            // end changed region
            
            if(node2 != null && node2 instanceof IPluginObject)
                return node2;
            if (offset >= node.getOffset()
                    && offset < node.getOffset() + node.getLength()) {
                return node;
            }
        }
        return null;
    }

    /**
     * Copy of previous, taking different arguments
     */
    private static IDocumentNode findNode(IDocumentNode[] nodes, int offset) {
        for (int i = 0; i < nodes.length; i++) {
            IDocumentNode node = nodes[i];
            IDocumentNode[] children = node.getChildNodes();
            IDocumentNode node2 = null;
            if(children.length > 0)
                node2 = PdeEditingMonitor.findNode(children, offset);
            if(node2 != null)
                return node2;
            if (offset >= node.getOffset()
                    && offset < node.getOffset() + node.getLength()) {
                return node;
            }
        }
        return null;
    }
}

// XXX used if we support the xmen editor
// if (part instanceof XMLTextEditor) {
// XMLTextEditor editor = (XMLTextEditor)part;
// TextSelection textSelection =
// (TextSelection)editor.getSelectionProvider().getSelection();
// if (textSelection != null) {
//
// try{
// // get the model for the xml elements
// XMLReconciler rec = editor.getModel();
//                    
// int start = textSelection.getStartLine();
// int end = textSelection.getEndLine();
//                    
// // get the node that was selected
//                    XMLNode node = rec.getNodeFromLine(start);
//                    
//                    MylarPlugin.getTaskscapeManager().handleElementSelected(
//                              node.getHandle(), node.getCanName(),
//                              IDegreeOfInterest.Value.Selections,
//                              ITaskscapeNode.Kind.XML);        
//                              
//                }catch(Exception e){
//                    //handle properly
//                }
//            }
