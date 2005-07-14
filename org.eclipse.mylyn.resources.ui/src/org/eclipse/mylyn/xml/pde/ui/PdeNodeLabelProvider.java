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
 * Created on Apr 18, 2005
  */
package org.eclipse.mylar.xml.pde.ui;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.mylar.xml.MylarXmlPlugin;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class PdeNodeLabelProvider implements ILabelProvider {

    public Image getImage(Object element) {
        return MylarImages.getImage(MylarImages.FILE_XML); 
    }

    /**
     * TODO: slow?
     */
    public String getText(Object element) {
        IMylarContextNode node = (IMylarContextNode)element;
        String name = MylarXmlPlugin.getPdeStructureBridge().getName(
                MylarXmlPlugin.getPdeStructureBridge().getObjectForHandle(node.getElementHandle())
        );
        return name;
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
} 
