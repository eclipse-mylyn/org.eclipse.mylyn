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
package org.eclipse.mylar.ui.internal;

import java.util.List;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.IMylarUiBridge;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.mylar.ui.actions.ApplyMylarToOutlineAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * Manages the installation of the outline filter.
 * 
 * @author Mik Kersten
 */
public class ContentOutlineManager implements IPartListener, IPageListener {
    
	public void partActivated(IWorkbenchPart part) {
		if (MylarPlugin.getContextManager().isContextCapturePaused()) return;
		 
    	if (part instanceof IEditorPart) {
            IEditorPart editorPart = (IEditorPart)part;
            configureDecorator(editorPart);
        }
    	
//    	IWorkbench workbench = PlatformUI.getWorkbench();
//		workbench.getDisplay().asyncExec(new Runnable() {
//			public void run() {
    	if (ApplyMylarToOutlineAction.getDefault() != null) {
    		ApplyMylarToOutlineAction.getDefault().update();
    	}
//			}
//		});
	}

    public void partOpened(IWorkbenchPart part) { 
    	// ignore
    }
    
    /**
     * TODO: refactor, this will get called too often
     */
    private void configureDecorator(IEditorPart editorPart) {
    	if (ApplyMylarToOutlineAction.getDefault() == null) return;
    	IMylarUiBridge bridge = MylarUiPlugin.getDefault().getUiBridgeForEditor(editorPart);
    	List<TreeViewer> viewers = bridge.getContentOutlineViewers(editorPart);
    	for (TreeViewer viewer : viewers) {
	        if (viewer != null) {
	        	if (!(viewer.getLabelProvider() instanceof DecoratingLabelProvider)) {
	                viewer.setLabelProvider(new DecoratingLabelProvider(
	                        (ILabelProvider)viewer.getLabelProvider(),
	                        PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
	        	}
	        }
    	}
    }
    
  public void partBroughtToTop(IWorkbenchPart partRef) {
		// ignore
	}

	public void partClosed(IWorkbenchPart partRef) {
		// ignore
	}

	public void partDeactivated(IWorkbenchPart partRef) {
		// ignore
	}

	public void pageActivated(IWorkbenchPage page) {
		// ignore
	}

	public void pageClosed(IWorkbenchPage page) {
		// ignore
	}

	public void pageOpened(IWorkbenchPage page) {
		// ignore

	}
    
//    public void contextActivated(IMylarContext taskscape) {
//    	// ignore
//    }
//
//    public void contextDeactivated(IMylarContext taskscape) {
//		// ignore
//    }
//    
//   public void interestChanged(List<IMylarElement> nodes) {
//    	if (nodes.size() == 0) return;
//    	IMylarElement lastNode = nodes.get(nodes.size()-1);
//    	interestChanged(lastNode);
//    }
//    
//    public void interestChanged(IMylarElement node) {
//    	// TODO: uncomment this for setting selections
//	    try {
//    		if (MylarPlugin.getContextManager().hasActiveContext()
//	    		&& ApplyMylarToOutlineAction.getDefault() != null
//	    		&& ApplyMylarToOutlineAction.getDefault().isChecked()) {
//    			
//    			IEditorPart editorPart = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
//    			IMylarUiBridge uiBridge = MylarUiPlugin.getDefault().getUiBridgeForEditor(editorPart);
//    			List<TreeViewer> viewers = uiBridge.getContentOutlineViewers(editorPart);
//    			for (TreeViewer viewer : viewers) {;
//    				if (viewer.getControl().isVisible() && isInLinkToEditorMode()) {
//    					IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(node.getContentType());
//    					Object element = bridge.getObjectForHandle(node.getHandleIdentifier());
//    					if(((StructuredSelection)viewer.getSelection()).getFirstElement() != element) {
//    						viewer.setSelection(new StructuredSelection(element));
//    					}			
//    				}
//				}
//    		}
//	    } catch (Throwable t) {
//			MylarPlugin.log(t, "Could not update package explorer");
//		}
//    }
//
//	public void revealInteresting() {
//    	// ignore     
//    }
//    
//    public void presentationSettingsChanging(UpdateKind kind) {
//    	// ignore
//    }
//
//    public void presentationSettingsChanged(UpdateKind kind) {
//    	// ignore
//    }
//
//    public void landmarkAdded(IMylarElement node) {
//    	// ignore
//    } 
//
//    public void landmarkRemoved(IMylarElement node) {
//    	// ignore
//    }
//    
//    public void nodeDeleted(IMylarElement node) {
//    	// ignore
//    }
//      
//    public void edgesChanged(IMylarElement node) {
//    	// ignore
//    }
//    
}