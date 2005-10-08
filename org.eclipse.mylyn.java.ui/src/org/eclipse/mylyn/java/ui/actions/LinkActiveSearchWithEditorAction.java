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

package org.eclipse.mylar.java.ui.actions;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ide.ui.views.ActiveSearchView;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class LinkActiveSearchWithEditorAction implements IViewActionDelegate, IActionDelegate2, IWorkbenchWindowActionDelegate {
    	
	public static String ID = "org.eclipse.mylar.ui.views.active.search.link";
    private SelectionTracker selectionTracker = new SelectionTracker();
	private static LinkActiveSearchWithEditorAction INSTANCE;
    
    public LinkActiveSearchWithEditorAction() {
    	INSTANCE = this;
    	update();
    }
    
	public void init(IViewPart view) {
//		update();
	}
	
	public void init(IAction action) {
//		update();
	}

	public void update() {
		update(MylarUiPlugin.getDefault().getPreferenceStore().getBoolean(ID));
	}
	
	public void run(IAction action) {
		// TODO Auto-generated method stub
		update(action.isChecked());
	}

	public void selectionChanged(IAction action, ISelection selection) {
		
	}
	
	public void update(boolean on) {
		MylarUiPlugin.getDefault().getPreferenceStore().setValue(ID, on); 
		ISelectionService service = Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();
		if (on) {
		    service.addPostSelectionListener(selectionTracker); 
		} else {
			service.removePostSelectionListener(selectionTracker); 
		}
	}
	
	private class SelectionTracker implements ISelectionListener {
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			try {
				ActiveSearchView view = ActiveSearchView.getFromActivePerspective();
				if (view == null || !view.getViewer().getControl().isVisible()) return; 
				if (selection instanceof TextSelection && part instanceof JavaEditor) {
                    TextSelection textSelection = (TextSelection)selection;
                    IJavaElement selectedElement = SelectionConverter.resolveEnclosingElement((JavaEditor)part, textSelection);
	                if (selectedElement != null && view.getViewer().testFindItem(selectedElement) != null) {
	                	view.getViewer().setSelection(new StructuredSelection(selectedElement), true);
	                }
	            }
		    } catch (Throwable t) {
				MylarPlugin.log(t, "Could not update package explorer");
			}
		}
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void runWithEvent(IAction action, Event event) {
		// TODO Auto-generated method stub
		
	}

	public static LinkActiveSearchWithEditorAction getDefault() {
		return INSTANCE;
	}

	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		update();
	}
}