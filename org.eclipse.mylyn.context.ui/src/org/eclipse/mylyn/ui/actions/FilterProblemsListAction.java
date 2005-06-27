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

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylar.core.model.InterestComparator;
import org.eclipse.mylar.ui.internal.UiUtil;
import org.eclipse.mylar.ui.internal.views.ProblemsListInterestFilter;
import org.eclipse.ui.views.markers.internal.IField;
import org.eclipse.ui.views.markers.internal.TableSorter;


/**
 * @author Mik Kersten
 */
public class FilterProblemsListAction extends AbstractInterestFilterAction {

	public static FilterProblemsListAction INSTANCE;
	
	public FilterProblemsListAction() {
		super(new ProblemsListInterestFilter());
		INSTANCE = this;
	}
	
	@Override
	protected StructuredViewer getViewer() {
		return UiUtil.getProblemViewFromActivePerspective();
	}

	@Override
	protected void refreshViewer() {
		getViewer().refresh();
	}
	
	public static FilterProblemsListAction getDefault() {
		return INSTANCE;
	}
}

/**
 * TODO: refactor, not used
 */
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
