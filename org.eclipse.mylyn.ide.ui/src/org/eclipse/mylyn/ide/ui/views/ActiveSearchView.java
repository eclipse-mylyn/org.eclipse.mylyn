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
package org.eclipse.mylar.ide.ui.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionManager;
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
import org.eclipse.mylar.dt.MylarWebRef;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.mylar.ui.actions.ToggleRelationshipProviderAction;
import org.eclipse.mylar.ui.views.MylarContextContentProvider;
import org.eclipse.mylar.ui.views.MylarContextLabelProvider;
import org.eclipse.mylar.ui.views.TaskscapeNodeClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.navigator.LocalSelectionTransfer;

/**
 * @author Mik Kersten
 */
public class ActiveSearchView extends ViewPart {

    private static final String STOP_JOBS_LABEL = "Stop Active Search Jobs";

	public static final String ID = "org.eclipse.mylar.ui.views.active.search";
	
    private TreeViewer viewer;
    private List<ToggleRelationshipProviderAction> relationshipProviderActions = new ArrayList<ToggleRelationshipProviderAction>();
    
    private final IMylarContextListener REFRESH_UPDATE_LISTENER = new IMylarContextListener() { 
        public void interestChanged(IMylarContextNode node) { 
            refresh(node, false);
        }
        
        public void interestChanged(List<IMylarContextNode> nodes) {
            refresh(nodes.get(nodes.size()-1), true);
        }

        public void contextActivated(IMylarContext taskscape) {
            refresh(null, true);
        }

        public void contextDeactivated(IMylarContext taskscape) {
            refresh(null, true);
        } 
        
        public void presentationSettingsChanging(UpdateKind kind) {
            refresh(null, true);
        }
        
        public void landmarkAdded(IMylarContextNode node) { 
            refresh(null, true);
        }

        public void landmarkRemoved(IMylarContextNode node) { 
            refresh(null, true);
        }

        public void edgesChanged(IMylarContextNode node) {
            refresh(node, true);
        }

        public void nodeDeleted(IMylarContextNode node) {
        	refresh(null, true);
        }

        public void presentationSettingsChanged(UpdateKind kind) {
        	if(viewer != null && !viewer.getTree().isDisposed()){
        		if (kind == IMylarContextListener.UpdateKind.HIGHLIGHTER) viewer.refresh();
        	}
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
        IViewPart view= activePage.findView(ID);
        if (view instanceof ActiveSearchView)
            return (ActiveSearchView)view;
        return null;    
    }
    
    public ActiveSearchView() { 
        MylarPlugin.getContextManager().addListener(REFRESH_UPDATE_LISTENER);
        for(AbstractRelationshipProvider provider: MylarPlugin.getContextManager().getActiveProviders()){
            provider.setEnabled(true);
        }
    }

    private void refresh(final IMylarContextNode node, final boolean updateLabels) {
        Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
            public void run() { 
                try {  
                    if (viewer != null && !viewer.getTree().isDisposed()) {
                    	if (node != null && containsNode(viewer.getTree(), node)) {
                    		viewer.refresh(node, updateLabels);
                    	} else if (node == null) {
                    		viewer.refresh();
                    	}
                    	viewer.expandAll();
                    }
                } catch (Throwable t) {
                	MylarPlugin.log(t, "active searchrefresh failed");
                }
            }
        });
    }
    
	private boolean containsNode(Tree tree, IMylarContextNode node) {
    	boolean contains = false;
    	for (int i = 0; i < tree.getItems().length; i++) {
			TreeItem item = tree.getItems()[i]; 
			if (node.equals(item.getData())) contains = true;
		}
		return contains;
	}
    
	@MylarWebRef(name="Drag and drop article", url="http://www.eclipse.org/articles/Article-Workbench-DND/drag_drop.html")
    private void initDrop() {
		Transfer[] types = new Transfer[] { LocalSelectionTransfer.getInstance() };
		viewer.addDropSupport(DND.DROP_MOVE, types, new ActiveViewDropAdapter(viewer));
	}
    
	@Override
	public void dispose() {
		super.dispose();
		MylarPlugin.getContextManager().removeListener(REFRESH_UPDATE_LISTENER);
		for(AbstractRelationshipProvider provider: MylarPlugin.getContextManager().getActiveProviders()){
            provider.setEnabled(false);
        }
	}
    
    /**
     * This is a callback that will allow us
     * to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(Composite parent) {
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.setUseHashlookup(true);
        viewer.setContentProvider(new MylarContextContentProvider(viewer.getTree(), this.getViewSite(), true));
        viewer.setLabelProvider(new DecoratingLabelProvider(
                new MylarContextLabelProvider(),
                PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
        viewer.setSorter(new DoiOrderSorter()); 
        viewer.setInput(getViewSite());
        hookContextMenu();
        initDrop();
        
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
 
    private void fillContextMenu(IMenuManager manager) {
        manager.add(new Separator());
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar(IToolBarManager manager) {
    	fillActions(manager);
    	manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }
    
    private void fillLocalPullDown(IMenuManager manager) {
    	fillActions(manager);
    	manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }
    
    private void fillActions(IContributionManager manager) {
       	// needed to prevent toolbar from getting too tall, TODO: fille bug report
    	IAction dummyAction = new Action(){
			@Override
			public void run() { }
        };
        dummyAction.setText("");
        dummyAction.setImageDescriptor(MylarImages.BLANK);
        manager.add(dummyAction);
    	
    	Map<String, IMylarStructureBridge> bridges = MylarPlugin.getDefault().getStructureBridges();
        for (String extension : bridges.keySet()) {
            IMylarStructureBridge bridge = bridges.get(extension);
            List<AbstractRelationshipProvider> providers = bridge.getRelationshipProviders(); 
            if(providers != null && providers.size() > 0) {
	            ToggleRelationshipProviderAction action = new ToggleRelationshipProviderAction(bridge);
	            relationshipProviderActions.add(action); 
	            manager.add(action); 
            }
        }
    	
    	IAction stopAction = new Action(){
			@Override
			public void run() {
	            for(AbstractRelationshipProvider provider: MylarPlugin.getContextManager().getActiveProviders()){
	            	provider.stopAllRunningJobs();
	            }
			}
        };
        stopAction.setToolTipText(STOP_JOBS_LABEL);
        stopAction.setText(STOP_JOBS_LABEL);
        stopAction.setImageDescriptor(MylarImages.STOP_SEARCH);
        manager.add(stopAction);
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

	public TreeViewer getViewer() {
		return viewer;
	}
}

