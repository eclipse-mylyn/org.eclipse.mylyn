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
 * Created on May 6, 2005
  */
package org.eclipse.mylar.ide.ui;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.InterestFilter;
import org.eclipse.ui.views.markers.internal.ProblemMarker;

/**
 * @author Mik Kersten
 */
public class ProblemsListInterestFilter extends InterestFilter {
	
	@Override
    public boolean select(Viewer viewer, Object parent, Object element) {
        if (!(element instanceof ProblemMarker)) return false;
         ProblemMarker marker = (ProblemMarker)element;
         if (marker.getSeverity() == IMarker.SEVERITY_ERROR) {
             return true;
         } else {
        	 if (!MylarPlugin.getContextManager().hasActiveContext()) {
        		 return false;
        	 }
             String handle = MylarPlugin.getDefault().getStructureBridge(marker.getResource().getFileExtension()).getHandleForOffsetInObject(marker, 0);
             if (handle == null) {
                 return false;
             } else {
                 return super.select(viewer, parent, MylarPlugin.getContextManager().getElement(handle));
             }
         }
    }
}