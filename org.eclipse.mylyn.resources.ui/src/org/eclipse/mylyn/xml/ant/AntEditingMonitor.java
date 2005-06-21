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
package org.eclipse.mylar.xml.ant;

import org.eclipse.ant.internal.ui.editor.AntEditor;
import org.eclipse.ant.internal.ui.model.AntElementNode;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.core.AbstractSelectionMonitor;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.xml.XmlNodeHelper;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.FileEditorInput;


public class AntEditingMonitor extends AbstractSelectionMonitor {

    public AntEditingMonitor() {
        super();
    }

    @Override
    protected void handleWorkbenchPartSelection(IWorkbenchPart part, ISelection selection) {
        if (part instanceof AntEditor) {

            TextSelection textSelection = null;
            IEditorInput in = null;

            // assume that we are editing an xml file due to the editor used
            // this is the build.xml and other ant file editor
            AntEditor editor = (AntEditor) part;

            if (!(editor.getSelectionProvider().getSelection() instanceof TextSelection)) return;

            textSelection = (TextSelection) editor.getSelectionProvider()
                    .getSelection();
            in = editor.getEditorInput();

            // check if we have a text selection
            if (textSelection != null) {
                try {
  
                    AntElementNode node = editor.getAntModel().getNode(textSelection.getOffset(), false);

                    FileEditorInput fei = (FileEditorInput)in;
                    
                    XmlNodeHelper xnode = new XmlNodeHelper(fei, node.getOffset());
                    super.handleElementSelection(part, xnode);
                } catch (Exception e) {
                	MylarPlugin.log(e, "selection resolve failed");
                }
            }
        }     
        return;
    }
}
