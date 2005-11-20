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
package org.eclipse.mylar.java;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ide.AbstractEditorTracker;
import org.eclipse.mylar.java.ui.editor.ActiveFoldingListener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class JavaEditorTracker extends AbstractEditorTracker {

    protected Map<JavaEditor, ActiveFoldingListener> editorListenerMap = new HashMap<JavaEditor, ActiveFoldingListener>();
    	
	@Override
	public void editorOpened(IEditorPart part) {
		if (part instanceof JavaEditor) registerEditor((JavaEditor)part);
	}

	@Override
	public void editorClosed(IEditorPart part) {
		if (part instanceof JavaEditor) unregisterEditor((JavaEditor)part);
	}
	    
    public void registerEditor(final JavaEditor editor) {
        if (editorListenerMap.containsKey(editor)) {
            return;
        } else {
            Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
                public void run() { 
                	ActiveFoldingListener listener = new ActiveFoldingListener(editor);
                	editorListenerMap.put(editor, listener);
                	MylarPlugin.getContextManager().addListener(listener);
                }
            });
        }        
    }
    
    public void unregisterEditor(JavaEditor editor) {
        ActiveFoldingListener listener = editorListenerMap.get(editor);
        MylarPlugin.getContextManager().removeListener(listener);
        editorListenerMap.remove(editor);
    }

}
