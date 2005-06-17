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
 * Created on Jul 26, 2004
 */
package org.eclipse.mylar.ui.internal;

import java.lang.reflect.Method;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.model.ITaskscapeNode;
import org.eclipse.mylar.core.model.internal.CompositeTaskscapeNode;
import org.eclipse.mylar.core.model.internal.Taskscape;
import org.eclipse.mylar.core.model.internal.TaskscapeNode;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.mylar.ui.internal.views.Highlighter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.views.markers.internal.ProblemView;
import org.eclipse.ui.views.markers.internal.TableView;

 
/**
 * @author Mik Kersten
 */
public class UiUtil {

    // TODO: remove hard-coded fonts
    public static final Font BOLD = new Font(null, "Tahoma", 8, SWT.BOLD);
    public static final Font ITALIC = new Font(null, "Tahoma", 8, SWT.ITALIC);
    
    public static TableViewer cachedProblemsTableViewer = null;
    /**
     * HACK: changing accessibility
     */
    public static TableViewer getProblemViewFromActivePerspective() {
        if (cachedProblemsTableViewer != null) return cachedProblemsTableViewer;
        IWorkbenchPage activePage= Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
        if (activePage == null) return null;
        try {
            IViewPart view= activePage.findView("org.eclipse.ui.views.ProblemView");
            if (view instanceof ProblemView) {

                Class infoClass = TableView.class;//problemView.getClass();
                Method method = infoClass.getDeclaredMethod("getViewer", new Class[] { } );
                method.setAccessible(true);
                cachedProblemsTableViewer = (TableViewer)method.invoke(view, new Object[] { });
                return cachedProblemsTableViewer;
            } 
        } catch (Exception e) {
        	MylarPlugin.log("org.eclipse.mylar.ui.UiUtil", e);
        }
        return null;
    }
    
    public static void refreshProblemsView() {
        Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
            public void run() { 
                TableViewer problemsTableView = UiUtil.getProblemViewFromActivePerspective();
                if (problemsTableView != null && problemsTableView.getTable().isVisible()) {
                    problemsTableView.refresh();
                } 
            }
        });   
    }
    
//    public static String getFilterStatisticsDecoration(IParent parent) {
//        IJavaElement[] children;
//        String text = "";
//        try {
//            children = parent.getChildren();
////            int numFiltered = 0;
//            int numVisible = 0;
//            int numTotal = children.length;
//            for (int i = 0; i < children.length; i++) {
//                ITaskscapeNode childInfo = MylarPlugin.getTaskscapeManager().getDoi(children[i].getHandleIdentifier());
//                if (childInfo != null && childInfo.getDegreeOfInterest().getDegreeOfInterest().isInteresting()) {
//                    numVisible++;
//                } 
//            }  
//            if (children.length > 0) 
//                text += "   |" + numVisible + ".." + numTotal + "|";
//        } catch (JavaModelException e) {
//            MylarPlugin.fail(e, e.toString(), false);
//        }
//
//	    return text;
//    }
    
    public static Color getBackgroundForElement(ITaskscapeNode node) {
        if (node == null || node.getDegreeOfInterest().isPredicted()) return null;
        ITaskscapeNode dominantNode = null;
        boolean isMultiple = false;
        if (node instanceof CompositeTaskscapeNode) {
            CompositeTaskscapeNode compositeNode = (CompositeTaskscapeNode)node;
            dominantNode = (ITaskscapeNode)compositeNode.getNodes().toArray()[0];
            if (compositeNode.getNodes().size() > 1) isMultiple = true;
                
            for(ITaskscapeNode concreteNode : compositeNode.getNodes()) {
                if (dominantNode != null 
                    && dominantNode.getDegreeOfInterest().getValue() < concreteNode.getDegreeOfInterest().getValue()) {
                    dominantNode = concreteNode;
                }
            }
        } else if (node instanceof TaskscapeNode) {
            dominantNode = node;
        }
        
        if (dominantNode != null) { 
            Highlighter highlighter = MylarUiPlugin.getDefault().getHighlighterForTaskId(
                  ((Taskscape)dominantNode.getTaskscape()).getId());
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
 
    public static Color getForegroundForElement(ITaskscapeNode node) {
        if (node == null) return null; 
        if (node.getDegreeOfInterest().isPredicted()) { 
            if (node.getDegreeOfInterest().getValue() >= 20) { // HACK: parametrize
                return MylarUiPlugin.getDefault().getColorMap().GRAY_DARK; 
            } else if (node.getDegreeOfInterest().getValue() >= 10) {
                return MylarUiPlugin.getDefault().getColorMap().GRAY_MEDIUM; 
            } else {
                return MylarUiPlugin.getDefault().getColorMap().GRAY_LIGHT; 
            }
        } else {
            return null;
        }
    }
}
