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
/*
 * Created on May 16, 2005
  */
package org.eclipse.mylar.java.ui.editor;

import java.util.List;

import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class ActiveFoldingListener implements IMylarContextListener {
    private final JavaEditor editor;
    private ActiveFoldingController controller;

    private IMylarContextNode lastUpdatedNode = null;
    
    private IPropertyChangeListener PREFERENCE_LISTENER = new IPropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals(PreferenceConstants.EDITOR_FOLDING_PROVIDER)) {// ||
				controller.resetFolding();
			}
		}        
    };
    
    public ActiveFoldingListener(JavaEditor editor) {
    	this.editor = editor;
        this.controller = new ActiveFoldingController(editor);
        JavaPlugin.getDefault().getPluginPreferences().addPropertyChangeListener(PREFERENCE_LISTENER);
    }

    public void interestChanged(IMylarContextNode node) {
    	if (!node.equals(lastUpdatedNode)) {
    		controller.updateFolding(true);
    		lastUpdatedNode = node;
    	}
    }

    public void interestChanged(List<IMylarContextNode> nodes) {
    	interestChanged(nodes.get(nodes.size()-1));     
    }

    public void contextActivated(IMylarContext taskscape) {
    	controller.resetFolding();
    }

    public void contextDeactivated(IMylarContext taskscape) {
    	controller.resetFolding();
    }

    public void presentationSettingsChanging(IMylarContextListener.UpdateKind kind) {
    	// don't care when the presentation settings are changing
    }

    public void presentationSettingsChanged(IMylarContextListener.UpdateKind kind) { 
    	controller.resetFolding();
    }

    public void landmarkAdded(IMylarContextNode element) { 
    	// don't care when a landmark is added
    }

    public void landmarkRemoved(IMylarContextNode element) { 
    	// don't are when a landmark is removed
    }

    public void edgesChanged(IMylarContextNode node) { 
    	// don't care when relationships change
    }

    public void nodeDeleted(IMylarContextNode node) {
//        hardRefresh(); 
//        foldingController.updateFolding(false);
    }
    
    class ActiveFoldingController implements IPartListener2 {
        
        public ActiveFoldingController(JavaEditor editor) {            
            IWorkbenchPartSite site= editor.getSite(); 
            if (site != null) {
                IWorkbenchPage page= site.getPage();
                if (!page.isPartVisible(editor)) page.addPartListener(this);
            } 
        }
        
        public void updateFolding(final boolean expand) {
            Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
                public void run() { 
//                    if (editor == null || editor.getEditorInput() == null) monitor.unregisterEditor(editor);
                    if (!editor.getSite().getPage().isPartVisible(editor)) return;
                    ISourceViewer sourceViewer = editor.getViewer();
                    if (sourceViewer instanceof ProjectionViewer) {
                        ProjectionViewer pv= (ProjectionViewer) sourceViewer;
                        if (isAutoFoldingEnabled()) {// && MylarUiPlugin.getDefault().isGlobalFilteringEnabled()) { 
                            if (expand) {
                                if (pv.canDoOperation(ProjectionViewer.EXPAND)) pv.doOperation(ProjectionViewer.EXPAND);
                            } else {
                                if (pv.canDoOperation(ProjectionViewer.COLLAPSE)) pv.doOperation(ProjectionViewer.COLLAPSE);  
                            }
                        } 
                    }
                }
            });    
        } 
    
        public void resetFolding() {
            Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
                public void run() { 
                    if (!editor.getSite().getPage().isPartVisible(editor)) {
                    	return;
                    } else {
                    	if (editor.isSaveAsAllowed() && editor.isDirty()) editor.doSave(null); // HACK: to avoid losing data
                    	editor.setInput(editor.getEditorInput()); // HACK: should be a better way
                    }
//                    ISourceViewer sourceViewer = editor.getViewer();
//                    if (sourceViewer instanceof ProjectionViewer) {
//                        ProjectionViewer pv = (ProjectionViewer) sourceViewer;
//                        if (pv.canDoOperation(ProjectionViewer.TOGGLE)) pv.doOperation(ProjectionViewer.TOGGLE);
//                    } 
                }
            });    
        } 
        
        private boolean isAutoFoldingEnabled() {
        	return AutoFoldingStructureProvider.ID.equals(JavaPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.EDITOR_FOLDING_PROVIDER));
        }
        
        public void partVisible(IWorkbenchPartReference partRef) {
        	// don't care when a part becomes visible
        }
        
        public void partActivated(IWorkbenchPartReference partRef) {
            if (editor.equals(partRef.getPart(false))) {
                updateFolding(true);
            } 
        }
    
        public void partClosed(IWorkbenchPartReference partRef) {
//            monitor.unregisterEditor(editor);
        }
        
        public void partBroughtToTop(IWorkbenchPartReference partRef) {
            if (editor.equals(partRef.getPart(false))) {
    //          cancel();
              updateFolding(true);
          } 
        }
        public void partDeactivated(IWorkbenchPartReference partRef) {
        	// don't care when a part is deactivated
        }
        public void partOpened(IWorkbenchPartReference partRef) {
        	// don't care when a part is opened
        }
        public void partHidden(IWorkbenchPartReference partRef) {
        	// don't care when a part is hidden
        }
        public void partInputChanged(IWorkbenchPartReference partRef) {
        	// don't care when an input changes
        }
    }
}

//ProjectionAnnotationModel model=(ProjectionAnnotationModel)  editor.getAdapter(ProjectionAnnotatioModel.class); 
//for(Iterator<Annotation> i = model.getAnnotationIterator(); i.hasNext(); ) {
//  Annotation annotation = i.next();
//  if (annotation instanceof ProjectionAnnotation) {
//      ProjectionAnnotation projectionAnnotation = (ProjectionAnnotation)annotation;
//      Position position = model.getPosition(annotation);
//  }
//} 

//if (this.editor.getSite().getPage().isPartVisible(this.editor)) {
//IJavaElement active = EditorUtility.getActiveEditorJavaInput();
//final ITaskscapeNode active = MylarPlugin.getTaskscapeManager().getActiveNode();
//editor.close(true);
//	MylarJavaPlugin.getUiBridge().open(active);
//            JavaPlugin.getDefault().getPreferenceStore().setValue(PreferenceConstants.EDITOR_FOLDING_ENABLED, false);
//JavaPlugin.getDefault().getPreferenceStore().setValue(PreferenceConstants.EDITOR_FOLDING_ENABLED, true);
//}

//Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
//    public void run() {
//        	controller.updateFolding(true);
//        	ISourceViewer sourceViewer = editor.getViewer();
//            if (sourceViewer instanceof ProjectionViewer) {
//                ProjectionViewer pv= (ProjectionViewer) sourceViewer;
//                try {
//					pv.reinitializeProjection();
//				} catch (BadLocationException e) {
//					// ignore
//				}
//            }
//        }
//});
//}
//ProjectionAnnotationModel model = (ProjectionAnnotationModel)editor.getAdapter(ProjectionAnnotationModel.class);
//for (Iterator it = model.getAnnotationIterator(); it.hasNext(); ) {
//	ProjectionAnnotation annotation = (ProjectionAnnotation)it.next();
//	model.toggleExpansionState(annotation);
//}

