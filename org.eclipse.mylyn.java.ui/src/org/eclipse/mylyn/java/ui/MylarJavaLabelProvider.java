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
 * Created on Aug 6, 2004
  */
package org.eclipse.mylar.java.ui;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.viewsupport.AppearanceAwareLabelProvider;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider;
import org.eclipse.jdt.internal.ui.viewsupport.TreeHierarchyLayoutProblemsDecorator;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.java.JavaStructureBridge;
import org.eclipse.swt.graphics.Image;



/**
 * @author Mik Kersten
 */
public class MylarJavaLabelProvider extends AppearanceAwareLabelProvider {
     
//	XXX never used
//    private boolean showDoi = false;
//    private boolean interestFilterEnabled = false;
//    private boolean filterDeclarationsEnabled = false;
//	private boolean fIsFlatLayout;
	private TreeHierarchyLayoutProblemsDecorator fProblemDecorator;
     
	public MylarJavaLabelProvider() {
		super(AppearanceAwareLabelProvider.DEFAULT_TEXTFLAGS | JavaElementLabels.P_COMPRESSED,
                AppearanceAwareLabelProvider.DEFAULT_IMAGEFLAGS | JavaElementImageProvider.SMALL_ICONS); 
		fProblemDecorator= new TreeHierarchyLayoutProblemsDecorator();
		addLabelDecorator(fProblemDecorator);
	}

//    @Override
//    public Color getForeground(Object object) {
//        if (object instanceof IJavaElement) {
//            ITaskscapeNode node = MylarPlugin.getTaskscapeManager().getDoi(((IJavaElement)object).getHandleIdentifier());
//            return UiUtil.getForegroundForElement(node);
//        } if (object instanceof IFile) {
//            ITaskscapeNode node = MylarPlugin.getTaskscapeManager().getDoi(((IFile)object).getFullPath().toPortableString());
//            return UiUtil.getForegroundForElement(node);
//        } else if (object instanceof ITaskscapeNode) {
//            return UiUtil.getForegroundForElement((ITaskscapeNode)object);
//        } else if (object instanceof TaskscapeEdge) {
//            return TaskListPlugin.getDefault().getColorMap().RELATIONSHIP;
//        } else {
//            return TaskListPlugin.getDefault().getColorMap().GRAY_LIGHT;
//        } 
//    }
    
//    @Override
//	public Color getBackground(Object object) {
//        if (object instanceof IJavaElement) {
//            ITaskscapeNode node = MylarPlugin.getTaskscapeManager().getDoi(((IJavaElement)object).getHandleIdentifier());
//            return UiUtil.getBackgroundForElement(node);
//        } else if (object instanceof IFile) {
//            ITaskscapeNode node = MylarPlugin.getTaskscapeManager().getDoi(((IFile)object).getFullPath().toPortableString());
//            return UiUtil.getBackgroundForElement(node);
//        } else if (object instanceof ITaskscapeNode) {
//            return UiUtil.getBackgroundForElement((ITaskscapeNode)object);
//        } else {
//            return null; 
//        }
//    }
	
	@Override
	public String getText(Object object) {
        if (object instanceof IMylarContextNode) { 
            IMylarContextNode node = (IMylarContextNode)object;
            if (node == null) return "<missing info>";
            if (JavaStructureBridge.EXTENSION.equals(node.getStructureKind())) {
                IJavaElement element = JavaCore.create(node.getElementHandle());
                if (element == null) {
                    return "<missing element>";                     
                } else {
                    return super.getText(element);
                }
            } 
        }
        return super.getText(object);
	}

	@Override
	public Image getImage(Object object) { 
        if (object instanceof IMylarContextNode) {
            IMylarContextNode node = (IMylarContextNode)object;
            if (node == null) return null;
            if (node.getStructureKind().equals(JavaStructureBridge.EXTENSION)) {
                return super.getImage(JavaCore.create(node.getElementHandle()));
            } 
        } 
        return super.getImage(object);
	}  
}