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
 * Created on Apr 13, 2005
  */
package org.eclipse.mylar.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.model.InterestComparator;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.mylar.ui.internal.views.ProblemListInterestFilter;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.views.markers.internal.IField;
import org.eclipse.ui.views.markers.internal.TableSorter;


/**
 * @author Mik Kersten
 */
public class FilterUninterestingProblemsAction extends Action implements IViewActionDelegate, IActionDelegate2 {

    public static final String PREF_ID = "org.eclipse.mylar.ui.filter.problems";
    
    protected ProblemListInterestFilter interestFilter = new ProblemListInterestFilter();
    
    public FilterUninterestingProblemsAction() {
        super();
        setText("Filter uninteresting"); 
		setImageDescriptor(MylarImages.FILTER_UNINTERESTING);	
		setToolTipText("Filter uninteresting items from package explorer");
    } 

    public void run(IAction action) {
        valueChanged(action, action.isChecked(), true);
    }
    
    /**
     * HACK: using reflection to muck with accessibility
     */
    private void valueChanged(IAction action, final boolean on, boolean store) {
        action.setChecked(on);
        if (store) MylarPlugin.getDefault().getPreferenceStore().setValue(PREF_ID, on); //$NON-NLS-1$
        
        //XXX add the filtering for the outline view
        
//        TableViewer viewer = UiUtil.getProblemViewFromActivePerspective();
//        if (viewer != null) {
//            if (viewer != null) {
//                ViewerFilter[] filters = viewer.getFilters();
//                boolean found = false;
//                for (int i = 0; i < filters.length; i++) {
//                    if (filters[i] instanceof ProblemListInterestFilter) found = true;
//                }
//                if (!isChecked()) {
//                    viewer.removeFilter(interestFilter);
//                } else {
//                    if (!found) viewer.addFilter(interestFilter);
//                }
//            } 
//            viewer.refresh();
//        }
     
    }
        
    public void init(IViewPart view) {
    	// don't have anything to initialize
    }

    public void selectionChanged(IAction action, ISelection selection) {
    	// don't care about selection changes
    }

	public void init(IAction action) {
		valueChanged(action, MylarPlugin.getDefault().getPreferenceStore().getBoolean(PREF_ID), true);
		
	}

	public void dispose() {
		// don't care about this
		
	}

	public void runWithEvent(IAction action, Event event) {
		run(action);
		
	}

}

class ProblemsListDoiSorter extends TableSorter { 

    public ProblemsListDoiSorter(IField[] properties, int[] defaultPriorities, int[] defaultDirections) {
        super(properties, defaultPriorities, defaultDirections);
    } 

    protected InterestComparator comparator = new InterestComparator();
    
    @Override
    protected int compare(Object obj1, Object obj2, int depth) {
        return super.compare(obj1, obj2, depth);
    }

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        return super.compare(viewer, e1, e1);
    }
}
