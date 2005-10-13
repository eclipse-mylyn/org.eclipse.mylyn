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

package org.eclipse.mylar.ui.actions;

import java.util.List;

import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.InterestFilter;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * Extending this class makes it possible to apply Mylar management to a structured view
 * (e.g. to provide interest-based filtering).
 * 
 * @author Mik Kersten
 */
public abstract class AbstractApplyMylarAction extends Action implements IViewActionDelegate, IActionDelegate2, IPropertyChangeListener {

    private static final String ACTION_LABEL = "Apply Mylar";
	public static final String PREF_ID_PREFIX = "org.eclipse.mylar.ui.interest.filter.";
    protected String prefId;
    protected IAction initAction = null;
    
    protected InterestFilter interestFilter;
    
    public AbstractApplyMylarAction(InterestFilter interestFilter) {
        super();
        this.interestFilter = interestFilter;
        setText(ACTION_LABEL); 
		setToolTipText(ACTION_LABEL);
		setImageDescriptor(MylarImages.INTEREST_FILTERING);	
   } 

	public void init(IAction action) {
		initAction = action;
        setChecked(action.isChecked());
	}
    
    public void init(IViewPart view) {
    	String id = view.getSite().getId();
    	prefId = PREF_ID_PREFIX + id;//.substring(id.lastIndexOf('.') + 1);
    }
    
    public void run(IAction action) {
        setChecked(action.isChecked());
        valueChanged(action, action.isChecked(), true);
    }
    
    /**
     * Don't update if the preference has not been initialized.
     */
    public void update() {
    	if (prefId != null) {
    		update(MylarPlugin.getDefault().getPreferenceStore().getBoolean(prefId));
    	}
    }

    /**
     * This operation is expensive.
     */
    public void update(boolean on) {
    	valueChanged(initAction, on, false);
    }
    
    protected void valueChanged(IAction action, final boolean on, boolean store) {
    	try {
    		setChecked(on);
	        action.setChecked(on);
	        if (store && MylarPlugin.getDefault() != null) MylarPlugin.getDefault().getPreferenceStore().setValue(prefId, on); 
	
	        for (StructuredViewer viewer : getViewers()) {
	        	MylarUiPlugin.getDefault().getViewerManager().addManagedViewer(viewer);
		        installInterestFilter(on, viewer);
	        } 
    	} catch (Throwable t) {
    		MylarPlugin.fail(t, "Could not install viewer manager on: " + prefId, false);
    	}
	}

    /**
     * Public for testing
     */
	public void installInterestFilter(final boolean on, StructuredViewer viewer) {
		if (viewer != null) {
			if (on) {
				installInterestFilter(viewer);
				MylarUiPlugin.getDefault().getViewerManager().addFilteredViewer(viewer);
			} else {
				uninstallInterestFilter(viewer);
				MylarUiPlugin.getDefault().getViewerManager().removeFilteredViewer(viewer);
			}	
			if (on && viewer instanceof TreeViewer) {
				((TreeViewer)viewer).expandAll();
			}
		}
	}
	
//    private void refreshViewer(StructuredViewer viewer) {
//		viewer.getControl().setRedraw(false);
//		viewer.refresh(true);						            
//		viewer.getControl().setRedraw(true);
//	}

	/**
     * Public for testing
     */
	public abstract List<StructuredViewer> getViewers() ;
	
	protected void installInterestFilter(StructuredViewer viewer) {
		try {
			if (viewer != null) {
	            boolean found = false;
	            for (int i = 0; i < viewer.getFilters().length; i++) {
	                ViewerFilter filter = viewer.getFilters()[i];
	                if (filter instanceof InterestFilter) found = true;
	            }
	            if (!found) viewer.addFilter(interestFilter);
	        } else {
	        	MylarPlugin.log("Could not install interest filter", this);
	        }
		} catch (Throwable t) {
			MylarPlugin.fail(t, "Could not install viewer fitler on: " + prefId, false);
		}
	}

	protected void uninstallInterestFilter(StructuredViewer viewer) {
		if (viewer != null) {
            for (int i = 0; i < viewer.getFilters().length; i++) {
                ViewerFilter filter = viewer.getFilters()[i];
                if (filter instanceof InterestFilter) {
                	viewer.removeFilter(filter);
                }
            }
        } else {
        	MylarPlugin.log("Could not uninstall interest filter", this);
        }
	}	
	
    public void selectionChanged(IAction action, ISelection selection) {
    	// ignore
    }

	public void dispose() {
        for (StructuredViewer viewer : getViewers()) {
        	MylarUiPlugin.getDefault().getViewerManager().removeManagedViewer(viewer);
        } 	
	}

	public void runWithEvent(IAction action, Event event) {
		run(action);
	}

//	public void setViewerIsSelfManaged(boolean isSelfManaged) {
//		this.isSelfManaged = isSelfManaged;
//	}

	public String getPrefId() {
		return prefId;
	}

	
	/**
	 * For testing.
	 */
	public InterestFilter getInterestFilter() {
		return interestFilter;
	}
}

