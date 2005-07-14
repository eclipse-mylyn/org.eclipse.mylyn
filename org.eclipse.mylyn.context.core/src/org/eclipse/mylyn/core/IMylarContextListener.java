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




/**
 * @author Mik Kersten
 */
public interface IMylarContextListener {

    public enum UpdateKind {
        HIGHLIGHTER,
        SCALING,
        UPDATE,
        FILTER
    }
    
    public void contextActivated(IMylarContext context);

    public void contextDeactivated(IMylarContext context);
    
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
    public void interestChanged(IMylarContextNode node);

    /**
     * Called when the interest level for multiple elements changes,
     * sorted according to the containment hierarchy.  The last element
     * is the element invoking the change.
     */
    public void interestChanged(List<IMylarContextNode> nodes);
    
    public void nodeDeleted(IMylarContextNode node);
    
    /**
     * @param newLandmarks  list of IJavaElement(s)
     */
    public void landmarkAdded(IMylarContextNode node);
    
    /**
     * @param newLandmarks  list of IJavaElement(s)
     */
    public void landmarkRemoved(IMylarContextNode node);    
    
    public void relationshipsChanged();
}
