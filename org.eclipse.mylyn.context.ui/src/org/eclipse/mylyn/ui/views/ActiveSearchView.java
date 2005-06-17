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

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylar.core.ITaskscapeListener;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.model.ITaskscape;
import org.eclipse.mylar.core.model.ITaskscapeNode;
import org.eclipse.mylar.core.model.InterestComparator;
import org.eclipse.mylar.core.search.RelationshipProvider;
import org.eclipse.mylar.ui.IMylarUiBridge;
import org.eclipse.mylar.ui.MylarUiPlugin;
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
    
    private final ITaskscapeListener REFRESH_UPDATE_LISTENER = new ITaskscapeListener() { 
        public void interestChanged(ITaskscapeNode node) { 
            refresh(node);
//            refresh();
        }
        
        public void interestChanged(List<ITaskscapeNode> nodes) {
            refresh(nodes.get(nodes.size()-1));
        }

        public void taskscapeActivated(ITaskscape taskscape) {
            refresh(null);
        }

        public void taskscapeDeactivated(ITaskscape taskscape) {
            refresh(null);
        } 
        
        public void presentationSettingsChanging(UpdateKind kind) {
            refresh(null);
        }
        
        public void landmarkAdded(ITaskscapeNode element) { 
//            viewer.refresh(element, true);
            refresh(null);
        }

        public void landmarkRemoved(ITaskscapeNode element) { 
//            viewer.refresh(element, true);
            refresh(null);
        }

        public void relationshipsChanged() {
            refresh(null);
        }

        private void refresh(final ITaskscapeNode node) {
            Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
                public void run() {
                    try { 
                        if (viewer != null && !viewer.getTree().isDisposed()) {
                            if (node != null) {
                                viewer.refresh(node);
                            } else {
                                viewer.refresh(); 
                            }
                            viewer.expandAll();
                        }
                    } catch (Throwable t) {
                    	MylarPlugin.log(this.getClass().toString(), t);
                    }
                }
            });
        }

        public void presentationSettingsChanged(UpdateKind kind) {
            if (kind == ITaskscapeListener.UpdateKind.HIGHLIGHTER) viewer.refresh(); 
        }

        public void nodeDeleted(ITaskscapeNode node) {
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
        IWorkbenchPage activePage= Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
        if (activePage == null)
            return null;
        IViewPart view= activePage.findView(VIEW_ID);
        if (view instanceof ActiveSearchView)
            return (ActiveSearchView)view;
        return null;    
    }
    
    public ActiveSearchView() { 
        MylarPlugin.getTaskscapeManager().addListener(REFRESH_UPDATE_LISTENER);
    }

    /**
     * This is a callback that will allow us
     * to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(Composite parent) {
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        //drillDownAdapter = new DrillDownAdapter(viewer);
        viewer.setContentProvider(new TaskscapeTreeContentProvider(this.getViewSite(), true));
        viewer.setLabelProvider(new DecoratingLabelProvider(
                new TaskscapeNodeLabelProvider(),
                PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
//        viewer.setLabelProvider(new MylarAppearanceAwareLabelProvider(viewer));
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

    public void resetProviders() {
        fillLocalToolBar(getViewSite().getActionBars().getToolBarManager());
//        getViewSite().getActionBars().getToolBarManager().update(true);
        IActionBars bars = getViewSite().getActionBars();
        bars.updateActionBars();
    }
    
    private void fillLocalPullDown(IMenuManager manager) {
//        manager.add(createCategory);
//        manager.add(new Separator());
//        manager.add(createTask);
    }

    void fillContextMenu(IMenuManager manager) {
//        manager.add(createTask);
        manager.add(new Separator());
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }
    
    private void fillLocalToolBar(IToolBarManager manager) {
        manager.removeAll();
//        System.err.println(">>>> " + MylarPlugin.getTaskscapeManager().getRelationshipProviders());
        for (RelationshipProvider provider : MylarPlugin.getTaskscapeManager().getRelationshipProviders()) {
            IMylarUiBridge bridge = MylarUiPlugin.getDefault().getUiBridge(provider.getStructureKind());
            ImageDescriptor image = bridge.getIconForRelationship(provider.getId());
            ToggleRelationshipProviderAction action = new ToggleRelationshipProviderAction(provider, image);
            relationshipProviderActions.add(action); 
            manager.add(action); 
        }
        manager.update(true);
    }

    private void makeActions() { 
    	// don't have any actions to make
    }
    	
    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
        //TODO: foo
    }

//    XXX never used
//    
//    private void showMessage(String message) {
//        MessageDialog.openInformation(
//            viewer.getControl().getShell(),
//            "Tasklist Message",
//            message);
//    }
//    
//    private String getBugIdFromUser() {
//        InputDialog dialog = new InputDialog(
//            Workbench.getInstance().getActiveWorkbenchWindow().getShell(), 
//            "Enter Bugzilla ID", 
//            "Enter the Bugzilla ID: ", 
//            "", 
//            null);
//        int dialogResult = dialog.open();
//        String answer;
//        if (dialogResult == Window.OK) { 
//            return dialog.getValue();
//        } else {
//            return null;
//        }
//    }
//    
//    private String getLabelNameFromUser(String kind) {
//        
//        InputDialog dialog = new InputDialog(
//            Workbench.getInstance().getActiveWorkbenchWindow().getShell(), 
//            "Enter name", 
//            "Enter a name for the " + kind + ": ", 
//            "", 
//            null);
//        int dialogResult = dialog.open();
//        String answer;
//        if (dialogResult == Window.OK) { 
//            return dialog.getValue();
//        } else {
//            return null;
//        }
//    }
}

