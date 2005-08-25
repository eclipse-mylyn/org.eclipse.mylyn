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

import java.lang.reflect.Method;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.InterestComparator;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ide.ui.ProblemsListInterestFilter;
import org.eclipse.mylar.ide.ui.ProblemsListLabelProvider;
import org.eclipse.mylar.ui.actions.AbstractApplyMylarAction;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.views.markers.internal.FieldFolder;
import org.eclipse.ui.views.markers.internal.FieldLineNumber;
import org.eclipse.ui.views.markers.internal.FieldMessage;
import org.eclipse.ui.views.markers.internal.FieldResource;
import org.eclipse.ui.views.markers.internal.FieldSeverity;
import org.eclipse.ui.views.markers.internal.IField;
import org.eclipse.ui.views.markers.internal.ProblemMarker;
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
        try {
        	ProblemView view = getProblemView();
        	if (view != null) {
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

	protected ProblemView getProblemView() {
		IWorkbenchPage activePage= Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
        if (activePage == null) return null;
        IViewPart view= activePage.findView("org.eclipse.ui.views.ProblemView");
        if (view instanceof ProblemView) {
        	return (ProblemView)view;
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
            viewer.setSorter(new ProblemsListDoiSorter());
        }
	}

	public void propertyChange(PropertyChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
}

/**
 * TODO: refactor, not used
 */
class ProblemsListDoiSorter extends TableSorter { 

    // COPIED: from ProblemView
    private final static int ASCENDING = TableSorter.ASCENDING;
    private final static int DESCENDING = TableSorter.DESCENDING;
    private final static int SEVERITY = 0;
    private final static int DOI = 1;
    private final static int DESCRIPTION = 2;
    private final static int RESOURCE = 3;
    private final static int[] DEFAULT_PRIORITIES = { 
        SEVERITY, 
        DOI, 
        DESCRIPTION,
        RESOURCE };
    private final static int[] DEFAULT_DIRECTIONS = { 
        DESCENDING, // severity
        ASCENDING, // folder
        ASCENDING, // resource
        ASCENDING}; // location
    private final static IField[] VISIBLE_FIELDS = { new FieldSeverity(),
            new FieldMessage(), new FieldResource(), new FieldFolder(),
            new FieldLineNumber() };
    // END COPY
    
    public ProblemsListDoiSorter() {
        super(VISIBLE_FIELDS, DEFAULT_PRIORITIES, DEFAULT_DIRECTIONS);
    } 

    protected InterestComparator<IMylarContextNode> comparator = new InterestComparator<IMylarContextNode>();
    
    @Override
    protected int compare(Object obj1, Object obj2, int depth) {
        if (obj1 instanceof ProblemMarker && obj1 instanceof ProblemMarker) { 
        	ProblemMarker marker = (ProblemMarker)obj1;
	        if (marker.getSeverity() == IMarker.SEVERITY_ERROR) {
	            return super.compare(obj1, obj2, depth);
	        } else {
	       	 	if (MylarPlugin.getContextManager().hasActiveContext()) {
	       	 		IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(marker.getResource().getFileExtension());
		            IMylarContextNode node1 =  MylarPlugin.getContextManager().getNode(bridge.getHandleForOffsetInObject((ProblemMarker)obj1, 0));
		            IMylarContextNode node2 =  MylarPlugin.getContextManager().getNode(bridge.getHandleForOffsetInObject((ProblemMarker)obj1, 0));
		            return comparator.compare(node1, node2);
	       	 	}
	        }
        }
        return super.compare(obj1, obj2, depth);
    }

    @Override
    public int compare(Viewer viewer, Object obj1, Object obj2) {
        if (obj1 instanceof ProblemMarker && obj1 instanceof ProblemMarker) { 
        	ProblemMarker marker = (ProblemMarker)obj1;
	        if (marker.getSeverity() == IMarker.SEVERITY_ERROR) {
	            return super.compare(viewer, obj1, obj2);
	        } else {
	       	 	if (MylarPlugin.getContextManager().hasActiveContext()) {
	       	 		IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(marker.getResource().getFileExtension());
		            IMylarContextNode node1 =  MylarPlugin.getContextManager().getNode(bridge.getHandleForOffsetInObject((ProblemMarker)obj1, 0));
		            IMylarContextNode node2 =  MylarPlugin.getContextManager().getNode(bridge.getHandleForOffsetInObject((ProblemMarker)obj1, 0));
		            return comparator.compare(node1, node2);
	       	 	}
	        }
        }
        return super.compare(viewer, obj1, obj2);
    }
}
