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
import org.eclipse.mylar.core.internal.MylarContextManager;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.mylar.ui.internal.views.Highlighter;
import org.eclipse.swt.graphics.Color;

 /**
 * @author Mik Kersten
 */
public class UiUtil {

    public static Color getBackgroundForElement(IMylarElement node) {
    	return getBackgroundForElement(node, false);
    }
	
    public static Color getBackgroundForElement(IMylarElement node, boolean resolveContextColor) {
        if (node == null) return null;
        if (!resolveContextColor && (node.getInterest().isPropagated() || node.getInterest().isPredicted())) {
        	return null;
        }

        boolean isMultiple = false;
        String contextId = MylarPlugin.getContextManager().getDominantContextHandleForElement(node);
        
        if (contextId != null) { 
            Highlighter highlighter = MylarUiPlugin.getDefault().getHighlighterForContextId(contextId);
            if (highlighter == null) {
                return null;
            } 
            else if (MylarUiPlugin.getDefault().isIntersectionMode()) {
                if (isMultiple) {
                    return MylarUiPlugin.getDefault().getIntersectionHighlighter().getHighlightColor();
                } else {
                    return null;
                }
            } else {
                return highlighter.getHighlight(node, false);
            }
        } else { 
            return MylarUiPlugin.getDefault().getColorMap().BACKGROUND_COLOR;
        }
    }
 
    public static Color getForegroundForElement(IMylarElement node) {
        if (node == null) return null; 
        if (node.getInterest().isPredicted() || node.getInterest().isPropagated()) { 
            if (node.getInterest().getValue() >= MylarContextManager.getScalingFactors().getLandmark()/3) { 
                return MylarUiPlugin.getDefault().getColorMap().GRAY_DARK; 
            } else if (node.getInterest().getValue() >= 10) {
                return MylarUiPlugin.getDefault().getColorMap().GRAY_MEDIUM; 
            } else {
                return MylarUiPlugin.getDefault().getColorMap().GRAY_LIGHT; 
            }
        } else if (node.getInterest().isInteresting()) {
        	return null;
        } 
        return MylarUiPlugin.getDefault().getColorMap().GRAY_MEDIUM; 
    }
}

//    if (node instanceof CompositeContextElement) {
//        CompositeContextElement compositeNode = (CompositeContextElement)node;
//        if (compositeNode.getNodes().isEmpty()) return null;
//        dominantNode = (IMylarElement)compositeNode.getNodes().toArray()[0];
//        if (compositeNode.getNodes().size() > 1) isMultiple = true;
//            
//        for(IMylarElement concreteNode : compositeNode.getNodes()) {
//            if (dominantNode != null 
//                && dominantNode.getDegreeOfInterest().getValue() < concreteNode.getDegreeOfInterest().getValue()) {
//                dominantNode = concreteNode;
//            }
//        }
//    } else if (node instanceof MylarContextElement) {
//        dominantNode = node;
//    }
//  List<Highlighter> highlighters = new ArrayList<Highlighter>();
//  for (Iterator<IDegreeOfInterest> it = compositeDoiInfo.getComposite().getInfos().iterator(); it.hasNext();) {
//      IDegreeOfInterest specificInfo = it.next();
//      Taskscape taskscape = specificInfo.getCorrespondingTaskscape();
//      Highlighter highlighter = MylarUiPlugin.getDefault().getHighlighterForTaskId(taskscape.getId());
//      if (highlighter != null) highlighters.add(highlighter);
//  }
//  if (highlighters.size() == 0) {
//      return MylarUiPlugin.getDefault().getColorMap().BACKGROUND_COLOR;
//  } else if (highlighters.size() == 1) {
//      return highlighters.get(0).getHighlight(info, false);
//  } else {
//      return Highlighter.blend(highlighters, info, false);
//  }
