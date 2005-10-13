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

import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.CompositeContextNode;
import org.eclipse.mylar.core.internal.MylarContext;
import org.eclipse.mylar.core.internal.MylarContextManager;
import org.eclipse.mylar.core.internal.MylarContextNode;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.mylar.ui.internal.views.Highlighter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;

 
/**
 * @author Mik Kersten
 */
public class UiUtil {

    public static Color getBackgroundForElement(IMylarElement node) {
        if (node == null || node.getDegreeOfInterest().isPropagated()) return null;
        IMylarElement dominantNode = null;
        boolean isMultiple = false;
        if (node instanceof CompositeContextNode) {
            CompositeContextNode compositeNode = (CompositeContextNode)node;
            if (compositeNode.getNodes().isEmpty()) return null;
            dominantNode = (IMylarElement)compositeNode.getNodes().toArray()[0];
            if (compositeNode.getNodes().size() > 1) isMultiple = true;
                
            for(IMylarElement concreteNode : compositeNode.getNodes()) {
                if (dominantNode != null 
                    && dominantNode.getDegreeOfInterest().getValue() < concreteNode.getDegreeOfInterest().getValue()) {
                    dominantNode = concreteNode;
                }
            }
        } else if (node instanceof MylarContextNode) {
            dominantNode = node;
        }
        
        if (dominantNode != null) { 
            Highlighter highlighter = MylarUiPlugin.getDefault().getHighlighterForTaskId(
                  ((MylarContext)dominantNode.getContext()).getId());
            if (highlighter == null) {
                return null;
            } else if (MylarUiPlugin.getDefault().isIntersectionMode()) {
                if (isMultiple) {
                    return MylarUiPlugin.getDefault().getIntersectionHighlighter().getHighlightColor();
//                    return highlighter.getHighlight(dominantInfo, false);
                } else {
                    return null;
                }
            } else {
                return highlighter.getHighlight(dominantNode, false);
            }
//            List<Highlighter> highlighters = new ArrayList<Highlighter>();
//            for (Iterator<IDegreeOfInterest> it = compositeDoiInfo.getComposite().getInfos().iterator(); it.hasNext();) {
//                IDegreeOfInterest specificInfo = it.next();
//                Taskscape taskscape = specificInfo.getCorrespondingTaskscape();
//                Highlighter highlighter = MylarUiPlugin.getDefault().getHighlighterForTaskId(taskscape.getId());
//                if (highlighter != null) highlighters.add(highlighter);
//            }
//            if (highlighters.size() == 0) {
//                return MylarUiPlugin.getDefault().getColorMap().BACKGROUND_COLOR;
//            } else if (highlighters.size() == 1) {
//                return highlighters.get(0).getHighlight(info, false);
//            } else {
//                return Highlighter.blend(highlighters, info, false);
//            }
        } else { 
            return MylarUiPlugin.getDefault().getColorMap().BACKGROUND_COLOR;
        }
    }
 
    public static Color getForegroundForElement(IMylarElement node) {
        if (node == null) return null; 
        if (node.getDegreeOfInterest().isPredicted() || node.getDegreeOfInterest().isPropagated()) { 
            if (node.getDegreeOfInterest().getValue() >= MylarContextManager.getScalingFactors().getLandmark()/3) { 
                return MylarUiPlugin.getDefault().getColorMap().GRAY_DARK; 
            } else if (node.getDegreeOfInterest().getValue() >= 10) {
                return MylarUiPlugin.getDefault().getColorMap().GRAY_MEDIUM; 
            } else {
                return MylarUiPlugin.getDefault().getColorMap().GRAY_LIGHT; 
            }
        } else if (node.getDegreeOfInterest().isInteresting()) {
        	return null;
        } 
        return MylarUiPlugin.getDefault().getColorMap().GRAY_MEDIUM; 
    }
    
    public static void closeAllEditors(final boolean save) {
    	IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.getDisplay().asyncExec(new Runnable() {
            public void run() {
		        try {
		            IWorkbenchPage page = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
		            if (page != null) {
		            	page.closeAllEditors(save);
		            }
		        } catch (Throwable t) {
		            MylarPlugin.fail(t, "Could not auto close editor.", false);
		        } 
            }
        });
    }
}
