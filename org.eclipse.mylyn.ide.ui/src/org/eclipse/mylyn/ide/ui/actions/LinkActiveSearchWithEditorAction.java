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

package org.eclipse.mylar.ide.ui.actions;

import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.core.util.ErrorLogger;
import org.eclipse.mylar.ide.ui.views.ActiveSearchView;
import org.eclipse.mylar.ui.IMylarUiBridge;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class LinkActiveSearchWithEditorAction extends Action {
    
	public static final String ID = "org.eclipse.mylar.ui.views.active.search.link";
    private static final String LABEL = "Link with Editor";
	private SelectionTracker selectionTracker = new SelectionTracker();
	private static LinkActiveSearchWithEditorAction INSTANCE;
	
    public LinkActiveSearchWithEditorAction() {
    	super(LABEL, IAction.AS_CHECK_BOX);
    	INSTANCE = this;
    	setId(ID);
    	setImageDescriptor(MylarImages.LINK_WITH_EDITOR);
    	setText(LABEL);
    	setToolTipText(LABEL);
    	MylarUiPlugin.getDefault().getPreferenceStore().setDefault(ID, true);
    	update(MylarUiPlugin.getDefault().getPreferenceStore().getBoolean(ID));
    }
	
	@Override
	public void run() {
		update(isChecked());
	}
	
	public void update(boolean on) { 
		setChecked(on);
		MylarUiPlugin.getDefault().getPreferenceStore().setValue(ID, on); 
		ISelectionService service = Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();
		if (on) {
		    service.addPostSelectionListener(selectionTracker); 
		} else {
			service.removePostSelectionListener(selectionTracker); 
		}
	}
	
	private static class SelectionTracker implements ISelectionListener {
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			try {
				if (selection instanceof TextSelection && part instanceof IEditorPart) {
					ActiveSearchView view = ActiveSearchView.getFromActivePerspective();
					if (view == null || !view.getViewer().getControl().isVisible()) return; 
					IMylarUiBridge bridge = MylarUiPlugin.getDefault().getUiBridgeForEditor((IEditorPart)part);
					Object toSelect = bridge.getObjectForTextSelection((TextSelection)selection, (IEditorPart)part);
	                if (toSelect != null && view.getViewer().testFindItem(toSelect) != null) {
	                	view.getViewer().setSelection(new StructuredSelection(toSelect), true);
	                }
				}
		    } catch (Throwable t) {
				ErrorLogger.log(t, "Could not update package explorer");
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

	public void propertyChange(PropertyChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
}