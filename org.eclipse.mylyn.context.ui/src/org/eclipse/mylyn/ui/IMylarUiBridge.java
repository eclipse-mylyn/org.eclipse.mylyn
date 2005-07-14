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
 * Created on Apr 6, 2005
  */
package org.eclipse.mylar.ui;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.ui.IEditorPart;


public interface IMylarUiBridge {

    public abstract void open(IMylarContextNode node);
    
    public abstract void close(IMylarContextNode node);
    
    public abstract ILabelProvider getLabelProvider();

    public abstract boolean acceptsEditor(IEditorPart editorPart);

    public abstract List<TreeViewer> getTreeViewers(IEditorPart editorPart);

    /**
     * @param element  if null refresh the whole tree
     * @param updateLabels TODO
     */
    public abstract void refreshOutline(Object element, boolean updateLabels);

    public abstract ImageDescriptor getIconForRelationship(String relationshipHandle);

    public abstract String getNameForRelationship(String relationshipHandle);
    
}
