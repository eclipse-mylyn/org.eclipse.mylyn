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
package org.eclipse.mylar.ide.ui;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.mylar.core.IMylarContextEdge;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ide.ResourceStructureBridge;
import org.eclipse.mylar.ui.AbstractContextLabelProvider;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class ResourceContextLabelProvider extends AbstractContextLabelProvider {

    public Image getImage(IMylarContextNode node) {
        IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(ResourceStructureBridge.CONTENT_TYPE);
        if (bridge != null) {
            Object object = bridge.getObjectForHandle(node.getElementHandle());
            if (object instanceof IFile) {
                return MylarImages.getImage(MylarImages.FILE_GENERIC); 
            } else if (object instanceof IContainer) {
                return MylarImages.getImage(MylarImages.FOLDER_GENERIC); 
            }
        } 
        return null;
    }

    /**
     * TODO: slow?
     */
    public String getText(IMylarContextNode node) {
    	IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(ResourceStructureBridge.CONTENT_TYPE);
        
    	String name = bridge.getName(
        		bridge.getObjectForHandle(node.getElementHandle())
        );
        return name;
    }

	@Override
	protected Image getImage(IMylarContextEdge edge) {
		return null;
	}

	@Override
	protected String getText(IMylarContextEdge edge) {
		return null;
	}
} 
