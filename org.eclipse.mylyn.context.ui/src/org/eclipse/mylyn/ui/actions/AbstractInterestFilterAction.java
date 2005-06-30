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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.InterestFilter;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten
 */
public abstract class AbstractInterestFilterAction extends Action implements IViewActionDelegate, IActionDelegate2 {

    public static final String PREF_ID_PREFIX = "org.eclipse.mylar.ui.interest.filter.";
    protected String prefId;
    private IAction initAction = null;
    
    protected ViewerFilter interestFilter;
    
    public AbstractInterestFilterAction(InterestFilter interestFilter) {
        super();
        this.interestFilter = interestFilter;
        setText("Filter uninteresting"); 
		setImageDescriptor(MylarImages.INTEREST_FILTERING);	
		setToolTipText("Filter uninteresting elements"); 
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
     * This operation is expensive.
     */
    public void update() {
    	valueChanged(initAction, MylarPlugin.getDefault().getPreferenceStore().getBoolean(prefId), false);
    }
    
    protected void valueChanged(IAction action, final boolean on, boolean store) {
        action.setChecked(on);
        if (store) MylarPlugin.getDefault().getPreferenceStore().setValue(prefId, on); 

        if (getViewer() != null) {
			if (on) {
				installInterestFilter(getViewer());
			} else {
				uninstallInterestFilter(getViewer());
			}
			refreshViewer();
        } else {
        	// ignore, failure to install is ok if there is no outline when attempted
//        	MylarPlugin.log("Couldn't mange filter installation on null viewer: " + prefId, this);
        }
	}
	
	protected abstract StructuredViewer getViewer() ;
	
	public abstract void refreshViewer();

	protected void installInterestFilter(StructuredViewer viewer) {
		if (viewer != null) {
            boolean found = false;
            for (int i = 0; i < viewer.getFilters().length; i++) {
                ViewerFilter filter = viewer.getFilters()[i];
                if (filter instanceof InterestFilter) found = true;
            }
            if (!found) viewer.addFilter(interestFilter);
        } else {
        	MylarPlugin.log("Could not install interest filter", null);
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
        	MylarPlugin.log("Could not uninstall interest filter", null);
        }
	}	
	
    public void selectionChanged(IAction action, ISelection selection) {
    	// ignore
    }

	public void dispose() {
		// don't need to do anything here
	}

	public void runWithEvent(IAction action, Event event) {
		run(action);
	}
}

