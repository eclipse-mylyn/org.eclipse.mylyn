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

package org.eclipse.mylar.java.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.viewsupport.AppearanceAwareLabelProvider;
import org.eclipse.jdt.internal.ui.viewsupport.JavaUILabelProvider;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.java.JavaStructureBridge;
import org.eclipse.mylar.java.ui.MylarJavaLabelProvider;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.mylar.ui.internal.UiUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Mik Kersten
 */
public class ActiveHierarchyView extends ViewPart {
	
    private TreeParent root = new TreeParent("<no hierarchy>");
    
    private TreeViewer viewer;
	
	final IMylarContextListener MODEL_LISTENER = new IMylarContextListener() { 
        
        public void contextActivated(IMylarContext taskscape) {
            refreshHierarchy(); 
        }

        public void contextDeactivated(IMylarContext taskscape) {
            refreshHierarchy();
        }        
        
	    public void interestChanged(IMylarContextNode info) { 
	    }
        
        public void interestChanged(List<IMylarContextNode> nodes) {
        } 
        
        public void landmarkAdded(IMylarContextNode element) { 
            refreshHierarchy();
        }

        public void landmarkRemoved(IMylarContextNode element) { 
            refreshHierarchy();
        }

        public void relationshipsChanged() {
        }
 
        public void presentationSettingsChanging(UpdateKind kind) {
        }

        public void presentationSettingsChanged(UpdateKind kind) {
            refreshHierarchy();
        }

        public void nodeDeleted(IMylarContextNode node) {
        }
	};

	class ViewContentProvider implements IStructuredContentProvider, 
										   ITreeContentProvider {
		
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			// don't care when the input is changed
		}
		
		public void dispose() { 
			// don't care when we are disposed
		}
		
		public Object[] getElements(Object parent) {
		    return root.getChildren();  
		}

        public Object getParent(Object child) {
            return ((TreeParent)child).getParent();
		}
		public Object [] getChildren(Object parent) {
			if (parent instanceof TreeParent) {
				return ((TreeParent)parent).getChildren();
			}
			return new Object[0];
		}
		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeParent)
				return ((TreeParent)parent).hasChildren();
			return false;
		}

//		XXX never used
//		private void initialize() {
//			invisibleRoot.addChild(new TreeParent(AsmManager.getDefault().getHierarchy().getRoot()));
//		}
	}

	public ActiveHierarchyView() {
	    MylarPlugin.getContextManager().addListener(MODEL_LISTENER);
	    refreshHierarchy();
	}

	private void refreshHierarchy() {
        try {            
            root.removeAllChildren();
            List<IMylarContextNode> landmarks = MylarPlugin.getContextManager().getActiveLandmarks();
            List<TreeParent> previousHierarchy = new ArrayList<TreeParent>();
            for (Iterator<IMylarContextNode> it = landmarks.iterator(); it.hasNext();) {
                IMylarContextNode node = it.next();
                IJavaElement element = null;
                if (node.getStructureKind().equals(JavaStructureBridge.EXTENSION)) {
                    element = JavaCore.create(node.getElementHandle());
                }
                if (element != null && element instanceof IType) {	
                    IType type = (IType)element;
                    ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
                    IType[] supertypes = hierarchy.getAllSuperclasses(type);
                    List<IType> hierarchyTypes = Arrays.asList(supertypes);
                    
//                    IType[] subtypes = hierarchy.getSubtypes(type);
//                    if (subtypes.length > 0) hierarchyTypes.add(subtypes[0]); 
//                    if (subtypes.length > 1) hierarchyTypes.add(subtypes[1]);
                    
                    TreeParent currChild = new TreeParent(type);
                    List<TreeParent> currentHierarchy = new ArrayList<TreeParent>();
                    boolean addedToPreviousHierarchy = false;
                    for (Iterator<IType> it2 = hierarchyTypes.iterator(); it2.hasNext() && !addedToPreviousHierarchy; ) {
                        IType currType = it2.next();
                        TreeParent parent = findInTree(root.getChildren(), currType);
                        if (parent == null) parent = new TreeParent(currType);
	                    currentHierarchy.add(parent);
                        addedToPreviousHierarchy = false;
                        for (Iterator<TreeParent> it3 = previousHierarchy.iterator(); it3.hasNext();) {
                            TreeParent prev = it3.next();
                            if (currType.equals(prev.getElement())) {
                                prev.addChild(currChild);
                                addedToPreviousHierarchy = true;
                            }
                        } 
                        if (!addedToPreviousHierarchy
                            && currChild.getName() != "Object") { // HACK ) {
                            parent.addChild(currChild); 
                        }
                        currChild = parent;
                    } 
                    if (!addedToPreviousHierarchy 
                        && currChild.getName() != "Object") { // HACK 
                        root.addChild(currChild);
                    } 
	                    previousHierarchy = currentHierarchy;
                }
            }
		    Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
				public void run() {
				    try { 
                        if (viewer != null && !viewer.getTree().isDisposed()) {
                            viewer.refresh();
					        viewer.expandAll();
                        }
				    } catch (Throwable t) {
			            MylarPlugin.fail(t, "Could not update viewer", false);
			        }
				}
			});
        } catch (Throwable t) {
            MylarPlugin.fail(t, "Could not update viewer", false);
        }
    }
	
    private TreeParent findInTree(TreeParent[] children, IType type) {
        for (int i = 0; i < children.length; i++) {
            TreeParent child = children[i];
            if (child.getElement().equals(type)) {
                return child;
            } else {
                return findInTree(child.getChildren(), type);
            }
        }
        return null;
    }
	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
    @Override
	public void createPartControl(Composite parent) {
		try {
		    viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
//			drillDownAdapter = new DrillDownAdapter(viewer);
			viewer.setContentProvider(new ViewContentProvider());
			viewer.setLabelProvider(new HierarchyLabelProvider(new MylarJavaLabelProvider()));
//			viewer.setSorter(new NameSorter());
			viewer.setInput(getViewSite());
//            viewer.addOpenListener(new TaskscapeNodeClickListener(viewer));
            
            viewer.addOpenListener(new IOpenListener() {
                public void open(OpenEvent event) {
                StructuredSelection selection = (StructuredSelection)viewer.getSelection();
                if (selection.getFirstElement() != null && selection.getFirstElement() instanceof TreeParent) {
                    TreeParent treeParent = (TreeParent)selection.getFirstElement();
                    if (treeParent.getElement() != null && !treeParent.getElement().getElementName().contains("Object"))
                        try {
                            JavaUI.openInEditor(treeParent.getElement());
                        } catch (Throwable e) { 
                        	MylarPlugin.log(e, "open problem");
                        }
                    } else {
                        return;
                    }
                }
            });
			makeActions();
			hookContextMenu();
			contributeToActionBars();
			viewer.getTree().setBackground(MylarUiPlugin.getDefault().getColorMap().BACKGROUND_COLOR);
        } catch (Throwable t) {
        	MylarPlugin.log(t, "create failed");
        }
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ActiveHierarchyView.this.fillContextMenu(manager);
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
//		manager.add(action1);
//		manager.add(new Separator());
//		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
//		manager.add(action1);
//		manager.add(action2);
//		manager.add(new Separator());
//		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
//		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
//		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() { 
		// no actions to make
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}

class TreeParent implements IAdaptable {
    protected IJavaElement element;
	protected TreeParent parent;

    private List<TreeParent> children;
	private String relationshipName;

	public TreeParent(IJavaElement element) {
	    this.element = element;
		children = new ArrayList<TreeParent>();
	}
	
	public TreeParent(String relationshipName) {
	    this.relationshipName = relationshipName;
	    children = new ArrayList<TreeParent>();
	}

	public String getName() {
		return element.getElementName();
	}
	
	public void setParent(TreeParent parent) {
		this.parent = parent;
	}
	
	public TreeParent getParent() {
		return parent;
	}
	
	public Object getAdapter(Class key) {
		return null;
	}
    
	public IJavaElement getElement() {
        return element;
    }
 
    @Override
	public String toString() {
	    if (getElement() == null) return relationshipName;
	    else return getName();
	}
	
	public void addChild(TreeParent child) {
		children.add(child);
		child.setParent(this);
	}

	public void removeAllChildren() {
        for (TreeParent node : children) node.setParent(null);
		children.clear();
	}
	
	public void removeChild(TreeParent child) {
		children.remove(child);
		child.setParent(null);
	}
	
	public TreeParent [] getChildren() {
		return children.toArray(new TreeParent[children.size()]);
	}
	
	public boolean hasChildren() {
		return children.size() > 0;
	}
} 

/**
 * Unwraps the elements.
 * TODO: use workbench decorator mechanism?
 * 
 * @author Mik Kersten
 */
class HierarchyLabelProvider extends AppearanceAwareLabelProvider implements IFontProvider {

    public HierarchyLabelProvider(JavaUILabelProvider labelProvider) {
//        super(labelProvider, PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());
    }
    @Override
    public Color getForeground(Object element) {
        IJavaElement javaElement = ((TreeParent)element).getElement();
        IMylarContextNode node = MylarPlugin.getContextManager().getNode(javaElement.getHandleIdentifier());
        return UiUtil.getForegroundForElement(node);
    }

    @Override
    public Color getBackground(Object element) {
        IJavaElement javaElement = ((TreeParent)element).getElement();
        IMylarContextNode node = MylarPlugin.getContextManager().getNode(javaElement.getHandleIdentifier());
        return UiUtil.getBackgroundForElement(node);
    }

    @Override
    public Image getImage(Object element) {
        return super.getImage(((TreeParent)element).getElement());
    }

    @Override
    public String getText(Object element) {
        return super.getText(((TreeParent)element).getElement());
    }
    
    public Font getFont(Object element) {
        IJavaElement javaElement = ((TreeParent)element).getElement();
        IMylarContextNode node = MylarPlugin.getContextManager().getNode(javaElement.getHandleIdentifier());
        if (node.getDegreeOfInterest().isLandmark() && !node.getDegreeOfInterest().isPropagated()) {
            return MylarUiPlugin.BOLD;
        }
        return null;
    }
    
//    protected void updateForDecorationReady(ViewerLabel settings, Object element) {
//        super.updateLabel(settings, element);
//    }
    
//    public void updateLabel(ViewerLabel settings, Object element) {
//        super.updateLabel(settings, ((TreeParent)element).getElement());
//    }
//  public Color getForeground(Object element) {
//  return super.getForeground(((TreeParent)element).getElement());
//}  
//
//public Color getBackground(Object element) {
//  return super.getBackground(((TreeParent)element).getElement());
//}
//public Image getImage(Object element) {
//  return super.getImage(((TreeParent)element).getElement());
//}
//public String getText(Object element) {
//  return super.getText(((TreeParent)element).getElement());
//}
}