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
import org.eclipse.jface.viewers.*;
import org.eclipse.mylar.core.model.InterestComparator;
import org.eclipse.mylar.ui.internal.UiUtil;
import org.eclipse.mylar.ui.internal.views.ProblemListInterestFilter;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.views.markers.internal.IField;
import org.eclipse.ui.views.markers.internal.TableSorter;


/**
 * @author Mik Kersten
 */
public class FilterUninterestingProblemsAction extends Action implements IViewActionDelegate {

    public static final String PREF_ID = "org.eclipse.mylar.java.ui.explorer.manage.isChecked";
    
    protected ProblemListInterestFilter interestFilter = new ProblemListInterestFilter();
    
    public FilterUninterestingProblemsAction() {
        super();
//        setChecked(true);
//        try {
//            boolean checked= MylarPlugin.getDefault().getPreferenceStore().getBoolean(PREF_ID); 
//            valueChanged(true, true);
//        } catch (Exception e) {
//            // handle exception
//        }
    } 

    /**
     * HACK: using reflection to muck with accessibility
     */
    public void run(IAction action) {
        setChecked(!isChecked());
        TableViewer viewer = UiUtil.getProblemViewFromActivePerspective();
        if (viewer != null) {
            if (viewer != null) {
                ViewerFilter[] filters = viewer.getFilters();
                boolean found = false;
                for (int i = 0; i < filters.length; i++) {
                    if (filters[i] instanceof ProblemListInterestFilter) found = true;
                }
                if (!isChecked()) {
                    viewer.removeFilter(interestFilter);
                } else {
                    if (!found) viewer.addFilter(interestFilter);
                }
            } 
            viewer.refresh();
        }
    }
    
        
    public void init(IViewPart view) {
    	// don't have anything to initialize
    }

    public void selectionChanged(IAction action, ISelection selection) {
    	// don't care about selection changes
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
