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

import java.lang.reflect.Method;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylar.core.InterestComparator;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.internal.views.ProblemsListInterestFilter;
import org.eclipse.mylar.ui.internal.views.ProblemsListLabelProvider;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.views.markers.internal.IField;
import org.eclipse.ui.views.markers.internal.ProblemView;
import org.eclipse.ui.views.markers.internal.TableSorter;
import org.eclipse.ui.views.markers.internal.TableView;
import org.eclipse.ui.views.markers.internal.TableViewLabelProvider;


/**
 * @author Mik Kersten
 */
public class ApplyMylarToProblemsListAction extends AbstractApplyMylarAction {

	public static ApplyMylarToProblemsListAction INSTANCE;
    public TableViewer cachedProblemsTableViewer = null;
	
	public ApplyMylarToProblemsListAction() {
		super(new ProblemsListInterestFilter());
		INSTANCE = this;
	}
	
    /**
     * HACK: changing accessibility
     */
	@Override
	protected StructuredViewer getViewer() {
        if (cachedProblemsTableViewer != null) return cachedProblemsTableViewer;
        IWorkbenchPage activePage= Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
        if (activePage == null) return null;
        try {
            IViewPart view= activePage.findView("org.eclipse.ui.views.ProblemView");
            if (view instanceof ProblemView) {

                Class infoClass = TableView.class;//problemView.getClass();
                Method method = infoClass.getDeclaredMethod("getViewer", new Class[] { } );
                method.setAccessible(true);
                cachedProblemsTableViewer = (TableViewer)method.invoke(view, new Object[] { });
                return cachedProblemsTableViewer;
            } 
        } catch (Exception e) {
        	MylarPlugin.log(e, "couldn't get problmes viewer");
        }
        return null;
	}

	@Override
	public void refreshViewer() {
		StructuredViewer viewer = getViewer();
		if (viewer != null && !viewer.getControl().isDisposed()) getViewer().refresh();
	}
	
	public static ApplyMylarToProblemsListAction getDefault() {
		return INSTANCE;
	}

	@Override
	public void update() {
		super.update();
		cachedProblemsTableViewer = null;
        TableViewer viewer = (TableViewer)getViewer();
        if (viewer != null) {
            viewer.setLabelProvider(new ProblemsListLabelProvider(
                    (TableViewLabelProvider)viewer.getLabelProvider()));
        }
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
