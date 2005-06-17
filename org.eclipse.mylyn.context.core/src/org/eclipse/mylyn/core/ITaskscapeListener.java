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
 * Created on Jul 7, 2004
 */
package org.eclipse.mylar.core;

import java.util.List;

import org.eclipse.mylar.core.model.ITaskscape;
import org.eclipse.mylar.core.model.ITaskscapeNode;



/**
 * @author Mik Kersten
 */
public interface ITaskscapeListener {

    public enum UpdateKind {
        HIGHLIGHTER,
        SCALING,
        UPDATE,
        FILTER
    }
    
    public void taskscapeActivated(ITaskscape taskscape);

    public void taskscapeDeactivated(ITaskscape taskscape);
    
    /**
     * E.g. highlighters or scaling factors are being actively modified (for active updating).
     * @param kind TODO
     */
    public void presentationSettingsChanging(UpdateKind kind);

    /**
     * Modification completed (for slow updating).
     * @param kind TODO
     */
    public void presentationSettingsChanged(UpdateKind kind);
    
    /**
     * Called when the interest level for a single element changes, e.g.
     * when it is selected by the user.
     */
    public void interestChanged(ITaskscapeNode node);

    /**
     * Called when the interest level for multiple elements changes,
     * sorted according to the containment hierarchy.  The last element
     * is the element invoking the change.
     */
    public void interestChanged(List<ITaskscapeNode> nodes);
    
    public void nodeDeleted(ITaskscapeNode node);
    
    /**
     * @param newLandmarks  list of IJavaElement(s)
     */
    public void landmarkAdded(ITaskscapeNode node);
    
    /**
     * @param newLandmarks  list of IJavaElement(s)
     */
    public void landmarkRemoved(ITaskscapeNode node);    
    
    public void relationshipsChanged();
}
