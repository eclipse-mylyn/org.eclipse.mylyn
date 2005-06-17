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
 * Created on Dec 29, 2004
 */
package org.eclipse.mylar.core.model;

import java.util.List;
import java.util.Set;

/**
 * @author Mik Kersten
 */
public interface ITaskscape {

    public abstract List<InteractionEvent> getInteractionHistory();
    
    public List<ITaskscapeNode> getInteresting();
    
    public abstract ITaskscapeNode get(String element);

    public abstract List<ITaskscapeNode> getLandmarks();
    
    public abstract Set<ITaskscapeNode> getInterestingResources();

    public abstract void setActiveElement(ITaskscapeNode activeNode);

    public abstract ITaskscapeNode getActiveNode();

    public abstract void remove(ITaskscapeNode node);

    public abstract List<ITaskscapeNode> getAllElements();
}
