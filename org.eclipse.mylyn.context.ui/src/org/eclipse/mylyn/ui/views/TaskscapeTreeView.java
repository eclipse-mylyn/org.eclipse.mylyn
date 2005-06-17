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


import java.util.List;

import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.mylar.core.ITaskscapeListener;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.model.ITaskscape;
import org.eclipse.mylar.core.model.ITaskscapeNode;
import org.eclipse.mylar.ui.*;
import org.eclipse.mylar.ui.actions.ToggleDecorateInterestLevelAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.*;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.ViewPart;


/**
 * @author Mik Kersten
 */
public class TaskscapeTreeView extends ViewPart {

    private ViewerSorter viewerSorter;
    protected TreeViewer viewer;
    private Action decorateInterestLevel;
    private Action linkRefresh;
    private boolean activeRefresh = MylarPlugin.DEBUG_MODE;
    
    private final ITaskscapeListener REFRESH_UPDATE_LISTENER = new ITaskscapeListener() { 
        public void interestChanged(ITaskscapeNode info) {
            refresh();
        }
        
        public void interestChanged(List<ITaskscapeNode> nodes) {
            refresh();
        }
  
        public void taskscapeActivated(ITaskscape taskscape) {
            refresh();
        }

        public void taskscapeDeactivated(ITaskscape taskscape) {
            refresh();
        } 

        public void presentationSettingsChanging(UpdateKind kind) {
            refresh();
        }
        
        public void landmarkAdded(ITaskscapeNode element) { 
            refresh();
        }

        public void landmarkRemoved(ITaskscapeNode element) { 
            refresh();
        }

        public void relationshipsChanged() {
            refresh();
        } 
        
        private void refresh() {
            Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
                public void run() {
                    try {
//                        List elements = MylarPlugin.getTaskscapeManager().getActiveTaskscape().getAllElements();                      
                        if (viewer != null && !viewer.getTree().isDisposed()) viewer.refresh();
                    } catch (Throwable t) {
                        MylarPlugin.fail(t, "Could not update viewer", false);
                    }
                }
            });
        }

        public void presentationSettingsChanged(UpdateKind kind) {
        } 

        public void nodeDeleted(ITaskscapeNode node) {
            refresh();
        }
    };
    
    public TaskscapeTreeView() {
        MylarPlugin.getTaskscapeManager().addListener(REFRESH_UPDATE_LISTENER);
    } 
    
    @Override
    public void createPartControl(Composite parent) {
        viewerSorter = new InterestSorter();
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.setContentProvider(new TaskscapeTreeContentProvider(this.getViewSite(), false));
        viewer.setSorter(viewerSorter);
        viewer.setInput(getViewSite());

//        viewer.setLabelProvider(new TaskscapeNodeLabelProvider());
        viewer.setLabelProvider(new DecoratingLabelProvider(
                new TaskscapeNodeLabelProvider(),
                PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));

        makeActions();
        hookContextMenu();
        contributeToActionBars();
        viewer.getTree().setBackground(MylarUiPlugin.getDefault().getColorMap().BACKGROUND_COLOR);
        
        viewer.addOpenListener(new TaskscapeNodeClickListener(viewer));
    }
    
    protected Object[] refreshView(Object parent) {
        if (MylarPlugin.getTaskscapeManager() == null) {
            return new String[] { "No model" };  
        } else { 
            try { 
                return MylarPlugin.getTaskscapeManager().getActiveTaskscape().getAllElements().toArray();
            } catch (Throwable t) { 
                MylarPlugin.fail(t, "failed to show model", false);
                return new String[] { 
                        "Absent or incompatible model data: " + t.getMessage(), 
                        "Consider resetting model file." };
            } 
        }
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                TaskscapeTreeView.this.fillContextMenu(manager);
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

    private void fillLocalPullDown(IMenuManager manager) {
        manager.add(decorateInterestLevel);
        manager.add(new Separator());
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(decorateInterestLevel);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }
    
    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(linkRefresh);
        manager.add(decorateInterestLevel);
    }

    private void makeActions() {
        linkRefresh = new ActiveRefreshAction();
        linkRefresh.setToolTipText("Active Refresh"); 
        linkRefresh.setImageDescriptor(MylarImages.SYNCHED);  
        linkRefresh.setChecked(activeRefresh);
        
        decorateInterestLevel = new ToggleDecorateInterestLevelAction();
    }  
    
    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }
    
    class ActiveRefreshAction extends Action {
        public ActiveRefreshAction() {
            super(null, IAction.AS_CHECK_BOX);
        } 
         
        @Override
        public void run() {
            activeRefresh = !activeRefresh;
            setChecked(activeRefresh);
            if (activeRefresh) {
                MylarPlugin.getTaskscapeManager().addListener(REFRESH_UPDATE_LISTENER);
            } else {
                MylarPlugin.getTaskscapeManager().removeListener(REFRESH_UPDATE_LISTENER);
            }
        }
    }

   
}



