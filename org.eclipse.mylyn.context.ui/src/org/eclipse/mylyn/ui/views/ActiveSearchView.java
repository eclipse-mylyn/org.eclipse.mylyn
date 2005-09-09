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
package org.eclipse.mylar.ui.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylar.core.AbstractRelationshipProvider;
import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.InterestComparator;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.mylar.ui.actions.ToggleRelationshipProviderAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Mik Kersten
 */
public class ActiveSearchView extends ViewPart {
    
    private TreeViewer viewer;
    private List<ToggleRelationshipProviderAction> relationshipProviderActions = new ArrayList<ToggleRelationshipProviderAction>();
    private static final String VIEW_ID = "org.eclipse.mylar.ui.views.ActiveSearchView";
    
    private final IMylarContextListener REFRESH_UPDATE_LISTENER = new IMylarContextListener() { 
        public void interestChanged(IMylarContextNode node) { 
            refresh(node);
        }
        
        public void interestChanged(List<IMylarContextNode> nodes) {
//            refresh(nodes.get(nodes.size()-1));
        }

        public void contextActivated(IMylarContext taskscape) {
            refresh(null);
        }

        public void contextDeactivated(IMylarContext taskscape) {
            refresh(null);
        } 
        
        public void presentationSettingsChanging(UpdateKind kind) {
            refresh(null);
        }
        
        public void landmarkAdded(IMylarContextNode element) { 
            viewer.refresh(element, true);
//            refresh(null);
        }

        public void landmarkRemoved(IMylarContextNode element) { 
            viewer.refresh(element, true);
//            refresh(null);
        }

        public void relationshipsChanged() {
            refresh(null);
        }

        private void refresh(final IMylarContextNode node) {
            Workbench.getInstance().getDisplay().syncExec(new Runnable() {
                public void run() {
                    try {  
                        if (viewer != null && !viewer.getTree().isDisposed()) {
                        	//TODO add back in for lazy refreshes
//                            if (node != null) {
//                                viewer.refresh(node);
//                            } else {
//                                viewer.refresh(); 
//                            }
                            viewer.refresh();
                            viewer.expandAll();
                        }
                    } catch (Throwable t) {
                    	MylarPlugin.log(t, "refresh failed");
                    }
                }
            });
        }

        public void presentationSettingsChanged(UpdateKind kind) {
        	if(viewer != null && !viewer.getTree().isDisposed()){
        		if (kind == IMylarContextListener.UpdateKind.HIGHLIGHTER) viewer.refresh();
        	}
        }

        public void nodeDeleted(IMylarContextNode node) {
        }
    };
    
    static class DoiOrderSorter extends ViewerSorter { 
        protected InterestComparator<Object> comparator = new InterestComparator<Object>();

        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            return comparator.compare(e1, e2);  
        }
    }

    public static ActiveSearchView getFromActivePerspective() {
    	if (Workbench.getInstance() == null) return null;
    	IWorkbenchPage activePage= Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
        if (activePage == null)
            return null;
        IViewPart view= activePage.findView(VIEW_ID);
        if (view instanceof ActiveSearchView)
            return (ActiveSearchView)view;
        return null;    
    }
    
    public ActiveSearchView() { 
        MylarPlugin.getContextManager().addListener(REFRESH_UPDATE_LISTENER);
    }

    /**
     * This is a callback that will allow us
     * to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(Composite parent) {
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.setContentProvider(new MylarContextContentProvider(this.getViewSite(), true));
        viewer.setLabelProvider(new DecoratingLabelProvider(
                new MylarContextLabelProvider(),
                PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
        viewer.setSorter(new DoiOrderSorter()); 
        viewer.setInput(getViewSite());
        makeActions();
        hookContextMenu();
        
        viewer.addOpenListener(new TaskscapeNodeClickListener(viewer));
        
        contributeToActionBars();
        viewer.expandToLevel(2);
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                ActiveSearchView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

//    public void resetProviders() {
//    	fillLocalToolBar(getViewSite().getActionBars().getToolBarManager());
//		getViewSite().getActionBars().getToolBarManager().update(true);
//		viewer.refresh();     
//    }
    
    private void fillLocalPullDown(IMenuManager manager) {

    }

    void fillContextMenu(IMenuManager manager) {
        manager.add(new Separator());
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }
    
    private void fillLocalToolBar(IToolBarManager manager) {
        manager.removeAll();
        Map<String, IMylarStructureBridge> bridges = MylarPlugin.getDefault().getStructureBridges();
        for (String extension : bridges.keySet()) {
            IMylarStructureBridge bridge = bridges.get(extension);
            List<AbstractRelationshipProvider> providers = bridge.getProviders(); 
            if(providers != null && providers.size() > 0){
	            ToggleRelationshipProviderAction action = new ToggleRelationshipProviderAction(bridge);
	            relationshipProviderActions.add(action); 
	            manager.add(action); 
            }
        }
        IAction stopAction = new Action(){
			@Override
			public void run() {
				Map<String, IMylarStructureBridge> bridges = MylarPlugin.getDefault().getStructureBridges();
		        for (String extension : bridges.keySet()) {
		            IMylarStructureBridge bridge = bridges.get(extension);
		            List<AbstractRelationshipProvider> providers = bridge.getProviders();
		            if(providers == null) continue;
		            for(AbstractRelationshipProvider provider: providers){
		            	provider.stopAllRunningJobs();
		            }
		        }
			}
        	
        };
        stopAction.setToolTipText("Stop all active search jobs");
        stopAction.setText("Stop all active search jobs");
        stopAction.setImageDescriptor(MylarImages.STOP_SEARCH);
        manager.add(stopAction);
        manager.markDirty();
    }

    private void makeActions() { 
    	// don't have any actions to make
    }
    	
    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
    	viewer.refresh();
        viewer.getControl().setFocus();
        //TODO: foo
    }

}

