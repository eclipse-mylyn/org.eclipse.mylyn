/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.context.core;

import java.util.List;

import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * A model of task context weighted by interaction based on frequency and recency of access, as determined by the
 * degree-of-interest weighting mechanism.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public interface IInteractionContext {

	/**
	 * @return null if no unique handle, e.g. if a composite context
	 */
	public abstract String getHandleIdentifier();

	public abstract List<InteractionEvent> getInteractionHistory();

	public List<IInteractionElement> getInteresting();

	public abstract IInteractionElement get(String element);

	public abstract IInteractionElement getActiveNode();

	public abstract void delete(IInteractionElement element);

	public abstract void updateElementHandle(IInteractionElement element, String newHandle);

	public abstract List<IInteractionElement> getAllElements();
	
	public InteractionContextScaling getScaling();
}
