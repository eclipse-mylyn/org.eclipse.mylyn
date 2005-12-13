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
 * Created on Apr 12, 2005
  */
package org.eclipse.mylar.ui;

import org.eclipse.jface.viewers.*;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.MylarContextRelation;
import org.eclipse.mylar.ui.internal.UiUtil;
import org.eclipse.swt.graphics.*;


/**
 * Not currently used.
 * 
 * @author Mik Kersten
 */
public class InterestDecorator implements ILabelDecorator, IFontDecorator, IColorDecorator {

    public InterestDecorator() {
        super();
    }

    private IMylarElement getNode(Object element) {
        IMylarElement node = null;
        if (element instanceof IMylarElement) {
            node = (IMylarElement)element;
        } else {
            IMylarStructureBridge adapter = MylarPlugin.getDefault().getStructureBridge(element);
            node = MylarPlugin.getContextManager().getElement(adapter.getHandleIdentifier(element));
        }
        return node;
    } 
  
    public void addListener(ILabelProviderListener listener) {
    	// don't care about listeners
    }

    public void dispose() {
    	// don't care when we are disposed
    }

    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    public void removeListener(ILabelProviderListener listener) {
    	// don't care about listeners
    }

    public Image decorateImage(Image image, Object element) {
        return null;
    }

    public String decorateText(String text, Object element) {
        return null;
    }

    public Font decorateFont(Object element) {
        IMylarElement node = getNode(element);
        if (node != null) {    
            if (node.getInterest().isLandmark() && !node.getInterest().isPropagated()) {
                return MylarUiPrefContstants.BOLD;
            } 
        } 
        return null;
    }

    public Color decorateForeground(Object element) {
        IMylarElement node = getNode(element);
        if (element instanceof MylarContextRelation) {
            return MylarUiPlugin.getDefault().getColorMap().RELATIONSHIP;
        } else if (node != null) {
            UiUtil.getForegroundForElement(node);
        }
        return null;
    }

    public Color decorateBackground(Object element) {
        IMylarElement node = getNode(element);
        if (node != null) {
            return UiUtil.getBackgroundForElement(node);   
        } else {
            return null;
        }
    }
}
