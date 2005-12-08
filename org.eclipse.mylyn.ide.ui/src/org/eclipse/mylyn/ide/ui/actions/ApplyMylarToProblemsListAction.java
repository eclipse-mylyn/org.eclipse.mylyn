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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylar.core.util.ErrorLogger;
import org.eclipse.mylar.ide.ui.ProblemsListDoiSorter;
import org.eclipse.mylar.ide.ui.ProblemsListInterestFilter;
import org.eclipse.mylar.ide.ui.ProblemsListLabelProvider;
import org.eclipse.mylar.ui.actions.AbstractApplyMylarAction;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.views.markers.internal.MarkerFilter;
import org.eclipse.ui.views.markers.internal.ProblemView;
import org.eclipse.ui.views.markers.internal.TableView;
import org.eclipse.ui.views.markers.internal.TableViewLabelProvider;

/**
 * @author Mik Kersten
 */
public class ApplyMylarToProblemsListAction extends AbstractApplyMylarAction {

	private static ApplyMylarToProblemsListAction INSTANCE;
    private StructuredViewer cachedProblemsTableViewer = null;
	private MarkerFilter defaultFilter = null;
    private ProblemsListDoiSorter interestSorter = new ProblemsListDoiSorter();
    
	public ApplyMylarToProblemsListAction() {
		super(new ProblemsListInterestFilter());
		INSTANCE = this;

//		IWorkspace workspace = ResourcesPlugin.getWorkspace();
//		workspace.addResourceChangeListener(new IResourceChangeListener() {
//	    	public void resourceChanged(IResourceChangeEvent event) {
//	    		if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
//	    			IWorkbench workbench = PlatformUI.getWorkbench();
//	    			workbench.getDisplay().asyncExec(new Runnable() {
//	    	            public void run() {
//	    	            	StructuredViewer viewer = getViewer();
//	    	            	verifySorterInstalled(viewer);
//	    	            	if (viewer != null && viewer.getSorter() != null) {
//	    	            		getViewer().getSorter().sort(getViewer(), ((TableViewer)getViewer()).getTable().getItems());
//	    	            	}
//	    	            }
//	    	        });
//	    		};
//	    	}
//		});
	}
	
    /**
     * HACK: changing accessibility
     */
	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
		if (cachedProblemsTableViewer == null) {
	        try {
	        	ProblemView view = getProblemView();
	        	if (view != null) {
	                Class infoClass = TableView.class;//problemView.getClass();
	                Method method = infoClass.getDeclaredMethod("getViewer", new Class[] { } );
	                method.setAccessible(true);
	                cachedProblemsTableViewer = (StructuredViewer)method.invoke(view, new Object[] { });
	    			updateLabelProvider(cachedProblemsTableViewer);           
	            } 
	        } catch (Exception e) {
	        	ErrorLogger.log(e, "couldn't get problmes viewer");
	        }
		}
        if (cachedProblemsTableViewer != null) viewers.add(cachedProblemsTableViewer);
        return viewers;
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
	
//	@Override
//	public void refreshViewer() {
//		for (StructuredViewer viewer : getViewer()) {
//			if (viewer != null && !viewer.getControl().isDisposed()) {
//				viewer.getControl().setRedraw(false);
//				viewer.refresh();
//				viewer.getControl().setRedraw(true);
//			}
//		}
//	}
	
	private void updateLabelProvider(StructuredViewer viewer) {
		IBaseLabelProvider currentProvider = viewer.getLabelProvider();
		if (!(currentProvider instanceof ProblemsListLabelProvider)) {
			viewer.setLabelProvider(new ProblemsListLabelProvider((TableViewLabelProvider)currentProvider));
		}
	}
	
	public static ApplyMylarToProblemsListAction getDefault() {
		return INSTANCE;
	}

	@Override
	public void update() {	
		super.update();
		cachedProblemsTableViewer = null;
		for (StructuredViewer viewer : getViewers()) {
			if (viewer instanceof TableViewer) {
		        TableViewer tableViewer = (TableViewer)viewer;
		        if (tableViewer != null && !(tableViewer.getLabelProvider() instanceof ProblemsListLabelProvider)) {
		        	tableViewer.setLabelProvider(new ProblemsListLabelProvider(
		                    (TableViewLabelProvider)tableViewer.getLabelProvider()));
		        }
			}
		}
	}

	public void propertyChange(PropertyChangeEvent event) {
		// ignore
	}

	@Override
	protected boolean installInterestFilter(final StructuredViewer viewer) {
//		defaultSorter = viewer.getSorter();
//		viewer.setSorter(interestSorter);
		super.installInterestFilter(viewer);
		toggleMarkerFilter(false); 
		return true;
//		if (viewer instanceof TreeViewer) {
//			IWorkbench workbench = PlatformUI.getWorkbench();
//			workbench.getDisplay().asyncExec(new Runnable() {
//	            public void run() {
//	            	if (viewer != null && !viewer.getControl().isDisposed()) {
//	        			viewer.getControl().setRedraw(false);
//	        			((TreeViewer)viewer).expandToLevel(3);
//	        			viewer.getControl().setRedraw(true);
//	            	}
//	            }
//	        });
//		}
	}

	@Override
	protected void uninstallInterestFilter(StructuredViewer viewer) {
		super.uninstallInterestFilter(viewer);
//		viewer.setSorter(defaultSorter);
		toggleMarkerFilter(true);
	}
	
	/**
	 * HACK: using reflection to gain accessibility
	 */
	protected void toggleMarkerFilter(boolean enabled) {
        try {
        	ProblemView view = getProblemView();
        	if (view != null) {
        		Class viewClass = view.getClass();
        		
        		try { 
        			// 3.1 way of removing existing filter
	        		Field problemFilter = viewClass.getDeclaredField("problemFilter");
	        		problemFilter.setAccessible(true);
	        		defaultFilter = (MarkerFilter)problemFilter.get(view);	
	        		
	        		Class filterClass = defaultFilter.getClass().getSuperclass();
	        		Method method = filterClass.getDeclaredMethod("setEnabled", new Class[] { boolean.class } );
	                method.setAccessible(true);
	        		method.invoke(defaultFilter, new Object[] { enabled });
	                Method refresh = view.getClass().getSuperclass().getDeclaredMethod("refresh", new Class[] { } );
	                refresh.setAccessible(true);
	                refresh.invoke(view, new Object[] { } );
        		} catch (NoSuchFieldException nfe) {
        			// 3.2 way
        		}
            } 
        } catch (Exception e) {
        	ErrorLogger.fail(e, "Couldn't toggle problem filter (not yet supported on Eclipse 3.2)", false);
        }
	}

	protected void verifySorterInstalled(StructuredViewer viewer) {
		if (viewer != null && viewer.getSorter() != interestSorter) {
			viewer.setSorter(interestSorter);
		}
	}
}